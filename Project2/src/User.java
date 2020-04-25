import java.util.HashMap;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    private final String username;
    private HashMap<String, Integer> items;
    String movieListURL;

    public User(String username) {
        this.username = username;
        items = new HashMap<>();

    }

//    public User(String username, HashMap<String, Integer> items){
//        this.username = username;
//        this.items = items;
//    }

    public String getUsername() {
        return username;
    }

    public HashMap<String, Integer> getItems() {
        return items;
    }

    public void addItem(String item, int quantity){
        synchronized(items){
            if (items.containsKey(item)){
                quantity += items.get(item);
            }
            items.put(item, quantity);
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

    public void clearItems(){
        synchronized(items){
            items = new HashMap<>();
        }
    }

    public void setMovieListURL(String movieListURL) {
        this.movieListURL = movieListURL;
    }

    public String getMovieListURL() {
        return movieListURL;
    }
}
