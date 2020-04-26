import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // check whether the user email/password info matches a record in the customers table

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();

            String query = "SELECT * from customers where email = \"" + email + "\" and password = \"" + password + "\"";

            System.out.println("login query is : " + query);
            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonObject responseJsonObject = new JsonObject();
            if (rs.next()) {
                // Login success:
                // set this user into the session
                request.getSession().setAttribute("user", new User(rs.getString("id"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("ccId"), rs.getString("address"),  rs.getString("email"), rs.getString("password")));

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "incorrect email or password");
            }
            response.getWriter().write(responseJsonObject.toString());

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
