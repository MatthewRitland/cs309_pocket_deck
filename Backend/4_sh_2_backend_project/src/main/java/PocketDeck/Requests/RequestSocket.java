package PocketDeck.Requests;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import PocketDeck.GameLobby.*;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static GameLobbyRepository gameLobbyRepo;
    private static GameLobbyMembershipRepository gameLobbyMembershipRepo;
    private static ObjectMapper objectMapper; // VERY helpful for converting JSON strings into java objects!!


    @Autowired
    public void setRequestRepository(RequestRepository repo) {
        requestRepo = repo;
    }
    @Autowired
    public void setUserRepository(UserRepository repo) {
        userRepo = repo;
    }
    @Autowired
    public void setGameLobbyRepository(GameLobbyRepository repo) {gameLobbyRepo = repo; }
    @Autowired
    public void setGameLobbyMembershipRepository(GameLobbyMembershipRepository repo) { gameLobbyMembershipRepo = repo; }
    @Autowired
    public void setObjectMapper(ObjectMapper mapper) {
        objectMapper = mapper; // used for RequestMessageData
    }

    private final Logger logger = LoggerFactory.getLogger(RequestSocket.class);
    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();

    @OnOpen
    public void onOpen (Session session, @PathParam("username") String username) throws IOException {
        logger.info("Entered into open");
        sessionUsernameMap.put(session, username); // a list of sessions linked by usernames (strings)
        usernameSessionMap.put(username, session);
    }

    @OnMessage
    public void onMessage (Session session, String message) throws IOException {
        logger.info("Entered into Message: Got Message:" + message);
        String username = sessionUsernameMap.get(session);

        try {
            RequestMessageData payload = objectMapper.readValue(message, RequestMessageData.class);
            RequestMessageAction action = payload.getAction();
            String targetUsername = payload.getTargetUsername();

            if (action == null || targetUsername == null) {
                session.getBasicRemote().sendText("invalid message input, missing action or targetUsername");
                return;
            }

            if (action == RequestMessageAction.INVITE) {
                logger.info("target user to INVITE is: " + targetUsername);
                User requester = userRepo.findByUsername(username);
                User requested = userRepo.findByUsername(targetUsername);

                // make sure there isn't a lobby invite already pending between them
                if (requester != null && requested != null) {
                    Request existingCheck = requestRepo.findByRequestedIdAndRequesterIdAndStatus(requested.getId(), requester.getId(), RequestStatus.PENDING);
                    if (existingCheck != null && existingCheck.getStatus() == RequestStatus.PENDING) {
                        session.getBasicRemote().sendText("An invite to this user is already pending!");
                        return;
                    }

                    Session requestedSession = usernameSessionMap.get(targetUsername);
                    if (requestedSession != null) {
                        requestedSession.getBasicRemote().sendText(username + " invites you to a lobby!");
                    }

                    Request request = new Request();
                    request.setRequester(userRepo.findByUsername(username));
                    request.setRequested(userRepo.findByUsername(targetUsername));
                    request.setStatus(RequestStatus.PENDING);

                    // if no id was provided in the payload, it defaulted to 0. will be > 0 if correctly given
                    if (payload.getGameLobbyId() > 0) {
                        GameLobby gameLobby = gameLobbyRepo.findById(payload.getGameLobbyId());
                        request.setGameLobby(gameLobby);
                    }
                    requestRepo.save(request);
                }
            }

            else if (action == RequestMessageAction.ACCEPT) {
                User requested = userRepo.findByUsername(username);
                User requester = userRepo.findByUsername(targetUsername);

                if (requested != null && requester != null) {
                    Request request = requestRepo.findByRequestedIdAndRequesterIdAndStatus(requested.getId(), requester.getId(), RequestStatus.PENDING);
                    if (request != null) {
                        GameLobbyMembership existingMember = gameLobbyMembershipRepo.findByGameLobbyMemberId(requested.getId());
                        if (existingMember != null) {
                            // if they're already in a lobby and about to join a new one, them remove them from the existing lobby
                            gameLobbyMembershipRepo.delete(existingMember);
                        }

                        request.setStatus(RequestStatus.ACCEPTED);
                        requestRepo.save(request);

                        if (request.getGameLobby() != null) {
                            GameLobbyMembership newMember = new GameLobbyMembership(requested, request.getGameLobby(), GameLobbyMembershipRole.PLAYER);
                            gameLobbyMembershipRepo.save(newMember);
                            Session requesterSession = usernameSessionMap.get(targetUsername);
                            if (requesterSession != null) {
                                requesterSession.getBasicRemote().sendText(username + " accepted your lobby invitation!");
                            }
                        }
                    }
                }
            }

            else if (action == RequestMessageAction.REJECT) {
                User requested = userRepo.findByUsername(username);
                User requester = userRepo.findByUsername(targetUsername);

                if (requested != null && requester != null) {
                    Request request = requestRepo.findByRequestedIdAndRequesterIdAndStatus(requested.getId(), requester.getId(), RequestStatus.PENDING);
                    if (request != null) {
                        request.setStatus(RequestStatus.REJECTED); // instead of deleting, status is set as rejected.
                        requestRepo.save(request);
                        Session requesterSession = usernameSessionMap.get(targetUsername);
                        if (requesterSession != null) {
                            requesterSession.getBasicRemote().sendText(username + " denied your lobby invitation :(");
                        }
                    }
                }
            }

        } catch (IOException e) {
                logger.info("Exception: " + e.getMessage().toString());
                e.printStackTrace();
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
