import java.awt.*;

class Player {

    final int NUMBER;
    boolean pink = false;
    boolean blue = false;
    boolean won = false;
    boolean MYTURN;
    int sunk = 0;
    int BORDER_THICKNESS = 2;
    Color COLOR;
    int HEIGHT = 80;
    int WIDTH = 150;
    Coord position;
    Color STRING_COLOR = Color.RED;
    String playerName;
    String PINK = "pink";
    String BLUE = "blue";
    boolean gotColor = false;
    String SUNK;

    Player(int number, boolean start, int xpos, int ypos, String playername) {
        NUMBER = number;
        MYTURN = start;
        position = new Coord(xpos, ypos);
        playerName = playername;
    }

    void paint(Graphics2D g2D) {
        if (MYTURN == true) {
            COLOR = Color.YELLOW;
        }
        if (MYTURN == false) {
            COLOR = Color.gray;
        }
        g2D.setColor(Color.BLACK);
        g2D.fillRect((int) (position.x + 0.5 - WIDTH / 2), (int) (position.y + 0.5 - HEIGHT / 2), (int) (WIDTH / 2), (int) (HEIGHT / 2));
        g2D.setColor(COLOR);
        g2D.fillRect((int) (position.x + 0.5 + BORDER_THICKNESS - WIDTH / 2), (int) (position.y + 0.5 + BORDER_THICKNESS - HEIGHT / 2), (int) (WIDTH - 2 * BORDER_THICKNESS), (int) (HEIGHT - 2 * BORDER_THICKNESS));
        g2D.setColor(STRING_COLOR);
        g2D.drawString(playerName, (int) (position.x), (int) (position.y));
        if (gotColor == true) {
            if (pink == true) {
                g2D.setColor(Color.PINK);
                g2D.drawString(PINK, (int) (position.x), (int) (position.y + 20));
                SUNK = Integer.toString(sunk);
                g2D.drawString(SUNK, (int) (position.x + 40), (int) (position.y + 20));
            }
            if (blue == true) {
                g2D.setColor(Color.BLUE);
                g2D.drawString(BLUE, (int) (position.x), (int) (position.y + 20));
                SUNK = Integer.toString(sunk);
                g2D.drawString(SUNK, (int) (position.x + 40), (int) (position.y + 20));
            }
        }
    }
}
