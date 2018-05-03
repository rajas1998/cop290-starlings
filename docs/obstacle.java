//! Class to represent the obstacles
public class Obstacle {
	//! Represents the position of the obstacle
	public PVector position;
	Obstacle(float x, float y) {
		position = new PVector(x,y);
	}
	//! Drawing the obstacle at the point specified by the position of the obstacle.
	public void draw(){
		fill(0, 255, 200);
     	ellipse(position.x, position.y, 15, 15);
	}
}