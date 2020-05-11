import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;


public class SAXParserActor extends DefaultHandler {

    List<Actor> myActors;

    private String tempVal; //to maintain context

    private Actor tempActor;

    private boolean addList; //whether add the actor into list

    String inconsistent = ""; // inconsistent report

    private Map<String, String> actorMap = new HashMap<>(); // hashmap. key: stage name, value: star id

    String maxIdString = ""; // maxId in the actorMap

    public SAXParserActor() {
        myActors = new ArrayList<Actor>();
    }

    /**
     * parse the xml file and add the data into database
     */
    public void runParser() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        parseDocument();
        System.out.println("Finish Parsing Actor. No. Valid data after parsing: " + myActors.size());
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
            System.out.println("Start Parsing Actor.");
            sp.parse("actors63.xml", this);

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
        if (qName.equalsIgnoreCase("actor")) {
            // if "actor", create a new instance of actor
            tempActor = new Actor();
            addList = true;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (addList && qName.equalsIgnoreCase("actor")) {
            // if "actor", add it to the list
            myActors.add(tempActor);

        } else if (qName.equalsIgnoreCase("stagename")) {
            // if "stagename", setStageName
            if (tempVal != null && tempVal.trim().length() > 0) {
                tempActor.setStageName(tempVal);
            } else {
                // if null, not add to list
                addList = false;
                inconsistent += "Star with null stagename " + tempActor.toString() + "\n";
            }

        } else if (qName.equalsIgnoreCase("dob")) {
            // if "dob", setBirthyear
            if (tempVal != null && tempVal.trim().length() > 0) {
                try {
                    Integer.parseInt(tempVal);
                    tempActor.setBirthyear(tempVal);
                } catch (Exception e) {
                    tempActor.setBirthyear(null);
                }

            } else {
                tempActor.setBirthyear(null);
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

//        PreparedStatement showMaxId = null;
//        String query = "Select max(id) as id from stars";

        PreparedStatement loadStar = null;
        String loadQuery = "SELECT * FROM stars";

        PreparedStatement preparedQuery = null;
        String insertQuery = "Insert into stars(id, name, birthYear) Values(?,?,?)";
        int[] iNoRows = null;
        int recordCount = 0;
        int maxId = 0;

        // load all stars in stars table in memory
        try {
            loadStar = conn.prepareStatement(loadQuery);
            ResultSet rsl = loadStar.executeQuery();

            while (rsl.next()) {
                // add to actorMap
                actorMap.put(rsl.getString("name"), rsl.getString("id"));

                // find max id in stars table
                int currId = Integer.parseInt(rsl.getString("id").substring(2));
                if (currId > maxId) {
                    maxId = currId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // find the max id in stars table
        try {
            maxId += 1;
            System.out.println("Start adding actor data, start actor id: " + maxId);

            conn.setAutoCommit(false);
            preparedQuery = conn.prepareStatement(insertQuery);

            // iterate all the actors we parsed
            for (int i = 0; i < myActors.size(); i++) {
                // get the actor
                Actor ac = myActors.get(i);

                // check whether the star already exists
                if (actorMap.containsKey(ac.getStageName())) {
                    // if the star already exists, go to next actor
                    inconsistent += "Star already exists: " + ac.toString() + "\n";
                    continue;
                } else {
                    // if not, add the star
                    String newId = "nm" + maxId;
                    preparedQuery.setString(1, newId);
                    preparedQuery.setString(2, ac.getStageName());

                    // add new id into hashmap
                    actorMap.put(ac.getStageName(), newId);

                    if (ac.getBirthyear() != null){
                        preparedQuery.setInt(3, Integer.parseInt(ac.getBirthyear()));
                    }
                    else{
                        preparedQuery.setNull(3, Types.NULL);
                    }
                    preparedQuery.addBatch();
                    recordCount += 1;
                    maxId += 1;
                }
            }
            System.out.println("adding star record number: " + recordCount + ", actor map size: " + actorMap.size());
            iNoRows= preparedQuery.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        maxIdString += "nm" + maxId;
        // print inconsistent to file
        try {
            FileWriter myWriter = new FileWriter("inconsistent_actor.txt");
            myWriter.write(inconsistent);
            myWriter.close();
            System.out.println("Successfully wrote inconsistent to the file inconsistent_actor.txt.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // close query and connection
        try {
            loadStar.close();
            preparedQuery.close();
            conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public Map<String, String> getActorMap() {
        return actorMap;
    }

    public String getMaxId() {
        return maxIdString;
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        SAXParserActor spe = new SAXParserActor();
        spe.runParser();
    }

}
