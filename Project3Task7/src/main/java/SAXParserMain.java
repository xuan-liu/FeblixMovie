import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import java.io.FileWriter;   // Import the FileWriter class


public class SAXParserMain extends DefaultHandler {

    List<Movie> myMovies;

    private String tempVal;

    private String tempDirector;

    private Movie tempMovie; //to maintain context

    private boolean addList; //whether add the actor into list

    String inconsistent = "";

    // hashmap. key: fid in xml file, value: movie id in database
    private Map<String, String> movieMap = new HashMap<>();

    // hashmap for genre. key: genre's whole name, value: genre's id
    private Map<String, Integer> genreMap = new HashMap<>();

    // hashmap for genre whole name. key: shorten genre in xml file, value: the whole name
    final private Map<String, String> genreNameMap = new HashMap<String, String>() {{
        put("Susp", "Thriller");
        put("CnR", "Cops and Robbers");
        put("Dram", "Drama");
        put("West", "Western");
        put("Myst", "Mystery");
        put("S.F.", "Sci-Fi");
        put("Advt", "Adventure");
        put("Horr", "Horror");
        put("Romt", "Romantic");
        put("Comd", "Comedy");
        put("Musc", "Musical");
        put("Docu", "Documentary");
        put("Porn", "Pornography");
        put("Noir", "Black");
        put("BioP", "Biographical Picture");
        put("TV", "TV Show");
        put("TVs", "TV series");
        put("TVm", "TV miniseries");
    }};


    public SAXParserMain() {
        myMovies = new ArrayList<Movie>();
    }

    /**
     * parse the xml file and add the data into database
     */
    public void runParser() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        parseDocument();
        System.out.println("Finish Parsing Main. No. Valid data after parsing: " + myMovies.size());
        addData();
    }

    /**
     * parse the xml file
     */
    private void parseDocument() {
        //get a factory
        SAXParserFactory spf2 = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            javax.xml.parsers.SAXParser sp2 = spf2.newSAXParser();

            //parse the file and also register this class for call backs
            System.out.println("Start Parsing Main.");
            sp2.parse("mains243.xml", this);

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
        if (qName.equalsIgnoreCase("directorfilms")) {
            // if "directorfilms", reset temp director
            tempDirector = "";

        } else if (qName.equalsIgnoreCase("film")) {
            // if "film", create a new instance of movie
            tempMovie = new Movie();
            addList = true;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("dirname")) {

            // if "dirname", set tempDirector
            tempDirector = tempVal;

            if (!(tempVal != null && tempVal.trim().length() > 0)) {
                addList = false;
                inconsistent += "Adding to movies table -- Movie with null director: " + tempMovie.toString() + "\n";
            }

        } else if (addList && qName.equalsIgnoreCase("film")) {
            // if "film", setDirector, add it to the list
            tempMovie.setDirector(tempDirector);
            myMovies.add(tempMovie);

        } else if (qName.equalsIgnoreCase("fid")) {
            // if "fid", setId
            tempMovie.setId(tempVal);

        } else if (qName.equalsIgnoreCase("t")) {

            // if "t", setTitle
            if (tempVal != null && tempVal.trim().length() > 0) {
                tempMovie.setTitle(tempVal);
            } else {
                // if null, not add to list
                addList = false;
                inconsistent += "Adding to movies table -- Movie with null title: " + tempMovie.toString() + "\n";
            }

        } else if (qName.equalsIgnoreCase("year")) {

            // if "year", setYear
            if (tempVal != null && tempVal.trim().length() > 0 && !tempVal.equals("0")) {
                try {
                    tempMovie.setYear(Integer.parseInt(tempVal));
                } catch (Exception e) {
                    addList = false;
                    inconsistent += "Adding to movies table -- Movie year cannot parse to int: " + tempMovie.toString() + "\n";
                }
            } else {
                addList = false;
                inconsistent += "Adding to movies table -- Movie with null year: " + tempMovie.toString() + "\n";
            }

        } else if (qName.equalsIgnoreCase("cat")) {
            // if "cats", setGenres
            if (tempVal != null && tempVal.trim().length() > 0) {
                tempMovie.addGenre(genreNameMap.get(tempVal));
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

        PreparedStatement showMaxId = null;
        String query = "Select MAX(id) as id from movies";

        PreparedStatement checkMovie = null;
        String checkQuery = "SELECT * FROM movies as m where m.title = ? and m.director = ? and m.year = ?";

        PreparedStatement preparedQuery = null;
        String insertQuery = "Insert into movies(id, title, director, year) Values(?,?,?,?)";
        int[] iNoRows=null;
        int recordCount = 0;

        PreparedStatement preparedGenre = null;
        String insertGenre = "Insert into genres(name) Values(?)";

        PreparedStatement preparedGenreMovie = null;
        String insertGenreMovie = "INSERT INTO genres_in_movies(genreId, movieId) VALUES(?, ?);";
        int[] iNoRowsg=null;
        int genreCount = 0;

        PreparedStatement loadGenre = null;
        String loadQuery = "SELECT * FROM genres";
        int maxGenreId = 0;

        // load all genres in genres table in memory
        try {
            loadGenre = conn.prepareStatement(loadQuery);
            ResultSet rsl = loadGenre.executeQuery();

            while (rsl.next()) {
                // add to actorMap
                genreMap.put(rsl.getString("name"), rsl.getInt("id"));

                // find max id in genres table
                if (rsl.getInt("id") > maxGenreId) {
                    maxGenreId = rsl.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // find the max id in stars table
        try {
            showMaxId = conn.prepareStatement(query);
            ResultSet rs = showMaxId.executeQuery();

            if (rs.next()){
                int startId = Integer.parseInt(rs.getString("id").substring(2)) + 1;
                System.out.println("Start adding movie data, start movie id: " + startId);

                conn.setAutoCommit(false);

                checkMovie = conn.prepareStatement(checkQuery);
                preparedQuery = conn.prepareStatement(insertQuery);
                preparedGenreMovie = conn.prepareStatement(insertGenreMovie);
                preparedGenre = conn.prepareStatement(insertGenre);

                // iterate all the movies we parsed
                for (int i = 0; i < myMovies.size(); i++) {
                    // get the actor
                    Movie mv = myMovies.get(i);

                    // check whether the movie already exists
                    checkMovie.setString(1, mv.getTitle());
                    checkMovie.setString(2, mv.getDirector());
                    checkMovie.setInt(3, mv.getYear());
                    rs = checkMovie.executeQuery();

                    if (rs.next()) {
                        // if the movie already exists, add existed id into hashmap
                        movieMap.put(mv.getId(), rs.getString("id"));

                        // go to next actor
                        inconsistent += "Adding to movies table -- Movie already exists: " + mv.toString() + "\n";
                        continue;
                    } else {
                        // if not, add the movie in movie table
                        String newId = "tt" + (startId + i);
                        preparedQuery.setString(1, newId);
                        preparedQuery.setString(2, mv.getTitle());
                        preparedQuery.setString(3, mv.getDirector());
                        preparedQuery.setInt(4, mv.getYear());
                        movieMap.put(mv.getId(), newId);

                        preparedQuery.addBatch();
                        recordCount += 1;

                        // add to genres table and the movie genre relationship
                        for (int j = 0; j < mv.getGenres().size(); j++) {
                            genreCount += 1;
                            try{
                                String g = mv.getGenres().get(j);
                                if (!genreMap.containsKey(g)) {
                                    // if the genre not exist in the database, add the genre
                                    preparedGenre.setString(1, g);
                                    preparedGenre.executeUpdate();

                                    // add to genreMap
                                    genreMap.put(g, maxGenreId);
                                    maxGenreId += 1;
                                }
                                preparedGenreMovie.setInt(1, genreMap.get(g));
                                preparedGenreMovie.setString(2, movieMap.get(mv.getId()));
                                preparedGenreMovie.addBatch();
                            } catch (Exception e) {
                                inconsistent += "Adding to genres_in_movies table -- Genre cannot add: " + mv.toString() + "\n";
                            }
                        }

                    }
                }
                System.out.println("adding movie record number: " + recordCount + ", movie map size: " + movieMap.size());
                iNoRows= preparedQuery.executeBatch();
                System.out.println("adding genres_in_movies record number: " + genreCount + ", genre map size: " + genreMap.size());
                iNoRowsg = preparedGenreMovie.executeBatch();
                conn.commit();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // print inconsistent to file
//        try {
//            FileWriter myWriter = new FileWriter("inconsistent_main.txt");
//            myWriter.write(inconsistent);
//            myWriter.close();
//            System.out.println("Successfully wrote inconsistent to the file inconsistent_main.txt.");
//        } catch (IOException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }

        // close query and connection
        try {
            loadGenre.close();
            showMaxId.close();
            checkMovie.close();
            preparedQuery.close();
            preparedGenre.close();
            preparedGenreMovie.close();
            conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public Map<String, String> getMovieMap() {
        return movieMap;
    }

    public String getInconsistent() {
        return inconsistent;
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        SAXParserMain spe = new SAXParserMain();
        spe.runParser();
    }

}
