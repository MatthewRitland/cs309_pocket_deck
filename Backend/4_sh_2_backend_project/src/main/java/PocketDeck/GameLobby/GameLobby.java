package PocketDeck.GameLobby;

import jakarta.persistence.*;
import PocketDeck.CardGames.CardGame;


// this class exists to represent a game lobby where users will gather before starting a game
@Entity
public class GameLobby {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // there are many cardgames in the GameLobby table.
    @ManyToOne
    @JoinColumn(nullable = false)
    private CardGame cardGame;

    private boolean isInviteOnly;

    public GameLobby() {
        this.isInviteOnly = true; // will be invite only by default
    }


    // =============================== Getters and Setters for each field ================================== //

    public int getId() { return this.id; }

    public CardGame getCardGame() { return this.cardGame; }
    public void setCardGame(CardGame cardGame) { this.cardGame = cardGame; }

    public boolean getIsInviteOnly() { return this.isInviteOnly; }
    public void setIsInviteOnly(boolean isInviteOnly) { this.isInviteOnly = isInviteOnly; }
}


