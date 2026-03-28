package com.cs309.websocket3.chat;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller      // this is needed for this to be an endpoint to springboot
@ServerEndpoint(value = "/chat/{username}")  // this is Websocket url
public class ChatSocket {

  // cannot autowire static directly (instead we do it by the below
  // method
	private static MessageRepository msgRepo;

	// declared here so don't have to
	private static ObjectMapper objectMapper; // VERY helpful for converting JSON strings into java objects!!
	/*
   * Grabs the MessageRepository singleton from the Spring Application
   * Context.  This works because of the @Controller annotation on this
   * class and because the variable is declared as static.
   * There are other ways to set this. However, this approach is
   * easiest.
	 */
	@Autowired
	public void setMessageRepository(MessageRepository repo) {
		msgRepo = repo;  // we are setting the static variable
	}


	// do something similar (@controller AND static declaration allows this)
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

		//Send chat history to the newly connected user
		sendMessageToPArticularUser(username, getChatHistory());
		
    // broadcast that new user joined
		String message = "User:" + username + " has Joined the Chat";
		broadcast(message);
	}


	@OnMessage
	// DOES NOT DEAL WITH FAULTY INPUT YET!!!
	public void onMessage(Session session, String recievedJSON) throws IOException {

		// Handle new messages
		logger.info("Entered into Message: Got Message:" + recievedJSON);
		String username = sessionUsernameMap.get(session);

		// use objectMapper to convert from JSON string to object (from jackson)
		ChatMessageData payload = objectMapper.readValue(recievedJSON, ChatMessageData.class);


		switch(payload.getAction()) {
			case SEND:
				String messageContent = payload.getMessageContent();
				//todo: will need to change from simple broadcast to real-time message delivery
				broadcast(username + ": " + messageContent);
				msgRepo.save(new Message(username, messageContent));
				break;

			case ADD_USER:
				//todo: will do this later, should add a user to a thread/groupchat

				break;

			case LEAVE:
				//todo: will implement later, for when a user wants to leave a specific groupchat, NOT THE WHOLE CONNECTION

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


	private void sendMessageToPArticularUser(String username, String message) {
		try {
			usernameSessionMap.get(username).getBasicRemote().sendText(message);
		} 
    catch (IOException e) {
			logger.info("Exception: " + e.getMessage().toString());
			e.printStackTrace();
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
	

  // Gets the Chat history from the repository
	private String getChatHistory() {
		List<Message> messages = msgRepo.findAll();
    
    // convert the list to a string
		StringBuilder sb = new StringBuilder();
		if(messages != null && messages.size() != 0) {
			for (Message message : messages) {
				sb.append(message.getUserName() + ": " + message.getContent() + "\n");
			}
		}
		return sb.toString();
	}

} // end of Class
