package onetoone.GameNotes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GameNoteRepository extends JpaRepository<GameNote, Integer>{
    @Transactional
    void deleteById(int id);

    GameNote findById (int id);

    List<GameNote> findByUserIdAndGameId(int userId, int gameId);

    List<GameNote> findByUserId (int userId);

    boolean existsById (int id);
}
