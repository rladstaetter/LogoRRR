package net.ladstatt.logboard;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * small bootstrap java class which delegates to Scala code (AKA murky hack)
 */
public class LogboardApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        new LogboardApplication().start(stage);
    }
}