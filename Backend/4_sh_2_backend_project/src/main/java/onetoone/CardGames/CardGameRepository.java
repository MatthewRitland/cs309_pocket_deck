package onetoone.CardGames;

import onetoone.CardGames.CardGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CardGameRepository extends JpaRepository<CardGame, Integer>{
    CardGame findById (int id);

    CardGame findByGameName (String gameName);

    @Transactional
    void deleteById(int id);

    boolean existsByGameName (String gameName);

    boolean existsById (int id);
}
