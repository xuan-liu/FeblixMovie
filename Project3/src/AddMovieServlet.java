import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;


@WebServlet(name = "AddMovieServlet", urlPatterns = "/admin/api/addmovie")
public class AddMovieServlet extends HttpServlet {

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("Enter!");

        String movieName = request.getParameter("moviename");
        String director = request.getParameter("director");
        String year = request.getParameter("year");
        String genre = request.getParameter("genre");
        String star = request.getParameter("star");

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement);
            String query = "CALL add_movie(?, ?, ?, ?, ?)";
            PreparedStatement preparedQuery = dbcon.prepareStatement(query);
            preparedQuery.setString(1, movieName);
            preparedQuery.setString(2, director);
            preparedQuery.setString(3, year);
            preparedQuery.setString(4, genre);
            preparedQuery.setString(5, star);

            ResultSet rs = preparedQuery.executeQuery();

            if (rs.next()) {
                String message = rs.getString("message");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("message", message);
                System.out.println(message);
                response.getWriter().write(jsonObject.toString());
            }



            response.setStatus(200);

            rs.close();
            preparedQuery.close();
            dbcon.close();
        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

        }


    }
}

