import java.awt.*;
import javax.swing.*;

public class labb3v1 {

  public static void main(String args[]) {
    JFrame frame = new JFrame("The Clock");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    MyPanelv1 myPanelv1 = new MyPanelv1();
    myPanelv1.setPreferredSize(new Dimension(1000, 600));
    myPanelv1.setBackground(Color.BLUE);

    frame.add(myPanelv1);
    frame.pack();
    frame.setVisible(true);

    Timer timer = new Timer(1000, myPanelv1);
    timer.start();
  }
}
