package onetoone.GameLobby;

import jakarta.persistence.*;
import onetoone.Users.User;
import onetoone.CardGames.CardGame;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


// this class exists to represent a game lobby where users will gather before starting a game
@Entity
public class GameLobby {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    // there are many users in one game lobby
    //@OneToMany
    //@JoinColumn(nullable = false)
    //todo should be a group membership like in messaging?

    // there is one card game for one game lobby
    @OneToOne
    @JoinColumn(nullable = false)
    private CardGame cardGame;

    private boolean isInviteOnly;

    public GameLobby() {
        // figure out rest of logic.
    }


    // =============================== Getters and Setters for each field ================================== //

    public int getId() { return this.id; }
}


