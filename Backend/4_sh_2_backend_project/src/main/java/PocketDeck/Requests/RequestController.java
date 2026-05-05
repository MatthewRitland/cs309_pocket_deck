package PocketDeck.Requests;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class RequestController {

    @Autowired
    RequestRepository requestRepo;

    @Operation(summary = "lists all Requests", description = "returns a list from the database of all Request objects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully returned a list of all Requests",
                    content = {@Content(mediaType="application/json",
                            schema = @Schema(implementation = Request.class))
                    })
    })
    @GetMapping("/request")
    List<Request> getAllRequests () {
        return requestRepo.findAll();
    }

    @Operation(summary = "finds one request", description = "returns a list a Request from the database based on the requested and requester ids")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully returned a Request",
                    content = {@Content(mediaType="application/json",
                            schema = @Schema(implementation = Request.class))
                    })
    })
    @GetMapping("/request/requested/{requestedId}/{requesterId}/{status}")
    Request getRequestsByRequestedAndRequester (@Parameter(description = "id of requested user")@PathVariable int requestedId, @Parameter(description = "id of requester user")@PathVariable int requesterId, @Parameter(description = "status of the friend request")@PathVariable RequestStatus status) {
        return requestRepo.findByRequestedIdAndRequesterId(requestedId, requesterId);
    }

    @Operation(summary = "finds one request", description = "returns a list a Request from the database based on the Request's id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully returned a Request",
                    content = {@Content(mediaType="application/json",
                            schema = @Schema(implementation = Request.class))
                    })
    })
    @GetMapping("/request/{id}")
    Request getRequestById (@Parameter(description = "id of Request")@PathVariable int id) {
        return requestRepo.findById(id);
    }

    @GetMapping("/request/requested/{id}")
    List<Request> getRequestsByRequested (@PathVariable int id) {
        return requestRepo.findByRequestedId(id);
    }

    @Operation(summary = "creates a Request", description = "creates a Request and stores it in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully created a Request",
                    content = {@Content(mediaType="application/json",
                            schema = @Schema(implementation = Request.class))
                    }),
            @ApiResponse(responseCode = "400", description = "missing one or two users", content = @Content)
    })
    @PostMapping("/request")
    Request createRequest (@Parameter(description = "Request object to save to the databse")@RequestBody Request req) {
        if (req == null || req.getRequested() == null || req.getRequester() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing a user");
        }
        req.setStatus(RequestStatus.PENDING);
        requestRepo.save(req);
        return req;
    }

    @Operation(summary = "updates one Request", description = "takes a Request object to update a Request from the database and returns the updated Request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully updated a Request",
                    content = {@Content(mediaType="application/json",
                            schema = @Schema(implementation = Request.class))
                    }),
            @ApiResponse(responseCode = "404", description = "failed to find a Request", content = @Content)
    })
    @PutMapping("/request/{id}")
    Request updateRequest (@Parameter(description = "Request object to update with")@RequestBody Request req, @Parameter(description = "id of Request")@PathVariable int id) {
        Request current = requestRepo.findById(id);
        if (current == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found");
        }
        current.setRequested(req.getRequested());
        current.setRequester(req.getRequester());
        if (req.getStatus() != RequestStatus.PENDING) {
            current.setStatus(req.getStatus());
        }
        requestRepo.save(current);
        return current;
    }

    @Operation(summary = "deletes one Request", description = "deletes a Request and returns a message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully deleted a Request",
                    content = {@Content(mediaType="application/json",
                            schema = @Schema(implementation = Request.class))
                    })
    })
    @DeleteMapping("request/{id}")
    String deleteRequest (@Parameter(description = "id of Request")@PathVariable int id) {
        Request toBeDeleted = requestRepo.findById(id);
        if (toBeDeleted == null) {
            return "{\"message\":\"failure\"}";
        }
        requestRepo.deleteById(id);
        if (requestRepo.findById(id) != null) {
            return "{\"message\":\"failure\"}";
        }
        return "{\"message\":\"success\"}";
    }

}
