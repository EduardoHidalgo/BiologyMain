class Cell {

  PImage img;

  PVector location;
  PVector velocity;
  PVector acceleration;

  float maxforce = 0.1f;    // Maximum steering force
  float maxspeed = 2f;    // Maximum speed

  int seconds;
  int countSeconds;
  int count;
  
  boolean duplicated = false;

  //Constructor
  Cell(PVector position)
  {
    img = loadImage("cell.png");
    location = position;
    acceleration = new PVector(0, 0);
    velocity = new PVector(0, 0);

    seconds = int(random(5,10));
    countSeconds = 0;
    count = 0;
  }

  void render(BiologyMain main, ArrayList<Cell> cells, Colony colony)
  {
    behaviors(main, cells, colony);
    update();
    display();
  }
  
  void behaviors(BiologyMain main, ArrayList<Cell> cells, Colony colony)
  {
     PVector separateForce = separate(cells);
     PVector seekForce = seek(colony.colonyPosition);
     separateForce.mult(2);
     seekForce.mult(1);
     acceleration.add(separateForce);
     acceleration.add(seekForce);
     duplicate(main);
  }
  
  // A method that calculates a steering force towards a target
  PVector seek(PVector target)
  {
    PVector desired = PVector.sub(target,location);  // A vector pointing from the location to the target
    
    desired.normalize(); // Normalize desired and scale to maximum speed
    desired.mult(maxspeed);
    PVector steer = PVector.sub(desired,velocity); // Steering = Desired minus velocity
    steer.limit(maxforce);  // Limit to maximum steering force
    
    return steer;
  }

  // Method checks for nearby vehicles and steers away
  PVector separate (ArrayList<Cell> cells)
  {
    float desiredSeparation = img.height - 4;
    PVector sum = new PVector();
    int count = 0;
    // For every boid in the system, check if it's too close
    for (Cell cell : cells)
    {
      float distance = PVector.dist(location, cell.location);
      // If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
      if ((distance > 0) && (distance < desiredSeparation))
      {
        // Calculate vector pointing away from neighbor
        PVector diff = PVector.sub(location, cell.location);
        diff.normalize();
        diff.div(distance);        // Weight by distance
        sum.add(diff);
        count++;            // Keep track of how many
      }
    }
    // Average -- divide by how many
    if (count > 0)
    {
      sum.div(count);
      // Our desired vector is the average scaled to maximum speed
      sum.normalize();
      sum.mult(maxspeed);
      // Implement Reynolds: Steering = Desired - Velocity
      sum.sub(velocity);
      sum.limit(maxforce);
    }
    return sum;
  }
  
  void duplicate(BiologyMain main)
  {
    if(!duplicated)
    {
      count++;
      
      if(count == 30)
      {
        countSeconds++;
        count = 0;
      }
  
      if(countSeconds == seconds)
      {     
        main.duplicate(new PVector(location.x + random(- 10, 10), location.y + random(- 10, 10)));
        countSeconds = 0;
        duplicated = true;
      }
    }
  }


  // Method to update location
  void update()
  {
    // Update velocity
    velocity.add(acceleration);
    // Limit speed
    velocity.limit(maxspeed);
    location.add(velocity);
    // Reset accelertion to 0 each cycle
    acceleration.mult(0);
  }

  void display()
  {
    pushMatrix();
    translate(location.x, location.y);
    image(img, 0, 0);
    popMatrix();
  }
}