package PocketDeck.Friends;

import jakarta.persistence.*;
import PocketDeck.Users.User;

import java.time.LocalDate;

/* DO NOT THINK OF THIS AS A PERSON, THINK OF IT AS THE SOCIAL CONSTRUCT, i.e. WHAT A 'FRIENDSHIP' IS!*/
@Entity
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // joincolumn specifies there is a foreign key, and allows me to define that the key CAN NOT be null,
    // since there must be at least 2 entities (users) for a relationship/friendship
    @ManyToOne
    @JoinColumn(nullable = false)
    private User requester;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User receiver;

    private LocalDate dateBefriended;

    @Enumerated(EnumType.STRING)
    FriendshipStatus friendshipStatus;

    public Friendship(User requester, User receiver) {
        this.requester = requester;
        this.receiver = receiver;
        this.friendshipStatus = FriendshipStatus.PENDING; // won't be reached if empty constructor is used
    }


    public Friendship() {
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


    public FriendshipStatus getFriendshipStatus() {
        return this.friendshipStatus;
    }

    public void setFriendshipStatus(FriendshipStatus friendshipStatus) {
        this.friendshipStatus = friendshipStatus;
    }
}
