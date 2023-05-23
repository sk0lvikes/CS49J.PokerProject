package main.texholdem;

import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class UserPlayer extends Player {

    public ToggleGroup betToggle;

    public UserPlayer(String name, int chips) {
        super(name, chips);
        this.turnDone = false;
    }
    public void setBetToggle(ToggleGroup betToggle) {
        this.betToggle = betToggle;
    }
    public int onBetClick(guiController gui, HoldEm holdEm) {
        int bet = gui.getSelectedBetAmount(gui);  // This method would get the bet amount from the user's input in the GUI
        if (bet > chips) {
            gui.insufficientFunds("You don't have enough chips to make that bet!");
        } else {
            chips -= bet;
            holdEm.updatePot(bet);
            gui.updateUserChipsLabel(chips);
            System.out.println(name + " bets " + bet);
        }
        return bet;
    }
    public void setBestHandRank(Hand.HandRank bestHandRank) {
        this.bestHandRank = bestHandRank;
    }
    @Override
    public Hand.HandRank getBestHandRank() {
        return bestHandRank;
    }
    public Hand.HandRank recursiveBestHand(List<Card> playerCards, Hand communityHand) {
        List<Card> allCards = new ArrayList<>(playerCards);
        allCards.addAll(communityHand.getCards());

        List<Hand.HandRank> allPossibleRanks = new ArrayList<>();
        List<List<Card>> allCombinations = combine(allCards, 5);

        for (List<Card> combination : allCombinations) {
            Hand hand = new Hand();
            for (Card card : combination) {
                hand.addCard(card);
            }
            allPossibleRanks.add(hand.evaluateHandRank(communityHand));
        }

        Hand.HandRank highestRank = Hand.HandRank.HIGH_CARD;

        for (Hand.HandRank rank : allPossibleRanks) {
            if (rank.getRankValue() > highestRank.getRankValue()) {
                highestRank = rank;
            }
        }

        return highestRank;
    }

    // This method generates all combinations of size n from the list of cards.
    private List<List<Card>> combine(List<Card> cards, int n) {
        List<List<Card>> combinations = new ArrayList<>();
        combineHelper(cards, n, 0, new ArrayList<>(), combinations);
        return combinations;
    }

    private void combineHelper(List<Card> cards, int n, int idx, List<Card> current, List<List<Card>> combinations) {
        if (current.size() == n) {
            combinations.add(new ArrayList<>(current));
            return;
        }

        for (int i = idx; i < cards.size(); i++) {
            current.add(cards.get(i));
            combineHelper(cards, n, i + 1, current, combinations);
            current.remove(current.size() - 1);
        }
    }
    @Override
    public void makeMove() {
        // Display the bet and fold buttons for the user to make a move

    }
    @Override
    public int placeBet() {
            int bet = onBetClick(gui,holdEm); // Get the bet amount from the GUI
            if (bet > getChips()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Bounced Checks");
            alert.setHeaderText("Chapter 11");
            alert.setContentText("You don't have enough Chips to make that bet!");
            alert.showAndWait();
            fold();
            return 0;
        }
        setCurrentBet(bet);
        deductChips(bet);
        return bet;
    }
}