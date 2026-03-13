package onetoone.GameHistory;

import jakarta.persistence.*;
import onetoone.Users.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Entity
public class GameHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private GameHistoryResult gameResult; // to see the enumeration "IN_PROGRESS" will probably need to use websockets?

    private LocalDateTime timeGameStarted;
    private LocalDateTime timeGameCompleted;
    private Duration timeGameDuration;

    // TODO
    // should probably have some variable called "game mode" to see the game mode. Should this be done here or
    // on in CardGames? (Such as an enumeration?)
    public GameHistory(User user, /*CardGameModes mode,*/ LocalDateTime timeStart,
                       LocalDateTime timeCompleted, GameHistoryResult result) {
        this.user = user;
        //this.mode = mode;

        this.timeGameStarted = timeStart;
        this.timeGameCompleted = timeCompleted;
        this.timeGameDuration = Duration.between(timeStart, timeCompleted); // gives the time between start and completion

        this.gameResult = result;


    }

    // empty constructor for spring to use (spring will use getters and setters)
    public GameHistory() {
    }


    // =============================== Getters and Setters for each field ================================== //

    public int getId() { return this.id; }


    public User getUser() { return this.user; }

    
    public LocalDateTime getTimeGameStarted() { return this.timeGameStarted; }
    public void setTimeGameStarted(LocalDateTime timeGameStarted) { this.timeGameStarted = timeGameStarted; }


    public LocalDateTime getTimeGameCompleted() { return this.timeGameCompleted; }
    public void setTimeGameCompleted(LocalDateTime timeGameCompleted) { this.timeGameStarted = timeGameCompleted; }


    public Duration getTimeGameDuration() { return timeGameDuration; }
    public void setTimeGameDuration(Duration timeGameDuration) { this.timeGameDuration = timeGameDuration; }


    public GameHistoryResult getGameResult() { return this.gameResult; }
    public void setGameResult(GameHistoryResult gameResult) { this.gameResult = gameResult; }


}


