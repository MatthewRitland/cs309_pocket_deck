package PocketDeck.Requests;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

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

    @Autowired
    public void setRequestRepository(RequestRepository repo) {
        requestRepo = repo;
    }
    @Autowired
    public void setUserRepository(UserRepository repo) {
        userRepo = repo;
    }

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
    public void onMessage (Session session, String message) throws IOException {
        logger.info("Entered into Message: Got Message:" + message);
        String username = sessionUsernameMap.get(session);
        if (message.startsWith("invite")) {
            try {
                logger.info(message.substring(7));
                usernameSessionMap.get(message.substring(7)).getBasicRemote().sendText(username +
                        " invites you to a game");
            }
            catch (IOException e) {
                logger.info("Exception: " + e.getMessage().toString());
                e.printStackTrace();
            }
            Request request = new Request();
            request.setRequester(userRepo.findByUsername(username));
            request.setRequested(userRepo.findByUsername(message.substring(7)));
            request.setStatus(RequestStatus.PENDING);
            requestRepo.save(request);
        }
        if (message.startsWith("accept")) {
            User requested = userRepo.findByUsername(username);
            User requester = userRepo.findByUsername(message.substring(7));
            Request req = requestRepo.findByRequestedIdAndRequesterId(requested.getId(), requester.getId());
            req.setStatus(RequestStatus.ACCEPTED);
            requestRepo.save(req);
        }
        if (message.startsWith("reject")) {
            User requested = userRepo.findByUsername(username);
            User requester = userRepo.findByUsername(message.substring(7));
            Request req = requestRepo.findByRequestedIdAndRequesterId(requested.getId(), requester.getId());
            req.setStatus(RequestStatus.REJECTED);
            requestRepo.save(req);
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
