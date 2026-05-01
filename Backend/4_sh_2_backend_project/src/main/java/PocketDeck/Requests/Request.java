package PocketDeck.Requests;
import PocketDeck.GameLobby.GameLobby;
import jakarta.persistence.*;
import PocketDeck.Users.User;

@Entity
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User requester;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User requested;

    // being used for game lobby invites
    @ManyToOne
    @JoinColumn(nullable = false) // can be null if being used for non-gameLobby things later (change to Integer)
    private GameLobby gameLobby;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public int getId(){
        return id;
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public User getRequested() {
        return requested;
    }

    public void setRequested(User requested) {
        this.requested = requested;
    }

    public GameLobby getGameLobby() { return gameLobby; }

    public void setGameLobby(GameLobby gameLobby) { this.gameLobby = gameLobby; }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
