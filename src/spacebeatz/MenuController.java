package spacebeatz;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javafx.animation.Animation;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import spacebeatzgame.SpaceBeatzGame;


public class MenuController {
	
	/**
	 * 
	 */
	@FXML
	private AnchorPane anchorPane;
	/**
	 * 
	 */
	@FXML
	private ImageView spaceBeatzLogo;
	/**
	 * 
	 */
	@FXML
	private Label chosenSong;
	/**
	 * 
	 */
	@FXML
	private Button browseButton;
	/**
	 * 
	 */
	@FXML
	private Button playButton;
	/**
	 * 
	 */
	@FXML
	private Button resumeButton;
	/**
	 * 
	 */
	@FXML
	private Button newGameButton;
	/**
	 * 
	 */
	@FXML
	private MenuItem quitMenuItem;
	/**
	 * 
	 */
	@FXML
	private MenuItem aboutMenuItem;
	/**
	 * 
	 */
	@FXML
	private ImageView bigBang;
	/**
	 * 
	 */
	@FXML
	private Label chooseSongLabel;
	/**
	 * 
	 */
	@FXML
	private Group textFieldGroup;
	/**
	 * 
	 */
	@FXML
	private CheckBox imageSmothingCheckBox;
	/**
	 * 
	 */
	@FXML
	private CheckBox circleVisualCheckBox;
	//Non-FXML Variables
	//-----------------------------------------------------------------------------------------------------------
	private String songPath;
	/**
	 * 
	 */
	private Media backgroundMusicAudio;
	/**
	 * 
	 */
	private MediaPlayer backgroundMusicPlayer;
	/**
	 * gameStage is passed to the spaceBeatzGame class
	 * so the controller will have access to its stage
	 */
	private Stage gameStage;
	
	SpaceBeatzGame game;
	

	@FXML
	public void initialize() {
		try {
			/*	
			 *  Music Source:
			 * 		Voltaic Kevin MacLeod (incompetech.com)
			 * 		Licensed under Creative Commons: By Attribution 3.0 License
			 * 		http://creativecommons.org/licenses/by/3.0/
			 */    		
			File file = new File("src/spacebeatz/res/backgroundmusic.mp3");
			URI uri = file.toURI();
			URL url = uri.toURL();

			newGameButton.setOnAction(event -> {
				newGameButtonListener();
			});

			backgroundMusicAudio = new Media(url.toString());
			backgroundMusicPlayer = new MediaPlayer(backgroundMusicAudio);
			backgroundMusicPlayer.setVolume(0.2);
			backgroundMusicPlayer.setCycleCount(Animation.INDEFINITE);
			backgroundMusicPlayer.play();
		} 
		catch (MalformedURLException | NullPointerException | IllegalArgumentException err) {
			err.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}   	
	}

	//-----------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void browseButtonListener() {
		// Open up a FileChooser and let the user choose a track to play
		FileChooser fileChooser = new FileChooser();
		// Filter only mp3 files
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.mp3");
		fileChooser.getExtensionFilters().add(extFilter);
		Window stage = null;  // TODO: Is this needed? Couldn't figure out how to implement a FileChooser without a stage
		File fileChosen = fileChooser.showOpenDialog(stage);
		if (fileChosen != null) {
			String songName;
			songName = fileChosen.getAbsolutePath();
			songPath = songName;
			int slashPos = songName.lastIndexOf("\\");
			songName = songName.substring(slashPos+1,songName.length());
			chosenSong.setText(songName); 
			playButton.setDisable(false);

		}
	}

	//-----------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void playButtonListener() {
		try {
			// Since the media player successfully accessed the audio file, we stop playing the menu music
			if (backgroundMusicPlayer.getStatus() != Status.STOPPED) {
				backgroundMusicPlayer.stop();
			}
			File musicFile = new File(songPath);
			URI uri = musicFile.toURI();
			URL url = uri.toURL();

			gameStage = new Stage();
			// TODO: There may be a better way to handle this
			game = new SpaceBeatzGame(url,gameStage,imageSmothingCheckBox.isSelected(),circleVisualCheckBox.isSelected());
			browseButton.setDisable(true);
			browseButton.setVisible(false);
			browseButton.setFocusTraversable(false);
			playButton.setDisable(true);
			playButton.setVisible(false);
			resumeButton.setVisible(true);
			resumeButton.setDisable(false);
			resumeButton.setFocusTraversable(true);
			newGameButton.setVisible(true);
			newGameButton.setDisable(false);
			newGameButton.setFocusTraversable(true);
			imageSmothingCheckBox.setDisable(true);
			circleVisualCheckBox.setDisable(true);

		} catch (NullPointerException | MalformedURLException | IllegalArgumentException e1) {
			System.err.println("No song found!");
		} catch (Exception e2) {
			System.err.print(e2.getMessage());
		}
	}

	//-----------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void resumeButtonListener() { 
		// If the game is not running, that means the game is hidden and will be opened back up
		// Else, the game is over and we start a new game 
		if(!game.isRunning())
			gameStage.show();
		else 
			newGameButton.fire();		
	}
	//-----------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void newGameButtonListener() {
		try {
			gameStage.close();
			game.stopGame();
			chosenSong.setText("Selected Song ...");
			backgroundMusicPlayer.play();
			browseButton.setDisable(false);
			browseButton.setVisible(true);
			browseButton.setFocusTraversable(true);
			playButton.setVisible(true);
			playButton.setFocusTraversable(true);
			resumeButton.setVisible(false);
			resumeButton.setDisable(true);
			resumeButton.setFocusTraversable(false);
			newGameButton.setVisible(false);
			newGameButton.setDisable(true);
			newGameButton.setFocusTraversable(false);
			imageSmothingCheckBox.setDisable(false);
			circleVisualCheckBox.setDisable(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	//-----------------------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	public void quitMenuItemListener() {
		System.exit(0);

	}//-----------------------------------------------------------------------------------------------------------
	/**
	 * Sets TextField group items visible.
	 */
	public void aboutMenuItemListener() {
		textFieldGroup.setVisible(true);

	}//-----------------------------------------------------------------------------------------------------------
	/**
	 * Sets TextField group items invisible.
	 */
	public void closeTextFieldButtonListener() {
		textFieldGroup.setVisible(false);
	}


}
