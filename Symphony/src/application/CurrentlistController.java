package application;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import com.jfoenix.controls.JFXListView;

public class CurrentlistController implements Initializable {
	@FXML
	private AnchorPane current;
	@FXML
	private JFXListView<Label> listcurr;
	@FXML
	private JFXButton addsongscurr;
	JFXButton bt;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		current.setOpacity(0.0);
		
		FadeTransition fadein = new FadeTransition(Duration.millis(300), current);
		
		fadein.setFromValue(0.0);
		fadein.setToValue(1.0);
		
		fadein.play();
		
	}


	
}
