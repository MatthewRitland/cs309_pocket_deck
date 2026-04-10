package onetoone.Chat;

import jakarta.persistence.*;
import onetoone.Users.User;

import java.util.Date;

@Entity
@Table(name = "messages")
public class Message {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn (nullable = false)
    private User user;

    @Lob
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent")
    private Date sent = new Date();

    @ManyToOne
    @JoinColumn (nullable = false)
    private GroupChat groupChat;
	
	public Message() {};
	
	public Message(User user, GroupChat groupChat, String content) {
		this.user = user;
		this.content = content;
        this.groupChat = groupChat;
	}


    // =============================== Getters and Setters for each field ================================== //
    public Long getId() {
        return id;
    }
    //public void setId(Long id) { this.id = id;}

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user= user;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public Date getSent() {
        return sent;
    }
    public void setSent(Date sent) {
        this.sent = sent;
    }

    public GroupChat getGroupChat() { return this.groupChat; }
    public void setGroupChat(GroupChat groupChat) { this.groupChat = groupChat; }
}
