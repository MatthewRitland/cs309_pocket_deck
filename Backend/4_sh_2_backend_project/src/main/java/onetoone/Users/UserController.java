package onetoone.Users;

import java.util.List;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


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
        User user = userRepository.findById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    // sign up feature
    @PostMapping(path = "/signup")
    User createUser(@RequestBody User user) {
        // is it a valid request? (is overall request empty? stopped w/ user == null,
        // or username or password is missing.)
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username or password is invalid");
        }

        // does this username already have an account?
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already in use");
        }
        // since the user was passed here, and NOT created through the constructor,
        // need to assign the status here before saving it to the DB
        user.setUserStatus(UserStatus.OFFLINE);

        // otherwise will be unique and valid, so save the new user
        userRepository.save(user);

        // return a success message, user is now in the database, return the id that user has
        // so frontend can call it after creation.
        return user;
    }

    @PutMapping("/users/{id}")
    User updateUser(@PathVariable int id, @RequestBody User request) {
        User user = userRepository.findById(id);

        // check if user was found by id, and if not throw an exception. (don't want to update a non-existent user)
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        // if username already exists for a user with a separate id, then throw an exception
        else if (userRepository.existsByUsernameAndIdNot(request.getUsername(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already in use");
        }

        // updating the user
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        if (request.getUserStatus() != null) {
            user.setUserStatus(request.getUserStatus());
        }

        // save the user to the database
        userRepository.save(user);

        // finally, return the updated user object to frontend
        return user;
    }


    @DeleteMapping(path = "/users/{id}")
        // delete the user that matches the id
    String deleteUser(@PathVariable int id) {
        if (userRepository.findById(id) == null) {
            return failure;
        }
        userRepository.deleteById(id);
        if (userRepository.findById(id) == null) {
            return success;
        }
        return failure;
    }


    // austin (edited 03/04 by matthew to return user object to frontend)
    @PostMapping("/login")
    User login(@RequestBody User request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        // make sure that password is correct, throw an exception  otherwise.
        else if (!user.getPassword().equals(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password");
        }

        // otherwise, login successful, and return the user object to the frontend
        user.setUserStatus(UserStatus.ONLINE);
        userRepository.save(user);
        return user;
    }

    /*
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
    */
}
