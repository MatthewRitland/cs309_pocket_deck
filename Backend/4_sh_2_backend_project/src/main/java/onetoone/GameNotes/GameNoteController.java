package onetoone.GameNotes;

import java.util.List;

import onetoone.CardGames.CardGameRepository;
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

    @GetMapping("/gameNotes/{userId}/{gameId}")
    public List<GameNote> getAllNotesForUserAndGame (@PathVariable int userId, @PathVariable int gameId) {
        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "could not find user");
        }
        if (!cardGameRepo.existsById(gameId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "could not find game");
        }
        return gameNoteRepo.findByUserIdAndGameId(userId, gameId);
    }

    @GetMapping("/gameNotes/{userId}")
    public List<GameNote> getAllNotesForUser (@PathVariable int userId) {
        if (!userRepo.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "could not find user");
        }
        return gameNoteRepo.findByUserId(userId);
    }

    @PostMapping("/gameNotes/{userId}/{gameId}")
    public GameNote createGameNote (@PathVariable int userId, @RequestBody String note, @PathVariable int gameId) {
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

    @PutMapping ("/gameNotes/{id}")
    public GameNote updateGameNote (@PathVariable int id, @RequestBody String text) {
        if (!gameNoteRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "could not find note");
        }
        GameNote note = gameNoteRepo.findById(id);
        note.setText(text);
        gameNoteRepo.save(note);
        return note;
    }

    @DeleteMapping ("/gameNotes/{id}")
    public String deleteGameNote (@PathVariable int id) {
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
