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
import java.util.HashMap;
import java.util.Map;


@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 5L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Construct a query with parameter represented by "?"
            String query = "select * from movies as m, ratings as r where r.movieId = m.id and m.id = ?";


            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                String movieId = rs.getString("movieId");
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");
                String rating = rs.getString("rating");

                JsonArray genreJsonArray = new JsonArray();
                String genre_query = "SELECT * from genres_in_movies as gm, genres as g where gm.movieId = ? and gm.genreId = g.id";
                PreparedStatement genre_statement = dbcon.prepareStatement(genre_query);
                genre_statement.setString(1, movieId);
                ResultSet genre_rs = genre_statement.executeQuery();

                while (genre_rs.next()){
                    genreJsonArray.add(genre_rs.getString("name"));

                }
                genre_rs.close();
                genre_statement.close();

                JsonArray starJsonArray = new JsonArray();
                String star_query = "Select s.name, sm.starId, count(sm.movieId) " +
                        "From stars as s, stars_in_movies as sm " +
                        "Where sm.starId = s.id and s.id in (Select starId From stars_in_movies Where movieId = ?) " +
                        "Group by s.name, sm.starId " +
                        "Order by count(sm.movieId) desc";
                System.out.println(star_query);
                PreparedStatement star_statement = dbcon.prepareStatement(star_query);
                star_statement.setString(1, movieId);
                ResultSet star_rs = star_statement.executeQuery();
                String stars = "";

                Map<String, String> starMap = new HashMap<String, String>();
                while (star_rs.next()){


                    String starName = star_rs.getString("name");
                    String starId = star_rs.getString("starId");
                    JsonObject starJsonObject = new JsonObject();
                    starJsonObject.addProperty("starName", starName);
                    starJsonObject.addProperty("starId", starId);
                    starJsonArray.add(starJsonObject);
                    starMap.put(starName, starId);
                }
                star_rs.close();
                star_statement.close();

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("movieId", movieId);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.add("genres",genreJsonArray);
                jsonObject.add("starInfo", starJsonArray);
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
