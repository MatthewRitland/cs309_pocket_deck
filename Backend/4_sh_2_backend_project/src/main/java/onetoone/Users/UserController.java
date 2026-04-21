package onetoone.Users;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import onetoone.Friends.FriendshipRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    FriendshipRepository friendshipRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @Operation(summary = "Lists all users", description = "Returns a complete list of all users from the database")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully returned list",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))
            }),
    })
    @GetMapping(path = "/users")
    List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Operation(summary = "Returns a single user from user Id", description = "Returns a complete user from the database")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully returned user",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = User.class))
            }),
            @ApiResponse(responseCode = "404", description = "failed to return complete user",
                    content = @Content),
    })
    @GetMapping(path = "/users/{id}")
    User getUserById(@Parameter(description = "id of user to get") @PathVariable int id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }


    @Operation(summary = "Returns a single user from username", description = "Returns a complete user from the database")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully returned user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))
                    }),
            @ApiResponse(responseCode = "404", description = "failed to return complete user",
                    content = @Content),
    })
    @GetMapping(path = "/users/{username}")
    User getUserByUsername(@Parameter(description = "username of user to get") @PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return user;
    }

    @Operation(summary = "Creates a user", description = "Creates and stores a newly created user to the database")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully created user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))
                    }),
            @ApiResponse(responseCode = "400", description = "username or password is invalid",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Username already in use",
                    content = @Content),
    })
    // sign up feature
    @PostMapping(path = "/signup")
    User createUser(@Parameter(description = "user object to create")@RequestBody User user) {
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

    @Operation(summary = "Edits a user's information ", description = "Edits and user in the database")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully edited user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))
                    }),
            @ApiResponse(responseCode = "404", description = "User was not found",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Username already in use",
                    content = @Content),
    })
    @PutMapping("/users/{id}")
    User updateUser(@Parameter(description = "id of user to update")@PathVariable int id,
                    @Parameter(description = "new user info to replace the old one")@RequestBody User request) {
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

    @Operation(summary = "Deletes a user", description = "Removes a user that was stored in the database by their ID")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "returns a success/fail string",
                    content = { @Content(mediaType = "text/plain")})
    })
    @DeleteMapping(path = "/users/{id}")
        // delete the user that matches the id
    String deleteUser(@PathVariable int id) {
        if (userRepository.findById(id) == null) {
            return failure;
        }

        // make sure to remove friendships that this user was a part of!
        friendshipRepository.deleteByRequesterIdOrReceiverId(id, id);

        userRepository.deleteById(id);
        if (userRepository.findById(id) == null) {
            return success;
        }
        return failure;
    }


    @Operation(summary = "Authenticates a user", description = "Confirms given info with info stored in the database")
    @ApiResponses(value =  {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated info",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))
                    }),
            @ApiResponse(responseCode = "400", description = "User was not given correctly",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content),
    })
    @PostMapping("/login")
    User login(@Parameter(description = "User info to attempt authentication with")@RequestBody User request) {
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
}
