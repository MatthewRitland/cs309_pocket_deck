package onetoone.Friends;

import onetoone.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface FriendRepository extends JpaRepository<User, Integer> {
    Friend findById(int id);

    // fetch a friend by their requester id
    Friend findByRequesterid(int requesterid);

    // returns true or false re
    boolean existsByRequesterid(int requesterid);

    @Transactional
    void deleteById(int id);

}
