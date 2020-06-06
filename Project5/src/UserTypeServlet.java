import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This UserTypeServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/cart.
 */
@WebServlet(name = "UserTypeServlet", urlPatterns = "/api/usertype")
public class UserTypeServlet extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws IOException{
//        HttpSession session = request.getSession();
        String userType = "customer";
//        String userType = ((User) session.getAttribute("user")).getType();
        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("usertype", userType);

        out.write(jsonObject.toString());
        out.close();
    }



}
