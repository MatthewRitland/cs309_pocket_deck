package com.example.pocketdeck;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity{

    private EditText nameInput, passwordInput, confirmPasswordInput;
    private Button continueButton;

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
            public void onClick(View v) {
                String username = nameInput.getText().toString();
                String password = passwordInput.getText().toString();
                String confirm = confirmPasswordInput.getText().toString();

                if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                    // An input is empty
                    Toast.makeText(getApplicationContext(), "Please fill out all input fields", Toast.LENGTH_LONG).show();
                }

                if (!password.equals(confirm)) {
                    // Does not match up, invalid.
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                } else {
                    // Check if user with username exists
                    // TEMP: Add call to check username
                    boolean user_exists = false;
                    if (user_exists) {
                        Toast.makeText(getApplicationContext(), "A user with that username already exists", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Passwords match and username is unique
                    // Make call to create user.

                }
            }
        });
    }
}
