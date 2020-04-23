import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "AddToCartServlet", urlPatterns = "/api/add")
public class AddToCartServlet extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        HttpSession session = request.getSession();
//        String sessionId = session.getId();
//        long lastAccessTime = session.getLastAccessedTime();
//
//        JsonObject responseJsonObject = new JsonObject();
//        responseJsonObject.addProperty("sessionID", sessionId);
//        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());
//
//        // write all the data into the jsonObject
//        response.getWriter().write(responseJsonObject.toString());
//    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String item = request.getParameter("movie");
        String quantityString = request.getParameter("quantity");
        int quantity = Integer.parseInt(quantityString);



        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
//        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        User user = (User) session.getAttribute("user");
        HashMap<String, Integer> previousItems = user.getItems();

            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
        synchronized (previousItems) {
            previousItems.put(item, quantity);
        }

        User updatedUser = new User(user.getUsername(), previousItems);

        session.setAttribute("user", updatedUser);



//        response.getWriter().write(String.join(",", previousItems));
    }
}
