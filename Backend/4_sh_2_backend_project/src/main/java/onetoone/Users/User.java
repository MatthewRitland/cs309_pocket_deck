package onetoone.Users;

import jakarta.persistence.*;

/*
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
 */

@Entity
public class User {

     /* 
     * The annotation @ID marks the field below as the primary key for the table created by springboot
     * The @GeneratedValue generates a value if not already present,
     * The strategy in this case is to start from 1 and increment for each table
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;



    // this is to show if the user is: ONLINE, IN_GAME, SPECTATING, AWAY or OFFLINE,
    // as declared in the UserStatus.java file (public enum UserStatus)
    @Enumerated(EnumType.STRING)
    private UserStatus status;


    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = UserStatus.OFFLINE;

    }

    public User() {
    }

    // =============================== Getters and Setters for each field ================================== //

    public int getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password;}

    public UserStatus getUserStatus() {return status;}

    public void setUserStatus(UserStatus status) {this.status = status;}


    public Boolean isEqual (User obj) {
            if (obj.getId() == this.getId() && obj.getUsername().equals(this.getUsername()) && obj.getPassword().equals(this.getPassword())) {
                return true;
            }
        return false;
    }

}
