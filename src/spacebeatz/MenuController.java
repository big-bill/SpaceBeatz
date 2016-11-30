/**
 * This is the menu controller for the MainMenu.fxml file.
 * 
 * @author Billy Matthews
 * @author Robert Munshower
 * @author Andrew Smith
 */

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

	/**
	 * 
	 */
	@FXML
	private Label finalScore;

	/**
	 * 
	 */
	@FXML
	private Label finalTime;

	/**
	 * 
	 */
	@FXML
	private Label finalCollisons;

	//Non-FXML Variables
	// ----------------------------------------------------------------------------------------------------------

	/**
	 * 
	 */
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
	 * gameStage is passed to the spaceBeatzGame class so the controller will have access to its stage
	 */
	private Stage gameStage;

	/**
	 * 
	 */
	SpaceBeatzGame game;


	/**
	 * Initializes the menu by loading the background music.
	 */
	@FXML
	public void initialize() {
		try {
			/**	
			 *  Music Source:
			 * 		Voltaic Kevin MacLeod (incompetech.com)
			 * 		Licensed under Creative Commons: By Attribution 3.0 License
			 * 		http://creativecommons.org/licenses/by/3.0/
			 */    		
			File file = new File("src/spacebeatz/res/backgroundmusic.mp3");
			URI uri = file.toURI();
			URL url = uri.toURL();

			// Load the background music and set it to loop
			backgroundMusicAudio = new Media(url.toString());
			backgroundMusicPlayer = new MediaPlayer(backgroundMusicAudio);
			backgroundMusicPlayer.setVolume(0.2);
			backgroundMusicPlayer.setCycleCount(Animation.INDEFINITE);
			backgroundMusicPlayer.play();
		} 
		catch (MalformedURLException | NullPointerException | IllegalArgumentException err) {
			// If the background music didn't load properly we alert the user
			err.printStackTrace();
			System.err.println("Error loading background music. Try re-downloading the game.");
		}
		catch (Exception e) {
			e.printStackTrace();
		}   
	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Browse button listener waits for the browse button to be clicked.
	 * Once clicked the user will be prompted with a file chooser for an MP3.
	 */
	public void browseButtonListener() {
		// If the game was played to completion, we hide the stats for the next game.
		finalTime.setVisible(false);
		finalScore.setVisible(false);
		finalCollisons.setVisible(false);

		// Open up a FileChooser and let the user choose a track to play
		FileChooser fileChooser = new FileChooser();
		// Filter for only MP3 files
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.mp3");
		fileChooser.getExtensionFilters().add(extFilter);

		Window stage = null;
		File fileChosen = fileChooser.showOpenDialog(stage);
		if (fileChosen != null) {
			String songName;  // We set the "Song chosen" label as the song name, not the path as it was before
			songName = fileChosen.getAbsolutePath();
			songPath = songName;  // Store the song's path, which is passed to the game
			int slashPos = songName.lastIndexOf("\\");
			songName = songName.substring(slashPos + 1, songName.length());
			chosenSong.setText(songName); 
			playButton.setDisable(false);  // Enable the play button now that a song has been selected
		}
	}

	//-----------------------------------------------------------------------------------------------------------
	/**
	 * Now that a song has been selected, we start the game and pass the path to the song.
	 */
	public void playButtonListener() {
		URL url = null;
		try {
			// Since the media player successfully accessed the audio file, we stop playing the menu music
			if (backgroundMusicPlayer.getStatus() != Status.STOPPED) backgroundMusicPlayer.stop();

			File musicFile = new File(songPath);
			URI uri = musicFile.toURI();
			url = uri.toURL();
		} 
		catch (NullPointerException | MalformedURLException | IllegalArgumentException e1) {
			e1.printStackTrace();
			System.err.println("No song found!");
			return;
		}
		
		if(url == null) return;
		
		try {
			// Create a new stage for the game
			gameStage = new Stage();
			game = new SpaceBeatzGame(this, url, gameStage, imageSmothingCheckBox.isSelected(), circleVisualCheckBox.isSelected());
			
			// We then disable and enable the appropriate buttons. 
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
		} catch (Exception gameException) {
			System.err.print(gameException.getMessage());
			System.err.println("Something has gone horribly wrong!");
		}
	}

	// ----------------------------------------------------------------------------------------------------------
	
	/**
	 * Resumes the game.
	 * If the user alt-tabbed and clicks resume, the window will be brought to the foreground.
	 */
	public void resumeButtonListener() { 
		gameStage.show();  
		gameStage.toFront(); 
	}
	
	// ----------------------------------------------------------------------------------------------------------

	/**
	 * This will end the current game and reset the menu to it's original state.
	 */
	public void newGameButtonListener() {
		// First check if the game is running, and if so don't allow the user to create a new game		
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
			System.err.print(e.getMessage());
			e.printStackTrace();
		}

	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * At the end of a song, we display the user's score onto the menu.
	 * 
	 * @param time Total song time
	 * @param score Total score (enemies that crossed the screen)
	 * @param hits Total amount of enemy collisions
	 */
	public void displayScore(String time, int score, int hits, boolean endGame) { 
		finalTime.setVisible(true);
		finalTime.setText(time);
		finalScore.setVisible(true);
		finalScore.setText("Score: " + score);
		finalCollisons.setVisible(true);
		finalCollisons.setText("Collisions: " + hits);
		// We activate the newGameButton so that the state of the menu will activate the appropriate buttons for the user
		// This will only occur if the game is over
		if(endGame) newGameButton.fire();
	}

	// ----------------------------------------------------------------------------------------------------------
	
	/**
	 * Exits the program.
	 */
	public void quitMenuItemListener() { System.exit(0); }
	
	// ----------------------------------------------------------------------------------------------------------
	
	/**
	 * Sets TextField group items visible.
	 */
	public void aboutMenuItemListener() { textFieldGroup.setVisible(true); }
	
	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Sets TextField group items invisible.
	 */
	public void closeTextFieldButtonListener() { textFieldGroup.setVisible(false); }

}
