package PocketDeck.GameHistory;

import jakarta.persistence.*;
import PocketDeck.Users.User;
import PocketDeck.CardGames.CardGame;

import java.time.Duration;
import java.time.LocalDateTime;


// this class exists to represent an object of a past(and possibly current??) game.
@Entity
public class GameHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    // there are many game history records for one user
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;


    @ManyToOne
    @JoinColumn(nullable = false)
    private CardGame cardGame;

    @Enumerated(EnumType.STRING)
    private GameHistoryResult gameResult; // to see the enumeration "IN_PROGRESS" will probably need to use websockets?

    private LocalDateTime timeGameStarted;
    private LocalDateTime timeGameCompleted;
    private Duration timeGameDuration;

    // TODO
    // should probably have some variable called "game mode" to see the game mode. Should this be done here or
    // on in CardGames? (Such as an enumeration?)

    // called when a game is created, can only initialize the user and time started, must update after game completes
    public GameHistory(User user, CardGame cardGame) {

        this.user = user;
        this.cardGame = cardGame;
        this.gameResult = GameHistoryResult.IN_PROGRESS;
        //this.mode = mode;

        this.timeGameStarted = LocalDateTime.now();


        // can't do these yet since this is called on creation of a game, BUT HERE TO REMEMBER TO UPDATE
        //this.timeGameCompleted = timeCompleted;
        //this.timeGameDuration = Duration.between(timeStart, timeCompleted); // gives the time between start and completion

        //this.gameResult = result;


    }

    // empty constructor for spring to use (spring will use getters and setters)
    public GameHistory() {
    }


    // =============================== Getters and Setters for each field ================================== //

    public int getId() { return this.id; }


    public User getUser() { return this.user; }
    public void setUser(User user) { this.user = user; }

    public CardGame getCardGame() { return this.cardGame; }
    public void setCardGame(CardGame cardGame) { this.cardGame = cardGame; }


    public LocalDateTime getTimeGameStarted() { return this.timeGameStarted; }
    public void setTimeGameStarted(LocalDateTime timeGameStarted) { this.timeGameStarted = timeGameStarted; }


    public LocalDateTime getTimeGameCompleted() { return this.timeGameCompleted; }
    public void setTimeGameCompleted(LocalDateTime timeGameCompleted) { this.timeGameCompleted = timeGameCompleted; }


    public Duration getTimeGameDuration() { return timeGameDuration; }
    public void setTimeGameDuration(Duration timeGameDuration) { this.timeGameDuration = timeGameDuration; }


    public GameHistoryResult getGameResult() { return this.gameResult; }
    public void setGameResult(GameHistoryResult gameResult) { this.gameResult = gameResult; }


}


