import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.Math.*;
import java.util.Calendar;
public class labb3 {

 
    public static void main(String args[]) {
      JFrame frame = new JFrame("The Clock");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      MyPanel myPanel = new MyPanel();
      myPanel.setPreferredSize(new Dimension(1000, 600));
      myPanel.setBackground(Color.BLUE);
     
      
      frame.add(myPanel);
      frame.pack();
      frame.setVisible(true);
      
      Timer timer = new Timer(1000,myPanel);
      timer.start();
    }
}

