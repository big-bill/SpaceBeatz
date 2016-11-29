package spacebeatzgame;

public class NPCSprite extends Sprite {
    /**
     * About NPCSprite
     * extends the sprite class minimal current usage as of 11/28/16
     * may by used in future releases to extend functionality of non-playable
     * sprites.
     */
    public NPCSprite() {
        super();
    }
    
	// ----------------------------------------------------------------------------------------------------------

    public NPCSprite(String imageFile, double setWidth, double setHeight, boolean preserveRatio, boolean smooth) {
        super();
        super.setAllImageAttributes(imageFile, setWidth, setHeight, preserveRatio, smooth);
    }

    // TODO: Add code regarding spawn rate and spawn position on Y axis, change spawns to right side as well
}
