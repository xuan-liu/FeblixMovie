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

        url = "https://ec2-18-212-1-122.compute-1.amazonaws.com:8443/Project4/api/";

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
        //request type is GET
        Log.d("movietitle",url + "result?title=" + movieTitle.getText().toString() + "&limit=20&offset=0&order=r_desc_t_asc");
        final StringRequest searchRequest = new StringRequest(Request.Method.GET, url + "result?title=" + movieTitle.getText().toString() + "&limit=20&offset=0&order=r_desc_t_asc", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                message.setText("");
                Log.d("response:",response);

                //initialize the activity(page)/destination
                Intent listPage = new Intent(SearchActivity.this, ListViewActivity.class);
                listPage.putExtra("data", response);
                listPage.putExtra("offset", 0);
                listPage.putExtra("search_movie", movieTitle.getText().toString());

                //without starting the activity/page, nothing would happen
                startActivity(listPage);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("search.error", error.toString());
                    }
                }) {

        };

        // !important: queue.add is where the login request is actually sent
        queue.add(searchRequest);

    }
}

