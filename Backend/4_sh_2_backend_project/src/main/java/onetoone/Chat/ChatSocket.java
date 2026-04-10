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

import javax.swing.*;
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
	private static GroupChatMembershipRepository groupChatMembershipRepo;
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
		groupChatMembershipRepo = gcmR;
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

		logger.info("Entered into @OnOpen, connected to websocket");

    	// store connecting user information
		sessionUsernameMap.put(session, username);
		usernameSessionMap.put(username, session);
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
						"   \"action\" : \"<AppropriateEnumeration>\",\n" +
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
				handleGetChatHistory(username, payload);
				break;

			case SEND:
				handleSendMessage(username, payload);
				break;

			case CREATE_GROUPCHAT:
				handleCreateGroupChat(username, payload);
				break;

			case ADD_USER:
				handleAddUser(username, payload);
				break;

			case LEAVE:
				handleLeaveGroupChat(username, payload);
				break;

			case DEBUG:
				//TODO?
				sendMessageToParticularUser(username, "CURRENTLY NOT IMPLEMENTED");
				break;
			default:
				logger.warn("Unspecified action was received from receivedJSON");
				sendMessageToParticularUser(username, "ERROR: action was not specified in message. use" +
						"GET_CHAT_HISTORY, SEND, ADD_USER, LEAVE, or DEBUG");

		}
	}


	@OnClose
	public void onClose(Session session) throws IOException {
		String username = sessionUsernameMap.get(session);
		logger.info(username + " entered into @OnClose, disconnected from websocket");

    	// remove the user connection information
		sessionUsernameMap.remove(session);
		usernameSessionMap.remove(username);
	}


	@OnError
	public void onError(Session session, Throwable throwable) {
		// Do error handling here
		logger.info("Entered into Error");
		throwable.printStackTrace();
	}


	// helper method that requires the session exists and is open to work. Sends a message
	// between this session's user and target user
	private void sendMessageToParticularUser(String targetUsername, String message) {
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

	// =============================== Helper methods for switch cases ================================== //

	private void handleGetChatHistory(String username, ChatMessageData payload) {
		Long historyGroupChatId = payload.getGroupChatId();
		List<Message> chatHistoryMessages = msgRepo.findByGroupChatId(historyGroupChatId);

		StringBuilder sb = new StringBuilder();
		if(chatHistoryMessages != null && chatHistoryMessages.size() != 0) {
			for (Message message : chatHistoryMessages) {
				sb.append(message.getUser().getUsername() + ": " + message.getContent() + "\n");
			}
			sendMessageToParticularUser(username, sb.toString());
		}
		else {
			sendMessageToParticularUser(username, "No messages found for chatGroupId: " + historyGroupChatId);
		}
	}

	private void handleSendMessage(String username, ChatMessageData payload) {
		String messageContent = payload.getMessageContent();
		Long targetGroupChatId = payload.getGroupChatId();

		User user = userRepo.findByUsername(username);
		GroupChat groupChat = groupChatRepo.findById(targetGroupChatId).orElse(null);

		if (user != null && groupChat != null) {
			List<GroupChatMembership> memberships = groupChatMembershipRepo.findByGroupChatId(targetGroupChatId);

			// make sure that the user is in that group chat they are trying to message
			boolean memberIsInGroupChat = false;
			// iterate through each member in the target group chat until find the user
			for(GroupChatMembership membership: memberships) {
				if (membership.getUser().getUsername().equals(username)) {
					memberIsInGroupChat = true;
					break;
				}
			}

			if (!memberIsInGroupChat) {
				logger.warn("User " + username + " does not belong to target group chat");
				sendMessageToParticularUser(username, "ERROR: User does not belong to target group chat");
				return;
			}
			//otherwise, user is a part of the groupchat, proceed
			Message newMsg = new Message(user, groupChat, messageContent);
			msgRepo.save(newMsg);

			for(GroupChatMembership membership: memberships) {
				String memberUsername = membership.getUser().getUsername();
				Session memberSession = usernameSessionMap.get(memberUsername);

				if (memberSession != null && memberSession.isOpen()) {
					sendMessageToParticularUser(memberUsername, user.getUsername() + ": " + messageContent);
				}
			}
		}
		else {
			logger.warn("invalid sender or groupChat ID");
			sendMessageToParticularUser(username, "ERROR: Message failed to save, User or groupChatId does not exist in the Database");
		}
	}

	private void handleCreateGroupChat(String username, ChatMessageData payload) {
		String newGroupChatName = payload.getMessageContent();

		if (newGroupChatName == null) {
			sendMessageToParticularUser(username, "ERROR: Group chat name is null");
			return;
		}

		User creator = userRepo.findByUsername(username);
		if (creator != null) {
			GroupChat newGroupChat = new GroupChat();
			newGroupChat.setGroupName(newGroupChatName);
			groupChatRepo.save(newGroupChat);

			GroupChatMembership newGroupChatMembership = new GroupChatMembership();
			newGroupChatMembership.setUser(creator);
			newGroupChatMembership.setGroupChat(newGroupChat);
			groupChatMembershipRepo.save(newGroupChatMembership);

			sendMessageToParticularUser(username, "Group chat " + newGroupChatName +
					" was successfully created with ID: " + newGroupChat.getId());
		}
		else {
			sendMessageToParticularUser(username, "ERROR: Did not create group, user not found");
		}
	}

	private void handleAddUser(String username, ChatMessageData payload) {
		String addedUsername = payload.getMessageContent();
		Long groupToJoinId = payload.getGroupChatId();

		User addedUser = userRepo.findByUsername(addedUsername);
		GroupChat groupToJoin = groupChatRepo.findById(groupToJoinId).orElse(null);

		if (addedUser != null && groupToJoin != null) {
			List<GroupChatMembership> currentMemberships = groupChatMembershipRepo.findByGroupChatId(groupToJoinId);

			boolean isAlreadyMember = false;
			boolean isSessionUserInGroup = false;
			for (GroupChatMembership membership: currentMemberships) {
				if (membership.getUser().getUsername().equals(username)) {
					isSessionUserInGroup = true;
				}
				if (membership.getUser().getUsername().equals(addedUsername)) {
					isAlreadyMember = true;
				}
				// if both values are true, found that session user and addedUser are in the group chat
				if(isAlreadyMember && isSessionUserInGroup) {
					break;
				}
			}
			if (!isSessionUserInGroup) {
				sendMessageToParticularUser(username, "ERROR: Can't add users to a group chat this user doesn't belong to");
				return;
			}

			if (isAlreadyMember) {
				sendMessageToParticularUser(username, "ERROR: User " +
						addedUsername + " is already in this groupchat");
			}
			else {
				GroupChatMembership newMembership = new GroupChatMembership();
				newMembership.setUser(addedUser);
				newMembership.setGroupChat(groupToJoin);
				groupChatMembershipRepo.save(newMembership);

				String newMemberAnnouncement = "NEW MEMBER: " + addedUsername + " was added by " + username + " to " + groupToJoin.getGroupChatName();
				// notify each member of the groupchat
				for (GroupChatMembership membership: currentMemberships) {
					sendMessageToParticularUser(membership.getUser().getUsername(), newMemberAnnouncement);
				}
				// need this as well since the list from above didn't have the added user in it
				sendMessageToParticularUser(addedUsername, "You were added to the group chat " +
						groupToJoin.getGroupChatName() + " by " + username);
			}
		}
		else {
			sendMessageToParticularUser(username, "ERROR: Failed to add user. user or group chat does not exist");
		}
	}

	private void handleLeaveGroupChat(String username, ChatMessageData payload) {
		Long groupToLeaveId = payload.getGroupChatId();
		GroupChat groupToLeave = groupChatRepo.findById(groupToLeaveId).orElse(null);
		User leavingUser = userRepo.findByUsername(username);

		if (leavingUser != null && groupToLeave != null) {

			List<GroupChatMembership> currentMemberships = groupChatMembershipRepo.findByGroupChatId(groupToLeaveId);
			boolean isMember = false;
			for (GroupChatMembership membership: currentMemberships) {
				if (membership.getUser().getUsername().equals(username)) {
					isMember = true;
					break;
				}
			}

			if (isMember) {
				groupChatMembershipRepo.deleteByGroupChatIdAndUserId(groupToLeaveId, leavingUser.getId());
				sendMessageToParticularUser(username, "You have left the group chat " + groupToLeave.getGroupChatName());

				List<GroupChatMembership> remainingMemberships = groupChatMembershipRepo.findByGroupChatId(groupToLeaveId);
				for (GroupChatMembership membership: remainingMemberships) {
					sendMessageToParticularUser(membership.getUser().getUsername(), username + " has left the group chat " + groupToLeave.getGroupChatName());
				}
				if (remainingMemberships.isEmpty()) {

					msgRepo.deleteByGroupChatId(groupToLeaveId);
					groupChatRepo.deleteById(groupToLeaveId);
					sendMessageToParticularUser(username, groupToLeave.getGroupChatName() + " was auto deleted along with its messages due to having no more users");

				}
			}
			else {
				sendMessageToParticularUser(username, "ERROR: User can't leave group chat they're not a member of");
			}
		}
		else if (leavingUser == null){
			sendMessageToParticularUser(username, "ERROR: user is null");
		}
		else {
			sendMessageToParticularUser(username, "ERROR: group chat is null");
		}
	}



} // end of Class
