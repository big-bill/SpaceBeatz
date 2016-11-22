package spacebeatzgame;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/**
 * About ScreenAttributes Collects information about the primary screen.
 *
 * NOTE:primary screen refers to an instance of having more than one monitor the
 * OS sets one as primary.
 */
public class ScreenAttributes {

    /**
     * The min x coordinate.
     */
    private final double minX;
    /**
     * The min y coordinate.
     */
    private final double minY;
    /**
     * The height of the primary screen.
     */
    private double screenHeight;
    /**
     * The width of the primary screen.
     */
    private double screenWidth;

    /**
     * Constructor
     */
    public ScreenAttributes() {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        this.screenHeight = primaryScreenBounds.getHeight();
        this.screenWidth = primaryScreenBounds.getWidth();
        this.minX = primaryScreenBounds.getMinX();
        this.minY = primaryScreenBounds.getMinY();

    }

    /**
     * Gets the primary screen height.
     *
     * @return A double value that is the screen height
     */
    public double getScreenHeight() {
        return screenHeight;
    }

    /**
     * Gets the primary screen width.
     *
     * @return A double value that is the screen width
     */
    public double getScreenWidth() {
        return screenWidth;
    }
    /**
     * Gets the min X coordinate.
     * @return A double value that is the minimum X coordinate
     */
    public double getMinX() {
        return minX;
    }
    /**
     * Gets the min Y coordinate
     * @return A double value that is the minimum Y coordinate
     */
    public double getMinY() {
        return minY;
    }
    /**
     * Set the screen width.
     * @param width custom double value for width
     */
    private void setWidth(double width){
        screenWidth = width;
    }
    /**
     * Set the screen height.
     * @param height custom double value for height
     */
    private void setHeight(double height){
        screenHeight = height;
    }
    /**
     * Gets the boundary of the sprite for collision detection.
     *
     * @return
     */
    public Rectangle2D getBoundary() {
        return new Rectangle2D(minX, minY, screenWidth, screenHeight);
    }
    
    /**
     * Gets object contents to string.
     *
     * @return
     */
    @Override
    public String toString() {
        return "ScreenAttributes{" + "minX=" + minX + ", minY=" + minY + ", screenHeight=" + screenHeight + ", screenWidth=" + screenWidth + '}';
    }

}
