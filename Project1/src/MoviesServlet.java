import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();

            String query = "SELECT * from movies as m, ratings as r where m.id = r.movieid order by r.rating desc limit 20";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movieId = rs.getString("id");
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");

                //Create and execute the genre query statement
                String genre_query = "SELECT * from genres_in_movies as gm, genres as g where gm.movieId = ? and gm.genreId = g.id";
                PreparedStatement genre_statement = dbcon.prepareStatement(genre_query);
                genre_statement.setString(1, movieId);
                ResultSet genre_rs = genre_statement.executeQuery();
                String genres = "";
                int count = 0;
                while (genre_rs.next() && count < 3){
                    if (count != 0){
                        genres = genres + ", ";
                    }
                    genres = genres + genre_rs.getString("name");
                    count++;
                }
                genre_rs.close();
                genre_statement.close();

                //Create and execute the star query statement
                String star_query = "SELECT * from stars as s, stars_in_movies as sm where sm.movieId = ? and sm.starId = s.id";
                PreparedStatement star_statement = dbcon.prepareStatement(star_query);
                star_statement.setString(1, movieId);
                ResultSet star_rs = star_statement.executeQuery();
                String stars = "";
                count = 0;
                while (star_rs.next() && count < 3){
                    if (count != 0){
                        stars = stars + ", ";
                    }
                    stars = stars + star_rs.getString("name");
                    count++;
                }
                star_rs.close();
                star_statement.close();


                String rating = rs.getString("rating");



                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("genres", genres);
                jsonObject.addProperty("stars", stars);
                jsonObject.addProperty("rating", rating);

                jsonArray.add(jsonObject);
            }

            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        out.close();

    }
}
