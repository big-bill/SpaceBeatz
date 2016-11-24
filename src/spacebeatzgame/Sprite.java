package spacebeatzgame;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Sprite {
    /**
     * Sprite image.
     */
    protected Image image;
    
    /**
     * Sprite x position.
     */
    protected double positionX;
    
    /**
     * Sprite y position.
     */
    protected double positionY;
    
    /**
     * Sprite velocity x
     */
    protected double velocityX;
    
    /**
     * Stored value of the sprite's velocity on the X-axis
     */
    protected double storedVelocityX;
    
    /**
     * Sprite velocity y
     */
    protected double velocityY;
    
    /**
     * Stored value of the sprite's velocity on the Y-axis
     */
    protected double storedVelocityY;
    
    /**
     * Sprite width
     */
    protected double width;
    
    /**
     * Sprite height
     */
    protected double height;
    
    /**
     * Sprite ImageView
     */
    protected ImageView spriteIV = new ImageView();

    /**
     * Constructor no-args sets default position and velocities to zero
     */
    public Sprite() {
    	image = null;
    	width = 0.0;
    	height = 0.0;
        positionX = 0.0;
        positionY = 0.0;
        velocityX = 0.0;
        velocityY = 0.0;
    }

    /**
     * Sets the image of the sprite along with the requested sizes. Preserves
     * the ratio, smoothes image, and loads in background.
     *
     * @param imageFile The sprite image file name.
     * @param setWidth The requested width of the sprite.
     * @param setHeight The requested height of the sprite.
     * @param preserveRatio Boolean to preserve ratio *Note please keep true
     * @param smooth Boolean to smooth Image must be recreated to change see
     * note below
     * 
     */
    // Note !!Image smoothing may be turned off to increase performance 
    // end booleans preserve ration ,smooth, NOT USED load in background  unless we need to multithread
    public void setAllImageAttributes(String imageFile, double setWidth, double setHeight, boolean preserveRatio, boolean smooth) { 
    	try {
	    	File file = new File(imageFile);
	    	URI uri = file.toURI();
	    	URL url = uri.toURL();
	        this.image = new Image(url.toString(), setWidth, setHeight, preserveRatio, smooth);
	        width = image.getRequestedWidth();
	        height = image.getRequestedHeight();
	        spriteIV = new ImageView(image);
    	} 
    	catch(IllegalArgumentException | MalformedURLException e) {
    		e.printStackTrace();
    	}

    }
    
    /**
     * Move the sprite based on the input provided with the ArrayList passed.
     * Also determines whether or not the sprite can move in a direction any further 
     * based on the boundaries passed by ScreenAttributes object.
     * 
     * 
     * @param input User input, determines direction the sprite will move
     * @param screen Provides boundaries for the sprite object
     */
    public void moveSprite(ArrayList<String> input, ScreenAttributes screen) {
    	
    	// If the user is holding down the "A" key, the ship moves left 
    	if(input.contains("A")) {
            if(this.getBoundary().getMinX() <= screen.getBoundary().getMinX())
                this.setPosition(screen.getBoundary().getMinX(), this.positionY);
            else
            	this.addVelocity(-700, 0);
        }
    	
    	// If the user is holding down the "D" key, the ship moves right 
        if(input.contains("D")) {
            if(this.getBoundary().getMaxX() >= screen.getBoundary().getMaxX())
            	this.setPosition(screen.getBoundary().getMaxX() - 55, this.positionY); 
            else
            	this.addVelocity(700, 0);
        }
        
        // If the user is holding down the "W" key, the ship moves up
        if(input.contains("W")) {
            if(this.getBoundary().getMinY() <= screen.getBoundary().getMinY())
            	this.setPosition(this.positionX, screen.getBoundary().getMinY()); 
            else    
            	this.addVelocity(0, -700);
            
        }
        
        // If the user is holding down the "S" key, the ship moves down 
        if(input.contains("S")) {
            if(this.getBoundary().getMaxY() >= screen.getBoundary().getMaxY() - 15)
            	this.setPosition(this.positionX, screen.getBoundary().getMaxY() - 55);
            else
            	this.addVelocity(0, 700);
        }
    	
    }

    /**
     * Sets the position of the sprite.
     *
     * @param xPos
     * @param yPos
     */
    public void setPosition(double xPos, double yPos) {
        positionX = xPos;
        positionY = yPos;
    }

    /**
     * Sets the speed and direction of the sprite.
     *
     * @param speedX
     * @param speedY
     */
    public void setVelocity(double speedX, double speedY) {
        velocityX = speedX;
        velocityY = speedY;
    }

    /**
     * Adds velocity to the sprite.
     *
     * @param speedX
     * @param speedY
     */
    public void addVelocity(double speedX, double speedY) {
        velocityX += speedX;
        velocityY += speedY;
    }

    /**
     * Updates the sprite velocity and position.
     *
     * @param time Double value that should reflect
     */
    public void update(double time) {
        positionX += velocityX * time;
        positionY += velocityY * time;
    }
    
    /**
     * Pauses the sprite's animation
     */
    public void pauseSprite() {
    	storedVelocityX = velocityX;
    	storedVelocityY = velocityY;
    	setVelocity(0.0, 0.0);
    }
    
    /**
     * Resume the sprite's animation
     */
    public void resumeSprite() {
    	velocityX = storedVelocityX;
    	velocityY = storedVelocityY;
    	setVelocity(velocityX, velocityY);
    }

    /**
     * Renders the sprite position.
     *
     * @param graphicsContext
     */
    public void render(GraphicsContext graphicsContext) {
        graphicsContext.drawImage(image, positionX, positionY);
    }

    /**
     * Gets the boundary of the sprite for collision detection.
     *
     * @return
     */
    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX, positionY, width, height);
    }

    /**
     * Intersect detects collision of one sprite and another
     *
     * @param sprite the sprite to detect
     * @return Boolean is true if a sprite and another intersect false
     * otherwise.
     */
    public boolean intersects(Sprite sprite) {
        return sprite.getBoundary().intersects(this.getBoundary());
    }
    
    /**
     * Intersect detects collision of one sprite and another
     *
     * @param sprite
     * @param screen
     * @return Boolean is true if a sprite and screen intersect false
     * otherwise.
     */
    public boolean beyondWindow(Sprite sprite,ScreenAttributes screen) {
        boolean flag = false;
        if(sprite.getPositionX() > screen.getScreenWidth() || sprite.getPositionY() >screen.getScreenHeight()){
            flag = true;
        }
        return flag;
    }

    /**
     * Sprite fields
     *
     * @return String of the sprite position and velocity.
     */
    public String toString() {
        return " Position: [" + positionX + "," + positionY + "]"
                + " Velocity: [" + velocityX + "," + velocityY + "]";
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }
    
    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public ImageView getSpriteIV() {
        return spriteIV;
    }
    

}
