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

    private String tempVal;

    //to maintain context
    private Actor tempActor;

    //whether add the actor into list
    private boolean addList;
    static String inconsistent = "";

    // hashmap. key: stage name, value: star id
    public Map<String, String> actorMap = new HashMap<>();

    public SAXParserActor() {
        myActors = new ArrayList<Actor>();
    }

    public void runExample() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        parseDocument();
        System.out.println("Finish Parsing. No. Valid data after parsing: " + myActors.size());
        addData();
//        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            javax.xml.parsers.SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            System.out.println("Start Parsing.");
            sp.parse("actors63.xml", this);

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
        String result = "No of Actors '" + myActors.size() + "'.\n";
        Iterator<Actor> it = myActors.iterator();
        while (it.hasNext()) {
            result += it.next().toString() + "\n";
        }

        try {
            FileWriter myWriter = new FileWriter("actor.txt");
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
                    addList = false;
                    inconsistent += "Star birthyear not int: " + tempActor.toString() + "\n";
                }
            } else {
                tempActor.setBirthyear(null);
            }
        }
//        else if (qName.equalsIgnoreCase("familyname")) {
//            // if "familyname", setLastName
//            if (tempVal != null && tempVal.trim().length() > 0) {
//                tempActor.setLastName(tempVal);
//            } else {
//                addList = false;
//                inconsistent += "Star with null familyname " + tempActor.toString() + "\n";
//            }
//        } else if (qName.equalsIgnoreCase("firstname")) {
//            // if "givenname", setFirstName
//            if (tempVal != null && tempVal.trim().length() > 0) {
//                tempActor.setFirstName(tempVal);
//            } else {
//                addList = false;
//                inconsistent += "Star with null firstname " + tempActor.toString() + "\n";
//            }
//        }

    }

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

        PreparedStatement showMaxId = null;
        String query = "Select max(id) as id from stars";

        PreparedStatement checkStar = null;
        String checkQuery = "SELECT * FROM stars WHERE name = ?";

        PreparedStatement preparedQuery = null;
        String insertQuery = "Insert into stars(id, name, birthYear) Values(?,?,?)";
        int[] iNoRows=null;
        int recordCount = 0;

        // find the max id in stars table
        try {
            showMaxId = conn.prepareStatement(query);
            ResultSet rs = showMaxId.executeQuery();

            if (rs.next()){
                int startId = Integer.parseInt(rs.getString("id").substring(2)) + 1;
                System.out.println("Start adding data, start id: " + startId);

                conn.setAutoCommit(false);
                checkStar = conn.prepareStatement(checkQuery);
                preparedQuery = conn.prepareStatement(insertQuery);

                // iterate all the actors we parsed
                for (int i = 0; i < myActors.size(); i++) {
                    // get the actor
                    Actor ac = myActors.get(i);
//                    String name;
//                    name = myActors.get(i).getFirstName() + " " + myActors.get(i).getLastName();

                    // check whether the star already exists
                    checkStar.setString(1, ac.getStageName());
                    rs = checkStar.executeQuery();

                    if (rs.next()) {
                        // add existed id into hashmap
                        actorMap.put(ac.getStageName(), rs.getString("id"));

                        // if the star already exists, go to next actor
                        inconsistent += "Star already exists: " + ac.toString() + "\n";
                        continue;
                    } else {
                        // if not, add the star
                        String newId = "nm" + (startId + i);
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
                    }
                    recordCount += 1;
                }
                System.out.println("adding star record number: " + recordCount + ", " + actorMap.size());
                iNoRows= preparedQuery.executeBatch();
                conn.commit();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
            showMaxId.close();
            checkStar.close();
            preparedQuery.close();
            conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        SAXParserActor spe = new SAXParserActor();
        spe.runExample();

        // print inconsistent to file
//        try {
//            FileWriter myWriter = new FileWriter("inconsistent_actor.txt");
//            myWriter.write(inconsistent);
//            myWriter.close();
//            System.out.println("Successfully wrote inconsistent to the file inconsistent_actor.txt.");
//        } catch (IOException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }

//        // connect to the database
//        Connection conn = null;
//
//        Class.forName("com.mysql.jdbc.Driver").newInstance();
//        String jdbcURL="jdbc:mysql://localhost:3306/moviedb";
//
//        try {
//            conn = DriverManager.getConnection(jdbcURL,"mytestuser", "mypassword");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        PreparedStatement showMaxId = null;
//        String query = "Select max(id) as id from stars";
//
//        PreparedStatement checkStar = null;
//        String checkQuery = "SELECT * FROM stars WHERE name = ?";
//
//        PreparedStatement preparedQuery = null;
//        String insertQuery = "Insert into stars(id, name, birthYear) Values(?,?,?)";
//        int[] iNoRows=null;
//        int recordCount = 0;
//
//        // find the max id in stars table
//        try {
//            showMaxId = conn.prepareStatement(query);
//            ResultSet rs = showMaxId.executeQuery();
//
//            if (rs.next()){
//                int startId = Integer.parseInt(rs.getString("id").substring(2)) + 1;
//                System.out.println("Start adding data, start id: " + startId);
//
//                conn.setAutoCommit(false);
//                checkStar = conn.prepareStatement(checkQuery);
//                preparedQuery = conn.prepareStatement(insertQuery);
//
//                // iterate all the actors we parsed
//                for (int i = 0; i < myActors.size(); i++) {
//                    // get the actor
//                    Actor ac = myActors.get(i);
////                    String name;
////                    name = myActors.get(i).getFirstName() + " " + myActors.get(i).getLastName();
//
//                    // check whether the star already exists
//                    checkStar.setString(1, ac.getStageName());
//                    rs = checkStar.executeQuery();
//
//                    if (rs.next()) {
//                        // if the star already exists, go to next actor
//                        inconsistent += "Star already exists: " + ac.toString() + "\n";
//                        continue;
//                    } else {
//                        // if not, add the star
//                        String newId = "nm" + (startId + i);
//                        preparedQuery.setString(1, newId);
//                        preparedQuery.setString(2, ac.getStageName());
//                        if (ac.getBirthyear() != null){
//                            preparedQuery.setInt(3, Integer.parseInt(ac.getBirthyear()));
//                        }
//                        else{
//                            preparedQuery.setNull(3, Types.NULL);
//                        }
//                        preparedQuery.addBatch();
//                    }
//                    recordCount += 1;
//                }
//                System.out.println("adding star record number: " + recordCount);
//                iNoRows= preparedQuery.executeBatch();
//                conn.commit();
//
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        // print inconsistent to file
//        try {
//            FileWriter myWriter = new FileWriter("inconsistent_actor.txt");
//            myWriter.write(inconsistent);
//            myWriter.close();
//            System.out.println("Successfully wrote inconsistent to the file inconsistent_actor.txt.");
//        } catch (IOException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }
//
//        // close query and connection
//        try {
//            if(preparedQuery!=null) preparedQuery.close();
//            if(conn!= null) conn.close();
//        } catch(Exception e) {
//            e.printStackTrace();
//        }

    }

}
