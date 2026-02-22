package com.example.pocketdeck;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import com.google.android.material.internal.EdgeToEdgeUtils;

public class LoginActivity extends AppCompatActivity {

    //xml references
    private EditText usernameText;
    private EditText passwordText;
    private Button loginButton;
    private Button signupButton;

    private static final String URL_STRING_REQ = "http://10.0.2.2:3000/users/1";
    // Alternative URLs for testing purposes
    // public static final String URL_STRING_REQ = "https://2aa87adf-ff7c-45c8-89bc-f3fbfaa16d15.mock.pstmn.io/users/1";
    // public static final String URL_STRING_REQ = "http://10.0.2.2:8080/users/1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameText = findViewById(R.id.login_username_txt);
        passwordText = findViewById(R.id.login_password_txt);
        loginButton = findViewById(R.id.login_signup_button);
        signupButton = findViewById(R.id.login_signup_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // attemptLogin();
                // call to activity to go to try to log in
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
    }

    private void attemptLogin() {
        //method to make sure something is in the field
        //and validate the request

        return;
    }

    private void loginRequest () {
        //communicate with the backend
        //and start the rest of the app
        return;
    }
}