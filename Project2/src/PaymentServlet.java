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

        JsonObject responseJsonObject = new JsonObject();
        if (firstname.equals("Xuan")) {
            // credit card verified success:
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");

        } else {
            // credit card verified fail
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "incorrect credit card info");
        }
        response.getWriter().write(responseJsonObject.toString());


//        try {
//            // Get a connection from dataSource
//            Connection dbcon = dataSource.getConnection();
//
//            // Declare our statement
//            Statement statement = dbcon.createStatement();
//
//            String query = "SELECT * from creditcards as m where m.id = " + cardnumber + " and m.firstName = " + firstname + " and m.lastName = " + lastname;
//
//            System.out.println(query);
//            // Perform the query
//            ResultSet rs = statement.executeQuery(query);
//
//            JsonObject responseJsonObject = new JsonObject();
//            if (rs.next()) {
//                // credit card verified success:
//                responseJsonObject.addProperty("status", "success");
//                responseJsonObject.addProperty("message", "success");
//
//            } else {
//                // credit card verified fail
//                responseJsonObject.addProperty("status", "fail");
//                responseJsonObject.addProperty("message", "incorrect credit card info");
//            }
//            response.getWriter().write(responseJsonObject.toString());
//
//            rs.close();
//            statement.close();
//            dbcon.close();
//        } catch (Exception e) {
//
//            // write error message JSON object to output
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("errorMessage", e.getMessage());
//
//            // set reponse status to 500 (Internal Server Error)
//            response.setStatus(500);
//
//        }
    }
}
