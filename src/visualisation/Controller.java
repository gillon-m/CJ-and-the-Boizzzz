package visualisation;

import javafx.concurrent.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
	@FXML public Button btn;
	@FXML public Label lbl;
	private Service<Void> backgroundThread;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("View is now loaded!");
	}

	public void click(){
		backgroundThread =new Service<Void>(){			//perform calculations in background thread
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>(){
					@Override
					protected Void call() throws Exception {
						for(int i = 0; i<=1000000000;i++){
							updateMessage("i: "+i);
						}
						return null;
					}
				};
			}
		};
		backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
			@Override
			public void handle(WorkerStateEvent event) {
				System.out.println("DOne");
				lbl.textProperty().unbind();
			}
		});

		lbl.textProperty().bind(backgroundThread.messageProperty());
		backgroundThread.restart();
	}


}