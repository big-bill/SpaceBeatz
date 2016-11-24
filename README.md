# Space Beatz Game

<B>TODO LIST</B>

WHEN AN ITEM IS COMPLETED PLEASE DON'T REMOVE FROM THE LIST 
INSTEAD APPEND '<B>(DONE: YOUR NAME)</B>' TO THE LAST LINE OF THE ITEM AND USE <strike>striketags</strike> AROUND THE ITEM.

<b>*)</b><strike> Class npcSprite extends sprite and is currently blank much of the logic located in the main including the random position & velocity generation needs to be move to this class.</strike><b>IDEA SCRAPPED</b>

<b>*)</b> The class sprite needs to show a blow up image after collision with another sprite or we need to implement another ideajust because we collide and blow up doesn't mean the game has to end it just means we wasted a life or something else will happen.

<b>*)</b> <strike>The games logic is located below the "new AnimationTimer()" line 120 on my end 
NOTE: ALL GAME LOGIC HAS TO BE INSIDE THE TIMER The entire thing should really be in its own class, AnimationTimer is abstract if nothing else all the 'if()' statements should be contained in methods.</strike><b>DONE: BIG BILL</b>

<b>*)</b>) <strike>Game is run in full screen mode pressing the escape key should pause the game it currently continues to run this can be done with animationTimer.pause() or something like that</strike><b>DONE: BIG BILL</b>

<b>*)</b> <strike>The randomness of enemies and asteroids is not that random it needs to be fine tuned all the Math.random()
often there are low traffic areas in the game the code was just a quick
fix for display purposes We need probably two or three random number generators  with min and max limits one for setting the size of the enemy/obstacle Images probably not much bigger than 80x80, one for setting positions of the enemyobstacle sprites and one for velocity.</strike><b>DONE: BIG BILL</b>

<b>*)</b> <strike>Worry about image changes when everything works, also don't worry about the space.png background image this will be replace by the audio visualization "maybe" if not we can later make it move to cause give a traveling effect</strike><b>Done: Munshower</b>

<b>*)</b> <strike>Difficulty currently the main generates 50 enemysprites with the random postioning and velocity and size tune this may be way too much we will need to implement a way maybe to alter this value prior to game start maybe in the gui or maybe
based of audio file which I think is possible but difficult to implement. Also we can simply limit the the the amount of of enemies by not allowing the the loop to traverse the entire array ie make array and of 100 enemies maybe we use them maybe we don't but place a var in the loop the that limits traversal.</srtike><b>DONE: BIG BILL</b>

<b>*)</b><strike>Ship can travel out of the window bounds we either need to hinder this or allow the the ship to re-emerge on the opposite end of the screen which could add an interesting dynamic during game play.</strike><b>DONE: Munshower</b> 

<b>*)</b>At the end, include the JAR and a BAT file to run the game



<b>WISHES/IDEAS</b>

<b>*)</b> Create HUD 'Heads up display lives score etc'

<b>*)</b> Power up sprites that float by the same as enemies. shields could protect the ship from collision could decay over time or with number of collisions. Bomb power ups that fire when collected destroying sprites around the ship or could be held until the user wishes to use it to clear an area around them.

<b>*)</b> Sharks with freakin laser beams attached to their heads, or just a laser weapon to destroy obstacles/enemies attached to the ship.

<b>*)</b> Alien ships could/should randomly change direction during travel to give them a sort of AI.
 
