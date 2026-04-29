package PocketDeck.GameNotes;

import jakarta.persistence.*;
import PocketDeck.CardGames.CardGame;
import PocketDeck.Users.User;

@Entity
public class GameNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String text;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private CardGame game;

    public GameNote () {}

    public GameNote (String text, User user, CardGame game) {
        this.text = text;
        this.user = user;
        this.game = game;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CardGame getGame() {
        return game;
    }

    public void setGame(CardGame game) {
        this.game = game;
    }

    @Override
    public String toString () {
        return text;
    }

}
