package onetoone.Chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// "table" storing the GroupChatMembership, Long since id key is Long datatype
// saves the grouping of members in group chats
public interface GroupChatMembershipRepository extends JpaRepository<GroupChatMembership, Long> {
    // finds and returns all members of a specific groupchat
    List<GroupChatMembership> findByGroupChatId(Long groupChatId);
}
