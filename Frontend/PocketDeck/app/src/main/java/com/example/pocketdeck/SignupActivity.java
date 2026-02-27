package com.example.pocketdeck;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
    private Button continueButton, loginLinkButton;

    // HTTP request URLs
    private static final String URL_USER_CREATE = "http:///10.0.2.2:3001/signup"; // Temp Mockoon

    @Override
    protected void onCreate(Bundle savedInstancesState)
    {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_signup);

        // Get text inputs
        nameInput = findViewById(R.id.nameInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);

        // Get Confirm button
        continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { create_user_request(); }
        });

        loginLinkButton = findViewById(R.id.loginLinkButton);
        loginLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void create_user_request() {
        String username = nameInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirm = confirmPasswordInput.getText().toString();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            // An input is empty
            Toast.makeText(getApplicationContext(), "Please fill out all input fields", Toast.LENGTH_LONG).show();
        }

        if (!password.equals(confirm)) {
            // Does not match up, invalid.
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            // Send request to create user
            JsonObjectRequest create_user_request = new JsonObjectRequest(
                    Request.Method.POST, URL_USER_CREATE, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            // Do code here.
                            String responseMessage = "";
                            try {
                                responseMessage = response.getString("message");
                                if (responseMessage.equals("success")) {
                                    // Successful creation
                                    // cry();

                                    // Ensure the local system knows its logged in
                                    String user_id = response.getString("userId");
                                    // current_user_id = user_id; (doesn't exist yet)

                                    Toast.makeText(getApplicationContext(), "User signup successful", Toast.LENGTH_SHORT).show();

                                    Intent i = new Intent(SignupActivity.this, MainActivity.class);
                                    startActivity(i);
                                }
                            } catch (JSONException jsonException) {
                                Toast.makeText(getApplicationContext(), "jsonException error encountered with server response.", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            // Failed to create user account
                            Toast.makeText(getApplicationContext(), "Response error", Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> user_params = new HashMap<>();

                    // TODO: Check if this JSON parameter is converted to a user.
                    user_params.put("userName", username);
                    user_params.put("password", password);

                    return user_params;
                }
            };
            // Volley command.
            VolleyCommand.getInstance(this).addToRequestQueue(create_user_request);
        }
    }
}
