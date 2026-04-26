package PocketDeck.Requests;
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

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
