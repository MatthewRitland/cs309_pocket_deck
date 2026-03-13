package onetoone.GameHistory;

import onetoone.Friends.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameHistoryController {
    @Autowired
    GameHistoryRepository gameHistoryRepository;


}
