package PocketDeck.CardGames;

import PocketDeck.Users.User;
import jakarta.persistence.*;

@Entity
public class CardGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String gameName;
    private int minPlayers;
    private int maxPlayers;
    private int turnTimeLimit;

    public CardGame () {}

    public CardGame (String gameName, int minPlayers, int maxPlayers, int turnTimeLimit) {
        this.gameName = gameName;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.turnTimeLimit = turnTimeLimit;
    }

    public int getId() {
        return id;
    }

    public String getGameName() {
        return gameName;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getTurnTimeLimit() {
        return turnTimeLimit;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setTurnTimeLimit(int turnTimeLimit) {
        this.turnTimeLimit = turnTimeLimit;
    }
    @Override
    public boolean equals (Object obj) {
        if (obj.getClass() == this.getClass()) {
            CardGame temp = (CardGame)obj;
            if (temp.getId() == this.getId() && temp.getGameName().equals(this.getGameName()) && temp.getMaxPlayers() == this.getMaxPlayers() && temp.getMinPlayers() == this.getMinPlayers() && temp.getTurnTimeLimit() == this.getTurnTimeLimit()) {
                return true;
            }
        }
        return false;
    }

}
