//! Class to represent the obstacles
class Obstacle {
	//! Represents the position of the obstacle
	PVector position;
	Obstacle(float x, float y) {
		position = new PVector(x,y);
	}
	//! Drawing the obstacle at the point specified by the position of the obstacle.
	void draw(){
		fill(0, 255, 200);
     	ellipse(position.x, position.y, 15, 15);
	}
}