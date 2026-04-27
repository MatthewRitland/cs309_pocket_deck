package PocketDeck.GameLobby;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface GameLobbyRepository extends JpaRepository<GameLobby, Integer> {
    GameLobby findById(int id);

    @Transactional
    void deleteById(int id);

}
