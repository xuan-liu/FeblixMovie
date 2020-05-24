package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends Activity {

    private EditText movieTitle;
    private TextView message;
    private Button searchButton;
    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // upon creation, inflate and initialize the layout
        setContentView(R.layout.search);

        movieTitle = findViewById(R.id.movietitle);
        message = findViewById(R.id.message);
        searchButton = findViewById(R.id.search);

        url = "https:/10.0.2.2:8443/Project4_war/api/";

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search();
            }
        });
    }



    public void Search() {

        message.setText("Searching...");
        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        //request type is POST
        Log.d("movietitel",url + "result?title=" + movieTitle.getText().toString() + "&limit=10&offset=0&order=r_desc_t_asc");
        final StringRequest loginRequest = new StringRequest(Request.Method.GET, url + "result?title=" + movieTitle.getText().toString() + "&limit=10&offset=0&order=r_desc_t_asc", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                Log.d("response:",response);

                JsonObject responseJsonObject = JsonParser.parseString(response).getAsJsonObject();




            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("login.error", error.toString());
                    }
                }) {

        };

        // !important: queue.add is where the login request is actually sent
        queue.add(loginRequest);

    }
}

