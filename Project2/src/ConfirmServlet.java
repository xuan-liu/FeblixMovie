import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * This CartServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/cart.
 */
@WebServlet(name = "ConfirmServlet", urlPatterns = "/api/confirm")
public class ConfirmServlet extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        HashMap<String, Integer> Items = user.getItems();

        PrintWriter out = response.getWriter();
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String,Integer> entry : Items.entrySet())
            jsonObject.addProperty(entry.getKey(), entry.getValue());
            jsonArray.add(jsonObject);

        // write all the data into the jsonObject
        out.write(jsonArray.toString());

        // clear shopping cart
        user.clearItems();
        user.clearMovieInfo();
        request.getSession().setAttribute("user", user);
    }
}
