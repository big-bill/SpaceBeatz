package spacebeatzgame;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class SpaceBeatzGame extends Application {

	Long lastNanoTimeHero = System.nanoTime();
	private Stage gameStage;
	private ScreenAttributes screen;	
	private Pane visualPane;			
	private GraphicsContext gc;

	private boolean gamePaused;
	private double totalTimeElapsed;
	private double totalTime;

	// input will be used to detect user input and will determine the ship's movement
	// Will also be used to check for the ESC key being pressed to pause the game
	ArrayList<String> input = new ArrayList<>();

	private ArrayList<NPCSprite> enemy;  // ArrayList used to hold enemy sprites
	private int enemyIndex;				 // Index that steps through the enemy ArrayList properly
	private final int enemyTotal = 130;  // Total amount of enemies in the ArrayList

	private Media audioFile;
	private MediaPlayer player;
	private URL url;
	private final int bandRate = 128;    // Band rate for the audio spectrum listener
	private boolean imageSmooth;		 // Value that will determine if the image will be smooth or not
	private boolean circleVisualization; // Value that will determine if the visualization will be used or not
	private Hud hud = new Hud();		 // This HUD will contain the elapsed time, score, and total collisions
	private int collisionCounter = 0;	 // Stores the total amount of player collisions
	private Circle[] circle = new Circle[(bandRate / 5)];

	// ----------------------------------------------------------------------------------------------------------

	public SpaceBeatzGame(URL url, Stage gameStage, boolean smooth, boolean circVis) {
		enemyIndex = 0;
		gamePaused = false;
		player = null;
		totalTimeElapsed = 0.0;
		totalTime = 0.0;
		hud = new Hud();
		this.url = url;
		this.gameStage = gameStage;
		imageSmooth = smooth;
		circleVisualization = circVis;

		// Initialize the enemy Sprite ArrayList
		enemy = new ArrayList<NPCSprite>();
		for(int i = 0; i < enemyTotal; ++i) {
			if (enemy.size() % 20 == 0) {
				enemy.add(new NPCSprite("src/spacebeatzgame/res/enemy.png", 80, 80, true, imageSmooth));
			} else {
				enemy.add(new NPCSprite("src/spacebeatzgame/res/asteroid.png", 55, 55, true, imageSmooth));
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

	// Build the game's stage
	public void buildStage() {
		//Get screen attributes
		screen = new ScreenAttributes();

		Group rootGroup = new Group();

		//create pane for visualizations
		visualPane = new Pane();
		Image background;
		try {
			File f = new File("src/spacebeatzgame/res/starfield.gif");///changed to gif
			URI uri = f.toURI();
			URL url = uri.toURL();

                        background = new Image(url.toString(),Screen.getPrimary().getBounds().getMaxX(),Screen.getPrimary().getBounds().getMaxX(), true, imageSmooth);///NEW
                        ImageView bgImage = new ImageView(background);
                        visualPane.getChildren().add(bgImage);
		} catch (MalformedURLException | NullPointerException | IllegalArgumentException e1) {
			e1.printStackTrace();
			e1.getMessage();
			System.exit(1);
		}

		//Now get the canvas Use custom canvas for debugging
		Canvas canvas = new Canvas(screen.getScreenWidth(), screen.getScreenHeight());

		gc = canvas.getGraphicsContext2D();

		//Add the canvas to the rootGroup
		rootGroup.getChildren().addAll(visualPane, canvas, hud.getHud());

		//Create the Scene with rootGroup
		Scene scene = new Scene(rootGroup, 600, 600);

		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				String code = e.getCode().toString();
				if (!input.contains(code)) {
					input.add(code);
				}
			}
		});

		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				String code = e.getCode().toString();
				input.remove(code);
			}
		});

		
		//Add Scene to gameStage
		gameStage.setScene(scene);
		//Format the Stage
		gameStage.setTitle("SpaceBeatz");
		gameStage.setFullScreen(true);

		gameStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

		gameStage.show();
	}

	// ----------------------------------------------------------------------------------------------------------

	// Builds the media player and defines the logic behind it
	public void buildMediaPlayer() {
		audioFile = new Media(url.toString());
		player = new MediaPlayer(audioFile);

		player.setAudioSpectrumInterval(.3);  //default is .1 this greatly hinders performance we can decrease it as we optimize our code
		player.setAudioSpectrumNumBands(bandRate);   //default is 128 this greatly hinders performance we may can up it as we optimize our code
		// We check if the song is over
		player.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
				totalTime = player.getTotalDuration().toMillis();
			}
		});

		// This Listener is responsible for spawning enemies based on frequency levels
		player.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> {
			//If additional visualization selected 
			if (circleVisualization) {
				visualPane.getChildren().clear();
				Random rand = new Random(System.currentTimeMillis());
				for (int count = 0; count < (phases.length /5) - 1; count++) {
					int red = rand.nextInt(255);
					int green = rand.nextInt(255);
					int blue = rand.nextInt(255);
					circle[count] = new Circle(Math.random() * 600);
					circle[count].setCenterX(Math.random() * gameStage.getWidth() - 100);
					circle[count].setCenterY(Math.random() * gameStage.getHeight() - 100);
					circle[count].setFill(Color.rgb(red, green, blue, .70));
					visualPane.getChildren().add(circle[count]);
					//Caching circles here does not seem to help likely due to the way they are altered in size and position
				}
			}
			int newLevels = 0;
			// Step through the magnitudes array and get their level of intensity
			for (int i = 0; i < magnitudes.length; ++i) {
				if (magnitudes[i] != -60) {
					newLevels++;
				} else {
					break;
				}
			}

			// Add an enemy if the magnitudes level is a bit low
			if(newLevels < 30 && newLevels > 5) {
				enemy.get(enemyIndex).activate(screen);
				// Increase enemy index
				enemyIndex++;
				// If the index is greater than the total stored amount of enemies we reset back to 0
				if(enemyIndex >= enemyTotal) {
					enemyIndex = 0;
				}
			}

			// This loop will repeat based on the magnitude levels
			for (int x = 0; x < (newLevels/30); ++x) {
				enemy.get(enemyIndex).activate(screen);
				// Increase enemy index
				enemyIndex++;
				// If the index is greater than the total stored amount of enemies we reset back to 0
				if(enemyIndex >= enemyTotal) {
					enemyIndex = 0;
				}
			}
		});

		// Play the song
		player.play();
	}

	// ----------------------------------------------------------------------------------------------------------

	// Main game loop that contains the game's logic
	public void gameEngine() {

		// User sprite
		Sprite ship = new Sprite();
		ship.setAllImageAttributes("src/spacebeatzgame/res/ship.png", 55, 55, true, imageSmooth);
		ship.setPosition(300, 300);

		new AnimationTimer() {

			public void handle(long currentNanoTime) {

				// calculate time since last update.
				double elapsedTime = (currentNanoTime - lastNanoTimeHero.doubleValue()) / 1000000000.0;
				totalTimeElapsed += elapsedTime;
				lastNanoTimeHero = currentNanoTime;

				// Check if time is null, if it is not, the end of the media has been reached
				if(totalTime != 0.0) {
					// We check if the total time elapsed is greater than the total duration time plus 7 seconds for the enemies to clear the screen
					if(totalTimeElapsed * 1000 > totalTime + 7000)
						gameStage.hide();
				}

				// If the game is resumed and the gamePaused boolean value is true, we resume the game
				// This will only occur if the "Resume" button is pressed from the main menu
				if (gameStage.isFocused() && gamePaused) 
					input.add(KeyCode.ESCAPE.toString());

				ship.setVelocity(0, 0);

				// If the input contains the ESC key being pressed we pause the game
				if (input.contains(KeyCode.ESCAPE.toString())) 
					pauseGame();

				// If the game is not paused, we move the ship based on the input provided
				if (!gamePaused) 
					ship.moveSprite(input, screen);

				// Update the ship's position
				ship.update(elapsedTime);

				// render
				gc.clearRect(0, 0, screen.getScreenWidth(), screen.getScreenHeight());

				// We only check this because when the ship is hit it will flash, and when it flashes we don't render the ship sprite
				if(ship.getRenderSprite())
					ship.render(gc);

				int enemiesPassed = 0;

				// Step through the enemy ArrayList and render each sprite on the canvas
				for (int i = 0; i < enemy.size(); i++) {
					if(enemy.get(i).isActive()) {
						enemy.get(i).render(gc);
						enemy.get(i).update(elapsedTime);
						if (enemy.get(i).intersects(ship)) {
							// Since the enemy hit the ship, the ship flashes and becomes invunerable for a brief moment and update collison count appropiately 
							collisionCounter = ship.hitSprite(collisionCounter);
							enemy.get(i).stopMovement();
						} else if (enemy.get(i).getPositionX() <= -100) {
							enemiesPassed++;
							// If the enemy exits the screen and is no longer isActive we hide it and set its velocity to 0
							enemy.get(i).stopMovement();
						}
					}
				}
				hud.updateHud(collisionCounter, enemiesPassed, player);
			}

		}.start();
	}

	// ----------------------------------------------------------------------------------------------------------

	// Pause the game
	public void pauseGame() {

		// First check if the game is paused
		if (!gamePaused) {
			// Step through the enemy sprites ArrayList and pause their animation
			for (int i = 0; i < enemy.size(); ++i) {
				if(enemy.get(i).isActive()) {
					enemy.get(i).pauseSprite();
				}
			}
			player.pause();
			gamePaused = true;
			gameStage.hide();
		} // If the first if statement was not entered, that means we are resuming since the Resume button has been pressed
		else {
			// Step through the enemy sprites ArrayList and resume their animation
			for (int i = 0; i < enemy.size(); ++i) {
				if(enemy.get(i).isActive()) {
					enemy.get(i).resumeSprite();
				}
			}
			player.play();
			gamePaused = false;
		}
		input.clear();
		return;
	}

	// ----------------------------------------------------------------------------------------------------------

	// Called by the menu to free up game resources and dispose of data
	public void stopGame() {
		enemy.clear();
		player.stop();
		player.dispose();	
	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Returns if the game is currently running or not.
	 */
	public boolean isRunning() { return (!gamePaused); }

}
