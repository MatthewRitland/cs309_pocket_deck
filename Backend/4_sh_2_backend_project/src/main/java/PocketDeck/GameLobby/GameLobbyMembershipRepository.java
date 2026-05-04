package PocketDeck.GameLobby;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GameLobbyMembershipRepository extends JpaRepository<GameLobbyMembership, Integer> {
    GameLobbyMembership findById(int id);

    GameLobbyMembership findByGameLobbyMemberId(int userId); // use to make sure that a member is in only ONE lobby at a time
    int countByGameLobbyId(int id);

    List<GameLobbyMembership> findByGameLobbyId(int gameLobbyId); // use to return a list of all members in a lobby
    @Transactional
    void deleteById(int id);

    @Transactional
    void deleteByGameLobbyMemberId(int id);
}
