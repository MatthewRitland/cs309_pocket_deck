package onetoone;

import static org.junit.jupiter.api.Assertions.assertEquals;

import onetoone.Users.UserRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
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
			assertEquals(userRepo.findByUsername("SystemTestUserRESTRICTED").getId(), returnObj.getInt("id"));
			assertEquals("SystemTestUserRESTRICTED", returnObj.getString("username"));
			assertEquals("1234", returnObj.getString("password"));
			assertEquals("OFFLINE", returnObj.getString("userStatus"));

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}





	// make sure this is reached if others aren't working!
	@Test
	public void deleteUserTest() {
		Response response = RestAssured.given().
				//header("Content-Type", "application/json"). // don't need this, not sending a body
				//header("charset","utf-8").
				when().
				delete("/users/" + userRepo.findByUsername("SystemTestUserRESTRICTED").getId());


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



}