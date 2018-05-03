Flock flock;

static float maxspeed = 4;
static float maxforce = 0.03;
static float r = 2.0;
static float maxr = 50; 
static int time_for_update = 5;
static float desiredseparation = 20.0f;
static float obstacle_separation = desiredseparation * 2;
static int num_of_obstacles = 30;
static int num_of_boids = 1000;
String current = "boid";

ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();

void setup() {
  size(1025, 576);
  flock = new Flock();
  // Add an initial set of boids into the system
  for (int i = 0; i < num_of_boids; i++) {
    flock.addBoid(new Boid(random(width),random(height)));
  }
  for (int i = 0; i < width; i+= width/num_of_obstacles) {
  	 obstacles.add(new Obstacle(i,10));
  }
}

void draw() {
  background(50);
  flock.run();
  for (int i = 0; i <obstacles.size(); i++) {
    Obstacle current = obstacles.get(i);
    current.draw();
  }
}

// Add a new boid into the System
void mousePressed() {
  if (current == "boid")
  {
    flock.addBoid(new Boid(mouseX,mouseY));
    num_of_boids++;
  }
  else if (current == "obstacle") {
    obstacles.add(new Obstacle(mouseX,mouseY));
  }
}

void keyPressed () {
  if (key == 'b') {
    current = "boid";
  } else if (key == 'o') {
    current = "obstacle";
  }
}
