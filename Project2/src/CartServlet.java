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
 * This CartServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/cart.
 */
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();

        HashMap<String, Integer> Items = ((User) session.getAttribute("user")).getItems();


        PrintWriter out = response.getWriter();
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String,Integer> entry : Items.entrySet())
            jsonObject.addProperty(entry.getKey(), entry.getValue());
            jsonArray.add(jsonObject);

        // write all the data into the jsonObject
        out.write(jsonArray.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String message = request.getParameter("message");
        String item = request.getParameter("movie");
        String quantityString = request.getParameter("quantity");


        System.out.println(message);
        System.out.println(item);
        System.out.println(quantityString);
        int quantity = Integer.parseInt(quantityString);

        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        User user = (User) session.getAttribute("user");
        HashMap<String, Integer> previousItems = user.getItems();

        if (message.equals("delete")) {
            // delete the movie
            synchronized (previousItems) {
                previousItems.remove(item);
            }
        } else if (message.equals("update")) {
            // update the movie with the quantity
            synchronized (previousItems) {
                previousItems.put(item, quantity);
            }
        }
        else if (message.equals("add")){
            // increase the quantity of movie
            synchronized (previousItems) {
                if (previousItems.containsKey(item)){
                    quantity += previousItems.get(item);
                }
                previousItems.put(item, quantity);
            }
        }

        User updatedUser = new User(user.getUsername(), previousItems);

        session.setAttribute("user", updatedUser);
    }
}
