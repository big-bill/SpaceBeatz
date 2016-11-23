package spacebeatzgame;

public class npcSprite extends Sprite {
	
	public npcSprite() {
		super();
	}
	
	public npcSprite(String imageFile, double setWidth, double setHeight, boolean preserveRatio, boolean smooth) {
		super();
		super.setAllImageAttributes(imageFile, setWidth, setHeight, preserveRatio, smooth);
	}
    
	// TODO: Add code regarding spawn rate and spawn position on Y axis, change spawns to right side as well
}
