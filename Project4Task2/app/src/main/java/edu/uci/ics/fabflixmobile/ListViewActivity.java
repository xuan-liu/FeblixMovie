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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends Activity {
    ArrayList<Movie> movies = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // show list view page
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);

        // data is passed from SearchActivity, which is retrieved from the database and the backend server
        String data = this.getIntent().getStringExtra("data");
        JsonArray responseJsonArray = JsonParser.parseString(data).getAsJsonArray();

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

            movies.add(new Movie(e.get("title").getAsString(), e.get("year").getAsString(), e.get("director").getAsString(), genres, stars));

        }

        MovieListViewAdapter adapter = new MovieListViewAdapter(movies, this);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
                String message = String.format("Clicked on position: %d, name: %s, %s", position, movie.getName(), movie.getYear());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }
}