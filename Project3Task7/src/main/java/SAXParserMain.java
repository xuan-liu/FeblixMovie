import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.io.FileWriter;   // Import the FileWriter class


public class SAXParserMain extends DefaultHandler {

    List<Movie> myMovies;

    private String tempVal;

    private String tempDirector;

    //to maintain context
    private Movie tempMovie;

    public SAXParserMain() {
        myMovies = new ArrayList<Movie>();
    }

    public void runExample() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf2 = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            javax.xml.parsers.SAXParser sp2 = spf2.newSAXParser();

            //parse the file and also register this class for call backs
            System.out.println("xxxxxxxxx");
            sp2.parse("mains243.xml", this);

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
        String result = "No of Movies '" + myMovies.size() + "'.\n";
        Iterator<Movie> it = myMovies.iterator();
        while (it.hasNext()) {
            result += it.next().toString() + "\n";
        }

        try {
            FileWriter myWriter = new FileWriter("main.txt");
            myWriter.write(result);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

//        System.out.println("No of Employees '" + myMovies.size() + "'.");
//
//        Iterator<Movie> it = myMovies.iterator();
//        while (it.hasNext()) {
//            System.out.println(it.next().toString());
//        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("directorfilms")) {
            // if "directorfilms", reset temp director
            tempDirector = "";
        } else if (qName.equalsIgnoreCase("film")) {
            // if "film", create a new instance of movie
            tempMovie = new Movie();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("dirname")) {
            // if "dirname", set tempDirector
            tempDirector = tempVal;
        } else if (qName.equalsIgnoreCase("film")) {
            // if "film", setDirector, add it to the list
            tempMovie.setDirector(tempDirector);
            myMovies.add(tempMovie);
        } else if (qName.equalsIgnoreCase("fid")) {
            // if "fid", setId
            tempMovie.setId(tempVal);
        } else if (qName.equalsIgnoreCase("t")) {
            // if "t", setTitle
            tempMovie.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("year")) {
            // if "year", setYear
            tempMovie.setYear(tempVal);
        } else if (qName.equalsIgnoreCase("cat")) {
            // if "cats", setGenres
            tempMovie.addGenre(tempVal);
        }

    }

    public static void main(String[] args) {
        SAXParserMain spe = new SAXParserMain();
        spe.runExample();
    }

}
