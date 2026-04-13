package onetoone.Chat;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// stores the chatrooms themselves
public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {
    //@Transactional - Already included from Spring Boot
    //void deleteById(GroupChatId);
}
