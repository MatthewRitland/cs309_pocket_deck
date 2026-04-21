package onetoone.GameLobby;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GameLobbyMembershipRepository extends JpaRepository<GameLobbyMembership, Integer> {
    GameLobby findById(int id);

    int countByGameLobbyId(int id);
    @Transactional
    void deleteById(int id);
}
