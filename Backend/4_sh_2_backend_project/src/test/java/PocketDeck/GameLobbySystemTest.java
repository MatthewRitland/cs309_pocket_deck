package PocketDeck;

import PocketDeck.CardGames.CardGame;
import PocketDeck.CardGames.CardGameRepository;
import PocketDeck.Game.Card;
import PocketDeck.Game.Game;
import PocketDeck.GameLobby.GameLobbyMembership;
import PocketDeck.GameLobby.GameLobbyMembershipRepository;
import PocketDeck.GameLobby.GameLobbyMembershipRole;
import PocketDeck.GameLobby.GameLobbyRepository;
import PocketDeck.Users.User;
import PocketDeck.Users.UserRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class GameLobbySystemTest {

	@Autowired
	UserRepository userRepo;
	@Autowired
	CardGameRepository cardGameRepo;
	@Autowired
	GameLobbyRepository gameLobbyRepo;
	@Autowired
	GameLobbyMembershipRepository gameLobbyMembershipRepo;

  	@LocalServerPort
	int port;


	int testUserId = 0;
	int testCardGame1Id = 0;
	int testCardGame2Id = 0;
	int testGameLobbyId = 0;

	@Before
	public void setUp() {
		RestAssured.port = port;
		RestAssured.baseURI = "http://localhost";

		User gameLobbyOwnerTest = new User("gameLobbyOwnerTestTest", "123");
		userRepo.save(gameLobbyOwnerTest);
		testUserId = gameLobbyOwnerTest.getId();

		// use same settings as our expected "poker" from cardGame table
		CardGame game1 = new CardGame("testPoker", 2, 4, 120);
		cardGameRepo.save(game1);
		testCardGame1Id = game1.getId();

		CardGame game2 = new CardGame("testBlackjack", 1, 4, 30);
		cardGameRepo.save(game2);
		testCardGame2Id = game2.getId();
	}


	@Test
	public void createGameLobbyTest() {
		// Send request and receive response
		Response response = RestAssured.given().
				header("Content-Type", "application/json").
				header("charset","utf-8").
				when().
				post("/gameLobbies/create/" + testUserId + "/" + testCardGame1Id);


		// Check status code
		assertEquals(200, response.getStatusCode());

		// Check response body for correct response
		String returnString = response.getBody().asString();
		try {
			JSONObject returnObj = new JSONObject(returnString);
			testGameLobbyId = returnObj.getInt("id");

			assertNotEquals(0, testGameLobbyId);
			assertTrue(returnObj.getBoolean("isInviteOnly"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	// tests to make sure a user is a member of MAX 1 gameLobby.
	@Test
	public void createGameLobbyLoyalMembershipTest() {
		// Send request and receive response
		Response response = RestAssured.given().
				header("Content-Type", "application/json").
				header("charset","utf-8").
				when().
				post("/gameLobbies/create/" + testUserId + "/" + testCardGame1Id);


		// Check response body for correct response
		String returnString = response.getBody().asString();
		try {
			JSONObject returnObj = new JSONObject(returnString);
			testGameLobbyId = returnObj.getInt("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Send request and receive response
		Response responseDuplicate = RestAssured.given().
				header("Content-Type", "application/json").
				header("charset","utf-8").
				when().
				post("/gameLobbies/create/" + testUserId + "/" + testCardGame2Id);

		// this should return throw a 400 BAD_REQUEST
		assertEquals(400, responseDuplicate.getStatusCode());
	}


	@Test
	public void changeGameModeTest() {
		// Send request and receive response
		Response response = RestAssured.given().
				header("Content-Type", "application/json").
				header("charset","utf-8").
				when().
				post("/gameLobbies/create/" + testUserId + "/" + testCardGame1Id);


		// Check response body for correct response
		String returnString = response.getBody().asString();
		try {
			JSONObject returnObj = new JSONObject(returnString);
			testGameLobbyId = returnObj.getInt("id");

			Response responseUpdate = RestAssured.given().
					header("Content-Type", "application/json").
					header("charset","utf-8").
					when().
					put("/gameLobbies/" + testGameLobbyId + "/cardGame/" + testCardGame2Id + "/" + testUserId);

			assertEquals(200, responseUpdate.getStatusCode());

			String updatedReturnString = responseUpdate.getBody().asString();
			JSONObject updatedReturnObj = new JSONObject(updatedReturnString);
			assertEquals(testCardGame2Id, updatedReturnObj.getJSONObject("cardGame").getInt("id"));
			assertEquals("testBlackjack", updatedReturnObj.getJSONObject("cardGame").getString("gameName"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	@Test
	public void leaveThenDeleteEmptyGameLobbyTest() {
		// Send request and receive response
		Response response = RestAssured.given().
				header("Content-Type", "application/json").
				header("charset","utf-8").
				when().
				post("/gameLobbies/create/" + testUserId + "/" + testCardGame1Id);

		// Check response body for correct response
		String returnString = response.getBody().asString();
		try {
			JSONObject returnObj = new JSONObject(returnString);
			testGameLobbyId = returnObj.getInt("id");

			Response responseDelete = RestAssured.given().
					header("Content-Type", "application/json").
					header("charset","utf-8").
					when().
					delete("/gameLobbies/leave/" + testUserId);

			assertEquals(200, responseDelete.getStatusCode());

			assertFalse(gameLobbyRepo.existsById(testGameLobbyId));
			testGameLobbyId = 0;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	@Test
	public void leaveThenReassignGameLobbyOwner() {
		// create a fake user that is a member
		User secondMember = new User("secondMemberTest", "123");
		userRepo.save(secondMember);
		int secondMemberId = secondMember.getId();

		// Send request and receive response
		Response response = RestAssured.given().
				header("Content-Type", "application/json").
				header("charset","utf-8").
				when().
				post("/gameLobbies/create/" + testUserId + "/" + testCardGame1Id);

		// Check response body for correct response
		String returnString = response.getBody().asString();
		try {
			JSONObject returnObj = new JSONObject(returnString);
			testGameLobbyId = returnObj.getInt("id");

			GameLobbyMembership secondMembership = new GameLobbyMembership(secondMember,
					gameLobbyRepo.findById(testGameLobbyId),
					GameLobbyMembershipRole.PLAYER);

			gameLobbyMembershipRepo.save(secondMembership);

			Response responseDelete = RestAssured.given().
					header("Content-Type", "application/json").
					header("charset","utf-8").
					when().
					delete("/gameLobbies/leave/" + testUserId);

			assertEquals(200, responseDelete.getStatusCode());

			assertTrue(gameLobbyRepo.existsById(testGameLobbyId));
			assertEquals(GameLobbyMembershipRole.OWNER_MEMBER, gameLobbyMembershipRepo.
					findByGameLobbyMemberId(secondMemberId).getMemberRole());
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			// "finally" do this to keep DB clean while keeping @After working
			gameLobbyMembershipRepo.deleteByGameLobbyMemberId(secondMemberId);
			userRepo.deleteById(secondMemberId);
		}
	}


	// runs after each test to clean it up
	@After
	public void cleanUp() {
		try {
			if (testUserId != 0) {
				gameLobbyMembershipRepo.deleteByGameLobbyMemberId(testUserId);
			}
			if (testGameLobbyId != 0) {
				if (gameLobbyRepo.existsById(testGameLobbyId)) {
					gameLobbyRepo.deleteById(testGameLobbyId);
				}
			}
			if (testCardGame1Id != 0) {
				cardGameRepo.deleteById(testCardGame1Id);
			}
			if (testCardGame2Id != 0) {
				cardGameRepo.deleteById(testCardGame2Id);
			}
			if (testUserId != 0) {
				userRepo.deleteById(testUserId);
			}
		} catch (Exception e) {
			System.out.println("Test cleanUp() failed "+ e.getMessage());
		} finally {
			testUserId = 0;
			testCardGame1Id = 0;
			testCardGame2Id = 0;
			testGameLobbyId = 0;
		}
	}


}