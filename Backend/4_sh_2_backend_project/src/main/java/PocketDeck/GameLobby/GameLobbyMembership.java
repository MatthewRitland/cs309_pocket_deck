package PocketDeck.GameLobby;

import jakarta.persistence.*;
import PocketDeck.Users.User;

// keeps track of which users are in which game lobby
@Entity
public class GameLobbyMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User gameLobbyMember;

    @Enumerated(EnumType.STRING)
    private GameLobbyPlayerRole playerRole;

    @ManyToOne
    @JoinColumn (nullable = false)
    private GameLobby gameLobby;

    public long getId () {
        return id;
    }

    public User getGameLobbyMember() {
        return gameLobbyMember;
    }

    public void setGameLobbyMember(User gameLobbyMember) {
        this.gameLobbyMember = gameLobbyMember;
    }

    public GameLobbyPlayerRole getPlayerRole() {
        return playerRole;
    }

    public void setPlayerRole(GameLobbyPlayerRole playerRole) {
        this.playerRole = playerRole;
    }

    public GameLobby getGameLobby() {
        return gameLobby;
    }

    public void setGameLobby(GameLobby gameLobby) {
        this.gameLobby = gameLobby;
    }
}
