package PocketDeck;

import PocketDeck.CardGames.CardGame;
import PocketDeck.CardGames.CardGameRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class GameHistorySystemTest {

	@Autowired
	UserRepository userRepo;
	@Autowired
	CardGameRepository cardGameRepo;

  	@LocalServerPort
	int port;


	int testUserId = 0;
	int testCardGame1Id = 0;
	int testGameHistoryRecordId = 0;

	@Before
	public void setUp() {
		RestAssured.port = port;
		RestAssured.baseURI = "http://localhost";

		// use same settings as our expected "poker" from cardGame table
		CardGame game1 = new CardGame("testPoker", 2, 4, 120);
		cardGameRepo.save(game1);
		testCardGame1Id = game1.getId();
	}


	@Test
	public void createGameHistoryRecordTest() {
	}


	// runs after each test to clean it up
	@After
	public void cleanUp() {
	}


}