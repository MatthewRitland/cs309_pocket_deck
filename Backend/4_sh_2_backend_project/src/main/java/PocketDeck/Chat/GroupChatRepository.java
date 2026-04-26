package PocketDeck.Chat;

import org.springframework.data.jpa.repository.JpaRepository;

// stores the chatrooms themselves
public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {
    //@Transactional - Already included from Spring Boot
    //void deleteById(GroupChatId);
}
