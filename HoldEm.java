package main.texholdem;

import java.util.List;
import java.util.*;

public class HoldEm {
    public List<Player> players, winningPlayers;
    public Deck deck;
    public Hand communityCards, userHand, cpuHand;
    public int pot, currentPlayerIndex, pot_tie, betAmount;
    private guiController gui;
    public boolean tiebreakerFlag, turnDone, roundOver;
    public UserPlayer user;
    public CpuPlayer cpu;
    public GameState gameState, currentState;

    public enum GameState {
        PRE_FLOP, FLOP, TURN, RIVER, END
    }

    public HoldEm() {
        this.players = new ArrayList<>();
        this.deck = new Deck();
        this.communityCards = new Hand();
        this.pot = 0;
        this.currentPlayerIndex = 0;
        this.pot_tie = 0;
        this.tiebreakerFlag = false;
        this.roundOver = false;
        this.currentState = GameState.PRE_FLOP;
    }

    public void newGame(int startingChips) {
        players.clear();
        resetPot();
        user = new UserPlayer("Player 1", startingChips);
        cpu = new CpuPlayer("Cpu 1", startingChips);
        user.setHoldEm(this);
        cpu.setHoldEm(this);
        gui.initializeButtons();
        user.setBetToggle(gui.betToggleGroup);
        user.setGui(gui);
        cpu.setGui(gui);
        players.add(user);
        players.add(cpu);
        user.setChips(startingChips);
        cpu.setChips(startingChips);
        userHand = players.get(0).getHand();
        cpuHand = players.get(1).getHand();
        deck.shuffle();
        gameState = GameState.PRE_FLOP;
        startGame();
    }

    public void setGui(guiController gui) {
        this.gui = gui;
    }

    public void startGame() {
        winningPlayers = new ArrayList<>(players);
        dealInitialCards();
    }

    public void dealInitialCards() {
        for (Player player : players) {
            player.receiveCard(deck.deal());
            player.receiveCard(deck.deal());
        }
        gui.updateUserHandImg(userHand.getCards());
        nextStep();
    }

    public void nextStep() {
        switch (this.currentState) {
            case PRE_FLOP:
                gui.updateUI();
                bettingRound();
                gui.updateUI();

                this.currentState = GameState.FLOP;
                break;
            case FLOP:
                flop();
                this.currentState = GameState.TURN;
                break;
            case TURN:
                turn();
                this.currentState = GameState.RIVER;
                break;
            case RIVER:
                river();
                this.currentState = GameState.PRE_FLOP;
                break;
            default:
                // Do nothing
        }
        if (this.roundOver()) {
            if (this.players.size() > 1) {
                this.nextStep();
            } else {
                this.determineWinner();
            }
        }
    }

    private boolean roundOver() {
        for (Player player : players) {
            if (!player.isTurnDone()) {
                return false;
            }
        }

        // All players have acted. Reset the 'turnDone' flag for the next round.
        for (Player player : players) {
            player.setTurnDone(false);
        }

        return true;
    }

    // Check if all players have had a turn for the current round


    public GameState getNextGameState() {
        switch (gameState) {
            case PRE_FLOP:
                return GameState.FLOP;
            case FLOP:
                return GameState.TURN;
            case TURN:
                return GameState.RIVER;
            case RIVER:
            default:
                return GameState.END;
        }
    }



    public void flop() {
        dealCommunityCards(3);
        gui.updateUI();
        bettingRound();
    }

    public void turn() {
        dealCommunityCards(1);
        gui.updateUI();
        bettingRound();
    }
    public void river() {
        dealCommunityCards(1);
        gui.updateUI();
        determineWinner();
    }
    public void dealCommunityCards(int numCards) {
        for (int i = 0; i < numCards; i++) {
            communityCards.addCard(deck.deal());
        }
    }
    public void bettingRound() {
        int userChips = user.getChips();
        int cpuChips = cpu.getChips();

        for (Player player : players) {
            player.turnDone = false;
            if (!player.hasFolded()) {
                while(!player.turnDone) {
                    int bet = player.placeBet();
                    updatePot(bet);
                    player.turnDone = true;
                }
            }
        }

        gui.updatePotLabel(getPot());
        gui.updateUserChipsLabel(userChips);
        gui.updateCpuChipsLabel(cpuChips);
        if (winningPlayers.size() == 1) {
            Player winner = winningPlayers.get(0);
            declareWinner(winner);
        }
    }


    public void determineWinner() {
        List<Player> playersInGame = this.players; // get all players still in the game
        Hand communityCards = this.communityCards; // get the community cards

        // Evaluate the hand rank for all players
        for (Player player : playersInGame) {
            Hand playerHand = player.getHand();
            playerHand.evaluateHandRank(communityCards);
        }

        // Find the player(s) with the highest hand rank
        List<Player> winningPlayers = new ArrayList<>();
        Hand.HandRank highestRank = null;
        for (Player player : playersInGame) {
            Hand.HandRank playerRank = player.getHandRank(communityCards);
            if (highestRank == null || playerRank.getRankValue() > highestRank.getRankValue()) {
                highestRank = playerRank;
                winningPlayers.clear();
                winningPlayers.add(player);
            } else if (playerRank.getRankValue() == highestRank.getRankValue()) {
                winningPlayers.add(player);
            }
        }
        // If there's a tie, compare the tiebreaker ranks
        if (winningPlayers.size() > 1) {
            for (int i = 0; i < 5; i++) { // In case of multiple tiebreakers
                int highestTieBreaker = -1;
                List<Player> newWinningPlayers = new ArrayList<>();
                for (Player player : winningPlayers) {
                    List<Card.Rank> tieBreakers = player.getHand().getTieBreakers();
                    int playerTieBreaker = tieBreakers.get(i).ordinal();
                    if (playerTieBreaker > highestTieBreaker) {
                        highestTieBreaker = playerTieBreaker;
                        newWinningPlayers.clear();
                        newWinningPlayers.add(player);
                    } else if (playerTieBreaker == highestTieBreaker) {
                        newWinningPlayers.add(player);
                    }
                }
                winningPlayers = newWinningPlayers;
                if (winningPlayers.size() == 1) {
                    Player winner = winningPlayers.get(0);
                    declareWinner(winner);
                }
            }
        }
        // Handle the case where there's still a tie after all tiebreakers are exhausted
        // This will be a "push" where the pot carries over to the next hand
        if (winningPlayers.size() > 1) {
            // Show a dialog indicating a tie and the pot amount
            String message = "It's a tie! The pot will be carried over to the next round. Pot size is : "+ pot +" chips";
            gui.showTieDialog(message);

            // Carry over the pot amount
            pot_tie += pot;
            pot = 0;

            // Reset the community cards and players' hands for the next round
            communityCards.clear();
            for (Player player : playersInGame) {
                player.getHand().clear();
            }

            return;
        }
    }
    public void declareWinner(Player winner) {
        // Update the winner's chips
        winner.addChips(pot);
        pot = 0;

        // Display a dialog with the winner and the option to continue or save and exit
        String message = "The winner is: " + winner.getName();
        gui.displayWinnerDialog(winner);
    }

    public void updatePot(int betAmount) {
        pot += betAmount;
    }

    public Hand getCommunityCards() {
        return communityCards;
    }

    public List<Player> getPlayers() {
        return this.players;
    }
    public void continueGame() {

    }
    public void checkGameStatus() {

    }
    public int getPot(){
        return pot;
    }
    public void resetPot(){
        pot = 0;
    }
}
