package onetoone.Users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;


    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @GetMapping(path = "/users")
    List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping(path = "/users/{id}")
    User getUserById(@PathVariable int id) {
        return userRepository.findById(id);
    }

    // sign up feature
    @PostMapping(path = "/signup")
    String createUser(@RequestBody User user) {
        // is it a valid request? (is overall request empty? stopped w/ user == null,
        // or username or password is missing.)
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return "{\"message\":\"failure, user doesn't exist, username is invalid, or password is invalid\"}";
        }

        // does this username already have an account?
        if (userRepository.existsByUsername(user.getUsername())) {
            return "{\"message\":\"failure name already in use\" + \"}";
        }
        // since the user was passed here, and NOT created through the constructor,
        // need to assign the status here before saving it to the DB
        user.setUserStatus(UserStatus.OFFLINE);

        // otherwise will be unique and valid, so save the new user
        userRepository.save(user);

        // return a success message, user is now in the database, return the id that user has
        // so frontend can call it after creation.
        return "{\"message\":\"success\", \"userId\":" + user.getId() + "}";
    }

    @PutMapping("/users/{id}")
    User updateUser(@PathVariable int id, @RequestBody User request) {
        User user = userRepository.findById(id);

        // check if user was found/exists
        if (user == null)
            return null;

        // updating the user
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setUserStatus(request.getUserStatus());

        // save the user again (which is now updated)
        userRepository.save(user);

        // stores
        return userRepository.findById(id);
    }


    @DeleteMapping(path = "/users/{id}")
        // delete the user that matches the id
    String deleteUser(@PathVariable int id) {
        userRepository.deleteById(id);
        return success;
    }


    // austin
    @PostMapping("/login")
    loginMessage login(@RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return new loginMessage(false, "Login failed");
        }
        if (user.getPassword().equals(password)) {
            return new loginMessage(true, "Login successful");
        }
        return new loginMessage(false, "Login failed");
    }

    static class loginMessage {
        boolean success;
        String message;

        public loginMessage(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean getSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
