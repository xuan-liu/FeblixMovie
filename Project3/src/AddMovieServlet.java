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


@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/addmovie")
public class AddMovieServlet extends HttpServlet {

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String movieName = request.getParameter("moviename");
        String director = request.getParameter("director");
        String year = request.getParameter("year");
        String genre = request.getParameter("genre");
        String star = request.getParameter("star");

        System.out.println("adminlogin");

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();
            String query = "Select max(id) as id from stars";

            ResultSet rs = statement.executeQuery(query);

            if (rs.next()){
                String maxId = rs.getString("id").substring(2);
                String newId = "nm" + (Integer.parseInt(maxId) + 1);

                String insertQuery = "Insert into stars(id, name, birthYear) Values(?,?,?)";

                PreparedStatement preparedQuery = dbcon.prepareStatement(insertQuery);

//                preparedQuery.setString(1, newId);
//                preparedQuery.setString(2, name_param);
//                if (birthyear_param.equals("")){
//                    preparedQuery.setNull(3, Types.NULL);
//                }
//                else{
//                    preparedQuery.setInt(3, Integer.parseInt(birthyear_param));
//                }

                preparedQuery.executeUpdate();




//                if (birthyear_param.equals("")){
//                    System.out.println("pass1");
//                        String insertQuery = "Insert into stars(id, name) Values(?,?)";
//
//                        PreparedStatement preparedQuery = dbcon.prepareStatement(insertQuery);
//
//                        preparedQuery.setString(1, newId);
//                        preparedQuery.setString(2, name_param);
//
//                    preparedQuery.executeUpdate();
//                    System.out.println("pass3");
//
//                }
//
//                else{
//                    System.out.println("pass2");
//                    String insertQuery = "Insert into stars(id, name, birthYear) Values(?,?,?)";
//
//                    PreparedStatement preparedQuery = dbcon.prepareStatement(insertQuery);
//
//                    preparedQuery.setString(1, newId);
//                    preparedQuery.setString(2, name_param);
//                    preparedQuery.setInt(3, Integer.parseInt(birthyear_param));
//                    preparedQuery.executeQuery();
//
//
//
//
//                }

            }







            // write JSON string to output

            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
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

