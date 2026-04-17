package onetoone.GameNotes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GameNoteRepository extends JpaRepository<GameNote, Integer>{
    @Transactional
    void deleteById(int id);

    GameNote findById (int id);

    List<GameNote> findByUserAndGame (int userId, int gameId);

    boolean existsById (int id);
}
