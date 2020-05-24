package edu.uci.ics.fabflixmobile;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    private String name;
    private String year;
    private String director;
    private List<String> genres = new ArrayList<>();
    private List<String> stars = new ArrayList<>();

    public Movie(String name, String year, String director, List<String> genres, List<String> stars) {
        this.name = name;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public String getDirector() {return director;}

    public String getGenres() {return genres.toString();}

    public String getStars() {return stars.toString();}
}