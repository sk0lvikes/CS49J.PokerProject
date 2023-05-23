package main.texholdem;

import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.Stage;

public class Main extends Application{

    @Override //setup game state
    public void init() throws Exception{
        super.init();
    }

    @Override
    public void start(Stage tableStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("table.fxml"));
        HoldEm game = new HoldEm();
        guiController gui = new guiController(game);
        loader.setController(gui);
        Scene scene = new Scene(loader.load());
        tableStage.setTitle("CS-49J Texas Hold'em Project");
        tableStage.setScene(scene);
        tableStage.show();
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();
        });
        launch();
    }
}
