/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.egetbiljard;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 *
 * @author Nils
 */
public class Table extends JPanel implements MouseListener, MouseMotionListener, ActionListener {

    private final int   TABLE_WIDTH    = 1000;
    private final int   TABLE_HEIGHT   = 800;
    private final int   FONT_SIZE      = 440;
    private final int   FONT_POS       = 160;
    private final int   WALL_THICKNESS = 20;
    private final int[] POLY_BOARDX    = {270,270,530,530,680,680,530,530,270,270,120,120};
    private final int[] POLY_BOARDY    = {270,120,120,270,270,530,530,680,680,530,530,270};
    private final int   POLY_CORNERS   = 12;
    private final int   NR_BALLS       = 16;
    private final int   NR_HOLES       = 8;
    private final int[] POLY_WALLX     = {250,250,550,550,700,700,550,550,250,250,100,100};
    private final int[] POLY_WALLY     = {250,100,100,250,250,550,550,700,700,550,550,250};
    private final Color[] BallColor    = {Color.BLACK,Color.ORANGE,Color.RED,Color.YELLOW,Color.blue,Color.CYAN,Color.pink,Color.MAGENTA};
    private final Color WHITE_COLOR    = Color.WHITE;
    private final Color TABLE_COLOR    = Color.green;
    private final Color WALL_COLOR     = Color.black;
    private final Color BACKGROUND     = Color.PINK;
    private final Color PLAYER_COLOR   = Color.yellow;
    private final Font myfont          = new Font("Garamond",Font.ITALIC,25);
    private final Font winnerFont      = new Font("Comic Sans MS",Font.BOLD,45);
    private final int ScoreBordWidth   = 180;
    private final int ScoreBordHeight  = 40;
    private final Coord whiteDownText  = new Coord(200,760);
    private final Coord turnsPos       = new Coord(800,100);
    public Coord      Scoreboardpos1   = new Coord(800,300);
    public Coord      Scoreboardpos2   = new Coord(800,400);
    private Coord     queStartPos      = new Coord(400,550);
    private final int StartPosBallsX   = 360;
    public int        Player1Score     = 0;
    public int        Player2Score     = 0;
    
    public boolean    WHITE_DOWN       = false;
    private boolean   PAUSED           = false;
    
    private String winner              = "";
    private String buttonPause         = "";
    private String buttonRestart       = "";
    private final String pasuemeny     = "Paused";
    private final String vitnere       = "Ooops.. the que ball went down, place it with a click";
    private Ball[] ballArray           = new Ball[NR_BALLS];//new Ball[NR_BALLS];
    private Hole[] holeArray           = new Hole[NR_HOLES];
    private final Timer simulationTimer;
    
    Table(JButton button,JButton button2 ) {
        
        setPreferredSize(new Dimension(TABLE_WIDTH + 2 * WALL_THICKNESS,
                                       TABLE_HEIGHT + 2 * WALL_THICKNESS));
        createInitialBalls();
        createInitialHoles();
        
        addMouseListener(this);
        addMouseMotionListener(this);
        setButtons(button, button2);
        
        simulationTimer = new Timer((int) (1000.0 / EgetBiljard.UPDATE_FREQUENCY), this);
    }


    private void createInitialBalls(){
        ballArray[0] = new Ball(queStartPos,this,WHITE_COLOR, true);
        int row = 0;
        int z = 0;
        int trimaker = 5;
        int colormaker = 0;
        int offset = Ball.RADIUS;
        for(int i = 1; i < NR_BALLS; i++){
            if(z == trimaker){
                row++;
                trimaker--;
                z=0;
            }
            if(colormaker == BallColor.length){
                colormaker = 0;
            }
            Coord initialPosition = new Coord(StartPosBallsX+z*offset*2 + offset*row,StartPosBallsX+row*offset*2);
            ballArray[i] = new Ball(initialPosition,this,BallColor[colormaker], false);
            z++;
            colormaker++;
        }
    }
    private void createInitialHoles(){
        int y = 0;
        for ( int i = 0; i < POLY_BOARDX.length; i++){
            if(i%3 != 0){
                holeArray[y]=new Hole(new Coord(POLY_BOARDX[i],POLY_BOARDY[i]));
                y++;
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {                                    // Timer event
        if(simulationTimer.isRunning()){
            for (Ball ball : ballArray) {
                ball.move();
                repaint();
            }
        }
        if (buttonRestart.equals(e.getActionCommand())) Restart();
        if (buttonPause.equals(e.getActionCommand())) Pause();
        
        if (StopCount()== ballArray.length) simulationTimer.stop();
        winnerCount();
    }
    
    public Ball[] getBallArray() {
        return ballArray;
    }
    public Hole[] getHoleArray() {
        return holeArray;
    }
    public int[] getPOLY_BOARDX() {
        return POLY_BOARDX;
    }

    public int[] getPOLY_BOARDY() {
        return POLY_BOARDY;
    }

    public int[] getPOLY_WALLX() {
        return POLY_WALLX;
    }

    public int[] getPOLY_WALLY() {
        return POLY_WALLY;
    }
    public Color[] getBallColor() {
        return BallColor;
    }
    
    private void setButtons(JButton b1, JButton b2) {
        buttonPause = b1.getText();
        buttonRestart = b2.getText();
        
        b1.setActionCommand(buttonPause);
        b2.setActionCommand(buttonRestart);
        b1.addActionListener(this);
        b2.addActionListener(this);
    }
    
    private void Pause(){
        if (!simulationTimer.isRunning()) {
                simulationTimer.start();
                PAUSED = false;
                repaint();
            }
             else if(simulationTimer.isRunning()) {
                simulationTimer.stop();
                PAUSED = true;
                repaint();
            }
    }
    
    private void Restart(){
        Player1Score     = 0;
        Player2Score     = 0;
        WHITE_DOWN       = false;
        PAUSED           = false;
        ballArray        = new Ball[NR_BALLS];
        queStartPos      = new Coord(400,550);
        winner           = "";
        createInitialBalls();
        repaint();
    }
    
    public int StopCount(){
        int ballsStopped = 0;
        for(Ball ball:ballArray){
            if(!ball.isMoving()) ballsStopped++;
        }
        return ballsStopped;
    }
    
    void winnerCount() {
        if (Player1Score == NR_BALLS/2) {winner = "Player 1";}
        else if (Player2Score == NR_BALLS/2) {winner = "Player 2";}
    }
    
    public void mousePressed(MouseEvent event) {
        if(!WHITE_DOWN&&!simulationTimer.isRunning() ){
            Coord mousePosition = new Coord(event);
            ballArray[0].setAimPosition(mousePosition);
            repaint();
        }
    }
    
    public void mouseDragged(MouseEvent event) {
        if(!WHITE_DOWN){
            Coord mousePosition = new Coord(event);       
            ballArray[0].updateAimPosition(mousePosition);
            repaint();
        }
    }
    
    public void mouseReleased(MouseEvent e) {
        if (!WHITE_DOWN && !simulationTimer.isRunning()){
            ballArray[0].shoot();
            if (!simulationTimer.isRunning()) simulationTimer.start();
        }
    }
    
    public void mouseClicked(MouseEvent e) {
        if(WHITE_DOWN&& !simulationTimer.isRunning()){
            Coord mousePosition = new Coord(e);
            ballArray[0].queDrop(mousePosition);
            repaint();
        }
    }
    
    public void mouseMoved(MouseEvent e) {
        if (!simulationTimer.isRunning()){
        Coord mousePosition = new Coord(e);
        ballArray[0].updateQuePos(mousePosition);
        repaint();
        }
    }
    
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2D = (Graphics2D) graphics;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // This makes the graphics smoother
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawTable(g2D);
        
        g2D.setColor(Color.BLACK);
        g2D.setFont(myfont);
        if (Ball.player &&  !simulationTimer.isRunning()){
            g2D.drawString("Player 1 it's your turn",(int) turnsPos.x,(int) turnsPos.y);
            drawScoreBoards(g2D, PLAYER_COLOR, WHITE_COLOR);
        }
        else if(!Ball.player &&  !simulationTimer.isRunning()){
            g2D.drawString("Player 2 it's your turn",(int) turnsPos.x,(int) turnsPos.y);
            drawScoreBoards(g2D, WHITE_COLOR, PLAYER_COLOR);
        }
        else drawScoreBoards(g2D, WHITE_COLOR, WHITE_COLOR);
        
        for(Ball ball : ballArray) ball.paint(g2D);
        
        for (Hole hole : holeArray) hole.paintHole(g2D);
            
        if(PAUSED) drawPauseSquare(g2D);
        
        if (WHITE_DOWN) g2D.drawString(vitnere, (int)whiteDownText.x , (int)whiteDownText.y);
        if (!"".equals(winner)) winnerSquare(g2D);
        
    }
    
    void drawTable(Graphics2D g2D) {
        g2D.setColor(BACKGROUND);
        g2D.fillRect(0, 0, TABLE_WIDTH + 2*WALL_THICKNESS, TABLE_HEIGHT + 2*WALL_THICKNESS);
        g2D.setColor(WALL_COLOR);
        g2D.fillPolygon(POLY_WALLX, POLY_WALLY, POLY_CORNERS);

        g2D.setColor(TABLE_COLOR);
        g2D.fillPolygon(POLY_BOARDX, POLY_BOARDY, POLY_CORNERS);
    }
    
    void drawScoreBoards(Graphics2D g2, Color c1, Color c2) {
        g2.setFont(myfont);
        g2.setColor(c1);
        g2.fillRect((int)Scoreboardpos1.x - WALL_THICKNESS/2, (int)Scoreboardpos1.y - WALL_THICKNESS/2, 
                ScoreBordWidth+WALL_THICKNESS , ScoreBordHeight+WALL_THICKNESS);
        g2.setColor(Color.lightGray);
        g2.fillRect((int)Scoreboardpos1.x, (int)Scoreboardpos1.y, ScoreBordWidth , ScoreBordHeight);
        g2.setColor(c2);
        g2.fillRect((int)Scoreboardpos2.x - WALL_THICKNESS/2, (int)Scoreboardpos2.y - WALL_THICKNESS/2, 
                ScoreBordWidth+WALL_THICKNESS , ScoreBordHeight+WALL_THICKNESS);
        g2.setColor(Color.lightGray);
        g2.fillRect((int)Scoreboardpos2.x, (int)Scoreboardpos2.y, ScoreBordWidth , ScoreBordHeight);
        g2.setColor(Color.BLACK);
        g2.drawString("Player 1", (int)Scoreboardpos1.x-80, (int)Scoreboardpos1.y+WALL_THICKNESS);
        g2.drawString("Player 2", (int)Scoreboardpos2.x-80, (int)Scoreboardpos2.y+WALL_THICKNESS);
    }
    
    void drawPauseSquare(Graphics2D g2D) {
        g2D.setFont(myfont);
        g2D.setColor(Color.ORANGE);
        g2D.fillRect(FONT_POS, FONT_POS, FONT_SIZE + 2*WALL_THICKNESS, FONT_SIZE + 2*WALL_THICKNESS);
        g2D.setColor(Color.LIGHT_GRAY);
        g2D.fillRect(FONT_POS + WALL_THICKNESS, FONT_POS + WALL_THICKNESS, FONT_SIZE, FONT_SIZE);
        g2D.setColor(Color.black);
        g2D.drawString(pasuemeny,280 , 310);
        g2D.drawString("Player 1 has "+(String)(""+Player1Score)+" balls down",200,380);
        g2D.drawString("Player 2 has "+(String)(""+Player2Score)+" balls down",200,420);
    }
    
    void winnerSquare(Graphics g2D) {
        g2D.setColor(Color.PINK);
        g2D.fillRect(0, 0, TABLE_WIDTH, TABLE_HEIGHT);
        g2D.setColor(Color.YELLOW);
        g2D.fillRect(4*WALL_THICKNESS, 4*WALL_THICKNESS, TABLE_WIDTH - 8*WALL_THICKNESS, TABLE_HEIGHT - 8*WALL_THICKNESS);
        g2D.setColor(Color.BLACK);
        g2D.setFont(winnerFont);
        g2D.drawString(winner+" IS THE WINNER!!!", TABLE_WIDTH/5, TABLE_HEIGHT/2);
    }
}
