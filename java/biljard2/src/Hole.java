/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.egetbiljard;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.JPanel;
/**
 *
 * @author Nils
 */
public class Hole extends JPanel {
    private final int   RADIUS       = 17;
    private final int   DIAMETER     = RADIUS*2;
    
    public Coord        position;
    private int         xPos;
    private int         yPos;
    
    Hole(Coord pos){
        xPos = (int)(pos.x)-RADIUS;
        yPos = (int)(pos.y)-RADIUS;
        position = pos;
        
    }
    void paintHole(Graphics2D g2D){
        g2D.setColor(Color.darkGray);
        g2D.fillOval(xPos, yPos, DIAMETER, DIAMETER);
         g2D.setColor(Color.black);
        g2D.fillOval(xPos+3, yPos+3, DIAMETER-2, DIAMETER-2);
    }
}
