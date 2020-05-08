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

import org.jasypt.util.password.StrongPasswordEncryptor;
/*
* Before running the project, make sure have run UpdateSecurePassword.java which read all the passwords from the
* existing moviedb.customers table, encrypt them, and update the table with the encrypted passwords.
*
* can also Create table “customers_backup” for the backup of "customers"
* create table customers_backup(id integer not null AUTO_INCREMENT, firstName varchar(50) not null,
* lastName varchar(50) not null, ccId varchar(20) not null, address varchar(200) not null,
* email varchar(50) not null, password varchar(20) not null, PRIMARY KEY(id),
* FOREIGN KEY(ccId) REFERENCES creditcards(id) on delete cascade on update cascade);
*
* Insert data into table “customers_backup”
* Insert into customers_backup select * from customers;
* */
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

        // Obtain RecaptchaResponse
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        try{
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);

        } catch (Exception e) {
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Recaptcha Verification Failed");
            response.getWriter().write(responseJsonObject.toString());
            return;
        }

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();
            String query = String.format("SELECT * from customers where email='%s'", email);

//            String query = "SELECT * from customers where email = \"" + email + "\" and password = \"" + password + "\"";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);
            boolean success = false;

            JsonObject responseJsonObject = new JsonObject();
            if (rs.next()) {
                // get the encrypted password from the database
                String encryptedPassword = rs.getString("password");

                // use the same encryptor to compare the user input password with encrypted password stored in DB
                success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
            }

            if (success){
                // Login success:
                // set this user into the session
                request.getSession().setAttribute("user", new User(rs.getString("id"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("ccId"), rs.getString("address"),  rs.getString("email"), rs.getString("password")));

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Fail! Incorrect email or password");
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
