package PocketDeck.Requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer>{
    Request findById (int id);

    @Transactional
    void deleteById(int id);


    Request findByRequestedIdAndRequesterId (int requestedId, int requesterId);

    List<Request> findByRequestedId (int id);

    Request findByRequestedIdAndRequesterIdAndStatus (int requestedId, int requesterId, RequestStatus status);

    Request findByRequestedIdOrRequesterId(int requestedId, int requesterId);

    // returns a list of all requests by requester for that game specific lobby
    List<Request> findByRequesterIdAndGameLobbyId(int requesterId, int gameLobbyId);

    List<Request> findByRequestedIdAndGameLobbyId(int requestedId, int gameLobbyId);

    List<Request> findByGameLobbyId(int gameLobbyId);
}
