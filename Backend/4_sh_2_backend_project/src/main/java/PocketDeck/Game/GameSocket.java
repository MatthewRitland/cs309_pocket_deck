package PocketDeck.Game;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import PocketDeck.CardGames.CardGameRepository;
import PocketDeck.GameHistory.GameHistory;
import PocketDeck.GameHistory.GameHistoryRepository;
import PocketDeck.GameHistory.GameHistoryResult;
import PocketDeck.Users.UserRepository;
import PocketDeck.Users.User;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static boolean gameOver;

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
            logger.info(game);
            if (cardGameRepository.findByGameName(game).getMaxPlayers() == users.size()) {
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
            gameOver = false;
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
        } else if (json.get("messageType").equals("action_made") && !gameOver) {
            if (sessionUsernameMap.get(session).equals(cardGame.getCurrentPlayer().getUsername())) {
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
            if (!cardGame.checkGameProgress()) {
                Card tempHand = new Card();
                tempHand = dealerHand[0];
                JSONObject card = new JSONObject();
                card.put("suit", tempHand.getSuit().toString());
                card.put("value", tempHand.getValue().toString());
                array.add(card);
                output.put("centerCards", array);
                output.put("centerHidden", cardCount - 1);
            }
            else {
                for (int i = 0; i < cardCount; i++) {
                    JSONObject card = new JSONObject();
                    card.put("suit", dealerHand[i].getSuit().toString());
                    card.put("value", dealerHand[i].getValue().toString());
                    array.add(card);
                }
                output.put("centerCards", array);
                output.put("centerHidden", 0);
            }
        }
        output.put("yourSeat", cardGame.findPlayer(userRepo.findByUsername(username)));
        output.put("currentTurn", cardGame.getTurn());
        if (cardGame.checkGameProgress()) {
            output.put("gamePhase", "finished");
            gameOver = true;
        } else {
            output.put("gamePhase", "in progress");
        }
        JSONArray actionArray = new JSONArray();
        Actions[] actions = cardGame.getPossibleActions();
        actionArray.add(actions[0].toString());
        actionArray.add(actions[1].toString());
        output.put("actions", actionArray);
        JSONArray players = new JSONArray();
        for (int i = 0; i < users.size(); i++) {
            JSONObject player = new JSONObject();
            player.put("seat", i);
            player.put("username", cardGame.getPlayers()[i].getUsername());
            if (cardGame.getClass().equals(BlackJack.class)) {
                BlackJack temp = (BlackJack) cardGame;
                player.put("stood", String.valueOf(temp.getStood()[i]));
                player.put("busted", String.valueOf(temp.isBusted(cardGame.getPlayers()[i])));
            }
            if (cardGame.getPlayers()[i].getUsername().equals(username)) {
                JSONArray array = new JSONArray();
                int cardCount = cardGame.getCardAmount(cardGame.getPlayers()[i]);
                Card[] playerHand = cardGame.getPlayerHand(cardGame.getPlayers()[i]);
                Card[] tempHand = new Card[cardCount];
                for (int j = 0; j < cardCount; j++) {
                    tempHand[j] = playerHand[j];
                }
                for (int j = 0; j < cardCount; j++) {
                    JSONObject card = new JSONObject();
                    card.put("suit", tempHand[j].getSuit().toString());
                    card.put("value", tempHand[j].getValue().toString());
                    array.add(card);
                }
                player.put("cards", array);
            } else {
                player.put("cards", "[]");
                player.put("hiddenCount", cardGame.getCardAmount(users.get(i)));
            }
            players.add(player);
        }
        output.put("players", players);
        if (cardGame.checkGameProgress()) {
            JSONArray array = new JSONArray();
            for (int j = 0; j < cardGame.getPlayers().length; j++) {
                array.add(cardGame.getWinners()[j].toString());
            }
            output.put("winners", array);
            if (cardGame.getClass().equals(BlackJack.class)) {
                BlackJack temp = (BlackJack) cardGame;
                logger.info(String.valueOf(temp.getDealerScore()));
            }
        }
        return output;
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("Entered into Close");
        String username = sessionUsernameMap.get(session);
        recordGame(session);
        logger.info(username);
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);
        logger.info(String.valueOf(users.remove(userRepo.findByUsername(username))));
    }

    private void recordGame (Session session) {
        gameHistory.setTimeGameCompleted(LocalDateTime.now());
        gameHistory.setTimeGameDuration(Duration.between(gameHistory.getTimeGameStarted(), gameHistory.getTimeGameCompleted()));
        Result result = cardGame.getWinners()[cardGame.findPlayer(userRepo.findByUsername(sessionUsernameMap.get(session)))];
        if (result.equals(Result.DRAW)) {
            gameHistory.setGameResult(GameHistoryResult.DRAW);
        }
        else if (result.equals(Result.LOSE)) {
            gameHistory.setGameResult(GameHistoryResult.DEFEAT);
        }
        else if (result.equals(Result.WIN)) {
            gameHistory.setGameResult(GameHistoryResult.VICTORY);
        }
        gameHistoryRepository.save(gameHistory);
    }



    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.info("Entered into Error");
        throwable.printStackTrace();
    }
}
