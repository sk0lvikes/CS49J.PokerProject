package poker;

public enum CardRank {
    ACE(1, "Ace"),
    TWO(2, "Two"),
    THREE(3, "Three"),
    FOUR(4, "Four"),
    FIVE(5, "Five"),
    SIX(6, "Six"),
    SEVEN(7, "Seven"),
    EIGHT(8, "Eight"),
    NINE(9, "Nine"),
    TEN(10, "Ten"),
    JACK(11, "Jack"),
    QUEEN(12, "Queen"),
    KING(13, "King");

    private final int rankValue;
    private final String rankName;

    CardRank(int rankValue, String rankName) {
        this.rankValue = rankValue;
        this.rankName = rankName;
    }

    public int getRankValue() {
        return rankValue;
    }

    public String getRankName() {
        return rankName;
    }

    @Override
    public String toString() {
        return rankName;
    }
}