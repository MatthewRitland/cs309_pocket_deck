package PocketDeck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
public class AustinSystemTest {

    @Autowired
    UserRepository userRepo;

    @Autowired
    CardGameRepository gameRepo;

    @Autowired
    GameNoteRepository noteRepo;

    @LocalServerPort
    int port;

    int id = 0;

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
            id = returnObj.getInt("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateGameNoteTest () {
        Response response1 = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                body("This is a test but again").
                when().
                post("/gameNotes/1/2");
        try {
            JSONObject createdNoteJSON = new JSONObject(response1.getBody().asString());
            id = createdNoteJSON.getInt("id");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                body("This is a test but again").
                when().
                put("/gameNotes/" + id);

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

    @Test
    public void getUserTest () {
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/gameNotes/8");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);
        assertNotEquals(null, response);
    }

    @Test
    public void getUserGameTest () {
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/gameNotes/8/2");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);
        assertNotEquals(null, response);
    }

    @Test
    public void deleteGameNoteTest () {
        GameNote note = new GameNote("This is a test", userRepo.findById(1), gameRepo.findById(2));
        noteRepo.save(note);
        id = note.getId();
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                delete("/gameNotes/" + id);
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);
        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("note was successfully deleted", returnObj.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void gettersAndSettersTest () {
        GameNote note = new GameNote ();
        note.setText("This is a test");
        assertEquals("This is a test", note.getText());
        note.setGame(gameRepo.findById(2));
        assertEquals(gameRepo.findById(2), note.getGame());
        note.setUser(userRepo.findById(1));
        assertEquals(userRepo.findById(1), note.getUser());
        assertEquals("This is a test", note.toString());
    }

    @After
    public void cleanUp () {
        if (id != 0) {
            try {
                if (noteRepo.existsById(id)) {
                    noteRepo.deleteById(id);
                }
            }
            catch (Exception e) {
                System.out.println("Test cleanUp() failed for noteId " + id + ": " + e.getMessage());
            }
            finally {
                id = 0;
            }
        }
    }
}
