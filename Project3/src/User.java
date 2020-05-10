import java.util.ArrayList;
import java.util.HashMap;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    private String id;
    private String firstName;
    private String lastName;
    private String ccId;
    private String address;
    private String email;
    private String password;
    private String type;

    private HashMap<String, Integer> items; // key is movieTitle, value is quantity
    private HashMap<String, String> movieInfo; // key is movieTitle, value is movieID
    String movieListURL;

    public User(String id, String firstName, String lastName, String ccId, String address, String email, String password, String type) {
        this.id = id;
        this.firstName = firstName;
        this.lastName= lastName;
        this.ccId = ccId;
        this.address = address;
        this.email = email;
        this.password = password;
        this.items = new HashMap<>();
        this.movieInfo = new HashMap<>();
        this.type = type;
    }

    public User(String email, String type){
        this.email = email;
        this.type = type;
    }

//    public User(String username, HashMap<String, Integer> items){
//        this.username = username;
//        this.items = items;
//    }

    public String getUsername() {
        return this.firstName + " " + this.lastName;
    }

    public String getUserId() {
        return this.id;
    }

    public HashMap<String, Integer> getItems() {
        return items;
    }
    public HashMap<String, String> getMovieInfo() {
        return movieInfo;
    }

    public String getType(){return this.type;}

    public void addItem(String item, int quantity){
        synchronized(items){
            if (items.containsKey(item)){
                quantity += items.get(item);
            }
            items.put(item, quantity);
        }
    }

    public void addMovieInfo(String title, String id){
        synchronized(movieInfo){
            movieInfo.put(title, id);
        }
    }

    public void updateItem(String item, int quantity){
        synchronized(items){
            items.put(item, quantity);
        }
    }

    public void removeItem(String item){
        synchronized(items){
            items.remove(item);
        }
    }

    public void removeMovieInfo(String title){
        synchronized(movieInfo){
            movieInfo.remove(title);
        }
    }

    public void clearItems(){
        synchronized(items){
            items = new HashMap<>();
        }
    }

    public void clearMovieInfo(){
        synchronized(items){
            movieInfo = new HashMap<>();
        }
    }

    public void setMovieListURL(String movieListURL) {
        this.movieListURL = movieListURL;
    }

    public String getMovieListURL() {
        return movieListURL;
    }
}
