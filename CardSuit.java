package poker;

public enum CardSuit {
    HEARTS("Hearts"),
    DIAMONDS("Diamonds"),
    CLUBS("Clubs"),
    SPADES("Spades");

    private final String suitName;

    CardSuit(String suitName) {
        this.suitName = suitName;
    }

    public String getSuitName() {
        return suitName;
    }

    @Override
    public String toString() {
        return suitName;
    }
}
