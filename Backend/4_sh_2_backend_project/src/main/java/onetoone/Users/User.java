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
    private String userName;
    private String password;


    // ---------temp, look in "UserStatus" for more info as to why (Keeps database from giving error) --------
                                        private boolean if_active = false;
    // ---------temp------------------------------------------------------------------------------------------


    // this is to show if the user is: ONLINE, IN_GAME, SPECTATING, AWAY or OFFLINE,
    // as declared in the UserStatus.java file (public enum UserStatus)
    @Enumerated(EnumType.STRING)
    private UserStatus status;


    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.status = UserStatus.OFFLINE;
    }

    public User() {
    }

    // =============================== Getters and Setters for each field ================================== //

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getUserName(){
        return userName;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password;}

    public UserStatus getUserStatus() {return status;}

    public void setUserStatus(UserStatus status) {this.status = status;}

}


/*
 * ***CAN DELETE THIS COMMENT BLOCK AT ANY TIME***
 * BELOW IS IRRELEVANT AND WHAT WAS REMOVED FROM TUTORIAL, BUT LEFT HERE AS
 * AN EXAMPLE OF WHAT WAS DONE TO HELP LINK 1-1 RELATIONSHIP TABLES:
 *
 * @OneToOne creates a relation between the current entity/table(Laptop) with the entity/table defined below it(User)
 * cascade is responsible propagating all changes, even to children of the class Eg: changes made to laptop within a user object will be reflected
 * in the database (more info : https://www.baeldung.com/jpa-cascade-types)
 * @JoinColumn defines the ownership of the foreign key i.e. the user table will have a field called laptop_id
 * @OneToOne(cascade = CascadeType.ALL)
 * @JoinColumn(name = "laptop_id")
 * private Laptop laptop;
*/
