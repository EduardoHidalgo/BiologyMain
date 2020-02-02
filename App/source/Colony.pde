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

        seconds = int(random(2,5));
    }

    void render()
    {
        update();
        borders();
        display();
    }

    void update()
    {   
        RandomDirection();
        velocity.add(direction); //le da dirección al movimiento
        colonyPosition.add(velocity); //hace avanzar la colonia
        
        aceleration.limit(0.01f);
        velocity.limit(0.01f);
    }

    void display()
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

    void borders()
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
            direction.setMag(.5); //normaliza y multiplica

            countSeconds = 0;
            seconds = int(random(2,5));
        }
    }
}
