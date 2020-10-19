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

    static String inconsistent = "";

    private static Map<String, String> actorMap;
    private static Map<String, String> movieMap;

    static String maxStarId = "";

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
            sp.parse("../data/casts124.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
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
                inconsistent += "Adding to stars_in_movies table -- Cast with null movie id: " + tempCast.toString() + "\n";
            }

        } else if (qName.equalsIgnoreCase("a")) {

            // if "a", setStageName
            if (tempVal != null && tempVal.trim().length() > 0) {
                tempCast.setStageName(tempVal);

                if (tempVal.equals("s a")) {
                    addList = false;
                    inconsistent += "Adding to stars_in_movies table -- Cast with stagename as s a: " + tempCast.toString() + "\n";
                }
            } else {
                // if null, not add to list
                addList = false;
                inconsistent += "Adding to stars_in_movies table -- Cast with null stagename: " + tempCast.toString() + "\n";
            }
        }

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

        PreparedStatement insertStar = null;
        String insertStarQuery = "Insert into stars(id, name, birthYear) Values(?,?,?)";
        int[] iNoRowss = null;
        int starCount = 0;
        int maxId = Integer.parseInt(maxStarId.substring(2));;

        try {
            preparedQuery = conn.prepareStatement(insertQuery);
            checkCast = conn.prepareStatement(checkQuery);
            insertStar = conn.prepareStatement(insertStarQuery);
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
                        inconsistent += "Adding to stars_in_movies table -- Cast already exists: " + ct.toString() + "\n";
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
                    if (actorMap.containsKey(ct.getStageName())) {
                        // if the star is in database, get the id and add the cast
                        String id = actorMap.get(ct.getStageName());
                        preparedQuery.setString(1, id);
                        preparedQuery.setString(2, movieId);
                        preparedQuery.addBatch();
                        recordCount += 1;
                    } else {
                        // if the star is not in database, add the star in stars table
                        String newId = "nm" + maxId;
                        insertStar.setString(1, newId);
                        insertStar.setString(2, ct.getStageName());
                        insertStar.setNull(3, Types.NULL);
                        insertStar.addBatch();
                        maxId += 1;
                        starCount += 1;

                        // add to actorMap
                        actorMap.put(ct.getStageName(), newId);

                        // add the cast
                        preparedQuery.setString(1, newId);
                        preparedQuery.setString(2, movieId);
                        preparedQuery.addBatch();
                        recordCount += 1;
                    }
                } else {
                    // otherwise, report inconsistent
                    inconsistent += "Adding to stars_in_movies table -- Cast Movie not found: " + ct.toString() + "\n";
                }

            }

            System.out.println("adding star record number: " + starCount);
            iNoRowss= insertStar.executeBatch();
            System.out.println("adding cast record number: " + recordCount);
            iNoRows= preparedQuery.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // close query and connection
        try {
            insertStar.close();
            checkCast.close();
            preparedQuery.close();
            conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void printInconsistent() throws IOException{
        // print inconsistent to file
        try {
            FileWriter myWriter = new FileWriter("inconsistent_report.txt");
            myWriter.write(inconsistent);
            myWriter.close();
            System.out.println("Successfully wrote all inconsistent cases to the file inconsistent_report.txt.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
        // get movieMap and actorMap
        SAXParserActor actor_parser = new SAXParserActor();
        actor_parser.runParser();
        actorMap = actor_parser.getActorMap();
        maxStarId += actor_parser.getMaxId();
        inconsistent += actor_parser.getInconsistent();

        SAXParserMain movie_parser = new SAXParserMain();
        movie_parser.runParser();
        movieMap = movie_parser.getMovieMap();
        inconsistent += movie_parser.getInconsistent();

        SAXParserCast spe = new SAXParserCast();
        spe.runParser();
        spe.printInconsistent();
    }

}
