package PocketDeck.GameLobby;

import PocketDeck.CardGames.CardGame;
import PocketDeck.CardGames.CardGameRepository;
import PocketDeck.Users.User;
import PocketDeck.Users.UserRepository;
import jakarta.validation.OverridesAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class GameLobbyController {
    @Autowired
    UserRepository userRepo;
    @Autowired
    CardGameRepository cardGameRepo;
    @Autowired
    GameLobbyRepository gameLobbyRepo;
    @Autowired
    GameLobbyMembershipRepository gameLobbyMembershipRepo;


    // create a new game lobby
    @PostMapping(path = "/gameLobbies/create/{userId}/{cardGameId}")
    public GameLobby createGameLobby(@PathVariable int userId, @PathVariable int cardGameId) {
        User gameLobbyOwner = userRepo.findById(userId);
        CardGame cardGame = cardGameRepo.findById(cardGameId);

        if (gameLobbyOwner == null || cardGame == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or CardGame not found");
        }

        // make sure user is not already in another lobby
        GameLobbyMembership existingMember = gameLobbyMembershipRepo.findByGameLobbyMemberId(userId);
        if (existingMember != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User must leave their current lobby!");
        }

        GameLobby newLobby = new GameLobby();
        newLobby.setCardGame(cardGame);
        gameLobbyRepo.save(newLobby);

        // set this user as the owner of this newly created party
        GameLobbyMembership ownerMembership = new GameLobbyMembership(gameLobbyOwner, newLobby, GameLobbyMembershipRole.OWNER_MEMBER);
        gameLobbyMembershipRepo.save(ownerMembership);

        return newLobby;
    }


    // return a list of all game lobbies
    @GetMapping (path = "/gameLobbies")
    public List<GameLobby> getGameLobbies() { return gameLobbyRepo.findAll();
    }

    // return a list of all members in a game lobby
    @GetMapping (path = "/gameLobbies/{gameLobbyId}/members")
    public List<GameLobbyMembership> getGameLobbyMembers(@PathVariable int gameLobbyId) {
        return gameLobbyMembershipRepo.findByGameLobbyId(gameLobbyId);
    }


    // changes a lobby from public and private
    @PutMapping (path = "/gameLobbies/{gameLobbyId}/publicity/{userId}")
    public GameLobby flipGameLobbyPublicity(@PathVariable int gameLobbyId, @PathVariable int userId) {
        GameLobbyMembership membership = gameLobbyMembershipRepo.findByGameLobbyMemberId(userId);
        if (membership == null || membership.getGameLobby().getId() != gameLobbyId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a member of this lobby");
        }
        if (membership.getMemberRole() != GameLobbyMembershipRole.OWNER_MEMBER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the lobby owner can change the lobby publicity");
        }
        GameLobby lobby = membership.getGameLobby();
        lobby.setIsInviteOnly(!(lobby.getIsInviteOnly())); // flip the publicity of the lobby
        return gameLobbyRepo.save(lobby);
    }


    // changes a member's readiness status
    @PutMapping("/gamelobbies/ready/{userId}")
    public GameLobbyMembership flipReadyStatus(@PathVariable int userId) {
        GameLobbyMembership member = gameLobbyMembershipRepo.findByGameLobbyMemberId(userId);
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not in a lobby");
        }

        member.setIsReady(!(member.getIsReady())); // flip the readiness of the member
        gameLobbyMembershipRepo.save(member);
        return member;
    }


    // update the cardgame used for the game lobby
    @PutMapping (path = "/gameLobbies/{gameLobbyId}/cardGame/{cardGameId}/{userId}")
    public GameLobby switchGameLobbyCardGame(@PathVariable int gameLobbyId, @PathVariable int cardGameId, @PathVariable int userId) {
        GameLobbyMembership membership = gameLobbyMembershipRepo.findByGameLobbyMemberId(userId);
        if (membership == null || membership.getGameLobby().getId() != gameLobbyId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a member of this lobby");
        }
        if (membership.getMemberRole() != GameLobbyMembershipRole.OWNER_MEMBER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the lobby owner can change the lobby game mode");
        }
        CardGame newCardGame = cardGameRepo.findById(cardGameId);
        if (newCardGame == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The game does not exist");
        }
        GameLobby lobby = membership.getGameLobby();
        lobby.setCardGame(newCardGame); // flip the publicity of the lobby
        return gameLobbyRepo.save(lobby);
    }



    @DeleteMapping(path = "/gameLobbies/leave/{userId}")
    public String leaveLobby(@PathVariable int userId) {
        //TODO
        return "";
    }

}
