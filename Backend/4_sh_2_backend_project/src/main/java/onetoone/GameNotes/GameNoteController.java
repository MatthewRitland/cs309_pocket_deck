package onetoone.GameNotes;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import onetoone.CardGames.CardGameRepository;
import onetoone.GameHistory.GameHistory;
import onetoone.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class GameNoteController {

    @Autowired
    GameNoteRepository gameNoteRepo;

    @Autowired
    CardGameRepository cardGameRepo;

    @Autowired
    UserRepository userRepo;

    @Operation(summary = "Gets all notes based on a user and a game", description = "Retrieves a list of GameNotes based on a user id and a game id from the dataase")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved a list of GameNotes",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameNote.class))
                    }),
            @ApiResponse(responseCode = "404", description = "could not find user or game",
                    content = @Content),
    })
    @GetMapping("/gameNotes/{userId}/{gameId}")
    public List<GameNote> getAllNotesForUserAndGame (@Parameter (description = "id of a user")@PathVariable int userId, @Parameter(description = "id of a game")@PathVariable int gameId) {
        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "could not find user");
        }
        if (!cardGameRepo.existsById(gameId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "could not find game");
        }
        return gameNoteRepo.findByUserIdAndGameId(userId, gameId);
    }

    @Operation(summary = "Gets a list of notes based on an user", description = "Retrieves a list of GameNotes from the database based on an user id")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved a list of GameNotes",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameNote.class))
                    }),
            @ApiResponse(responseCode = "404", description = "could not find user",
                    content = @Content),
    })
    @GetMapping("/gameNotes/{userId}")
    public List<GameNote> getAllNotesForUser (@Parameter(description = "id of user")@PathVariable int userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "could not find user");
        }
        return gameNoteRepo.findByUserId(userId);
    }

    @Operation(summary = "Creates a game note for a user for a game", description = "Creates and stores a user's game note for a specific game into the database")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully created GameNote and stored it",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameNote.class))
                    }),
            @ApiResponse(responseCode = "404", description = "could not find user or game",
                    content = @Content),
    })
    @PostMapping("/gameNotes/{userId}/{gameId}")
    public GameNote createGameNote (@Parameter(description = "id of user")@PathVariable int userId, @Parameter(description = "text to create note with")@RequestBody String note, @Parameter(description = "id of game")@PathVariable int gameId) {
        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "could not find user");
        }
        if (!cardGameRepo.existsById(gameId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "could not find game");
        }
        GameNote sticky = new GameNote();
        sticky.setUser(userRepo.findById(userId));
        sticky.setGame(cardGameRepo.findById(gameId));
        sticky.setText(note);
        gameNoteRepo.save(sticky);
        return sticky;
    }

    @Operation(summary = "Updates a user's game note", description = "finds a user's game note from the database and updates it.")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully updated the GameNote",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameNote.class))
                    }),
            @ApiResponse(responseCode = "404", description = "could not find game note",
                    content = @Content),
    })
    @PutMapping ("/gameNotes/{id}")
    public GameNote updateGameNote (@Parameter(description = "id of game note")@PathVariable int id, @Parameter(description = "text used to update text of game note")@RequestBody String text) {
        if (!gameNoteRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "could not find note");
        }
        GameNote note = gameNoteRepo.findById(id);
        note.setText(text);
        gameNoteRepo.save(note);
        return note;
    }
    @Operation(summary = "Deletes a user's game note", description = "Finds a game note from the database and deletes it.")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the game note",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameNote.class))
                    }),
            @ApiResponse(responseCode = "404", description = "could not find game note",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "game note was not deleted",
                    content = @Content),
    })
    @DeleteMapping ("/gameNotes/{id}")
    public String deleteGameNote (@Parameter(description = "id of game note")@PathVariable int id) {
        if (!gameNoteRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "could not find note");
        }
        gameNoteRepo.deleteById(id);
        if (gameNoteRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "note was not deleted");
        }
        return "{\"message\":\"note was successfully deleted\"}";
    }
}
