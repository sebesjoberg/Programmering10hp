import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * *****************************************************************************************
 * Coord
 * <p>
 * A coordinate is a pair (x,y) of doubles. Also used to represent vectors. Here
 * are various utility methods to compute with vectors.
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
}
