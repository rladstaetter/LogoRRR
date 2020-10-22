package net.ladstatt.logboard;

import javafx.application.Application;
import javafx.stage.Stage;

/** small bootstrap java class to workaround an error during graalvm compilation */
public class LogboardBootstrapper extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage stage) throws Exception {
        new LogboardApp().start(stage, getParameters());
    }
}
