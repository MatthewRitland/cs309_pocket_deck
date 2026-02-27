package onetoone.Users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 
 * @author Vivek Bengre
 * 
 */ 

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;


    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @GetMapping(path = "/users")
    List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @GetMapping(path = "/users/{id}")
    User getUserById( @PathVariable int id){
        return userRepository.findById(id);
    }

    @PostMapping(path = "/users")
    String createUser(@RequestBody User user){
        if (user == null)
            return failure;
        userRepository.save(user);
        return success;
    }

    @PutMapping("/users/{id}")
    User updateUser(@PathVariable int id, @RequestBody User request){
        User user = userRepository.findById(id);
        if(user == null)
            return null;
        userRepository.save(request);
        return userRepository.findById(id);
    }   
    
    @PostMapping("/login")
    loginMessage login (@RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUserName(username);
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

    @DeleteMapping(path = "/users/{id}")
    String deleteUser(@PathVariable int id){
        userRepository.deleteById(id);
        return success;
    }
}
