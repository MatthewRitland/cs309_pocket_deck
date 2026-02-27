package onetoone.Users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



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

    // sign up feature
    @PostMapping(path = "/signup")
    String createUser(@RequestBody User user){
        // is it a valid request? (is overall request empty? stopped w/ user == null,
        // or username or password is missing.)
        if (user == null || user.getUserName() == null || user.getPassword() == null) {
            return failure + "username or password is blank or incorrectly formatted.";
        }

        // does this username already have an account?
        if (userRepository.existsByUserName(user.getUserName())) {
            return failure + "\nname already in use.";
        }

        // otherwise will be unique and valid, so save the new user
        userRepository.save(user);

        // return a success message, user is now in the database, return the id that user has
        // so frontend can call it after creation.
        return success + " with user id: " + user.getId();
    }

    @PutMapping("/users/{id}")
    User updateUser(@PathVariable int id, @RequestBody User request){
        User user = userRepository.findById(id);

        // check if user was found/exists
        if(user == null)
            return null;

        // updating the user
        user.setUserName(request.getUserName());
        user.setPassword(request.getPassword());
        user.setIfActive(request.getIfActive());

        // save the user again (which is now updated)
        userRepository.save(user);

        // stores
        return userRepository.findById(id);
    }   
    


    @DeleteMapping(path = "/users/{id}")
    // delete the user that matches the id
    String deleteUser(@PathVariable int id){
        userRepository.deleteById(id);
        return success;
    }
}

/*
***CAN DELETE THIS COMMENT BLOCK AT ANY TIME***
LEFT OVER STUFF FROM TUTORIAL, BUT SERVES AS A GOOD EXAMPLE OF HOW TO IMPLEMENT SOMETHING THAT WOULD
HAVE HAD A RELATIONSHIP.
import onetoone.Laptops.Laptop;
import onetoone.Laptops.LaptopRepository;

public class UserController {

    @Autowired
    LaptopRepository laptopRepository;
}

@PutMapping("/users/{userId}/laptops/{laptopId}")
    String assignLaptopToUser(@PathVariable int userId,@PathVariable int laptopId){
        User user = userRepository.findById(userId);
        Laptop laptop = laptopRepository.findById(laptopId);
        if(user == null || laptop == null)
            return failure;
        laptop.setUser(user);
        user.setLaptop(laptop);
        userRepository.save(user);
        return success;
    }
 */