package spacebeatz;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import spacebeatzgame.SpaceBeatzGame;


public class MenuController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView spaceBeatzLogo;

    @FXML
    private Label chosenSong;

    @FXML
    private Button browseButton;

    @FXML
    private Button playButton;

    @FXML
    private ImageView bigBang;

    @FXML
    private Label chooseSongLabel;

    private Media backgroundMusicAudio;
    private MediaPlayer backgroundMusicPlayer;
    
    
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
    	playButton.setDisable(true);    	
    }

    //-----------------------------------------------------------------------------------------------------------
    
    public void browseButtonListener() {
    	// Open up a FileChooser and let the user choose a track to play
    	FileChooser fileChooser = new FileChooser();
    	// Filter only mp3 files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 files (*.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(extFilter);
        Window stage = null;  // TODO: Is this needed? Couldn't figure out how to implement a FileChooser without a stage
		File fileChosen = fileChooser.showOpenDialog(stage);
		if (fileChosen != null) {  
			chosenSong.setText(fileChosen.getAbsolutePath()); 
			playButton.setDisable(false); 
		}
    }
    
    //-----------------------------------------------------------------------------------------------------------
  
    public void playButtonListener() {
    	try {    		
    		// Since the media player successfully accessed the audio file, we stop playing the menu music
    		if(backgroundMusicPlayer.getStatus() != Status.STOPPED) backgroundMusicPlayer.stop();
    		File musicFile = new File(chosenSong.getText());
    		URI uri = musicFile.toURI();
    		URL url = uri.toURL();
  
    		// TODO: There may be a better way to handle this
			SpaceBeatzGame game = new SpaceBeatzGame(url);
    	}
    	catch (NullPointerException | MalformedURLException | IllegalArgumentException e1) {
    		System.err.println("No song found!");
    	}
    	catch(Exception e2) {
    		System.err.print(e2.getMessage());
    	}
    }
    
}
