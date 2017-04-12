/**
 * This class contains most of the game's logic.
 *
 * @author Billy Matthews
 * @author Robert Munshower
 * @author Andrew Smith
 */
package spacebeatzgame;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import spacebeatz.MenuController;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class SpaceBeatzGame extends Application {

    private final int enemyTotal = 160;         // Total amount of enemies in the ArrayList
    private final int bandRate = 128;           // Band rate for the audio spectrum listener
    private Stage gameStage;
    private ScreenAttributes screen;
    private Pane visualPane;
    private GraphicsContext gc;
    private Long lastNanoTimeHero = System.nanoTime();
    private boolean gamePaused;                 // Used to communicate with menu that the game is paused or not
    private double totalTimeElapsed;            // Total time elapsed (current time of the song, stops when music stops)
    private double totalTime;                   // Total time the game has been running
    // input will be used to detect user input and will determine the ship's movement
    // Will also be used to check for the ESC key being pressed to pause the game
    private ArrayList<String> input;
    // ArrayList used to hold enemy sprites
    private ArrayList<NPCSprite> enemies;
    private int enemyIndex;                     // Index that steps through the enemy ArrayList properly
    private URL url;                            // URL for the audio file
    private MediaPlayer player;                 // Plays the audio file
    private boolean imageSmooth;                // Value that will determine if the image will be smooth or not
    private boolean circleVisualization;        // Value that will determine if the visualization will be used or not
    private int bgChoice;                       // Choice selected by the user in the main menu
    private Circle[] circle = new Circle[(bandRate / 5)];

    private Hud hud;                            // This HUD will contain the elapsed time, score, and total collisions
    private int collisionCounter;               // Stores the total amount of player collisions

    // Instance of the menu is passed and is used to display stats of the game when it's paused or finished
    private MenuController menu;

    // ----------------------------------------------------------------------------------------------------------

    public SpaceBeatzGame(MenuController menu, URL url, Stage gameStage, boolean smooth, int bgChoice) {
        this.menu = menu;
        enemyIndex = 0;
        gamePaused = false;
        player = null;
        totalTimeElapsed = 0.0;
        totalTime = 0.0;
        collisionCounter = 0;
        hud = new Hud();
        this.url = url;
        this.gameStage = gameStage;

        imageSmooth = smooth;
        this.bgChoice = bgChoice;
        if (bgChoice == 2) circleVisualization = true;

        input = new ArrayList<>();
        enemies = new ArrayList<>();
        // Initialize the enemy Sprite ArrayList
        if (enemies.isEmpty()) {
            for (int i = 0; i < enemyTotal; ++i) {
                if (enemies.size() % 20 == 0)
                    enemies.add(new NPCSprite("src/spacebeatzgame/res/enemy2.png", 80, 80, true, imageSmooth));
                else enemies.add(new NPCSprite("src/spacebeatzgame/res/asteroid.png", 55, 55, true, imageSmooth));
            }
        }
        start(gameStage);
    }

    // ----------------------------------------------------------------------------------------------------------

    @Override
    public void start(Stage gameStage) {

        // Build the media player and logic for it
        buildMediaPlayer();

        // Build the game's stage
        buildStage();

        // Main game loop
        gameEngine();

    }

    // ----------------------------------------------------------------------------------------------------------

    // Builds the media player and defines the logic behind it
    private void buildMediaPlayer() {
        Media audioFile = new Media(url.toString());
        player = new MediaPlayer(audioFile);

        player.setAudioSpectrumInterval(.2);  // Change this value to alter the enemy spawn rate
        player.setAudioSpectrumNumBands(bandRate);

        // We check if the song is over, and if so get the total duration of the song
        player.setOnEndOfMedia(() -> totalTime = player.getTotalDuration().toMillis());

        // This Listener is responsible for spawning enemies based on magnitude levels
        player.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> {
            // If additional visualization selected
            if (circleVisualization) {
                visualPane.getChildren().clear();
                Random rand = new Random(System.currentTimeMillis());
                for (int count = 0; count < (phases.length / 5) - 1; count++) {
                    int red = rand.nextInt(255);
                    int green = rand.nextInt(255);
                    int blue = rand.nextInt(255);
                    circle[count] = new Circle(Math.random() * 600);
                    circle[count].setCenterX(Math.random() * gameStage.getWidth() - 100);
                    circle[count].setCenterY(Math.random() * gameStage.getHeight() - 100);
                    circle[count].setFill(Color.rgb(red, green, blue, .70));
                    visualPane.getChildren().add(circle[count]);
                }
            }

            // TODO: The logic can be improved. Right now there isn't any rhyme or reason to it.
            // TODO: Also need to verify that enemies can't over-spawn (max right now is 160 enemies on screen)
            /*
             * As of now the enemy spawn rate is .2 seconds and is based on the magnitude levels picked up by the media player.
			 * Here is the current spawn logic
			 * Magnitude Levels  0 through 4: 		Spawn 0 enemies
			 * Magnitude Levels 5 through 59: 		Spawn 1 enemy
			 * Magnitude Levels 60 through 89: 		Spawn 2 enemies
			 * Magnitude Levels 90 through 119: 		Spawn 3 enemies
			 * Magnitude Levels 120 - BandRate (128): 	Spawn 4 enemies
			 */

            // indexLevel will equal the index the first instance of -60 is found in magnitudes
            int indexLevel = 0;
            // Step through the magnitudes array and get their level of intensity
            for (int i = 0; i < magnitudes.length; ++i) {
                if (magnitudes[i] == -60) {
                    indexLevel = i + 1;
                    break;
                }
            }

            // Add an enemy if the magnitudes level are low
            if (indexLevel < 30 && indexLevel > 5) {
                enemies.get(enemyIndex).activate(screen);
                // Increase enemy index
                enemyIndex++;
                // If the index is greater than the total stored amount of enemies we reset back to 0
                if (enemyIndex >= enemyTotal) enemyIndex = 0;
            } else {
                // This loop will repeat based on the magnitude levels
                for (int x = 0; x < (indexLevel / 30); ++x) {
                    enemies.get(enemyIndex).activate(screen);
                    // Increase enemy index
                    enemyIndex++;
                    // If the index is greater than the total stored amount of enemies we reset back to 0
                    if (enemyIndex >= enemyTotal) enemyIndex = 0;
                }
            }
        });

        // Play the song
        player.play();
    }
    // ----------------------------------------------------------------------------------------------------------

    // Build the game's stage
    private void buildStage() {
        // Get screen attributes
        screen = new ScreenAttributes();

        Group rootGroup = new Group();

        // Create pane for visualizations
        visualPane = new Pane();

        ImagePattern bgPattern = null;
        switch (bgChoice) {
            // Case 1 is the choice for the starfield gif
            case 1:
                try {
                    File f = new File("src/spacebeatzgame/res/starfield.gif");
                    URI uri = f.toURI();
                    URL url = uri.toURL();

                    Image background = new Image(url.toString(), Screen.getPrimary().getBounds().getMaxX(), Screen.getPrimary().getBounds().getMaxX(), true, imageSmooth);
                    ImageView bgImage = new ImageView(background);
                    bgImage.setCache(true);
                    bgImage.setCacheHint(CacheHint.SPEED);
                    visualPane.getChildren().add(bgImage);
                } catch (MalformedURLException | NullPointerException | IllegalArgumentException e1) {
                    System.err.println("Error loading background image. Please check your files.");
                    e1.getMessage();
                    System.exit(1);
                }
                break;

            // Case 3 is the choice for a static image of space
            case 3:
                try {
                    File f = new File("src/spacebeatzgame/res/Space.jpg");
                    URI uri = f.toURI();
                    URL url = uri.toURL();

                    Image background = new Image(url.toString(), screen.getScreenWidth(), screen.getScreenHeight(), true, imageSmooth);
                    bgPattern = new ImagePattern(background);
                } catch (MalformedURLException | NullPointerException | IllegalArgumentException e1) {
                    System.err.println("Error loading background image. Please check your files.");
                    e1.getMessage();
                    System.exit(1);
                }
                break;
        }

        Canvas canvas = new Canvas(screen.getScreenWidth(), screen.getScreenHeight());

        gc = canvas.getGraphicsContext2D();

        // Add the canvas to the rootGroup
        rootGroup.getChildren().addAll(visualPane, canvas, hud.getHud());

        // Create the Scene with rootGroup
        Scene scene = new Scene(rootGroup, 800, 600);

        // Set the event handler for when a key is pressed
        scene.setOnKeyPressed((event -> {
            String code = event.getCode().toString();
            if (!input.contains(code)) input.add(code);
        }));

        // Create the event handler for when a key is released
        scene.setOnKeyReleased((event -> {
            String code = event.getCode().toString();
            input.remove(code);
        }));

        // Add Scene to gameStage
        gameStage.setScene(scene);

        // Format the Stage
        gameStage.setTitle("SpaceBeatz");
        if (bgChoice == 2) scene.setFill(Color.WHITE);
        if (bgChoice == 3) scene.setFill(bgPattern);
        gameStage.setFullScreen(true);

        // We set this to NO_MATCH because ESC will be tested to close the screen instead
        gameStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        gameStage.show();
    }

    // ----------------------------------------------------------------------------------------------------------

    // Main game loop that contains the game's logic
    private void gameEngine() {
        // User sprite
        Sprite ship = new Sprite();
        ship.setAllImageAttributes("src/spacebeatzgame/res/ship.png", 55, 55, true, imageSmooth);
        // Place the sprite on the screen, also changing the render status to true
        ship.setPosition(300, 300);

        // The game's logic for moving sprites, handling collision, and media player status is done here
        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {

                // Calculate time since last update
                double elapsedTime = (currentNanoTime - lastNanoTimeHero.doubleValue()) / 1000000000.0;
                totalTimeElapsed += elapsedTime;
                lastNanoTimeHero = currentNanoTime;

                // Check if time is not 0, and if it's not, then the end of the media has been reached
                if (totalTime != 0.0 || player.getStatus() == Status.STOPPED) {
                    // We check if the total time elapsed is greater than the total duration time plus 7 seconds for the enemies to clear the screen
                    if (totalTimeElapsed * 1000 > totalTime + 7000) {
                        menu.displayScore(hud.getTime(), hud.getCurrentScore(), hud.getCurrentHitCount(), true);
                        this.stop();
                    }
                }

                // Check if the music player has been spontaneously disposed of, and if so exit the game
                if (player.getStatus() == Status.DISPOSED) this.stop();

                // Reset the ships velocity every frame so the ship doesn't constantly gain velocity
                ship.setVelocity(0, 0);

                // If the game is resumed and the gamePaused boolean value is true, we resume the game
                // This will only occur if the "Resume" button is pressed from the main menu
                if (gameStage.isFocused() && gamePaused) input.add(KeyCode.ESCAPE.toString());

                // If the input contains the ESC key being pressed we pause the game
                if (input.contains(KeyCode.ESCAPE.toString())) toggleGameStatus();

                // If the game is not paused, we move the ship based on the input provided
                if (!gamePaused) ship.moveSprite(input, screen);

                // Update the ship's position
                ship.update(elapsedTime);

                // Render screen
                gc.clearRect(0, 0, screen.getScreenWidth(), screen.getScreenHeight());

                // We only check this because when the ship is hit it will flash, and when it flashes we don't render the ship sprite
                if (ship.getRenderSprite()) ship.render(gc);

                // We reset the amount of enemies passed each frame
                int enemiesPassed = 0;

                // Step through the enemy ArrayList and render each sprite on the canvas
                for (Sprite enemy : enemies) {
                    if (enemy.getRenderSprite()) {
                        enemy.update(elapsedTime);
                        enemy.render(gc);
                        if (enemy.intersects(ship)) {
                            // Since the enemy hit the ship, the ship flashes and becomes invulnerable for a brief moment
                            // This returns the total number of collisions which will be sent to the HUD
                            collisionCounter = ship.hitSprite(collisionCounter);
                            enemy.stopMovement();
                        } else if (enemy.getPositionX() <= -100) {
                            // Enemy has passed the screen
                            enemiesPassed++;
                            // If the enemy exits the screen we hide it and set its velocity to 0
                            enemy.stopMovement();
                        }
                    }
                }
                // Update the HUD's counters
                hud.updateHud(collisionCounter, enemiesPassed, player);
                // Update game status on menu
                menu.displayScore(hud.getTime(), hud.getCurrentScore(), hud.getCurrentHitCount(), false);
            }

        }.start();
    }

    // ----------------------------------------------------------------------------------------------------------

    /**
     * This method pauses and resumes the game.
     */
    private void toggleGameStatus() {

        // First check if the game is paused
        if (!gamePaused) {
            // Step through the enemy sprites ArrayList and pause their animation
            for (Sprite enemy : enemies) if (enemy.getRenderSprite()) enemy.pauseSprite();

            // Pause the media player and the game
            player.pause();
            gamePaused = true;

            // Then we hide the game and send the current score to the menu
            gameStage.hide();
            menu.displayScore(hud.getTime(), hud.getCurrentScore(), hud.getCurrentHitCount(), false);
        }
        // If the first if statement was not entered, that means we are resuming since the Resume button has been pressed
        // We step through the enemies list and resume all the sprites that should be rendered (those who are on screen)
        else {
            for (Sprite enemy : enemies) if (enemy.getRenderSprite()) enemy.resumeSprite();
            player.play();
            gamePaused = false;
        }
        input.clear();
    }

    // ----------------------------------------------------------------------------------------------------------

    // Called by the menu to free up game resources and dispose of data
    public void stopGame() {
        player.dispose();
    }

    // ----------------------------------------------------------------------------------------------------------

    /**
     * Returns if the game is currently running or not.
     *
     * @return Returns the state of the game. The game is considered running when it is playing and the user has not pressed ESC.
     */
    public boolean isRunning() {
        return (!gamePaused);
    }

}
