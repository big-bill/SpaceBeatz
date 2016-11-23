package spacebeatzgame;

public class npcSprite extends Sprite {
	
	public npcSprite() {
		super();
	}
	
	public npcSprite(String imageFile, double setWidth, double setHeight, boolean preserveRatio, boolean smooth) {
		super();
		super.setAllImageAttributes(imageFile, setWidth, setHeight, preserveRatio, smooth);
	}
	
	/**
	 * Update the NPC's position.
	 * However, since NPCs travel from right to left, we subtract from the axis positions.
	 */
	public void update(double time) {
		positionX -= velocityX * time;
        positionY -= velocityY * time;
	}
    
	// TODO: Add code regarding spawn rate and spawn position on Y axis, change spawns to right side as well
}
