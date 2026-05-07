package PocketDeck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import PocketDeck.CardGames.CardGame;
import PocketDeck.CardGames.CardGameRepository;
import PocketDeck.GameNotes.GameNote;
import PocketDeck.GameNotes.GameNoteRepository;
import PocketDeck.Users.User;
import PocketDeck.Users.UserRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class CardGameSystemTest {
    @Autowired
    UserRepository userRepo;

    @Autowired
    CardGameRepository gameRepo;

    @LocalServerPort
    int port;

    int id = 0;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void getAllGamesTest() {
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/cardGames");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String responseString = response.getBody().asString();
        assertNotEquals(null, responseString);
    }

    @Test
    public void createCardGameTest () {
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                body("{" +
                        "\"gameName\"" + " : " + "\"SystemCardGameTest\"," +
                        "\"minPlayers\"" + " : " + "\"1\"," +
                        "\"maxPlayers\"" + " : " + "\"10\"," +
                        "\"turnTimeLimit\"" + " : " + "\"100\"" +
                        "}").
                when().
                post("/cardGames");

        int responseCode = response.getStatusCode();
        assertEquals(200, responseCode);

        String responseString = response.getBody().asString();
        try {
            JSONObject responseObj = new JSONObject(responseString);
            assertEquals("game was created and saved", responseObj.get("message"));
            id = gameRepo.findByGameName("SystemCardGameTest").getId();
        } catch (JSONException e) {
            assertEquals(null, e);
            e.printStackTrace();
        }
    }

    @Test
    public void getOneCardGameTest() {
        CardGame game = new CardGame ("test", 1, 5, 120);
        gameRepo.save(game);
        id = game.getId();
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/cardGames/" + id);

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String responseString = response.getBody().asString();
        try {
            JSONObject responseObj = new JSONObject(responseString);
            assertEquals("test", responseObj.get("gameName"));
            assertEquals(1, responseObj.getInt("minPlayers"));
            assertEquals(5, responseObj.getInt("maxPlayers"));
            assertEquals(120, responseObj.getInt("turnTimeLimit"));
        } catch (JSONException e) {
            assertEquals(null, e);
            e.printStackTrace();
        }
    }

    @Test
    public void updateCardGameTest() {
        CardGame game = new CardGame ("test", 1, 20, 50);
        gameRepo.save(game);
        id = game.getId();
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                body("{" +
                        "\"gameName\"" + " : " + "\"SystemCardGameTest\"," +
                        "\"minPlayers\"" + " : " + "\"1\"," +
                        "\"maxPlayers\"" + " : " + "\"10\"," +
                        "\"turnTimeLimit\"" + " : " + "\"100\"" +
                        "}").
                when().
                put("/cardGames/" + id);

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String responseString = response.getBody().asString();
        try {
            JSONObject responseObj = new JSONObject(responseString);
            assertEquals("SystemCardGameTest", responseObj.get("gameName"));
            assertEquals(1, responseObj.getInt("minPlayers"));
            assertEquals(10, responseObj.getInt("maxPlayers"));
            assertEquals(100, responseObj.getInt("turnTimeLimit"));
        } catch (JSONException e) {
            assertEquals(null, e);
            e.printStackTrace();
        }
    }

    @Test
    public void deleteCardGameTest() {
        CardGame game = new CardGame ("test", 1, 20, 50);
        gameRepo.save(game);
        id = game.getId();
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                delete("/cardGames/" + id);

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);
        String responseString = response.getBody().asString();

        try {
            JSONObject responseObj = new JSONObject(responseString);
            assertEquals("game was deleted", responseObj.get("message"));
        } catch (JSONException e) {
            assertEquals(null, e);
            e.printStackTrace();
        }
    }

    @Test
    public void cardGameClassTest() {
        CardGame game = new CardGame();
        game.setGameName("test");
        assertEquals("test", game.getGameName());
        game.setMaxPlayers(100);
        assertEquals(100, game.getMaxPlayers());
        game.setMinPlayers(0);
        assertEquals(0, game.getMinPlayers());
        game.setTurnTimeLimit(1000);
        assertEquals(1000, game.getTurnTimeLimit());
        gameRepo.save(game);
        id = game.getId();
        assert(game.equals(gameRepo.findById(id)));
    }

    @After
    public void cleanUp () {
        if (id != 0) {
            try {
                if (gameRepo.existsById(id)) {
                    gameRepo.deleteById(id);
                }
            }
            catch (Exception e) {
                System.out.println("Test cleanUp() failed for gameId " + id + ": " + e.getMessage());
            }
            finally {
                id = 0;
            }
        }
    }

}
