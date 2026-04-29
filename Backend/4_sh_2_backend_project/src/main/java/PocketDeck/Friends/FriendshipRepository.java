package PocketDeck.Friends;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
    Friendship findById(int id);

    // find all of the requests that were sent by one user
    List<Friendship> findByRequesterId(int requesterId);

    // find all requests that were received by one user
    List<Friendship> findByReceiverId(int receiverId);

    // returns true or false, basically checks if there is already a friendship between two users
    boolean existsByRequesterIdAndReceiverId(int requesterId, int receiverId);

    // find a 'Friendship' that exists between requester and receiver Users
    Friendship findByRequesterIdAndReceiverId(int requesterId, int receiverId);

    // used to find all received pending friend requetss for a user
    List<Friendship> findByReceiverIdAndFriendshipStatus(int receiverId, FriendshipStatus status);

    // used to find all friend requests sent by a user
    List<Friendship> findByRequesterIdAndFriendshipStatus(int requesterId, FriendshipStatus status);

    // for accepted friends, where the user may have been the sender or id, AND friendshipStatus is FRIEND (they're friends)
    List<Friendship> findByRequesterIdAndFriendshipStatusOrReceiverIdAndFriendshipStatus(int requesterId, FriendshipStatus status1, int receiverId, FriendshipStatus status2);

    @Transactional
    void deleteById(int id);

    // used to delete friendships before deleting a user (the same user id will be entered for both params)
    @Transactional
    void deleteByRequesterIdOrReceiverId(int requesterId, int receiverId);
}
