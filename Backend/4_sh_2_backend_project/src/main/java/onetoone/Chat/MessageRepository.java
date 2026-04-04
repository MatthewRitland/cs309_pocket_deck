package onetoone.Chat;

import org.springframework.data.jpa.repository.JpaRepository;

// stores the actual messages
public interface MessageRepository extends JpaRepository<Message, Long>{

}
