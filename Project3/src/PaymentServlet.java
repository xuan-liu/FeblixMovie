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

import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PreparedStatement creditCardQuery = null;

        // check whether the credit card info matches a record in the credit cards table
        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            String query = "SELECT * from creditcards where id = ? and firstName = ? and lastName = ? and expiration = ?";
            creditCardQuery = dbcon.prepareStatement(query);
            creditCardQuery.setString(1, request.getParameter("cardnumber"));
            creditCardQuery.setString(2, request.getParameter("firstname"));
            creditCardQuery.setString(3, request.getParameter("lastname"));
            creditCardQuery.setString(4, request.getParameter("expir_date"));
            System.out.println(creditCardQuery);

            // Perform the query
            ResultSet rs = creditCardQuery.executeQuery();

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
                        PreparedStatement updateSales = null;
                        String updateString = "INSERT INTO sales VALUES(NULL,?, ?, ?)";
                        updateSales = dbcon.prepareStatement(updateString);
                        updateSales.setString(1, customerId);
                        updateSales.setString(2, movieId);
                        updateSales.setString(3, java.time.LocalDate.now().toString());
                        System.out.println(updateSales);
                        updateSales.executeUpdate();
                    }
                }

            } else {
                // credit card verified fail
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Fail! incorrect credit card information");
            }
            response.getWriter().write(responseJsonObject.toString());

            rs.close();
            creditCardQuery.close();
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
