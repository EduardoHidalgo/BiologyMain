import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class BiologyMain extends PApplet {


ArrayList<Cell> cells;
Colony colony;

public void setup() 
{
  
  //configs();
  
  frameRate(60);

  cells = new ArrayList<Cell>();
  colony = new Colony();

  //Crea una célula semilla que se duplicará
  //seed = new Cell(cells, colony);
  for (int i = 0; i < 10; ++i)
    cells.add(new Cell(new PVector(random(0, width), random(0, height))));
}

public void draw()
{
  background(255);
  colony.render();

  //Llama los métodos de ejecución
  for (int i = cells.size() - 1; i >= 0; i--)
  {
    Cell temp = cells.get(i);
    temp.render(this, cells, colony);
  }
  
  println("time: " + PApplet.parseInt(millis() / 1000) + " | cells: " + cells.size());
}

//lista de métodos de configuración
public void configs()
{
  size(400,400);
  frameRate(30);
  antialiasing(-1);
}

//antialiasing method
public void antialiasing(int render)
{
  switch(render) 
  {
    case -1:
      noSmooth();
    case 2: 
      smooth(2); //x2
      break;
    case 3: 
      smooth(3); //x3
      break;
      case 4: 
      smooth(4); //x4
      break;
    case 8: 
      smooth(8); //x8
      break;
    default:
      smooth();
      break;
    }
}

public void duplicate(PVector location)
{  
  cells.add(new Cell(location));
}
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

    seconds = PApplet.parseInt(random(5,10));
    countSeconds = 0;
    count = 0;
  }

  public void render(BiologyMain main, ArrayList<Cell> cells, Colony colony)
  {
    behaviors(main, cells, colony);
    update();
    display();
  }
  
  public void behaviors(BiologyMain main, ArrayList<Cell> cells, Colony colony)
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
  public PVector seek(PVector target)
  {
    PVector desired = PVector.sub(target,location);  // A vector pointing from the location to the target
    
    desired.normalize(); // Normalize desired and scale to maximum speed
    desired.mult(maxspeed);
    PVector steer = PVector.sub(desired,velocity); // Steering = Desired minus velocity
    steer.limit(maxforce);  // Limit to maximum steering force
    
    return steer;
  }

  // Method checks for nearby vehicles and steers away
  public PVector separate (ArrayList<Cell> cells)
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
  
  public void duplicate(BiologyMain main)
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
  public void update()
  {
    // Update velocity
    velocity.add(acceleration);
    // Limit speed
    velocity.limit(maxspeed);
    location.add(velocity);
    // Reset accelertion to 0 each cycle
    acceleration.mult(0);
  }

  public void display()
  {
    pushMatrix();
    translate(location.x, location.y);
    image(img, 0, 0);
    popMatrix();
  }
}
public class Colony
{
    //properties
    PVector colonyPosition; //posición de la colonia
    private PVector direction; //hacia donde se mueve la colonia
    private PVector velocity; //la velocidad con la que avanza
    private PVector aceleration; //el incremento de velocidad

    float colonyDiameter = 50;

    float speed = 0.01f;

    //contadores de tiempo para cambiar dirección
    int seconds;
    int countSeconds;
    int count;

    //constructor
    Colony()
    {
        colonyPosition = new PVector(width/2, height/2); //posiciona el circulo en el centro de la pantalla
        direction = new PVector(random(-1, 1), random(-1, 1)); //
        velocity = new PVector(RandomSpeed(), RandomSpeed());
        aceleration = PVector.random2D();

        seconds = PApplet.parseInt(random(2,5));
    }

    public void render()
    {
        update();
        borders();
        display();
    }

    public void update()
    {   
        RandomDirection();
        velocity.add(direction); //le da dirección al movimiento
        colonyPosition.add(velocity); //hace avanzar la colonia
        
        aceleration.limit(0.01f);
        velocity.limit(0.01f);
    }

    public void display()
    {
        //mouse = new PVector(mouseX, mouseY);
        fill(200); //relleno
        stroke(0); //color borde
        strokeWeight(2); //ancho del borde

        pushMatrix(); //guarda la posición del sistema de coordenadas
        translate(colonyPosition.x, colonyPosition.y); //mueve el sistema de coordenadas a esta posición

        //mouse.sub(colonyPosition);
        //mouse.setMag(colonyDiameter);

        //ellipse(0, 0, colonyDiameter, colonyDiameter);        
        //line(0, 0, mouse.x, mouse.y);

        popMatrix(); //regresa el sistema de coordenadas a su posición
    }

    public void borders()
    {
        if(colonyPosition.x < (0 + (colonyDiameter/2)) || colonyPosition.x > (width - (colonyDiameter/2)))
            velocity.x = RandomSpeed(velocity.x);

        if(colonyPosition.y < (0 + (colonyDiameter/2)) || colonyPosition.y > (height - (colonyDiameter/2)))
            velocity.y = RandomSpeed(velocity.y);

    }

    private float RandomSpeed() 
    {
      return random( 0.1f - speed, 0.1f + speed);
    }
    
    private float RandomSpeed(float value) 
    {
      if(value > 0)
      {
        return random( 0 - speed, 0 - speed);
      }
      else
      {
        return random( speed, speed);
      }
    }

    private void RandomDirection()
    {
        count++;
        
        if(count == 30)
        {
            countSeconds++;
            count = 0;
        }

        if(countSeconds == seconds)
        {
            direction = new PVector(random(-0.1f, 0.1f), random(-0.1f, 0.1f)); //la colonia se mueve hacia el mouse
            //direction.sub(colonyPosition); //resta el vector del mouse del vector del centro del circulo  
            direction.setMag(.5f); //normaliza y multiplica

            countSeconds = 0;
            seconds = PApplet.parseInt(random(2,5));
        }
    }
}
  public void settings() {  size(800,800);  smooth(2); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "BiologyMain" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
