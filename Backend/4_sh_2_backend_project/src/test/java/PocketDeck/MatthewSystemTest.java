package PocketDeck;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import org.springframework.boot.test.web.server.LocalServerPort;	// SBv3

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class MatthewSystemTest {

	@Autowired
	UserRepository userRepo;

  	@LocalServerPort
	int port;

	@Before
	public void setUp() {
		RestAssured.port = port;
		RestAssured.baseURI = "http://localhost";
	}
	int userId = 0;


	@Test
	public void createUserTest() {
		// Send request and receive response
		Response response = RestAssured.given().
				header("Content-Type", "application/json").
				header("charset","utf-8").
				body("{" +
							"\"username\"" + " : " + "\"SystemTestUserRESTRICTED\"," +
							"\"password\"" + " : " + "\"1234\"" +
						"}").
				when().
				post("/signup");


		// Check status code
		int statusCode = response.getStatusCode();
		assertEquals(200, statusCode);

		// Check response body for correct response
		String returnString = response.getBody().asString();
		try {
			//JSONArray returnArr = new JSONArray(returnString); //DONT USE, THIS TEST RETURNS AN OBJECT, NOT AN ARRAY

			JSONObject returnObj = new JSONObject(returnString);

			//assertEquals(<expectedValue>, <keyFromJSON>)
			userId = userRepo.findByUsername("SystemTestUserRESTRICTED").getId(); // use userId for @After delete
			assertEquals(userId, returnObj.getInt("id"));
			assertEquals("SystemTestUserRESTRICTED", returnObj.getString("username"));
			assertEquals("1234", returnObj.getString("password"));
			assertEquals("OFFLINE", returnObj.getString("userStatus"));

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	@Test
	public void createDuplicateUserTest() {
		// create this test user in the DB
		User testUser = new User("SystemTestUserDuplicateRESTRICTED", "1234");
		userRepo.save(testUser);
		userId = testUser.getId();

		// using Response, NOW try to create a new user  w/ duplicate username
		Response response = RestAssured.given().
				header("Content-Type", "application/json").
				header("charset","utf-8").
				body("{" +
						"\"username\"" + " : " + "\"SystemTestUserDuplicateRESTRICTED\"," +
						"\"password\"" + " : " + "\"5678\"" +
						"}").
				when().
				post("/signup");


		// Check status code
		int statusCode = response.getStatusCode();
		assertEquals(409, statusCode);
	}


	@Test
	public void ReadUserTest() {
		// create this test user in the DB
		User testUser = new User("SystemTestUserReadRESTRICTED", "1234");
		userRepo.save(testUser);
		userId = testUser.getId();

		Response response = RestAssured.given().
				header("Content-Type", "application/json").
				header("charset","utf-8").
				when().
				get("/users/" + userId);


		// Check status code
		int statusCode = response.getStatusCode();
		assertEquals(200, statusCode);

		String returnString = response.getBody().asString();
		try {
			JSONObject returnObj = new JSONObject(returnString);

			assertEquals(userId, returnObj.getInt("id"));
			assertEquals("SystemTestUserReadRESTRICTED", returnObj.getString("username"));
			assertEquals("1234", returnObj.getString("password"));
			assertEquals("OFFLINE", returnObj.getString("userStatus"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}





	@Test
	public void UpdateUserTest() {
		// create this test user in the DB
		User testUser = new User("SystemTestUserUpdateRESTRICTED", "1234");
		userRepo.save(testUser);
		userId = testUser.getId();

		Response response = RestAssured.given().
				header("Content-Type", "application/json").
				header("charset","utf-8").
				body("{" +
						"\"username\"" + " : " + "\"SystemTestUserUpdateRESTRICTEDUpdated\"," +
						"\"password\"" + " : " + "\"5678\"," +
						"\"userStatus\"" + " : " + "\"ONLINE\"" +
						"}").
				when().
				put("/users/" + userId);


		// Check status code
		int statusCode = response.getStatusCode();
		assertEquals(200, statusCode);

		String returnString = response.getBody().asString();
		try {
			JSONObject returnObj = new JSONObject(returnString);

			assertEquals(userId, returnObj.getInt("id"));
			assertEquals("SystemTestUserUpdateRESTRICTEDUpdated", returnObj.getString("username"));
			assertEquals("5678", returnObj.getString("password"));
			assertEquals("ONLINE", returnObj.getString("userStatus"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	@Test
	public void deleteUserTest() {
		// create this test user in the DB
		User testUser = new User("SystemTestUserDeleteRESTRICTED", "1234");
		userRepo.save(testUser);
		userId = testUser.getId();

		// actual delete test
		Response response = RestAssured.given().
				when().
				delete("/users/" + userId);

		// Check status code
		int statusCode = response.getStatusCode();
		assertEquals(200, statusCode);

		// Check response body for correct response
		String returnString = response.getBody().asString();
		try {

			JSONObject returnObj = new JSONObject(returnString);
			assertEquals("success", returnObj.getString("message"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// runs after each test to clean it up
	@After
	public void cleanUp() {
		if (userId != 0) {
			try {
				// check if user exists before trying to delete to avoid errors
				if (userRepo.existsById(userId)) {
					userRepo.deleteById(userId);
				}
			} catch (Exception e) {
				System.out.println("Test cleanUp() failed for userId " + userId + ": " + e.getMessage());
			} finally {
				userId = 0;
			}
		}
	}



}