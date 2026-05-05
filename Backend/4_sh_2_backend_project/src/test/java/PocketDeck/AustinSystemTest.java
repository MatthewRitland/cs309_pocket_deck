package PocketDeck;

import static org.junit.jupiter.api.Assertions.assertEquals;

import PocketDeck.CardGames.CardGameRepository;
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
public class AustinSystemTest {

    @Autowired
    UserRepository userRepo;

    @Autowired
    CardGameRepository gameRepo;

    @Autowired
    GameNoteRepository noteRepo;

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void createGameNoteTest () {
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                body("This is a test").
                when().
                post("/gameNotes/1/2");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try {

            JSONObject returnObj = new JSONObject(returnString);
            JSONObject userObj = new JSONObject(returnObj.getString("user"));
            JSONObject gameObj = new JSONObject(returnObj.getString("game"));

            assertEquals("test1", userObj.getString("username"));
            assertEquals("blackjack", gameObj.getString("gameName"));
            assertEquals("This is a test", returnObj.getString("text"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateGameNoteTest () {
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                body("This is a test but again").
                when().
                put("/gameNotes/11");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try {

            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("This is a test but again", returnObj.getString("text"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateNonExistentGameNoteTest () {
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                body("this is a test but again").
                put("/gameNotes/100");

        int statusCode = response.getStatusCode();
        assertEquals(404, statusCode);

    }

    @Test
    public void createNonExistentGameGameNoteTest () {
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                body("This is a test").
                when().
                post("/gameNotes/1/10");

        int statusCode = response.getStatusCode();
        assertEquals(404, statusCode);

    }
}
