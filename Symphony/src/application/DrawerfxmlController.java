package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DrawerfxmlController implements Initializable {
	@FXML
	private ImageView drawerimage;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Image img = new Image("resources/drawing.jpg");
		drawerimage.setImage(img);
	}

}
