package onetoone.GameHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface GameHistoryRepository extends JpaRepository<GameHistory, Integer> {
    GameHistory findById(int id);


    @Transactional
    void deleteById(int id);

}
