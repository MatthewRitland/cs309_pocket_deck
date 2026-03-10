package com.example.pocketdeck;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AccountSettings extends AppCompatActivity {

    // URLs
    private static final String URL_USER_PATH = "http://coms-3090-025.class.las.iastate.edu:8080/users/";

    // Text Input and Display
    private EditText usernameInput;
    private EditText currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private TextView userIdLabel;

    private UserUtilities userUtils;

    // Buttons
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

        userUtils = new UserUtilities(AccountSettings.this);

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
        Button confirmButton = findViewById(R.id.confirmAccountSettingsButton);
        Button backButton = findViewById(R.id.discardAccountSettingsButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmChoices();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDisplay();
                Intent i = new Intent(AccountSettings.this, MainActivity.class);
                startActivity(i);
            }
        });

        updateDisplay();
    }

    String currentUsername = "";
    int userId = -1;
    int currentPortrait = 0;

    /**
     * Updates the components of the page to represent current user settings.
     */
    private void updateDisplay() {
        // Update all the components to represent present user settings.
        currentUsername = userUtils.getSavedUsername();
        userId = userUtils.getSavedId();

        usernameInput.setText(currentUsername);
        userIdLabel.setText(Integer.toString(userId));

        currentPasswordInput.setText("");
        newPasswordInput.setText("");
        confirmPasswordInput.setText("");
        // Change picture selection
    }

    private void confirmChoices() {
        // Do checks

        JsonObjectRequest userRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_USER_PATH + Integer.toString(userId),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // On retrieval
                        try {
                            String updatedUsername = usernameInput.getText().toString();
                            String updatedPassword = response.getString("password");

                            String currentPassword = currentPasswordInput.getText().toString().trim();
                            String newPassword = newPasswordInput.getText().toString().trim();
                            String confirmPassword = confirmPasswordInput.getText().toString().trim();
                            //InputResult validInput = validPasswordInput(currentPassword, newPassword, confirmPassword);

                            // Password Checks
                            if (!currentPassword.isEmpty() || !newPassword.isEmpty()) {
                                if (!newPassword.equals(confirmPassword)) {
                                    Toast.makeText(getApplicationContext(), "The new password and confirmation password must match.", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    if (currentPassword.equals(updatedPassword)) {
                                        updatedPassword = newPassword;
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Password is incorrect.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            }

                            if (updatedUsername.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "New username cannot be blank.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            HashMap<String, String> final_user = new HashMap<String, String>();
                            final_user.put("username", updatedUsername);
                            final_user.put("password", updatedPassword);
                            JSONObject jsonUpdatedUser = new JSONObject(final_user);
                            sendUpdatedUser(jsonUpdatedUser);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "JSON parsing error: " + e.toString(), Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // On failed
                        Toast.makeText(getApplicationContext(), "Error in fetching user data: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleyCommand.getInstance(AccountSettings.this).addToRequestQueue(userRequest);
    }

    private void sendUpdatedUser(JSONObject userObject) {
        JsonObjectRequest userUpdate = new JsonObjectRequest(
                Request.Method.PUT,
                URL_USER_PATH + Integer.toString(userId),
                userObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Changes have been sent over", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(AccountSettings.this, MainActivity.class);
                        startActivity(i);
                        try {
                            String uname = userObject.getString("username");
                            userUtils.applyUsername(uname);
                        }
                        catch(Exception e) { }
                        updateDisplay();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Issue sending updated data over. " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleyCommand.getInstance(AccountSettings.this).addToRequestQueue(userUpdate);
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
                userUtils.logoutUser();

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
                if (userId == -1) {
                    Toast.makeText(getApplicationContext(), "Couldn't retrieve userId.", Toast.LENGTH_SHORT).show();
                    return;
                }

                JsonObjectRequest deleteRequest;

                deleteRequest = new JsonObjectRequest(
                        Request.Method.DELETE,
                        URL_USER_PATH + Integer.toString(userId),
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Deleted
                                try {
                                    if (response.getString("message").equals("success")) {
                                        Toast.makeText(getApplicationContext(), "User deleted successfully", Toast.LENGTH_LONG).show();
                                        // Clear properties

                                        UserUtilities userUtils = new UserUtilities(AccountSettings.this);
                                        userUtils.logoutUser();

                                        // Send user to Sign-up
                                        Intent i = new Intent(AccountSettings.this, SignupActivity.class);
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "INVALID RESPONSE " + response.getString("message"), Toast.LENGTH_LONG).show();
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
                VolleyCommand.getInstance(AccountSettings.this).addToRequestQueue(deleteRequest);

                // Clear dialog
                dialog.dismiss();
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
    @Override
    protected void onResume() {
        super.onResume();
        MusicPlayer.musicPref(this);
    }
}