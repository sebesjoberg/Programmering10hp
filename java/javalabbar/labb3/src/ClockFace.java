/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.labb3;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
/**
 *
 * @author Nils
 */
public class ClockFace extends JPanel implements ActionListener{
    final int SIZEX = 415, SIZEY = 415;
    final int CLOCK_RADIUS = 210, CLOCK_CENTRE = 200;
    final double BLOCK_ANGLE = Math.PI / 6;
    final int BLOCK_HEIGHT = 20, BLOCK_WIDTH = 5, BLOCK_AMOUNT = 12;
    final int MINUTE_HEIGHT = 150, MINUTE_WIDTH = 5, MBLOCK_HEIGHT = 8, MBLOCK_WIDTH = 3;
    final int HOUR_HEIGHT = 100, HOUR_WIDTH = 8;
    final double MINUTE_ANGLE = Math.PI/30, HOUR_ANGLE = Math.PI/6, FIX_ANGLE = Math.PI/2;
    final int SECONDS_HEIGHT = 175, SECONDS_WIDTH = 2;
    final int MID_CIRCLE = 20;
    boolean paintIt;
    final Color[] colorList = new Color[] {Color.PINK, Color.YELLOW, Color.MAGENTA, Color.RED,
    Color.BLUE, Color.CYAN, Color.ORANGE, Color.WHITE, Color.BLACK, Color.GREEN};
    Random rand = new Random();
    
    ClockFace() {
        setPreferredSize(new Dimension(SIZEX, SIZEY));
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        createClockFace(g2);
        settingTime(g2);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        paintIt = !paintIt;
        repaint();
    }
    public void createClockFace(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fillRect(0,0,SIZEX,SIZEY);
        g2.setStroke(new BasicStroke(10));
        int randC = rand.nextInt(10);
        Color c = colorList[randC];
        if(paintIt){
            g2.setColor(c);
        } else {
                    g2.setColor(c);
            }
        //g2.drawOval(10,10,2*CLOCK_CENTRE,2*CLOCK_CENTRE);
        g2.fillOval(CLOCK_CENTRE, CLOCK_CENTRE, MID_CIRCLE, MID_CIRCLE);
        drawMinuteBlocks(g2);
        drawBlocks(g2);
    }
    
    public void drawBlocks(Graphics2D g2) {
        double angle = FIX_ANGLE;
        int xPos; int yPos;
        int xBlock; int yBlock;
        g2.setStroke(new BasicStroke(BLOCK_WIDTH));
        
        for(int i = 0; i < BLOCK_AMOUNT; i++) {
            xPos = (int)(CLOCK_RADIUS + Math.cos(angle)*CLOCK_CENTRE);
            yPos = (int)(CLOCK_RADIUS - Math.sin(angle)*CLOCK_CENTRE);
            xBlock = (int)(xPos - Math.cos(angle)*BLOCK_HEIGHT);
            yBlock = (int)(yPos + Math.sin(angle)*BLOCK_HEIGHT);
            g2.drawLine(xPos, yPos,xBlock, yBlock);
            angle += BLOCK_ANGLE;
        }
    }
    public void drawMinuteBlocks(Graphics2D g2) {
        double angle = FIX_ANGLE;
        int xPos; int yPos;
        int xBlock; int yBlock;
        g2.setStroke(new BasicStroke(MBLOCK_WIDTH));
        
        for(int i = 0; i < 60; i++) {
            xPos = (int)(CLOCK_RADIUS + Math.cos(angle)*CLOCK_CENTRE);
            yPos = (int)(CLOCK_RADIUS - Math.sin(angle)*CLOCK_CENTRE);
            xBlock = (int)(xPos - Math.cos(angle)*MBLOCK_HEIGHT);
            yBlock = (int)(yPos + Math.sin(angle)*MBLOCK_HEIGHT);
            g2.drawLine(xPos, yPos,xBlock, yBlock);
            angle -= MINUTE_ANGLE;
        }
    }
    public void settingTime(Graphics2D g2) {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("HH");
	String currentHour = currentTime.format(formatterHour);
	int currentHourInt = Integer.parseInt(currentHour);  
	DateTimeFormatter formatterMinute = DateTimeFormatter.ofPattern("mm");
	String currentMinute = currentTime.format(formatterMinute);
	int currentMinuteInt = Integer.parseInt(currentMinute);
        DateTimeFormatter formatterSecond = DateTimeFormatter.ofPattern("ss");
        String currentSecond = currentTime.format(formatterSecond);
        int currentSecondInt = Integer.parseInt(currentSecond);
        
        double minuteAngle = FIX_ANGLE - currentMinuteInt*MINUTE_ANGLE;
        double hourAngle = FIX_ANGLE - (currentHourInt*HOUR_ANGLE) - 
                Math.sqrt(Math.pow((HOUR_ANGLE*(currentMinuteInt*MINUTE_ANGLE)/(2*Math.PI)),2));
                //Math.sqrt(Math.pow((minuteAngle*(HOUR_ANGLE/(currentMinuteInt*MINUTE_ANGLE))),2));
        double secondAngle = FIX_ANGLE - currentSecondInt * MINUTE_ANGLE;
        drawPointers(MINUTE_HEIGHT, MINUTE_WIDTH, minuteAngle, g2);
        drawPointers(HOUR_HEIGHT, HOUR_WIDTH, hourAngle, g2);
        drawPointers(SECONDS_HEIGHT,SECONDS_WIDTH,secondAngle,g2);
    }
    
    public void drawPointers(int length, int width, double angle, Graphics2D pointer) {
        int xPos = (int)(CLOCK_RADIUS + Math.cos(angle)*length); 
        int yPos = (int)(CLOCK_RADIUS - Math.sin(angle)*length);
        pointer.setColor(Color.BLACK);
        pointer.setStroke(new BasicStroke(width));
        pointer.drawLine(xPos, yPos,CLOCK_RADIUS, CLOCK_RADIUS);
    }
}
