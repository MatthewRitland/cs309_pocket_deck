package onetoone.Chat;

import jakarta.persistence.*;

// represents the chatroom itself
@Entity
public class GroupChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String groupName;


    public GroupChat() {
    }

    public GroupChat(String groupName) {
        this.groupName = groupName;
    }



    // =============================== Getters and Setters for each field ================================== //
    public long getId() { return id; }

    public String getGroupChatName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
}
