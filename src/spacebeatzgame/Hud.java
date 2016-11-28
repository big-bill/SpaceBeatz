package spacebeatzgame;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *About Hud Class
 * 
 * 
 */
public class Hud {

    /**
     * hudRect represents the heads up display of the game and contains
     * pertinent game statistics
     */
    private Pane hudPane;

    /**
     * Hud Color
     */
    private final Color TEXT_COLOR = Color.WHITE;

    /**
     * Opacity of HUD
     */
    private final double OPACITY = .3;

    /**
     * WIDTH represents hud width
     */
    private final double WIDTH = 400;

    /**
     * HEIGHT represents hud height
     */
    private final double HEIGHT = 100;

    /**
     * Holds elapsed time of game
     */
    private Label etLabel = new Label();

    /**
     * Holds current game score
     */
    private Label scoreLabel= new Label();
    
    /**
     * Holds current game score
     */
    private Label collisionLabel= new Label();

    /**
     * Holds String for Elapsed Time
     */
    private final String ET_STRING = "Elapsed Time: ";

    /**
     * Holds String for Score
     */
    private final String SCORE_STRING = "Score: ";
    
    /**
     * Holds String collisions
     */
    private final String COLLISION_STRING = "Collisions: ";

    /**
     * Holds game font
     */
    private final Font GAME_FONT = Font.font("Agency FB Bold", FontWeight.BOLD, 24);

    /**
     * Holds statistics for hud with a 12 pixel spacing.
     */
    private HBox statBox = new HBox(100);

    /**
     * Hud x position.
     */
    protected double positionX;

    /**
     * Hud y position.
     */
    protected double positionY;

    /**
     * Constructor for Hud
     */
    public Hud() {

        //Set Attributes of the hud
        hudPane = new Pane();

        //Set Attributes of labels
        etLabel.setFont(GAME_FONT);
        etLabel.setTextFill(TEXT_COLOR);
        etLabel.setText(ET_STRING + "--:--");
         
        scoreLabel.setFont(GAME_FONT);
        scoreLabel.setTextFill(TEXT_COLOR);
        scoreLabel.setText(SCORE_STRING + "000,000");
        
        collisionLabel.setFont(GAME_FONT);
        collisionLabel.setTextFill(TEXT_COLOR);
        collisionLabel.setText(COLLISION_STRING + "0");
        
        

        //add children to statBox
        statBox.getChildren().addAll(scoreLabel,etLabel,collisionLabel);
        statBox.setFocusTraversable(false);
        
        hudPane.getChildren().add(statBox);
    }

    /**
     * Sets position of hud.
     *
     * @param xPos x coordinate position
     * @param yPos y coordinate position
     */
    public void setHudPos(double xPos, double yPos) {
        hudPane.setLayoutX(xPos);
        hudPane.setLayoutY(yPos);
    }

    /**
     * Get the hud
     *
     * @return the rectangle node containing formated hud.
     */
    public Pane getHud() {
        return hudPane;
    }


}
