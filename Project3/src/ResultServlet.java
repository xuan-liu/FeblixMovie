import com.google.gson.Gson;
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
import java.util.HashMap;
import java.util.Map;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "ResultServlet", urlPatterns = "/api/result")
public class ResultServlet extends HttpServlet {
//    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        //Obtain and checks if the parameters exists in the url
        String title_param = request.getParameter("title");
        String year_param = request.getParameter("year");
        String director_param = request.getParameter("director");
        String star_param = request.getParameter("star");
        String limit_param = request.getParameter("limit");
        String offset_param = request.getParameter("offset");
        String category_param = request.getParameter("category");
        String genre_param = request.getParameter("genre");
        String order_param = request.getParameter("order");


//        String orderSection = "r.rating desc";
        String orderSection = "";
        if (order_param.equals("t_asc_r_asc")){
            //"t_asc_r_asc" = title ascending order, rating ascending order
            orderSection = "m.title asc, r.rating asc";
        }
        else if (order_param.equals("t_asc_r_desc")){
            //title ascending order, rating descending order
            orderSection = "m.title asc, r.rating desc";
        }
        else if (order_param.equals("t_desc_r_asc")){
            //title descending order, rating ascending order
            orderSection = "m.title desc, r.rating asc";
        }
        else if (order_param.equals("t_desc_r_desc")){
            //title descending order, rating descending order
            orderSection = "m.title desc, r.rating desc";
        }
        else if (order_param.equals("r_asc_t_asc")){
            //rating ascending order, title ascending order
            orderSection = "r.rating asc, m.title asc";
        }
        else if (order_param.equals("r_asc_t_desc")){
            //rating ascending order, title descending order
            orderSection = "r.rating asc, m.title desc";
        }
        else if (order_param.equals("r_desc_t_asc")){
            //rating descending order, title ascending order
            orderSection = "r.rating desc, m.title asc";
        }
        else if (order_param.equals("r_desc_t_desc")){
            //rating descending order, title ascending order
            orderSection = "r.rating desc, m.title desc";
        }




        String whereSection = "";


        String queryBegin = "SELECT distinct m.id, m.title, m.year, m.director, r.rating from movies as m, ratings as r";

        if (category_param != null){
            if (!category_param.equals("*")){
                whereSection += " and m.title like '" + category_param + "%'";
            }
            else{
                whereSection += " and m.title regexp '^[^a-z0-9]'";
            }

        }
        else if(genre_param != null){
            queryBegin += ",genres_in_movies as gm, genres as g";
            whereSection += " and g.name='" + genre_param +"' and gm.movieId = m.id and gm.genreId = g.id";
        }
        else{
            if (star_param != ""){
                queryBegin += ",stars as s, stars_in_movies as sm";
                whereSection += " and m.id = sm.movieId and s.id = sm.starId and s.name like '%" + star_param + "%'";
            }
            if (director_param != ""){
                whereSection += " and m.director like '%" + director_param + "%'";
            }
            if (year_param != ""){
                whereSection += " and m.year = " + year_param;
            }
            if (title_param != "") {
                whereSection += " and m.title like '%" + title_param + "%'";
            }
        }




        String queryTail = " where m.id = r.movieId" + whereSection + " order by "+ orderSection;

        if (limit_param == null || limit_param == ""){
            queryTail += " limit 30 ";
        }
        else{
            queryTail += " limit " + limit_param + " ";
        }

        if (offset_param == null || offset_param ==""){
            queryTail += "offset 0";
        }
        else{
            queryTail += "offset " + offset_param;
        }



        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();

            String query = queryBegin + queryTail;

            log(query);
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
                JsonArray genreJsonArray = new JsonArray();
                String genre_query = "SELECT * from genres_in_movies as gm, genres as g where gm.movieId = ? and gm.genreId = g.id order by g.name asc limit 3";
                PreparedStatement genre_statement = dbcon.prepareStatement(genre_query);
                genre_statement.setString(1, movieId);
                ResultSet genre_rs = genre_statement.executeQuery();
//                String genres = "";
                String genre;
//                int count = 0;
                while (genre_rs.next()){
//                    if (count != 0){
//                        genres = genres + ", ";
//                    }
                    genreJsonArray.add(genre_rs.getString("name"));
//                    count++;
                }
                genre_rs.close();
                genre_statement.close();

                //Create and execute the star query statement
                JsonArray starJsonArray = new JsonArray();
                String star_query = "Select s.name, sm.starId, count(sm.movieId) " +
                        "From stars as s, stars_in_movies as sm " +
                        "Where sm.starId = s.id and s.id in (Select starId From stars_in_movies Where movieId = ?) " +
                        "Group by s.name, sm.starId " +
                        "Order by count(sm.movieId) desc, s.name asc " +
                        "Limit 3";
//                String star_query = "SELECT * from stars as s, stars_in_movies as sm where sm.movieId = ? and sm.starId = s.id";
                System.out.println(star_query);
                PreparedStatement star_statement = dbcon.prepareStatement(star_query);
                star_statement.setString(1, movieId);
                ResultSet star_rs = star_statement.executeQuery();
//                String stars = "";

                Map<String, String> starMap = new HashMap<String, String>();
                while (star_rs.next()){
//                    if (count != 0){
//                        stars = stars + ", ";
//                    }
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


                String rating = rs.getString("rating");



                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movieId", movieId);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
//                jsonObject.addProperty("genres", genres);
                jsonObject.add("genres", genreJsonArray);
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