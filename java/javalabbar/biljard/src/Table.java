import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ****************************************************************************************
 * Table
 * <p>
 * The table has some constants and instance variables relating to the graphics
 * and the balls. When simulating the balls it starts a timer which fires
 * UPDATE_FREQUENCY times per second. Each time the timer is activated one step
 * of the simulation is performed. The table reacts to events to accomplish
 * repaints and to stop or start the timer.
 */
class Table extends JPanel implements MouseListener, MouseMotionListener, ActionListener {


    final int TABLE_WIDTH = 1000;
    final int TABLE_HEIGHT = 500;
    final int WALL_THICKNESS = 150;
    private final Color COLOR = Color.green;
    private final Color WALL_COLOR = Color.black;

    boolean stop;
    boolean SIMULATION_RUNNING = false;
    int NUMBER_OF_POCKETS = 6;
    Pocket[] pockets;
    private final Timer simulationTimer;
    int NUMBER_OF_BALLS = 16;
    int ITERATION_START = 0;
    private Ball[] balls = new Ball[NUMBER_OF_BALLS];
    int WALLX1 = WALL_THICKNESS;
    int WALLX2 = TABLE_WIDTH + WALL_THICKNESS;
    int WALLY1 = WALL_THICKNESS;
    int WALLY2 = TABLE_HEIGHT + WALL_THICKNESS;
    Pocket pocket;
    Player player1, player2;
    int winnerFontSize = 20;
    int winnerXpos = 800;
    int winnerYpos = 200;

    int POCKET_RADIUS = 20;
    int xpos1 = 100;
    int xpos2 = 1200;
    int ypos1 = 25;
    int ypos2 = 25;
    String player1name = "player1";
    String player2name = "player2";
    String player1wins = player1name + " har vunnit";
    String player2wins = player2name + " har vunnit";
    boolean PLACERING_AV_VIT_BOLL = false;
    boolean someOneWon = false;
    boolean sunkOnePlayer1 = false;
    boolean sunkOnePlayer2 = false;
    boolean CUEBALL_SUNK = false;

    Table() {

        setPreferredSize(new Dimension(TABLE_WIDTH + 2 * WALL_THICKNESS,
                TABLE_HEIGHT + 2 * WALL_THICKNESS));
        createInitialBalls();
        createPockets();
        createPlayers();
        addMouseListener(this);
        addMouseMotionListener(this);

        simulationTimer = new Timer((int) (1000.0 / Twoballs.UPDATE_FREQUENCY), this);
    }

    private void createPlayers() {
        int playernumber = 1;
        boolean myturn = false;
        player1 = new Player(playernumber, myturn, xpos1, ypos1, player1name);
        playernumber = 2;
        myturn = true;
        player2 = new Player(playernumber, myturn, xpos2, ypos2, player2name);
    }

    private void createPockets() {
        pockets = new Pocket[NUMBER_OF_POCKETS];
        Coord position = new Coord(WALL_THICKNESS + TABLE_WIDTH / 2, TABLE_HEIGHT + WALL_THICKNESS - POCKET_RADIUS);
        pocket = new Pocket(position);
        pockets[0] = pocket;
        position = new Coord(WALL_THICKNESS + TABLE_WIDTH / 2, WALL_THICKNESS + POCKET_RADIUS);
        pocket = new Pocket(position);
        pockets[1] = pocket;
        position = new Coord(WALL_THICKNESS + POCKET_RADIUS, WALL_THICKNESS + POCKET_RADIUS);
        pocket = new Pocket(position);
        pockets[2] = pocket;
        position = new Coord(WALL_THICKNESS + TABLE_WIDTH - POCKET_RADIUS, WALL_THICKNESS + POCKET_RADIUS);
        pocket = new Pocket(position);
        pockets[3] = pocket;
        position = new Coord(WALL_THICKNESS + POCKET_RADIUS, WALL_THICKNESS + TABLE_HEIGHT - POCKET_RADIUS);
        pocket = new Pocket(position);
        pockets[4] = pocket;
        position = new Coord(WALL_THICKNESS + TABLE_WIDTH - POCKET_RADIUS, WALL_THICKNESS + TABLE_HEIGHT - POCKET_RADIUS);
        pocket = new Pocket(position);
        pockets[5] = pocket;
    }

    Coord[] getPosition() {
        Coord[] positions = new Coord[16];
        positions[0] = new Coord(1000, 400);
        positions[1] = new Coord(550, 400);
        positions[2] = new Coord(520, 385);
        positions[3] = new Coord(490, 430);
        positions[4] = new Coord(460, 355);
        positions[5] = new Coord(460, 415);
        positions[6] = new Coord(430, 340);
        positions[7] = new Coord(430, 370);
        positions[8] = new Coord(490, 400);
        positions[9] = new Coord(520, 415);
        positions[10] = new Coord(490, 370);
        positions[11] = new Coord(460, 445);
        positions[12] = new Coord(460, 385);
        positions[13] = new Coord(430, 460);
        positions[14] = new Coord(430, 400);
        positions[15] = new Coord(430, 430);
        return positions;
    }

    private void createInitialBalls() {

        Coord[] positions = getPosition();

        boolean cueball = true;
        boolean pink = false;
        boolean eightball = false;
        boolean blue = false;
        Ball ball = new Ball(positions[0], cueball, pink, eightball, blue);
        balls[0] = ball;
        cueball = false;
        pink = true;
        for (int i = 1; i < 8; i = i + 1) {
            ball = new Ball(positions[i], cueball, pink, eightball, blue);
            balls[i] = ball;
        }
        pink = false;
        eightball = true;
        ball = new Ball(positions[8], cueball, pink, eightball, blue);
        balls[8] = ball;
        eightball = false;
        blue = true;
        for (int i = 9; i < 16; i = i + 1) {
            ball = new Ball(positions[i], cueball, pink, eightball, blue);
            balls[i] = ball;
        }

    }

    void ballremover(int index) {

        for (int i = ITERATION_START, k = 0; i < NUMBER_OF_BALLS; i = i + 1) {
            if (i != index) {
                balls[k] = balls[i];
                k = k + 1;
            }
        }

    }

    public void actionPerformed(ActionEvent e) {

        for (int i = ITERATION_START; i < NUMBER_OF_BALLS; i = i + 1) {
            Ball ball = balls[i];

            ball.move();
            ball.kantstuds(WALLX1, WALLX2, WALLY1, WALLY2);
            for (int k = 0; k < NUMBER_OF_POCKETS; k = k + 1) {

                pocket = pockets[k];
                ball.sunk(pocket);

                if (ball.sunk == true) {
                    if (ball.isCueball == false && ball.isEightBall == false) {
                        if (player1.MYTURN == true && player1.gotColor == false) {
                            if (ball.isPink == true) {
                                player1.pink = true;
                                player1.gotColor = true;
                                player2.blue = true;
                                player2.gotColor = true;
                            }
                            if (ball.isBlue == true) {
                                player2.pink = true;
                                player1.gotColor = true;
                                player1.blue = true;
                                player2.gotColor = true;
                            }
                        }
                        if (player2.MYTURN == true && player2.gotColor == false) {
                            if (ball.isPink == true) {
                                player2.pink = true;
                                player1.gotColor = true;
                                player1.blue = true;
                                player2.gotColor = true;
                            }
                            if (ball.isBlue == true) {
                                player1.pink = true;
                                player1.gotColor = true;
                                player2.blue = true;
                                player2.gotColor = true;
                            }
                        }
                        if (ball.isPink == true && player1.pink == true) {
                            player1.sunk = player1.sunk + 1;
                            sunkOnePlayer1 = true;
                        }
                        if (ball.isPink == true && player2.pink == true) {
                            player2.sunk = player2.sunk + 1;
                            sunkOnePlayer2 = true;
                        }
                        if (ball.isBlue == true && player1.blue == true) {
                            player1.sunk = player1.sunk + 1;
                            sunkOnePlayer1 = true;
                        }
                        if (ball.isBlue == true && player2.blue == true) {
                            player2.sunk = player2.sunk + 1;
                            sunkOnePlayer2 = true;
                        }
                        ballremover(i);
                        i = i - 1;
                        NUMBER_OF_BALLS = NUMBER_OF_BALLS - 1;
                    }
                    if (ball.isCueball == true) {
                        ITERATION_START = 1;
                        CUEBALL_SUNK = true;

                    }
                    if (ball.isEightBall == true) {
                        ballremover(i);
                        i = i - 1;
                        NUMBER_OF_BALLS = NUMBER_OF_BALLS - 1;
                        if (player1.MYTURN == true) {
                            if (player1.sunk == 7) {
                                player1.won = true;
                            }
                            if (player1.sunk != 7) {
                                player2.won = true;
                            }
                        }
                        if (player2.MYTURN == true) {
                            if (player2.sunk == 7) {
                                player2.won = true;
                            }
                            if (player2.sunk != 7) {
                                player1.won = true;
                            }
                        }

                    }
                }

            }

        }

        for (int i = ITERATION_START; i < NUMBER_OF_BALLS; i = i + 1) {
            Ball ball = balls[i];
            for (int n = ITERATION_START; n < NUMBER_OF_BALLS; n = n + 1) {
                if (n != i) {
                    Ball otherball = balls[n];

                    if (ball.bollstuds(otherball) == true) {
                        ball.studs(otherball);

                    }
                }
            }
        }
        stop = true;
        for (int i = ITERATION_START; i < NUMBER_OF_BALLS; i = i + 1) {
            Ball ball = balls[i];
            if (ball.isMoving()) {
                stop = false;
            }
        }
        if (stop == true) {
            simulationTimer.stop();
            SIMULATION_RUNNING = false;
            if (PLACERING_AV_VIT_BOLL == false) {

                if (player1.MYTURN == true) {
                    if (sunkOnePlayer1 == false || CUEBALL_SUNK == true) {
                        player1.MYTURN = false;
                        player2.MYTURN = true;
                    }
                } else {
                    if (sunkOnePlayer2 == false || CUEBALL_SUNK == true) {
                        player1.MYTURN = true;
                        player2.MYTURN = false;
                    }
                }
                sunkOnePlayer1 = false;
                sunkOnePlayer2 = false;
                CUEBALL_SUNK = false;
            }
        } else {
            PLACERING_AV_VIT_BOLL = false;
        }

        repaint();
    }

    public void mousePressed(MouseEvent event) {
        if (SIMULATION_RUNNING == false) {
            Coord mousePosition = new Coord(event);

            balls[0].setAimPosition(mousePosition);

            repaint();                                          //  To show aiming line
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (SIMULATION_RUNNING == false) {

            balls[0].shoot();

            if (balls[0].velocity.x != 0 || balls[0].velocity.y != 0) {

                if (!simulationTimer.isRunning()) {
                    simulationTimer.start();
                    SIMULATION_RUNNING = true;
                }
            }
        }
    }

    public void mouseDragged(MouseEvent event) {
        if (SIMULATION_RUNNING == false) {
            Coord mousePosition = new Coord(event);
            balls[0].updateAimPosition(mousePosition);

            repaint();
        }
    }

    // Obligatory empty listener methods
    public void mouseClicked(MouseEvent e) {
        if (ITERATION_START == 1) {
            Coord mousePosition = new Coord(e);
            boolean okayPos = true;
            for (int i = ITERATION_START; i < NUMBER_OF_BALLS; i = i + 1) {
                if (Coord.distance(mousePosition, balls[i].position) < 2 * balls[i].RADIUS) {
                    okayPos = false;
                }

            }
            for (int n = 0; n < 6; n = n + 1) {
                if (Coord.distance(mousePosition, pockets[n].POSITION) < pockets[n].RADIUS + balls[n].RADIUS) {
                    okayPos = false;
                }
            }
            if (okayPos == true) {
                okayPos = false;
                if (mousePosition.x + balls[0].RADIUS > WALLX1
                        && mousePosition.x + balls[0].RADIUS < WALLX2
                        && mousePosition.y + balls[0].RADIUS > WALLY1
                        && mousePosition.y + balls[0].RADIUS < WALLY2) {


                    boolean cueball = true;
                    boolean pink = false;
                    boolean eightball = false;
                    boolean blue = false;
                    balls[0] = new Ball(mousePosition, cueball, pink, eightball, blue);
                    ITERATION_START = 0;

                }
            }
        }
        PLACERING_AV_VIT_BOLL = true;
        repaint();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void paintComponent(Graphics graphics) {
        if (someOneWon == false) {
            super.paintComponent(graphics);
            Graphics2D g2D = (Graphics2D) graphics;
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // This makes the graphics smoother
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2D.setColor(WALL_COLOR);
            g2D.fillRect(0, 0, TABLE_WIDTH + 2 * WALL_THICKNESS, TABLE_HEIGHT + 2 * WALL_THICKNESS);

            g2D.setColor(COLOR);
            g2D.fillRect(WALL_THICKNESS, WALL_THICKNESS, TABLE_WIDTH, TABLE_HEIGHT);

            for (int i = ITERATION_START; i < NUMBER_OF_BALLS; i = i + 1) {

                Ball ball = balls[i];

                ball.paint(g2D);

            }

            for (int i = 0; i < NUMBER_OF_POCKETS; i = i + 1) {
                pocket = pockets[i];
                pocket.paint(g2D);
            }
            player1.paint(g2D);
            player2.paint(g2D);

            if (player1.won == true) {
                g2D.setFont(new Font("TimesRoman", Font.PLAIN, winnerFontSize));
                g2D.setColor(Color.BLACK);
                g2D.drawString(player1wins, winnerXpos, winnerYpos);
                someOneWon = true;
            }
            if (player2.won == true) {
                g2D.setFont(new Font("TimesRoman", Font.PLAIN, winnerFontSize));
                g2D.setColor(Color.BLACK);
                g2D.drawString(player2wins, winnerXpos, winnerYpos);
                someOneWon = true;
            }
        }
    }
}  // end class Table
