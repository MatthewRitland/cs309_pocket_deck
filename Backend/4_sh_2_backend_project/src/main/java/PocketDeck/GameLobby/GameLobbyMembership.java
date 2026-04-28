package PocketDeck.GameLobby;

import jakarta.persistence.*;
import PocketDeck.Users.User;

// keeps track of which users are in which game lobby
@Entity
public class GameLobbyMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User gameLobbyMember;

    @Enumerated(EnumType.STRING)
    private GameLobbyMembershipRole memberRole;

    @ManyToOne
    @JoinColumn (nullable = false)
    private GameLobby gameLobby;

    private Boolean isReady;

    public GameLobbyMembership (User gameLobbyMember, GameLobby gameLobby, GameLobbyMembershipRole memberRole) {
        this.gameLobbyMember = gameLobbyMember;
        this.gameLobby = gameLobby;
        this.memberRole = memberRole;
        this.isReady = false;
    }

    public GameLobbyMembership() {}


    // =============================== Getters and Setters for each field ================================== //

    public int getId() { return this.id; }

    public User getGameLobbyMember() { return this.gameLobbyMember; }
    public void setGameLobbyMember(User gameLobbyMember) { this.gameLobbyMember = gameLobbyMember; }

    public GameLobby getGameLobby() { return this.gameLobby; }
    public void setGameLobby(GameLobby gameLobby) { this.gameLobby = gameLobby; }

    public GameLobbyMembershipRole getMemberRole() { return this.memberRole; }
    public void setMemberRole(GameLobbyMembershipRole memberRole) { this.memberRole = memberRole; }

    public boolean getIsReady() { return this.isReady; }
    public void setIsReady(boolean isReady) { this.isReady = isReady; }
}
