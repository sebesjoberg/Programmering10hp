import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.net.URL;
import javax.sound.sampled.*;
/**
 *
 * @author joachimparrow 2014
 */
public class Tow{
    public static void main(String[] args) {
            //System.out.println("start");

        new Game();
    }
}


class Game extends JFrame {

    private final int    FIRST_LEVEL = 0;
    private final double FIRST_SCALE = 1.0;

    private GameArea  gameArea;
    private Header    header;
    private Buttons   buttons;

    private Clip crashSound,  demolishSound;
    private Clip [] fanfares = new Clip [3];

    private double scale;
    private int    level;
    private String levelName;

    Game() {

        super();
        // System.out.println("start Game");

        String laf = UIManager.getCrossPlatformLookAndFeelClassName();
         try {
             UIManager.setLookAndFeel(laf);
         } catch (Exception e) {
             System.err.println("Error loading L&F: " + e.getMessage());
         }
         
         // System.out.println("after laf");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());

        setSounds();

        level = FIRST_LEVEL;
        scale = FIRST_SCALE;
            
        // System.out.println("before launch");

        launch();
    }

    private void setSounds() {

        crashSound =    getSound("crash.wav");
        demolishSound = getSound("crash3.wav");
        fanfares[0] =   getSound("fanfare1.wav");
        fanfares[1] =   getSound("fanfare2.wav");
        fanfares[2] =   getSound("fanfare3.wav");
    }

    private Clip getSound(String name) {

        Clip clip;
        try {
            URL url = new URL("http://user.it.uu.se/~joachim/Tow/" + name);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            return clip;
        } catch (Exception e) {
            System.out.println("Could not get sound "+name+".");
            return null;
        }
    }

    private void launch(){

        Container pane = getContentPane();
        pane.removeAll();

        gameArea = new GameArea(this, scale, level);
        pane.add(gameArea, BorderLayout.CENTER);

        levelName = LevelFactory.getName(level);
        int maxTime = gameArea.getMaxTime();
        header = new Header(this, levelName, maxTime);
        pane.add(header, BorderLayout.NORTH);

        setTitle(levelName);

        buttons = new Buttons(this);
        pane.add(buttons, BorderLayout.EAST);

        pack();
        setVisible(true);
        gameArea.setFocusable(true);
        refocus();
    }

    private void playSound(Clip clip) {

        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }
    }

    private void playRandomFanfare() {

         int fanfareNumber = (int)(Math.random()*3);
         playSound(fanfares[fanfareNumber]);
    }

    void refocus() {

        gameArea.requestFocusInWindow();
    }

    private void stop() {

        header.stop();
        gameArea.stop();
    }

    void relaunch() {

        stop();
        launch();
    }

    void launchSize(double s) {

        scale = s;
        relaunch();
    }

    void launchLevel(int lev) {

        level = lev;
        relaunch();
    }

    void launchRandom() {

        level = LevelFactory.maxLevel+1;
        relaunch();
    }

    void timeout(){

        stop();
        JOptionPane.showMessageDialog(this, "Sorry, you took too long. Click OK to restart level");
        launch();
    }

    void tryToGo(String attempt) {

        int target = LevelFactory.findLevel(attempt);
        if (target == LevelFactory.NO_SUCH_LEVEL) {
            JOptionPane.showMessageDialog(this,
                    "No such level. Type the name of a level, precisely as shown when you visited it.");
        }
        else {
            level = target;
            relaunch();
        }
    }

    void crash(String crashMessage) {

        playSound(crashSound);
        JOptionPane.showMessageDialog(this, crashMessage);
    }

    void crashOut() {

        stop();
        playSound(demolishSound);
        JOptionPane.showMessageDialog(this,
                "CRAAAAASSSH. I warned you. Yor car broke down.... Click OK to restart level");
        launch();
    }

    void succeed() {

        stop();
        playRandomFanfare();
        int runTime = header.runTime();
        if (level >= LevelFactory.maxLevel) {
            JOptionPane.showMessageDialog(this, "Congratulations! You finished it in " + runTime +
                                                " seconds. Try it again!");
        } else {
            level++;
            JOptionPane.showMessageDialog(this, "Congratulations! You finished in " + runTime +
                                                " seconds.  Now go to the next level: " + LevelFactory.getName(level));
        }
        launch();
    }
}

//    End class Game -------------------------------------------------------------------------------------


class Header extends JPanel {

    private class TimeKeeper extends JPanel implements ActionListener {

        private int maxTime;
        private int timeLeft;
        private final Color ARC_COLOR = Color.RED;
        private final int SIZE = 75;

        TimeKeeper(int maxTime) {

            this.maxTime = maxTime;
            timeLeft = maxTime;
            setPreferredSize(new Dimension(SIZE, SIZE));
        }

        int runTime() {

            return maxTime - timeLeft;
        }

        @Override
        public void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // This makes the graphics smoother
                    RenderingHints.VALUE_ANTIALIAS_ON);
            final int OFFSET = 12;
            final int ARC_SIZE = 50;
            int angle = (360 * timeLeft) / maxTime;
            g2.setColor(ARC_COLOR);
            g2.fillArc(OFFSET, 2 * OFFSET, ARC_SIZE, ARC_SIZE, 90, angle);
            g2.setColor(Color.BLACK);
            g2.drawString("Time:", (int) (1.5 * OFFSET), OFFSET);
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {

            timeLeft--;
            Header.this.repaint();
            if (timeLeft == 0) {
                game.timeout();
            }
        }
    }


    private Game       game;
    private JLabel     levelName = new JLabel();
    private TimeKeeper timeKeeper;
    private Timer      clock;


    Header(Game g, String name, int maxTime) {

        game = g;

        setLayout(new FlowLayout(FlowLayout.CENTER));
        levelName.setText(name);
        levelName.setFont(new Font("Times", Font.BOLD, 64));
        add(levelName);

        add(Box.createRigidArea(new Dimension(100,0)));
        timeKeeper = new TimeKeeper(maxTime);
        add(timeKeeper);
        clock = new Timer(1000, timeKeeper);
        clock.start();
    }

    int runTime() {

        return timeKeeper.runTime();
    }

    void stop () {

        clock.stop();
    }
}

//    End class Header -------------------------------------------------------------------------------------


class Buttons extends JPanel {

    private Game  game;

    Buttons(Game g) {
        game = g;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        addHelpButton();
        addSizeButton(1.2, "   Large size   ");
        addSizeButton(1.0, "Normal size");
        addSizeButton(0.8, "Small size");
        addSizeButton(0.6, "Tiny size");
        addStartButton();
        addRandomButton();
        addGoField();
    }

    private void addHelpButton() {

        final String helpText = "Control the car by holding down arrow keys. \n" +
            "Hold down spacebar to brake.\n" +
            "No need to hit keys repeatedly, just hold them down.\n\n"+
            "Place the trailer on red coloured spots to win.\n" +
            "You have limited time and the car can sustain only a few crashes.\n" +
            "To skip to a level you have already visited, just type the name of that level.\n" +
            "Random road means randomly placed obstacles.";

        JButton helpButton =   new JButton("Help");
        add(Box.createRigidArea(new Dimension(0, 50)));
        helpButton.setBackground(Color.YELLOW);
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(game, helpText);
                game.refocus();
            }
        });
        add(helpButton);
        add(Box.createRigidArea(new Dimension(0, 100)));
    }

    private void addSizeButton(final double scale, String buttonText) {
        JButton sizeButton = new JButton(buttonText);
        sizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.launchSize(scale);
            }
        });
        add(sizeButton);
    }

    private void addStartButton() {

        JButton startButton =  new JButton("(r) Restart this level");
        add(Box.createRigidArea(new Dimension(0, 50)));
        startButton.setBackground(Color.GREEN);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.relaunch();
            }
        });
        add(startButton);
    }

    private void addRandomButton() {

        JButton randomButton = new JButton("Random road");
        add(Box.createRigidArea(new Dimension(0, 50)));
        randomButton.setBackground(Color.ORANGE);
        randomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 game.launchRandom();
            }
        });
        add(randomButton);
    }

    private void addGoField() {

        final JTextField goField = new JTextField(12);
        JLabel goLabel           = new JLabel("Go directly to level:");

        add(Box.createRigidArea(new Dimension(0, 50)));
        JPanel goPanel = new JPanel();
        goPanel.setLayout(new BoxLayout(goPanel, BoxLayout.Y_AXIS));

        goPanel.add(goLabel);
        goField.setMaximumSize(new Dimension(Integer.MAX_VALUE, goField.getPreferredSize().height));
        goPanel.add(goField);
        goField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.tryToGo(goField.getText());
            }
        });
        add(goPanel);
    }
}

//    End class Buttons -------------------------------------------------------------------------------------


class Cockpit extends KeyAdapter {      // Interpreting keyboard actions into game actions

        private final int        MAX_STEER          = 40;
        private final int        STEER_INCREMENT    = 2;
        private final double     VELOCITY_INCREMENT;
        private final double     BRAKE_INCREMENT;
        private final double     MAX_VELOCITY;
        private final double     MIN_VELOCITY;

        private int              steeringWheelAngle;
        int getSteeringWheelAngle () {
            return steeringWheelAngle;
        }

        private double           velocity;
        double getVelocity () {
            return velocity;
        }
        Car mycar;

        private boolean          isTurningLeft, isTurningRight,
                                 isAcceleratingForward, isAcceleratingBackward, isBraking;

    Cockpit(Car car) {

        VELOCITY_INCREMENT = 0.2 * GameArea.scale;
        BRAKE_INCREMENT = VELOCITY_INCREMENT * 2;
        MAX_VELOCITY = 15 * GameArea.scale;
        MIN_VELOCITY = -2 * GameArea.scale;
        mycar=car;
    }

        @Override
        public void keyPressed(KeyEvent e) {
            if(!mycar.onice){
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    isTurningLeft = true;
                    break;
                case KeyEvent.VK_RIGHT:
                    isTurningRight = true;
                    break;
                case KeyEvent.VK_UP:
                    isAcceleratingForward = true;
                    break;
                case KeyEvent.VK_DOWN:
                    isAcceleratingBackward = true;
                    break;
                case KeyEvent.VK_SPACE:
                    isBraking = true;
                    break;
            }}
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if(!mycar.onice){
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    isTurningLeft = false;
                    break;
                case KeyEvent.VK_RIGHT:
                    isTurningRight = false;
                    break;
                case KeyEvent.VK_UP:
                    isAcceleratingForward = false;
                    break;
                case KeyEvent.VK_DOWN:
                    isAcceleratingBackward = false;
                    break;
                case KeyEvent.VK_SPACE:
                    isBraking = false;
                    break;
            }
        }}

        void changeState() {

            if (isTurningLeft) turnLeft();
            if (isTurningRight) turnRight();
            if (isAcceleratingForward) accelerateForward();
            if (isAcceleratingBackward) accelerateBackward();
            if (isBraking) brake();
        }

        void resetKeys() {

            isTurningLeft          =false;
            isTurningRight         =false;
            isAcceleratingForward  =false;
            isAcceleratingBackward =false;
            isBraking              =false;
            velocity               =0;
        }

        private void turnLeft() {
            if (steeringWheelAngle > -MAX_STEER) {
                steeringWheelAngle -= STEER_INCREMENT;
            }
        }

        private void turnRight() {

            if (steeringWheelAngle < MAX_STEER) {
                steeringWheelAngle += STEER_INCREMENT;
            }
        }

        private void accelerateForward() {

            if (velocity >= 0 && velocity < MAX_VELOCITY) {
                velocity += VELOCITY_INCREMENT;
            }
        }

        private void accelerateBackward() {

            if (velocity <= 0 && velocity > MIN_VELOCITY) {
                velocity -= VELOCITY_INCREMENT;
            }
        }

        private void brake() {

            if (velocity > 2 * BRAKE_INCREMENT) {
                velocity -= BRAKE_INCREMENT;
            } else if (velocity < -2 * BRAKE_INCREMENT) {
                velocity += BRAKE_INCREMENT;
            } else {
                velocity = 0;
            }
        }
    }

//    End class Cockpit -------------------------------------------------------------------------------------



class GameArea extends JPanel implements ActionListener {

    static double scale;

    static  int sizeX ;
    static  int sizeY;

    private final Timer timer;
    private final int   UPDATE_FREQUENCY = 30;

    private Car     car;
    private Trailer trailer;

    private Cockpit cockpit ;

    private Level level;

    private int levelNumber;
    private int collisionsLeft;

    private final Game game;

    private final int MAX_COLLISIONS = 5;

    GameArea(Game g, double scaleFactor, int levNumber) {

        super();

        scale   = scaleFactor;

        game  = g;
        levelNumber = levNumber;

        sizeX = (int)(1000*scale);
        sizeY = (int)(1000*scale);


        level   = LevelFactory.generateLevel(levelNumber);
        car     = level.theCar;
        trailer = level.theTrailer;

        cockpit = new Cockpit(car);
        collisionsLeft = MAX_COLLISIONS;

        setPreferredSize(new Dimension(sizeX, sizeY));

        addKeyListener(cockpit);

        addKeyListener(new KeyAdapter() {  //For debugging: type Q to go to next level!
            @Override
            public void keyTyped(KeyEvent e) {

                if (e.getKeyChar() == 'Q') game.succeed();
                if (e.getKeyChar() == 'r' || e.getKeyChar() == 'R')
                    game.relaunch();
            }
        });

        setVisible(true);
        setFocusable(true);
        requestFocusInWindow();

        timer = new Timer(1000/UPDATE_FREQUENCY, this);
        timer.start();
        repaint();
    }


    void stop() {

        timer.stop();
    }

    String getLevelName() {

        return level.name;
    }

    int getMaxTime() {

        return level.maxTime;
    }


    @Override
    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // This makes the graphics smoother
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.gray);
        g2.fillRect(0, 0, sizeX, sizeY);

        level.paint(g2);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

        cockpit.changeState();

        double velocity = cockpit.getVelocity();
        int angle = cockpit.getSteeringWheelAngle();

        try {
            car.move(velocity, angle);
        } catch (Vehicle.CollisionAlert collisionAlert) {
            collisionAlert.firstInvolved.crash();
            if (collisionAlert.secondInvolved != null) {
                collisionAlert.secondInvolved.crash();
            }
            car.restoreState();
            crash();
        }

        repaint();
        level.checkGoals(trailer);
        if (level.allGoalsAreReached()) {
            game.succeed();
        }
    }

    private void crash() {
        repaint();
        if (collisionsLeft == 1) {
            game.crashOut();
        } else {
            collisionsLeft--;
            cockpit.resetKeys();
            if (collisionsLeft == 1) {
                game.crash("CRASH! CAREFUL! NEXT CRASH YOU BREAK DOWN!");
            } else {
                game.crash("CRASH! Be careful. Your car will break down after " + collisionsLeft + " more crashes");
            }
        }
    }
}

//    End class GameArea -------------------------------------------------------------------------------------


class Obstacle {

    private final Shape shape;
    private final Area area;
    private final Color color;
    boolean slippery=false;
    Obstacle(Shape s, Color c) {

        shape = s;
        color = c;
        area = new Area(shape);
    }

    void paint(Graphics2D g, Color otherColor) {

        g.setColor(otherColor);
        g.fill(shape);
    }

    void paint(Graphics2D g) {

        g.setColor(color);
        g.fill(shape);
    }

    boolean isOn(Vehicle vehicle) {

        Area vehicleArea = new Area(vehicle.appearance);
        vehicleArea.intersect(area);
        return !vehicleArea.isEmpty();
    }

    Area getArea() {
        return area;
    }
}


class RectangleObstacle extends Obstacle {

    RectangleObstacle(double x, double y, double sx, double sy) {

        super((new Rectangle2D.Double(x * GameArea.scale, y * GameArea.scale,
                                      sx * GameArea.scale, sy * GameArea.scale)), Color.BLACK);
    }

    RectangleObstacle(double x, double y, double sx, double sy, Color c) {

        super((new Rectangle2D.Double(x * GameArea.scale, y * GameArea.scale,
                                      sx * GameArea.scale, sy * GameArea.scale)), c);
    }
}

class Ice extends Obstacle {
    
    Ice(double x, double y, double sx,double sy){
        super((new Ellipse2D.Double(x*GameArea.scale, y*GameArea.scale,sx*GameArea.scale,sy*GameArea.scale)),Color.WHITE);
        slippery=true;
    }
}

class Goal extends RectangleObstacle {

    private boolean isReached;
    private static final Color goalColor = Color.RED;
    private static final Color reachedColor = Color.GREEN;
    private final static double GOAL_SIZE = 25;

    Goal(double x, double y) {

        super(x, y, GOAL_SIZE, GOAL_SIZE, goalColor);
    }

    boolean isReached() {
        return isReached;
    }

    void reach() {
        isReached = true;
    }

    @Override
    void paint(Graphics2D g) {

        if (isReached) {
            super.paint(g, reachedColor);
        } else {
            super.paint(g);
        }
    }
}
//h�r

//    End class Obstacle and subclasses -------------------------------------------------------------------------------------


class Coord {

    double x, y;

    Coord(double x, double y) {                // A new coordinate just records x and y
        this.x = x;
        this.y = y;
    }

    double magnitude() {                        // magnitude, ie distance from origo
        return Math.sqrt(x * x + y * y);
    }

    Direction norm() {                              // norm: a normalised vector at the same direction
        return new Direction(x / magnitude(), y / magnitude());
    }

    static Coord add(Coord a, Coord b) {        // vector addition
        return new Coord(a.x + b.x, a.y + b.y);
    }

    static Coord sub(Coord a, Coord b) {        // vector subtraction
        return new Coord(a.x - b.x, a.y - b.y);
    }

    static Coord mul(double c, Coord a) {       // multiplication by a constant
        return new Coord(c * a.x, c * a.y);
    }

    static Coord middle (Coord a, Coord b) {     // midpoint
        return mul(0.5, add(a,b));
    }
}


class Direction extends Coord {     // direction as unit vector

    Direction(double x, double y) {
        super(x,y);
    }

    static Direction rotate(Direction a, double angle) {   // rotating a unit vector
        angle = angle * Math.PI /180;
        Direction rotation = new Direction(Math.cos(angle), Math.sin(angle));
        return complexMult(a, rotation);
    }

    static double angle(Direction a, Direction b) {    // angle between directions
        return 180/Math.PI*Math.acos(scal(a,b)/(a.magnitude()*b.magnitude()));
    }

    private static Direction complexMult(Direction a, Direction b){    // multiplication of complex numbers
        return new Direction(a.x*b.x - a.y*b.y, a.x*b.y + a.y*b.x);
    }

    private static double scal(Direction a, Direction b) {      // scalar product
        return a.x * b.x + a.y * b.y;
    }
}


//    End classes Coord and Direction -------------------------------------------------------------------------------------

abstract class Level {

    String              name;
    ArrayList<Obstacle> obstacles;
    ArrayList<Goal>     goals;
    Car                 theCar;
    Trailer             theTrailer;
    final int           maxTime;

    final double borderWidth            = 10;

    private final double CAR_BETWEEN_AXES;
    private final double CAR_FRONT;
    private final double CAR_REAR;
    private final double CAR_WIDTH;

    final Direction  RIGHT                  = new Direction(1, 0);
    final Direction  DOWN                   = new Direction(0,1);
    final Direction  LEFT                   = new Direction(-1,0);
    final Direction  UP                     = new Direction(0,-1);
    final Direction  DUMMY                  = new Direction(0, 0);

    private final double TRAILER_BETWEEN_AXES;
    private final double TRAILER_FRONT          = 0;
    private final double TRAILER_REAR;
    private final double TRAILER_WIDTH;
    private final Color  CAR_COLOR              = Color.BLUE;
    private final Color  TRAILER_COLOR          = Color.DARK_GRAY;

    Level() {

        maxTime = 90;

        CAR_BETWEEN_AXES    = 100*GameArea.scale;
        CAR_FRONT           = CAR_BETWEEN_AXES / 5 ;
        CAR_REAR            = CAR_BETWEEN_AXES / 4 ;
        CAR_WIDTH           = CAR_BETWEEN_AXES / 3 ;
        TRAILER_BETWEEN_AXES = 100*GameArea.scale;
        TRAILER_REAR        = TRAILER_BETWEEN_AXES / 2 ;
        TRAILER_WIDTH       = CAR_WIDTH * 1.2 ;

        obstacles = generateBorders();
        goals = new ArrayList();
    }

    private ArrayList<Obstacle> generateBorders() {

        ArrayList<Obstacle> result = new ArrayList();

        result.add(new RectangleObstacle(0, 0, borderWidth, 1000));
        result.add(new RectangleObstacle(0, 0,1000, borderWidth));
        result.add(new RectangleObstacle(0, 1000 - borderWidth, 1000, borderWidth));
        result.add(new RectangleObstacle(1000 - borderWidth, 0, borderWidth, 1000));
        return result;
    }

    Car newCar(int xCoord, int yCoord, Direction direction) {

        return new Car(Coord.mul(GameArea.scale, new Coord(xCoord, yCoord)), CAR_BETWEEN_AXES,
                       direction, CAR_FRONT, CAR_REAR, CAR_WIDTH, CAR_COLOR, this);
    }

    Trailer newTrailer(Direction direction, Vehicle towedBy) {

        Trailer trailer = new Trailer(DUMMY, TRAILER_BETWEEN_AXES, direction, TRAILER_FRONT,
                                      TRAILER_REAR, TRAILER_WIDTH, TRAILER_COLOR, this);
        towedBy.connectTow(trailer);
        return trailer;
    }
    
    boolean onIce(Vehicle vehicle){
        boolean onice=false;
                for (Obstacle obstacle : obstacles) {
            if (obstacle.isOn(vehicle)) {
                if(obstacle.slippery){
                    
                
                onice = true;}
            }
    }return onice;}

    boolean isOnObstacle(Vehicle vehicle) {

        boolean collision = false;
        for (Obstacle obstacle : obstacles) {
            if (obstacle.isOn(vehicle)) {
                if(!obstacle.slippery){
                    
                
                collision = true;}
            }
        }
        return collision;
    }

    void checkGoals(Vehicle trailer) {
        for (Goal goal: goals) {
            if (goal.isOn(trailer)) goal.reach();
        }
    }

    boolean allGoalsAreReached() {

        for (Goal goal : goals) {
            if (!goal.isReached()) {
                return false;
            }
        }
        return true;
    }
    
    void paint(Graphics2D g2) {

        for (Obstacle obstacle : obstacles) obstacle.paint(g2);
        for (Goal goal : goals) goal.paint(g2);
        theCar.paint(g2);
    }
}

class LevelFactory {

    static final int maxLevel = 14;
    static final int NO_SUCH_LEVEL = -1;

    static Level generateLevel(int levelNo) {

        switch (levelNo) {
            case 0:  return new Warmup();
            case 1:  return new Steer();
            case 2:  return new Turn();
            case 3:  return new TurnAround();
            case 4:  return new EasyPark();
            case 5:  return new GetAll();
            case 6:  return new Precision();
            case 7:  return new Straight();
            case 8:  return new Curve();
            case 9:  return new Corner();
            case 10: return new CornerOut();
            case 11: return new Tricky();
            case 12: return new Impossible();
            case 13: return new Kidding();
            case 14: return new IceTest();
            default: return new RandomRoad();
        }
    }

    static String getName(int levelNo) {
        return generateLevel(levelNo).name;
    }

    static int findLevel(String attempt) {
        for (int i = 0; i <= maxLevel; i++) {
            if (getName(i).equals(attempt)) {
                return i;
            }
        }
        return NO_SUCH_LEVEL;
    }
}


class Warmup extends Level {

    Warmup() {

        goals.add(new Goal(50, 300));
        theCar = newCar(600, 300, RIGHT);
        theTrailer = newTrailer(RIGHT, theCar);
        name = "Warming up";
    }
}

    class Steer extends Level {

        Steer() {

            goals.add(new Goal(50, 300));

            obstacles.add(new RectangleObstacle(0, 0, 200, 200));

            theCar = newCar(600, 300, Direction.rotate(RIGHT, -20));
            theTrailer = newTrailer(Direction.rotate(RIGHT, -20), theCar);
            name = "Steer it";
        }
    }

    class Turn extends Level {

        Turn() {

            goals.add(new Goal(50, 300));

            obstacles.add(new RectangleObstacle(0, 0, 200, 200));

            theCar = newCar(400, 500, DOWN);
            theTrailer = newTrailer(DOWN, theCar);
            name = "turn it";
        }
    }

    class TurnAround extends Level {

        TurnAround() {

            goals.add(new Goal(50, 300));

            obstacles.add(new RectangleObstacle(0, 0, 200, 200));
            obstacles.add(new RectangleObstacle(0, 450, 200, 200));

            theCar = newCar(200, 300, LEFT);
            theTrailer = newTrailer(Direction.rotate(LEFT, 5), theCar);
            name = "Turn around";
        }
    }

    class EasyPark extends Level {

        EasyPark() {

            goals.add(new Goal(50, 300));

            obstacles.add(new RectangleObstacle(0, 400, 300, borderWidth));
            obstacles.add(new RectangleObstacle(300 - borderWidth, 300, borderWidth, 100));

            theCar = newCar(900, 300, RIGHT);
            theTrailer = newTrailer(Direction.rotate(RIGHT, 20), theCar);
            name = "Easy parking";
        }
    }

    class GetAll extends Level {

        GetAll() {

            goals.add(new Goal(50, 300));
            goals.add(new Goal(50, 450));
            goals.add(new Goal(50, 600));

            obstacles.add(new RectangleObstacle(0, 375, 75, borderWidth));
            obstacles.add(new RectangleObstacle(0, 525, 75, borderWidth));

            theCar = newCar(400, 300, RIGHT);
            theTrailer = newTrailer(RIGHT, theCar);
            name = "Get all";
        }
    }

    class Precision extends Level {

        Precision() {

            goals.add(new Goal(10, 450));

            obstacles.add(new RectangleObstacle(0, 410, 150, borderWidth));
            obstacles.add(new RectangleObstacle(0, 515, 150, borderWidth));

            theCar = newCar(400,300, RIGHT);
            theTrailer = newTrailer(RIGHT, theCar);
            name = "Precision parking";
        }
    }

    class Straight extends Level {

        Straight() {

            goals.add(new Goal(50, 300));

            obstacles.add(new RectangleObstacle(0, 0, 500, 225));
            obstacles.add(new RectangleObstacle(0, 385, 500, 1000 - 385));

            theCar = newCar(900, 300, RIGHT);
            theTrailer = newTrailer(Direction.rotate(RIGHT, 5), theCar);
            name = "Run straight";
        }
    }

    class Curve extends Level {

        Curve() {

            goals.add(new Goal(350, 100));

            Ellipse2D.Double pond = new Ellipse2D.Double(200 * GameArea.scale,
                    200 * GameArea.scale,
                    500 * GameArea.scale,
                    GameArea.sizeY - 400 * GameArea.scale);
            obstacles.add(new Obstacle(pond, Color.CYAN));
            obstacles.add(new RectangleObstacle(450, 0, borderWidth, 200));

            theCar = newCar(900, 300, UP);
            theTrailer = newTrailer(Direction.rotate(UP, 5), theCar);
            name = "Run around";
        }
    }


    class Corner extends Level {

        Corner() {

            goals.add(new Goal(50, 300));

            obstacles.add(new RectangleObstacle(0, 0, 400, 225));
            obstacles.add(new RectangleObstacle(0, 400, 400, 1000 - 400));
            obstacles.add(new RectangleObstacle(700, 0, 1000 - 700, 1000));

            theCar = newCar(500, 300, DOWN);
            theTrailer = newTrailer(DOWN, theCar);
            name = "Corner turn";
        }
    }

    class CornerOut extends Level {

        CornerOut() {

            goals.add(new Goal(50, 300));

            obstacles.add(new RectangleObstacle(0, 0, 400, 225));
            obstacles.add(new RectangleObstacle(0, 400, 400, 1000 - 400));
            obstacles.add(new RectangleObstacle(700, 0, 1000 - 700, 1000));


            theCar = newCar(100, 300, LEFT);
            theTrailer = newTrailer(LEFT, theCar);
            name = "Out of corner";
        }
    }

    class Tricky extends Level {

        Tricky() {

            goals.add(new Goal(1000 * 7 / 8, 1000 / 8));

            obstacles.add(new RectangleObstacle(1000 / 2, 0, borderWidth, 1000 * 2 / 3));
            obstacles.add(new RectangleObstacle(1000 * 3 / 4, 1000. / 4, 1000 / 4, borderWidth));

            theCar = newCar(400, 300, RIGHT);
            theTrailer = newTrailer(RIGHT, theCar);
            name = "Tricky parking";
        }
    }

    class Impossible extends Level {

        Impossible() {

            goals.add(new Goal(1000 * 7 / 8, 1000 / 8));

            theCar = newCar(700, 300, RIGHT);
            Trailer middle = newTrailer(RIGHT, theCar);
            theTrailer = newTrailer(RIGHT, middle);
            name = "Is this possible?";
        }
    }

    class Kidding extends Level {

        Kidding() {

            goals.add(new Goal(1000 * 7 / 8, 1000 / 8));

            theCar = newCar(700, 300, RIGHT);
            Trailer middle = newTrailer(RIGHT, theCar);
            Trailer middle2 = newTrailer(RIGHT, middle);
            theTrailer = newTrailer(RIGHT, middle2);
            name = "You are kidding!";
        }
    }
class IceTest extends Level{
    
    IceTest() {
        goals.add(new Goal(1000/8,1000/6));
        
        theCar = newCar(700,300,LEFT);
        theTrailer = newTrailer(LEFT, theCar);
        name="IceTest";
        obstacles.add(new Ice(1000  / 4, 1000 / 4, 1000 / 12, 1000/12));
    }
}

    class RandomRoad extends Level {

        RandomRoad() {

            int numberOfObstacles = 8;
            final double minSize = 50 * GameArea.scale;
            final double maxSize = 200 * GameArea.scale;

            Ellipse2D.Double randomShape;


            goals.add(new Goal(950, 950));

            theCar = newCar(100, 300, DOWN);
            theTrailer = newTrailer(DOWN, theCar);

            Area carArea = new Area(new Rectangle2D.Double(0, 0, 200 * GameArea.scale, 400 * GameArea.scale));
            Area goalArea = new Area(new Rectangle2D.Double(800 * GameArea.scale, 800 * GameArea.scale,
                                                            200 * GameArea.scale, 200 * GameArea.scale));

            while (numberOfObstacles > 0) {
                randomShape = new Ellipse2D.Double(
                        (Math.random() * GameArea.sizeX),
                        (Math.random() * GameArea.sizeY),
                        (Math.random() * (maxSize - minSize)) + minSize,
                        (Math.random() * (maxSize - minSize)) + minSize);
                Area avoid = new Area();
                for (Obstacle otherObstacle : obstacles) {
                    avoid.add(otherObstacle.getArea());
                }
                avoid.add(carArea);
                avoid.add(goalArea);
                avoid.intersect(new Area(randomShape));
                if (avoid.isEmpty()) {
                    obstacles.add(new Obstacle(randomShape, Color.BLACK));
                    numberOfObstacles--;
                }
            }
            name = "Random road";
        }
    }

//    End class Level and subclasses -------------------------------------------------------------------------------------


abstract class Vehicle {

    class CollisionAlert extends Exception {

        Vehicle firstInvolved, secondInvolved;

        CollisionAlert(Vehicle secondVehicleInvolved) {
            firstInvolved = Vehicle.this;
            secondInvolved = secondVehicleInvolved;
        }
    };

    private final Level level;

    Coord frontAxisPos;      //midpoint beetween front wheels
    Coord rearAxisPos;       // midpoint between rear wheels
    Direction direction;     // unit vector vehicle orientation

    
    Coord rearPos;           // rearmost point
    Coord frontPos;          // foremost point

    Coord saveFrontAxisPos;   // to save the state
    Direction saveDirection;

    private final double lengthBetweenAxes; // distance bewtween front and rear axes
    private final double frontLength;  // distance between front axis and front of vehicle
    private final double rearLength;   // distance between rear axis and rear of vehicle
    final double width;        // width of vehicle
    private final double HOOK_SIZE;  // size of rear hook
    final double wheelWidth;
    final double halfWheelLength;

    private final Color color;
    private final Color crashColor = Color.RED;
    private boolean justHasCrashed = false;
    final Color wheelColor = Color.LIGHT_GRAY;

    private Trailer towed;
    boolean onice=false;
    Shape appearance;

    Vehicle (Coord frontAxisPos, double lengthBetweenAxes, Direction direction,
             double frontLength, double rearLength, double width, Color color, Level lev) {

        this.frontAxisPos = frontAxisPos;
        this.lengthBetweenAxes = lengthBetweenAxes;
        this.frontLength = frontLength;
        this.rearLength = rearLength;
        this.width = width;
        this.color = color;
        this.direction = direction;
        this.level = lev;

        HOOK_SIZE  = 5*GameArea.scale;
        wheelWidth = 5*GameArea.scale;
        halfWheelLength = 10*GameArea.scale;

        setPositions();
    }

    final void setPositions() {

         frontPos    = Coord.add(frontAxisPos, Coord.mul(frontLength, direction));
         rearAxisPos = Coord.sub(frontAxisPos, Coord.mul(lengthBetweenAxes, direction));
         rearPos     = Coord.sub(rearAxisPos,  Coord.mul(rearLength, direction));
         if (isTowing()) towed.frontAxisPos = rearPos;
         appearance  = appearance();
    }

    void connectTow(Trailer tow){

        towed = tow;
        towed.frontAxisPos = this.rearPos;
        towed.setPositions();
    }

    private boolean isTowing() {
        return towed != null;
    }

    void move() throws CollisionAlert {        // assumes frontAxisPos has been changed

        direction = Coord.sub(frontAxisPos, rearAxisPos).norm(); // Good enough approximation!
        setPositions();

        if (level.isOnObstacle(this) 	&& !level.onIce(this)) {
            throw new CollisionAlert(null);
        }
       this.onice=level.onIce(this);

        if (isTowing()) {
            towed.move();
            if (hasCollidedWithTrailer()) {
                throw new CollisionAlert(towed);
            }
        }
    }

    void crash() {

        justHasCrashed = true;
    }

    void saveState() {

        justHasCrashed = false;
        saveDirection = direction;
        if (isTowing()) towed.saveState();
    }

    void restoreState() {

        direction    = saveDirection;
        setPositions();
        if (isTowing()) towed.restoreState();
    }

    private boolean hasCollidedWithTrailer() {

        Area myArea  = new Area(appearance);
        Area towArea = new Area(towed.appearance);
        myArea.intersect(towArea);
        return !myArea.isEmpty();
    }

    abstract Shape appearance();

    void paint(Graphics2D g2){
        
        g2.setColor(color);
        if (justHasCrashed) g2.setColor(crashColor);
        g2.fill(appearance());

        if (isTowing()) {
            g2.fillOval((int)(rearPos.x - HOOK_SIZE), (int)(rearPos.y-HOOK_SIZE),
                        (int)(HOOK_SIZE*2), (int)(HOOK_SIZE*2));
            towed.paint(g2);
        }
        
        paintRearWheels(g2);    
    }

    Coord wheeltip(Coord center, double length, Direction direction) {

        return Coord.add(center, Coord.mul(length, direction));
    }

    private void paintRearWheels(Graphics2D g){

        final double wheelDisplacement = width - wheelWidth;
        Coord leftWheelPos = Coord.add(rearAxisPos,
                                       Coord.mul(wheelDisplacement,Direction.rotate(direction,90)));
        Coord rightWheelPos = Coord.sub(rearAxisPos,
                                        Coord.mul(wheelDisplacement,Direction.rotate(direction,90)));
        g.setColor(wheelColor);
        g.setStroke(new BasicStroke((int)wheelWidth));
        g.drawLine((int)wheeltip(leftWheelPos, halfWheelLength, direction).x,
                   (int)wheeltip(leftWheelPos, halfWheelLength, direction).y,
                   (int)wheeltip(leftWheelPos, -halfWheelLength, direction).x,
                   (int)wheeltip(leftWheelPos, -halfWheelLength, direction).y);
        g.drawLine((int)wheeltip(rightWheelPos, halfWheelLength, direction).x,
                   (int)wheeltip(rightWheelPos, halfWheelLength, direction).y,
                   (int)wheeltip(rightWheelPos, -halfWheelLength, direction).x,
                   (int)wheeltip(rightWheelPos, -halfWheelLength, direction).y);
    }
}


class Car extends Vehicle {
   
    private Direction frontWheelDirection;      // unit vector where front wheels are pointed
    
    Car (Coord frontAxisPos, double lengthBetweenAxes,  Direction direction,
         double frontLength, double rearLength, double width, Color color, Level lev) {

        super(frontAxisPos, lengthBetweenAxes, direction, frontLength, rearLength, width, color, lev);
        
        frontWheelDirection = direction;
    }

    void move(double velocity, int frontWheelAngle) throws CollisionAlert {

          saveState();
          frontWheelDirection = Direction.rotate(direction, frontWheelAngle);
          Coord frontAxisMove = Coord.mul(velocity, frontWheelDirection);
          frontAxisPos = Coord.add(frontAxisPos, frontAxisMove);
          super.move();
        }
    

    @Override
    void saveState() {

        saveFrontAxisPos = frontAxisPos;
        super.saveState();
    }

    @Override
    void restoreState() {

        frontAxisPos = saveFrontAxisPos;
        super.restoreState();
    }
    
    @Override
    Polygon appearance() {

        Coord widthOffset = Coord.mul(width, Direction.rotate(direction, 90));
        Coord frontLeft = Coord.add(frontPos, widthOffset);
        Coord frontRight = Coord.sub(frontPos, widthOffset);
        Coord rearLeft = Coord.add(rearPos, widthOffset);
        Coord rearRight = Coord.sub(rearPos, widthOffset);
        int[] xpoints = {(int) frontLeft.x, (int) rearLeft.x, (int) rearRight.x, (int) frontRight.x};
        int[] ypoints = {(int) frontLeft.y, (int) rearLeft.y, (int) rearRight.y, (int) frontRight.y};
        return new Polygon(xpoints, ypoints, xpoints.length);
    }

     @Override
     void paint(Graphics2D g2){

        super.paint(g2);
        paintFrontWheels(g2);
     }

     private void paintFrontWheels(Graphics2D g) {

        final double wheelDisplacement = width - wheelWidth;
        Coord leftWheelPos = Coord.add(frontAxisPos,
                                       Coord.mul(wheelDisplacement,Direction.rotate(direction,90)));
        Coord rightWheelPos = Coord.sub(frontAxisPos,
                                        Coord.mul(wheelDisplacement,Direction.rotate(direction,90)));

        g.setColor(wheelColor);
        g.setStroke(new BasicStroke((int)wheelWidth));
        g.drawLine((int)wheeltip(leftWheelPos, halfWheelLength, frontWheelDirection).x,
                   (int)wheeltip(leftWheelPos, halfWheelLength, frontWheelDirection).y,
                   (int)wheeltip(leftWheelPos, -halfWheelLength, frontWheelDirection).x,
                   (int)wheeltip(leftWheelPos, -halfWheelLength, frontWheelDirection).y);
        g.drawLine((int)wheeltip(rightWheelPos, halfWheelLength, frontWheelDirection).x,
                   (int)wheeltip(rightWheelPos, halfWheelLength, frontWheelDirection).y,
                   (int)wheeltip(rightWheelPos, -halfWheelLength, frontWheelDirection).x,
                   (int)wheeltip(rightWheelPos, -halfWheelLength, frontWheelDirection).y);
     }
}


class Trailer extends Vehicle {

    Trailer(Coord frontAxisPos, double lengthBetweenAxes,  Direction direction,
            double frontLength, double rearLength, double width, Color color, Level lev) {

        super(frontAxisPos, lengthBetweenAxes, direction, frontLength, rearLength, width, color, lev);
        }

    @Override
    Polygon appearance() {

        Coord widthOffset   = Coord.mul(width, Direction.rotate(direction, 90));
        Coord boomEndpoint  = Coord.middle(frontAxisPos, rearAxisPos);
        Coord frontLeft     = Coord.add(boomEndpoint, widthOffset);
        Coord frontRight    = Coord.sub(boomEndpoint, widthOffset);
        Coord boomLeft      = Coord.middle(frontLeft, boomEndpoint);
        Coord boomRight     = Coord.middle(frontRight, boomEndpoint);
        Coord rearLeft      = Coord.add(rearPos, widthOffset);
        Coord boomConnector = Coord.sub (frontAxisPos, Coord.mul(3, direction));
        Coord rearRight     = Coord.sub(rearPos, widthOffset);

        int[] xpoints = {(int) frontLeft.x, (int) rearLeft.x,      (int) rearRight.x, (int) frontRight.x,
                         (int) boomRight.x, (int) boomConnector.x, (int)boomLeft.x};
        int[] ypoints = {(int) frontLeft.y, (int) rearLeft.y,      (int) rearRight.y, (int) frontRight.y,
                         (int) boomRight.y, (int) boomConnector.y, (int)boomLeft.y};

        return new Polygon(xpoints, ypoints, xpoints.length);
    }
}

