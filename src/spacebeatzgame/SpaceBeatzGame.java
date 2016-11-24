package spacebeatzgame;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
//import javafx.scene.paint.ImagePattern;for starry background image on game
import javafx.scene.shape.Circle;
import javafx.stage.Stage;


public class SpaceBeatzGame extends Application {
    private Timeline timeline; 
    Long lastNanoTimeHero = System.nanoTime();
    Long lastNanoTimeEnemy = System.nanoTime();
    private boolean gamePaused;
    
    private double screenHeight; // Screen height used by the Scene object
    private double screenWidth;  // Screen width used by the Scene object
    
    private Media audioFile;
    private MediaPlayer player;
    private URL url;
    private Stage gameStage;
     
    
    public SpaceBeatzGame(URL url, Stage gameStage) {
    	gamePaused = false;
    	screenHeight = 600;  
    	screenWidth = 600;   
    	this.url = url;
    	this.gameStage = gameStage;
    	start(gameStage);
    }
    
    @Override
    public void start(Stage primaryStage) {
    	
		audioFile = new Media(url.toString());
		player = new MediaPlayer(audioFile);
		
        player.setAudioSpectrumInterval(.4);  //default is .1 this greatly hinders performance we may can up it as we optimize our code
        player.setAudioSpectrumNumBands(4);   //default is 128 this greatly hinders performance we may can up it as we optimize our code
        

        //play the song
        player.play();
        
        //Get screen attributes
        ScreenAttributes screen = new ScreenAttributes();

        Group rootGroup = new Group();
        
        
        //create pane for visualizations
        Pane visualPane = new Pane();
        
        //Image background = new Image("images/Space.jpg",screen.getScreenWidth(),screen.getScreenHeight(),true,true);
        //ImagePattern bgPattern = new ImagePattern(background);
        //create sprites
        
        Sprite ship = new Sprite();
        ship.setAllImageAttributes("src/spacebeatzgame/res/ship.png", 55, 55, true, true);
        npcSprite[] enemy = new npcSprite[20];  // Temporarily reduce from 50 to improve current performance
        for(int count = 0; count < enemy.length; count++){
            if(count > 3) enemy[count] = new npcSprite("src/spacebeatzgame/res/asteroid.png", 55, 55, true, true);
            else enemy[count] = new npcSprite("src/spacebeatzgame/res/enemy.png", 80, 80, true, true);
            
            // TODO: While this position won't go over the top Y position, it will clip through the bottom, due to the task bar
            // I've tried everything, adding 80 in and out of the random number gen, subtracting it, subtracting 15 and adding
            // We can tinker with it later
            enemy[count].setPosition(screen.getScreenWidth() + 100, (Math.random() * screen.getBoundary().getMaxY()));
            enemy[count].addVelocity((Math.random() * (-100) - 400), 0);

        }
        
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
        
        //scene.setFill(bgPattern);
        

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
                		// Step through the enemy sprites array and pause their animation
	                	for(int i = 0; i < enemy.length; ++i) enemy[i].pauseSprite();
	                	// ship.setVelocity(0.0, 0.0);
	                	player.pause();
	                	gamePaused = true;
                        gameStage.hide();
                	}
                	
                	// If the first if statement was not entered, that means we are resuming since the Resume button has been pressed
                	else {
                		// Step through the enemy sprites array and resume their animation
                		for(int i = 0; i < enemy.length; ++i) enemy[i].resumeSprite();
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
                for(int count = 0; count < enemy.length; count++) {
                    enemy[count].render(gc);
                    enemy[count].update(elapsedTime);
                    if(enemy[count].intersects(ship)) {
                    	// ship.deathAnimation();
                    	// enemy[count].deathAnimation();
                        enemy[count].setPosition(screen.getScreenWidth() + 100, (Math.random() * screen.getBoundary().getMaxY()));
                   }
                    else if(enemy[count].getPositionX() <= -100) {
                    	// enemy[count].hide()   temp change later
                        enemy[count].setPosition(screen.getScreenWidth() + 100, (Math.random() * screen.getBoundary().getMaxY()));        
                    }
                }
                
            }
            
            
        }.start();
        
        Circle[] circle = new Circle[8];//matches band rate
        player.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> {
	        visualPane.getChildren().clear();
	        int i = 0;
	        int x = 10;
	        double y = primaryStage.getScene().getHeight() / 2;
	        Random rand = new Random(System.currentTimeMillis());
	        // Build random colored circles
	        for (int count = 0; count < phases.length; count++) {
	            // System.out.println(phases.length);
	            int red = rand.nextInt(255);
	            int green = rand.nextInt(255);
	            int blue = rand.nextInt(255);
	            circle[count] = new Circle(Math.random()*1000);
	            circle[count].setCenterX(Math.random()*primaryStage.getWidth()-100);
	            circle[count].setCenterY(Math.random()*primaryStage.getHeight()-100);
	            circle[count].setFill(Color.rgb(red, green, blue, .70));
	            visualPane.getChildren().add(circle[count]);
	 
	        }
                    
                    // System.out.println(player.statusProperty().toString());
	    }); // setAudioSpectrumListener()        
    }//end start method for gameStage    
}
