/**
 * @author Mack Quinn
 */

package com.example.pocketdeck;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log; // for debug msg
import android.widget.Toast; // popup msg
import com.android.volley.Request; //volley request from backend
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue; // create a request queue
import com.android.volley.toolbox.StringRequest; //Volley that returns string
import java.util.HashMap; // to store username and password
import java.util.Map; // to push to UI

import com.google.android.material.internal.EdgeToEdgeUtils;

public class VolleyCommand {

    //Feat: feature
    //Fix: bug
    //Refactor:
    //Build:
    /*
    Creates one request queue to be used throughout the project
    and anywhere within it.
     */
    private static VolleyCommand instance; //create one queue so stuff doesn't get lost

    private RequestQueue requestQueue; //create the request queue

    /**
     *
     * @param context
     */
    private VolleyCommand(Context context) {
        // context is the info about the app while its running
        // this creates the request queue with access to that information
        // it is a private constructor so that it cant be created more than once
        // because we only want one queue to be memory efficient
       requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    // this returns tha instance we created above
    // if it doesnt exist than it will create one

    /**
     *
     * @param context
     * @return
     */
    public static synchronized VolleyCommand getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyCommand(context);
        }
        return instance;
    }

    // adds a request to the queue

    /**
     *
     * @param request
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> request) {
        requestQueue.add(request);
    }
}
