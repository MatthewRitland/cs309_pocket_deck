package com.example.pocketdeck;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AccountSettings extends AppCompatActivity {

    // Text Input and Display
    private EditText usernameInput;
    private EditText currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private TextView userIdLabel;

    // Buttons
    private Button confirmButton, backButton, logoutButton, deleteButton;
    private CheckBox inputRevealButton, idRevealButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_account_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

            // -- GET INPUTS -- //

        // Username and User-ID
        usernameInput = findViewById(R.id.accSett_username);
        userIdLabel = findViewById(R.id.accSett_userIdLabel);
        idRevealButton = findViewById(R.id.accSett_idReveal);

        // Toggle button functionality
        userIdLabel.setInputType(0x81);
        idRevealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle password visibility
                if (idRevealButton.isChecked()) {
                    userIdLabel.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    userIdLabel.setInputType(0x81);
                }
            }
        });

        // Password buttons and fields
        currentPasswordInput = findViewById(R.id.currentPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmNewPasswordInput);

        // Account logout and deletion
        logoutButton = findViewById(R.id.logoutButton);
        deleteButton = findViewById(R.id.deleteUserButton);

        // Navigation buttons
        confirmButton = findViewById(R.id.confirmAccountSettingsButton);
        backButton = findViewById(R.id.discardAccountSettingsButton);

        updateDisplay();
    }

    /**
     * Updates the components of the page to represent current user settings.
     */
    private void updateDisplay() {
        // Update all the components to represent present user settings.
    }

    private boolean confirmChoices() {
        // Do checks

        // Do communication
        return false;
    }

    enum InputResult {
        VALID,
        INVALID,
        BLANK
    }

    private boolean handlePassword() {
        String currentPassword = currentPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        InputResult validInput = validPasswordInput(currentPassword, newPassword, confirmPassword);

        if (validInput == InputResult.INVALID) {
            // TODO: Invalid password toast
            return false;
        }

        if (validInput == InputResult.VALID) {
            if (currentPassword.equals(newPassword)) {
                // TODO : Toast
                return false;
            }
            // TODO: Send new password request to server
            // TODO: Get user_id
        }

        return true;
    }

    /**
     * Checks the new password fields for whether or not the password should be changed.
     * @return The checks result, either that input is BLANK, INVALID in some way or VALID
     */
    private InputResult validPasswordInput(String currentPassword, String newPassword, String confirmPassword) {
        // All inputs are BLANK, password should not be changed.
        if (currentPassword.isEmpty() && newPassword.isEmpty() && confirmPassword.isEmpty()) {
            return InputResult.BLANK;
        }

        // Any but not all inputs are blank, password changing input is INVALID.
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            return InputResult.INVALID;
        }

        // Confirmation field doesn't match the new password, input INVALID.
        if (!newPassword.equals(confirmPassword)) return InputResult.INVALID;

        // All inputs are (technically) valid.
        return InputResult.VALID;
    }
}