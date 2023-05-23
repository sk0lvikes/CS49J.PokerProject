package main.texholdem;

import java.util.*;
public class Hand implements Comparable<Hand> {
    public final List<Card> cards;
    public HandRank handrank;
    public List<Card.Rank> tieBreaker;

    public enum HandRank {
        HIGH_CARD(1), PAIR(2), TWO_PAIR(3), THREE_OF_A_KIND(4), STRAIGHT(5), FLUSH(6), FULL_HOUSE(7), FOUR_OF_A_KIND(8), STRAIGHT_FLUSH(9), ROYAL_FLUSH(10);
        public final int rankValue;

        HandRank(int rankValue) {
            this.rankValue = rankValue;
        }

        public int getRankValue() {
            return rankValue;
        }
    }

    // Constructor
    public Hand() {
        this.cards = new ArrayList<>();
        this.tieBreaker = new ArrayList<>();
    }
    public void addCard(Card card) {
        cards.add(card);
        Collections.sort(cards, Comparator.comparing(Card::getRank).reversed());
    }
    public boolean isEmpty() {
        return cards.isEmpty();
    }
    //clear cards
    public void clear() {
        cards.clear();
        handrank = null;
        tieBreaker.clear();
    }
    // Get the cards
    public List<Card> getCards() {
        return cards;
    }
    public List<Card.Rank> getTieBreakers() {
        return tieBreaker;
    }
    // Evaluate the hand
    public HandRank evaluateHandRank(Hand communityCards) {
        List<Card> combinedCards = new ArrayList<>(cards);
        Map<Card.Rank, Integer> rankCounts = new HashMap<>();
        combinedCards.addAll(communityCards.getCards());

        boolean isFlush = true;
        Card.Suit suit = cards.get(0).getSuit();
        for (Card card : combinedCards) {
            rankCounts.put(card.getRank(), rankCounts.getOrDefault(card.getRank(), 0) + 1);
            if (card.getSuit() != suit) {
                isFlush = false;
            }
        }
        int pairs = 0;
        int threeOfAKinds = 0;
        int fourOfAKinds = 0;
        for (int count : rankCounts.values()) {
            if (count == 2) {
                pairs++;
            } else if (count == 3) {
                threeOfAKinds++;
            } else if (count == 4) {
                fourOfAKinds++;
            }
        }
        boolean isStraight = false;
        List<Card.Rank> ranks = new ArrayList<>(rankCounts.keySet());
        Collections.sort(ranks);
        if (ranks.size() >= 5) {
            isStraight = true;
            //checks for breaks in the straight and returns false if any are found
            for (int i = 0; i < ranks.size() - 1; i++) {
                if (ranks.get(i).ordinal() != ranks.get(i + 1).ordinal() - 1) {
                    isStraight = false;
                    break;
                }
            }
            // check for wheel straight (Ace low, eg.(A, 2, 3, 4, 5))
            if (!isStraight && ranks.contains(Card.Rank.ACE) && ranks.get(0) == Card.Rank.TWO && ranks.get(3) == Card.Rank.FIVE) {
                isStraight = true;
            }
            if (isStraight) {
                // For a straight, the tiebreaker is the highest card in the straight
                Card.Rank highStraightCard = ranks.get(ranks.size() - 1);

                // If it's a wheel straight, the highest card should be 5
                if (ranks.contains(Card.Rank.ACE) && ranks.get(0) == Card.Rank.TWO && ranks.get(3) == Card.Rank.FIVE) {
                    highStraightCard = Card.Rank.FIVE;
                }

                this.handrank = HandRank.STRAIGHT;
                this.tieBreaker = Arrays.asList(highStraightCard);
            }
        }
        if (isStraight && isFlush) { //condition for Royal Flush
            if (ranks.get(ranks.size() - 1) == Card.Rank.ACE) {
                this.handrank = HandRank.ROYAL_FLUSH;
            } else {
                this.handrank = HandRank.STRAIGHT_FLUSH;
            }
        } else if (fourOfAKinds > 0) {
            Card.Rank fourRank = null;
            Card.Rank kickerRank = null;
            for (Map.Entry<Card.Rank, Integer> entry : rankCounts.entrySet()) {
                if (entry.getValue() == 4) {
                    fourRank = entry.getKey();
                } else {
                    kickerRank = entry.getKey();
                }
            }
            this.handrank = HandRank.FOUR_OF_A_KIND;
            this.tieBreaker = Arrays.asList(fourRank, kickerRank);
        } else if (threeOfAKinds > 0 && pairs > 0) {
            Card.Rank threeOfAKindRank = null;
            Card.Rank pairRank = null;
            for (Map.Entry<Card.Rank, Integer> entry : rankCounts.entrySet()) {
                if (entry.getValue() == 3) {
                    threeOfAKindRank = entry.getKey();
                } else if (entry.getValue() == 2) {
                    pairRank = entry.getKey();
                }
            }
            List<Card.Rank> tieBreakers = new ArrayList<>();
            tieBreakers.add(threeOfAKindRank);
            tieBreakers.add(pairRank);
            this.handrank = HandRank.FULL_HOUSE;
            this.tieBreaker = tieBreakers;
        } else if (isFlush) {
            this.handrank = HandRank.FLUSH;
        } else if (isStraight) {
            List<Card.Rank> tieBreakers = new ArrayList<>();
            tieBreakers.add(ranks.get(ranks.size() - 1));
            this.handrank = HandRank.STRAIGHT;
            this.tieBreaker = tieBreakers;
        } else if (threeOfAKinds > 0) {
            this.handrank = HandRank.THREE_OF_A_KIND;
        } else if (pairs == 2) {
            List<Card.Rank> pairRanks = new ArrayList<>();
            Card.Rank otherRank = null;
            for (Map.Entry<Card.Rank, Integer> entry : rankCounts.entrySet()) {
                if (entry.getValue() == 2) {
                    pairRanks.add(entry.getKey());
                } else {
                    otherRank = entry.getKey();
                }
            }
            Collections.sort(pairRanks, Collections.reverseOrder());
            pairRanks.add(otherRank);
            this.handrank = HandRank.TWO_PAIR;
            this.tieBreaker = pairRanks;
        } else if (pairs == 1) {
            Card.Rank pairRank = null;
            List<Card.Rank> kickerRanks = new ArrayList<>();
            for (Map.Entry<Card.Rank, Integer> entry : rankCounts.entrySet()) {
                if (entry.getValue() == 2) {
                    pairRank = entry.getKey();
                } else {
                    kickerRanks.add(entry.getKey());
                }
            }
            Collections.sort(kickerRanks, Collections.reverseOrder());
            kickerRanks.add(0, pairRank);
            this.handrank = HandRank.PAIR;
            this.tieBreaker = kickerRanks;
        } else {
            this.handrank = HandRank.HIGH_CARD;
        }
        return this.handrank;
    }

   @Override
    public int compareTo(Hand other) {
        HandRank thisRank = this.handrank;
        HandRank otherRank = other.handrank;

         //Compare based on the hand ranks
        int rankCompare = thisRank.compareTo(otherRank);

        if (rankCompare != 0) {
            return rankCompare;
        } else {
            List<Card.Rank> thisTieBreakers = this.getTieBreakers();
            List<Card.Rank> otherTieBreakers = other.getTieBreakers();
            for (int i = 0; i < this.tieBreaker.size(); i++) {
                int tieBreakerCompare = this.tieBreaker.get(i).compareTo(other.tieBreaker.get(i));
                if (tieBreakerCompare != 0) {
                    return tieBreakerCompare;
                }
            }
            return 0;
        }
    }
}