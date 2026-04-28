package PocketDeck.CardGames;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CardGameController {
    @Autowired
    CardGameRepository cardGameRepository;

    @Operation(summary = "lists all card games", description = "returns a list from the database of all CardGame objects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully returned a list of all CardGames",
            content = {@Content(mediaType="application/json",
                schema = @Schema(implementation = CardGame.class))
            })
    })
    @GetMapping("/cardGames")
    List<CardGame> getAllCardGames () {
        return cardGameRepository.findAll();
    }

    @Operation(summary = "lists one CardGame", description = "returns one CardGame from the databse found via id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully returned a CardGame",
                    content = {@Content(mediaType="application/json",
                            schema = @Schema(implementation = CardGame.class))
                    })
    })
    @GetMapping("/cardGames/{id}")
    CardGame getCardGameById (@Parameter(description = "id of CardGame") @PathVariable int id) {
        return cardGameRepository.findById(id);
    }

    @Operation(summary = "creates a CardGame", description = "saves a CardGame to the database and returns whether is was successful or not")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully added a CardGame to the database",
                    content = {@Content(mediaType="application/json",
                            schema = @Schema(implementation = CardGame.class))
                    })
    })
    @PostMapping("/cardGames")
    String createCardGame (@Parameter(description = "CardGame object to save")@RequestBody CardGame game) {
        if (game == null) {
            return "{\"message\":\"game was not created\"}";
        }
        cardGameRepository.save(game);
        return "{\"message\":\"game was created and saved\"}";
    }

    @Operation(summary = "updates an existing CardGame", description = "updates an existing CardGame from the database, then returns it")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully returned the updated CardGame",
                    content = {@Content(mediaType="application/json",
                            schema = @Schema(implementation = CardGame.class))
                    })
    })
    @PutMapping("/cardGames/{id}")
    CardGame updateCardGame (@Parameter(description = "id of CardGame to update")@PathVariable int id, @Parameter(description = "CardGame object to update with")@RequestBody CardGame game) {
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

    @Operation(summary = "deletes a CardGame", description = "deletes a CardGame from the database, found via id, then returns a message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully deleted a CardGame",
                    content = {@Content(mediaType="application/json",
                            schema = @Schema(implementation = CardGame.class))
                    })
    })
    @DeleteMapping("/cardGames/{id}")
    String deleteCardGame (@Parameter(description = "id of CardGame to delete")@PathVariable int id) {
        cardGameRepository.deleteById(id);
        return "{\"message\":\"game was deleted\"}";
    }
}
