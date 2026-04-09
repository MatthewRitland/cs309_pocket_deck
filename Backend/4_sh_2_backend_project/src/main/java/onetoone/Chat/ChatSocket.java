package onetoone.Chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import onetoone.Users.User;
import onetoone.Users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Controller      // this is needed for this to be an endpoint to springboot
@ServerEndpoint(value = "/chat/{username}")  // this is Websocket url
public class ChatSocket {

  // cannot autowire static directly (instead we do it by the below
  // method
	private static MessageRepository msgRepo;
	private static GroupChatRepository groupChatRepo;
	private static GroupChatMembershipRepository groupMembershipRepo;
	private static UserRepository userRepo;
	private static ObjectMapper objectMapper; // VERY helpful for converting JSON strings into java objects!!
	/*
   * Grabs the MessageRepository singleton from the Spring Application
   * Context.  This works because of the @Controller annotation on this
   * class and because the variable is declared as static.
   * There are other ways to set this. However, this approach is
   * easiest.
	 */

	// singleton = only one instance of that class
	@Autowired
	public void setMessageRepository(MessageRepository msgR) {
		msgRepo = msgR;  // we are setting the static variable
	}
	@Autowired
	public void setGroupChatRepository(GroupChatRepository gcR) {
		groupChatRepo = gcR;
	}
	@Autowired
	public void setGroupMemberShipRepository(GroupChatMembershipRepository gcmR) {
		groupMembershipRepo = gcmR;
	}
	@Autowired
	public void setUserRepository(UserRepository uR) {
		userRepo = uR;
	}
	// (@controller AND static declaration allows this)
	@Autowired
	public void setObjectMapper(ObjectMapper mapper) {
		objectMapper = mapper; // set the static variable
	}
	// Store all socket session and their corresponding username.
	private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
	private static Map<String, Session> usernameSessionMap = new Hashtable<>();

	private final Logger logger = LoggerFactory.getLogger(ChatSocket.class);




	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username)
      throws IOException {

		logger.info("Entered into Open");

    // store connecting user information
		sessionUsernameMap.put(session, username);
		usernameSessionMap.put(username, session);

    // broadcast that new user joined
		String message = "User:" + username + " has Joined the Chat";
		broadcast(message);
	}


	@OnMessage
	public void onMessage(Session session, String recievedJSON) throws IOException {

		// Handle new messages
		logger.info("Entered into Message: Got Message:" + recievedJSON);
		String username = sessionUsernameMap.get(session);
		ChatMessageData payload = new ChatMessageData();
		payload.setMessageAction(MessageAction.DEBUG);
		payload.setMessageContent("Something went wrong. This is a fake debugging message");

		try {
			// use objectMapper to convert from JSON string to object (from jackson)
			payload = objectMapper.readValue(recievedJSON, ChatMessageData.class);
		} catch (Exception e) {
			// need to tell client that it was invalid message format, since websocket connection otherwise won't
			// tell that the server code (in this file) failed.
			try {
				session.getBasicRemote().sendText(
						"ERROR: Invalid message format, expected a JSON object (of ChatMessageData) such as:\n" +
						"{\n" +
						"   \"action\" : \"<AppriateEnumeration>\",\n" +
						"   \"messageContent\" : \"hello world\",\n" +
						"   \"groupChatId\" : <Long>\n" +
			         	"}"
				);
			} catch (IOException ioException) {
				logger.error("failed to send error message to client");
			}
			logger.error("Failed to parse incoming message as JSON: " + recievedJSON);
			return;
		}

		switch(payload.getAction()) {
			case GET_CHAT_HISTORY:
				Long historyGroupChatId = payload.getGroupChatId();
				List<Message> chatHistoryMessages = msgRepo.findByGroupChatId(historyGroupChatId);

				StringBuilder sb = new StringBuilder();
				if(chatHistoryMessages != null && chatHistoryMessages.size() != 0) {
					for (Message message : chatHistoryMessages) {
						sb.append(message.getUser().getUsername() + ": " + message.getContent() + "\n");
					}
					sendMessageToPArticularUser(username, sb.toString());
				}
				else {
					sendMessageToPArticularUser(username, "No messages found for chatGroupId: " + historyGroupChatId);
				}
				break;

			case SEND:
				String messageContent = payload.getMessageContent();
				Long targetGroupChatId = payload.getGroupChatId();

				User user = userRepo.findByUsername(username);
				GroupChat groupChat = groupChatRepo.findById(targetGroupChatId).orElse(null);

				if (user != null && groupChat != null) {
					Message newMsg = new Message(user, groupChat, messageContent);
					msgRepo.save(newMsg);

					List<GroupChatMembership> members = groupMembershipRepo.findByGroupChatId(targetGroupChatId);

					for(GroupChatMembership member : members) {
						String memberUsername = member.getUser().getUsername();
						Session memberSession = usernameSessionMap.get(memberUsername);

						if (memberSession != null && memberSession.isOpen()) {
							sendMessageToPArticularUser(memberUsername, user.getUsername() + ": " + messageContent);
						}
					}
				}
				else {
					logger.warn("invalid sender or groupChat ID");
					sendMessageToPArticularUser(username, "ERROR: Message failed to save, User or groupChatId does not exist in the Database");
				}
				break;

			case ADD_USER:
				//TODO
				break;

			case LEAVE:
				//TODO
				break;

			default:
				logger.warn("Unspecified action was recieved!!");
		}

		/*
    // Direct message to a user using the format "@username <message>"
		if (recievedJSON.startsWith("@")) {
			String destUsername = recievedJSON.split(" ")[0].substring(1);

      // send the message to the sender and receiver
			sendMessageToPArticularUser(destUsername, "[DM] " + username + ": " + recievedJSON);
			sendMessageToPArticularUser(username, "[DM] " + username + ": " + recievedJSON);

		} 
    else { // broadcast
			broadcast(username + ": " + recievedJSON);
		}

		// Saving chat history to repository
		msgRepo.save(new Message(username, recievedJSON));

		 */
	}


	@OnClose
	public void onClose(Session session) throws IOException {
		logger.info("Entered into Close");

    // remove the user connection information
		String username = sessionUsernameMap.get(session);
		sessionUsernameMap.remove(session);
		usernameSessionMap.remove(username);

    // broadcase that the user disconnected
		String message = username + " disconnected";
		broadcast(message);
	}


	@OnError
	public void onError(Session session, Throwable throwable) {
		// Do error handling here
		logger.info("Entered into Error");
		throwable.printStackTrace();
	}


	// requires that teh session exists and is open to work.
	private void sendMessageToPArticularUser(String targetUsername, String message) {
		Session session = usernameSessionMap.get(targetUsername);
		if (session != null && session.isOpen()) {
			try {
				session.getBasicRemote().sendText(message);
			}
			catch (IOException e) {
				logger.info("Exception: " + e.getMessage().toString());
				e.printStackTrace();
			}
		}

	}


	private void broadcast(String message) {
		sessionUsernameMap.forEach((session, username) -> {
			try {
				session.getBasicRemote().sendText(message);
			} 
      catch (IOException e) {
				logger.info("Exception: " + e.getMessage().toString());
				e.printStackTrace();
			}

		});

	}
	



} // end of Class
