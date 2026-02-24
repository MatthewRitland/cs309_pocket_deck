package onetoone.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


public interface UserRepository extends JpaRepository<User, Integer> {
    User findById(int id);

    // fetch a user by the email id to see if they exist
    User findByEmailId(String emailId);

    // returns true or false if email is taken/exists
    boolean existsByEmailId(String emailID);

    // returns true or false if email is taken/exists
    boolean existsByName(String name);
    @Transactional
    void deleteById(int id);
}
