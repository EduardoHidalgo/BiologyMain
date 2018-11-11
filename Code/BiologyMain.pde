
ArrayList<Cell> cells;
Colony colony;

void setup() 
{
  size(800,800);
  //configs();
  smooth(2);
  frameRate(60);

  cells = new ArrayList<Cell>();
  colony = new Colony();

  //Crea una célula semilla que se duplicará
  //seed = new Cell(cells, colony);
  for (int i = 0; i < 10; ++i)
    cells.add(new Cell(new PVector(random(0, width), random(0, height))));
}

void draw()
{
  background(255);
  colony.render();

  //Llama los métodos de ejecución
  for (int i = cells.size() - 1; i >= 0; i--)
  {
    Cell temp = cells.get(i);
    temp.render(this, cells, colony);
  }
  
  println("time: " + int(millis() / 1000) + " | cells: " + cells.size());
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

void duplicate(PVector location)
{  
  cells.add(new Cell(location));
}