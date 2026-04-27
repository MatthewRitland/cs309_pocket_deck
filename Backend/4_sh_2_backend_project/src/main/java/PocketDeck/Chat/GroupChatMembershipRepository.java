package PocketDeck.Chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// "table" storing the GroupChatMembership, Long since id key is Long datatype
// saves the grouping of members in group chats
public interface GroupChatMembershipRepository extends JpaRepository<GroupChatMembership, Long> {
    // finds and returns a list of all groupChatMemberships that belong to a group chat
    List<GroupChatMembership> findByGroupChatId(Long groupChatId);

    // finds and returns a list of all groupChatMemberships a user belongs to
    List<GroupChatMembership> findByUserId(int UserId);

    @Transactional
    void deleteByGroupChatIdAndUserId(Long groupChatId, int UserId); // used to remove a user from a group chat

}
