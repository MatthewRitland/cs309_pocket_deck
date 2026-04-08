package onetoone.GameHistory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import onetoone.Friends.Friendship;
import onetoone.Friends.FriendshipRepository;
import onetoone.Users.User;
import onetoone.Users.UserRepository;

import onetoone.CardGames.CardGame;
import onetoone.CardGames.CardGameRepository;

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

    @Autowired
    UserRepository userRepository;

    @Autowired
    CardGameRepository cardGameRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";



    @Operation(summary = "Creates a user's game history record", description = "Creates and stores a user's game history record belonging to the database")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully created game history record",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameHistory.class))
                    }),
            @ApiResponse(responseCode = "400", description = "given user was null",
                    content = @Content),
    })
    @PostMapping(path = "/users/gamehistory/{userId}")
    GameHistory createGameRecord(@Parameter(description = "id of user the created game record belongs to")@PathVariable int userId,
                                 @Parameter(description = "id of card game played")@PathVariable int cardGameId) {

        User user = userRepository.findById(userId);

        if(user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "could not find user");
        }

        CardGame cardGame = cardGameRepository.findById(cardGameId);
        if(cardGame == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "could not find card game");
        }

        GameHistory record = new GameHistory(user, cardGame);

        return gameHistoryRepository.save(record);
    }


    @Operation(summary = "Gets a list of game history records of a user", description = "Returns a complete list of a user' game history records from the database")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully returned a list of a user's game history records",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameHistory.class))
                    }),
            @ApiResponse(responseCode = "400", description = "given user was null",
                    content = @Content),
    })
    @GetMapping(path = "/users/gamehistory/{userId}") //same path, but different request/operation
    List<GameHistory> getGameRecords(@Parameter(description = "user id that the game record belongs to")@PathVariable int userId) {

        User user = userRepository.findById(userId);

        if(user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "could not find user");
        }
        return gameHistoryRepository.findByUserId(userId);
    }


    @Operation(summary = "Gets a single game history record of a user", description = "Returns a user's complete game history record from the database")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully returned a user's single game history record",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameHistory.class))
                    }),
            @ApiResponse(responseCode = "400", description = "given user was null",
                    content = @Content),
    })
    @GetMapping(path = "/users/specific/gamehistory/{userId}/{gameHistoryId}")
    GameHistory getGameRecord(@Parameter(description = "user id that the game record belongs to")@PathVariable int userId,
                              @Parameter(description = "game record id that will be read")@PathVariable int gameHistoryId) {

        User user = userRepository.findById(userId);

        if(user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "could not find user");
        }
        return gameHistoryRepository.findByUserIdAndId(userId, gameHistoryId);
    }


    @Operation(summary = "Updates a specific game history record of a user", description = "Updates the game history record for that user")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully updated a user's single game history record",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameHistory.class))
                    }),
            @ApiResponse(responseCode = "400", description = "given user or given game record was null",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "given game record does not belong to the user",
                    content = @Content),
    })
    @PutMapping(path = "/users/gamehistory/update/{userId}/{gameHistoryId}")
    GameHistory updateGameRecord(@Parameter(description = "user id that the game record belongs to")@PathVariable int userId,
                                 @Parameter(description = "game record id that will be updated")@PathVariable int gameHistoryId,
                                 @Parameter(description = "new game record id that will replace the old one")@RequestBody GameHistory updateRequest) {


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


    @Operation(summary = "Deletes a game history record", description = "Removes a game history record from the database by its id")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully deleted game history record",
                    content = { @Content(mediaType = "text/plain")}),
            @ApiResponse(responseCode = "404", description = "game record by that id not found",
                    content = @Content),
    })
    @DeleteMapping(path="/users/gamehistory/{gameHistoryId}")
    String deleteGameHistory(@Parameter(description = "id of gameHistory record to delete")@PathVariable int gameHistoryId) {
        GameHistory record = gameHistoryRepository.findById(gameHistoryId);

        // if game record not found, throw not found excpetion
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "game record not found");
        }

        gameHistoryRepository.deleteById(gameHistoryId);
        return success;
    }
}
