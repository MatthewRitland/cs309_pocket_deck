package PocketDeck;

import PocketDeck.Game.*;
import PocketDeck.Users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import PocketDeck.Users.UserRepository;
import PocketDeck.CardGames.CardGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class gameTest {

    @Autowired
    UserRepository userRepo;

    @Autowired
    CardGameRepository gameRepo;

    @Test
    public void cardClassTest() {
        Card card = new Card();
        card.setValue(Value.ACE);
        assertEquals(Value.ACE, card.getValue());
        card.setSuit(Suit.CLUBS);
        assertEquals(Suit.CLUBS, card.getSuit());
        assertEquals(false, card.getIsFace());
    }

    @Test
    public void blackjackConstructorTest() {
        User player = new User ("test", "1234");
        User[] players = {player};
        BlackJack game = new BlackJack (gameRepo.findByGameName("blackjack"), players);
        assertEquals(Actions.STAND, game.getPossibleActions()[0]);
        assertEquals(Actions.HIT, game.getPossibleActions()[1]);
        assertEquals(30, game.getDealerHand().length);
        assertNotEquals(0, game.getDealerScore());
        assertNotEquals(0, game.getScores()[0]);
    }

    @Test
    public void searchTest() {
        User player = new User ("test", "1234");
        User[] players = {player};
        BlackJack game = new BlackJack (gameRepo.findByGameName("blackjack"), players);
        assertNotNull(game.getCurrentPlayerHand()[1]);
        assertNotNull(game.getCard(player, 0));
        Card card = game.getPlayerHand(player)[1];
        assertEquals(1, game.getCardLocation(player, card));
        assertEquals(0, game.findPlayer(player));
        assertEquals(0, game.findCurrentPlayer());
    }

    @Test
    public void gettersAndSettersTest() {
        User player = new User ("test", "1234");
        User[] players = {player};
        BlackJack game = new BlackJack (gameRepo.findByGameName("blackjack"), players);
        User player2 = new User ("test2", "1111");
        players = new User[]{player, player2};
        game.setPlayers(players);
        assertEquals(player2, game.getPlayers()[1]);
        game.setGameRules(gameRepo.findByGameName("poker"));
        assertEquals(gameRepo.findByGameName("poker"), game.getGameRules());
        game.setHandLimit(100);
        assertEquals(100, game.getHandLimit());
        game.setPossibleActions(new Actions[] {Actions.HIT});
        assertEquals(Actions.HIT, game.getPossibleActions()[0]);
        game.setCurrentPlayer();
        assertEquals(player, game.getCurrentPlayer());
        Card[] deck = {new Card (Suit.CLUBS, Value.ACE), new Card (Suit.CLUBS, Value.TWO), new Card (Suit.CLUBS, Value.THREE), new Card (Suit.CLUBS, Value.FOUR), new Card (Suit.CLUBS, Value.FIVE), new Card (Suit.CLUBS, Value.SIX), new Card (Suit.CLUBS, Value.SEVEN), new Card (Suit.CLUBS, Value.EIGHT), new Card (Suit.CLUBS, Value.NINE), new Card (Suit.CLUBS, Value.TEN), new Card (Suit.CLUBS, Value.JACK), new Card (Suit.CLUBS, Value.QUEEN), new Card (Suit.CLUBS, Value.KING), new Card (Suit.SPADES, Value.ACE), new Card (Suit.SPADES, Value.TWO), new Card (Suit.SPADES, Value.THREE), new Card (Suit.SPADES, Value.FOUR), new Card (Suit.SPADES, Value.FIVE), new Card (Suit.SPADES, Value.SIX), new Card (Suit.SPADES, Value.SEVEN), new Card (Suit.SPADES, Value.EIGHT), new Card (Suit.SPADES, Value.NINE), new Card (Suit.SPADES, Value.TEN), new Card (Suit.SPADES, Value.JACK), new Card (Suit.SPADES, Value.QUEEN), new Card (Suit.SPADES, Value.KING), new Card (Suit.DIAMONDS, Value.ACE), new Card (Suit.DIAMONDS, Value.TWO), new Card (Suit.DIAMONDS, Value.THREE), new Card (Suit.DIAMONDS, Value.FOUR), new Card (Suit.DIAMONDS, Value.FIVE), new Card (Suit.DIAMONDS, Value.SIX), new Card (Suit.DIAMONDS, Value.SEVEN), new Card (Suit.DIAMONDS, Value.EIGHT), new Card (Suit.DIAMONDS, Value.NINE), new Card (Suit.DIAMONDS, Value.TEN), new Card (Suit.DIAMONDS, Value.JACK), new Card (Suit.DIAMONDS, Value.QUEEN), new Card (Suit.DIAMONDS, Value.KING), new Card (Suit.HEARTS, Value.ACE), new Card (Suit.HEARTS, Value.TWO), new Card (Suit.HEARTS, Value.THREE), new Card (Suit.HEARTS, Value.FOUR), new Card (Suit.HEARTS, Value.FIVE), new Card (Suit.HEARTS, Value.SIX), new Card (Suit.HEARTS, Value.SEVEN), new Card (Suit.HEARTS, Value.EIGHT), new Card (Suit.HEARTS, Value.NINE), new Card (Suit.HEARTS, Value.TEN), new Card (Suit.HEARTS, Value.JACK), new Card (Suit.HEARTS, Value.QUEEN), new Card (Suit.HEARTS, Value.KING)};
        for (int i = 0; i < deck.length; i++) {
            assertEquals(deck[i], game.getDeck()[i]);
        }
        game.setPlayerHands();
        for (int i = 0; i < game.getPlayers().length; i++) {
            for (int j = 0; j < game.getHandLimit(); j++) {
                assertNull(game.getPlayerHands()[i][j]);
            }
        }
        assertEquals(0, game.getTurn());
        game.setWinner(0, Result.DRAW);
        assertEquals(Result.DRAW, game.getWinners()[0]);
    }
}
