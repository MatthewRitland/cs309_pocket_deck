package onetoone.GameLobby;

import jakarta.persistence.*;
import onetoone.Chat.GroupChat;
import onetoone.Users.User;

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
}
