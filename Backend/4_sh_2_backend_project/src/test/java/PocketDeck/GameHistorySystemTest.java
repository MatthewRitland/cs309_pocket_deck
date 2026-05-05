package PocketDeck;

import PocketDeck.CardGames.CardGame;
import PocketDeck.CardGames.CardGameRepository;
import PocketDeck.GameHistory.GameHistory;
import PocketDeck.GameHistory.GameHistoryRepository;
import PocketDeck.GameHistory.GameHistoryResult;
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
    @Autowired
    GameHistoryRepository gameHistoryRepo;

    @LocalServerPort
    int port;


    int testUserId = 0;
    int testCardGame1Id = 0;
    int testGameHistoryRecordId = 0;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        User gamehistoryTestUser = new User("gameHistoryTest", "123");
        userRepo.save(gamehistoryTestUser);
        testUserId = gamehistoryTestUser.getId();
        // use same settings as our expected "poker" from cardGame table
        CardGame game1 = new CardGame("testPoker", 2, 4, 120);
        cardGameRepo.save(game1);
        testCardGame1Id = game1.getId();
    }


    @Test
    public void createGameHistoryRecordTest() {
        Response createResponse = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                post("/users/gamehistory/" + testUserId + "/" + testCardGame1Id);

        assertEquals(200, createResponse.getStatusCode());
        String returnString = createResponse.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            testGameHistoryRecordId = returnObj.getInt("id");

            assertNotEquals(0, testGameHistoryRecordId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getGameHistoryRecordsForUserTest() {
        Response createResponse = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                post("/users/gamehistory/" + testUserId + "/" + testCardGame1Id);
        try {
            JSONObject createdObj = new JSONObject(createResponse.getBody().asString());
            testGameHistoryRecordId = createdObj.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Response getResponse = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/users/gamehistory/" + testUserId);

        assertEquals(200, getResponse.getStatusCode());

        String returnString = getResponse.getBody().asString();
        try {
            JSONArray returnArr = new JSONArray(returnString);
            // should have found at least 1 entry since one was made here
            assertTrue(returnArr.length() > 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void gameHistoryGettersAndSettersTest() {
        CardGame testGame = cardGameRepo.findById(testCardGame1Id);
        User testUser = userRepo.findById(testUserId);

        GameHistory testGameHistoryRecord = new GameHistory();

        testGameHistoryRecord.setCardGame(testGame);
        assertEquals(testGameHistoryRecord.getCardGame(), testGame);

        testGameHistoryRecord.setUser(testUser);
        assertEquals(testUser, testGameHistoryRecord.getUser());


        testGameHistoryRecord.setGameResult(GameHistoryResult.DEFEAT);
        assertEquals(GameHistoryResult.DEFEAT, testGameHistoryRecord.getGameResult());

        testGameHistoryRecord.setMinutes(2);
        testGameHistoryRecord.setSeconds(30);

        assertEquals(2, testGameHistoryRecord.getMinutes());
        assertEquals(30,testGameHistoryRecord.getSeconds());

        assertNotNull(testGameHistoryRecord.getCardGame());
        assertNotNull(testGameHistoryRecord.getUser());
    }

    // runs after each test to clean it up
    @After
    public void cleanUp() {
        try {
            if (testGameHistoryRecordId != 0) {
                if (gameHistoryRepo.findById(testGameHistoryRecordId) != null) {

                    gameHistoryRepo.deleteById(testGameHistoryRecordId);
                }
            }
            if (testCardGame1Id != 0) {
                cardGameRepo.deleteById(testCardGame1Id);
            }
            if (testUserId != 0) {
                userRepo.deleteById(testUserId);
            }
        } catch (Exception e) {
            System.out.println("Test cleanUp() failed "+ e.getMessage());
        } finally {
            testUserId = 0;
            testCardGame1Id = 0;
            testGameHistoryRecordId = 0;
        }
    }


}