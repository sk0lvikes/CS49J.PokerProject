package main.texholdem;

import java.util.*;

public class CpuPlayer extends Player {
    public Random random;

    public CpuPlayer(String name, int chips) {
        super(name, chips);
        random = new Random();
        this.turnDone = false;
        this.hasFolded = false;
    }

    public int getCurrentBet() {
        // Randomly choose between folding and betting
        if (random.nextBoolean()) {
            // If the CPU player chooses to bet, randomly choose between the bet amounts
            int[] betAmounts = {1, 10, 100, 1000};
            int bet = betAmounts[random.nextInt(betAmounts.length)];

            // Make sure the player has enough chips to make the bet
            if (bet > getChips()) {
                System.out.println("Not enough chips to make bet");
                return 0; // This will cause the player to fold
            }

            // Deduct the bet from the player's chips and return the bet amount
            deductChips(bet);
            holdEm.user.setTurnDone(false);
            return bet;
        } else {
            // If the CPU player chooses to fold
            fold();
            return 0;
        }
    }
    @Override
    public Hand.HandRank getBestHandRank() {
        return null;
    };
    @Override
    public void makeMove() {
        Hand.HandRank handrank = hand.evaluateHandRank(holdem.getCommunityCards());

        // If the hand rank is less than a certain value, fold
        if (handrank.getRankValue() < Hand.HandRank.PAIR.getRankValue()) {
            fold();
            System.out.println(name + " folds.");
        } else {
            // Otherwise, place a bet
            int bet = placeBet();
            holdem.updatePot(bet);
            System.out.println(name + " bets " + bet);
        }
    }

    @Override
    public int placeBet() {
        // Randomly choose between folding or calling
        boolean fold = Math.random() < 0.5;

        if (fold) {
            fold();
            return 0;
        } else {
            // Call
            int currentBet = getCurrentBet();
            int chipsToBet = Math.min(getChips(), currentBet);
            deductChips(chipsToBet);
            return chipsToBet;
        }
    }
}