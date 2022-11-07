import java.awt.*;
import javax.swing.*;

public class labb3vSebastian {
  /**
   *
   * @author Sebastian Sjöberg
   */
  public static void main(String args[]) {
    JFrame frame = new JFrame("The Clock");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    ClockFacevSebastian clockfacevSebastian = new ClockFacevSebastian();
    clockfacevSebastian.setPreferredSize(new Dimension(1000, 600));
    clockfacevSebastian.setBackground(Color.BLUE);

    frame.add(clockfacevSebastian);
    frame.pack();
    frame.setVisible(true);

    Timer timer = new Timer(1000, clockfacevSebastian);
    timer.start();
  }
}
