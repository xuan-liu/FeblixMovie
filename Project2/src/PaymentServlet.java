import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String cardnumber = request.getParameter("cardnumber");
//        java.sql.Date expir_date = request.getParameter("expir_date");
        String expir_date = request.getParameter("expir_date");

        // check whether the credit card info matches a record in the credit cards table
        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();

            String query = "SELECT * from creditcards where id = \"" + cardnumber + "\" and firstName = \"" + firstname + "\" and lastName = \"" + lastname +"\" and expiration = \"" + expir_date + "\"";

            System.out.println(query);
            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonObject responseJsonObject = new JsonObject();
            if (rs.next()) {
                // credit card verified success:
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

                // get user information
                User user = (User) request.getSession().getAttribute("user");
                String customerId = user.getUserId();
                HashMap<String, Integer> items = user.getItems();
                HashMap<String, String> movieInfo = user.getMovieInfo();
                System.out.println("time: " + java.time.LocalDate.now());

                // record transactions in the "sales" table
                for (Map.Entry<String, Integer> entry : items.entrySet()) {
                    String movieId = movieInfo.get(entry.getKey());
                    int quantity = entry.getValue();
                    for (int i = 0; i < quantity; i++) {
                        String updateQuery = "INSERT INTO sales VALUES(NULL,\"" + customerId + "\", \"" + movieId + "\", \"" + java.time.LocalDate.now() + "\")";
                        System.out.println(updateQuery);
                        statement.executeUpdate(updateQuery);
                    }
                }

            } else {
                // credit card verified fail
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Fail! incorrect credit card information");
            }
            response.getWriter().write(responseJsonObject.toString());

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);

        }
    }
}
