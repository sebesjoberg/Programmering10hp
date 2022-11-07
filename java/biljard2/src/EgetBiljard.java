/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.egetbiljard;
import java.awt.*;
import javax.swing.*;
/**
 *
 * @author Nils
 */
public class EgetBiljard {
    final static int UPDATE_FREQUENCY = 100;

    public static void main(String[] args) {

        JFrame frame = new JFrame("No collision?");          
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3,1));
        JButton button = new JButton("Play/Pause");        
        JButton button2 = new JButton("Restart");
        buttonPanel.add(button);                
        buttonPanel.add(button2);
        
        Table table = new Table(button,button2);       
        
        frame.setLayout(new BorderLayout());
        frame.add(table, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.EAST);
        frame.pack();
        frame.setVisible(true);
    }
}
