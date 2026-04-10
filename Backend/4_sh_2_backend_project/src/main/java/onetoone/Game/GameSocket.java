package onetoone.Game;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

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
import org.json.simple.JSONArray;

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
    public void onOpen(Session session, @PathParam("username") String username, @PathParam("game") String game) throws IOException {
        logger.info("Entered into open");
        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);

        gameHistory = new GameHistory(userRepo.findByUsername(username), cardGameRepository.findByGameName(game));
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("game") String game) throws IOException, ParseException {
        JSONObject json = (JSONObject) new JSONParser().parse(message);
        if (json.get("messageType").equals("join_game")) {
            if (!(users.size() == 0) && cardGameRepository.findByGameName(game).getMaxPlayers() == users.size()) {
                try {
                    logger.info((String) json.get("username"));
                    JSONObject mes = new JSONObject();
                    mes.put("messageType", "message");
                    mes.put("text", "Game full");
                    usernameSessionMap.get((String) json.get("username")).getBasicRemote().sendText((mes.toJSONString()));
                } catch (IOException e) {
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
            for (int i = 0; i < users.size(); i++) {
                try {
                    logger.info(users.get(i).getUsername());
                    JSONObject output = makeOutput(game, users.get(i).getUsername());
                    usernameSessionMap.get(users.get(i).getUsername()).getBasicRemote().sendText(output.toJSONString());
                } catch (IOException e) {
                    logger.info("Exception: " + e.getMessage().toString());
                    e.printStackTrace();
                }
            }
        } else if (json.get("messageType").equals("action_made")) {
            if (json.get("move").equals("hit")) {
                cardGame.takeTurn(Actions.HIT);
            } else if (json.get("move").equals("stand")) {
                cardGame.takeTurn(Actions.STAND);
            }
            for (int i = 0; i < users.size(); i++) {
                try {
                    logger.info(users.get(i).getUsername());
                    JSONObject output = makeOutput(game, users.get(i).getUsername());
                    usernameSessionMap.get(users.get(i).getUsername()).getBasicRemote().sendText(output.toJSONString());
                } catch (IOException e) {
                    logger.info("Exception: " + e.getMessage().toString());
                    e.printStackTrace();
                }
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
            JSONArray array = new JSONArray();
            Card[] dealerHand = temp.getDealerHand();
            int cardCount;
            for (cardCount = 0; cardCount < dealerHand.length; cardCount++) {
                if (dealerHand[cardCount] == null) {
                    break;
                }
            }
            Card[] tempHand = new Card[cardCount];
            for (int i = 0; i < cardCount; i++) {
                tempHand[i] = dealerHand[i];
            }
            array.addAll(List.of(tempHand));
            output.put("centerCards", array);
        }
        output.put("yourSeat", cardGame.findPlayer(userRepo.findByUsername(username)));
        output.put("currentTurn", cardGame.getTurn());
        if (!cardGame.checkGameProgress()) {
            output.put("gamePhase", "finished");
        } else {
            output.put("gamePhase", "in progress");
        }
        output.put("actions", cardGame.getPossibleActions());
        JSONArray players = new JSONArray();
        for (int i = 0; i < users.size(); i++) {
            JSONObject player = new JSONObject();
            player.put("seat", i);
            player.put("username", cardGame.getPlayers()[i].getUsername());
            if (cardGame.getPlayers()[i].getUsername().equals(username)) {
                JSONArray array = new JSONArray();
                int cardCount = cardGame.getCardAmount(cardGame.getPlayers()[i]);
                Card[] playerHand = cardGame.getPlayerHand(cardGame.getPlayers()[i]);
                Card[] tempHand = new Card[cardCount];
                for (int j = 0; j < cardCount; j++) {
                    tempHand[j] = playerHand[j];
                }
                array.addAll(List.of(tempHand));
                player.put("cards", array);
            } else {
                player.put("cards", "[]");
                player.put("hiddenCount", cardGame.getCardAmount(users.get(i)));
            }
            players.add(player);
        }
        output.put("players", players);
        if (cardGame.checkGameProgress()) {
            output.put("winners", Arrays.toString(cardGame.getWinners()));
        }
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
        users.remove(userRepo.findByUsername(username));
    }


    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.info("Entered into Error");
        for (int i = 0; i < users.size(); i++) {
            logger.info(users.get(i).getUsername());
        }
        throwable.printStackTrace();
    }
}
