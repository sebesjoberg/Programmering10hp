/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.*;
/**
 *
 * @author Nils
 */
public class labb3 {
    public static void main(String[] args) {
        JFrame frame = new JFrame("My Clock");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ClockFace myClock = new ClockFace();       
        frame.add(myClock);
        frame.pack();
        frame.setVisible(true);
        Timer t = new Timer(1000, myClock);
        t.start();
    }
}

