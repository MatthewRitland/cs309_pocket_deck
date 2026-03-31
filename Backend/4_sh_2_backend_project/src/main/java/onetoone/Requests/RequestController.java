package onetoone.Requests;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class RequestController {

    @Autowired
    RequestRepository requestRepo;

    @GetMapping("/request")
    List<Request> getAllRequests () {
        return requestRepo.findAll();
    }
    @GetMapping("/request/requested/{id}")
    List<Request> getRequestsByRequested (@PathVariable int id) {
        return requestRepo.findByRequestedId(id);
    }

    @GetMapping("/request/{id}")
    Request getRequestById (@PathVariable int id) {
        return requestRepo.findById(id);
    }

    @PostMapping("/request")
    Request createRequest (@RequestBody Request req) {
        if (req == null || req.getRequested() == null || req.getRequester() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing a user");
        }
        req.setStatus(RequestStatus.PENDING);
        requestRepo.save(req);
        return req;
    }

    @PutMapping("/request/{id}")
    Request updateRequest (@RequestBody Request req, @PathVariable int id) {
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

    @DeleteMapping("request/{id}")
    String deleteRequest (@PathVariable int id) {
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
