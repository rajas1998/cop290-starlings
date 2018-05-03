
//! Class to represent a single unit or boid
class Boid {


  //! Vector to show the position of the current boid
  PVector position;
  //! Vector to show the velocity of the current boid
  PVector velocity;
  //! Vector to show the acceleration of the current boid
  PVector acceleration;
  //! ArrayList containing all the neighbours defined by the radius in which they must exist
  /*
    There is a defined distance within which boids are considered neighbours
  */
  ArrayList<Boid> neighbors;
  //! Time taken by the boid to decide when to change its position
  int decision_timer = 0;
  //! Mass of the current boid
  float mass = 0.058; 
  //! Energy of the current boid
  float energy;
  //! Momentum of the current boid
  float momentum;

  //! Initialization function
  /*! 
    Function to initialize the boid position and velocity
    \param x x-coordinate of the boid
    \param y Y-coordinate of the boid
  */
  Boid(float x, float y) {
    acceleration = new PVector(0, 0);
    float angle = random(TWO_PI);
    velocity = new PVector(cos(angle), sin(angle));
    position = new PVector(x, y);
  }
  //! Calculate the neighbours of the boid
  /*!
    Goes over the boids calculating which of the boids should be included in the neighbours
    \param boids The list containing all boids
  */
  void calc_neighbours(ArrayList<Boid> boids){
    ArrayList<Boid> next_neighbors = new ArrayList<Boid>();
    for (Boid other:boids){
      float d = PVector.dist(position, other.position);
      if (d>0 && d<maxr && this!=other){
        next_neighbors.add(other);
      }
    }
    neighbors = next_neighbors;
  }

  //! Main function to integrate all the updates of the boid, position, velocity and acceleration
  /*!
    \param boids The list containing all boids
  */
  void run(ArrayList<Boid> boids) {
    if (decision_timer == 0){
      calc_neighbours(boids);
    }
    increment();
    flock(boids);
    update();
    borders();
    // render();
  }

  //! Increment the timer
  void increment(){
    decision_timer = (decision_timer + 1) % time_for_update;
  }

  //! Apply the particular force
  /*!
    This function simply adds the acceleration passed to it to the total acceleration
    \param force The acceleration which must be added to the total acceleration.
  */
  void applyForce(PVector force) {
    acceleration.add(force);
  }

  //! Accumulates a new acceleration each time based on three rules
  /*!
    \param boids The list containing all boids
  */
  void flock(ArrayList<Boid> boids) {
    PVector sep = separate();   // Separation
    PVector ali = align();      // Alignment
    PVector coh = cohesion();   // Cohesion
    PVector noise = new PVector(random(2) - 1, random(2) -1); 
    PVector obst = avoidObstacles();
    PVector gravity = new PVector(0,1);

    // Arbitrarily weight these forces
    sep.mult(1.2);
    ali.mult(1.0);
    coh.mult(1.3);
    noise.mult(0.05);
    obst.mult(2.0);
    gravity.mult(0.002);

    // Add the force vectors to acceleration
    applyForce(sep);
    applyForce(ali);
    applyForce(coh);
    applyForce(noise);
    applyForce(obst);
    applyForce(gravity);

  }

  //! Method to update position
  void update() {
    energy = 0.5 * mass * (velocity.mag() * velocity.mag());
    momentum = mass * velocity.mag();
    velocity.add(acceleration);
    velocity.limit(maxspeed);
    position.add(velocity);
    acceleration.mult(0);
  }

  PVector seek(PVector target) {
    PVector desired = PVector.sub(target, position);  // A vector pointing from the position to the target
    desired.normalize();
    desired.mult(maxspeed);
    PVector steer = PVector.sub(desired, velocity);
    steer.limit(maxforce);  
    return steer;
  }
  //! Method to render the boid according to the current heading, velocity and position
  /*!
      Renders the boids accordingly
  */
  void render() {
    float theta = velocity.heading2D() + radians(90);
    
    fill(200, 100);
    stroke(255);
    pushMatrix();
    translate(position.x, position.y);
    rotate(theta);
    beginShape(TRIANGLES);
    vertex(0, -r*2);
    vertex(-r, r*2);
    vertex(r, r*2);
    endShape();
    popMatrix();
  }

  //! Wraparound the boid along corners
  /*!
      Modulo the dimensions of the screen
  */
  void borders() {
    if (position.x < -r) position.x = width+r;
    if (position.y < -r) position.y = height+r;
    if (position.x > width+r) position.x = -r;
    if (position.y > height+r) position.y = -r;
  }

  //! The rule for the separation of boids
  PVector separate () {
    PVector steer = new PVector(0, 0, 0);
    int count = 0;
    for (Boid other : neighbors) {
      float d = PVector.dist(position, other.position);
      if ((d > 0) && (d < desiredseparation)) {
        PVector diff = PVector.sub(position, other.position);
        diff.normalize();
        diff.div(d);
        // if (d < 2)
        //   diff.mult(0.1);        
        steer.add(diff);
        count++;      
      }
    }

    if (count > 0) {
      steer.div((float)count);
    }

    if (steer.mag() > 0) {
      steer.normalize();
      steer.mult(maxspeed);
      steer.sub(velocity);
      steer.limit(maxforce);
    }
    return steer;
  }
  //! The rule for the alignmenty of boids
  PVector align () {
    PVector sum = new PVector(0, 0);
    int count = 0;
    for (Boid other : neighbors) {
      sum.add(other.velocity);
      count++;
    }
    if (count > 0) {
      sum.div((float)count);
      sum.normalize();
      sum.mult(maxspeed);
      PVector steer = PVector.sub(sum, velocity);
      steer.limit(maxforce);
      return steer;
    } 
    else {
      return new PVector(0, 0);
    }
  }

   //! The rule for the cohesion of boids
   PVector cohesion () {
    PVector sum = new PVector(0, 0);   // Start with empty vector to accumulate all positions
    int count = 0;
    for (Boid other : neighbors) {
        sum.add(other.position); 
        count++;
    }
    if (count > 0) {
      sum.div(count);
      return seek(sum);  // Steer towards the position
    } 
    else {
      return new PVector(0, 0);
    }
  }
  //! The rule for avoidance of obstacles by the boids
  PVector avoidObstacles(){
    PVector steer = new PVector(0, 0, 0);
    int count = 0;
    for (Obstacle other : obstacles) {
      float d = PVector.dist(position, other.position);
      if ((d > 0) && (d < obstacle_separation)) {
        PVector diff = PVector.sub(position, other.position);
        diff.normalize();
        diff.div(d);        
        steer.add(diff);
        count++;      
      }
    }
    return steer;
  }
}


