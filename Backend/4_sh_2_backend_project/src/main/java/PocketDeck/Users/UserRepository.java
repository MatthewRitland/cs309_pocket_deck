package PocketDeck.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


public interface UserRepository extends JpaRepository<User, Integer> {
    User findById(int id);

    // fetch a user by the username
    User findByUsername(String username);

    // returns true or false if username is taken/exists
    boolean existsByUsername(String username);

    // looks to see if there exists a username that DOES NOT have the given id
    boolean existsByUsernameAndIdNot(String username, int id);

    @Transactional
    void deleteById(int id);

}


/* Spring Data JPA follows the format: (with appropriate Capitalization)

[action prefix] + [property] +      [keyword]         + [glue] + ...(repeat)...
   existsBy        username            NOT               AND
   findBy          password        "" = Is = Equals      OR
   deleteBy        status            LessThan
   countBy          ...              IsNull
     ...                           IgnoreCase
                                     ^...^
*/