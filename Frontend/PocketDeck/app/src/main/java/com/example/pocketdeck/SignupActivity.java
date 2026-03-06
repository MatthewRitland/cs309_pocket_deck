package com.example.pocketdeck;

/**
 * Class for handling the logic of the Signup screen.
 * @author Raine McKellar with some code snippets by Mack Quinn
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity{

    private EditText nameInput, passwordInput, confirmPasswordInput;

    // HTTP request URLs
    //private static final String URL_USER_CREATE = "http:///10.0.2.2:3001/signup"; // Temp URL, Mockoon
    // Server HTTP URL for Signup.
    private static final String URL_USER_CREATE = "http://coms-3090-025.class.las.iastate.edu:8080/signup";

    @Override
    protected void onCreate(Bundle savedInstancesState)
    {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_signup);

        // Get text inputs
        nameInput = findViewById(R.id.nameInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);

        // Get Buttons

        // Confirmation
        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { try_create_user(); }
        });

        // Link to login page
        Button loginLinkButton = findViewById(R.id.loginLinkButton);
        loginLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Reads user inputs from the fields and checks for any faults. If none are found, it will
     * send over the username and confirmed password over to the server and handle the response.
     */
    private void try_create_user() {
        // Get user input and trim down
        String username = nameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirm = confirmPasswordInput.getText().toString().trim();

        // Set inputs to newly trimmed versions (for better readability)
        nameInput.setText(username);
        passwordInput.setText(password);
        confirmPasswordInput.setText(confirm);

        // Check for any empty fields.
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            // An input is empty
            Toast.makeText(getApplicationContext(), "Please fill out all input fields", Toast.LENGTH_LONG).show();
            return;
        }

        // Check for valid password confirmation
        if (!password.equals(confirm)) {
            // Inform user of invalid confirmation input (toast)
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            // All previous checks passed, attempt to create user on server.
            create_user_request(username, password);
        }
    }

    /**
     * Sends the username and password over to the server and handle response
     * @param username Desired username for the new user, will be sent over exactly as is.
     * @param password Desired password for the new user, will be sent over exactly as is.
     */
    private void create_user_request(String username, String password) {
        // Send request to create user
        JsonObjectRequest create_user_request = new JsonObjectRequest(
                Request.Method.POST,
                URL_USER_CREATE,
                new JSONObject(getUserMap(username, password)),
                new Response.Listener<JSONObject>() {
                    // Handler for server response to username and password request.
                    @Override
                    public void onResponse(JSONObject response) {
                        // Try-catch for json exceptions from reading response.
                        try {
                            // Fetch response message
                            //String responseMessage = response.getString("message");
                            // Check for success message (May need rework later)

                            // Successful creation (print toast first to ensure user is aware of creation)
                            Toast.makeText(getApplicationContext(), "User signup successful", Toast.LENGTH_SHORT).show();

                            // Attempt to fetch user_id from response

                            //if (response.has("user")) {
                                //JSONObject user_object = response.getJSONObject("user");
                            ApplyUserObject(response);

                            // Switch screen over to main.
                            Intent i = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(i);

                        } catch (JSONException jsonException) {
                            // Respond to JSONException. Currently just a generic message.
                            Toast.makeText(getApplicationContext(), "Server response invalid. " + jsonException.toString(), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Failed to create user account (Error in access or volley)
                        Toast.makeText(getApplicationContext(), "Response error: " + volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        // Volley command.
        VolleyCommand.getInstance(this).addToRequestQueue(create_user_request);
    }

    /**
     * Encodes the username and password for the sign-in API in a Map.
     * @param username The desired username
     * @param password The desired password
     * @return HashMap of the username and password.
     */
    private Map<String, String> getUserMap(String username, String password) {
        Map<String, String> params = new HashMap<String, String>();

        // TODO: check user conversion.
        params.put("username", username);
        params.put("password", password);

        return params;
    }

    /**
     * Code to apply the shared preferences for signing in with a user object, most
     * code has been copied over from Mack's implementation in the LoginActivity.
     * @param user The user JSON object, formatted as returned by the server.
     * @throws JSONException Thrown when a parameter expected is missing from the user object.
     */
    private void ApplyUserObject(JSONObject user) throws JSONException{
        int userID = user.getInt("id");
        String uname = user.getString("username");
        String status = user.getString("userStatus");

        SharedPreferences preferences = getSharedPreferences("userLoggedInCheck", MODE_PRIVATE);

        preferences.edit().putBoolean("isLoggedIn", true).apply();
        preferences.edit().putInt("userID", userID).apply();
        preferences.edit().putString("username", uname).apply();
        preferences.edit().putString("status", status).apply();
    }
}
