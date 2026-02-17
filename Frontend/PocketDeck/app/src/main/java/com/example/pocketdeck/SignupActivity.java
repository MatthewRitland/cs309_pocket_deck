package com.example.pocketdeck;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
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
                if (!passwordInput.getText().equals(confirmPasswordInput.getText())) {
                    // Does not match up, invalid.
                }
            }
        });
    }
}
