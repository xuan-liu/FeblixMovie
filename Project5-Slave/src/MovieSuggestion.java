
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

// server endpoint URL
@WebServlet("/movie-suggestion")
public class MovieSuggestion extends HttpServlet {

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;

    //Default Constructor
    public MovieSuggestion() {
        super();
    }

    /*
     *
     * Match the query against superheroes and return a JSON response.
     *
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "heroID": 101 } },
     * 	{ "value": "Supergirl", "data": { "heroID": 113 } }
     * ]
     *
     * The format is like this because it can be directly used by the
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     *
     *
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        response.setContentType("application/json");


        String query = request.getParameter("query");

        JsonArray jsonArray = new JsonArray();

        if (query == null || query.trim().isEmpty()) {
            response.getWriter().write(jsonArray.toString());
            return;
        }

        HashMap<Integer, String> string_parameters = new HashMap<Integer, String>();

        String statment = " SELECT * FROM movies WHERE match(title) against (";

        int index = 0;
        for (String s : query.split(" +")){
            statment += "?";
            string_parameters.put(++index, "+" + s + "*");
        }


        statment += " IN BOOLEAN MODE) limit 10";

        try {

//            Connection dbcon = dataSource.getConnection();
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbcon = ds.getConnection();

            // setup the response json arrray



            PreparedStatement prepared_statement = dbcon.prepareStatement(statment);

            for (int i=0; i < index; i++){
                prepared_statement.setString(i+1, string_parameters.get(i+1));
            }

            ResultSet rs = prepared_statement.executeQuery();

            while (rs.next()){
                String title = rs.getString("title");
                String movieId = rs.getString("id");

                jsonArray.add(generateJsonObject(title, movieId));


            }
            dbcon.close();
            prepared_statement.close();
            rs.close();
            response.setStatus(200);

            response.getWriter().write(jsonArray.toString());
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "Iron Man",
     *   "data": { "heroID": 11 }
     * }
     *
     */
    private static JsonObject generateJsonObject(String title, String movieId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", title);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieId", movieId);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }


}
