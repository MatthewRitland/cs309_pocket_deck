package onetoone.Chat;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// stores the actual messages
public interface MessageRepository extends JpaRepository<Message, Long>{
    // return a list of all Message objects within the repository that have that groupChat ID
    List<Message> findByGroupChatId(Long groupChatId);


    // deletes all messages in a group chat that matches the given id
    @Transactional
    void deleteByGroupChatId(Long GroupChatId);
}
