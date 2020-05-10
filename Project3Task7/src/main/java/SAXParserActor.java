import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SAXParserActor extends DefaultHandler {

    static List<Actor> myActors;

    private String tempVal;

    //to maintain context
    private Actor tempActor;

    public SAXParserActor() {
        myActors = new ArrayList<Actor>();
    }

    public void runExample() {
        parseDocument();
//        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            javax.xml.parsers.SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            System.out.println("xxxxxxxxx");
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
            // if "actor", clean tempLastname, create a new instance of actor
            tempActor = new Actor();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("actor")) {
            // if "actor", add it to the list
            myActors.add(tempActor);
        } else if (qName.equalsIgnoreCase("stagename")) {
            // if "stagename", setStageName
            if (tempVal != null && tempVal.trim().length() > 0) {
                tempActor.setStageName(tempVal);
            } else {
                tempActor.setStageName(null);
            }
        } else if (qName.equalsIgnoreCase("familyname")) {
            // if "familyname", setLastName
            if (tempVal != null && tempVal.trim().length() > 0) {
                tempActor.setLastName(tempVal);
            } else {
                tempActor.setLastName(null);
            }
        } else if (qName.equalsIgnoreCase("firstname")) {
            // if "givenname", setFirstName
            if (tempVal != null && tempVal.trim().length() > 0) {
                tempActor.setFirstName(tempVal);
            } else {
                tempActor.setFirstName(null);
            }
        } else if (qName.equalsIgnoreCase("dob")) {
            // if "dob", setBirthyear
//            System.out.println("len: " + tempVal.trim().length());
            if (tempVal != null && tempVal.trim().length() > 0) {
                tempActor.setBirthyear(tempVal);
            } else {
//                System.out.println("set null");
                tempActor.setBirthyear(null);
            }
        }

    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        SAXParserActor spe = new SAXParserActor();
        spe.runExample();

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
        String checkQuery = "SELECT COUNT(*) FROM stars WHERE name = ?";

        PreparedStatement preparedQuery = null;
        String insertQuery = "Insert into stars(id, name, birthYear) Values(?,?,?)";
        int[] iNoRows=null;

        // find the max id in stars table
        try {
            showMaxId = conn.prepareStatement(query);
            ResultSet rs = showMaxId.executeQuery();
            int startId = Integer.parseInt(rs.getString("id").substring(2)) + 1;
            System.out.println("start id: " + startId);

            if (rs.next()){
//                conn.setAutoCommit(false);

                checkStar = conn.prepareStatement(checkQuery);
                preparedQuery = conn.prepareStatement(insertQuery);

                // iterate all the actors we parsed
                for (int i = 0; i < myActors.size(); i++) {
                    // get the actor name
                    Actor ac = myActors.get(i);
                    String name;

                    if (ac.getFirstName() != null && ac.getLastName() != null) {
                        name = myActors.get(i).getFirstName() + " " + myActors.get(i).getLastName();
                    } else {
//                        name = null;
                        // if the actor has no name, go to next actor
                        System.out.println("Star with Name Null: " + ac.toString());
                        continue;
                    }

                    // check whether the star already exists
                    checkStar.setString(1, name);
                    rs = showMaxId.executeQuery();

                    if (rs.next()) {
                        // if the star already exists, go to next actor
                        System.out.println("Star already exists: " + ac.toString());
                        continue;
                    } else {
                        // if not, add the star
                        String newId = "nm" + (startId + i);
                        preparedQuery.setString(1, newId);
                        preparedQuery.setString(2, name);
                        if (ac.getBirthyear() != null){
                            try {
                                preparedQuery.setInt(3, Integer.parseInt(ac.getBirthyear()));
                            } catch (Exception e) {
                                System.out.println("Star birthyear not int: " + ac.toString());
                                continue;
                            }
                        }
                        else{
                            preparedQuery.setNull(3, Types.NULL);
                        }
                        preparedQuery.addBatch();
                    }
                }

                iNoRows= preparedQuery.executeBatch();
//                conn.commit();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // close query and connection
        try {
            if(preparedQuery!=null) preparedQuery.close();
            if(conn!= null) conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}
