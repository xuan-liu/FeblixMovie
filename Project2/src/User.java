import java.util.HashMap;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    private final String username;
    private HashMap<String, Integer> items;

    public User(String username) {
        this.username = username;
        items = new HashMap<>();
    }

    public User(String username, HashMap<String, Integer> items){
        this.username = username;
        this.items = items;
    }

    public String getUsername() {
        return username;
    }

    public HashMap<String, Integer> getItems() {
        return items;
    }

    public void addItem(String item, int quantity){
        if (items.containsKey(item)){
            items.put(item, items.get(item) + quantity);
        }
    }


}
