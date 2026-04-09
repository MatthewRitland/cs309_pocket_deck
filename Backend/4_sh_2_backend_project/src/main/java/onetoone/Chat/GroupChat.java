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

    // set to true when have "direct message" which is really just a group with max size of 2
    @Column
    private boolean isDirectMessage = false;

    public GroupChat() {
    }

    public GroupChat(String groupName, boolean isDirectMessage) {
        this.groupName = groupName;
        this.isDirectMessage = isDirectMessage;
    }



    // =============================== Getters and Setters for each field ================================== //
    public long getId() { return id; }

    String getGroupName() { return groupName; }
    void setGroupName(String groupName) { this.groupName = groupName; }

    Boolean getIsDirectMessage() { return this.isDirectMessage; }
    void setIsDirectMessage(Boolean isDirectMessage) { this.isDirectMessage = isDirectMessage; }

}
