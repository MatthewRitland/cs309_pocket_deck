package onetoone.Friends;

import jakarta.persistence.*;
import onetoone.Users.User;

import java.time.LocalDate;

@Entity
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Friend users User, but User doesn't use Friend (I think???)
    @ManyToOne
    private User requester;

    @ManyToOne
    private User receiver;

    private LocalDate dateBefriended;

    @Enumerated(EnumType.STRING)
    FriendStatus friendStatus;

    public Friend(User requester, User receiver) {
        this.requester = requester;
        this.receiver = receiver;
        this.friendStatus = FriendStatus.PENDING; // won't be reached if empty constructor is used
        this.dateBefriended = LocalDate.now();
    }


    public Friend() {
    }


    // =============================== Getters and Setters for each field ================================== //

    public int getId() {
        return this.id;
    }

    public User getRequester() {
        return this.requester;
    }
    public void setRequester(User requester) {
        this.requester = requester;
    }


    public User getReceiver() {
        return this.receiver;
    }
    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }


    public LocalDate getDateBefriended() {
        return this.dateBefriended;
    }
    public void setDateBefriended(LocalDate dateBefriended) {
        this.dateBefriended = dateBefriended;
    }


    public FriendStatus getFriendStatus() {
        return this.friendStatus;
    }
    public void setFriendStatus(FriendStatus friendStatus) {
        this.friendStatus = friendStatus;
    }


}
