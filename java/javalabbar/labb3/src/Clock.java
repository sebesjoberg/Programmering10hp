import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Clock {
    public Clock()
    {
        MyPanel myPanel = new MyPanel();
        myPanel.setPreferredSize(new Dimension(1000, 600));
        myPanel.setBackground(Color.BLUE);
        JFrame frame = new JFrame("The Clock");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(myPanel);
        frame.pack();
        frame.setVisible(true);

    }



    }
