/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.egetbiljard;
import java.awt.*;
/**
 *
 * @author Nils
 */
public class Ball {
    
    private final int    BORDER_THICKNESS    = 2;
    public static int    RADIUS              = 10;
    private final double DIAMETER            = 2 * RADIUS;
    public static boolean player             = true;
    private int          player1Count        = 0;
    private int          player2Count        = 0;
    private final int    bonusTime           = 2;
    private double       SPEED               = 10;
    private final double FRICTION            = 0.025;
    private final double FRICTION_PER_UPDATE = 
                          1.0 - Math.pow(1.0 - FRICTION,
                                         100.0 / EgetBiljard.UPDATE_FREQUENCY);
    private Coord position;
    private Coord velocity;
    private Coord aimPosition;
    private Table myTable;
    private Color color;
    private final boolean queBall;

    Ball(Coord initialPosition, Table t,Color c, boolean kb) {
        position = initialPosition;
        velocity = new Coord(0,0);
        myTable  = t;
        color    = c;
        queBall  = kb;
    }

    private boolean isAiming() {
        return aimPosition != null;
    }
    
    boolean isMoving() {
        return velocity.magnitude() > FRICTION_PER_UPDATE;
    }
    
    void setAimPosition(Coord grabPosition) {
        if (Coord.distance(position, grabPosition) <= RADIUS) {
            aimPosition = grabPosition;
        }
    }
    
    void updateAimPosition(Coord newPosition) {
        if (isAiming()){
            aimPosition = newPosition;
        }
    }
     void updateQuePos(Coord newPosition){
        if (myTable.WHITE_DOWN){
            position = newPosition;
        }
    }
    void queDrop(Coord newPosition){
        if(ifDropped() && !ifDroppedTableX() && !ifDroppedTableY()){
            position = newPosition;
            myTable.WHITE_DOWN = !myTable.WHITE_DOWN;
        }
    }
     
    boolean ifDropped() {
        boolean dropped = false;
        int coll = 0; 
        for (Ball b:myTable.getBallArray()){
            if (Coord.distance(position, b.position) <= DIAMETER && b!=this) coll++;
        }
        for (Hole h:myTable.getHoleArray()){
            if (Coord.distance(position, h.position) <= DIAMETER) coll++;
        }
        if (coll == 0) {dropped = true;}
        return dropped;
    }
    
    boolean ifDroppedTableX() {
        int[] xPos = myTable.getPOLY_BOARDX();
        int[] yPos = myTable.getPOLY_BOARDY();
        return position.x-RADIUS<= xPos[10] || position.x-RADIUS<= xPos[0] && position.y-RADIUS>yPos[1] &&position.y+RADIUS<yPos[0] ||
                position.x-RADIUS<= xPos[0] && position.y-RADIUS>yPos[5] &&position.y+RADIUS<yPos[7] ||
                position.x+RADIUS>= xPos[5] || position.x+RADIUS>= xPos[3] && position.y-RADIUS>yPos[1] &&position.y+RADIUS<yPos[0] ||
                position.x+RADIUS>= xPos[3] && position.y-RADIUS>yPos[5] &&position.y+RADIUS<yPos[7];
    }
    
    boolean ifDroppedTableY() {
        int[] xPos = myTable.getPOLY_BOARDX();
        int[] yPos = myTable.getPOLY_BOARDY();
        return position.y+RADIUS>= yPos[7] || position.y+RADIUS>= yPos[5] && position.x-RADIUS > xPos[10] && position.x+RADIUS<xPos[0] ||
                position.y+RADIUS>= yPos[5] && position.x-RADIUS > xPos[3] && position.x+RADIUS<xPos[5] ||
                position.y-RADIUS<= yPos[1] || position.y-RADIUS<= yPos[0] && position.x-RADIUS>xPos[10] &&position.x+RADIUS<xPos[0] ||
                position.y-RADIUS<= yPos[0] && position.x-RADIUS>xPos[3] &&position.x+RADIUS<xPos[5];
    }
    
    void bonusCount() {
        if (player) player1Count++;
        else player2Count++;
        
        if (player1Count == bonusTime) { 
            SPEED *= 2;
            player1Count = 0;
        }
        else if (player2Count == bonusTime) SPEED *= 2; player2Count = 0;
    }
            
    void shoot() {
        bonusCount();
        if (isAiming()) {
            Coord aimingVector = Coord.sub(position, aimPosition);
            velocity = Coord.mul(Math.sqrt(SPEED * aimingVector.magnitude() / EgetBiljard.UPDATE_FREQUENCY),
                                 aimingVector.norm());
            aimPosition = null;
            player = !player;
        }
    }
    
    void move() {
         if (isMoving()) {                                   
            position.increase(velocity);      
            velocity.decrease(Coord.mul(FRICTION_PER_UPDATE, velocity.norm()));
        }
        collisionWallX();
        collisionWallY();
        contact();
        enterHole();
    }
    boolean ifContact(Ball ball) {
        return  (Coord.distance(position, ball.position) <= DIAMETER &&
                Coord.distance(Coord.add(position, velocity), Coord.add(ball.position, ball.velocity)) < 
                Coord.distance(position, ball.position) && ball != this);
    }
    void contact() {
        for (Ball ball : myTable.getBallArray()) {
            if (ifContact(ball)) {
                if (velocity.magnitude() < ball.velocity.magnitude()) {
                    double[] velList = collision(velocity, ball.velocity, position, ball.position);
                    velocity = new Coord(velList[0], velList[1]);
                    ball.velocity = new Coord(velList[2], velList[3]);
                }
                else {
                    double[] velList = collision(ball.velocity, velocity, ball.position, position);
                    velocity = new Coord(velList[2], velList[3]);
                    ball.velocity = new Coord(velList[0], velList[1]);
                }
            }
        }
    }
    static double[] collision(Coord velA,Coord velB,Coord posA,Coord posB) { //Formler enligt biljard-pdf:en (velocity(vel) = rÃ¶relsemÃ¤ngd(P))
        Coord subVel = Coord.sub(posA,posB).norm(); // Nytt dx och dy (rikitningsvektorn mha bollarnas positioner) enligt formel
        double impuls = ((velB.x*subVel.x) + (velB.y*subVel.y)) - ((velA.x*subVel.x) + (velA.y*subVel.y)); // Impuls(J) enligt formel
        velB.x = velB.x - (impuls*subVel.x);//Detta blir nya P => P' enligt formel
        velB.y = velB.y - (impuls*subVel.y);
        velA.x = velA.x + (impuls*subVel.x);
        velA.y = velA.y + (impuls*subVel.y);
        return new double[]  {velA.x,velA.y,velB.x,velB.y}; // returnerar array med nya P, alltsÃ¥ P' i formeln
    }
    
    void collisionWallX(){
        int[] xPos = myTable.getPOLY_BOARDX();
        int[] yPos = myTable.getPOLY_BOARDY();
        if(velocity.x<0){
            if(position.x-RADIUS<= xPos[10]){velocity.x *=-1;}
            else if(position.x-RADIUS<= xPos[0] && position.y-RADIUS>yPos[1] &&position.y+RADIUS<yPos[0] ){velocity.x *=-1;}
            else if(position.x-RADIUS<= xPos[0] && position.y-RADIUS>yPos[5] &&position.y+RADIUS<yPos[7] ){velocity.x *=-1;}
        }
        else if(velocity.x>0){
            if(position.x+RADIUS>= xPos[5]){velocity.x *=-1;}
            else if(position.x+RADIUS>= xPos[3] && position.y-RADIUS>yPos[1] &&position.y+RADIUS<yPos[0] ){velocity.x *=-1;}
            else if(position.x+RADIUS>= xPos[3] && position.y-RADIUS>yPos[5] &&position.y+RADIUS<yPos[7] ){velocity.x *=-1;}
        }
    }
    
    void collisionWallY() {
        int[] xPos = myTable.getPOLY_BOARDX();
        int[] yPos = myTable.getPOLY_BOARDY();
        if(velocity.y>0){
            if(position.y+RADIUS>= yPos[7]){velocity.y *=-1;}
            else if(position.y+RADIUS>= yPos[5] && position.x-RADIUS > xPos[10] && position.x+RADIUS<xPos[0] ){velocity.y *=-1;}
            else if(position.y+RADIUS>= yPos[5] && position.x-RADIUS > xPos[3] && position.x+RADIUS<xPos[5] ){velocity.y *=-1;}
        }
        else if(velocity.y<0){
            if(position.y-RADIUS<= yPos[1]){velocity.y *=-1;}
            else if(position.y-RADIUS<= yPos[0] && position.x-RADIUS>xPos[10] &&position.x+RADIUS<xPos[0] ){velocity.y *=-1;}
            else if(position.y-RADIUS<= yPos[0] && position.x-RADIUS>xPos[3] &&position.x+RADIUS<xPos[5] ){velocity.y *=-1;}
        }
    }
    
    boolean ifBlackBall(Color c) {
        return c == Color.BLACK;
    }
    
    boolean checkBallPos() {
        boolean dropped = false;
        int coll = 0; 
        for (Ball b:myTable.getBallArray()){
            if (Coord.distance(position, b.position) < DIAMETER && b !=this) coll++;
        }
        if (coll == 0) {dropped = true;}
        return dropped;
    }
    
    void countPoints() {
        int countP1 = 0;
        int countP2 = 0;
        for (Ball b: myTable.getBallArray()) {
            if ( b.position.x > myTable.Scoreboardpos1.x && 
                b.position.y < myTable.Scoreboardpos2.y ) {
                countP1++;
            }
            else if ( b.position.x > myTable.Scoreboardpos2.x && 
                b.position.y > myTable.Scoreboardpos2.y ) {
                countP2++;
            }
        }
        myTable.Player1Score = countP1;
        myTable.Player2Score = countP2;
    }
    
    void bonusPoint() {
        int rand = (int) (Math.random() * myTable.getBallColor().length) + 2;
        if (ifBlackBall(color) && !player) {
            myTable.Player1Score++;
            myTable.getBallArray()[rand].position.y = myTable.Scoreboardpos1.y+DIAMETER;
            myTable.getBallArray()[rand].position.x = myTable.Scoreboardpos1.x+DIAMETER*myTable.Player1Score;
        }
        else if (ifBlackBall(color) && player) {
            myTable.Player2Score++;
            myTable.getBallArray()[rand].position.y = myTable.Scoreboardpos2.y+DIAMETER;
            myTable.getBallArray()[rand].position.x = myTable.Scoreboardpos2.x+DIAMETER*myTable.Player2Score;
        }
    }
    
    void enterHole(){
        for (Hole hole : myTable.getHoleArray()) {
            if (Coord.distance(position, hole.position) <= DIAMETER && !queBall){
                if (!player){
                    getPoint1();
                    countPoints();
                }
                else if (player){
                    getPoint2();
                    countPoints();
                }
                velocity = new Coord(0,0);
            }
            else if ( Coord.distance(position, hole.position) <= DIAMETER && queBall){
                myTable.WHITE_DOWN = true;
                velocity= new Coord(0,0);
                position = new Coord(-10,-10);
            }
        }
    }
    void getPoint1() {
        myTable.Player1Score++;
        position.y = myTable.Scoreboardpos1.y+DIAMETER;
        position.x = myTable.Scoreboardpos1.x+DIAMETER*myTable.Player1Score;
        int i = 1;
        while (!checkBallPos()) {
            position.y = myTable.Scoreboardpos1.y+DIAMETER;
            position.x = myTable.Scoreboardpos1.x+DIAMETER*(myTable.Player1Score + i);
            i++;
        }
        if (checkBallPos()) bonusPoint();
    }
    
    void getPoint2() {
        myTable.Player2Score++;
        position.y = myTable.Scoreboardpos2.y+DIAMETER;
        position.x = myTable.Scoreboardpos2.x+DIAMETER*myTable.Player2Score;
        int i = -1;
        while (!checkBallPos()) {
            position.y = myTable.Scoreboardpos2.y+DIAMETER;
            position.x = myTable.Scoreboardpos2.x+DIAMETER*(myTable.Player2Score + i);
            i++;
        }
        if (checkBallPos()) bonusPoint();
    }

    // paint: to draw the ball, first draw a black ball
    // and then a smaller ball of proper color inside
    // this gives a nice thick border
    void paint(Graphics2D g2D) {
        g2D.setColor(Color.black);
        g2D.fillOval(
                (int) (position.x - RADIUS + 0.5),
                (int) (position.y - RADIUS + 0.5),
                (int) DIAMETER,
                (int) DIAMETER);
        g2D.setColor(color);
        g2D.fillOval(
                (int) (position.x - RADIUS + 0.5 + BORDER_THICKNESS),
                (int) (position.y - RADIUS + 0.5 + BORDER_THICKNESS),
                (int) (DIAMETER - 2 * BORDER_THICKNESS),
                (int) (DIAMETER - 2 * BORDER_THICKNESS));
        if (isAiming()) {
            paintAimingLine(g2D);
        }
    }
    
    private void paintAimingLine(Graphics2D graph2D) {
            Coord.paintLine(
                    graph2D,
                    aimPosition, 
                    Coord.sub(Coord.mul(2, position), aimPosition)
                           );
    } 
}
