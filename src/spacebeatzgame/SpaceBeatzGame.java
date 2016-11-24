package spacebeatzgame;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;


public class SpaceBeatzGame extends Application { 
    Long lastNanoTimeHero = System.nanoTime();
    Long lastNanoTimeEnemy = System.nanoTime();
    private boolean gamePaused;
    
    private double screenHeight; // Screen height used by the Scene object
    private double screenWidth;  // Screen width used by the Scene object
    
    private ArrayList<npcSprite> enemy;
    
    private Media audioFile;
    private MediaPlayer player;
    private URL url;
    private Stage gameStage;
    private int bandRate;   	// Band rate for the audio spectrum listener
    
    private int currentLevel;
     
    
    public SpaceBeatzGame(URL url, Stage gameStage) {
    	gamePaused = false;
    	screenHeight = 600;  
    	screenWidth = 600;  
    	bandRate = 32;
    	currentLevel = 2;
    	this.url = url;
    	this.gameStage = gameStage;
    	start(gameStage);
    }
    
    @Override
    public void start(Stage primaryStage) {
    	
		audioFile = new Media(url.toString());
		player = new MediaPlayer(audioFile);
		
        player.setAudioSpectrumInterval(.2);  //default is .1 this greatly hinders performance we may can up it as we optimize our code
        player.setAudioSpectrumNumBands(bandRate);   //default is 128 this greatly hinders performance we may can up it as we optimize our code
        

        //play the song
        player.play();
        
        //Get screen attributes
        ScreenAttributes screen = new ScreenAttributes();

        Group rootGroup = new Group();
        
        
        //create pane for visualizations
        Pane visualPane = new Pane();
        
        Image background;
        ImagePattern bgPattern = null;
    	try {
	        File f = new File("src/spacebeatzgame/res/Space.jpg");
	        URI uri = f.toURI();
			URL url = uri.toURL();
			
		    background = new Image(url.toString(), screen.getScreenWidth(), screen.getScreenHeight(), true, true);
	        bgPattern = new ImagePattern(background);
        
		} catch (MalformedURLException | NullPointerException | IllegalArgumentException e1) {
			e1.printStackTrace();
			e1.getMessage();
			System.exit(1);
		}
        
        // TODO: Change to field?
        Sprite ship = new Sprite();
        ship.setAllImageAttributes("src/spacebeatzgame/res/ship.png", 55, 55, true, true);
        
        // Initialize the enemy Sprite
        enemy = new ArrayList<npcSprite>();
        
        //Now get the canvas Use custom canvas for debugging
        Canvas canvas = new Canvas(screen.getScreenWidth(), screen.getScreenHeight());

        
        //set hero sprite position by using screen size references from ScreenAtrributes class as X,Y coord
        ship.setPosition(300, 300);
        
        
        //Create GraphicsContext NOTE: This class is used to issue draw calls to a Canvas using a buffer.  NOTE 2: Class has lots of options we may need to use
        //See http://docs.oracle.com/javafx/2/canvas/jfxpub-canvas.htm# for GraphicContext tutorial
        GraphicsContext gc = canvas.getGraphicsContext2D();
  
        //Add the canvas to the rootGroup
        rootGroup.getChildren().addAll(visualPane, canvas);
        
        //Create the Scene with rootGroup
        Scene scene = new Scene(rootGroup, screenWidth, screenHeight, Color.BLACK);
        
        scene.setFill(bgPattern);
        //Add Scene to gameStage
        primaryStage.setScene(scene);
        //Format the Stage
        primaryStage.setTitle("SpaceBeatz");
        primaryStage.setFullScreen(true);
    
        
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        // primaryStage.setMaximized(true);
       
        primaryStage.show();
        

        //This Block handles keyboard input
        ArrayList<String> input = new ArrayList<>();

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent e)
                {
                    String code = e.getCode().toString();
                    if (!input.contains(code))
                        input.add(code);
                }
            });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent e)
                {
                    String code = e.getCode().toString();
                    input.remove(code);
                }
            });
        

         new AnimationTimer() {
        	 
            public void handle(long currentNanoTime) {
               	
                // calculate time since last update.
                double elapsedTime = (currentNanoTime - lastNanoTimeHero.doubleValue()) / 1000000000.0;
                lastNanoTimeHero = currentNanoTime;
            	
            	// If the game is resumed and the gamePaused boolean value is true, we resume the game
                // This will only occur if the "Resume" button is pressed from the main menu
            	if(gameStage.isFocused() && gamePaused) input.add(KeyCode.ESCAPE.toString());
                
                ship.setVelocity(0, 0);
                
                // If the input contains the ESC key being pressed we pause the game
                if(input.contains(KeyCode.ESCAPE.toString())) {
                	// TODO: Create menu and pause game, and a button to resume 
              
                	// First check if the game is paused
                	if(!gamePaused) {
                		// Step through the enemy sprites ArrayList and pause their animation
	                	for(int i = 0; i < enemy.size(); ++i) enemy.get(i).pauseSprite();
	                	player.pause();
	                	gamePaused = true;
                        gameStage.hide();
                	}
                	
                	// If the first if statement was not entered, that means we are resuming since the Resume button has been pressed
                	else {
                		// Step through the enemy sprites ArrayList and resume their animation
                		for(int i = 0; i < enemy.size(); ++i) enemy.get(i).resumeSprite();
                		player.play();
            			gamePaused = false;
                	}
                	input.clear();
                	return;
                }
                
                // If the game is not paused, we move the ship based on the input provided
                if(!gamePaused) ship.moveSprite(input, screen);

                // Update the ship's position
                ship.update(elapsedTime);

                // render
                gc.clearRect(0, 0, screen.getScreenWidth(), screen.getScreenHeight());
                ship.render(gc);
                
                // Step through the enemy ArrayList and render each sprite on the canvas
                for(int i = 0; i < enemy.size(); i++) {
                    enemy.get(i).render(gc);
                    enemy.get(i).update(elapsedTime);
                    if(enemy.get(i).intersects(ship)) {
                    	// ship.deathAnimation();
                    	// enemy[count].deathAnimation();
                        enemy.remove(i);
                        //enemy.trimToSize();
                   }
                    else if(enemy.get(i).getPositionX() <= -100) {
                    	// enemy[count].hide()   temp change later
                    	 enemy.remove(i);
                         //enemy.trimToSize(); 
                    }
                }
                
            }
            
            
        }.start();
        
        // This Listener is responsible for spawning enemies based on frequency levels
        player.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> {
	      
        	int newLevels = 0;
        	for(int i = 0; i < magnitudes.length; ++i) {
        		if(magnitudes[i] != -60) newLevels++;
        		else break;
        	}
        	for(int i = 0; i < 2; i++) {
	        	if(newLevels > currentLevel) { 
	        		currentLevel = newLevels;
	        		
	        	    // Spawn an enemy, if the size of the ArrayList is divisble by 20, we spawn an enemy space ship (FEATURES COMING SOON - OR NEVER)
	                // Move this to sprite class or maybe
	            	 if(enemy.size() % 20 == 0) {
	            		 enemy.add(new npcSprite("src/spacebeatzgame/res/enemy.png", 80, 80, true, true));
	            	 }
	                 else {
	                	 enemy.add(new npcSprite("src/spacebeatzgame/res/asteroid.png", 55, 55, true, true));
	                 }
	            	 enemy.get(enemy.size() - 1).setPosition(screen.getScreenWidth() + 100, (Math.random() * screen.getBoundary().getMaxY()));
	                 enemy.get(enemy.size() - 1).addVelocity((Math.random() * (-100) - 400), 0);
	            }
	        	
	        	else {
	        		currentLevel--;  // Turn down the level one by one to help spawn more enemies
	        	}
        	}
	    }); 
    }
}
