import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
/**
 *
 * @author Sebastian Sjöberg
 */
public class MyPanelv1 extends JPanel implements ActionListener {
    final Color HAND_COLOR = Color.PINK;
    final int URTAVLA_WIDTH = 20;
    final int HOUR_HAND_LENGTH = 100;
    final int HOUR_HAND_WIDTH = 5;
    final int MINUTE_HAND_LENGTH = 140;
    final int MINUTE_HAND_WIDTH = 5;
    final int SECOND_HAND_LENGTH = 180;
    final int SECOND_HAND_WIDTH = 5;
    final int RADIUS = 200;
    final int X_MIDDLE = 500;
    final int Y_MIDDLE = 300;
    final int STRECK_LANGD = 15;
    final int HOUR_DEGREE_FOR_HOUR = 30;
    final double MINUTE_DEGREE_FOR_HOUR = 0.5;
    final int MINUTE_DEGREE_FOR_MINUTE = 6;
    final int SECOND_DEGREE_FOR_SECOND = 6;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics g2d = (Graphics2D) g;
        int[] degrees = { 0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330 };
        MyPanelv1 obj = new MyPanelv1();

        for (int degree : degrees) {
            obj.urTavla(degree, (Graphics2D) g2d);
        }
        obj.calenderToVisare((Graphics2D) g2d, obj);
    }

    public void urTavla(int degree, Graphics2D g2d) {
        int radie = 20;
        String number = "";
        if (degree == 0) {
            number = "3";
        }
        if (degree == 270) {
            number = "12";
        }
        if (degree == 180) {
            number = "9";
        }
        if (degree == 90) {
            number = "6";
        }
        int xOval = this.X_MIDDLE - radie / 2;
        int yOval = this.Y_MIDDLE - radie / 2;
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(xOval, yOval, radie, radie);
        g2d.setStroke(new BasicStroke(10));
        int xStart = (int) (this.X_MIDDLE + this.RADIUS * Math.cos(degree * (Math.PI / 180)));
        int yStart = (int) (this.Y_MIDDLE + this.RADIUS * Math.sin(degree * (Math.PI / 180)));
        int xEnd = (int) (xStart + this.STRECK_LANGD * Math.cos(degree * (Math.PI / 180)));
        int yEnd = (int) (yStart + this.STRECK_LANGD * Math.sin(degree * (Math.PI / 180)));
        int xnumber = (int) (this.X_MIDDLE
                + (this.RADIUS - this.STRECK_LANGD - 30) * Math.cos(degree * (Math.PI / 180)));
        int ynumber = (int) (this.Y_MIDDLE
                + (this.RADIUS - this.STRECK_LANGD - 30) * Math.sin(degree * (Math.PI / 180)));
        g2d.setColor(Color.black);
        g2d.drawString(number, xnumber, ynumber);
        g2d.setColor(Color.cyan);
        g2d.setStroke(new BasicStroke(this.URTAVLA_WIDTH));
        g2d.drawLine(xStart, yStart, xEnd, yEnd);
    }

    public void calenderToVisare(Graphics2D g2d, MyPanelv1 obj) {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        double vinkel = hour * this.HOUR_DEGREE_FOR_HOUR + minute * this.MINUTE_DEGREE_FOR_HOUR;
        obj.visare(this.HOUR_HAND_LENGTH, this.HOUR_HAND_WIDTH, vinkel, (Graphics2D) g2d, this.HAND_COLOR);
        vinkel = minute * this.MINUTE_DEGREE_FOR_MINUTE;
        obj.visare(this.MINUTE_HAND_LENGTH, this.MINUTE_HAND_WIDTH, vinkel, (Graphics2D) g2d, this.HAND_COLOR);
        vinkel = second * this.SECOND_DEGREE_FOR_SECOND;
        obj.visare(this.SECOND_HAND_LENGTH, this.SECOND_HAND_WIDTH, vinkel, (Graphics2D) g2d, this.HAND_COLOR);
    }

    public void visare(int length, int bredd, double vinkel, Graphics2D g2d, Color color) {
        g2d.setStroke(new BasicStroke(bredd));
        vinkel = vinkel - 90;
        int xStart = this.X_MIDDLE;
        int yStart = this.Y_MIDDLE;
        int xEnd = (int) (xStart + length * Math.cos(vinkel * (Math.PI / 180)));
        int yEnd = (int) (yStart + length * Math.sin(vinkel * (Math.PI / 180)));
        g2d.setColor(color);
        g2d.drawLine(xStart, yStart, xEnd, yEnd);
    }

    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
