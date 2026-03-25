package onetoone.GameHistory;

import onetoone.Friends.Friendship;
import onetoone.Friends.FriendshipRepository;
import onetoone.Users.User;
import onetoone.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class GameHistoryController {
    @Autowired
    GameHistoryRepository gameHistoryRepository;

    @Autowired UserRepository userRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";



    @PostMapping(path = "users/gamehistory/{userId}")
    GameHistory createGameRecord(@PathVariable int userId) {
        User user = userRepository.findById(userId);

        if(user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "could not find user");
        }
        GameHistory record = new GameHistory(user);

        return gameHistoryRepository.save(record);
    }


    @GetMapping(path = "users/gamehistory/{userId}") //same path, but different request/operation
    List<GameHistory> getGameRecords(@PathVariable int userId) {
        User user = userRepository.findById(userId);

        if(user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "could not find user");
        }
        return gameHistoryRepository.findByUserId(userId);
    }


    @GetMapping(path = "users/specific/gamehistory/{userId}/{gameHistoryId}")
    GameHistory getGameRecord(@PathVariable int userId, @PathVariable int gameHistoryId) {
        User user = userRepository.findById(userId);

        if(user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "could not find user");
        }
        return gameHistoryRepository.findByUserIdAndId(userId, gameHistoryId);
    }


    @PutMapping(path = "users/gamehistory/update/{userId}/{gameHistoryId}")
    GameHistory updateGameRecord(@PathVariable int userId, @PathVariable int gameHistoryId,
                                 @RequestBody GameHistory updateRequest) {

        User user = userRepository.findById(userId);

        // make sure that user was found
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "could not find user");
        }

        GameHistory record = gameHistoryRepository.findById(gameHistoryId);
        // make sure that pre-existing record is found
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "game record does not exist");
        }

        // make sure that the user of the record and the given user match
        if (record.getUser().getId() != userId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "game record does not belong to this user");
        }

        // NOW we can set the time completed, as well as the game result which will be given
        // use Java's 'Duration' to get the duration of the game between start and completion
        record.setGameResult(updateRequest.getGameResult());
        record.setTimeGameCompleted(LocalDateTime.now());
        record.setTimeGameDuration(Duration.between(record.getTimeGameStarted(), record.getTimeGameCompleted()));

        return gameHistoryRepository.save(record);
    }

    @DeleteMapping(path="/users/gamehistory/{gameHistoryId}")
    String removeFriendship(@PathVariable int gameHistoryId) {
        GameHistory record = gameHistoryRepository.findById(gameHistoryId);

        // if game record not found, throw not found excpetion
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "game record not found");
        }

        gameHistoryRepository.deleteById(gameHistoryId);
        return success;
    }
}
