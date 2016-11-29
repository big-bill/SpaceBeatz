package spacebeatzgame;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


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
	 * Status on whether the sprite is invincible or not
	 */
	protected boolean vulnerable;


	/**
	 * Status on whether the sprite should be rendered or not
	 */
	protected boolean renderSprite;

	/**
	 * Sprite width
	 */
	protected double width;

	/**
	 * Sprite height
	 */
	protected double height;

	/**
	 * Sprite state (if the sprite is active on the screen or not)
	 */
	protected boolean isActive;
	/**
	 * Sprite ImageView
	 */
	protected ImageView spriteIV = new ImageView();

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Constructor no-args sets default position and velocities to zero
	 */
	public Sprite() {
		image = null;
		isActive = false;
		vulnerable = true;
		renderSprite = true;
		width = 0.0;
		height = 0.0;
		positionX = 0.0;
		positionY = 0.0;
		velocityX = 0.0;
		velocityY = 0.0;
	}

	// ----------------------------------------------------------------------------------------------------------

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
			spriteIV.setCache(true);
			spriteIV.setCacheHint(CacheHint.SPEED);
		} 
		catch(IllegalArgumentException | MalformedURLException e) {
			e.printStackTrace();
		}

	}

	// ----------------------------------------------------------------------------------------------------------

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
			if(getBoundary().getMinX() <= screen.getBoundary().getMinX())
				setPosition(screen.getBoundary().getMinX(), positionY);
			else
				addVelocity(-500, 0);
		}

		// If the user is holding down the "D" key, the ship moves right 
		if(input.contains("D")) {
			if(getBoundary().getMaxX() >= screen.getBoundary().getMaxX())
				setPosition(screen.getBoundary().getMaxX() - 55, positionY); 
			else
				addVelocity(500, 0);
		}

		// If the user is holding down the "W" key, the ship moves up
		if(input.contains("W")) {
			if(getBoundary().getMinY() <= screen.getBoundary().getMinY())
				setPosition(positionX, screen.getBoundary().getMinY()); 
			else    
				addVelocity(0, -500);

		}

		// If the user is holding down the "S" key, the ship moves down 
		if(input.contains("S")) {
			if(getBoundary().getMaxY() >= screen.getBoundary().getMaxY() - 15)
				setPosition(positionX, screen.getBoundary().getMaxY() - 55);
			else
				addVelocity(0, 500);
		}
	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Called when a sprite is hit, flashes the sprite to indicate that it has been hit
	 * 
	 */
	public int hitSprite(int collisions) {
		// First check if the user is vulnerable (user can't get hit if they were just hit and now flashing)
		if(vulnerable) {
			collisions++;//increment collison if vulnerable
			renderSprite = vulnerable = false;
			Timeline userHit = new Timeline(new KeyFrame(Duration.seconds(.15), new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					// If the renderSprite is true, we set it to false so it won't be rendered
					if(renderSprite)
						renderSprite = false;
					else
						renderSprite = true;
				}
			}));

			// After the flashing animation is over, we set the values back to true so the user will be registered when hit and rendered
			userHit.setOnFinished(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					vulnerable = true;
					renderSprite = true;
				}});
			// Sets the amount of times the flashing (10 times, the method sets it to true and false) occurs
			userHit.setCycleCount(20);
			userHit.play();

		}
		return collisions;
	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * This method is used if the player sprite should be rendered on screen or not.
	 * This is used for flashing the sprite to indicate that it was hit.
	 * 
	 * @return The status of if the sprite should be rendered on screen or not
	 */
	public boolean getRenderSprite() { return renderSprite; }

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * This changes the sprite's state to inactive and halts it.
	 */
	public void stopMovement() {
		isActive = false;
		setVelocity(0.0, 0.0);
	}

	// ----------------------------------------------------------------------------------------------------------

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

	// ----------------------------------------------------------------------------------------------------------

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

	// ----------------------------------------------------------------------------------------------------------

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

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Updates the sprite velocity and position.
	 *
	 * @param time Double value that should reflect
	 */
	public void update(double time) {
		positionX += velocityX * time;
		positionY += velocityY * time;
	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Pauses the sprite's animation and stores the previous velocity values.
	 */
	public void pauseSprite() {
		velocityX = 0.0;
		velocityY = 0.0;
	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Resume the sprite's animation. Should only be called if the sprite has been paused.
	 */
	public void resumeSprite() {
		velocityX = storedVelocityX;
		velocityY = storedVelocityY;
	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Renders the sprite position.
	 *
	 * @param graphicsContext
	 */
	public void render(GraphicsContext graphicsContext) {
		graphicsContext.drawImage(image, positionX, positionY);
	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Gets the boundary of the sprite for collision detection.
	 *
	 * @return
	 */
	public Rectangle2D getBoundary() {//Narrow bounds -15 X -15 to increas fairness of intersections
		return new Rectangle2D(positionX, positionY, width-15, height-15);
	}

	// ----------------------------------------------------------------------------------------------------------

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

	// ----------------------------------------------------------------------------------------------------------

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
		if(sprite.getPositionX() > screen.getScreenWidth() || sprite.getPositionY() >screen.getScreenHeight()) {
			flag = true;
		}
		return flag;
	}

	// ----------------------------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return "Sprite [image=" + image + ", positionX=" + positionX + ", positionY=" + positionY + ", velocityX="
				+ velocityX + ", storedVelocityX=" + storedVelocityX + ", velocityY=" + velocityY + ", storedVelocityY="
				+ storedVelocityY + ", vulnerable=" + vulnerable + ", renderSprite=" + renderSprite + ", width=" + width
				+ ", height=" + height + ", isActive=" + isActive + ", spriteIV=" + spriteIV + "]";
	}

	// ----------------------------------------------------------------------------------------------------------

	public double getPositionX() {
		return positionX;
	}

	// ----------------------------------------------------------------------------------------------------------

	public double getPositionY() {
		return positionY;
	}

	// ----------------------------------------------------------------------------------------------------------

	public double getVelocityX() {
		return velocityX;
	}

	// ----------------------------------------------------------------------------------------------------------

	public double getVelocityY() {
		return velocityY;
	}

	// ----------------------------------------------------------------------------------------------------------

	public ImageView getSpriteIV() {
		return spriteIV;
	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Checks whether or not the sprite is currently active or not.
	 * 
	 * @return Returns the state of the sprite (if it is on screen or not)
	 */
	public boolean isActive() {
		return isActive;
	}

}
