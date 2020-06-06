import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.synth.SynthEditorPaneUI;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
//        StringBuffer requestURL = httpRequest.getRequestURL();

        String contextPath = httpRequest.getContextPath();

        System.out.println("session id login filter: " + httpRequest.getSession().getId());

        String redirectPath = contextPath + "/login.html";

        chain.doFilter(request, response);
        // Check if this URL is allowed to access without logging in
//        if (this.isUrlAllowedWithoutLogin(httpRequest.getServletPath())) {
//            // Keep default action: pass along the filter chain
//
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // Redirect to login page if the "user" attribute doesn't exist in session
//        if (httpRequest.getSession().getAttribute("user") == null) {
//            httpResponse.sendRedirect(redirectPath);
//            System.out.println("Not Login");
//        } else {
//            chain.doFilter(request, response);
//            System.out.println("User" +  httpRequest.getSession().getAttribute("user").toString() + "is Logged In");
//        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */

        //Compares the servletPath with the allowed servlet path('/login.html')
        System.out.println("url: " + requestURI.toLowerCase());
        return allowedURIs.stream().anyMatch(element -> element.equals(requestURI.toLowerCase()));

    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("/admin_login.html");
        allowedURIs.add("/admin_login.js");
        allowedURIs.add("/api/adminlogin");
        allowedURIs.add("/login.html");
        allowedURIs.add("/login.js");
        allowedURIs.add("/api/login");
    }

    public void destroy() {
        // ignored.
    }

}
