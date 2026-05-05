package com.example.pocketdeck;

import android.content.Context;

public class Requester {
    private static final String URL_REQUESTS = "http://coms-3090-025.class.las.iastate.edu:8080/request";

    public void sendRequest(long userId, Context context) {
        UserUtilities userUtils = new UserUtilities(context);
        String requestPath = URL_REQUESTS;
    }
}
