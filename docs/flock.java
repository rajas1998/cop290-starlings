//! Class to maintain the aggregate behaviour of boids
public class Flock {
  //! List of boids in the flock
  public ArrayList<Boid> boids; // An ArrayList for all the boids
  
  float totalenergytemp;
  // The total energy of the flock
  public float totalenergy;
  float totalmomentumtemp;
  //! The total momentum of the flock
  public float totalmomentum;

  //! The number of times the display is updated before the speed shown changes
  public int notimes = 100;

  //! The timer/clock to look at the changes seen in the flock
  public int timer = 5;

  Flock() {
    boids = new ArrayList<Boid>(); // Initialize the ArrayList
  }
  //! Thread class to run the boids in parallel
  public class RunParallel extends Thread{
   //! The start of the boid array
   int start;
   //! The end of the boid array
   int end;
   //! The copy of the state of boids so that threads can be run in parallel
   ArrayList<Boid> copy_of_boids;
 
    public RunParallel(int st, int en, ArrayList<Boid> co){
      start = st;
      end = en;
      copy_of_boids = co;
    }
    //! The function to run the thread of the boid array
    /*!
      The function is run independent of the other threads and the state of the boids is saved sp that
      there is no problem due to early updation of the boids in the flock
    */
    public void run(){
      for (int i = start; i < end; ++i) {
        boids.get(i).run(copy_of_boids);
      }
    }
  }
  //! Run the entire flock
  /*! Runs the entire flock code*/
  public void run() {
    timer--;
    if (timer == 0){
      totalenergy = totalenergytemp;
      totalmomentum = totalmomentumtemp;
      timer = notimes;
    }
    totalenergytemp = 0;
    totalmomentumtemp = 0;
    for (Boid b : boids) {
      totalenergytemp += b.energy;
      totalmomentumtemp += b.momentum;
    }
    ArrayList<Boid> copy = new ArrayList<Boid>();
    for (Boid b : boids) {
      copy.add(b);
    }
    RunParallel p1 = new RunParallel(0,num_of_boids/4, copy);
    RunParallel p2 = new RunParallel(num_of_boids/4,num_of_boids/2, copy);
    RunParallel p3 = new RunParallel(num_of_boids/2,(num_of_boids * 3)/4, copy);
    RunParallel p4 = new RunParallel((num_of_boids * 3)/4,num_of_boids, copy);
    p1.start();
    p2.start();
    p3.start();
    p4.start();
    try {
      p1.join();
    } catch (Exception InterruptedException) {
      System.out.println("Hi");
    }
    try {
      p2.join();
    } catch (Exception InterruptedException) {
      System.out.println("Hi");
    }
    try {
      p3.join();
    } catch (Exception InterruptedException) {
      System.out.println("Hi");
    }
    try {
      p4.join();
    } catch (Exception InterruptedException) {
      System.out.println("Hi");
    }
    for (Boid b: boids) {
        b.render();
    } 
  }

  public void addBoid(Boid b) {
    boids.add(b);
  }

}