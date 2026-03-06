package onetoone.Friends;

import onetoone.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<User, Integer> {
    Friendship findById(int id);

    // find all of the requests that were sent by one user
    List<Friendship> findByRequesterId(int requesterId);

    // find all requests that were received by one user
    List<Friendship> findByReceiverId(int receiverId);

    // returns true or false, basically checks if there is already a friendship between two users
    boolean existsByRequesterIdAndReceiverId(int requesterId, int receiverId);

    // find a 'Friendship' that exists between requester and receiver Users
    Friendship findByRequesterIdAndReceiverId(int requesterId, int receiverId);
    
    @Transactional
    void deleteById(int id);

}
