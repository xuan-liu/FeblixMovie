import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SAXParserActor extends DefaultHandler {

    List<Actor> myActors;

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
            System.out.println("len: " + tempVal.trim().length());
            if (tempVal != null && tempVal.trim().length() > 0) {
                tempActor.setBirthyear(tempVal);
            } else {
                System.out.println("set null");
                tempActor.setBirthyear(null);
            }
        }

    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        SAXParserActor spe = new SAXParserActor();
        spe.runExample();

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
//        Statement statement = conn.createStatement();
//        String query = "Select max(id) as id from stars";
//        ResultSet rs = statement.executeQuery(query);
//        PreparedStatement psInsertRecord=null;
//        String sqlInsertRecord=null;
//
//        int[] iNoRows=null;
//
//        sqlInsertRecord="insert into stars (id, name, birthYear) values(?,?,?)";
//        try {
//            conn.setAutoCommit(false);
//
//            psInsertRecord=conn.prepareStatement(sqlInsertRecord);
//
//
//            for(int i=1;i<=50;i++)
//            {
//                psInsertRecord.setInt(1, i);
//                psInsertRecord.setString(2,"My next text piece " + (i*i));
//                psInsertRecord.addBatch();
//            }
//
//            iNoRows=psInsertRecord.executeBatch();
//            conn.commit();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            if(psInsertRecord!=null) psInsertRecord.close();
//            if(conn!=null) conn.close();
//        } catch(Exception e) {
//            e.printStackTrace();
//        }

    }

}
