package onetoone.Friends;
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



    @PostMapping(path = "friendships/request/{requesterId}/{receiverId}")
    Friendship sendFriendshipRequest(@PathVariable int requesterId, @PathVariable int receiverId) {

        // check that both given ids are not the same
        if (requesterId == receiverId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't friend same user");
        }

        User requester = userRepository.findById(requesterId);
        User receiver = userRepository.findById(receiverId);

        // make sure that both users exist, if not, then throw an exception
        if (requester == null || receiver == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friendship not found");
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


    // useful for testing/showing all friendships (pending+friends) (not sure about anything else tho)
    @GetMapping(path = "/friendships")
    List <Friendship> getAllFriendships() {
        return friendshipRepository.findAll();
    }

    // gets and returns all friendships for a user (no matter friendship status)
    @GetMapping(path = "/friendships/received/{userId}")
    List <Friendship> getReceivedRequests(@PathVariable int userId) {
        return friendshipRepository.findByReceiverId(userId);
    }


    // accepts a friendship by updating friendshipStatus + date accordingly, and returns the updated friendship object
    @PutMapping(path="/friendships/accept/{friendshipId}")
    Friendship acceptFriendshipRequest(@PathVariable int friendshipId) {
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


    // deletes the friendship (unfriends two users)
    @DeleteMapping(path="/friendships/{friendshipId}")
    String removeFriendship(@PathVariable int friendshipId) {
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
