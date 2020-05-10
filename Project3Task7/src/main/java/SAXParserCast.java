import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SAXParserCast extends DefaultHandler {

    List<Cast> myCasts;

    private String tempVal;

    //to maintain context
    private Cast tempCast;

    public SAXParserCast() {
        myCasts = new ArrayList<Cast>();
    }

    public void runExample() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            javax.xml.parsers.SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            System.out.println("xxxxxxxxx");
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

//        System.out.println("No of Actors '" + myActors.size() + "'.");
//
//        Iterator<Actor> it = myActors.iterator();
//        while (it.hasNext()) {
//            System.out.println(it.next().toString());
//        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("m")) {
            // if "m", create a new instance of cast
            tempCast = new Cast();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("m")) {
            // if "actor", add it to the list
            myCasts.add(tempCast);
        } else if (qName.equalsIgnoreCase("f")) {
            // if "f", setMovieId
            tempCast.setMovieId(tempVal);
        } else if (qName.equalsIgnoreCase("a")) {
            // if "a", setStageName
            tempCast.setStageName(tempVal);
        }

    }

    public static void main(String[] args) {
        SAXParserCast spe = new SAXParserCast();
        spe.runExample();
    }

}
