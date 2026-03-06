package com.example.pocketdeck;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

public class AccountSettings extends AppCompatActivity {

    // URLs
    private static final String URL_USER_DELETE = "http:///10.0.2.2:3001/users/";

    // Text Input and Display
    private EditText usernameInput;
    private EditText currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private TextView userIdLabel;

    // Buttons
    private Button confirmButton, backButton;
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
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { logOutUser(); }
        });

        Button deleteButton = findViewById(R.id.deleteUserButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { deleteUserAccount(); }
        });

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

    private void logOutUser() {
        // Pop up to confirm
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to log out?");
        builder.setCancelable(false);

        builder.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Clear properties
                SharedPreferences preferences = getSharedPreferences("userLoggedInCheck", MODE_PRIVATE);

                preferences.edit().putBoolean("isLoggedIn", false).apply();
                preferences.edit().remove("userID").apply();
                preferences.edit().remove("username").apply();
                preferences.edit().remove("status").apply();

                // Clear dialog
                dialog.dismiss();

                // Send to Login
                Intent i = new Intent(AccountSettings.this, LoginActivity.class);
                startActivity(i);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Clear dialog
                dialog.cancel();
            }
        });

        AlertDialog logoutAlert = builder.create();
        logoutAlert.show();
    }

    private void deleteUserAccount() {
        // Pop up to confirm
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountSettings.this);
        builder.setTitle("Are you sure you want to delete your account?");
        builder.setMessage("Any data from your account CANNOT be recovered afterwards.");
        builder.setCancelable(false);

        // Confirm button
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Confirmed deletion.
                // Send request to delete user to server
                SharedPreferences preferences = getSharedPreferences("userLoggedInCheck", MODE_PRIVATE);
                int userId = preferences.getInt("userID", -1);
                if (userId == -1) {
                    Toast.makeText(getApplicationContext(), "Couldn't retrieve userId.", Toast.LENGTH_SHORT).show();
                }

                JsonObjectRequest deleteRequest;

                // Clear dialog
                dialog.dismiss();

                deleteRequest = new JsonObjectRequest(
                        Request.Method.DELETE,
                        URL_USER_DELETE + Integer.toString(userId),
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Deleted
                                try {
                                    if (response.getString("message").equals("success")) {
                                        Toast.makeText(getApplicationContext(), "User deleted successfully", Toast.LENGTH_LONG).show();
                                        // Clear properties
                                        // TODO: ADD CALL TO USER_UTILITIES.
                                        SharedPreferences preferences = getSharedPreferences("userLoggedInCheck", MODE_PRIVATE);

                                        preferences.edit().putBoolean("isLoggedIn", false).apply();
                                        preferences.edit().remove("userID").apply();
                                        preferences.edit().remove("username").apply();
                                        preferences.edit().remove("status").apply();

                                        // Send user to Sign-up
                                        Intent i = new Intent(AccountSettings.this, SignupActivity.class);
                                        startActivity(i);
                                    }
                                }
                                catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Server response invalid.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Error in deletion: " + error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                );
            }
        });

        // Cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog deletionAlert = builder.create();
        deletionAlert.show();
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