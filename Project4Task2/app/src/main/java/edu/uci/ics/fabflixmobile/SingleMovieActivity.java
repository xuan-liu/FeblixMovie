package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class SingleMovieActivity extends Activity {
    private TextView titleView;
    private TextView yearView;
    private TextView directorView;
    private TextView genresView;
    private TextView starsView;
    private Button backListButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // upon creation, inflate and initialize the layout
        setContentView(R.layout.singlemovie);
        titleView = findViewById(R.id.movieTitle);
        yearView = findViewById(R.id.movieYear);
        directorView = findViewById(R.id.movieDirector);
        genresView = findViewById(R.id.movieGenres);
        starsView = findViewById(R.id.movieStars);
        backListButton = findViewById(R.id.backList);

        // get single movie data
        String data = this.getIntent().getStringExtra("data");
        Log.d("data: ", data);
        JsonObject responseJsonObj = JsonParser.parseString(data).getAsJsonArray().get(0).getAsJsonObject();

        List<String> genres = new ArrayList<>();
        List<String> stars = new ArrayList<>();

        // load genres
        JsonArray gen = responseJsonObj.get("genres").getAsJsonArray();
        for (int j = 0; j < gen.size(); j++) {
            genres.add(gen.get(j).getAsString());
        }

        // load stars
        JsonArray st = responseJsonObj.get("starInfo").getAsJsonArray();
        for (int k = 0; k < st.size(); k++) {
            Log.d("starName: ", st.get(k).getAsJsonObject().get("starName").getAsString());
            stars.add(st.get(k).getAsJsonObject().get("starName").getAsString());
        }

        // set movie details
        titleView.setText(responseJsonObj.get("title").getAsString());
        yearView.setText("Year: " + responseJsonObj.get("year").getAsString());
        directorView.setText("Director: " + responseJsonObj.get("director").getAsString());
        genresView.setText("Genres: " + genres.toString());
        starsView.setText("Stars: " + stars.toString());

        backListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}

