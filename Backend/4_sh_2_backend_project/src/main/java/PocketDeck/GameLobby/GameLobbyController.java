package PocketDeck.GameLobby;

import PocketDeck.CardGames.CardGame;
import PocketDeck.CardGames.CardGameRepository;
import PocketDeck.Requests.Request;
import PocketDeck.Requests.RequestRepository;
import PocketDeck.Users.User;
import PocketDeck.Users.UserRepository;
import jakarta.validation.OverridesAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class GameLobbyController {
    @Autowired
    UserRepository userRepo;
    @Autowired
    CardGameRepository cardGameRepo;
    @Autowired
    GameLobbyRepository gameLobbyRepo;
    @Autowired
    GameLobbyMembershipRepository gameLobbyMembershipRepo;
    @Autowired
    RequestRepository requestRepo;


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


    @PostMapping(path = "/gameLobbies/{gameLobbyId}/start/{userId}")
    public String startGame (@PathVariable int gameLobbyId, @PathVariable int userId) {
        GameLobbyMembership membership = gameLobbyMembershipRepo.findByGameLobbyMemberId(userId);

        if(membership == null || membership.getGameLobby().getId() != gameLobbyId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a member of this lobby");
        }
        if(membership.getMemberRole() != GameLobbyMembershipRole.OWNER_MEMBER) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only the lobby owner can start the game");
        }

        List<GameLobbyMembership> memberships = gameLobbyMembershipRepo.findByGameLobbyId(gameLobbyId);
        for (GameLobbyMembership foundMembership : memberships ) {
            if (foundMembership.getIsReady()) continue;
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All members must be ready to start the game");

        }

        // can be used by frontend to know when to switch screens. Route all players to game screen
        // DOESN'T ACUTALLY START THE GAME. That is done in "Game" class. This is just useful for changing the screen
        // to the blackjack screen.
        GameLobbySocket.broadcastToLobby(gameLobbyId, "{\"type\":\"GAME_START\", \"lobbyId\":" + gameLobbyId + "}");
        return "{\"message\":\"Game has started\"}";
    }


    // return a list of all game lobbies
    @GetMapping (path = "/gameLobbies")
    public List<GameLobby> getGameLobbies() { return gameLobbyRepo.findAll();
    }

    // return a list of all members in a game lobby
    @GetMapping (path = "/gameLobbies/{gameLobbyId}/members")
    public List<GameLobbyMembership> getGameLobbyMembers(@PathVariable int gameLobbyId) {
        if (gameLobbyRepo.findById(gameLobbyId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game lobby does not exist");
        }
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
        GameLobby gameLobby = membership.getGameLobby();
        gameLobby.setIsInviteOnly(!(gameLobby.getIsInviteOnly())); // flip the publicity of the lobby
        gameLobbyRepo.save(gameLobby);
        GameLobbySocket.broadcastToLobby(membership.getGameLobby().getId(), "{\"type\":\"LOBBY_UPDATE\", \"lobbyId\":" + membership.getGameLobby().getId() + "}");

        return gameLobby;
    }


    // changes a member's readiness status
    @PutMapping("/gameLobbies/ready/{userId}")
    public GameLobbyMembership flipReadyStatus(@PathVariable int userId) {
        GameLobbyMembership membership = gameLobbyMembershipRepo.findByGameLobbyMemberId(userId);
        if (membership == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not in a lobby");
        }

        membership.setIsReady(!(membership.getIsReady())); // flip the readiness of the member
        gameLobbyMembershipRepo.save(membership);

        GameLobbySocket.broadcastToLobby(membership.getGameLobby().getId(), "{\"type\":\"LOBBY_UPDATE\", \"lobbyId\":" + membership.getGameLobby().getId() + "}");
        return membership;
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
        GameLobby gameLobby = membership.getGameLobby();
        gameLobby.setCardGame(newCardGame); // flip the publicity of the lobby

        gameLobbyRepo.save(gameLobby);
        GameLobbySocket.broadcastToLobby(membership.getGameLobby().getId(), "{\"type\":\"LOBBY_UPDATE\", \"lobbyId\":" + membership.getGameLobby().getId() + "}");

        return gameLobby;
    }


    // remove a user from a lobby (and a user should only be able to be a member of one lobby at a time)
    @DeleteMapping(path = "/gameLobbies/leave/{userId}")
    public String leaveLobby(@PathVariable int userId) {
        GameLobbyMembership membership = gameLobbyMembershipRepo.findByGameLobbyMemberId(userId);
        if (membership == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a member of a lobby");
        }

        // since a member can only be a user of ONE lobby at a time, delete all of their "sent" requests
        // if they aren't in that lobby anymore
        List<Request> sentRequests = requestRepo.findByRequesterIdAndGameLobbyId(
                membership.getGameLobbyMember().getId(),
                membership.getGameLobby().getId());

        for (Request foundSentRequest : sentRequests) {
            requestRepo.deleteById(foundSentRequest.getId());
        }

        // delete requests received by member (that were accepted)
        List<Request> receivedRequests = requestRepo.findByRequestedIdAndGameLobbyId(userId, membership.getGameLobby().getId());
        for (Request foundReceivedRequest : receivedRequests) {
            requestRepo.deleteById(foundReceivedRequest.getId());
        }

        // if there is only 1 member left, then delete the game lobby AND its requests
        if (gameLobbyMembershipRepo.countByGameLobbyId(membership.getGameLobby().getId()) <= 1) {

            // delete ALL requests left for that game lobby
            List<Request> allLobbyRequests = requestRepo.findByGameLobbyId(membership.getGameLobby().getId());
            for (Request leftoverRequest : allLobbyRequests) {
                requestRepo.deleteById(leftoverRequest.getId());
            }

            gameLobbyMembershipRepo.deleteByGameLobbyMemberId(userId);
            gameLobbyRepo.deleteById(membership.getGameLobby().getId());
            return "{\"message\":\"success\"}";
        }

        // if this member was the owner and there's more members, need to assign another member as the owner
        if (membership.getMemberRole() == GameLobbyMembershipRole.OWNER_MEMBER) {
            List<GameLobbyMembership> memberships = gameLobbyMembershipRepo.
                    findByGameLobbyId(membership.getGameLobby().getId());

            for(GameLobbyMembership foundMembership : memberships) {
                if (foundMembership.getGameLobbyMember().getId() == (membership.getGameLobbyMember().getId())) {
                    continue;
                }
                foundMembership.setMemberRole(GameLobbyMembershipRole.OWNER_MEMBER);
                gameLobbyMembershipRepo.save(foundMembership);
                break;
            }
        }
        gameLobbyMembershipRepo.deleteByGameLobbyMemberId(userId);
        GameLobbySocket.broadcastToLobby(membership.getGameLobby().getId(), "{\"type\":\"LOBBY_UPDATE\", \"lobbyId\":" + membership.getGameLobby().getId() + "}");

        return "{\"message\":\"success\"}";

    }

}
