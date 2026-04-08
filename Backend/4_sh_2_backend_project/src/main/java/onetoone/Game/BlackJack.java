package onetoone.Game;

import onetoone.CardGames.CardGame;
import onetoone.Users.User;

import java.util.Random;

public class BlackJack extends Game{
    private int[] scores;
    private int dealerScore;
    private Card[] dealerHand;
    private Result[] winners;
    private boolean[] stood;
    private final static int MAX_SCORE = 21;

    public BlackJack () {
    }

    public BlackJack (CardGame gameRules, User[] players, int handLimit) {
        super (gameRules, players, handLimit);
        scores = new int[players.length];
        dealerHand = new Card[handLimit];
        winners = new Result[players.length];
        stood = new boolean[players.length];
        dealerScore = 0;
        for (int i = 0; i < players.length; i++) {
            scores[i] = 0;
            stood[i] = false;
        }
        Actions[] actions = new Actions[2];
        actions[0] = Actions.STAND;
        actions[1] = Actions.HIT;
        this.setPossibleActions(actions);
    }

    private int cardValueConversion (User player, Card card) {
        Value value = card.getValue();
        if (value.equals(Value.TWO)) {
            return 2;
        }
        else if (value.equals(Value.THREE)) {
            return 3;
        }
        else if (value.equals(Value.FOUR)) {
            return 4;
        }
        else if (value.equals(Value.FIVE)) {
            return 5;
        }
        else if (value.equals(Value.SIX)) {
            return 6;
        }
        else if (value.equals(Value.SEVEN)) {
            return 7;
        }
        else if (value.equals(Value.EIGHT)) {
            return 8;
        }
        else if (value.equals(Value.NINE)) {
            return 9;
        }
        else if (value.equals(Value.TEN)) {
            return 10;
        }
        else if (value.equals(Value.JACK)) {
            return 10;
        }
        else if (value.equals(Value.QUEEN)) {
            return 10;
        }
        else if (value.equals(Value.KING)) {
            return 10;
        }
        else if (value.equals(Value.ACE)) {
            if (scores[findPlayer(player)] + 11 > MAX_SCORE) {
                return 1;
            }
            else {
                return 11;
            }
        }
        return 0;
    }

    private int cardValueConversion (Card card) {
        Value value = card.getValue();
        if (value.equals(Value.TWO)) {
            return 2;
        }
        else if (value.equals(Value.THREE)) {
            return 3;
        }
        else if (value.equals(Value.FOUR)) {
            return 4;
        }
        else if (value.equals(Value.FIVE)) {
            return 5;
        }
        else if (value.equals(Value.SIX)) {
            return 6;
        }
        else if (value.equals(Value.SEVEN)) {
            return 7;
        }
        else if (value.equals(Value.EIGHT)) {
            return 8;
        }
        else if (value.equals(Value.NINE)) {
            return 9;
        }
        else if (value.equals(Value.TEN)) {
            return 10;
        }
        else if (value.equals(Value.JACK)) {
            return 10;
        }
        else if (value.equals(Value.QUEEN)) {
            return 10;
        }
        else if (value.equals(Value.KING)) {
            return 10;
        }
        else if (value.equals(Value.ACE)) {
            if (dealerScore + 11 > MAX_SCORE) {
                return 1;
            }
            else {
                return 11;
            }
        }
        return 0;
    }

    private void dealerTurn () {
        if (dealerScore < 17) {
            int cardCount = 0;
            Card currentCard = dealerHand[cardCount];
            while (currentCard != null && cardCount < dealerHand.length) {
                cardCount += 1;
                if (cardCount < dealerHand.length) {
                    currentCard = dealerHand[cardCount];
                }
            }
            if (dealerHand.length <= cardCount) {
                return;
            }
            Random ran = new Random();
            dealerHand[cardCount] = getDeck()[ran.nextInt(53)];
            dealerScore += cardValueConversion(dealerHand[cardCount]);
        }
    }

    public boolean checkGameProgress () {
        for (int i = 0; i < stood.length; i++) {
            if (!stood[i]) {
                return false;
            }
        }
        return true;
    }

    public void checkWinners () {
        for (int i = 0; i < winners.length; i++) {
            if (dealerScore > MAX_SCORE) {
                winners[i] = Result.WIN;
            }
            else if (scores[i] > MAX_SCORE) {
                winners[i] = Result.LOSE;
            }
            else if (scores[i] == dealerScore) {
                winners[i] = Result.DRAW;
            }
            else if (scores[i] > dealerScore) {
                winners[i] = Result.WIN;
            }
            else if (scores[i] < dealerScore) {
                winners[i] = Result.LOSE;
            }
        }
    }

    public void takeTurn (Actions action) {
        int currentPlayerLocation = findCurrentPlayer();
        if (!stood[currentPlayerLocation]) {
            if (action.equals(Actions.STAND)) {
                stood[currentPlayerLocation] = true;
            }
            else if (action.equals(Actions.HIT)) {
                Card drawn = draw();
                scores[currentPlayerLocation] += cardValueConversion(getCurrentPlayer(), drawn);
                if (scores[currentPlayerLocation] > MAX_SCORE) {
                    Card[] currentHand = getCurrentPlayerHand();
                    for (int i = 0; i < currentHand.length; i++) {
                        if (currentHand[i].getValue().equals(Value.ACE)) {
                            if (scores[currentPlayerLocation] > MAX_SCORE && scores[currentPlayerLocation] - 10 <= MAX_SCORE) {
                                scores[currentPlayerLocation] -= 10;
                            }
                        }
                    }
                    if (scores[currentPlayerLocation] > MAX_SCORE) {
                        stood[currentPlayerLocation] = true;
                    }
                }
            }
        }
        if (currentPlayerLocation == getPlayers().length - 1) {
            dealerTurn();
        }
        if (!checkGameProgress()) {
            nextPlayer();
            return;
        }
        checkWinners();
    }
}
