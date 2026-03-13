package onetoone.GameHistory;

import jakarta.persistence.*;
import onetoone.Users.User;

@Entity
public class GameHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(nullable = false)
    private User user;


    // =============================== Getters and Setters for each field ================================== //

}


