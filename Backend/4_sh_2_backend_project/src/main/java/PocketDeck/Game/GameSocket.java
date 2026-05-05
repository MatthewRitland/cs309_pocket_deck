package PocketDeck.Game;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import PocketDeck.GameLobby.GameLobbyMembership;
import PocketDeck.GameLobby.GameLobbyMembershipRepository;
import PocketDeck.GameLobby.GameLobbyRepository;
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
@ServerEndpoint(value = "/game/{gameLobbyId}/{username}")
public class GameSocket {

    private static GameHistoryRepository gameHistoryRepository;

    private static UserRepository userRepo;

    private static CardGameRepository cardGameRepository;

    private static GameLobbyRepository gameLobbyRepo;

    private static GameLobbyMembershipRepository gameLobbyMembershipRepo;

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

    @Autowired
    public void setGameLobbyRepository(GameLobbyRepository repo) { gameLobbyRepo = repo; }

    @Autowired
    public void setGameLobbyMembershipRepository(GameLobbyMembershipRepository repo) { gameLobbyMembershipRepo = repo; }

    private final Logger logger = LoggerFactory.getLogger(GameSocket.class);
    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();

    private static Map<Integer, Game> activeGames = new Hashtable<>();
    private static Map<Integer, List<Session>> lobbySessions = new Hashtable<>();
    private GameHistory gameHistory;

    @OnOpen
    public void onOpen(Session session, @PathParam("gameLobbyId") int gameLobbyId, @PathParam("username") String username) throws IOException {
        logger.info("Entered into open");

        User user = userRepo.findByUsername(username);
        if(user == null) {
            session.getBasicRemote().sendText("user does not exist, closing session...");
            session.close();
            return;
        }
        GameLobbyMembership membership = gameLobbyMembershipRepo.findByGameLobbyMemberId(user.getId());
        if(membership == null || membership.getGameLobby().getId() != gameLobbyId) {
            session.getBasicRemote().sendText("You are not a member of this lobby. Closing session...");
            session.close();
            return;
        }
        logger.info("Verified that this session belongs to game lobby");

        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);

        // if session list from lobbySessions doesn't exist, then initialize it
        if (!lobbySessions.containsKey(gameLobbyId)) {
            lobbySessions.put(gameLobbyId, new ArrayList<>());
        }
        lobbySessions.get(gameLobbyId).add(session);

        String gameName = gameLobbyRepo.findById(gameLobbyId).getCardGame().getGameName();
        gameHistory = new GameHistory(userRepo.findByUsername(username), cardGameRepository.findByGameName(gameName));
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("gameLobbyId") int gameLobbyId) throws IOException, ParseException {
        // *** As of 05/04, GOT RID OF join_game since that is handled by game lobby ***
        JSONObject json = (JSONObject) new JSONParser().parse(message);
        String gameName = gameLobbyRepo.findById(gameLobbyId).getCardGame().getGameName();

        // IMPORTANT: only OWNER_MEMBER from gameLobby should send this message exactly once
        // (or technically you can just have MAXIMUM OF ONE member send th start_game message...)
        if (json.get("messageType").equals("start_game")) {
            logger.info(gameName);
            List<GameLobbyMembership> memberships = gameLobbyMembershipRepo.findByGameLobbyId(gameLobbyId);
            User[] players = new User[memberships.size()];
            for (int i = 0; i < memberships.size(); i++) {
                players[i] = memberships.get(i).getGameLobbyMember();
            }

            Game newGame = new BlackJack(cardGameRepository.findByGameName(gameName), players);
            activeGames.put(gameLobbyId, newGame);
            broadcastGameState(gameLobbyId, gameName);

        } else if (json.get("messageType").equals("action_made")) {
            Game currentGame = activeGames.get(gameLobbyId);
            if (currentGame == null || currentGame.checkGameProgress()) return;
            if (sessionUsernameMap.get(session).equals(currentGame.getCurrentPlayer().getUsername())) {
                if (json.get("move").equals("hit")) {
                    currentGame.takeTurn(Actions.HIT);
                } else if (json.get("move").equals("stand")) {
                    currentGame.takeTurn(Actions.STAND);
                }

                broadcastGameState(gameLobbyId, gameName);
            }
        }
    }

    // helper to broadcast the game state instead of looping through every player seperately in the old code
    private void broadcastGameState(int gameLobbyId, String gameName) {
        List<Session> sessions = lobbySessions.get(gameLobbyId);
        if (sessions != null) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    try {
                        String username = sessionUsernameMap.get(session);
                        JSONObject output = makeOutput(gameName, username, gameLobbyId);
                        session.getBasicRemote().sendText(output.toJSONString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private JSONObject makeOutput (String game, String username, int gameLobbyId) {
        Game currentGame = activeGames.get(gameLobbyId);
        User[] currentUsers = currentGame.getPlayers();

        JSONObject output = new JSONObject();
        output.put("messageType", "game_state");
        output.put("gameName", game);
        output.put("status", "Seat " + currentGame.findCurrentPlayer() + " turn");
        if (currentGame.getClass().equals(BlackJack.class)) {
            BlackJack temp = (BlackJack) currentGame;
            JSONArray array = new JSONArray();
            Card[] dealerHand = temp.getDealerHand();
            int cardCount;
            for (cardCount = 0; cardCount < dealerHand.length; cardCount++) {
                if (dealerHand[cardCount] == null) {
                    break;
                }
            }
            if (!currentGame.checkGameProgress()) {
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
        output.put("yourSeat", currentGame.findPlayer(userRepo.findByUsername(username)));
        output.put("currentTurn", currentGame.getTurn());
        if (currentGame.checkGameProgress()) {
            output.put("gamePhase", "finished");
        } else {
            output.put("gamePhase", "in progress");
        }
        JSONArray actionArray = new JSONArray();
        Actions[] actions = currentGame.getPossibleActions();
        for (int i = 0; i < actions.length; i++) {
            actionArray.add(actions[i].toString());
        }
        output.put("actions", actionArray);
        JSONArray players = new JSONArray();
        for (int i = 0; i < currentUsers.length; i++) {
            JSONObject player = new JSONObject();
            player.put("seat", i);
            player.put("username", currentUsers[i].getUsername());
            if (currentGame.getClass().equals(BlackJack.class)) {
                BlackJack temp = (BlackJack) currentGame;
                player.put("stood", String.valueOf(temp.getStood()[i]));
            }
            if (currentUsers[i].getUsername().equals(username)) {
                JSONArray array = new JSONArray();
                if (currentGame.getClass().equals(BlackJack.class)) {
                    BlackJack temp = (BlackJack) currentGame;
                    player.put("busted", String.valueOf(temp.isBusted(currentUsers[i])));
                }
                int cardCount = currentGame.getCardAmount(currentUsers[i]);
                Card[] playerHand = currentGame.getPlayerHand(currentUsers[i]);
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
                player.put("hiddenCount", currentGame.getCardAmount(currentUsers[i]));
            }
            players.add(player);
        }
        output.put("players", players);
        if (currentGame.checkGameProgress()) {
            JSONArray array = new JSONArray();
            for (int j = 0; j < currentUsers.length; j++) {
                array.add(currentGame.getWinners()[j].toString());
            }
            output.put("winners", array);

            // may be useful for debug
            if (currentGame.getClass().equals(BlackJack.class)) {
                BlackJack temp = (BlackJack) currentGame;
                logger.info(String.valueOf(temp.getDealerScore()));
            }
        }
        return output;
    }

    @OnClose
    public void onClose(Session session, @PathParam("gameLobbyId") int gameLobbyId) throws IOException {
        logger.info("Entered into Close");
        String username = sessionUsernameMap.get(session);
        recordGame(session, gameLobbyId);
        logger.info(username);
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);

        if(lobbySessions.containsKey(gameLobbyId)) {
            lobbySessions.get(gameLobbyId).remove(session);

            if(lobbySessions.get(gameLobbyId).isEmpty()) {
                lobbySessions.remove(gameLobbyId);
                activeGames.remove(gameLobbyId);
                logger.info("GameLobby with id " + gameLobbyId + " is now empty and deleted from GameSocket.");
            }
        }
    }

    private void recordGame (Session session, int gameLobbyId) {
        Game currentGame = activeGames.get(gameLobbyId);
        // if there was no game for some reason just return (maybe players left before anything happened)
        if(currentGame == null) return;

        gameHistory.setTimeGameCompleted(LocalDateTime.now());
        Duration duration = Duration.between(gameHistory.getTimeGameStarted(), gameHistory.getTimeGameCompleted());
        gameHistory.setMinutes(duration.toMinutes());
        gameHistory.setSeconds(duration.getSeconds() - (gameHistory.getMinutes() * 60));

        int playerIdx = currentGame.findPlayer(userRepo.findByUsername(sessionUsernameMap.get(session)));

        if (playerIdx != -1) {
            Result result = currentGame.getWinners()[playerIdx];
            if (result != null) {
                if (result.equals(Result.DRAW)) {
                    gameHistory.setGameResult(GameHistoryResult.DRAW);
                } else if (result.equals(Result.LOSE)) {
                    gameHistory.setGameResult(GameHistoryResult.DEFEAT);
                } else if (result.equals(Result.WIN)) {
                    gameHistory.setGameResult(GameHistoryResult.VICTORY);
                }
            }
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
