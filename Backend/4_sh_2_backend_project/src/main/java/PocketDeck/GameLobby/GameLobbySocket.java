package PocketDeck.GameLobby;

import PocketDeck.Requests.*;
import PocketDeck.Users.User;
import PocketDeck.Users.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Controller
@ServerEndpoint(value = "/gamelobbies/listenForUpdates/{gameLobbyId}/{username}")
public class GameLobbySocket {

    private final Logger logger = LoggerFactory.getLogger(GameLobbySocket.class);
    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();

    // need this to map gameLobbyId to a list of active sessions in a lobby
    private static Map<Integer, List<Session>> lobbySessions = new Hashtable<>();


    @OnOpen
    public void onOpen(Session session, @PathParam("gameLobbyId") int gameLobbyId, @PathParam("username")
    String username) throws IOException {
        logger.info("Entered into open");
        sessionUsernameMap.put(session, username); // a list of sessions linked by usernames (strings)
        usernameSessionMap.put(username, session);

        // if the lobby isn't in the lobbySessions map yet, make a new empty list
        if (!lobbySessions.containsKey(gameLobbyId)) {
            lobbySessions.put(gameLobbyId, new ArrayList<>()); // use .put to make new ArrayList
        }
        // add user's session to the lobbySession list
        lobbySessions.get(gameLobbyId).add(session); // use .add to add the user to that ArrayList

        logger.info("User {} joined GameLobbySocket for gameLobbyId {}", username, gameLobbyId);
    }


    @OnClose
    public void onClose(Session session, @PathParam("gameLobbyId") int gameLobbyId) throws IOException {
        logger.info("Entered into Close");

        String username = sessionUsernameMap.get(session);
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);

        List<Session> sessions = lobbySessions.get(gameLobbyId);
        if (sessions != null) {
            sessions.remove(session); // removes user that left
            if (sessions.isEmpty()) {
                lobbySessions.remove(gameLobbyId);
            }
        }
    }


    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.info("Entered into Error");
        throwable.printStackTrace();
    }


    public static void broadcastToLobby(int gameLobbyId, String message) {
        List<Session> sessions = lobbySessions.get(gameLobbyId);

        if (sessions != null) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
