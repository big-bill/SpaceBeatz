/*
 *  Author:       Robert Munshower
 *  File Name:    Hud
 *  Package:      spacebeatzgame
 *  Project Name: SpaceBeatz 
 *  Created:      Nov 27, 2016
 *  Last Modified: 
 */

package spacebeatzgame;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *About Hud
 * 
 * 
 */
public class Hud {
    private Rectangle hudRect = new Rectangle(600,100);
    private Image shipImage;
    private final double WIDTH = 10;
    private final double HEIGHT = 10;
    
    /**
     * Constructor for Hud
     */
    Hud(){
        
//        hudRect.setFill(Color.SKYBLUE);
//                hudRect.setArcHeight(20);
//                hudRect.setArcWidth(20);
//                hudRect.setOpacity(.3);
//                
//                HBox livesBox = new HBox();
//                Image[] livesArray;
//                try {
//			File shipFile = new File("src/spacebeatzgame/res/ship.png");
//			URI uri = shipFile.toURI();
//			URL url = uri.toURL();
//			shipImage = new Image(url.toString(),WIDTH,HEIGHT, preserveRatio, smooth);
//			
//		} 
//		catch(IllegalArgumentException | MalformedURLException e) {
//			e.printStackTrace();
//		}
//                for (int lifeCounter = 0; lifeCounter < 3; lifeCounter++){
//                    livesArray = new Image[lifeCounter](""); 
//                }
    
    }

}
