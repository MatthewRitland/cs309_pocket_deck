package onetoone;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import onetoone.Users.User;
import onetoone.Users.UserRepository;

/**
 * 
 * @author Vivek Bengre
 * 
 */ 

@SpringBootApplication
@EnableJpaRepositories
class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // Create 3 users with their machines
    /**
     * 
     * @param userRepository repository for the User entity
     * Creates a commandLine runner to enter dummy data into the database
     * As mentioned in User.java just associating the Laptop object with the User will save it into the database because of the CascadeType
     */
    @Bean
    CommandLineRunner initUser(UserRepository userRepository) {
        return args -> {
            /* Testing: creating users works
            User user1 = new User("JohnNew", "johnnew@somemail.com", "123");
            User user2 = new User("JaneNew", "janenew@somemail.com", "456");
            User user3 = new User("JustinNew", "justinnew@somemail.com", "789");



            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);
            */

            /* Testing: deleting users works.
            System.out.println("user count: " + userRepository.count() + "\n");
            userRepository.deleteById(11);
            System.out.println("There are now " + userRepository.count() + " users");

             */

        };
    }

}