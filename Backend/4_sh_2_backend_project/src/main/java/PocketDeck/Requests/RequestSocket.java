package PocketDeck.Requests;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
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

                    // make sure that the inviting user is a member
                    GameLobbyMembership requesterMembership = gameLobbyMembershipRepo.findByGameLobbyMemberId(requester.getId());

                    if (requesterMembership == null || requesterMembership.getGameLobby().getId() != payload.getGameLobbyId()) {
                        session.getBasicRemote().sendText("You can't invite players to a lobby you aren't in!");
                        return;
                    }
                    // is user already in this game lobby?
                    GameLobbyMembership requestedMembership = gameLobbyMembershipRepo.findByGameLobbyMemberId(requested.getId());
                    if (requestedMembership != null && payload.getGameLobbyId() > 0) {
                        if (requestedMembership.getGameLobby().getId() == payload.getGameLobbyId()) {
                            session.getBasicRemote().sendText("This user is already in your lobby!");
                            return;
                        }
                    }

                    // does this user have an existing request history? (caused by rejecting or accepting, then getting invited again)
                    Request existingCheck = requestRepo.findByRequestedIdAndRequesterId(requested.getId(), requester.getId());
                    if (existingCheck != null) {
                        if (existingCheck.getStatus() == RequestStatus.PENDING) {
                            session.getBasicRemote().sendText("An invite to this user is already pending!");
                            return;
                        }
                        else {
                            requestRepo.deleteById(existingCheck.getId());
                        }
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
                            GameLobby previousGameLobby = existingMember.getGameLobby();
                            int previousGameLobbyId = previousGameLobby.getId();
                            boolean wasOwner = (existingMember.getMemberRole() == GameLobbyMembershipRole.OWNER_MEMBER);

                            // delete user's requests sent in previous game lobby
                            List<Request> sentRequests = requestRepo.findByRequesterIdAndGameLobbyId(requested.getId(), previousGameLobbyId);
                            for (Request r : sentRequests) requestRepo.deleteById(r.getId());

                            List<Request> receivedRequests = requestRepo.findByRequestedIdAndGameLobbyId(requested.getId(), previousGameLobbyId);
                            for (Request r : receivedRequests) requestRepo.deleteById(r.getId());

                            // remove the user from the previous lobby
                            gameLobbyMembershipRepo.delete(existingMember);

                            // reassign member status or delete previous lobby
                            if (gameLobbyMembershipRepo.countByGameLobbyId(previousGameLobby.getId()) == 0) {
                                List<Request> requestsForEmptyLobby = requestRepo.findByGameLobbyId(previousGameLobby.getId());
                                for (Request r : requestsForEmptyLobby) {
                                    requestRepo.deleteById(r.getId());
                                }
                                gameLobbyRepo.deleteById(previousGameLobby.getId());
                            }
                            else {
                                if (wasOwner) {
                                    List<GameLobbyMembership> previousGameLobbyMemberships = gameLobbyMembershipRepo.findByGameLobbyId(previousGameLobbyId);
                                    for (GameLobbyMembership foundMembership : previousGameLobbyMemberships) {
                                        if (foundMembership.getGameLobbyMember().getId() == requested.getId()) {
                                            continue;
                                        }
                                        foundMembership.setMemberRole(GameLobbyMembershipRole.OWNER_MEMBER);
                                        gameLobbyMembershipRepo.save(foundMembership);
                                        break;
                                    }
                                }
                                // notify previous lobby members (use this in frontend to update their screens)
                                GameLobbySocket.broadcastToLobby(previousGameLobbyId, "{\"type\":\"LOBBY_UPDATE\", \"lobbyId\":" + previousGameLobbyId + "}");
                            }

                        }


                        request.setStatus(RequestStatus.ACCEPTED);
                        requestRepo.save(request);

                        if (request.getGameLobby() != null) {
                            GameLobbyMembership newMember = new GameLobbyMembership(requested, request.getGameLobby(), GameLobbyMembershipRole.PLAYER);
                            gameLobbyMembershipRepo.save(newMember);

                            // notify other lobby members that this user joined
                            GameLobbySocket.broadcastToLobby(request.getGameLobby().getId(), "{\"type\":\"LOBBY_UPDATE\", \"lobbyId\":" + request.getGameLobby().getId() + "}");
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
