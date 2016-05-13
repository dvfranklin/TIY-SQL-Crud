import java.util.ArrayList;

public class User {

    private String userName;
    private String password;
    private int userId;

    public User(){

    }

    public User(int id, String username, String password){
        this.userId = id;
        this.userName = username;
        this.password = password;
    }

    public User(String username) {
        this.userName = username;
    }

    public User(String username, String password){
        this.userName = username;
        this.password = password;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
