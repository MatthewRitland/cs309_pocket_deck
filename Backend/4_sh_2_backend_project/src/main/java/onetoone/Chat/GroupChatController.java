package onetoone.Chat;


import io.swagger.v3.oas.annotations.security.OAuthFlow;
import onetoone.Users.User;
import onetoone.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GroupChatController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private MessageRepository msgRepo;

    @Autowired
    private GroupChatRepository groupChatRepo;
    @Autowired
    private GroupChatMembershipRepository groupChatMembershipRepo;


    @GetMapping(path = "/groupChat/history/{groupChatId}")
    public List<Message> getGroupChatHistory(@PathVariable Long groupChatId) {
        GroupChat groupChat = groupChatRepo.findById(groupChatId).orElse(null);
        if (groupChat == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group chat was not found");
        }
        return msgRepo.findByGroupChatId(groupChatId);
    }

    // returns a list of all group chats a user belongs to
    @GetMapping(path = "/user/groupChats/{userId}")
    public List<GroupChat> getGroupChats(@PathVariable int userId) {
        User user = userRepo.findById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        List<GroupChat> groupChatList = new ArrayList<>();
        List<GroupChatMembership> memberships = groupChatMembershipRepo.findByUserId(userId);

        for (GroupChatMembership membership : memberships) {
            groupChatList.add(membership.getGroupChat());
        }
        return groupChatList;
    }



    // returns a list of users in a groupchat
    @GetMapping(path = "/groupChatMembers/{groupChatId}")
    public List<User> getGroupChatMembers(@PathVariable Long groupChatId) {
        List<GroupChatMembership> members = groupChatMembershipRepo.findByGroupChatId(groupChatId);
        List<User> usersList = new ArrayList<>();

        for (GroupChatMembership member : members) {
            usersList.add(member.getUser());
        }
        return usersList;
    }
}

