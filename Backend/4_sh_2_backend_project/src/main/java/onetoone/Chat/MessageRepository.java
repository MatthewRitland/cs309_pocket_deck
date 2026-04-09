package onetoone.Chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// stores the actual messages
public interface MessageRepository extends JpaRepository<Message, Long>{
    // return a list of all Message objects within the repository that have that groupChat ID
    List<Message> findByGroupChatId(Long groupChatId);
}
