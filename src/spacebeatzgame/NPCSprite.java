/**
 * Extends the sprite class
 * Currently not much usage, could be extended for future uses.
 * 
 * @author Billy Matthews
 * @author Robert Munshower
 * @author Andrew Smith
 */

package spacebeatzgame;

public class NPCSprite extends Sprite {
	
	public NPCSprite() {
		super();
	}

	// ----------------------------------------------------------------------------------------------------------

	public NPCSprite(String imageFile, double setWidth, double setHeight, boolean preserveRatio, boolean smooth) {
		super();
		super.setAllImageAttributes(imageFile, setWidth, setHeight, preserveRatio, smooth);
	}

	// ----------------------------------------------------------------------------------------------------------

	/**
	 * This places the sprite onto the screen and sets the visibility option to true, which 
	 * will allow for the sprite to be rendered and updated.
	 * For NPC sprites, this will also check if the sprite spawns in a bad position and will reposition it.
	 * 
	 * @param screen Used to place the sprite on the right side of the screen
	 */
	public void activate(ScreenAttributes screen) {
		pauseSprite();
		setPosition(screen.getScreenWidth() + 80, (Math.random() * screen.getBoundary().getMaxY()));
		if(getBoundary().getMinY() <= screen.getBoundary().getMinY())
			setPosition(positionX, screen.getBoundary().getMinY()); 
		if(getBoundary().getMaxY() >= screen.getBoundary().getMaxY() - 15)
			setPosition(positionX, screen.getBoundary().getMaxY() - 80);
		addVelocity((Math.random() * (-100) - 400), 0);
		storedVelocityX = velocityX;
		storedVelocityY = velocityY;
	}

}
