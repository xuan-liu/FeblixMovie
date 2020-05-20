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
 * This JumpServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/cart.
 */
@WebServlet(name = "JumpServlet", urlPatterns = "/api/jump")
public class JumpServlet extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws IOException{
        HttpSession session = request.getSession();

        String movieListURL = ((User) session.getAttribute("user")).getMovieListURL();
        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("movielisturl", movieListURL);

        out.write(jsonObject.toString());
        out.close();
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");
        String movieListURL = request.getParameter("movielisturl");
        System.out.println("movieurl is: "+movieListURL);
        user.setMovieListURL(movieListURL);

        session.setAttribute("user",user);
        System.out.println(((User) session.getAttribute("user")).getMovieListURL());
    }
}
