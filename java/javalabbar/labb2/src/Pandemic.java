import java.awt.*;
import javax.swing.*;

class DisplayPandemic {
      
    static final double   GRAPH_SCALE = 0.7;

    private PandemicPanel pandemicPanel;
    private GraphPanel    graphPanel;

    DisplayPandemic(Village theVillage) {
        
        JFrame theFrame = new JFrame("Pandemic Simulator");
        pandemicPanel   = new PandemicPanel(theVillage);
        graphPanel      = new GraphPanel(theVillage);

        theFrame.setLayout(new BorderLayout());
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theFrame.add(pandemicPanel, BorderLayout.CENTER);
        theFrame.add(graphPanel,    BorderLayout.SOUTH);
        theFrame.pack();
        theFrame.setVisible(true);
    }

    void show() {
        
        pandemicPanel.repaint();
        graphPanel.   repaint();
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            System.out.println("Error in pausing Thread " + e.getMessage());
        }
    }
}

class PandemicPanel extends JPanel {
    
    private final Village theVillage;
    private final int SIZE;

    PandemicPanel(Village v) {
        
        theVillage = v;
        SIZE =       (int)(theVillage.SIZE * DisplayPandemic.GRAPH_SCALE);
        setPreferredSize(new Dimension(SIZE, SIZE));
    }
    
    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, SIZE, SIZE);
        
        for (Person person : theVillage.population) 
            paintPerson(person, g);
    }
    
    private void paintPerson(Person person, Graphics g) {
        
        final int personSize = (int)(5 * DisplayPandemic.GRAPH_SCALE) ;
        
        Color color = Color.WHITE;
        if (person.isSick)             color = Color.RED;
        if (person.daysLeftImmune > 0) color = Color.GREEN;
        if (person.isDead)             color = Color.BLACK;
        
        g.setColor(color);
        int xcoord = (int)(person.xPos * DisplayPandemic.GRAPH_SCALE);
        int ycoord = (int)(person.yPos * DisplayPandemic.GRAPH_SCALE);

        g.fillOval(xcoord - personSize, ycoord - personSize,
                personSize * 2, personSize * 2);
    }
}

class GraphPanel extends JPanel {
    
    private final Village theVillage;
    private final int STEP_WIDTH = (int)(10 * DisplayPandemic.GRAPH_SCALE);
    
    private final int xSize,ySize;

    private final int numberOfSteps;
    private int [] xCoord, deadYCoord, sickYCoord, immuneYCoord;
    private int time = 0;
    
    GraphPanel(Village v) {
        
        theVillage = v;
        xSize = (int)(theVillage.SIZE * DisplayPandemic.GRAPH_SCALE);
        ySize = xSize/5;
        
        setPreferredSize(new Dimension(xSize, ySize));
        numberOfSteps = xSize/STEP_WIDTH;
        
        initiateGraphCoordinates();
    }
    
    private void initiateGraphCoordinates() {
        
        int numberOfCoords = (numberOfSteps + 1) * 2;
        
        xCoord =       new int[numberOfCoords];
        deadYCoord =   new int[numberOfCoords];
        sickYCoord =   new int[numberOfCoords];
        immuneYCoord = new int[numberOfCoords];

        for (int i = 0; i <= numberOfSteps; i++) {
            xCoord[i] = i * STEP_WIDTH;
            xCoord[numberOfSteps + i + 1] = (numberOfSteps - i) * STEP_WIDTH;
        }

        for (int i = 0; i < numberOfCoords; i++) {
            deadYCoord[i]   = ySize;
            sickYCoord[i]   = ySize;
            immuneYCoord[i] = 0;
        }
    }
    
    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);

        shiftCoordsLeft();
        computeNewCoords();
        paintGraphs(g);

        time++;
    }
    
    private void computeNewCoords() {
        
        int numberOfDead=0, numberOfSick=0, numberOfImmune=0;
        
        for (Person person: theVillage.population) {
            if (person.isDead)           numberOfDead++;
            if (person.isSick)           numberOfSick++;
            if (person.daysLeftImmune>0) numberOfImmune++;
        }
        double  deadProportion   = (double)numberOfDead / theVillage.population.length;
        double  sickProportion   = (double)numberOfSick / theVillage.population.length;
        double  immuneProportion = (double)numberOfImmune / theVillage.population.length;

        deadYCoord[numberOfSteps]   = (int)(ySize * (1-deadProportion));
        sickYCoord[numberOfSteps]   = (int)(ySize * (1-deadProportion - sickProportion));
        immuneYCoord[numberOfSteps] = (int)(ySize * immuneProportion);
    }
    
    private void shiftCoordsLeft() {
        
        for (int i=1; i<=numberOfSteps; i++) {
            deadYCoord[i-1]   = deadYCoord[i];
            sickYCoord[i-1]   = sickYCoord[i]; 
            immuneYCoord[i-1] = immuneYCoord[i];
        }
    }
    
    private void paintGraphs(Graphics g) {
        
        g.setColor(Color.WHITE);
        g.fillRect(0,0,xSize, ySize);
        
        paintGraph(immuneYCoord, Color.GREEN, g);
        paintGraph(sickYCoord,   Color.RED,   g);
        paintGraph(deadYCoord,   Color.BLACK, g);        

        g.setColor(Color.GRAY);
        if (time<=numberOfSteps)
            g.fillRect(0,0,STEP_WIDTH*(numberOfSteps-time), ySize);
    }
    
    private void paintGraph(int [] yCoord, Color color, Graphics g) {
        
        g.setColor(color);
        g.fillPolygon(xCoord, yCoord, xCoord.length);
    }
}
