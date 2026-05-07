package PocketDeck;

import PocketDeck.CardGames.CardGame;
import PocketDeck.Chat.*;
import PocketDeck.GameLobby.*;
import PocketDeck.Users.User;
import PocketDeck.Users.UserRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Member;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ChatSystemTest {

	@Autowired
	UserRepository userRepo;
	@Autowired
	GroupChatRepository groupChatRepo;
	@Autowired
	GroupChatMembershipRepository groupChatMembershipRepo;
	@Autowired
	MessageRepository msgRepo;

  	@LocalServerPort
	int port;


	int testUserId1 = 0;
	int testUserId2 = 0;
	long testGroupChatId = 0;
	long testMessageId = 0;

	@Before
	public void setUp() {
		RestAssured.port = port;
		RestAssured.baseURI = "http://localhost";

		User user1 = new User("msgTest1", "123");
		User user2 = new User("msgTest2", "123");

		userRepo.save(user1);
		userRepo.save(user2);

		testUserId1 = user1.getId();
		testUserId2 = user2.getId();

		GroupChat gc = new GroupChat("Testing GroupChat");
		groupChatRepo.save(gc);
		testGroupChatId = gc.getId();

		GroupChatMembership membership1 = new GroupChatMembership();
		membership1.setUser(user1);
		membership1.setGroupChat(gc);
		groupChatMembershipRepo.save(membership1);

		GroupChatMembership membership2 = new GroupChatMembership();
		membership2.setUser(user2);
		membership2.setGroupChat(gc);
		groupChatMembershipRepo.save(membership2);

		Message msg = new Message(user1, gc, "Test Message");
		msgRepo.save(msg);
		testMessageId = msg.getId();
	}


	@Test
	public void getGroupChatHistoryTest() {
		// Send request and receive response
		Response response = RestAssured.given().
				header("Content-Type", "application/json").
				header("charset","utf-8").
				when().
				get("/groupChat/history/" + testGroupChatId);


		// Check status code
		assertEquals(200, response.getStatusCode());

		// Check response body for correct response
		String returnString = response.getBody().asString();
		try {
			JSONArray returnArray = new JSONArray((returnString));
			assertTrue(returnArray.length() > 0);

			assertEquals("Test Message", returnArray.getJSONObject(0).getString("content"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	// tests to make sure a user is a member of MAX 1 gameLobby.
	@Test
	public void getGroupChatHistoryNotFoundTest() {
		// Send request and receive response
		Response response = RestAssured.given().
				header("Content-Type", "application/json").
				header("charset","utf-8").
				when().
				get("/groupChat/history/-1");


		// Check response body for correct response
		assertEquals(404, response.getStatusCode());
	}


	@Test
	public void getGroupChatsForUserTest() {
		Response response = RestAssured.given().
				header("Content-Type", "application/json").
				header("charset","utf-8").
				when().
				get("/user/groupChats/" + testUserId1);

		assertEquals(200, response.getStatusCode());

		String returnString = response.getBody().asString();
		try {
			JSONArray returnArray = new JSONArray(returnString);
			assertTrue(returnArray.length() > 0);

			assertEquals("Testing GroupChat", returnArray.getJSONObject(0).getString("groupChatName"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	@Test
	public void getGroupChatsForUserNotFoundTest() {
		Response response = RestAssured.given().
				header("Content-Type", "application/json").
				header("charset","utf-8").
				when().
				get("/user/groupChats/-1");

		assertEquals(404, response.getStatusCode());
	}


	@Test
	public void getGroupChatMembershipTest() {
		// Send request and receive response
		Response response = RestAssured.given().
				header("Content-Type", "application/json").
				header("charset","utf-8").
				when().
				get("/groupChatMembers/" + testGroupChatId);

		assertEquals(200, response.getStatusCode());
		// Check response body for correct response
		String returnString = response.getBody().asString();
		try {
			JSONArray returnArray = new JSONArray(returnString);
			assertEquals(2, returnArray.length());

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void groupChatGettersAndSettersTest() {
		GroupChat gc = new GroupChat ();
		gc.setGroupName("Test GroupChatGS");

		assertEquals("Test GroupChatGS", gc.getGroupChatName());
	}

	@Test
	public void groupChatMembershipGettersAndSettersTest() {
		GroupChat gc = new GroupChat("Membership Test Gc");
		User tmpUser = new User("test1","123");
		GroupChatMembership membership = new GroupChatMembership();

		membership.setGroupChat(gc);
		membership.setUser(tmpUser);

		assertEquals(tmpUser, membership.getUser());
		assertEquals(gc, membership.getGroupChat());
		assertNull(membership.getId());
	}

	@Test
	public void messageGettersAndSettersTest() {
		GroupChat gc = new GroupChat("Membership Test Gc");
		User tmpUser = new User("test1", "123");
		Date dateNow = new Date();
		Message msg = new Message();

		msg.setGroupChat(gc);
		msg.setUser(tmpUser);
		msg.setSent(dateNow);
		msg.setContent("Test Content");

		assertEquals(gc, msg.getGroupChat());
		assertEquals(tmpUser, msg.getUser());
		assertEquals(dateNow, msg.getSent());
		assertEquals("Test Content", msg.getContent());

		assertNull(msg.getId());
	}

	@Test
	public void chatMessageDataGettersAndSettersTest() {
		GroupChat gc = new GroupChat("Message Data Test Gc");
		ChatMessageData msgData = new ChatMessageData();

		msgData.setMessageAction(ChatMessageAction.SEND);
		msgData.setMessageContent("Test Content");

		assertEquals(ChatMessageAction.SEND, msgData.getMessageAction());
		assertEquals("Test Content", msgData.getMessageContent());
	}





	// runs after each test to clean it up
	@After
	public void cleanUp() {
		try {
			if (testGroupChatId != 0) {
				msgRepo.deleteByGroupChatId(testGroupChatId);
				List<GroupChatMembership> memberships = groupChatMembershipRepo.findByGroupChatId(testGroupChatId);
				groupChatMembershipRepo.deleteAll(memberships);
				groupChatRepo.deleteById(testGroupChatId);
			}
			if (testUserId1 != 0) {
				userRepo.deleteById(testUserId1);
			}
			if (testUserId2 != 0) {
				userRepo.deleteById(testUserId2);
			}
		} catch (Exception e) {
			System.out.println("Test cleanUp() failed "+ e.getMessage());
		} finally {
			testUserId1 = 0;
			testUserId2 = 0;
			testGroupChatId = 0;
			testMessageId = 0;
		}
	}


}