import java.awt.*;

class StartAndPause{
    int BORDER_THICKNESS=2;
    private Coord position;
    Color COLOR = Color.BLUE;
    Color STRING_COLOR = Color.PINK;
    int HEIGHT = 80;
    int WIDTH = 140;
    String STARTPAUSE="STARTED";
   StartAndPause(int xpos,int ypos){
       position = new Coord(xpos,ypos);
   } 
   
   void paint(Graphics2D g2D){
     g2D.setColor(Color.BLACK);
     g2D.fillRect((int)(position.x+0.5-WIDTH/2),(int)(position.y+0.5-HEIGHT/2),(int)(WIDTH/2),(int)(HEIGHT/2));
     g2D.setColor(COLOR);
     g2D.fillRect((int)(position.x+0.5+BORDER_THICKNESS-WIDTH/2),(int)(position.y+0.5+BORDER_THICKNESS-HEIGHT/2), (int)(WIDTH-2*BORDER_THICKNESS), (int)(HEIGHT-2*BORDER_THICKNESS));
   g2D.setColor(STRING_COLOR);
   g2D.drawString(STARTPAUSE,(int)(position.x),(int)(position.y));
   }
}

class DeathDisplay{
    int BORDER_THICKNESS=2;
    private Coord position;
    Color COLOR=Color.red;
    Color STRING_COLOR=Color.BLACK;
    int HEIGHT=80;
    int WIDTH=140;
    String DEAD;
    DeathDisplay(int xpos,int ypos){
        position = new Coord(xpos,ypos);
    }
    void paint(Graphics2D g2D,int dead){
        g2D.setColor(Color.BLACK);
     g2D.fillRect((int)(position.x+0.5-WIDTH/2),(int)(position.y+0.5-HEIGHT/2),(int)(WIDTH/2),(int)(HEIGHT/2));
     g2D.setColor(COLOR);
     g2D.fillRect((int)(position.x+0.5+BORDER_THICKNESS-WIDTH/2),(int)(position.y+0.5+BORDER_THICKNESS-HEIGHT/2), (int)(WIDTH-2*BORDER_THICKNESS), (int)(HEIGHT-2*BORDER_THICKNESS));
   g2D.setColor(STRING_COLOR);
   DEAD=Integer.toString(dead);
   g2D.drawString(DEAD,(int)(position.x),(int)(position.y));
    }
}
