package main.texholdem;

import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.shape.*;
import javafx.scene.control.Alert.*;
import java.util.*;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.*;
import javafx.scene.control.ToggleGroup;
import javafx.geometry.*;
import javafx.stage.*;


public class guiController{
    @FXML
    public ImageView flopCardImg1, flopCardImg2, flopCardImg3, turnCardImg1, riverCardImg1,userCardImg1,userCardImg2,cpuCardImg1,cpuCardImg2;
    @FXML
    public Button  betButton, foldButton, newGameButton, recursiveBest;
    @FXML
    public Label cpuChipsLabel, playerNameLabel, userChipsLabel, potAmountLabel;
    @FXML
    public Rectangle bestHandid;
    @FXML
    public ToggleGroup betToggleGroup;
    public HoldEm game;
    public Hand.HandRank rank;
    public guiController(HoldEm game) {
        this.game = game;
        this.game.setGui(this);
    }
    public void initializeButtons() {
    RadioButton rb1 = new RadioButton();
    rb1.selectedProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue) {
            game.user.setBetAmount(1);
        }
    });
    RadioButton rb10 = new RadioButton();
    rb10.selectedProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue) {
            game.user.setBetAmount(10);
        }
    });
    RadioButton rb100 = new RadioButton();
    rb100.selectedProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue) {
            game.user.setBetAmount(100);
        }
    });
    RadioButton rb1000 = new RadioButton();
    rb1000.selectedProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue) {
            game.user.setBetAmount(1000);
        }
    });
    }
    public void updateUI() {
        Hand communityCards = game.getCommunityCards();

        userChipsLabel.setText(String.valueOf(game.user.chips));
        cpuChipsLabel.setText(String.valueOf(game.cpu.chips));
        potAmountLabel.setText(String.valueOf(game.pot));

        if(!communityCards.isEmpty()){
        Card flop1 = communityCards.getCards().get(0);
        String fileName1 = flop1.getFileName();
        Image newImage1 = new Image(getClass().getResourceAsStream("/img/" + fileName1 + ".png"));
        flopCardImg1.setImage(newImage1);

        Card flop2 = communityCards.getCards().get(1);
        String fileName2 = flop2.getFileName();
        Image newImage2 = new Image(getClass().getResourceAsStream("/img/" + fileName2 + ".png"));
        flopCardImg2.setImage(newImage2);

        Card flop3 = communityCards.getCards().get(2);
        String fileName3 = flop3.getFileName();
        Image newImage3 = new Image(getClass().getResourceAsStream("/img/" + fileName3 + ".png"));
        flopCardImg3.setImage(newImage3);

        try {
            Card turn = communityCards.getCards().get(3);
            String fileName4 = turn.getFileName();
            Image turnImage = new Image(getClass().getResourceAsStream("/img/" + fileName4 + ".png"));
            turnCardImg1.setImage(turnImage);
        } catch (IndexOutOfBoundsException e1) {
            try {
                Card river = communityCards.getCards().get(4);
                String fileName5 = river.getFileName();
                Image riverImage = new Image(getClass().getResourceAsStream("/img/" + fileName5 + ".png"));
                riverCardImg1.setImage(riverImage);
            } catch (IndexOutOfBoundsException e2) {
            }
        }
    }
    }
    public void displayWinnerDialog(Player winner) {
        // Create a new stage for the dialog
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Winner!");
        dialogStage.setResizable(false);

        // Create an image view with the crown image
        ImageView crownImageView = new ImageView(new Image(getClass().getResourceAsStream("/img/crown.png")));
        crownImageView.setPreserveRatio(true); // Maintain the aspect ratio of the image


        // Create a label with the congratulatory message
        Label messageLabel = new Label("Congratulations, " + winner.getName() + "! You won!");

        // Create a VBox layout and add the image view and message label
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().addAll(crownImageView, messageLabel);

        // Create a scene with the VBox layout
        Scene scene = new Scene(vbox);

        // Set the scene to the dialog stage
        dialogStage.setScene(scene);

        // Show the dialog
        dialogStage.showAndWait();
    }
    public void showTieDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tie!");
        alert.setHeaderText("It's a tie!");
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void insufficientFunds(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Insufficient Funds");
        alert.setHeaderText("You do not have the required chips");
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void updatePotLabel(int potAmount) {
        potAmountLabel.setText("Pot: " + potAmount);
    }
    public void updateUserChipsLabel(int chips) {
        userChipsLabel.setText("Chips: " + chips);
    }
    public void updateCpuChipsLabel(int chips) {
        cpuChipsLabel.setText("Chips: " + chips);
    }

    @FXML
    public void updateUserHandImg(List<Card> hand) {
        Card card1 = hand.get(0);
        String fileName1 = card1.getFileName();
        Image newImage1 = new Image(getClass().getResourceAsStream("/img/" + fileName1 + ".png"));
        userCardImg1.setImage(newImage1);

        Card card2 = hand.get(1);
        String fileName2 = card2.getFileName();
        Image newImage2 = new Image(getClass().getResourceAsStream("/img/" + fileName2 + ".png"));
        userCardImg2.setImage(newImage2);
    }
    @FXML
    protected void onChangeNameClick() {
        if (game.user != null) {
            TextInputDialog dialogName = new TextInputDialog(game.user.getName());
            dialogName.setTitle("Change Name");
            dialogName.setHeaderText("Enter a new name:");
            dialogName.setContentText("Name:");

            Optional<String> resultName = dialogName.showAndWait();

            if (resultName.isPresent() && !resultName.get().trim().isEmpty() && resultName.get().trim().matches("[a-zA-Z]+")) {
                // Change the player's name and update the label
                game.user.setName(resultName.get().trim());
                playerNameLabel.setText(resultName.get().trim());
            } else {
                // Show an error dialog if the input was empty or contained non-alphabetic characters
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Invalid name");
                alert.setContentText("Name cannot be empty and must contain only alphabetic characters.");
                alert.showAndWait();
            }
        } else {
            game.user.setName("Player 1");
            TextInputDialog dialogName = new TextInputDialog(game.user.getName());
            dialogName.setTitle("Change Name");
            dialogName.setHeaderText("Enter a new name:");
            dialogName.setContentText("Name:");

            Optional<String> resultName = dialogName.showAndWait();

            if (resultName.isPresent() && !resultName.get().trim().isEmpty() && resultName.get().trim().matches("[a-zA-Z]+")) {
                // Change the player's name and update the label
                game.user.setName(resultName.get().trim());
                playerNameLabel.setText(resultName.get().trim());
            } else {
                // Show an error dialog if the input was empty or contained non-alphabetic characters
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Invalid name");
                alert.setContentText("Name cannot be empty and must contain only alphabetic characters.");
                alert.showAndWait();
            }
        }
    }
    @FXML
    protected void onNewGameClick () {
        if (this.game != null) {
            TextInputDialog dialogChips = new TextInputDialog("1000");
            dialogChips.setTitle("New Game");
            dialogChips.setHeaderText("Enter the starting number of chips:");
            dialogChips.setContentText("Chips:");
            boolean validInput = false;
            while (!validInput) {
                try {
                    // Show the dialog and wait for the user's response
                    String resultChips = dialogChips.showAndWait().get();
                    // Validate the input
                    int startingChips = Integer.parseInt(resultChips);
                    if (startingChips <= 0) {
                        throw new NumberFormatException("Number of chips must be positive.");
                    }
                    // If we've reached this point, the input is valid
                    validInput = true;
                    game.newGame(startingChips); // Start a new game with the specified number of chips
                } catch (NumberFormatException e) {
                    // If the input wasn't a valid integer, show an error message and then show the dialog again
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Invalid number of chips");
                    alert.setContentText("Please enter a positive integer.");
                    alert.showAndWait();
                }
            }
        }
    }
    @FXML
    protected void onRecursiveClick() {
        // Set the bestHandid rectangle's visibility to true
        bestHandid.setVisible(true);
        rank = Hand.HandRank.HIGH_CARD;
        // Adjust the Y position of the rectangle based on the HandRank option
        rank = game.user.getBestHandRank();
        switch (rank) {
            case HIGH_CARD:
                // Adjust the Y position for HIGH_CARD
                bestHandid.setLayoutY(750);
                bestHandid.setVisible(true);
                break;
            case PAIR:
                // Adjust the Y position for PAIR
                bestHandid.setLayoutY(705);
                bestHandid.setVisible(true);
                break;
            case TWO_PAIR:
                // Adjust the Y position for TWO_PAIR
                bestHandid.setLayoutY(665);
                bestHandid.setVisible(true);
                break;
            case THREE_OF_A_KIND:
                // Adjust the Y position for THREE_OF_A_KIND
                bestHandid.setLayoutY(620);
                bestHandid.setVisible(true);
                break;
            case STRAIGHT:
                // Adjust the Y position for STRAIGHT
                bestHandid.setLayoutY(580);
                bestHandid.setVisible(true);
                break;
            case FLUSH:
                // Adjust the Y position for FLUSH
                bestHandid.setLayoutY(540);
                bestHandid.setVisible(true);
                break;
            case FULL_HOUSE:
                // Adjust the Y position for FULL_HOUSE
                bestHandid.setLayoutY(495);
                bestHandid.setVisible(true);
                break;
            case FOUR_OF_A_KIND:
                // Adjust the Y position for FOUR_OF_A_KIND
                bestHandid.setLayoutY(455);
                bestHandid.setVisible(true);
                break;
            case STRAIGHT_FLUSH:
                // Adjust the Y position for STRAIGHT_FLUSH
                bestHandid.setLayoutY(420);
                bestHandid.setVisible(true);
                break;
            case ROYAL_FLUSH:
                // Adjust the Y position for ROYAL_FLUSH
                bestHandid.setLayoutY(375);
                bestHandid.setVisible(true);
                break;
            default:
                // Handle any other cases if needed
                break;
        }
    }
    @FXML
    protected void onFoldButton() {

    }
    @FXML
    protected void onBetButton(){}
    public int getSelectedBetAmount(guiController gui) {

        RadioButton selectedRadioButton = (RadioButton) betToggleGroup.getSelectedToggle();
        String selectedBetAmount = selectedRadioButton.getText();
        boolean validInput = false;
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Invalid number of chips");
        alert.set;
        alert.showAndWait();
        return Integer.parseInt(selectedBetAmount);
    }
    }