import java.awt.*;

/**
 * ****************************************************************************************
 * Ball:
 * <p>
 * The ball has instance variables relating to its graphics and game state:
 * position, velocity, and the position from which a shot is aimed (if any).
 */
class Ball {

    boolean isCueball = false;
    boolean isEightBall = false;
    boolean isPink = false;
    boolean isBlue = false;
    Coord initialvelocity = new Coord(0, 0);

    boolean sunk;
    private Color COLOR;
    private final int BORDER_THICKNESS = 2;
    final double RADIUS = 15;
    private final double DIAMETER = 2 * RADIUS;
    private final double FRICTION = 0.015;                          // its friction constant (normed for 100 updates/second)
    private final double FRICTION_PER_UPDATE
            = // friction applied each simulation step
            1.0 - Math.pow(1.0 - FRICTION, // don't ask - I no longer remember how I got to this
                    100.0 / Twoballs.UPDATE_FREQUENCY);
    Coord position;
    Coord velocity;
    private Coord aimPosition;              // if aiming for a shot, ow null
    Coord increasedPosition;
    private final Color CUEBALL_COLOR = Color.WHITE;
    private final Color PINK_COLOR = Color.PINK;
    private final Color EIGHTBALL_COLOR = Color.BLACK;
    private final Color BLUE_COLOR = Color.BLUE;

    Ball(Coord initialPosition, boolean cueball, boolean pink, boolean eightball, boolean blue) {
        if (cueball == true) {
            COLOR = CUEBALL_COLOR;
            isCueball = true;
        }
        if (pink == true) {
            COLOR = PINK_COLOR;
            isPink = true;
        }
        if (eightball == true) {
            COLOR = EIGHTBALL_COLOR;
            isEightBall = true;
        }
        if (blue == true) {
            COLOR = BLUE_COLOR;
            isBlue = true;
        }
        position = initialPosition;
        velocity = initialvelocity;      // WARNING! Are initial velocities
    }                                // clones or aliases? Is this important?

    private boolean isAiming() {
        return aimPosition != null;
    }

    boolean isMoving() {    // if moving too slow I am deemed to have stopped
        if (velocity.magnitude() < FRICTION_PER_UPDATE) {
            velocity = initialvelocity;
        }
        return velocity.magnitude() > FRICTION_PER_UPDATE;
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
            velocity = Coord.mul(Math.sqrt(10.0 * aimingVector.magnitude() / Twoballs.UPDATE_FREQUENCY),
                    aimingVector.norm());  // don't ask - determined by experimentation
            aimPosition = null;
        }
    }

    void move() {
        if (isMoving()) {
            position.increase(velocity);
            velocity.decrease(Coord.mul(FRICTION_PER_UPDATE, velocity.norm()));
        }

    }

    void sunk(Pocket pocket) {

        this.sunk = false;
        if (Coord.distance(position, pocket.POSITION) <= pocket.RADIUS) {

            increasedPosition = new Coord(position.x + velocity.x, position.y + velocity.y);
            if (Coord.distance(increasedPosition, pocket.POSITION) < Coord.distance(position, pocket.POSITION)) {
                this.sunk = true;
            }

        }
    }

    void kantstuds(int wallx1, int wallx2, int wally1, int wally2) {

        if (position.x <= wallx1 + this.RADIUS) {
            double x = position.x + velocity.x;
            if (x < position.x) {

                velocity.x = -velocity.x;
            }
        }
        if (position.x >= wallx2 - this.RADIUS) {
            double x = position.x + velocity.x;
            if (x > position.x) {
                velocity.x = -velocity.x;
            }
        }

        if (position.y <= wally1 + this.RADIUS) {
            double y = position.y + velocity.y;
            if (y < position.y) {
                velocity.y = -velocity.y;

            }
        }
        if (position.y >= wally2 - this.RADIUS) {
            double y = position.y + velocity.y;
            if (y > position.y) {
                velocity.y = -velocity.y;
            }
        }

    }

    boolean bollstuds(Ball ball2) {
        boolean studschecker = false;
        if (this.position.distance(this.position, ball2.position) <= this.RADIUS + ball2.RADIUS) {
            double x1 = this.position.x + this.velocity.x;
            double y1 = this.position.y + this.velocity.x;
            Coord cord1 = new Coord(x1, y1);
            double x2 = ball2.position.x + ball2.velocity.x;
            double y2 = ball2.position.y + ball2.velocity.y;
            Coord cord2 = new Coord(x2, y2);
            if (this.position.distance(cord1, cord2) < this.position.distance(this.position, ball2.position)) {
                studschecker = true;

            }
        }
        return studschecker;
    }

    void studs(Ball ball2) {
        double pax = this.velocity.x;
        double pay = this.velocity.y;
        double pbx = ball2.velocity.x;
        double pby = ball2.velocity.y;
        double xa = this.position.x;
        double ya = this.position.y;
        double xb = ball2.position.x;
        double yb = ball2.position.y;
        double dx = (xa - xb) / Math.sqrt((xa - xb) * (xa - xb) + (ya - yb) * (ya - yb));
        double dy = (ya - yb) / Math.sqrt((xa - xb) * (xa - xb) + (ya - yb) * (ya - yb));
        double j = pbx * dx + pby * dy - (pax * dx + pay * dy);
        this.velocity.x = this.velocity.x + j * dx;
        this.velocity.y = this.velocity.y + j * dy;
        ball2.velocity.x = ball2.velocity.x - j * dx;
        ball2.velocity.y = ball2.velocity.y - j * dy;

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
        g2D.setColor(COLOR);
        g2D.fillOval(
                (int) (position.x - RADIUS + 0.5 + BORDER_THICKNESS),
                (int) (position.y - RADIUS + 0.5 + BORDER_THICKNESS),
                (int) (DIAMETER - 2 * BORDER_THICKNESS),
                (int) (DIAMETER - 2 * BORDER_THICKNESS));
        if (isAiming()) {
            paintAimingLine(g2D);
        }
    }

    private void paintAimingLine(Graphics2D graph2D) {
        Coord.paintLine(
                graph2D,
                aimPosition,
                Coord.sub(Coord.mul(2, position), aimPosition)
        );
    }
} // end  class Ball
