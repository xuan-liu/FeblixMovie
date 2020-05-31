import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


// Declaring a WebServlet called MetaDataServlet, which maps to url "/api/stars"
@WebServlet(name = "MetaDataServlet", urlPatterns = "/admin/api/metadata")
public class MetaDataServlet extends HttpServlet {

//    @Resource(name = "jdbc/moviedb")
//    private DataSource dataSource;


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");
            Connection dbcon = ds.getConnection();
            if (dbcon == null)
                out.println("dbcon is null.");

            DatabaseMetaData databaseMetaData = dbcon.getMetaData();

            ResultSet rs = databaseMetaData.getTables(null, null, null, new String[]{"TABLE"});

            JsonArray jsonArray = new JsonArray();

            while(rs.next())
            {
                String tableName = rs.getString("TABLE_NAME");
                ResultSet columns = databaseMetaData.getColumns(null,null, tableName, null);

                JsonArray table = new JsonArray();
                while(columns.next())
                {
                    JsonObject column = new JsonObject();
                    column.addProperty("name", columns.getString("COLUMN_NAME"));
                    column.addProperty("type", columns.getString("TYPE_NAME"));
                    table.add(column);
                }
                JsonObject tableObject = new JsonObject();
                tableObject.addProperty("name", tableName);
                tableObject.add("columns", table);
                jsonArray.add(tableObject);

            }

//            System.out.println(jsonArray.toString());
            out.write(jsonArray.toString());



            // set response status to 200 (OK)
            response.setStatus(200);

            out.close();
            rs.close();
            dbcon.close();
        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);

        }
        out.close();

    }
}
