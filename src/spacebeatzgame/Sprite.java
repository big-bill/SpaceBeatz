/**
 * This is the menu controller for the MainMenu.fxml file.
 * 
 * @author Billy Matthews
 * @author Robert Munshower
 * @author Andrew Smith
 */
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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
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
	 * Timeline for flashing the sprite
	 */
	protected Timeline userHit;

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Constructor no-args sets default position and velocities to zero.
	 * Sets the sprite's render status to false
	 */
	public Sprite() {
		image = null;
		vulnerable = true;
		renderSprite = false;
		width = 0.0;
		height = 0.0;
		positionX = 0.0;
		positionY = 0.0;
		velocityX = 0.0;
		velocityY = 0.0;

		userHit = new Timeline(new KeyFrame(Duration.seconds(.15), new EventHandler<ActionEvent>() {
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

		// Set the cycle count to 20, flashing the sprite 10 times
		userHit.setCycleCount(20);
	}

	// ----------------------------------------------------------------------------------------------------------

	public Sprite(String imageFile, double setWidth, double setHeight, boolean preserveRatio, boolean smooth) {
		this();
		setAllImageAttributes(imageFile, setWidth, setHeight, preserveRatio, smooth);
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
	 * @param smooth Boolean to smooth Image 
	 */
	public void setAllImageAttributes(String imageFile, double setWidth, double setHeight, boolean preserveRatio, boolean smooth) { 
		try {
			File file = new File(imageFile);
			URI uri = file.toURI();
			URL url = uri.toURL();
			this.image = new Image(url.toString(), setWidth, setHeight, preserveRatio, smooth);
			width = image.getRequestedWidth();
			height = image.getRequestedHeight();
		} 
		catch(IllegalArgumentException | MalformedURLException e) {
			System.err.print(e.getMessage());
			System.exit(1);
		}

	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Move the sprite based on the input provided with the ArrayList passed.
	 * Also determines whether or not the sprite can move in a direction any further 
	 * based on the boundaries passed by ScreenAttributes object.
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
	 * @param collisions The total amount of collisions is passed
	 * @return returns the total amount of collisions with the sprite
	 */
	public int hitSprite(int collisions) {
		// First check if the user is vulnerable (user can't get hit if they were just hit and now flashing)
		if(vulnerable) {
			collisions++; // If vulnerable, we increment the total amount of collisions
			renderSprite = vulnerable = false;
			userHit.play();
		}
		// Return the new total of collision counter back to the game (will be counted on the hud)
		return collisions;
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
	 * This changes the sprite's state to inactive and halts it.
	 */
	public void stopMovement() {
		renderSprite = false;
		setVelocity(0.0, 0.0);
	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Sets the position of the sprite and sets the status for rendering the sprite to true.
	 *
	 * @param xPos
	 * @param yPos
	 */
	public void setPosition(double xPos, double yPos) {
		renderSprite = true;
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
	 * Renders the sprite position.
	 *
	 * @param graphicsContext
	 */
	public void render(GraphicsContext graphicsContext) { graphicsContext.drawImage(image, positionX, positionY); }

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * This method returns the render status of the sprite.
	 * 
	 * @return The status of if the sprite should be rendered on screen or not
	 */
	public boolean getRenderSprite() { return renderSprite; }

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Sets the status on whether or not the sprite should be rendered on the screen or not.
	 * s
	 * @param renderStatus Determines if the sprite will be rendered
	 */
	public void setRenderSprite(boolean renderStatus) { renderSprite = renderStatus;  }

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * Gets the boundary of the sprite for collision detection.
	 *
	 * @return
	 */
	public Rectangle2D getBoundary() {
		//Narrow bounds -15 X -15 to increase fairness of intersections
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
	public boolean intersects(Sprite sprite) { return sprite.getBoundary().intersects(this.getBoundary()); }

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
		if(sprite.getPositionX() > screen.getScreenWidth() || sprite.getPositionY() > screen.getScreenHeight())
			return true;
		return false;
	}

	// ----------------------------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return "Sprite [image=" + image + ", positionX=" + positionX + ", positionY=" + positionY + ", velocityX="
				+ velocityX + ", storedVelocityX=" + storedVelocityX + ", velocityY=" + velocityY + ", storedVelocityY="
				+ storedVelocityY + ", vulnerable=" + vulnerable + ", renderSprite=" + renderSprite + ", width=" + width
				+ ", height=" + height + "]";
	}

	// ----------------------------------------------------------------------------------------------------------

	public double getPositionX() { return positionX; }

	// ----------------------------------------------------------------------------------------------------------

	public double getPositionY() { return positionY; }

	// ----------------------------------------------------------------------------------------------------------

	public double getVelocityX() { return velocityX; }

	// ----------------------------------------------------------------------------------------------------------

	public double getVelocityY() { return velocityY; }

}
