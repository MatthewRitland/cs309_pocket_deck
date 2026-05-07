package PocketDeck;

import PocketDeck.Friends.Friendship;
import PocketDeck.Friends.FriendshipRepository;
import PocketDeck.Friends.FriendshipStatus;
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
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class FriendsSystemTest {

    @Autowired
    UserRepository userRepo;
    @Autowired
    FriendshipRepository friendshipRepo;

    @LocalServerPort
    int port;


    int testUserId1 = 0;
    int testUserId2 = 0;
    int testUserId3 = 0;
    int testFriendshipId = 0;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        User user1 = new User("friendTest1", "123");
        User user2 = new User("friendTest2", "123");
        User user3 = new User("friendTest2", "123");

        userRepo.save(user1);
        userRepo.save(user2);
        userRepo.save(user3);

        testUserId1 = user1.getId();
        testUserId2 = user2.getId();
        testUserId3 = user3.getId();

        Friendship friendship = new Friendship(user1, user2);
        friendshipRepo.save(friendship);
        testFriendshipId = friendship.getId();

    }


    @Test
    public void sendFriendRequestTest() {
        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                post("/friendships/request/" + testUserId2 + "/" + testUserId3);


        // Check status code
        assertEquals(200, response.getStatusCode());

        // Check response body for correct response
        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject((returnString));
            assertEquals("PENDING", returnObj.getString("friendshipStatus"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendFriendRequestToSelfTest() {
        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                post("/friendships/request/" + testUserId1 + "/" + testUserId1);


        // Check status code
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void sendAnotherFriendRequestWhenAlreadyFriendsTest() {
        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                post("/friendships/request/" + testUserId1 + "/" + testUserId2);


        // Check status code
        assertEquals(409, response.getStatusCode());
    }


    @Test
    public void getPendingFriendshipRequestsTest() {
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                get("/friendships/requests/sent/" + testUserId1);

        assertEquals(200, response.getStatusCode());

        String returnString = response.getBody().asString();
        try {
            JSONArray returnArray = new JSONArray(returnString);
            assertTrue(returnArray.length() > 0);

            assertEquals("PENDING", returnArray.getJSONObject(0).getString("friendshipStatus"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void acceptFriendshipRequestTest() {
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                put("/friendships/accept/" + testFriendshipId);

        assertEquals(200, response.getStatusCode());
        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("FRIEND", returnObj.getString("friendshipStatus"));
            assertNotNull(returnObj.getString("dateBefriended"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void removeFriendshipTest() {
        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                when().
                delete("/friendships/" + testFriendshipId);

        assertEquals(200, response.getStatusCode());
        assertNull(friendshipRepo.findById(testFriendshipId));
    }

    @Test
    public void friendshipGettersAndSettersTest() {
        User reqUser = new User("requester", "123");
        User recUser = new User("receiver", "123");
        LocalDate now = LocalDate.now();

        Friendship friendship = new Friendship();

        friendship.setRequester(reqUser);
        friendship.setReceiver(recUser);

        friendship.setDateBefriended(now);

        friendship.setFriendshipStatus(FriendshipStatus.FRIEND);

        assertEquals(reqUser, friendship.getRequester());
        assertEquals(recUser, friendship.getReceiver());

        assertEquals(now, friendship.getDateBefriended());

        assertEquals(FriendshipStatus.FRIEND, friendship.getFriendshipStatus());
    }


    // runs after each test to clean it up
    @After
    public void cleanUp() {
        try {
            friendshipRepo.deleteById(testFriendshipId);

            if (testUserId1 != 0) {
                userRepo.deleteById(testUserId1);
            }
            if (testUserId2 != 0) {
                userRepo.deleteById(testUserId2);
            }
            if (testUserId3 != 0) {
                userRepo.deleteById(testUserId3);
            }
        } catch (Exception e) {
            System.out.println("Test cleanUp() failed "+ e.getMessage());
        } finally {
            testUserId1 = 0;
            testUserId2 = 0;
            testUserId3 = 0;
            testFriendshipId = 0;
        }
    }
}