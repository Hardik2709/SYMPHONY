package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class PlayListViewerController implements Initializable{
	@FXML
	private AnchorPane rootPane;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		rootPane.setOpacity(0.0);
		
		FadeTransition fadein = new FadeTransition(Duration.millis(300), rootPane);
		
		fadein.setFromValue(0.0);
		fadein.setToValue(1.0);
		
		fadein.play();
		
	}

}
