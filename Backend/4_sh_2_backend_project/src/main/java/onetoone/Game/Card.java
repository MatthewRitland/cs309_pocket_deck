package onetoone.Game;

public class Card {
    private Suit suit;
    private Value value;
    private Boolean isFace;

    public Card () {}

    public Card (Suit suit, Value value) {
        this.suit = suit;
        this.value = value;
        isFaceCheck (value);
    }

    public Suit getSuit() {
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
        isFaceCheck (value);
    }

    public Boolean getIsFace() {
        return isFace;
    }

    public void isFaceCheck (Value value) {
        isFace = (value == Value.JACK) || (value == Value.QUEEN) || (value == Value.KING);
    }
}
