package onetoone.Game;

import onetoone.CardGames.CardGame;
import onetoone.Users.User;

import java.util.Random;


abstract public class Game {
    private static Card[] deck = {new Card (Suit.CLUBS, Value.ACE), new Card (Suit.CLUBS, Value.TWO), new Card (Suit.CLUBS, Value.THREE), new Card (Suit.CLUBS, Value.FOUR), new Card (Suit.CLUBS, Value.FIVE), new Card (Suit.CLUBS, Value.SIX), new Card (Suit.CLUBS, Value.SEVEN), new Card (Suit.CLUBS, Value.EIGHT), new Card (Suit.CLUBS, Value.NINE), new Card (Suit.CLUBS, Value.TEN), new Card (Suit.CLUBS, Value.JACK), new Card (Suit.CLUBS, Value.QUEEN), new Card (Suit.CLUBS, Value.KING), new Card (Suit.SPADES, Value.ACE), new Card (Suit.SPADES, Value.TWO), new Card (Suit.SPADES, Value.THREE), new Card (Suit.SPADES, Value.FOUR), new Card (Suit.SPADES, Value.FIVE), new Card (Suit.SPADES, Value.SIX), new Card (Suit.SPADES, Value.SEVEN), new Card (Suit.SPADES, Value.EIGHT), new Card (Suit.SPADES, Value.NINE), new Card (Suit.SPADES, Value.TEN), new Card (Suit.SPADES, Value.JACK), new Card (Suit.SPADES, Value.QUEEN), new Card (Suit.SPADES, Value.KING), new Card (Suit.DIAMONDS, Value.ACE), new Card (Suit.DIAMONDS, Value.TWO), new Card (Suit.DIAMONDS, Value.THREE), new Card (Suit.DIAMONDS, Value.FOUR), new Card (Suit.DIAMONDS, Value.FIVE), new Card (Suit.DIAMONDS, Value.SIX), new Card (Suit.DIAMONDS, Value.SEVEN), new Card (Suit.DIAMONDS, Value.EIGHT), new Card (Suit.DIAMONDS, Value.NINE), new Card (Suit.DIAMONDS, Value.TEN), new Card (Suit.DIAMONDS, Value.JACK), new Card (Suit.DIAMONDS, Value.QUEEN), new Card (Suit.DIAMONDS, Value.KING), new Card (Suit.HEARTS, Value.ACE), new Card (Suit.HEARTS, Value.TWO), new Card (Suit.HEARTS, Value.THREE), new Card (Suit.HEARTS, Value.FOUR), new Card (Suit.HEARTS, Value.FIVE), new Card (Suit.HEARTS, Value.SIX), new Card (Suit.HEARTS, Value.SEVEN), new Card (Suit.HEARTS, Value.EIGHT), new Card (Suit.HEARTS, Value.NINE), new Card (Suit.HEARTS, Value.TEN), new Card (Suit.HEARTS, Value.JACK), new Card (Suit.HEARTS, Value.QUEEN), new Card (Suit.HEARTS, Value.KING)};
    private CardGame gameRules;
    private User[] players;
    private User currentPlayer;
    private Card[][] playerHands;
    private int handLimit;
    private Actions[] possibleActions;
    private int turn;

    public Game () {}

    public Game (CardGame gameRules, User[] players, int handLimit) {
        this.gameRules = gameRules;
        this.players = players;
        this.handLimit = handLimit;
        currentPlayer = players[0];
        playerHands = new Card[players.length][handLimit];
        turn = 0;
    }

    public User[] getPlayers () {
        return players;
    }

    public void setPlayers (User[] players) {
        this.players = players;
    }

    public CardGame getGameRules () {
        return gameRules;
    }

    public void setGameRules (CardGame gameRules) {
        this.gameRules = gameRules;
    }

    public int getHandLimit () {
        return handLimit;
    }

    public void setHandLimit (int handLimit) {
        this.handLimit = handLimit;
    }

    public Actions[] getPossibleActions () {
        return possibleActions;
    }

    public void setPossibleActions (Actions[] actions) {
        this.possibleActions = actions;
    }

    public User getCurrentPlayer () {
        return currentPlayer;
    }

    public void setCurrentPlayer () {
        currentPlayer = players[0];
    }

    public Card[] getDeck () {
        return deck;
    }

    public Card[][] getPlayerHands () {
        return playerHands;
    }

    public void setPlayerHands () {
        playerHands = new Card[players.length][handLimit];
    }

    public int getTurn () { return turn; }

    protected void setTurn (int turn) { this.turn = turn; }

    public Card[] getCurrentPlayerHand () {
        if (findCurrentPlayer() == -1) {
            return null;
        }
        return playerHands[findCurrentPlayer()];
    }

    public Card[] getPlayerHand (User player) {
        if (findPlayer(player) == -1) {
            return null;
        }
        return playerHands[findPlayer(player)];
    }

    public Card getCard (User player, int handPlacement) {
        int playerLocation = findPlayer(player);
        if (playerLocation == -1) {
            return null;
        }
        if (handPlacement < 0 || handPlacement >= handLimit) {
            return null;
        }
        return playerHands[playerLocation][handPlacement];
    }

    public int getCardLocation (User player, Card card) {
        Card[] playerHand = getPlayerHand(player);
        int cardLocation = 0;
        for (cardLocation = 0; cardLocation < playerHand.length; cardLocation++) {
            if (card.isEqual(playerHand[cardLocation])) {
                return cardLocation;
            }
        }
        return -1;
    }

    public int findPlayer (User player) {
        int current;
        for (current = 0; current < players.length; current++) {
            if (player.isEqual(players[current])) {
                return current;
            }
        }
        return -1;
    }

    public int findCurrentPlayer () {
        int current;
        for (current = 0; current < players.length; current++) {
            if (currentPlayer.isEqual(players[current])) {
                return current;
            }
        }
        return -1;
    }

    public void nextPlayer () {
        int current = findCurrentPlayer();
        if (current == players.length - 1) {
            current = 0;
        }
        else {
            current += 1;
        }
        currentPlayer = players[current];
    }

    public Card draw () {
        if (findCurrentPlayer() == -1) {
            return null;
        }
        Card[] currentHand = playerHands[findCurrentPlayer()];
        int cardCount = 0;
        Card currentCard = currentHand[cardCount];
        while (currentCard != null && cardCount < currentHand.length) {
            cardCount += 1;
            if (cardCount < currentHand.length) {
                currentCard = currentHand[cardCount];
            }
        }
        if (currentHand.length <= cardCount) {
            return null;
        }
        Random ran = new Random();
        playerHands[findCurrentPlayer()][cardCount] = deck[ran.nextInt(52)];
        return playerHands[findCurrentPlayer()][cardCount];
    }

    abstract public void takeTurn (Actions action);

    abstract public boolean checkGameProgress ();
}
