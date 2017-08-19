package visualisation;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Visualiser extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        Label lb = new Label();

        StackPane root = new StackPane();
        root.getChildren().add(lb);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }
}