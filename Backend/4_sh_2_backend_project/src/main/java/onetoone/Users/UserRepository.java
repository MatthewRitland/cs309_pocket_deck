package onetoone.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


public interface UserRepository extends JpaRepository<User, Integer> {
    User findById(int id);

    // fetch a user by the username
    User findByUsername(String username);

    // returns true or false if username is taken/exists
    boolean existsByUsername(String username);

    @Transactional
    void deleteById(int id);

}
