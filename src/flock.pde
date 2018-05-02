class Flock {
  ArrayList<Boid> boids; // An ArrayList for all the boids

  Flock() {
    boids = new ArrayList<Boid>(); // Initialize the ArrayList
  }
  class RunParallel extends Thread{
 
   int start;
   int end;
   ArrayList<Boid> copy_of_boids;
 
    public RunParallel(int st, int en, ArrayList<Boid> co){
      start = st;
      end = en;
      copy_of_boids = co;
    }
 
    public void run(){
      for (int i = start; i < end; ++i) {
        boids.get(i).run(copy_of_boids);
      }
    }
  }
  void run() {
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

  void addBoid(Boid b) {
    boids.add(b);
  }

}