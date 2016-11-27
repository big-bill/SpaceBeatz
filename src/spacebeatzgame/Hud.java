package spacebeatzgame;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
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
    private Rectangle hudRect;
    
    /**
     * Hud Color
     */
    private final Color HUD_COLOR = Color.SKYBLUE;
    
    /**
     * Opacity of HUD
     */
    private final double HUD_OPACITY = .3;
    
    /**
     * WIDTH represents hud width
     */
    private final double WIDTH = 400;
    
    /**
     * HEIGHT represents hud height
     */
    private final double HEIGHT = 100;
    
    /**
     * Vertical diameter of the arc at the four corners of the rectangle.
     */
    private final double ARC_HEIGHT = 20;
    
    /**
     * Horizontal diameter of the arc at the four corners of the rectangle
     */
    private final double ARC_WIDTH = 20;
    
    /**
     * Holds elapsed time of game
     */
    private Label etLabel;
    
    /**
     * Holds current game score
     */
    private Label scoreLabel;
    
    /**
     * Holds String for Elapsed Time
     */
    private final String ET_STRING = "Elapsed Time: ";
    
    /**
     * Holds String for Score
     */
    private final String SCORE_STRING = "Score: ";
    
    /**
     * Holds game font
     */
    private final Font GAME_FONT = Font.font("Agency FB Bold",FontWeight.BOLD,14);
    
    /**
     * Holds statistics for hud with a 12 pixel spacing.
     */
    private HBox statBox = new HBox(12);

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
        hudRect = new Rectangle(WIDTH, HEIGHT);
        hudRect.setFill(HUD_COLOR);
        hudRect.setArcHeight(ARC_HEIGHT);
        hudRect.setArcWidth(ARC_WIDTH);
        hudRect.setOpacity(HUD_OPACITY);
        
        //Set Attributes of labels
        etLabel.setText(ET_STRING + "--:--");
        etLabel.setFont(GAME_FONT);
        scoreLabel.setText(SCORE_STRING + "000,000");
        scoreLabel.setFont(GAME_FONT);
        
        
        //add children to statBox
        statBox.getChildren().addAll(etLabel,scoreLabel);
        statBox.setFocusTraversable(false);
        

    }
    /**
     * Sets position of hud.
     * @param xPos x coordinate position
     * @param yPos y coordinate position
     */
    public void setHudPos(double xPos, double yPos){
        hudRect.setX(xPos);
        hudRect.setY(yPos);
    }
    
    /**
     * Get the hud
     * 
     * @return the rectangle node containing formated hud.
     */
    public Rectangle getHud(){
        return hudRect;
    }


}
