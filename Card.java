package main.texholdem;

public class Card implements Comparable<Card> {
    private final Rank rank;
    private final Suit suit;
    public Card(Rank rank, Suit suit){
        this.rank = rank;
        this.suit = suit;
    }
    public enum Suit {
        SPADES("s"),HEARTS("h"),DIAMONDS("d"),CLUBS("c");
        private final String symbol;

        Suit(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    public enum Rank {
        TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"), NINE("9"), TEN("10"), JACK("11"), QUEEN("12"), KING("13"), ACE("14");
        private final String symbol;


        Rank(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        public int getValue() {
            return Integer.parseInt(symbol);
        }
    }
        public Rank getRank(){
            return rank;
    }

        public Suit getSuit(){
            return suit;
    }

    public String getFileName() {
        String rankSymbol = rank.getSymbol();
        String suitSymbol = suit.getSymbol();
        return rankSymbol + suitSymbol;
    }

    @Override
    public int compareTo(Card otherCard) {
        return Integer.compare(this.rank.getValue(), otherCard.rank.getValue());
    }
}