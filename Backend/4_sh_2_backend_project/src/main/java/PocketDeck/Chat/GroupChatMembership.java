package PocketDeck.Chat;

// should involve the membship status of users. Need to figure out how to
// get websockets into our backend folder without breaking stuff first, though.

import jakarta.persistence.*;
import PocketDeck.Users.User;

//connects Users to a specific group
@Entity
public class GroupChatMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn (nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn (nullable = false)
    GroupChat groupChat;


    public GroupChatMembership() {}


    // =============================== Getters and Setters for each field ================================== //
    public Long getId() { return this.id; }

    public User getUser() { return this.user; }
    public void setUser(User user) { this.user = user; }

    public GroupChat getGroupChat() { return this.groupChat; }
    public void setGroupChat(GroupChat groupChat) { this.groupChat = groupChat; }

}
