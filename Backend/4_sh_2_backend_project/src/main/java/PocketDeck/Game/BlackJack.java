package PocketDeck.Game;

import PocketDeck.CardGames.CardGame;
import PocketDeck.Users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlackJack extends Game{
    private int[] scores;
    private int dealerScore;
    private Card[] dealerHand;
    private boolean[] stood;
    private final static int MAX_SCORE = 21;

    public BlackJack () {
    }

    public BlackJack (CardGame gameRules, User[] players) {
        super (gameRules, players, 30);
        scores = new int[players.length];
        dealerHand = new Card[30];
        stood = new boolean[players.length];
        dealerScore = 0;
        for (int i = 0; i < players.length; i++) {
            scores[i] = 0;
            stood[i] = false;
            draw();
            draw();
            scores[i] = cardValueConversion(getCard(getCurrentPlayer(), 0)) + cardValueConversion(getCard(getCurrentPlayer(), 1));
            nextPlayer();
        }
        dealerTurn();
        dealerTurn();
        Actions[] actions = new Actions[2];
        actions[0] = Actions.STAND;
        actions[1] = Actions.HIT;
        this.setPossibleActions(actions);
    }

    public Card[] getDealerHand () {
        return dealerHand;
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
        Card currentCard;
        int cardCount = 0;
        if (dealerHand.length > 2) {
            if (dealerScore < 17) {
                cardCount = 0;
                currentCard = dealerHand[cardCount];
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
                dealerHand[cardCount] = getDeck()[ran.nextInt(52)];
                dealerScore = 0;
                int aceCount = 0;
                ArrayList<Card> aces = new ArrayList<>();
                for (int i = 0; i < cardCount; i++) {
                    if (!dealerHand[i].getValue().equals(Value.ACE)) {
                        dealerScore += cardValueConversion(dealerHand[i]);
                    }
                    else {
                        aceCount += 1;
                        aces.add(dealerHand[i]);
                    }
                }
                for (int i = 0; i < aceCount; i++) {
                    dealerScore += cardValueConversion(aces.get(i));
                }
            }
        }
        else {
            if (dealerHand[cardCount] == null ) {
                currentCard = dealerHand[cardCount];
                while (currentCard != null && cardCount < dealerHand.length) {
                    cardCount += 1;
                    if (cardCount < dealerHand.length) {
                        currentCard = dealerHand[cardCount];
                    }
                }
            }
            if (dealerHand.length <= cardCount) {
                return;
            }
            Random ran = new Random();
            dealerHand[cardCount] = getDeck()[ran.nextInt(52)];
            dealerScore = 0;
            int aceCount = 0;
            ArrayList<Card> aces = new ArrayList<>();
            for (int i = 0; i < cardCount; i++) {
                if (!dealerHand[i].getValue().equals(Value.ACE)) {
                    dealerScore += cardValueConversion(dealerHand[i]);
                }
                else {
                    aceCount += 1;
                    aces.add(dealerHand[i]);
                }
            }
            for (int i = 0; i < aceCount; i++) {
                dealerScore += cardValueConversion(aces.get(i));
            }
        }
    }

    public boolean isBusted (User player) {
        if (scores[findPlayer(player)] > MAX_SCORE) {
            return true;
        }
        return false;
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
        for (int i = 0; i < getWinners().length; i++) {
            if (dealerScore > MAX_SCORE) {
                setWinner(i, Result.WIN);
            }
            else if (scores[i] > MAX_SCORE) {
                setWinner(i, Result.LOSE);
            }
            else if (scores[i] == dealerScore) {
                setWinner(i, Result.DRAW);
            }
            else if (scores[i] > dealerScore) {
                setWinner(i, Result.WIN);
            }
            else if (scores[i] < dealerScore) {
                setWinner(i, Result.LOSE);
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
                scores[currentPlayerLocation] = 0;
                int cardCount = getCardAmount(getCurrentPlayer());
                Card[] currentHand = getCurrentPlayerHand();
                int aceCount = 0;
                ArrayList<Card> aces = new ArrayList<>();
                for (int i = 0; i < cardCount; i++) {
                    if (!currentHand[i].getValue().equals(Value.ACE)) {
                        scores[currentPlayerLocation] += cardValueConversion(getCurrentPlayer(), currentHand[i]);
                    }
                    else {
                        aceCount += 1;
                        aces.add(currentHand[i]);
                    }
                }
                for (int i = 0; i < aceCount; i++) {
                        scores[currentPlayerLocation] += cardValueConversion(getCurrentPlayer(), aces.get(i));
                }
                if (scores[currentPlayerLocation] > MAX_SCORE) {
                    stood[currentPlayerLocation] = true;
                }
            }
        }
        if (currentPlayerLocation == getPlayers().length - 1) {
            dealerTurn();
        }
        if (currentPlayerLocation == getPlayers().length - 1) {
            this.setTurn(this.getTurn() + 1);
        }
        if (!checkGameProgress()) {
            nextPlayer();
            while(stood[findCurrentPlayer()]) {
                nextPlayer();
            }
            return;
        }
        checkWinners();
    }
}
