package onetoone.CardGames;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CardGameController {
    @Autowired
    CardGameRepository cardGameRepository;

    @GetMapping("/cardGames")
    List<CardGame> getAllCardGames () {
        return cardGameRepository.findAll();
    }

    @GetMapping("/cardGames/{id}")
    CardGame getCardGameById (@PathVariable int id) {
        return cardGameRepository.findById(id);
    }

    @PostMapping("/cardGames")
    String createCardGame (@RequestBody CardGame game) {
        if (game == null) {
            return "{\"message\":\"game was not created\"}";
        }
        cardGameRepository.save(game);
        return "{\"message\":\"game was created and saved\"}";
    }

    @PutMapping("/cardGames/{id}")
    CardGame updateCardGame (@PathVariable int id, @RequestBody CardGame game) {
        CardGame existingGame = cardGameRepository.findById(id);
        if (existingGame == null) {
            return null;
        }
        existingGame.setGameName(game.getGameName());
        existingGame.setMaxPlayers(game.getMaxPlayers());
        existingGame.setMinPlayers(game.getMinPlayers());
        existingGame.setTurnTimeLimit(game.getTurnTimeLimit());

        cardGameRepository.save(existingGame);

        return cardGameRepository.findById(id);
    }

    @DeleteMapping("/cardGames/{id}")
    String deleteCardGame (@PathVariable int id) {
        cardGameRepository.deleteById(id);
        return "{\"message\":\"game was deleted\"}";
    }
}
