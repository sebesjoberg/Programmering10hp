import java.awt.Color;
import java.awt.Graphics2D;

class Pocket {

    final Color COLOR = Color.WHITE;
    Coord POSITION;
    int RADIUS = 20;
    int DIAMETER = RADIUS * 2;

    int BORDER_THICKNESS = 2;

    Pocket(Coord position) {
        POSITION = position;

    }

    void paint(Graphics2D g2D) {
        g2D.setColor(COLOR);

        g2D.fillOval(
                (int) (POSITION.x - RADIUS + 0.5),
                (int) (POSITION.y - RADIUS + 0.5),
                (int) DIAMETER,
                (int) DIAMETER);
        g2D.setColor(COLOR);
        g2D.fillOval(
                (int) (POSITION.x - RADIUS + 0.5 + BORDER_THICKNESS),
                (int) (POSITION.y - RADIUS + 0.5 + BORDER_THICKNESS),
                (int) (DIAMETER - 2 * BORDER_THICKNESS),
                (int) (DIAMETER - 2 * BORDER_THICKNESS));

    }
}

