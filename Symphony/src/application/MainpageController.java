package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class MainpageController implements Initializable {
	//declarations
	@FXML
    private AnchorPane rootPane;

    @FXML
    private JFXHamburger menu;

    @FXML
    private AnchorPane playerpane;

    @FXML
    private ImageView musicicon;

    @FXML
    private Slider durationslider;

    @FXML
    private Label songname;

    @FXML
    private Label songduration;

    @FXML
    private JFXButton play;

    @FXML
    private JFXButton seekleft;

    @FXML
    private JFXButton seekright;

    @FXML
    private Slider volumeslider;

    @FXML
    private JFXDrawer menudrawer;

    @FXML
    private AnchorPane viewerpane;
    
    @FXML 
    private MediaView media;
    
    @FXML
    private AnchorPane current;
    
    @FXML
    CurrentlistController curr;
    
    @FXML
    private JFXButton cycler;
    
    @FXML
    private Label namelabel;
    
    private VBox box;
    private AnchorPane pa;
    private AnchorPane playlist;
    private ContextMenu popup;
    private ContextMenu popup1;

    private JFXListView<Label> lister;
    private JFXListView<Label> playListMenu;
    private final ObservableList<String> data = FXCollections.observableArrayList();
    private final ObservableList<String> dataname = FXCollections.observableArrayList();
    private final ObservableList<String> playlistName = FXCollections.observableArrayList();
    private final ObservableList<String> data1 = FXCollections.observableArrayList();
    private final ObservableList<String> dataname1 = FXCollections.observableArrayList();
    
    private boolean stopRequested = false;
	private boolean repeat = false;
	private Duration duration;
	private boolean atEndOfMedia = false;
	private boolean playing = false;
	private int buttoncounter = 0;
	private Map<String, Integer> listMap;
	
	Media me;
	MediaPlayer mp;
    

	//initialization
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		listMap = new HashMap<String, Integer>();
		Image img = new Image("resources/logo.jpg");
		musicicon.setImage(img);
		
		try{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayListViewer.fxml"));
			playlist = loader.load();
			viewerpane.getChildren().setAll(playlist);
				for(Node chil : playlist.getChildren()){
					if(chil.getAccessibleText().equals("playlistsMenu")){
						playListMenu = (JFXListView<Label>) chil;
					}
				}
				playListMenu.setDepth(1);
				playListMenu.setVerticalGap(10.0);
				playListMenu.setExpanded(true);
				popup1 = new ContextMenu();
				
				popup1.setOnAction(new EventHandler<ActionEvent>(){

				@Override
				public void handle(ActionEvent ev) {
					int inde = playListMenu.getSelectionModel().getSelectedIndex();
					playlistName.remove(inde);
					playListMenu.getItems().remove(inde);
					File fi = new File("src/resources/Playlists.txt");
					if(fi.exists()){
						try{	
							fi.delete();
							File fie = new File("src/resources/Playlists.txt");
							fie.createNewFile();
							PrintWriter p = new PrintWriter(new BufferedWriter(new FileWriter(fie, true)));
							for(int i = 0; i < playlistName.size(); i++){
								p.println(playlistName.get(i));
							}
							p.close();
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
				}
			});
			popup1maker();
			playListMenu.setContextMenu(popup1);
			ListViewButtonMaker();
			playListMenuController();
			menudrawer.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		File playlistname = new File("src/resources/Playlists.txt");
		
		if(playlistname.exists()){
			try {
				BufferedReader br = new BufferedReader(new FileReader(playlistname));
				String line;
				while((line = br.readLine()) != null){
					listMap.put(line, 1);
				}
				br.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Drawerfxml.fxml"));
			//loader.setController(cont);
			box = (VBox)loader.load();
			menudrawer.setSidePane(box);
			sidepanelaction();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		hammaker();
		
		
	}
	
	//menu bar in the form of hamburger
	public void hammaker(){
		HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(menu);
		transition.setRate(-1);
		menu.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
			transition.setRate(transition.getRate() * -1);
			transition.play();
			if(menudrawer.isShown()){
				menudrawer.close();
			}
			else{
				menudrawer.open();
			}
		});
	}
	
	//sidepanel of menubar assigning actions
	@SuppressWarnings("unchecked")
	public void sidepanelaction(){
		for(Node node : box.getChildren()){
			//System.out.println("1");
			if(node.getAccessibleText() != null){
				node.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
					switch(node.getAccessibleText()){
					case "nowplaying" :{//currently playing
						namelabel.setText("NOW PLAYING");
						try {
							FXMLLoader loader = new FXMLLoader(getClass().getResource("Currentlist.fxml"));
							pa = loader.load();
							viewerpane.getChildren().setAll(pa);
							for(Node n3 : pa.getChildren()){
								if(n3.getAccessibleText().equals("listcurr")){
									lister = (JFXListView<Label>) n3;
								}
							}
							lister.setExpanded(true);
							lister.depthProperty().set(1);
							popup = new ContextMenu();
							popup.setOnAction(new EventHandler<ActionEvent>(){

								@Override
								public void handle(ActionEvent arg0) {
									// TODO Auto-generated method stub
									int indexremoval = lister.getSelectionModel().getSelectedIndex();
									lister.getItems().remove(indexremoval);
									data.remove(indexremoval);
									if(playing == true){
										mp.stop();
										playing = false;
									}
								}
								
							});
							popupmaker();
							lister.setContextMenu(popup);
							addSongstoList();
							currentListLoader();
							
							menudrawer.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						break;
					}
					case "myplaylist":{//all playlists
						namelabel.setText("PLAYLISTS");
						try{
							FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayListViewer.fxml"));
							playlist = loader.load();
							viewerpane.getChildren().setAll(playlist);
							for(Node chil : playlist.getChildren()){
								if(chil.getAccessibleText().equals("playlistsMenu")){
									playListMenu = (JFXListView<Label>) chil;
								}
							}
							playListMenu.setDepth(1);
							playListMenu.setVerticalGap(10.0);
							playListMenu.setExpanded(true);
							popup1 = new ContextMenu();
							popup1.setOnAction(new EventHandler<ActionEvent>(){

							@Override
							public void handle(ActionEvent ev) {
								int inde = playListMenu.getSelectionModel().getSelectedIndex();
								playlistName.remove(inde);
								playListMenu.getItems().remove(inde);
								File fi = new File("src/resources/Playlists.txt");
								if(fi.exists()){
									try{
										fi.delete();
										File fie = new File("src/resources/Playlists.txt");
										fie.createNewFile();
										listMap.clear();
										PrintWriter p = new PrintWriter(new BufferedWriter(new FileWriter(fie, true)));
										for(int i = 0; i < playlistName.size(); i++){
											p.println(playlistName.get(i));
											listMap.put(playlistName.get(i), 1);
										}
										p.close();
									}catch(Exception ex){
										ex.printStackTrace();
									}
								}
							}
						});
							popup1maker();
							playListMenu.setContextMenu(popup1);
						ListViewButtonMaker();
						playListMenuController();
						menudrawer.close();
					}catch(Exception ex){
						ex.printStackTrace();
					}
					break;
					}
					case "newplaylist":{
						namelabel.setText("CREATE NEW PLAYLIST");
						try{
							FXMLLoader loader = new FXMLLoader(getClass().getResource("Currentlist.fxml"));
							pa = loader.load();
							viewerpane.getChildren().setAll(pa);
							for(Node n3 : pa.getChildren()){
								if(n3.getAccessibleText().equals("listcurr")){
									lister = (JFXListView<Label>) n3;
								}
							}
							data1.clear();
							dataname1.clear();
							lister.setExpanded(true);
							lister.depthProperty().set(1);
							popup = new ContextMenu();
							popup.setOnAction(new EventHandler<ActionEvent>(){

								@Override
								public void handle(ActionEvent arg0) {
									// TODO Auto-generated method stub
									int indexremoval = lister.getSelectionModel().getSelectedIndex();
									lister.getItems().remove(indexremoval);
									data1.remove(indexremoval);
									if(playing == true){
										mp.stop();
										playing = false;
									}
								}
								
							});
							popupmaker();
							lister.setContextMenu(popup);
							currentListLoader1();
							menudrawer.close();
						}catch(Exception ex){
							ex.printStackTrace();
						}
						break;
					}
					case "exit": System.exit(0);
						break;	
					}
				});
			}
		}
	}

	//creating playlist menu on playlistpanel
	private void playListMenuController() {
		playListMenu.setOnMouseClicked(new EventHandler<MouseEvent>(){

			@SuppressWarnings("unchecked")
			@Override
			public void handle(MouseEvent ev) {
				if(ev.getButton().equals(MouseButton.SECONDARY)){
					popup1.show(playListMenu, ev.getX(), ev.getY());
				}else{
					int in = playListMenu.getSelectionModel().getSelectedIndex();
					data.clear();
					dataname.clear();
					String st = playlistName.get(in);
					String filename = "src/resources/" + st + ".txt";
					String filenamepath = "src/resources/" + st + "paths.txt";
					try {
						BufferedReader reader = new BufferedReader(new FileReader(filename));
						BufferedReader reader1 = new BufferedReader(new FileReader(filenamepath));
						String line;
						String line1;
						while((line = reader.readLine()) != null){
							dataname.add(line);
						}
						while((line1 = reader1.readLine()) != null){
							data.add(line1);
						}
						reader.close();
						reader1.close();
						namelabel.setText("NOW PLAYING");
						pa = FXMLLoader.load(getClass().getResource("Currentlist.fxml"));
						viewerpane.getChildren().setAll(pa);
						for(Node n3 : pa.getChildren()){
							if(n3.getAccessibleText().equals("listcurr")){
								lister = (JFXListView<Label>) n3;
							}
						}
						lister.setExpanded(true);
						lister.depthProperty().set(1);
						popup = new ContextMenu();
						popup.setOnAction(new EventHandler<ActionEvent>(){

							@Override
							public void handle(ActionEvent arg0) {
								int indexremoval = lister.getSelectionModel().getSelectedIndex();
								lister.getItems().remove(indexremoval);
								data.remove(indexremoval);
								if(playing == true){
									mp.stop();
									playing = false;
								}
							}
						});
						popupmaker();
						lister.setContextMenu(popup);
						addSongstoList();
						currentListLoader();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	private void currentListLoader1() {
		for(Node n1 : pa.getChildren()){
			n1.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
				switch(n1.getAccessibleText()){
					case "addlistcurr":{
						addSongListener1();
						break;
					}
					case "createlistcurr":{
						TextInputDialog dialog = new TextInputDialog();
						dialog.setTitle("Playlist");
						dialog.setHeaderText("Enter the name of Playlist");
						Optional<String> result = dialog.showAndWait();
						if(result.isPresent()){
							try {
								if(listMap.containsKey(result.get())){
									Alert alert = new Alert(AlertType.CONFIRMATION);
									alert.setTitle("");
									alert.setHeaderText("Playlist already exists");
									alert.setContentText("Do you want to overwrite the previous playlist");
									Optional<javafx.scene.control.ButtonType> result1 = alert.showAndWait();
									if(result1.get().getText().equals("OK")){
										String d = result.get();
										String musicfilename = "src/resources/" + d + ".txt";
										String musicfilepath = "src/resources/" + d + "paths.txt";
										
										File f1 = new File(musicfilename);
										File f2 = new File(musicfilepath);
										
										f1.delete();
										f2.delete();
										
										File f3 = new File(musicfilename);
										File f4 = new File(musicfilepath);
										f3.createNewFile();
										f4.createNewFile();
										
										PrintWriter p1 = new PrintWriter(f3);
										PrintWriter p2 = new PrintWriter(f4);
										
										int i;
										for(i = 0; i < data1.size(); i++){
											p2.println(data1.get(i));
										}
										
										for(i = 0; i < dataname1.size(); i++){
											p1.println(dataname1.get(i));
										}
										data1.clear();
										dataname1.clear();
										p1.close();
										p2.close();
									}
								}else{
									listMap.put(result.get(), 1);
									PlayListFile1(result.get());
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}
				}
			});
		}

		
	}

	private void PlayListFile1(String s) throws IOException {
		File listFile = new File("src/resources/Playlists.txt");
		if(!listFile.exists()){
			//System.out.print("newer");
			listFile.createNewFile();
		}
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(listFile, true)));
		writer.println(s);
		String fileMusicname = "src/resources/" + s + ".txt"; 
		String fileMusicPathname = "src/resources/" + s + "paths.txt";
		File MusicName = new File(fileMusicname);
		File MusicPath = new File(fileMusicPathname);
		
		MusicName.createNewFile();
		MusicPath.createNewFile();
		
		PrintWriter p1 = new PrintWriter(MusicName);
		PrintWriter p2 = new PrintWriter(MusicPath);
		
		int i;
		for(i = 0; i < data1.size(); i++){
			p2.println(data1.get(i));
		}
		
		for(i = 0; i < dataname1.size(); i++){
			p1.println(dataname1.get(i));
		}
		data1.clear();
		dataname1.clear();
		writer.close();
		p1.close();
		p2.close();
	}

	private void addSongListener1() {
		FileChooser fc = new FileChooser();
		File selected = fc.showOpenDialog(null);
		
		if(selected != null){
			Label lab = new Label();
			lab.setText(selected.getName());
			lister.getItems().add(lab);
			FadeTransition fadein = new FadeTransition(Duration.millis(300), lab);
			fadein.setFromValue(0.0);
			fadein.setToValue(1.0);
			data1.add(selected.getAbsolutePath());
			fadein.play();
			dataname1.add(selected.getName());
		}
		
		
	}

	//playlist adding items to list view of playlists
	private void ListViewButtonMaker() {
		try{
			File playerLister = new File("src/resources/Playlists.txt");
			if(playerLister.exists()){
				BufferedReader rd = new BufferedReader(new FileReader(playerLister));
				String line;
				while((line = rd.readLine()) != null){
					Label la = new Label();
					la.setText(line);
					playListMenu.getItems().add(la);
					playlistName.add(line);
				}
				rd.close();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	//creating a popup for adding delete option to currently playing list view
	private void popupmaker() {
		MenuItem b1 = new MenuItem("");
		b1.setText("remove");
		popup.getItems().addAll(b1);
	}
	
	private void popup1maker(){
		MenuItem b1 = new MenuItem("remove");
		popup1.getItems().addAll(b1);
	}

	//loading the now playing pane
	private void currentListLoader() {
		for(Node n1 : pa.getChildren()){
			n1.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
				switch(n1.getAccessibleText()){
					case "addlistcurr":{
						addSongListener();
						break;
					}
					case "listcurr":{
						listController();
						break;
					}
					case "createlistcurr":{
						TextInputDialog dialog = new TextInputDialog();
						dialog.setTitle("Playlist");
						dialog.setHeaderText("Enter the name of Playlist");
						Optional<String> result = dialog.showAndWait();
						if(result.isPresent()){
							try {
								if(listMap.containsKey(result.get())){
									Alert alert = new Alert(AlertType.CONFIRMATION);
									alert.setTitle("");
									alert.setHeaderText("Playlist already exists");
									alert.setContentText("Do you want to overwrite the previous playlist");
									Optional<javafx.scene.control.ButtonType> result1 = alert.showAndWait();
									if(result1.get().getText().equals("OK")){
										String d = result.get();
										String musicfilename = "src/resources/" + d + ".txt";
										String musicfilepath = "src/resources/" + d + "paths.txt";
										
										File f1 = new File(musicfilename);
										File f2 = new File(musicfilepath);
										
										f1.delete();
										f2.delete();
										
										File f3 = new File(musicfilename);
										File f4 = new File(musicfilepath);
										f3.createNewFile();
										f4.createNewFile();
										
										PrintWriter p1 = new PrintWriter(f3);
										PrintWriter p2 = new PrintWriter(f4);
										
										int i;
										for(i = 0; i < data.size(); i++){
											p2.println(data.get(i));
										}
										
										for(i = 0; i < dataname.size(); i++){
											p1.println(dataname.get(i));
										}
										
										p1.close();
										p2.close();
									}
								}else{
									listMap.put(result.get(), 1);
									PlayListFile(result.get());
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}
				}
			});
		}
		
	}

	//creating new playlist
	private void PlayListFile(String s) throws IOException {
		File listFile = new File("src/resources/Playlists.txt");
		if(!listFile.exists()){
			//System.out.print("newer");
			listFile.createNewFile();
		}
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(listFile, true)));
		writer.println(s);
		String fileMusicname = "src/resources/" + s + ".txt"; 
		String fileMusicPathname = "src/resources/" + s + "paths.txt";
		File MusicName = new File(fileMusicname);
		File MusicPath = new File(fileMusicPathname);
		
		MusicName.createNewFile();
		MusicPath.createNewFile();
		
		PrintWriter p1 = new PrintWriter(MusicName);
		PrintWriter p2 = new PrintWriter(MusicPath);
		
		int i;
		for(i = 0; i < data.size(); i++){
			p2.println(data.get(i));
		}
		
		for(i = 0; i < dataname.size(); i++){
			p1.println(dataname.get(i));
		}
		writer.close();
		p1.close();
		p2.close();
	}
	
	//adding songs if playing from a playlist or switching between screens
	private void addSongstoList() {
		for(int i = 0; i < data.size(); i++){
			Label l1 = new Label();
			l1.setText(dataname.get(i));
			lister.getItems().add(l1);
		}
		
	}

	//adding items to now playing list view for playing meu
	private void addSongListener() {
		FileChooser fc = new FileChooser();
		File selected = fc.showOpenDialog(null);
		
		if(selected != null){
			Label lab = new Label();
			lab.setText(selected.getName());
			lister.getItems().add(lab);
			FadeTransition fadein = new FadeTransition(Duration.millis(300), lab);
			fadein.setFromValue(0.0);
			fadein.setToValue(1.0);
			data.add(selected.getAbsolutePath());
			fadein.play();
			dataname.add(selected.getName());
		}
		
	}

	//controller for now playing listview	
	private void listController() {
		lister.setOnMouseClicked(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent ev) {
				if(ev.getButton().equals(MouseButton.SECONDARY)){
					popup.show(lister, ev.getX(), ev.getY());
				}
				else{
					rootPane.requestFocus();
					rootPane.setMouseTransparent(false);
					if(playing == true){
						mp.pause();
					}
					int i = lister.getSelectionModel().getSelectedIndex();
					MusicController(i);
				
				}
			}
		});
	}
	
	//music controller for loading and playing music
	private void MusicController(int index) {
		String path = data.get(index);
		final Integer ind2 = new Integer(index);
		index = index + 1;
		final Integer ind = new Integer(index);
		final Integer ind1 = new Integer(0);
		me = new Media(new File(path).toURI().toString());
		mp = new MediaPlayer(me);
		play.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				javafx.scene.media.MediaPlayer.Status status = mp.getStatus();
		         
                if (status == javafx.scene.media.MediaPlayer.Status.UNKNOWN  || status == javafx.scene.media.MediaPlayer.Status.HALTED)
                {
                   // don't do anything in these states
                   return;
                }
         
                if ( status == javafx.scene.media.MediaPlayer.Status.PAUSED || status == javafx.scene.media.MediaPlayer.Status.READY || status == javafx.scene.media.MediaPlayer.Status.STOPPED)
                {
                     // rewind the movie if we're sitting at the end
                     if (atEndOfMedia) {
                        mp.seek(mp.getStartTime());
                        atEndOfMedia = false;
                     }
                     mp.play();
                } else {
                       mp.pause();
                }
			}
		});
		
		seekright.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				javafx.scene.media.MediaPlayer.Status status = mp.getStatus();

				if (status == javafx.scene.media.MediaPlayer.Status.UNKNOWN  || status == javafx.scene.media.MediaPlayer.Status.HALTED)
                {
                   // don't do anything in these states
                   return;
                }
				
				mp.seek(mp.getTotalDuration());
				mp.play();
			}
			
		});	
		
		seekleft.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				javafx.scene.media.MediaPlayer.Status status = mp.getStatus();

				if (status == javafx.scene.media.MediaPlayer.Status.UNKNOWN  || status == javafx.scene.media.MediaPlayer.Status.HALTED)
                {
                   // don't do anything in these states
                   return;
                }
				
				mp.seek(mp.getStartTime());
				mp.play();
				
			}
			
		});
		
		mp.currentTimeProperty().addListener(new InvalidationListener() 
        {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });
		
		mp.setOnPlaying(new Runnable(){

			@Override
			public void run() {
				if(stopRequested){
					mp.pause();
					stopRequested = false;
					play.setText("I>");
				} else{
					mp.play();
					play.setText("||");
				}
				playing = true;
			}
			
		});
		
		mp.setOnPaused(new Runnable(){

			@Override
			public void run() {
				play.setText("I>");
				
			}
			
		});
		
		mp.setOnReady(new Runnable(){

			@Override
			public void run() {
				duration = mp.getMedia().getDuration();
				songname.setText((String)mp.getMedia().getMetadata().get("title"));
				Image img1 = (Image) mp.getMedia().getMetadata().get("image");
				if(img1 != null)
					musicicon.setImage(img1);
				updateValues();
			}
			
		});
		
		mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
		
		mp.setOnEndOfMedia(new Runnable(){

			@Override
			public void run() {
				play.setText("I>");
				if(cycler.getText().equals("repeat 1")){
					MusicController(ind2);
				}else {
					if(ind < data.size()){
						MusicController(ind);
					}else{
						if(cycler.getText().equals("repeat")){
							MusicController(ind1);
						}else{
							playing = false;
						}
					}
				}
			}
			
		});
		
		media.setMediaPlayer(mp);
		
		DoubleProperty width = media.fitWidthProperty();
		DoubleProperty height = media.fitHeightProperty();
		width.bind(Bindings.selectDouble(media.sceneProperty(), "width"));
		height.bind(Bindings.selectDouble(media.sceneProperty(), "height"));
		volumeslider.setValue(mp.getVolume() * 100);
		volumeslider.valueChangingProperty().addListener(new InvalidationListener(){

			@Override
			public void invalidated(Observable arg0) {
				// TODO Auto-generated method stub
				mp.setVolume(volumeslider.getValue()/100);
			}
			
		});
		
		
		durationslider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (durationslider.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    mp.seek(duration.multiply(durationslider.getValue() / 100.0));
                }
            }
        });
		rootPane.setFocusTraversable(true);
		mp.play();
	}
	
	//updating values of timeslider volumeslider and labels 
		protected void updateValues() {
			  if (songduration != null && durationslider != null && volumeslider != null) {
			     Platform.runLater(new Runnable() {
			        @SuppressWarnings("deprecation")
					public void run() {
			          Duration currentTime = mp.getCurrentTime();
			          songduration.setText(formatTime(currentTime, duration));
			          durationslider.setDisable(duration.isUnknown());
			          if (!durationslider.isDisabled() 
			            && duration.greaterThan(Duration.ZERO) 
			            && !durationslider.isValueChanging()) {
			        	  durationslider.setValue(currentTime.divide(duration).toMillis()
			                  * 100.0);
			          }
			          if (!volumeslider.isValueChanging()) {
			        	  volumeslider.setValue((int)Math.round(mp.getVolume() 
			                  * 100));
			          }
			        }
			     });
			  }
		}
		
		//formatting time from (Duration) to (String)
		private static String formatTime(Duration elapsed, Duration duration) {
			   int intElapsed = (int)Math.floor(elapsed.toSeconds());
			   int elapsedHours = intElapsed / (60 * 60);
			   if (elapsedHours > 0) {
			       intElapsed -= elapsedHours * 60 * 60;
			   }
			   int elapsedMinutes = intElapsed / 60;
			   int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;
			 
			   if (duration.greaterThan(Duration.ZERO)) {
			      int intDuration = (int)Math.floor(duration.toSeconds());
			      int durationHours = intDuration / (60 * 60);
			      if (durationHours > 0) {
			         intDuration -= durationHours * 60 * 60;
			      }
			      int durationMinutes = intDuration / 60;
			      int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
			      if (durationHours > 0) {
			         return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds, durationHours, durationMinutes, durationSeconds);
			      } else {
			          return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds,durationMinutes, durationSeconds);
			      }
			      } else {
			          if (elapsedHours > 0) {
			             return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
			          } else {
			                return String.format("%02d:%02d",elapsedMinutes, elapsedSeconds);
			          }
			      }
		}
		
	    //listener for repeat button
	    @FXML
	    void repeatListener(ActionEvent event) {
	    	if(buttoncounter % 3 == 0){
	    		cycler.setText("repeat");
	    	}else if(buttoncounter % 3 == 1){
	    		cycler.setText("repeat 1");
	    	}else{
	    		cycler.setText("no repeat");
	    	}
	    	buttoncounter++;
	    }
}
