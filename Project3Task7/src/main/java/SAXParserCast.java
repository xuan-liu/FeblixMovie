import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;


public class SAXParserCast extends DefaultHandler {

    List<Cast> myCasts;

    private String tempVal;

    private Cast tempCast; //to maintain context

    private boolean addList; //whether add the actor into list

    String inconsistent = "";

    private static Map<String, String> actorMap;
    private static Map<String, String> movieMap;

    public SAXParserCast() {
        myCasts = new ArrayList<Cast>();
    }

    /**
     * parse the xml file and add the data into database
     */
    public void runParser() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
        parseDocument();
        System.out.println("Finish Parsing Cast. No. Valid data after parsing: " + myCasts.size());
        addData();
    }

    /**
     * parse the xml file
     */
    private void parseDocument() {
        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            javax.xml.parsers.SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            System.out.println("Start Parsing Cast.");
            sp.parse("casts124.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {
        String result = "No of Casts '" + myCasts.size() + "'.\n";
        Iterator<Cast> it = myCasts.iterator();
        while (it.hasNext()) {
            result += it.next().toString() + "\n";
        }

        try {
            FileWriter myWriter = new FileWriter("cast.txt");
            myWriter.write(result);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("m")) {
            // if "m", create a new instance of cast
            tempCast = new Cast();
            addList = true;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (addList && qName.equalsIgnoreCase("m")) {
            // if "actor", add it to the list
            myCasts.add(tempCast);

        } else if (qName.equalsIgnoreCase("f")) {

            // if "f", setMovieId
            if (tempVal != null && tempVal.trim().length() > 0) {
                tempCast.setMovieId(tempVal);
            } else {
                // if null, not add to list
                addList = false;
                inconsistent += "Cast with null movie id: " + tempCast.toString() + "\n";
            }

        } else if (qName.equalsIgnoreCase("a")) {

            // if "a", setStageName
            if (tempVal != null && tempVal.trim().length() > 0) {
                tempCast.setStageName(tempVal);

                if (tempVal.equals("s a")) {
                    addList = false;
                    inconsistent += "Cast with stagename as s a: " + tempCast.toString() + "\n";
                }
            } else {
                // if null, not add to list
                addList = false;
                inconsistent += "Cast with null stagename: " + tempCast.toString() + "\n";
            }
        }

    }

    /**
     * set actorMap from class SAXParserActor, movieMap from class SAXParserMain
     */
    public void setHashMap() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        // get movieMap and actorMap
        SAXParserActor actor_parser = new SAXParserActor();
        actor_parser.runParser();
        actorMap = actor_parser.getActorMap();

        SAXParserMain movie_parser = new SAXParserMain();
        movie_parser.runParser();
        movieMap = movie_parser.getMovieMap();
    }

    /**
     * add the data to the database
     */
    public void addData() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        // connect to the database
        Connection conn = null;

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String jdbcURL="jdbc:mysql://localhost:3306/moviedb";

        try {
            conn = DriverManager.getConnection(jdbcURL,"mytestuser", "mypassword");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement preparedQuery = null;
        String insertQuery = "Insert into stars_in_movies(starId, movieId) Values(?,?)";
        int[] iNoRows = null;
        int recordCount = 0;

        PreparedStatement checkCast = null;
        String checkQuery = "SELECT * FROM stars_in_movies WHERE starId = ? and movieId = ?";

        PreparedStatement checkStar = null;
        String checkStarQuery = "SELECT * FROM stars WHERE name = ?";

        try {
            preparedQuery = conn.prepareStatement(insertQuery);
            checkCast = conn.prepareStatement(checkQuery);
            checkStar = conn.prepareStatement(checkStarQuery);
            System.out.println("Start adding cast data.");

            conn.setAutoCommit(false);

            // iterate all the casts we parsed
            for (int i = 0; i < myCasts.size(); i++) {
                // get the cast
                Cast ct = myCasts.get(i);
                String movieId = movieMap.get(ct.getMovieId());
                String starId = actorMap.get(ct.getStageName());

                if (movieId != null && starId != null) {
                    // if both movie and star in hashmap, check whether the cast already exists
                    checkCast.setString(1, starId);
                    checkCast.setString(2, movieId);

                    ResultSet rs = checkCast.executeQuery();

                    if (rs.next()) {
                        // if the cast already exists, continue for next cast
                        inconsistent += "Cast already exists: " + ct.toString() + "\n";
                        continue;
                    } else {
                        // if not, add the cast
                        recordCount += 1;
                        preparedQuery.setString(1, starId);
                        preparedQuery.setString(2, movieId);
                        preparedQuery.addBatch();
                    }

                } else if (movieId != null && starId == null) {
                    // if movie in hashmap, no star in hashmap, check whether the star is in database
                    checkStar.setString(1, ct.getStageName());
                    ResultSet rs = checkStar.executeQuery();

                    if (rs.next()) {
                        // if the star is in database, get the id and add the cast
                        String id = rs.getString("id");
                        recordCount += 1;
                        preparedQuery.setString(1, id);
                        preparedQuery.setString(2, movieId);
                        preparedQuery.addBatch();
                    } else {
                        // add the cast and add the actor
                        inconsistent += "Cast Star cannot add: " + ct.toString() + "\n";
                    }
                } else {
                    // otherwise, report inconsistent
                    inconsistent += "Cast Movie with null id: " + ct.toString() + "\n";
                }

            }

            System.out.println("adding cast record number: " + recordCount);
            iNoRows= preparedQuery.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // print inconsistent to file
        try {
            FileWriter myWriter = new FileWriter("inconsistent_cast.txt");
            myWriter.write(inconsistent);
            myWriter.close();
            System.out.println("Successfully wrote inconsistent to the file inconsistent_cast.txt.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // close query and connection
        try {
            checkCast.close();
            preparedQuery.close();
            conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        // get movieMap and actorMap
        SAXParserActor actor_parser = new SAXParserActor();
        actor_parser.runParser();
        actorMap = actor_parser.getActorMap();

        SAXParserMain movie_parser = new SAXParserMain();
        movie_parser.runParser();
        movieMap = movie_parser.getMovieMap();

        SAXParserCast spe = new SAXParserCast();
        spe.runParser();
    }

}
