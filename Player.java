package main.texholdem;

import java.util.*;

public abstract class Player {
    public int chips, currentBet;
    public Hand hand;
    public HoldEm holdem;
    public String name;
    public boolean hasFolded, turnDone;
    public int betAmount;
    protected Hand.HandRank highestRank, bestHandRank;
    protected guiController gui;

    public Player(String name, int startingChips) {
        this.chips = startingChips;
        this.hand = new Hand();
        this.name = name;
        this.betAmount = 0;
        this.hasFolded = false;
        this.turnDone = false;
    }
    public void receiveCard(Card card) {
        hand.addCard(card);
    }
    public Hand getHand() {
        return hand;
    }
    public void setChips(int startingChips){
        this.chips = startingChips;
}
    public int getChips() {
        return chips;
    }
    public int getCurrentBet() {
        return currentBet;
    }
    public void setCurrentBet(int bet) {
        currentBet = bet;
    }
    public void setBetAmount(int bet) {
        currentBet = bet;
    }
    public void addChips(int amount) {
        this.chips += amount;
    }

    public void deductChips(int amount) {
        this.chips -= amount;
    }
    public boolean isTurnDone() {
        return turnDone;
    }
    public void setTurnDone(boolean turnDone) {
        this.turnDone = turnDone;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Hand.HandRank getHandRank(Hand communityCards) {
        return hand.evaluateHandRank(communityCards);
    }
    public Hand getBestPossibleHand(Hand communityCards) {
        List<Card> allCards = new ArrayList<>(hand.getCards());
        allCards.addAll(communityCards.getCards());

        List<List<Card>> combinations = generateCombinations(allCards, 5); // Generate all combinations of 5 cards

        Hand bestHand = null;
        Hand.HandRank bestRank = null;

        for (List<Card> combination : combinations) {
            Hand possibleHand = new Hand();
            for (Card card : combination) {
                possibleHand.addCard(card);
            }

            Hand.HandRank possibleRank = possibleHand.evaluateHandRank(communityCards);
            if (bestRank == null || possibleRank.getRankValue() > bestRank.getRankValue()) {
                bestRank = possibleRank;
                bestHand = possibleHand;
            }
        }

        return bestHand;
    }

    public List<List<Card>> generateCombinations(List<Card> cards, int k) {
        List<List<Card>> combinations = new ArrayList<>();
        generateCombinationsHelper(cards, k, 0, new ArrayList<>(), combinations);
        return combinations;
    }
    public void generateCombinationsHelper(List<Card> cards, int k, int start, List<Card> current, List<List<Card>> combinations) {
        if (current.size() == k) {
            combinations.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < cards.size(); i++) {
            current.add(cards.get(i));
            generateCombinationsHelper(cards, k, i + 1, current, combinations);
            current.remove(current.size() - 1);
        }
    }
    protected HoldEm holdEm;

    public void setHoldEm(HoldEm holdEm) {
        this.holdEm = holdEm;
    }

    public void setGui(guiController gui) {
        if (gui == null) {
            throw new IllegalArgumentException("gui cannot be null");
        }
        this.gui = gui;
    }
    public void fold() {
        this.hasFolded = true;
        if (holdem.winningPlayers.contains(this)) {
            holdem.winningPlayers.remove(this);
        }
    }
    public boolean hasFolded() {
        return this.hasFolded;
    }
    public abstract int placeBet(
    );
    public abstract void makeMove();
    public Hand.HandRank getBestHandRank() {
        return bestHandRank;
    }
    public void setBestHandRank(Hand.HandRank bestHandRank) {
        this.bestHandRank = bestHandRank;
    }

}
