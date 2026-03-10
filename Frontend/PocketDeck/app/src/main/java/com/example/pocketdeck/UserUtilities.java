package com.example.pocketdeck;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Raine McKellar and Mack Quinn
 */

public class UserUtilities {
    private final SharedPreferences preferences;
    private final Context contextReference;

    public UserUtilities(Context ref) {
        this.preferences = ref.getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        this.contextReference = ref;
    }

    /**
     * Applies the data in the user object to the saved preferences on the device.
     *
     * @param user The user object to apply to the shared preferences
     * @throws JSONException User object is missing a necessary parameter.
     */
    public void applyUserObject(JSONObject user) throws JSONException {
        // Unpack user object.
        int userID = user.getInt("id");
        String uname = user.getString("username");
        String status = user.getString("userStatus");

        applyUserObject(uname, status, userID);
    }

    public void applyUserObject(String uname, String status, int userID) {
        preferences.edit().putBoolean("isLoggedIn", true).apply();
        preferences.edit().putInt("userID", userID).apply();
        preferences.edit().putString("username", uname).apply();
        preferences.edit().putString("status", status).apply();
    }

    public void applyUsername(String uname) {
        int userID = getSavedId();
        String status = getSavedStatus();

        applyUserObject(uname, status, userID);
    }

    /**
     * Packs username and password into a HashMap object.
     *
     * @param username Supplied username
     * @param password Supplied password
     * @return username and password packed into HashMap
     */
    public Map<String, String> getUserMap(String username, String password) {
        Map<String, String> params = new HashMap<String, String>();

        params.put("username", username);
        params.put("password", password);

        return params;
    }

    /**
     * Clears the local saved preferences and marks the user as "logged-out"
     */
    public void logoutUser() {
        // Fetch preference object

        // Overrides and erases user data.
        preferences.edit().putBoolean("isLoggedIn", false).apply();
        preferences.edit().remove("userID").apply();
        preferences.edit().remove("username").apply();
        preferences.edit().remove("status").apply();
    }

    /**
     * Gets the saved Id in the preferences.
     * @return Saved user ID
     */
    public int getSavedId() {
        return preferences.getInt("userID", -1);
    }

    /**
     * Gets the saved username in the preferences
     * @return Saved username
     */
    public String getSavedUsername() {
        return preferences.getString("username", "ERR_INVALID_REQUEST");
    }

    public String getSavedStatus() {
        return preferences.getString("status", "INVALID_STATUS");
    }
}
