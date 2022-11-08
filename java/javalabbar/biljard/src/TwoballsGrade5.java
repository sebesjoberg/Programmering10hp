

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Adam Sundqvist
 */
public class TwoballsGrade5 {

    final static int UPDATE_FREQUENCY = 100;    // Global constant: fps, ie times per second to simulate

    public static void main(String[] args) {

        JFrame frame = new JFrame("No collisions!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Table table = new Table();

        frame.setLayout(new BorderLayout());
        frame.add(table.restartButton, BorderLayout.EAST);
        frame.add(table, BorderLayout.CENTER);

        frame.add(table);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

/**
 * *****************************************************************************************
 * Coord
 *
 * A coordinate is a pair (x,y) of doubles. Also used to represent vectors. Here
 * are various utility methods to compute with vectors.
 *
 *
 */
class Coord {

    double x, y;

    Coord(double xCoord, double yCoord) {
        x = xCoord;
        y = yCoord;
    }

    Coord(MouseEvent event) {                   // Create a Coord from a mouse event
        x = event.getX();
        y = event.getY();
    }

    static final Coord ZERO = new Coord(0, 0);

    double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    Coord norm() {                              // norm: a normalised vector at the same direction
        return new Coord(x / magnitude(), y / magnitude());
    }

    void increase(Coord c) {
        x += c.x;
        y += c.y;
    }

    void decrease(Coord c) {
        x -= c.x;
        y -= c.y;
    }

    static double scal(Coord a, Coord b) {      // scalar product
        return a.x * b.x + a.y * b.y;
    }

    static Coord sub(Coord a, Coord b) {
        return new Coord(a.x - b.x, a.y - b.y);
    }

    static Coord mul(double k, Coord c) {       // multiplication by a constant
        return new Coord(k * c.x, k * c.y);
    }

    static double distance(Coord a, Coord b) {
        return Coord.sub(a, b).magnitude();
    }

    static void paintLine(Graphics2D graph2D, Coord a, Coord b) {  // paint line between points
        graph2D.setColor(Color.black);
        graph2D.drawLine((int) a.x, (int) a.y, (int) b.x, (int) b.y);
    }

    /*static void paintWhiteBall(Graphics2D graph2D, Coord a) {
        graph2D.setColor(Color.black);
        graph2D.fillOval((int)a.x-15, (int) a.y-15, 30, 30);
    }*/
}

/**
 * ****************************************************************************************
 * Table
 *
 * The table has some constants and instance variables relating to the graphics
 * and the balls. When simulating the balls it starts a timer which fires
 * UPDATE_FREQUENCY times per second. Each time the timer is activated one step
 * of the simulation is performed. The table reacts to events to accomplish
 * repaints and to stop or start the timer.
 *
 */
class Table extends JPanel implements MouseListener, MouseMotionListener, ActionListener {

    static final int TABLE_WIDTH = 800;
    static final int TABLE_HEIGHT = 500;
    static final int WALL_THICKNESS = 30;
    private final Color COLOR = Color.green;
    public final Color WALL_COLOR = new Color(80, 51, 0);
    static int numberOfHoles = 6;
    static Hole[] holes = new Hole[numberOfHoles];
    static int numberOfBalls = 16;
    static Ball[] balls = new Ball[numberOfBalls];
    private final Timer simulationTimer;

    public Coord initialPosition_whiteBall = new Coord(250, 280);
    public Coord initialPosition_ball1 = new Coord(570, 280);
    public Coord initialPosition_ball2 = new Coord(595, 300);
    public Coord initialPosition_ball3 = new Coord(595, 260);
    public Coord initialPosition_ball4 = new Coord(620, 320);
    public Coord initialPosition_ball5 = new Coord(620, 280);
    public Coord initialPosition_ball6 = new Coord(620, 240);
    public Coord initialPosition_ball7 = new Coord(645, 340);
    public Coord initialPosition_ball8 = new Coord(645, 300);
    public Coord initialPosition_ball9 = new Coord(645, 260);
    public Coord initialPosition_ball10 = new Coord(645, 220);
    public Coord initialPosition_ball11 = new Coord(670, 360);
    public Coord initialPosition_ball12 = new Coord(670, 320);
    public Coord initialPosition_ball13 = new Coord(670, 280);
    public Coord initialPosition_ball14 = new Coord(670, 240);
    public Coord initialPosition_ball15 = new Coord(670, 200);

    public static int scoreTeamOrange;
    public static int scoreTeamBlue;

    public static boolean playerOne = true;
    public static boolean playerTwo = false;
    public static boolean inPot;
    public static boolean blackBall_inPot;
    public static boolean whiteBall_inPot;

    JButton restartButton = new JButton("Restart");

    Table() {
        restartButton.addActionListener(this);

        setPreferredSize(new Dimension(TABLE_WIDTH + 2 * WALL_THICKNESS,
                TABLE_HEIGHT + 2 * WALL_THICKNESS));
        createInitialBalls();
        createHoles();

        scoreTeamOrange = 0;
        scoreTeamBlue = 0;
        whiteBall_inPot = false;

        addMouseListener(this);
        addMouseMotionListener(this);
        this.add(restartButton);

        simulationTimer = new Timer((int) (1000.0 / TwoballsGrade5.UPDATE_FREQUENCY), this);

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(simulationTimer)) {
            for (Ball ball : balls) {
                ball.move();
                repaint();
            }
        }
        if (e.getSource().equals(restartButton)) {
            Table newTable = new Table();
        }
        int isRunning = 0;
        for (Ball ball : balls) {
            if (!ball.isMoving()) {
                isRunning++;
            }
            if (isRunning == 16) {
                simulationTimer.stop();
                checkWinScreen();
                if (blackBall_inPot == true) {
                    gameOverScreen();
                    blackBall_inPot = false;
                }
                if (inPot == false) {
                    Table.switchTurn();

                }
                repaint();
            }
        }
    }

    public void mouseClicked(MouseEvent event) {

        Coord mousePos = new Coord(event);
        boolean correctPlacement = checkPlacement(mousePos);
        if (!simulationTimer.isRunning()) {
            if (whiteBall_inPot == true) {
                for (Ball ball : balls) {
                    if (ball.isWhiteBall == true && correctPlacement == true) {
                        ball.position = mousePos;
                        ball.inHole = false;
                        repaint();
                        whiteBall_inPot = false;
                    } else if (ball.isWhiteBall == true && correctPlacement == false) {
                        ball.position = new Coord(1000, 1000);
                    }
                }
            }
        }
    }

    boolean checkPlacement(Coord coordinate) {
        boolean correctPlacement = true;
        for (Hole hole : holes) {
            for (Ball ball : balls) {
                if (!ball.isWhiteBall) {
                    if (Coord.distance(ball.position, coordinate) < ball.DIAMETER
                            || Coord.distance(hole.holePos, coordinate) < hole.HOLE_RADIUS) {
                        correctPlacement = false;
                    }
                }
            }
        }
        return correctPlacement;
    }

    public void mousePressed(MouseEvent event) {
        if (!simulationTimer.isRunning()) {
            if (whiteBall_inPot == false) {
                Coord mousePosition = new Coord(event);
                for (Ball ball : balls) {
                    if (ball.isWhiteBall == true) {
                        ball.setAimPosition(mousePosition);
                        repaint();
                    }
                }
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (whiteBall_inPot == false) {
            for (Ball ball : balls) {
                if (ball.isWhiteBall == true) {
                    ball.shoot();
                }
            }

            if (!simulationTimer.isRunning()) {
                simulationTimer.start();
                inPot = false;
            }
        }

    }

    public void mouseDragged(MouseEvent event) {
        Coord mousePosition = new Coord(event);
        for (Ball ball : balls) {
            if (ball.isWhiteBall == true) {
                ball.updateAimPosition(mousePosition);
                repaint();
            }
        }
    }

    void checkWinScreen() {
        if (scoreTeamOrange == 7) {
            JOptionPane.showMessageDialog(this, "Team Orange wins!", "Winner!", JOptionPane.INFORMATION_MESSAGE);
        }
        if (scoreTeamBlue == 7) {
            JOptionPane.showMessageDialog(this, "Team Blue wins!", "Winner!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void gameOverScreen() {
        if (playerOne == true) {
            JOptionPane.showMessageDialog(this, "Player Two wins!", "Game over", JOptionPane.INFORMATION_MESSAGE);
        } else if (playerTwo == true) {
            JOptionPane.showMessageDialog(this, "Player One wins!", "Game over", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    void createHoles() {
        holes[0] = new Hole(new Coord(WALL_THICKNESS, WALL_THICKNESS), this);
        holes[1] = new Hole(new Coord(WALL_THICKNESS, TABLE_HEIGHT + WALL_THICKNESS), this);
        holes[2] = new Hole(new Coord((TABLE_WIDTH / 2) + WALL_THICKNESS, WALL_THICKNESS), this);
        holes[3] = new Hole(new Coord((TABLE_WIDTH / 2) + WALL_THICKNESS, TABLE_HEIGHT + WALL_THICKNESS), this);
        holes[4] = new Hole(new Coord(TABLE_WIDTH + WALL_THICKNESS, WALL_THICKNESS), this);
        holes[5] = new Hole(new Coord(TABLE_WIDTH + WALL_THICKNESS, TABLE_HEIGHT + WALL_THICKNESS), this);
    }

    void createInitialBalls() {
        int i = 0;
        while (i < numberOfBalls) {
            if (i == 0) {
                balls[i] = new Ball(initialPosition_whiteBall, i);
            } else if (i == 1) {
                balls[i] = new Ball(initialPosition_ball1, i);
            } else if (i == 2) {
                balls[i] = new Ball(initialPosition_ball2, i);
            } else if (i == 3) {
                balls[i] = new Ball(initialPosition_ball3, i);
            } else if (i == 4) {
                balls[i] = new Ball(initialPosition_ball4, i);
            } else if (i == 5) {
                balls[i] = new Ball(initialPosition_ball5, i);
            } else if (i == 6) {
                balls[i] = new Ball(initialPosition_ball6, i);
            } else if (i == 7) {
                balls[i] = new Ball(initialPosition_ball7, i);
            } else if (i == 8) {
                balls[i] = new Ball(initialPosition_ball8, i);
            } else if (i == 9) {
                balls[i] = new Ball(initialPosition_ball9, i);
            } else if (i == 10) {
                balls[i] = new Ball(initialPosition_ball10, i);
            } else if (i == 11) {
                balls[i] = new Ball(initialPosition_ball11, i);
            } else if (i == 12) {
                balls[i] = new Ball(initialPosition_ball12, i);
            } else if (i == 13) {
                balls[i] = new Ball(initialPosition_ball13, i);
            } else if (i == 14) {
                Coord initialPosition = new Coord(670, 170);
                balls[i] = new Ball(initialPosition_ball14, i);
            } else if (i == 15) {
                balls[i] = new Ball(initialPosition_ball15, i);
            }
            i++;
        }
    }

    public static void switchTurn() {
        if (playerOne == true) {
            playerOne = false;
            playerTwo = true;

        } else if (playerTwo == true) {
            playerTwo = false;
            playerOne = true;

        }
    }

    // Obligatory empty listener method
    public void mouseMoved(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2D = (Graphics2D) graphics;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // This makes the graphics smoother
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2D.setColor(WALL_COLOR);
        g2D.fillRect(0, 0, TABLE_WIDTH + 2 * WALL_THICKNESS, TABLE_HEIGHT + 2 * WALL_THICKNESS);

        g2D.setColor(COLOR);
        g2D.fillRect(WALL_THICKNESS, WALL_THICKNESS, TABLE_WIDTH, TABLE_HEIGHT);

        for (Hole hole : holes) {
            hole.paintHoles(g2D);
        }

        for (Ball ball : balls) {
            if (ball.inHole == false) {
                ball.paint(g2D);
            }
        }
        g2D.setColor(Color.BLUE);
        g2D.setFont(new Font("Courier New", 1, 17));
        g2D.drawString("Team Blue has: " + scoreTeamBlue + " points", 100, 15);
        g2D.setColor(Color.ORANGE);
        g2D.drawString("Team Orange has: " + scoreTeamOrange + " points", 500, 15);

        g2D.setColor(Color.LIGHT_GRAY);
        if (playerOne == true) {
            g2D.drawString("Player One's turn!", 100, 545);
            if (whiteBall_inPot == true) {
                g2D.drawString("Place White ball", 500, 545);
            }
        }

        if (playerTwo == true) {
            g2D.drawString("Player Two's turn!", 500, 545);
            if (whiteBall_inPot == true) {
                g2D.drawString("Place White ball", 100, 545);
            }
        }
    }
}// end class Table

/**
 * ****************************************************************************************
 * Ball:
 *
 * The ball has instance variables relating to its graphics and game state:
 * position, velocity, and the position from which a shot is aimed (if any).
 *
 */
class Ball {

    public final Color WHITE = Color.white;
    public final int BORDER_THICKNESS = 2;
    public final double RADIUS = 15;
    public final double DIAMETER = 2 * RADIUS;
    public final double FRICTION = 0.01;                          // its friction constant (normed for 100 updates/second)
    public final double FRICTION_PER_UPDATE
            = // friction applied each simulation step
            1.0 - Math.pow(1.0 - FRICTION, // don't ask - I no longer remember how I got to this
                    100.0 / TwoballsGrade5.UPDATE_FREQUENCY);
    public Coord position;
    public Coord velocity;
    public Coord aimPosition;
    public Coord whiteBallPosition;

    public boolean ballbounce;
    public boolean wallbounce_y;
    public boolean wallbounce_x;
    public boolean inHole;

    public boolean isWhiteBall = false;
    public boolean isTeamOrange;
    public boolean isTeamBlue;
    public boolean isBlackBall;
    Table myTable;

    Ball(Coord initialPosition, int i) {
        position = initialPosition;
        velocity = new Coord(0, 0);
        if (i == 0) {
            isWhiteBall = true;
        }
        if (i == 5) {
            isBlackBall = true;
        }
        if (i == 1 || i == 6 || i == 8 || i == 9 || i == 11 || i == 13 || i == 15) {
            isTeamOrange = true;
        }
        if (i == 2 || i == 3 || i == 4 || i == 7 || i == 10 || i == 12 || i == 14) {
            isTeamBlue = true;
        }
    }

    public boolean isAiming() {
        return aimPosition != null;
    }

    boolean isMoving() {    // if moving too slow I am deemed to have stopped
        return velocity.magnitude() > FRICTION_PER_UPDATE;
    }

    void setWhiteBallPosition(Coord gPos) {
        whiteBallPosition = gPos;
    }

    void updateWhiteBallPosition(Coord newPosition) {
        whiteBallPosition = newPosition;
    }

    void setAimPosition(Coord grabPosition) {
        if (Coord.distance(position, grabPosition) <= RADIUS) {
            aimPosition = grabPosition;
        }
    }

    void updateAimPosition(Coord newPosition) {
        if (isAiming()) {
            aimPosition = newPosition;
        }
    }

    void shoot() {
        if (isAiming()) {
            Coord aimingVector = Coord.sub(position, aimPosition);
            velocity = Coord.mul(Math.sqrt(10.0 * aimingVector.magnitude() / TwoballsGrade5.UPDATE_FREQUENCY),
                    aimingVector.norm());  // don't ask - determined by experimentation
            aimPosition = null;
        }
    }

    void checkWallBounce() {
        if (position.x - RADIUS < Table.WALL_THICKNESS && velocity.x < 0
                || position.x + RADIUS > Table.WALL_THICKNESS + Table.TABLE_WIDTH && velocity.x > 0) {
            wallbounce_x = true;
        }
        if (position.y - RADIUS < Table.WALL_THICKNESS && velocity.y < 0
                || position.y + RADIUS > Table.WALL_THICKNESS + Table.TABLE_HEIGHT && velocity.y > 0) {
            wallbounce_y = true;
        }
    }

    void checkBallBounce(Ball otherBall) {
        double distance = Coord.distance(this.position, otherBall.position);
        if (distance < RADIUS * 2 + BORDER_THICKNESS && willCollide(otherBall) == true && inHole == false) {
            ballbounce = true;
        }
    }

    Boolean willCollide(Ball otherBall) {
        double x_coord_other = otherBall.position.x + otherBall.velocity.x;
        double y_coord_other = otherBall.position.y + otherBall.velocity.y;

        double x_coord_this = this.position.x + this.velocity.x;
        double y_coord_this = this.position.y + this.velocity.y;

        Coord nextOther = new Coord(x_coord_other, y_coord_other);
        
        Coord nextThis = new Coord(x_coord_this, y_coord_this);

        return Coord.distance(nextOther, nextThis) < Coord.distance(this.position, otherBall.position);
    }

    void resultOfBallBounce(Ball otherBall) {
        double xA = this.position.x;
        double xB = otherBall.position.x;
        double yA = this.position.y;
        double yB = otherBall.position.y;
        double PxA = this.velocity.x;
        double PxB = otherBall.velocity.x;
        double PyA = this.velocity.y;
        double PyB = otherBall.velocity.y;

        double dX = (xB - xA) / Math.sqrt(((xB - xA) * (xB - xA)) + ((yB - yA) * (yB - yA)));
        double dY = (yB - yA) / Math.sqrt(((xB - xA) * (xB - xA)) + ((yB - yA) * (yB - yA)));

        double impulseSizeJ = ((PxA * dX + PyA * dY) - (PxB * dX + PyB * dY));

        this.velocity.x = PxA - (impulseSizeJ * dX);
        this.velocity.y = PyA - (impulseSizeJ * dY);

        otherBall.velocity.x = PxB + (impulseSizeJ * dX);
        otherBall.velocity.y = PyB + (impulseSizeJ * dY);

        this.position.increase(this.velocity);
        this.velocity.decrease(Coord.mul(FRICTION_PER_UPDATE, this.velocity.norm()));

        otherBall.position.increase(otherBall.velocity);
        otherBall.velocity.decrease(Coord.mul(FRICTION_PER_UPDATE, otherBall.velocity.norm()));
    }

    public void goesInCheck() {

        for (Hole hole : Table.holes) {
            if (Coord.distance(this.position, hole.holePos) < Hole.HOLE_RADIUS) {

                if (isWhiteBall == true) {
                    this.position = new Coord(1000, 800);
                    inHole = true;
                    velocity = new Coord(0, 0);
                    Table.whiteBall_inPot = true;

                } else if (isBlackBall == true) {
                    Table.blackBall_inPot = true;
                    inHole = true;
                    velocity = new Coord(0, 0);
                } else {
                    this.inHole = true;
                    Table.inPot = true;
                    this.position = new Coord(1000, 1000);
                    this.velocity = new Coord(0, 0);
                    this.scoreCounter();
                }
            }
        }
    }

    public void scoreCounter() {
            if (this.isTeamOrange == true) {
                Table.scoreTeamOrange++;
            }
            if (this.isTeamBlue == true) {
                Table.scoreTeamBlue++;
        }
    }

    void move() {
        if (isMoving()) {
            checkWallBounce();
            for (Ball ball : Table.balls) {
                ball.goesInCheck();

                if (this != ball) {
                    checkBallBounce(ball);
                    if (ballbounce == true) {
                        resultOfBallBounce(ball);
                    }
                    ballbounce = false;
                }
            }

            if (wallbounce_x == true) {
                velocity.x = velocity.x * -1;
                wallbounce_x = false;
            }
            if (wallbounce_y == true) {
                velocity.y = velocity.y * -1;
                wallbounce_y = false;
            }
            if (wallbounce_x && wallbounce_y == true) {
                velocity.x = velocity.x * -1;
                velocity.y = velocity.y * -1;
                wallbounce_x = false;
                wallbounce_y = false;
            }
            position.increase(velocity);
            velocity.decrease(Coord.mul(FRICTION_PER_UPDATE, velocity.norm()));
        }
    }

    // paint: to draw the ball, first draw a black ball
    // and then a smaller ball of proper color inside
    // this gives a nice thick border
    void paint(Graphics2D g2D) {
        g2D.setColor(Color.black);
        g2D.fillOval(
                (int) (position.x - RADIUS + 0.5),
                (int) (position.y - RADIUS + 0.5),
                (int) DIAMETER,
                (int) DIAMETER);
        if (this.isTeamOrange) {
            g2D.setColor(Color.ORANGE);
        }
        if (this.isTeamBlue) {
            g2D.setColor(Color.cyan);
        }
        if (this.isWhiteBall) {
            g2D.setColor(WHITE);
        }
        if (this.isBlackBall) {
            g2D.setColor(Color.BLACK);
        }
        g2D.fillOval(
                (int) (position.x - RADIUS + 0.5 + BORDER_THICKNESS),
                (int) (position.y - RADIUS + 0.5 + BORDER_THICKNESS),
                (int) (DIAMETER - 2 * BORDER_THICKNESS),
                (int) (DIAMETER - 2 * BORDER_THICKNESS));
        if (isAiming()) {
            paintAimingLine(g2D);
        }
    }

    public void paintAimingLine(Graphics2D graph2D) {
        Coord.paintLine(
                graph2D,
                aimPosition,
                Coord.sub(Coord.mul(2, position), aimPosition)
        );
    }

    /*public void paintWhiteBall(Graphics2D graph2D) {
        Coord.paintWhiteBall(graph2D, whiteBallPosition);
    }*/
} // end  class Ball

class Hole {

    public Coord holePos;
    Table myTable;
    public static final int HOLE_DIAMETER = 50;
    public static int HOLE_RADIUS = HOLE_DIAMETER / 2;
    public Color HOLE_COLOR = new Color(51, 0, 0);

    Hole(Coord holePosition, Table table) {
        holePos = holePosition;
        myTable = table;
    }

    void paintHoles(Graphics2D g2D) {
        g2D.setColor(HOLE_COLOR);

        g2D.fillOval((int) (holePos.x) - HOLE_RADIUS,
                (int) (holePos.y) - HOLE_RADIUS,
                HOLE_DIAMETER, HOLE_DIAMETER);
    }

}
