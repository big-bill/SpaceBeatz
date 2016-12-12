/**
 * This is the menu controller for the MainMenu.fxml file.
 *
 * @author Billy Matthews
 * @author Robert Munshower
 * @author Andrew Smith
 */
package spacebeatz;

import javafx.animation.Animation;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import spacebeatzgame.SpaceBeatzGame;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class MenuController {

    /**
     * Uninitialized game
     */
    SpaceBeatzGame game;
    /**
     * The root node of the main menu
     */
    @FXML
    private AnchorPane anchorPane;
    /**
     * Displays the chosen song title
     */
    @FXML
    private Label chosenSong;
    /**
     * Opens filed explorer
     */
    @FXML
    private Button browseButton;
    /**
     * Starts the game
     */
    @FXML
    private Button playButton;
    /**
     * Resumes a game in progress
     */
    @FXML
    private Button resumeButton;
    /**
     * Creates a new game
     */
    @FXML
    private Button newGameButton;
    /**
     * Menu item that on action quits the game
     */
    @FXML
    private MenuItem quitMenuItem;
    /**
     * Menu item that on action shows the about dialog
     */
    @FXML
    private MenuItem aboutMenuItem;
    /**
     * Holds about dialog
     */
    @FXML
    private Group textFieldGroup;
    /**
     * Image smoothing on and off
     */
    @FXML
    private CheckBox imageSmoothingCheckBox;
    /**
     * Gif background selection
     */
    @FXML
    private RadioButton gifRadio;
    /**
     * Radio buttons group
     */
    @FXML
    private ToggleGroup backgroundSelectionGroup;
    /**
     * Circle visualization radio
     */
    @FXML
    private RadioButton circRadio;
    /**
     * Static image radio
     */
    @FXML
    private RadioButton staticRadio;
    /**
     * Displays final score
     */
    @FXML
    private Label score;
    /**
     * Displays game time
     */
    @FXML
    private Label time;

    //Non-FXML Variables
    // ----------------------------------------------------------------------------------------------------------
    /**
     *
     */
    @FXML
    private Label collisons;
    /**
     * Holds the path to selected music
     */
    private String songPath;
    /**
     * Background music player
     */
    private MediaPlayer backgroundMusicPlayer;
    /**
     * gameStage is passed to the SpaceBeatzGame class so the controller will
     * have access to its stage
     */
    private Stage gameStage;

    /**
     * Initializes the menu by loading the background music.
     */
    @FXML
    public void initialize() {
        try {
            Media backgroundMusicAudio;

            /**
             * Music Source: Voltaic Kevin MacLeod (incompetech.com) Licensed
             * under Creative Commons: By Attribution 3.0 License
             * http://creativecommons.org/licenses/by/3.0/
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
        } catch (MalformedURLException | NullPointerException | IllegalArgumentException err) {
            // If the background music didn't load properly we alert the user
            err.printStackTrace();
            System.err.println("Error loading background music. Try re-downloading the game.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------------------------------------------------------------

    /**
     * Browse button listener waits for the browse button to be clicked. Once
     * clicked the user will be prompted with a file chooser for an MP3.
     */
    public void browseButtonListener() {
        // If the game was played to completion, we hide the stats for the next game.
        time.setVisible(false);
        score.setVisible(false);
        collisons.setVisible(false);

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
     * Now that a song has been selected, we start the game and pass the path to
     * the song.
     */
    public void playButtonListener() {
        URL url;
        try {
            // Since the media player successfully accessed the audio file, we stop playing the menu music
            if (backgroundMusicPlayer.getStatus() != Status.STOPPED) backgroundMusicPlayer.stop();

            File musicFile = new File(songPath);
            URI uri = musicFile.toURI();
            url = uri.toURL();
        } catch (NullPointerException | MalformedURLException | IllegalArgumentException e1) {
            e1.printStackTrace();
            System.err.println("No song found!");
            return;
        }

        if (url == null) return;

        try {
            // Create a new stage for the game
            gameStage = new Stage();
            game = new SpaceBeatzGame(this, url, gameStage, imageSmoothingCheckBox.isSelected(), getBackground());

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
            imageSmoothingCheckBox.setDisable(true);
            circRadio.setDisable(true);
            staticRadio.setDisable(true);
            gifRadio.setDisable(true);
        } catch (Exception gameException) {
            System.err.print(gameException.getMessage());
            System.err.println("Something has gone horribly wrong!");
        }
    }

    // ----------------------------------------------------------------------------------------------------------

    /**
     * Resumes the game. If the user alt-tabbed and clicks resume, the window
     * will be brought to the foreground.
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
            System.gc();
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
            imageSmoothingCheckBox.setDisable(false);
            circRadio.setDisable(false);
            staticRadio.setDisable(false);
            gifRadio.setDisable(false);
        } catch (Exception e) {
            System.err.print(e.getMessage());
            e.printStackTrace();
        }

    }

    // ----------------------------------------------------------------------------------------------------------

    /**
     * At the end of a song, we display the user's score onto the menu.
     *
     * @param time  Total song time
     * @param score Total score (enemies that crossed the screen)
     * @param hits  Total amount of enemy collisions
     */
    public void displayScore(String time, int score, int hits, boolean endGame) {
        this.time.setVisible(true);
        this.time.setText(time);
        this.score.setVisible(true);
        this.score.setText("Score: " + score);
        collisons.setVisible(true);
        collisons.setText("Collisions: " + hits);
        // We activate the newGameButton so that the state of the menu will activate the appropriate buttons for the user
        // This will only occur if the game is over
        if (endGame) newGameButton.fire();
    }

    // ----------------------------------------------------------------------------------------------------------

    /**
     * Exits the program.
     */
    public void quitMenuItemListener() {
        System.exit(0);
    }

    // ----------------------------------------------------------------------------------------------------------

    /**
     * Sets TextField group items visible.
     */
    public void aboutMenuItemListener() {
        textFieldGroup.setVisible(true);
        imageSmoothingCheckBox.setDisable(true);
    }

    // ----------------------------------------------------------------------------------------------------------

    /**
     * Sets TextField group items invisible.
     */
    public void closeTextFieldButtonListener() {
        textFieldGroup.setVisible(false);
        imageSmoothingCheckBox.setDisable(false);
    }

    /**
     * Gets the selected background and returns the appropriate int to be used
     * by the spaceBeatzGame class. If for some reason nothing is selected the
     * gif background is used.
     *
     * @return integer representing background to be used in game
     */
    private int getBackground() {

        if (gifRadio.isSelected()) return 1;
        else if (circRadio.isSelected()) return 2;
        else if (staticRadio.isSelected()) return 3;
        else return 3;
    }

}
