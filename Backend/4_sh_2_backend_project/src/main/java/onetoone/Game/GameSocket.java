package onetoone.Game;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import onetoone.CardGames.CardGameRepository;
import onetoone.GameHistory.GameHistory;
import onetoone.GameHistory.GameHistoryRepository;
import onetoone.Requests.RequestRepository;
import onetoone.Requests.RequestSocket;
import onetoone.Users.UserRepository;
import onetoone.Users.User;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.stereotype.Controller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Controller
@ServerEndpoint(value = "/game/{username}/{game}")
public class GameSocket {

    private static GameHistoryRepository gameHistoryRepository;

    private static UserRepository userRepo;

    private static CardGameRepository cardGameRepository;

    @Autowired
    public void setGameHistoryRepository(GameHistoryRepository repo) {
        gameHistoryRepository = repo;
    }

    @Autowired
    public void setUserRepository(UserRepository repo) {
        userRepo = repo;
    }

    @Autowired
    public void setCardGameRepository(CardGameRepository repo) {
        cardGameRepository = repo;
    }

    private final Logger logger = LoggerFactory.getLogger(GameSocket.class);
    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static ArrayList<User> users = new ArrayList<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();
    private GameHistory gameHistory;
    private static Game cardGame;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {
        logger.info("Entered into open");
        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);

        gameHistory = new GameHistory(userRepo.findByUsername(username));
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("game") String game) throws IOException, ParseException {
        JSONObject json = (JSONObject) new JSONParser().parse(message);
        if (json.get("messageType").equals("join_game")) {
            if (cardGameRepository.findByGameName(game).getMaxPlayers() == users.size()) {
                try {
                    logger.info((String) json.get("username"));
                    JSONObject mes = new JSONObject();
                    mes.put("messageType", "message");
                    mes.put("text", "Game full");
                    usernameSessionMap.get((String) json.get("username")).getBasicRemote().sendObject(mes);
                } catch (IOException | EncodeException e) {
                    logger.info("Exception: " + e.getMessage().toString());
                    e.printStackTrace();
                    return;
                }
            } else {
                users.add(userRepo.findByUsername((String) json.get("username")));
            }
        } else if (json.get("messageType").equals("start_game")) {
            User[] players = new User[users.size()];
            for (int i = 0; i < players.length; i++) {
                players[i] = users.get(i);
            }
            cardGame = new BlackJack(cardGameRepository.findByGameName(game), players);
        } else if (json.get("messageType").equals("action_made")) {
            if (json.get("move").equals("hit")) {
                cardGame.takeTurn(Actions.HIT);
            } else if (json.get("move").equals("stand")) {
                cardGame.takeTurn(Actions.STAND);
            }
        }

        for (int i = 0; i < users.size(); i++) {
            try {
                logger.info(cardGame.getPlayers()[i].getUsername());
                JSONObject output = makeOutput(game, cardGame.getPlayers()[i].getUsername());
                usernameSessionMap.get(cardGame.getPlayers()[i].getUsername()).getBasicRemote().sendObject(output);
            } catch (IOException | EncodeException e) {
                logger.info("Exception: " + e.getMessage().toString());
                e.printStackTrace();
            }
        }
    }

    private JSONObject makeOutput (String game, String username) {
        JSONObject output = new JSONObject();
        output.put("messageType", "game_state");
        output.put("gameName", game);
        output.put("status", "Seat " + cardGame.findCurrentPlayer() + " turn");
        if (cardGame.getClass().equals(BlackJack.class)) {
            BlackJack temp = (BlackJack) cardGame;
            output.put("centerCards", temp.getDealerHand());
        }
        output.put("yourSeat", cardGame.findPlayer(userRepo.findByUsername(username)));
        output.put("currentTurn", cardGame.getTurn());
        if (!cardGame.checkGameProgress()) {
            output.put("gamePhase", "finished");
        } else {
            output.put("gamePhase", "in progress");
        }
        output.put("actions", cardGame.getPossibleActions());
        JSONObject[] players = new JSONObject[cardGame.getPlayers().length];
        for (int i = 0; i < players.length; i++) {
            JSONObject player = new JSONObject();
            player.put("seat", i);
            player.put("username", cardGame.getPlayers()[i].getUsername());
            if (cardGame.getPlayers()[i].isEqual(cardGame.findPlayer(userRepo.findByUsername(username)))) {
                player.put("cards", cardGame.getPlayerHand(userRepo.findByUsername(username)));
            } else {
                player.put("cards", "[]");
                player.put("hiddenCount", cardGame.getPlayerHand(cardGame.getPlayers()[i]).length);
            }
        }
        output.put("players", players);
        return output;
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("Entered into Close");
        gameHistory.setTimeGameCompleted(LocalDateTime.now());
        gameHistoryRepository.save(gameHistory);

        String username = sessionUsernameMap.get(session);
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);
    }


    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.info("Entered into Error");
        throwable.printStackTrace();
    }
}
