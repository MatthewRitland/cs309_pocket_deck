package onetoone.Friends;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import onetoone.GameHistory.GameHistory;
import onetoone.Users.User;

import onetoone.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
public class FriendshipController {

    @Autowired
    FriendshipRepository friendshipRepository;

    @Autowired
    UserRepository userRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";



    @Operation(summary = "Sends a friend request to a user", description = "creates and stores a friendship object between users")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully creates and stores a new friendship object between two users",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Friendship.class))
                    }),
            @ApiResponse(responseCode = "400", description = "given user was both requester and reciever",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "given requester or receiver was not found",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "there is already a friendship between these two users",
                    content = @Content),
    })
    @PostMapping(path = "/friendships/request/{requesterId}/{receiverId}")
    Friendship sendFriendshipRequest(@Parameter(description = "id of the requesting user")@PathVariable int requesterId,
                                     @Parameter(description = "id of the receiving user")@PathVariable int receiverId) {

        // check that both given ids are not the same
        if (requesterId == receiverId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't friend same user");
        }

        User requester = userRepository.findById(requesterId);
        User receiver = userRepository.findById(receiverId);

        // make sure that both users exist, if not, then throw an exception
        if (requester == null || receiver == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requester, Receiver, or both users not found");
        }

        // check to see if friendship already exists, if so, throw an exception (A to B)
        if (friendshipRepository.existsByRequesterIdAndReceiverId(requesterId, receiverId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Friendship already exists");
        }

        // make sure B to A doesn't already exist, if so then just accept the request
        if (friendshipRepository.findByRequesterIdAndReceiverId(receiverId, requesterId) != null) {
            Friendship friendship = friendshipRepository.findByRequesterIdAndReceiverId(receiverId, requesterId);
            friendship.setFriendshipStatus(FriendshipStatus.FRIEND);
            friendship.setDateBefriended(LocalDate.now());

            return friendshipRepository.save(friendship);
        }

        Friendship friendship = new Friendship(requester, receiver);
        return friendshipRepository.save(friendship);
    }


    @Operation(summary = "Lists of ALL users' friendships (for testing)", description = "returns a list of all friendship objects in the database")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully returned a list of all friendship objects",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Friendship.class))
                    })
    })
    // useful for testing/showing all friendships (pending+friends) (not sure about anything else tho)
    @GetMapping(path = "/friendships")
    List <Friendship> getAllFriendships() {
        return friendshipRepository.findAll();
    }


    @Operation(summary = "Lists of a user's friendships", description = "returns a list from the database of all friendship objects that belong to a user")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully returned a list of a user's friendships",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Friendship.class))
                    })
    })
    // gets and returns all friendships for a user (no matter friendship status)
    @GetMapping(path = "/friendships/received/{userId}")
    List <Friendship> getReceivedRequests(@Parameter(description = "id of the user who's list of friendships will be given") @PathVariable int userId) {
        return friendshipRepository.findByReceiverId(userId);
    }


    @Operation(summary = "Accepts a friend request, making two users friends", description = "Updates the friendship object between two users, and saves it to the database.")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully updated a friendship object between users",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Friendship.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Friendship object was not found",
                    content = @Content),
    })
    // accepts a friendship by updating friendshipStatus + date accordingly, and returns the updated friendship object
    @PutMapping(path="/friendships/accept/{friendshipId}")
    Friendship acceptFriendshipRequest(@Parameter(description = "id of the friendship object that will be updated")@PathVariable int friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId);

        // if friendshop not found, throw not found excpetion
        if (friendship == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friendship not found");
        }

        // set the friendstatus between the two users to 'FRIEND' and set the date they became friends
        friendship.setFriendshipStatus(FriendshipStatus.FRIEND);
        friendship.setDateBefriended(LocalDate.now());

        return friendshipRepository.save(friendship);
    }


    /*
    // TODO: implement an endpoint for blocking
    // may want to consider if we even want this, as it turned out to be harder than I thought, and doesn't yield
    // too much for the app...    (do we want PENDING, FRIEND, BLOCKED, or just PENDING, FRIEND


     */


    @Operation(summary = "Deletes/Unfriends a friendship between two users", description = "Removes a friendship object from the database between two users")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the friendship object",
                    content = { @Content(mediaType = "text/plain"),
                    }),
            @ApiResponse(responseCode = "404", description = "given friendship object was not found",
                    content = @Content),
    })
    // deletes the friendship (unfriends two users)
    @DeleteMapping(path="/friendships/{friendshipId}")
    String removeFriendship(@Parameter(description = "id of the friendship object that will be deleted")@PathVariable int friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId);

        // if friendship not found, throw not found excpetion
        if (friendship == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friendship not found");
        }

        // delete the friendship
        // TODO: this should probably also clean up/do some other things (cascading?)
        //
        //
        // once ready, then finally delete
        friendshipRepository.deleteById(friendshipId);
        return success;
    }



}
