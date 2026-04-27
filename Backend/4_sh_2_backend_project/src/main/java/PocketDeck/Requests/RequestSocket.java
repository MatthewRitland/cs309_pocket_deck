package PocketDeck.Requests;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import PocketDeck.GameLobby.GameLobby;
import PocketDeck.GameLobby.GameLobbyMembership;
import PocketDeck.GameLobby.GameLobbyMembershipRepository;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnClose;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import PocketDeck.Users.UserRepository;
import PocketDeck.Users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@ServerEndpoint(value = "/request/{username}")
public class RequestSocket {

    private static RequestRepository requestRepo;

    private static UserRepository userRepo;

    private static GameLobbyMembershipRepository memberRepo;

    @Autowired
    public void setRequestRepository(RequestRepository repo) {
        requestRepo = repo;
    }
    @Autowired
    public void setUserRepository(UserRepository repo) {
        userRepo = repo;
    }
    @Autowired
    public void setGameLobbyMembershipRepository(GameLobbyMembershipRepository repo) { memberRepo = repo; }

    private final Logger logger = LoggerFactory.getLogger(RequestSocket.class);
    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();

    @OnOpen
    public void onOpen (Session session, @PathParam("username") String username) throws IOException {
        logger.info("Entered into open");
        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);
    }

    @OnMessage
    public void onMessage (Session session, String message) throws IOException, NullPointerException {
        logger.info("Entered into Message: Got Message:" + message);
        String username = sessionUsernameMap.get(session);
        if (message.startsWith("invite")) {
            String requested = message.substring(7);
            requested = requested.strip();
            try {
                logger.info(requested);
                usernameSessionMap.get(requested).getBasicRemote().sendText(username +
                        " invites you to a game");
            }
            catch (IOException | NullPointerException e) {
                logger.info("Exception: " + e.getMessage().toString());
                e.printStackTrace();
            }
            Request request = new Request();
            logger.info(String.valueOf(userRepo.existsByUsername(requested)));
            request.setRequester(userRepo.findByUsername(username));
            request.setRequested(userRepo.findByUsername(requested));
            request.setStatus(RequestStatus.PENDING);
            requestRepo.save(request);
        }
        if (message.startsWith("accept")) {
            User requested = userRepo.findByUsername(username);
            User requester = userRepo.findByUsername(message.substring(7));
            List<Request> reqs = requestRepo.findByRequestedIdAndRequesterId(requested.getId(), requester.getId());
            reqs.get(reqs.size() - 1).setStatus(RequestStatus.ACCEPTED);
            requestRepo.save(reqs.get(reqs.size() - 1));
            GameLobby lobby = memberRepo.findById(requester.getId());
            GameLobbyMembership member = new GameLobbyMembership();
            member.setGameLobbyMember(requested);
            member.setGameLobby(lobby);
            memberRepo.save(member);
        }
        if (message.startsWith("reject")) {
            User requested = userRepo.findByUsername(username);
            User requester = userRepo.findByUsername(message.substring(7));
            List<Request> reqs = requestRepo.findByRequestedIdAndRequesterId(requested.getId(), requester.getId());
            reqs.get(reqs.size() - 1).setStatus(RequestStatus.REJECTED);
            requestRepo.save(reqs.get(reqs.size() - 1));
        }
    }


    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("Entered into Close");

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
