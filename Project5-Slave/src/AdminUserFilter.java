import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class AdminUserFilter
 */
@WebFilter(filterName = "AdminUserFilter", urlPatterns = "/admin/*")
public class AdminUserFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;


        String contextPath = httpRequest.getContextPath();

        String redirectPath = contextPath + "/admin_login.html";

        System.out.println("Admin Context path: " + contextPath);
        System.out.println("Admin Re path: " + redirectPath);


        if (httpRequest.getSession().getAttribute("user") == null) {
            httpResponse.sendRedirect(redirectPath);
        }

        else{
            System.out.println(((User)httpRequest.getSession().getAttribute("user")).getType());
            if (! ((User)httpRequest.getSession().getAttribute("user")).getType().equals("admin")){
                httpResponse.sendRedirect(redirectPath);

            }
            chain.doFilter(request, response);
            return;
        }



    }

    public void init(FilterConfig fConfig) {

    }

    public void destroy() {
        // ignored.
    }

}
