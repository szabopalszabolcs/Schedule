import javafx.application.Application;
import javafx.stage.Stage;

public class Schedule extends Application {

    @Override
    public void start(Stage primaryStage) {
        new MainMenu().createMainMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}