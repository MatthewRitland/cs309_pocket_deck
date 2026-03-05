package com.example.pocketdeck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONObject;
import org.json.JSONException;
import com.android.volley.toolbox.JsonObjectRequest;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log; // for debug msg
import android.widget.Toast; // popup msg
import com.android.volley.Request; //volley request from backend
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue; // create a request queue
import com.android.volley.toolbox.StringRequest; //Volley that returns string
import java.util.HashMap; // to store username and password
import java.util.Map; // to push to UI

import com.google.android.material.internal.EdgeToEdgeUtils;

public class LoginActivity extends AppCompatActivity {

    //xml references
    private EditText usernameText;
    private EditText passwordText;
    private Button loginButton;
    private Button signupButton;

    private Button backButton;

    // private static final String URL_STRING_REQ = "http://10.0.2.2:3000/login"; // for macoon
    //private static final String URL_STRING_REQ = "http://coms-3090-025.class.las.iastate.edu:8080/login"; // for backend
    //private static final String URL_USER_ID = "http://coms-3090-025.class.las.iastate.edu:8080/user/{username}";

    // Alternative URLs for testing purposes
    // public static final String URL_STRING_REQ = "https://2aa87adf-ff7c-45c8-89bc-f3fbfaa16d15.mock.pstmn.io/users/1";
    public static final String URL_STRING_REQ = "http://10.0.2.2:8080/users/1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity); //xml layout sheet ref
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameText = findViewById(R.id.login_username_txt);
        passwordText = findViewById(R.id.login_password_txt);
        loginButton = findViewById(R.id.login_login_button);
        signupButton = findViewById(R.id.login_signup_button);
        backButton = findViewById(R.id.login_back_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginRequest(); // call to activity to go to try to log in
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
    //to send login request to the backend
    private void loginRequest () {

        // these get the text entered by the user
        final String username = usernameText.getText().toString().trim();
        final String password = passwordText.getText().toString().trim();

        // make sure theres something entered
        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(LoginActivity.this, "Enter Username and Password!", Toast.LENGTH_SHORT).show();
            return;
        }

        // upon succesful login message and activity start
        StringRequest request = new StringRequest(Request.Method.POST, URL_STRING_REQ, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Success", response); //log for debugging

                        //NEW ADDED FOR JSON
                        try {
                            //create the object and read the value returned
                            JSONObject json = new JSONObject(response);

                            String message = json.optString("message");
                            boolean loginSuccess = json.optBoolean("success");

                            //this happens if the login success is true
                            //EDIT: crate a flag using SharedPreferences to save login data even when the app is closed
                            if(loginSuccess){
                                //Create json object to store all user info in the shared preferences
                                JSONObject userInfo = json.getJSONObject("user");

                                int userID = userInfo.getInt("id");
                                String uname = userInfo.getString("username");
                                String status = userInfo.getString("userStatus");
                                //create a shared preference that saves data to "userLoggedIn". MODE_Private means that
                                //only the app can use this.
                                //Look at this for more info. https://www.geeksforgeeks.org/android/shared-preferences-in-android-with-examples/
                                //I used this page and some others to figure this out
                                //Save the flag here when you sign in so that I can use it in main for the play button logic
                                SharedPreferences preferences = getSharedPreferences("userLoggedInCheck", MODE_PRIVATE);
                                preferences.edit().putBoolean("isLoggedIn", true).apply();
                                preferences.edit().putInt("userID", userID).apply();
                                preferences.edit().putString("username", uname).apply();
                                preferences.edit().putString("status", status).apply();

                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                            } else {
                                //if the backend says login failed
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e){
                            //if the backend doesnt send a json this will happen
                            Log.e("JSON_ERROR", e.toString());
                            Toast.makeText(LoginActivity.this, "No Server Response", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                // Log in failure
                // **THIS IS NOT INCORRECT USERNAME AND PASSWORD**
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("LOGIN_ERROR", error.toString()); //error response on login
                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                // Parameters for the data sent to the server
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        // pass this to the VolleyCommand queue
        VolleyCommand.getInstance(this).addToRequestQueue(request);
    }
}