package spacebeatzgame;

//import java.time.Duration;
import javafx.util.Duration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import static javafx.application.Application.launch;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
//import javafx.scene.paint.ImagePattern;for starry background image on game
import javafx.scene.shape.Circle;


public class SpaceBeatzGame extends Application {
    private Timeline timeline; 
    Long lastNanoTimeHero = System.nanoTime();
    Long lastNanoTimeEnemy = System.nanoTime();
    
    private Media audioFile;
    private MediaPlayer player;
    private URL url;

     
    
    public SpaceBeatzGame(URL url) {
    	this.url = url;
    	Stage stage = new Stage();
    	start(stage);
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
        ship.setAllImageAttributes("res/ship.png", 55, 55, true, true);
        Sprite[] enemy = new Sprite[20];  // Temporarily reduce from 50 to improve current performance
        for(int count = 0; count < enemy.length; count++){
            enemy[count] = new Sprite();
            if(count > 3){
                enemy[count].setAllImageAttributes("res/asteroid.png", 55, 55, true, true);
            }else {  //only three enemy ships
            	enemy[count].setAllImageAttributes("res/enemy.png", 80, 80, true, true);
            }
            enemy[count].setPosition(Math.random()*10,Math.random()*1000);
            enemy[count].addVelocity(Math.random()*1000,0);

        }
        
        

        
        //Now get the canvas Use custom canvas for debugging
        Canvas canvas = new Canvas(screen.getScreenWidth(),screen.getScreenHeight());

        
        //set hero sprite position by using screen size references from ScreenAtrributes class as X,Y coord
        ship.setPosition(300,300);
        
        
        //Create GraphicsContext NOTE: This class is used to issue draw calls to a Canvas using a buffer.  NOTE 2: Class has lots of options we may need to use
        //See http://docs.oracle.com/javafx/2/canvas/jfxpub-canvas.htm# for GraphicContext tutorial
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        
        

        

        //Add the canvas to the rootGroup
        rootGroup.getChildren().addAll(visualPane,canvas);
        //Create the Scene with rootGroup
        Scene scene = new Scene(rootGroup,600,600);
        //scene.setFill(bgPattern);
        //Add Scene to stage
        primaryStage.setScene( scene );
        //Format the Stage
        primaryStage.setTitle("SpaceBeatz");
        //primaryStage.setFullScreen(true);
        primaryStage.setMaximized(true);
        primaryStage.show();
        

                //This Block handles keyboard input
        ArrayList<String> input = new ArrayList<>();

        scene.setOnKeyPressed(
            new EventHandler<KeyEvent>()
            {
                @Override
                public void handle(KeyEvent e)
                {
                    String code = e.getCode().toString();
                    if ( !input.contains(code) )
                        input.add( code );
                }
            });

        scene.setOnKeyReleased(
            new EventHandler<KeyEvent>()
            {
                @Override
                public void handle(KeyEvent e)
                {
                    String code = e.getCode().toString();
                    input.remove( code );
                }
            });
        

         new AnimationTimer() {
           
            public void handle(long currentNanoTime) {
                // calculate time since last update.
                double elapsedTime = (currentNanoTime - lastNanoTimeHero.doubleValue()) / 1000000000.0;
                lastNanoTimeHero = currentNanoTime;

                // game logic
                ship.setVelocity(0, 0);
                if (input.contains("A")) {
                    ship.addVelocity(-700, 0);

                }
                if (input.contains("D")) {
                    ship.addVelocity(700, 0);
                }
                if (input.contains("W")) {
                    ship.addVelocity(0, -700);
                }
                if (input.contains("S")) {
                    ship.addVelocity(0, 700);
                }

                ship.update(elapsedTime);

                // render
                gc.clearRect(0, 0, screen.getScreenWidth(), screen.getScreenHeight());
                ship.render(gc);
                for (int count = 0; count < enemy.length; count++) {
                    enemy[count].render(gc);
                    enemy[count].update(elapsedTime);
                    if(enemy[count].intersects(ship)){
                        enemy[count].setPosition(0, 0);
                    }else if(enemy[count].beyondWindow(enemy[count],screen)){
                        enemy[count].setPosition(Math.random()*-1000+Math.random()*100,Math.random()*1000-Math.random()*100);
                        enemy[count].setVelocity(Math.random()*1000,Math.random()*1000);
                    }
                }
                
            }
        }.start();
                Circle[] circle = new Circle[4];//matches band rate
                player.setAudioSpectrumListener(
                        (double timestamp,
                                double duration,
                                float[] magnitudes,
                                float[] phases) -> {
                            visualPane.getChildren().clear();
                            int i = 0;
                            int x = 10;
                            double y = primaryStage.getScene().getHeight() / 2;
                            Random rand = new Random(System.currentTimeMillis());
                            // Build random colored circles
                            for (int count = 0; count < phases.length; count++) {
                                System.out.println(phases.length);
                                int red = rand.nextInt(255);
                                int green = rand.nextInt(255);
                                int blue = rand.nextInt(255);
                                circle[count] = new Circle(Math.random()*1000);
                                circle[count].setCenterX(Math.random()*primaryStage.getWidth()-100);
                                circle[count].setCenterY(Math.random()*primaryStage.getHeight()-100);
                                circle[count].setFill(Color.rgb(red, green, blue, .70));
                                visualPane.getChildren().add(circle[count]);
                                i += 200;
//                                if(player.getCurrentTime().greaterThan(Duration.seconds(5))){
//                                    primaryStage.setFullScreen(true);
//                                }
                            }
                            
                            System.out.println(player.statusProperty().toString());
                        }); // setAudioSpectrumListener()



       
         
        
        
    }//end start method for stage    
}
