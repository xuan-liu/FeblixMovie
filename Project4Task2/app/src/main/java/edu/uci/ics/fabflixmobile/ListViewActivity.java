package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends Activity {
    ArrayList<Movie> movies = new ArrayList<>();
    private String url = "https://ec2-18-212-1-122.compute-1.amazonaws.com:8443/Project4/api/";
    private Button prevButton;
    private Button nextButton;
    int limit = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // show list view page
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        prevButton = findViewById(R.id.prev);
        nextButton = findViewById(R.id.next);

        // data is passed from SearchActivity, which is retrieved from the database and the backend server
        String data = this.getIntent().getStringExtra("data");
        JsonArray responseJsonArray = JsonParser.parseString(data).getAsJsonArray();
        int offset = this.getIntent().getIntExtra("offset", 0);
        String search_movie = this.getIntent().getStringExtra("search_movie");

        // load all data into array list
        for (int i = 0; i < responseJsonArray.size(); i++) {
            JsonObject e = responseJsonArray.get(i).getAsJsonObject();
            List<String> genres = new ArrayList<>();
            List<String> stars = new ArrayList<>();

            // load genres
            JsonArray gen = e.get("genres").getAsJsonArray();
            for (int j = 0; j < gen.size(); j++) {
                genres.add(gen.get(j).getAsString());
            }

            // load stars
            JsonArray st = e.get("starInfo").getAsJsonArray();
            for (int k = 0; k < st.size(); k++) {
                Log.d("starName: ", st.get(k).getAsJsonObject().get("starName").getAsString());
                stars.add(st.get(k).getAsJsonObject().get("starName").getAsString());
            }

            movies.add(new Movie(e.get("movieId").getAsString(), e.get("title").getAsString(), e.get("year").getAsString(), e.get("director").getAsString(), genres, stars));

        }

        // process prev/next button
        if (offset != 0) {
            // if not the first page, show prev button
            prevButton.setVisibility(View.VISIBLE);
            int newOffset = Math.max(0,offset - limit);
            Log.d("prevButton: ",url + "result?title=" + search_movie + "&limit=20&offset=" + newOffset + "&order=r_desc_t_asc");

            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showNewPage(search_movie, newOffset);
                }
            });

        } else {
            prevButton.setVisibility(View.GONE);
        }

        if(movies.size() == limit){
            // If not the last page, show next button
            nextButton.setVisibility(View.VISIBLE);
            int newOffset = offset + limit;
            Log.d("nextButton: ",url + "result?title=" + search_movie + "&limit=20&offset=" + newOffset + "&order=r_desc_t_asc");

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showNewPage(search_movie, newOffset);
                }
            });
        } else {
            nextButton.setVisibility(View.GONE);
        }

        // show the list of result
        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                String movieId = movie.getId();
                Log.d("movieID:", movie.getId());

                showSingleMovie(movieId);

//                String message = String.format("Clicked on position: %d, name: %s, %s", position, movie.getName(), movie.getYear());
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showSingleMovie(String movieId) {
        Log.d("movieID:", movieId);
        // get single movie data
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest singleRequest = new StringRequest(Request.Method.GET, url + "single-movie?id=" + movieId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response:",response);

                //initialize the activity(page)/destination
                Intent singlePage = new Intent(ListViewActivity.this, SingleMovieActivity.class);
                singlePage.putExtra("data", response);

                //without starting the activity/page, nothing would happen
                startActivity(singlePage);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("singlemovie.error", error.toString());
                    }
                }) {

        };

        // !important: queue.add is where the login request is actually sent
        queue.add(singleRequest);
    }

    public void showNewPage(String search_movie, int newOffset) {
        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        //request type is GET
        String new_url = url + "result?title=" + search_movie + "&limit=20&offset=" + newOffset + "&order=r_desc_t_asc";
        final StringRequest searchRequest = new StringRequest(Request.Method.GET, new_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response:",response);

                //initialize the activity(page)/destination
                Intent listPage = new Intent(ListViewActivity.this, ListViewActivity.class);
                listPage.putExtra("data", response);
                listPage.putExtra("offset", newOffset);
                listPage.putExtra("search_movie", search_movie);

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