package onetoone.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


public interface UserRepository extends JpaRepository<User, Integer> {
    User findById(int id);

    // fetch a user by the username
    User findByUserName(String userName);

    // returns true or false if username is taken/exists
    boolean existsByUserName(String userName);

    @Transactional
    void deleteById(int id);
}
