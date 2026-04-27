package PocketDeck.GameHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GameHistoryRepository extends JpaRepository<GameHistory, Integer> {
    GameHistory findById(int id);

    // find all past games by associated with the userId
    List<GameHistory> findByUserId(int userId);

    // finds only one record
    GameHistory findByUserIdAndId(int userId, int id);

    @Transactional
    void deleteById(int id);

}
