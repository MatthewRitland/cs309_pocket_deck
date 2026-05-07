package PocketDeck;

import PocketDeck.CardGames.CardGameRepository;
import PocketDeck.GameLobby.GameLobbyRepository;
import PocketDeck.GameNotes.GameNote;
import PocketDeck.GameNotes.GameNoteRepository;
import PocketDeck.Requests.*;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class RequestSystemTest {

    @Autowired
    UserRepository userRepo;

    @Autowired
    RequestRepository requestRepo;

    @Autowired
    GameLobbyRepository gameLobbyRepo;

    @LocalServerPort
    int port;

    int id = 0;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void getAllRequestsTest() {
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/request");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String responseString = response.getBody().asString();
        assertNotNull(responseString);
    }

    @Test
    public void getRequestRequestedRequestertest() {
        Request request = new Request ();
        request.setRequester(userRepo.findById(1));
        request.setRequested(userRepo.findById(2));
        request.setStatus(RequestStatus.PENDING);
        request.setGameLobby(gameLobbyRepo.findById(163));
        requestRepo.save(request);
        id = request.getId();
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/request/requested/2/1/" + RequestStatus.PENDING);

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String responseString = response.getBody().asString();
        try {
            JSONObject responseObj = new JSONObject(responseString);
            JSONObject requester = responseObj.getJSONObject("requester");
            JSONObject requested = responseObj.getJSONObject("requested");
            assertEquals(1, requester.getInt("id"));
            assertEquals(2, requested.getInt("id"));
            assertEquals(RequestStatus.PENDING.toString(), responseObj.get("status"));
        } catch (JSONException e) {
            assertNull(e);
            e.printStackTrace();
        }
    }

    @Test
    public void getOneRequestTest() {
        Request request = new Request ();
        request.setRequester(userRepo.findById(1));
        request.setRequested(userRepo.findById(2));
        request.setStatus(RequestStatus.PENDING);
        request.setGameLobby(gameLobbyRepo.findById(163));
        requestRepo.save(request);
        id = request.getId();
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/request/" + id);
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String responseString = response.getBody().asString();
        try {
            JSONObject responseObj = new JSONObject(responseString);
            JSONObject requester = responseObj.getJSONObject("requester");
            JSONObject requested = responseObj.getJSONObject("requested");
            assertEquals(1, requester.getInt("id"));
            assertEquals(2, requested.getInt("id"));
            assertEquals(RequestStatus.PENDING.toString(), responseObj.get("status"));
        } catch (JSONException e) {
            assertNull(e);
            e.printStackTrace();
        }
    }

    @Test
    public void getRequestsByRequestedTest() {
        Request request = new Request ();
        request.setRequester(userRepo.findById(1));
        request.setRequested(userRepo.findById(2));
        request.setStatus(RequestStatus.PENDING);
        request.setGameLobby(gameLobbyRepo.findById(163));
        requestRepo.save(request);
        id = request.getId();
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/request/requested/2");
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String responseString = response.getBody().asString();
        assertNotNull(responseString);
    }

    @Test
    public void createRequestTest() {
        JSONObject request = new JSONObject();
        JSONObject requester = new JSONObject();
        JSONObject requested = new JSONObject();
        JSONObject gameLobby = new JSONObject();
        JSONObject cardGame = new JSONObject();
        try {
            requester.put("id", 1);
            requester.put("username", userRepo.findById(1).getUsername());
            requester.put("password", userRepo.findById(1).getPassword());
            requester.put("userStatus", userRepo.findById(1).getUserStatus());
            requested.put("id", 2);
            requested.put("username", userRepo.findById(2).getUsername());
            requested.put("password", userRepo.findById(2).getPassword());
            requested.put("userStatus", userRepo.findById(2).getUserStatus());
            request.put("requested", requested);
            request.put("requester", requester);
            cardGame.put("id", 2);
            cardGame.put("gameName", "blackjack");
            cardGame.put("minPlayers", 1);
            cardGame.put("maxPlayers", 4);
            cardGame.put("turnTimeLimit", 30);
            gameLobby.put("id", 163);
            gameLobby.put("cardGame", cardGame);
            gameLobby.put("isInviteOnly", true);
            request.put("gameLobby", gameLobby);
            request.put("status", "PENDING");
            Response response = RestAssured.given().
                    header("Content-Type", "application/json").
                    header("charset","utf-8").
                    body(request.toString()).
                    when().
                    post("/request");
            int statusCode = response.getStatusCode();
            assertEquals(200, statusCode);
            String responseString = response.getBody().asString();
            JSONObject responseObj = new JSONObject(responseString);
            id = responseObj.getInt("id");
            JSONObject gameLobbyResponse = responseObj.getJSONObject("gameLobby");
            JSONObject requestedResponse = responseObj.getJSONObject("requested");
            JSONObject requesterResponse = responseObj.getJSONObject("requester");
            assertEquals(163, gameLobbyResponse.getInt("id"));
            assertEquals(1, requesterResponse.getInt("id"));
            assertEquals(2, requestedResponse.getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteRequestTest() {
        Request request = new Request ();
        request.setRequester(userRepo.findById(1));
        request.setRequested(userRepo.findById(2));
        request.setStatus(RequestStatus.PENDING);
        request.setGameLobby(gameLobbyRepo.findById(163));
        requestRepo.save(request);
        id = request.getId();
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                delete("/request/" + id);

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);
        String responseString = response.getBody().asString();
        try {
            JSONObject responseObj = new JSONObject(responseString);
            assertEquals("success", responseObj.get("message"));
        } catch (JSONException e) {
            assertNull(e);
            e.printStackTrace();
        }
    }

    @Test
    public void requestMessageDataTest() {
        RequestMessageData data = new RequestMessageData();
        data.setAction(RequestMessageAction.INVITE);
        assertEquals(RequestMessageAction.INVITE, data.getAction());
        data.setGameLobbyId(1);
        assertEquals(1, data.getGameLobbyId());
        data.setTargetUsername("test1");
        assertEquals("test1", data.getTargetUsername());
    }

    @Test
    public void requestClassTest() {
        Request request = new Request ();
        request.setRequester(userRepo.findById(1));
        assertEquals(userRepo.findById(1), request.getRequester());
        request.setRequested(userRepo.findById(2));
        assertEquals(userRepo.findById(2), request.getRequested());
        request.setStatus(RequestStatus.PENDING);
        assertEquals(RequestStatus.PENDING, request.getStatus());
        request.setGameLobby(gameLobbyRepo.findById(163));
        assertEquals(gameLobbyRepo.findById(163).getId(), request.getGameLobby().getId());
    }

    @After
    public void cleanUp () {
        if (id != 0) {
            try {
                if (requestRepo.existsById(id)) {
                    requestRepo.deleteById(id);
                }
            }
            catch (Exception e) {
                System.out.println("Test cleanUp() failed for requestId " + id + ": " + e.getMessage());
            }
            finally {
                id = 0;
            }
        }
    }
}
