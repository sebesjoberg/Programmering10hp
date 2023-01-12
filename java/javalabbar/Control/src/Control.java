//*******************************************************************
//
// GAME OF CONTROL
//
//                                 (c) Joachim Parrow 2003, 2006, 2010
//
//*******************************************************************

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;
import java.util.Arrays;

public class Control

 {

     static final String VERSION_NUMBER = "6.3";              // version number
     static final boolean testSetup = true;                  // If true, start a test session

     public static void main(String[] args)
     {

       // The following hack is needed to make the background colors of buttons
       // show on a Mac. If it makes trouble just delete it.

         String laf = UIManager.getCrossPlatformLookAndFeelClassName();
         try {
             UIManager.setLookAndFeel(laf);
         } catch (Exception e) {
             System.err.println("Error loading L&F: " + e.getMessage());
         }


      // main just starts one instance of StartControl - or two instances if testing.

        if (!testSetup) {
            new StartControl(false, false);       // Normal start: Here goes!
        } else {
            new StartControl(true, false);        // Test start: start a listener
            new StartControl(false, true);        // and then start a client on the same thread (OK for test purposes!)
        }
    }
}

//*************************************************************************
//
// Start Control
//
//  Handles all user interaction to start a game of control
//
//*************************************************************************


  class StartControl  implements ActionListener
  {
    // Graphic common variables

    final private JFrame myFrame         = new JFrame("The game of Control v" + Control.VERSION_NUMBER);

    final private Container thePane      = myFrame.getContentPane();

    final private JButton connectButton  = new JButton("Connect");     // The Buttons on the main screen
    final private JButton quitButton     = new JButton("Quit");
    final private JButton listenButton   = new JButton("Listen");
    final private JButton abortButton    = new JButton("Abort");
    final private JButton playButton     = new JButton("Play");
    final private JButton recordButton   = new JButton("Record");
    final private JButton playbackButton = new JButton("Playback");
    final private JButton optionsButton  = new JButton("Options");
    final private JButton defaultsButton = new JButton("Defaults");
    final private JButton returnButton   = new JButton("Return");

    private JTextField adrField;                                // input of opponent host name to connect to
    private JTextField nickField;                               // input of user nick

       // Button explanatory labels

    final private JLabel connectLabel   = new JLabel("   Press Connect to connect to another computer", SwingConstants.RIGHT);
    final private JLabel quitLabel      = new JLabel("   Press Quit to exit this program", SwingConstants.RIGHT);
    final private JLabel listenLabel    = new JLabel("   Press Listen to listen for connections", SwingConstants.RIGHT);
    final private JLabel abortLabel     = new JLabel("   Press Abort to return to main screen", SwingConstants.RIGHT);
    final private JLabel playLabel      = new JLabel("   Press Play to start the game", SwingConstants.RIGHT);
    final private JLabel recordLabel    = new JLabel("   Press Record to choose a file for recording the game", SwingConstants.RIGHT);
    final private JLabel playbackLabel  = new JLabel("   Press Playback to playback a recorded game", SwingConstants.RIGHT);
    final private JLabel optionsLabel   = new JLabel("   Press Options to change options", SwingConstants.RIGHT);
    final private JLabel returnLabel    = new JLabel("   Set options and return to main menu", SwingConstants.RIGHT);
    final private JLabel defaultsLabel  = new JLabel("   Reset to default values", SwingConstants.RIGHT);
    private JLabel ipLabel;


    final private JPanel connectPanel   = new JPanel();   // panels containing buttons and their labels
    final private JPanel quitPanel      = new JPanel();
    final private JPanel listenPanel    = new JPanel();
    final private JPanel abortPanel     = new JPanel();
    final private JPanel playPanel      = new JPanel();
    final private JPanel recordPanel    = new JPanel();
    final private JPanel playbackPanel  = new JPanel();
    final private JPanel optionsPanel   = new JPanel();
    final private JPanel returnPanel    = new JPanel();
    final private JPanel defaultsPanel  = new JPanel();

    // The following is all concerned with the options screen

    final private JRadioButton compactButton = new JRadioButton("Compact");     // graphic option radio buttons
    final private JRadioButton largeButton   = new JRadioButton ("Large");
    final private JRadioButton hugeButton    = new JRadioButton("Huge");

    final private JRadioButton fastButton   = new JRadioButton("Fast");         // speed option radio buttons
    final private JRadioButton normalButton = new JRadioButton ("Normal");
    final private JRadioButton slowButton   = new JRadioButton("Slow");

    final private JRadioButton squareButton = new JRadioButton("Square");       // Area shape radio buttons
    final private JRadioButton hexButton    = new JRadioButton("Hexagonal");
    final private JRadioButton circleButton = new JRadioButton("Circle");

    final private JRadioButton boardCircleButton = new JRadioButton("Round");   // board shape radio buttons
    final private JRadioButton boardSquareButton = new JRadioButton("Rectangular");

    final private JRadioButton liteButton    = new JRadioButton("Controlite (no heavy pieces)");  // Game type radio buttons
    final private JRadioButton controlButton = new JRadioButton("Control (all pieces)");

    final private JRadioButton noHillsButton = new JRadioButton("Flat board");         // Board topography radio buttons
    final private JRadioButton hillsButton   = new JRadioButton("Hills on the board");

    final private int[] sizeChoices       = {8,10,12,14,16};                     // allowed choices of width and height and pebbles

    final private String[] widthChoisesStr   = {"Width:  8","Width: 10","Width: 12","Width: 14","Width: 16"};
    final private String[] heightChoisesStr  = {"Height:  8","Height: 10","Height: 12","Height: 14","Height: 16"};
    final private String[] pebblesChoisesStr = {"Pebbles:  8","Pebbles: 10","Pebbles: 12","Pebbles: 14","Pebbles: 16"};


    final private int[] lengthChoices       = {5,10,15,20,30};                   // allowed choices of max game length
    final private String[] lengthChoicesStr = {" 5 min.","10 min.","15 min.","20 min.","30 min."};

    final private JComboBox widthBox   = new JComboBox(widthChoisesStr);
    final private JComboBox heightBox  = new JComboBox(heightChoisesStr);
    final private JComboBox pebblesBox = new JComboBox(pebblesChoisesStr);
    final private JComboBox lengthBox  = new JComboBox(lengthChoicesStr);



    // ----------------- Texts to inform the user ----------------------------------------

    final private String welcomeStatus         = "         Welcome to the game of Control!\n"+
                             "                          (c) Joachim Parrow 2003,2006,2010\n\n"+
                            "You are currently not connected to another Player. "+
                            "To attempt to connect, press the Connect Button above ("+
                            "you will need to tell me the IP of the computer to connect to). " +
                            "Alternatively, to listen in case someone tries to connect to you " +
                            "press the Listen Button.";

    final private String listenStatus   = "You are currently listening for someone to connect to you.\n\n"+
                            "When this happens I shall notify you and ask if you want to accept the connection. "+
                            "Until then there is nothing to do but wait. "+
                            "Pressing Abort above will mean that you give up listening.";

    final private String connectingStatus = "Please enter either the IP name or the number "+
                              "you want to connect to in the field above (for example 'myhost.edu.com' or '137.0.1.15'. Pressing <return> "+
                              "in this field will make me start connecting to it. "+
                              "Pressing Abort returns to main screen.";

    final private String acceptingstatus = "An opponent at the address given above is ready to play.\n\n"+
                             "Accept the challange by pressing the Play button. "+
                             "If you do not wish to play this opponent press Abort "+
                             "which returns you to the main menu";

    final private String connectedStatus = "You have successfully connected to a server at the address given above. "+
                             "Now you must wait for a user at that address to accept to play with you. "+
                             "When that happens the game will start. "+
                             "Until then you must wait. If you get tired Abort takes you back to the main menu "+
                             "and the connection will be lost.";

    final private String optionsStatus    = "Here you can set options for your game. A nick is optional and will only be used "+
                             "to tell your opponent who you are."+
                             "The other options must be set in the same way by both players.\n\n"+
                             "Game type: Choose controlite (only Pebbles and Squares) or Control (all heavy pieces).\n\n"+
                             "Hills: Hills are randomly distributed; a piece on a hill exerts control over a larger area. "+
                             "Choose to play with hills on the board or not.\n\n"+
                             "Board shape is the shape of the board, while area shape is the shape of the area a piece controls.\n\n"+
                             "The relative board dimensions adjust the size of the board in relation to the size of pieces, while the choice of graphics "+
                             "determines how large it appears on the screen.\n\n"+
                             "Game speed determines how long a player has to wait between moves, and length is the duration of a game.\n\n"+
                             "Finally, Pebbles is the number of Pebbles available to each player (the initial Pebble plus the additional Pebbles that may be built).";



    // Communication  variables

    private Socket              outSocket      = null;          // outgoing communications socket to other player
    private ServerSocket        inSocket       = null;          // incoming communications server from other player
    private Socket              connection     = null;          // incoming communications socket
    private ObjectOutputStream  outgoing;                       // outgoing stream to opponent
    private ObjectInputStream   incoming;                       // incoming stream from opponent

    private GetConnection       getConnection = null;          // threads listening for connections
    private GetAccept           getAccept = null;


    private ObjectOutputStream  record;                         // for recording games
    private ObjectInputStream   playback;                       // for recorded games

    final private static int    PORT_NUMBER =   8888;           // port number that this game uses

    // Game logic  variables

    private boolean playWhite;                              // set if I (randomly) got to play white

    private String  nick =          "";                     // your nickname
    private String  opponentNick =  "";                     // opponent's nickname
    private String  lastAddress =   "";                     // address typed in when connecting

    private int graphics, shape, boardShape, speed, width, height, pebbles, length;      // game params
    private boolean isLite, withHills;

    private int widthIdx, heightIdx, pebblesIdx, lengthIdx; // idx to the options

    // default options

    final  private static int DEFAULT_GRAPHICS = 1, DEFAULT_SHAPE = 3, DEFAULT_BOARDSHAPE = 1,
                              DEFAULT_SPEED = 1, DEFAULT_WIDTH = 8, DEFAULT_HEIGHT = 8,
                              DEFAULT_PEBBLES = 8, DEFAULT_LENGTH = 10;
    final  private static boolean DEFAULT_ISLITE = false;
    final  private static boolean DEFAULT_WITHHILLS = true;

     // test parameters

     private boolean testServer;            // true if testing and I should set up a server
     private boolean testClient;            // true if testing and I should set up a client

    //-------------------------------------------------------------------------------
    // Constructor just initialises first frame
    //-------------------------------------------------------------------------------



     StartControl(boolean testServer, boolean testClient)
    {
       quitButton.setBackground    (Color.red);        // set button colors need to redifne laf to work on a Mac :(
       connectButton.setBackground (Color.green);
       listenButton.setBackground  (Color.green);
       playButton.setBackground    (Color.green);
       abortButton.setBackground   (Color.orange);
       recordButton.setBackground  (Color.yellow);
       playbackButton.setBackground(Color.yellow);
       optionsButton.setBackground (Color.yellow);
       returnButton.setBackground  (Color.green);
       defaultsButton.setBackground(Color.orange);

       Color buttonColor = Board.BACKGROUNDCOLOR.brighter();  // color for buttons in options panel

       controlButton.setBackground (buttonColor);
       liteButton.setBackground    (buttonColor);

       hillsButton.setBackground   (buttonColor);
       noHillsButton.setBackground (buttonColor);

       compactButton.setBackground (buttonColor);
       largeButton.setBackground   (buttonColor);
       hugeButton.setBackground    (buttonColor);

       fastButton.setBackground    (buttonColor);
       normalButton.setBackground  (buttonColor);
       slowButton.setBackground    (buttonColor);

       squareButton.setBackground (buttonColor);
       hexButton.setBackground    (buttonColor);
       circleButton.setBackground (buttonColor);

       boardCircleButton.setBackground (buttonColor);
       boardSquareButton.setBackground (buttonColor);


       widthBox.setBackground   (buttonColor);
       heightBox.setBackground  (buttonColor);
       pebblesBox.setBackground (buttonColor);
       lengthBox.setBackground  (buttonColor);

       quitPanel.add(quitButton);                   // build button panels
       connectPanel.add(connectButton);
       listenPanel.add(listenButton);
       abortPanel.add(abortButton);
       playPanel.add(playButton);
       recordPanel.add(recordButton);
       playbackPanel.add(playbackButton);
       optionsPanel.add(optionsButton);
       returnPanel.add(returnButton);
       defaultsPanel.add(defaultsButton);

       quitPanel.setBackground(Board.BACKGROUNDCOLOR);  // set panel backgrounds
       listenPanel.setBackground(Board.BACKGROUNDCOLOR);
       connectPanel.setBackground(Board.BACKGROUNDCOLOR);
       abortPanel.setBackground(Board.BACKGROUNDCOLOR);
       playPanel.setBackground(Board.BACKGROUNDCOLOR);
       recordPanel.setBackground(Board.BACKGROUNDCOLOR);
       playbackPanel.setBackground(Board.BACKGROUNDCOLOR);
       optionsPanel.setBackground(Board.BACKGROUNDCOLOR);
       returnPanel.setBackground(Board.BACKGROUNDCOLOR);
       defaultsPanel.setBackground(Board.BACKGROUNDCOLOR);

       quitButton.addActionListener(this);              // add me as listener for all buttons
       connectButton.addActionListener(this);
       listenButton.addActionListener(this);
       abortButton.addActionListener(this);
       playButton.addActionListener(this);
       recordButton.addActionListener(this);
       playbackButton.addActionListener(this);
       optionsButton.addActionListener(this);
       returnButton.addActionListener(this);
       defaultsButton.addActionListener(this);

       graphics   = DEFAULT_GRAPHICS;                 // set params to defaults
       speed      = DEFAULT_SPEED;
       length     = DEFAULT_LENGTH;
       height     = DEFAULT_HEIGHT;
       width      = DEFAULT_WIDTH;
       pebbles    = DEFAULT_PEBBLES;
       shape      = DEFAULT_SHAPE;
       isLite     = DEFAULT_ISLITE;
       boardShape = DEFAULT_BOARDSHAPE;
       withHills  = DEFAULT_WITHHILLS;

       heightIdx  = 0;                           // default idx in param choices must also be set
       widthIdx   = 0;
       pebblesIdx = 0;
       lengthIdx  = 1;

       myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // Ow process continues to run when window closes


       try                              // try to get previously saved game parameters from the file options<V>.controloptions
         {
           FileInputStream optionsFile = new FileInputStream("options"+Control.VERSION_NUMBER+".controloptions");
           ObjectInputStream OS = new ObjectInputStream(optionsFile);

           nick         = (String)OS.readObject();
           speed        = OS.readInt();
           graphics     = OS.readInt();
           shape        = OS.readInt();
           boardShape   = OS.readInt();
           widthIdx     = OS.readInt();
           heightIdx    = OS.readInt();
           pebblesIdx   = OS.readInt();
           lengthIdx    = OS.readInt();
           width        = OS.readInt();
           height       = OS.readInt();
           pebbles      = OS.readInt();
           length       = OS.readInt();
           lastAddress  = (String)OS.readObject();
           isLite       = OS.readBoolean();
           withHills    = OS.readBoolean();

           OS.close();
         }
       catch (Exception e) {}      // If it doesn't work then just ignore

      //  Calculate my IP number

      String myIP;
      try
        {
          myIP = InetAddress.getLocalHost().getHostAddress();
      }
      catch (IOException e) {myIP = null;}

      if (myIP != null)
          ipLabel = new JLabel("    your IP number is " + myIP, SwingConstants.RIGHT);


      this.testServer = testServer;
      this.testClient = testClient;

       welcome();                       // go to main screen
    }

    //-------------------------------------------------------------------------------
    // Main screen
    //-------------------------------------------------------------------------------

    private void welcome ()
    {

     thePane.removeAll();                       // clear frame
     thePane.setLayout(new BorderLayout());


     JPanel statusPanel    = new JPanel();      // two new panels
     JPanel buttonsPanel   = new JPanel();

     buttonsPanel.setLayout(new GridLayout(7,2,20,20));   // for buttons and their explanatory labels

     TextArea statusArea = new TextArea(welcomeStatus,7,40,TextArea.SCROLLBARS_VERTICAL_ONLY);  // intro message

       buttonsPanel.add(connectLabel);          // build the buttons panel
       buttonsPanel.add(connectPanel);
       buttonsPanel.add(listenLabel);
       buttonsPanel.add(listenPanel);
       buttonsPanel.add(recordLabel);
       buttonsPanel.add(recordPanel);
       buttonsPanel.add(playbackLabel);
       buttonsPanel.add(playbackPanel);
       buttonsPanel.add(optionsLabel);
       buttonsPanel.add(optionsPanel);
       buttonsPanel.add(quitLabel);
       buttonsPanel.add(quitPanel);


       buttonsPanel.add(Box.createRigidArea(new Dimension(0,20)));

       statusArea.setEditable(false);           // and the status message
       statusPanel.add(statusArea);

       thePane.add(buttonsPanel);               // build the frame
       thePane.add(statusPanel,BorderLayout.SOUTH);
       thePane.add(Box.createRigidArea(new Dimension(0, 40)),BorderLayout.NORTH);
       thePane.add(Box.createRigidArea(new Dimension(50, 0)), BorderLayout.WEST);

       thePane.setBackground(Board.BACKGROUNDCOLOR);  // make sure background is right

       buttonsPanel.setBackground(Board.BACKGROUNDCOLOR);
       statusPanel.setBackground(Board.BACKGROUNDCOLOR);

       myFrame.pack();                          // display the frame
       myFrame.setVisible(true);

       if (testServer) startListening();
       if (testClient) tryConnecting();
    }

  //-------------------------------------------------------------------------------
  // Set game params (aka options)
  //    We come here when user presses "options"
  //-------------------------------------------------------------------------------

    private void options()
    {
     thePane.removeAll();                       // clear frame
     thePane.setLayout(new GridBagLayout());

     Color optionGroupColor = Board.BACKGROUNDCOLOR;    // background for the options group

     JPanel nickPane = new JPanel();                 // the panel where nick should be input
     JLabel nickLabel = new JLabel("Your nickname (optional)");   // its label
     nickField = new JTextField(nick,10);            // and text input field
     nickField.addActionListener(this);
     nickPane.setLayout(new BoxLayout(nickPane, BoxLayout.X_AXIS));  // build this panel
     nickPane.add(Box.createRigidArea(new Dimension(50,0)));
     nickPane.add(nickLabel);
     nickPane.add(Box.createRigidArea(new Dimension(50,0)));
     nickPane.add(nickField);
     nickPane.add(Box.createRigidArea(new Dimension(50,0)));
     nickPane.setBackground(Board.BACKGROUNDCOLOR);


     ButtonGroup typeGroup = new ButtonGroup();     // Group of buttons for game type
     if (isLite) liteButton.setSelected(true);
       else controlButton.setSelected(true);
     typeGroup.add(liteButton);
     typeGroup.add(controlButton);
     JPanel typePanel = new JPanel();
     typePanel.add(liteButton);
     typePanel.add(Box.createRigidArea(new Dimension(30,0)));
     typePanel.add(controlButton);
     typePanel.setBackground(optionGroupColor);
     Border typeBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED), "Game type", TitledBorder.ABOVE_TOP, TitledBorder.LEFT);
     typePanel.setBorder( typeBorder);

     ButtonGroup hillGroup = new ButtonGroup();     // Group of buttons for board topography
     if (withHills) hillsButton.setSelected(true);
       else noHillsButton.setSelected(true);
     hillGroup.add(hillsButton);
     hillGroup.add(noHillsButton);
     JPanel hillsPanel = new JPanel();
     hillsPanel.add(hillsButton);
     hillsPanel.add(Box.createRigidArea(new Dimension(30,0)));
     hillsPanel.add(noHillsButton);
     hillsPanel.setBackground(optionGroupColor);
     Border hillsBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED), "Board topography", TitledBorder.ABOVE_TOP, TitledBorder.LEFT);
     hillsPanel.setBorder(hillsBorder);

     ButtonGroup graphicGroup = new ButtonGroup();        // group of radio buttons for graphics
     if (graphics==1) compactButton.setSelected(true);
     else if (graphics==2) largeButton.setSelected(true);
     else hugeButton.setSelected(true);
     graphicGroup.add(compactButton);
     graphicGroup.add(largeButton);
     graphicGroup.add(hugeButton);
     JPanel graphicPanel = new JPanel();
     graphicPanel.add(compactButton);
     graphicPanel.add(Box.createRigidArea(new Dimension(30,0)));
     graphicPanel.add(largeButton);
     graphicPanel.add(Box.createRigidArea(new Dimension(30,0)));
     graphicPanel.add(hugeButton);
     graphicPanel.setBackground(optionGroupColor);
     Border graphicBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED), "Graphics", TitledBorder.ABOVE_TOP, TitledBorder.LEFT);
     graphicPanel.setBorder(graphicBorder);

     ButtonGroup boardShapeGroup = new ButtonGroup();        // group of radio buttons for board shape
     boardShapeGroup.add(boardSquareButton);
     boardShapeGroup.add(boardCircleButton);
     if (boardShape==1) boardSquareButton.setSelected(true);
     else if (boardShape==2) boardCircleButton.setSelected(true);
     JPanel boardShapePanel = new JPanel();
     boardShapePanel.add(boardSquareButton);
     boardShapePanel.add(Box.createRigidArea(new Dimension(30,0)));
     boardShapePanel.add(boardCircleButton);
     boardShapePanel.setBackground(optionGroupColor);
     Border boardShapeBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED), "Board shape", TitledBorder.ABOVE_TOP, TitledBorder.LEFT);
     boardShapePanel.setBorder(boardShapeBorder);


     ButtonGroup shapeGroup = new ButtonGroup();        // group of radio buttons for area shape
     if (shape==1) squareButton.setSelected(true);
     else if (shape==2) hexButton.setSelected(true);
     else if (shape==3) circleButton.setSelected(true);
     shapeGroup.add(squareButton);
     shapeGroup.add(hexButton);
     shapeGroup.add(circleButton);
     JPanel shapePanel = new JPanel();
     shapePanel.add(squareButton);
     shapePanel.add(Box.createRigidArea(new Dimension(30,0)));
     shapePanel.add(hexButton);
     shapePanel.add(Box.createRigidArea(new Dimension(30,0)));
     shapePanel.add(circleButton);
     shapePanel.setBackground(optionGroupColor);
     Border shapeBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED), "Shape of areas", TitledBorder.ABOVE_TOP, TitledBorder.LEFT);
     shapePanel.setBorder(shapeBorder);

     ButtonGroup speedGroup = new ButtonGroup();        // group of radio buttons for speed
     if (speed==1) fastButton.setSelected(true);
     else if (speed==2) normalButton.setSelected(true);
     else slowButton.setSelected(true);
     speedGroup.add(fastButton);
     speedGroup.add(normalButton);
     speedGroup.add(slowButton);
     JPanel speedPanel = new JPanel();
     speedPanel.add(fastButton);
     speedPanel.add(Box.createRigidArea(new Dimension(30,0)));
     speedPanel.add(normalButton);
     speedPanel.add(Box.createRigidArea(new Dimension(30,0)));
     speedPanel.add(slowButton);
     speedPanel.setBackground(optionGroupColor);
     Border speedBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED), "Game speed", TitledBorder.ABOVE_TOP, TitledBorder.LEFT);
     speedPanel.setBorder(speedBorder);

     JPanel sizePanel = new JPanel();                   // relative board size options
     heightBox.setSelectedIndex(heightIdx);
     widthBox.setSelectedIndex(widthIdx);
     pebblesBox.setSelectedIndex(pebblesIdx);
     sizePanel.add(widthBox);
     sizePanel.add(Box.createRigidArea(new Dimension(30,0)));
     sizePanel.add(heightBox);
     sizePanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), "Relative board dimensions", TitledBorder.ABOVE_TOP, TitledBorder.LEFT));
     sizePanel.setBackground(optionGroupColor);


     JPanel resourcePanel = new JPanel();                   // Pebbles
     pebblesBox.setSelectedIndex(pebblesIdx);
     resourcePanel.add(pebblesBox);
     resourcePanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), "Pebbles", TitledBorder.ABOVE_TOP, TitledBorder.LEFT));
     resourcePanel.setBackground(optionGroupColor);

     JPanel lengthPanel = new JPanel();                 // game max length option
     lengthBox.setSelectedIndex(lengthIdx);
     lengthPanel.add(lengthBox);
     lengthPanel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), "Game length", TitledBorder.ABOVE_TOP, TitledBorder.LEFT));
     lengthPanel.setBackground(optionGroupColor);

     JPanel buttonsPanel   = new JPanel();
     buttonsPanel.setLayout(new GridLayout(2,2,0,20));   // for buttons and their explanatory labels
     buttonsPanel.add(defaultsLabel);                    // buttons here are defaults and return
     buttonsPanel.add(defaultsPanel);
     buttonsPanel.add(returnLabel);
     buttonsPanel.add(returnPanel);
     buttonsPanel.setBackground(Board.BACKGROUNDCOLOR);

     JPanel statusPanel    = new JPanel();
     TextArea statusArea = new TextArea(optionsStatus,5,60,TextArea.SCROLLBARS_VERTICAL_ONLY);  // intro message
     statusArea.setEditable(false);
     statusPanel.setBackground(Board.BACKGROUNDCOLOR);
     statusPanel.add(statusArea);

     // finally build the frame

     GridBagConstraints constr = new GridBagConstraints();
     constr.insets = new Insets(10,10,10,10);                // padding
     constr.ipadx = constr.ipady = 20;

     constr.gridx = constr.gridy=0;
     constr.gridwidth=2;
     thePane.add(nickPane,constr);
     constr.gridx = 0; constr.gridy = 1; constr.gridwidth = 1;
       thePane.add(typePanel,constr);
     constr.gridx = 1; constr.gridy = 1;
       thePane.add(hillsPanel,constr);
     constr.gridx = 0; constr.gridy = 2;
       thePane.add(boardShapePanel,constr);
     constr.gridx = 1; constr.gridy = 2;
       thePane.add(shapePanel,constr);
     constr.gridx = 0; constr.gridy = 3;
       thePane.add(sizePanel,constr);
     constr.gridx = 1; constr.gridy = 3;
       thePane.add(graphicPanel,constr);
     constr.gridx = 0; constr.gridy = 4;
       thePane.add(speedPanel,constr);
     constr.gridx = 1; constr.gridy = 4;
       thePane.add(lengthPanel,constr);
     constr.gridx = 0; constr.gridy = 5;
       thePane.add(resourcePanel,constr);
     constr.gridx = 1; constr.gridy = 5;
       thePane.add(buttonsPanel,constr);
     constr.gridx = 0; constr.gridy = 6;  constr.gridwidth=2;
       thePane.add(statusPanel,constr);

     myFrame.pack();                          // display the frame
     myFrame.setVisible(true);

    }

  //-------------------------------------------------------------------------------
  // Exiting options: these will have to be remembered
  //  We come here when user exits the option screen
  //-------------------------------------------------------------------------------

    private void exitOptions()
    {
       nick = nickField.getText();

       if (fastButton.isSelected()) speed = 1;
       else if (normalButton.isSelected()) speed = 2;
       else speed = 3;

       if (compactButton.isSelected()) graphics = 1;
       else if (largeButton.isSelected()) graphics = 2;
       else graphics = 3;

       if (squareButton.isSelected()) shape = 1;
       else if (hexButton.isSelected()) shape = 2;
       else shape = 3;

       if (boardSquareButton.isSelected()) boardShape = 1;
       else boardShape = 2;

       if (liteButton.isSelected()) isLite = true; else isLite = false;
       withHills =  hillsButton.isSelected();

       widthIdx   = widthBox.getSelectedIndex();    // also remember idx in choice lists
       heightIdx  = heightBox.getSelectedIndex();
       pebblesIdx = pebblesBox.getSelectedIndex();
       lengthIdx  = lengthBox.getSelectedIndex();

       width      = sizeChoices[widthIdx];
       height     = sizeChoices[heightIdx];
       pebbles    = sizeChoices[pebblesIdx];
       length     = lengthChoices[lengthIdx];

       writeOptions();                              // save options to disc
       welcome();                                   // return to main screen

    }


    private void writeOptions()
    {
       //---- and write on the options file

       try
       {FileOutputStream optionsFile = new FileOutputStream("options"+Control.VERSION_NUMBER+".controloptions");
       ObjectOutputStream OS = new ObjectOutputStream(optionsFile);


       OS.writeObject(nick);
       OS.writeInt(speed);
       OS.writeInt(graphics);
       OS.writeInt(shape);
       OS.writeInt(boardShape);
       OS.writeInt(widthIdx);
       OS.writeInt(heightIdx);
       OS.writeInt(pebblesIdx);
       OS.writeInt(lengthIdx);
       OS.writeInt(width);
       OS.writeInt(height);
       OS.writeInt(pebbles);
       OS.writeInt(length);
       OS.writeObject(lastAddress);
       OS.writeBoolean(isLite);
       OS.writeBoolean(withHills);

       OS.close();
       }
       catch (Exception e) {}     // If it doesn't work then just ignore

    }


  //-------------------------------------------------------------------------------
  // Reset Options to defaults
  //-------------------------------------------------------------------------------

    private void defaults()
    {  graphics = DEFAULT_GRAPHICS;
       shape = DEFAULT_SHAPE;
       boardShape = DEFAULT_BOARDSHAPE;
       speed   = DEFAULT_SPEED;
       length  = DEFAULT_LENGTH;
       height  = DEFAULT_HEIGHT;
       width   = DEFAULT_WIDTH;
       pebbles = DEFAULT_PEBBLES;
       isLite = DEFAULT_ISLITE;
       withHills = DEFAULT_WITHHILLS;

       heightIdx = 0;   // also reset choice idx:es
       widthIdx  = 0;
       pebblesIdx = 0;
       lengthIdx = 1;

       options();       // go back to options screen
    }

  //-------------------------------------------------------------------------------
  // User wants to record game so choose a file
  //-------------------------------------------------------------------------------

    private void recordGame()

    {
        JFileChooser myFileChooser = new JFileChooser("Choose file where to save the game");  // get the file
        myFileChooser.setBackground(Board.BACKGROUNDCOLOR);
        myFileChooser.setCurrentDirectory(new File("C:/Games/Control"));
        if (myFileChooser.showSaveDialog(myFrame) == JFileChooser.APPROVE_OPTION)
         {try
          {record = new ObjectOutputStream(new FileOutputStream (myFileChooser.getSelectedFile()));
           record.writeObject("Game of Control");                                            // write "Game of Control" and version number on the file
           record.writeObject(Control.VERSION_NUMBER);}
         catch(Exception ex)
          {JOptionPane.showMessageDialog(thePane, "Sorry, unable to open and write on file.\n"+   // tell user if and why it failed
                                               "The reason given by the system is:\n"
                                               + ex.toString(),
                                               "File Error",
                                               JOptionPane.ERROR_MESSAGE);
           record = null;
          }
        }
    }

  //-------------------------------------------------------------------------------
  // User wants to play back a recorded game so let him choose a file
  //-------------------------------------------------------------------------------

    private void playBack()
    {   String reason="";           // to hold reason for a failure

        JFileChooser myFileChooser = new JFileChooser("Choose file to play back");  // get the file
        myFileChooser.setCurrentDirectory(new File("C:/Games/Control"));
        if (myFileChooser.showOpenDialog(myFrame) == JFileChooser.APPROVE_OPTION)
          try
           {playback = new ObjectInputStream(new FileInputStream (myFileChooser.getSelectedFile()));
            String s = (String) playback.readObject();
            String s2 = (String) playback.readObject();
            if (!s.equals("Game of Control")) {reason = "Not a recorded game file"; throw new Exception();}    // check it begins correctly
            if (!s2.equals(Control.VERSION_NUMBER)) {reason = "Wrong version number of the recorded game"; throw new Exception();} // check version number
           }
          catch (Exception ex)
            {
             JOptionPane.showMessageDialog(thePane, "Sorry, unable to open and read from file.\n"+   // tell user if and why it failed
                                                 "The reason is:\n"
                                               + ex.toString() + "  " + reason,
                                               "File Error",
                                               JOptionPane.ERROR_MESSAGE);
             playback = null;
            }
        else                                    // user clicked on cancel
            playback=null;

         if (playback != null)              // if recorded game seems OK
          {thePane.removeAll();             // then make this fram invisible
           myFrame.setVisible(false);
           myFrame.pack();

           new PlayBack(this, playback);    // and start the playback!
          }
      }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //
    //  Client side methods: try to connect to server and set up game
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    //-------------------------------------------------------------------------------
    // Connect screen. User should type in an IP
    //-------------------------------------------------------------------------------

    private void startConnecting()
    {
     thePane.removeAll();       // clear screen
     thePane.setLayout(new BoxLayout(thePane, BoxLayout.Y_AXIS));

     JPanel adrPane = new JPanel();     // the panel where address should be input
     JLabel adrLabel = new JLabel("Type host name or IP");   // its label
     adrField = new JTextField(lastAddress,20);            // and text input field
     adrField.addActionListener(this);
     adrPane.setLayout(new BoxLayout(adrPane, BoxLayout.X_AXIS));  // build this panel
     adrPane.add(Box.createRigidArea(new Dimension(50,0)));
     adrPane.add(adrLabel);
     adrPane.add(Box.createRigidArea(new Dimension(50,0)));
     adrPane.add(adrField);
     adrPane.add(Box.createRigidArea(new Dimension(50,0)));
     adrPane.setBackground(Board.BACKGROUNDCOLOR);

     JPanel buttonPanel = new JPanel();                     // build a button panel with abort and quit
     buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
     buttonPanel.add(abortPanel);
     buttonPanel.add(quitPanel);

     JPanel statusPanel    = new JPanel();                  // build a text area explaining status
     TextArea statusArea = new TextArea(connectingStatus,5,35,TextArea.SCROLLBARS_VERTICAL_ONLY);
     statusArea.setEditable(false);
     statusPanel.setBackground(Board.BACKGROUNDCOLOR);
     statusPanel.add(statusArea);

     thePane.add(Box.createRigidArea(new Dimension(0, 50)));        // build the frame
     thePane.add(adrPane);
     thePane.add(Box.createRigidArea(new Dimension(0, 50)));
     thePane.add(buttonPanel);
     thePane.add(Box.createRigidArea(new Dimension(0, 50)));
     thePane.add(statusPanel);
     thePane.setBackground(Board.BACKGROUNDCOLOR);  // make sure background is right
     buttonPanel.setBackground(Board.BACKGROUNDCOLOR);

     myFrame.pack();                    // show it
     myFrame.setVisible(true);

    }


    //-------------------------------------------------------------------------------
    // We come here when User has typed an IP and now we should try to connect to it
    //-------------------------------------------------------------------------------



   private void tryConnecting()
   {
       if (testClient)
            {lastAddress = "localhost";
             testClient = false;
       }

       else
            {lastAddress = adrField.getText();            //remember what the user typed
             writeOptions();                              // also save it in the options file
             adrField.setEditable(false);
             }

       JLabel label = new JLabel("Trying to connect, please wait...");
       myFrame.repaint();                           // for some reason this never seems to happen

       boolean failed = false;                      // temp status variables
       String reason = "";

       if (outSocket != null) try {outSocket.close();} catch (Exception e){} // close any remaining outsocket
       try{outSocket = new Socket(lastAddress, PORT_NUMBER);                // set up the connection
           outgoing = new ObjectOutputStream (outSocket.getOutputStream());
           incoming = new ObjectInputStream (outSocket.getInputStream());
           }
         catch (Exception e) {failed = true;                        // if setting up connection failed
                              reason = e.toString();                // remember why
                              }

       if (failed)
          {JOptionPane.showMessageDialog(thePane, "Sorry, unable to connect.\n"+   // tell user if and why it failed
                                                   "Try again if you want\n" +
                                                   "The reason given by the network is:\n"
                                                   + reason,
                                                   "Connection Error",
                                                   JOptionPane.ERROR_MESSAGE);

          adrField.setEditable(true);                           // Let the useer try again
          label.setText("Type name or IP");
          myFrame.repaint();
         }

       else                 // connection did not fail
           isConnected();   // so proceed to the state where you are connected
       }

    //-------------------------------------------------------------------------------
    // Connection to opponent server successful. Now negotiate game start
    //-------------------------------------------------------------------------------

   private void isConnected()
     {
         // First show the 'is connected' screen


     thePane.removeAll();       // clear screen
     thePane.setLayout(new BoxLayout(thePane, BoxLayout.Y_AXIS));

     JPanel adrPane = new JPanel();            // the panel where address should be input
     JLabel adrlab = new JLabel("Successfully connected to "+lastAddress);     // its label
     adrPane.setLayout(new BoxLayout(adrPane, BoxLayout.X_AXIS));  // build this panel
     adrPane.add(Box.createRigidArea(new Dimension(50,0)));
     adrPane.add(adrlab);
     adrPane.add(Box.createRigidArea(new Dimension(50,0)));
     adrPane.setBackground(Board.BACKGROUNDCOLOR);

     JPanel buttonPanel = new JPanel();                     // build a button panel with abort and quit
     buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
     buttonPanel.add(abortPanel);
     buttonPanel.add(quitPanel);

     JPanel statusPanel    = new JPanel();                  // build a text area explaining status
     TextArea statusArea = new TextArea(connectedStatus,7,30,TextArea.SCROLLBARS_VERTICAL_ONLY);
     statusArea.setEditable(false);
     statusPanel.add(statusArea);

     thePane.add(Box.createRigidArea(new Dimension(0, 50)));        // build the frame
     thePane.add(adrPane);
     thePane.add(Box.createRigidArea(new Dimension(0, 50)));
     thePane.add(buttonPanel);
     thePane.add(Box.createRigidArea(new Dimension(0, 50)));
     thePane.add(statusPanel);

     thePane.setBackground(Board.BACKGROUNDCOLOR);  // make sure background is right
     buttonPanel.setBackground(Board.BACKGROUNDCOLOR);
     statusPanel.setBackground(Board.BACKGROUNDCOLOR);

     myFrame.pack();                    // show it
     myFrame.setVisible(true);

     // Tell opponent I want to play and my params

       String reason="";            // temp status varaibles

       boolean failed = false;

       playWhite = new java.util.Random().nextFloat() < 0.5;  // randomly select my color for play

       try{
            outgoing.writeObject("Game of control");       // First message to say what is going on
            outgoing.writeObject(                          // then tell my params
              new GameParameters(Control.VERSION_NUMBER, graphics, isLite, withHills, nick, shape, boardShape, speed, width, height, pebbles, length));

            outgoing.writeObject(playWhite ? "I play white" // tell who plays white
                                    : "You play white");

            getAccept = new GetAccept();  // start a new thread to listen for reply
            getAccept.start();
           }
       catch (Exception e){failed = true; reason = e.toString();   // if IOfailure remember why
             }
         if (failed)
           {JOptionPane.showMessageDialog(thePane, "Sorry, unable to start game\n"+    // Tell if and why it failed
                                               "Try again if you want\n" +
                                               "The reason  is:\n"
                                               + reason,
                                               "Connection Error",
                                               JOptionPane.ERROR_MESSAGE);

            welcome();                     // if failed then return to main screen
          }

   }

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        //
        // Inner class GetAccept: a thread to get a reply from a challenge
        //
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        private class GetAccept extends Thread
        {
            private boolean aborted = false;
            private boolean failed = false;

            private String reply, reason;


            ///---------------------------------  To make me stop-------------------------------------


            void abort()
              {aborted = true;
              }


             ///--------------------------------- Start of this thread---------------------------------

            @Override
            public void run()

              {try
               {reply = (String)incoming.readObject();}                     // try reading something from opponent
                catch (Exception e){failed = true; reason = e.toString();}  // if it failed remember why

                if (reason == null) reason = reply;                         // default fail reason is a text transmitted by opponent ( eg mismatch params)

                if (!aborted)                                               // only continue if I should
                 if (failed || !reply.equals("OK Game of control"))         // if something amiss then inform user
                    {JOptionPane.showMessageDialog(thePane, "Game refused\n" +
                                                            "The contact would not start the game.\n" +
                                                           "The reason is:\n"+
                                                           reason
                                                        ,
                                                           "Game Error", JOptionPane.ERROR_MESSAGE);
                      abortIt();                                         // and tell my parent to stop this attempt


                     }
                 else // read did not fail and game seems OK

                   {try
                       {opponentNick = (String)incoming.readObject();}    // read opponent's nick
                    catch (Exception e) {failed = true; reason = e.toString();} // if that fails I just ignore it
                    startGame();                                           // in any case tell parent to go ahead with game
                  }  // end else
              }
            }

    //-------------------------------------------------------------------------------
    // All is ready, so start the game. We come here when getAccept invokes the method
    // after having receivved an accept to play from opponent
    //-------------------------------------------------------------------------------

    private void startGame()
    {
     thePane.removeAll();               // get rid of the frame
     myFrame.setVisible(false);
     myFrame.pack();

     ControlTimer controlTimer = new ControlTimer(playWhite);       // get new control timer for the player

     if (record != null)                // if recording then write game params on the record
         try {record.writeObject(
             new RecordedGame(Control.VERSION_NUMBER,  isLite, withHills, graphics, playWhite?nick:opponentNick, playWhite?opponentNick:nick,
                                shape, boardShape, width,  height, pebbles, length));}
          catch (Exception e)
               {JOptionPane.showMessageDialog(thePane, "Sorry, unable to record.\n"+   // if this fail tell user
                                               "The reason  is:\n"
                                               + "no response for five seconds",
                                               "File Error",
                                               JOptionPane.ERROR_MESSAGE);
                record = null;                                                          // and cease trying to record
                }

     Player player = new Player(isLite, withHills, playWhite, controlTimer, incoming, outgoing, record, graphics, shape, boardShape,
                                height, width, pebbles, speed, length);                  // Set up the player


     new PlayerInterface (player, controlTimer, this, nick, opponentNick);              // and the interface

    }


    //-------------------------------------------------------------------------------
    // Come here when a game has ended
    //-------------------------------------------------------------------------------

    void playAgain()
    {
    abortIt();          // release everything and go to main screen
    }

    //-------------------------------------------------------------------------------
    //  Come here when you press "abort" instead of accepting to play
    //-------------------------------------------------------------------------------


    private void decline() {
        try {
            outgoing.writeObject("Opponent declines to play");   // Inform the opponent that you decline
        } catch (Exception ex) {}
        abortIt();
    }



    //-------------------------------------------------------------------------------
    // Release everything and go to main screen
    //-------------------------------------------------------------------------------


    private void abortIt()

        {      if (getAccept != null) getAccept.abort();            // abort any threads listening for communication
               if (getConnection != null) getConnection.abort();
               getAccept = null;
               getConnection = null;

               try{
               if (incoming != null) incoming.close();              // close all streams
               if (inSocket != null) inSocket.close();
               if (outgoing != null) outgoing.close();
               if (connection != null) connection.close();
               if (outSocket != null) outSocket.close();
               } catch (Exception ex){}


               outgoing = null;
               incoming = null;
               connection = null;
               outSocket = null;
               inSocket=null;

               if (record != null) try {record.close();} catch (Exception e){}
               record = null;

               welcome();                                           // go to main screen
           }


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //
    //  Server side methods: listen for a connection where to play the game
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++



    //-------------------------------------------------------------------------------
    // Set up listening screen
    //-------------------------------------------------------------------------------


    private void startListening()
    {
      thePane.removeAll();                       // clear screen
      thePane.setLayout(new BoxLayout(thePane, BoxLayout.Y_AXIS));


       JPanel buttonsPanel   = new JPanel();
       buttonsPanel.setLayout(new GridLayout(3,2,20,40));
       buttonsPanel.add(abortLabel);                     // set up the buttons
       buttonsPanel.add(abortPanel);
       buttonsPanel.add(quitLabel);
       buttonsPanel.add(quitPanel);
       if (ipLabel != null) buttonsPanel.add(ipLabel);

       JPanel statusPanel    = new JPanel();
       TextArea statusArea = new TextArea(listenStatus,7,40,TextArea.SCROLLBARS_VERTICAL_ONLY);    // message about listening
       statusArea.setEditable(false);
       statusPanel.add(statusArea);

       thePane.add(Box.createRigidArea(new Dimension(0, 30)));
       thePane.add(buttonsPanel);                                   // set up the screen
       thePane.add(Box.createRigidArea(new Dimension(0, 30)));
       thePane.add(statusPanel);

       thePane.setBackground(Board.BACKGROUNDCOLOR);        // make sure background is OK
       buttonsPanel.setBackground(Board.BACKGROUNDCOLOR);
       statusPanel.setBackground(Board.BACKGROUNDCOLOR);

       myFrame.pack();                  // show screen
       myFrame.setVisible(true);
       myFrame.invalidate();
       myFrame.repaint();


       getConnection = new GetConnection();                        // Listen for connection in a new thread!
       getConnection.start();                                      // The reason is that startListening is invoked by
                                                                   // a button click and therefore runs on the
                                                                   // AWT thread. We must NEVER put this thread to sleep,
                                                                   // so it cannot wait at an input.
    }

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        //
        // Inner class GetConnection: a thread which tries to establish connection on the server side
        //
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        private class GetConnection extends Thread

             {

              private boolean aborted = false;  // to kill me off


              /// --------------------------------- Method to stop this thread


              void abort(){aborted=true;}

              ///--------------------------------- Start of this thread

              @Override
              public void run()
              {
                   boolean  failed  = false;            // temp status variables
                            aborted = false;

                   String  reason = "";
                   GameParameters gp = null;

                   if (inSocket == null)                                  // only if we have not done this before
                     {try {inSocket = new ServerSocket(StartControl.PORT_NUMBER);}     // create a server socket
                      catch (Exception e)
                          {failed = true;                                 // if that does not work tell user

                           JOptionPane.showMessageDialog(thePane, "Sorry, unable to open server\n"+
                                                            "It seems you have another server listening at this port\n" +
                                                           "Try again if you want, by pressing Abort\n"+
                                                           "The reason given by the network is:\n"+
                                                           (e.toString()),
                                                           "Server Error", JOptionPane.ERROR_MESSAGE);

                          }
                   }

                    if (!failed & !aborted)   // Server created successfully

                     {
                      try                       // try to set up connection
                        {
                            failed = false;
                            connection = inSocket.accept();         // get connection and streams
                            incoming = new ObjectInputStream(connection.getInputStream());
                            outgoing = new ObjectOutputStream(connection.getOutputStream());

                            if (!((String)incoming.readObject()).equals("Game of control"))  // check if it is someone who wants to play
                                {failed = true; reason = "someone made contact but not to play Control";}

                             gp = (GameParameters)(incoming.readObject());

                             if (!gp.versionNumber.equals(Control.VERSION_NUMBER))
                                 {failed = true; reason = "Wrong version of the game";}   // check that we all agree on game parameters
                             if (gp.graphics != graphics)
                             {failed=true; reason="Different graphic sizes";}
                             if (gp.shape != shape)
                                  {failed = true; reason = "Mismatching area shapes";}
                             if (gp.boardShape != boardShape)
                                  {failed = true; reason = "Mismatching board shape";}
                             if (gp.isLite != isLite)
                                 {failed = true; reason = "Only one of us wants ControLITE";}
                             if (gp.withHills != withHills)
                                 {failed = true; reason = "Only one of us wants to play with hills";}
                             if (gp.width != width)
                                  {failed = true; reason = "Mismatching board width";}
                             if (gp.height != height)
                                  {failed = true; reason = "Mismatching board height";}
                             if (gp.pebbles != pebbles)
                                  {failed = true; reason = "Mismatching number of pebbles";}
                             if (gp.speed != speed)
                                  {failed = true; reason = "Mismatching game speed";}
                             if (gp.length != length)
                                  {failed = true; reason = "Mismatching game length";}

                            Object o = incoming.readObject();                               // opponent decides who should play white

                            if (((String)o).equals("I play white")) playWhite = false;
                                else
                                if (((String)o).equals("You play white")) playWhite = true;
                                    else {failed = true; reason = "someone contacted me but then quit";}

                          }
                       catch (Exception e) {failed = true; reason = e.toString();}      // if IO failed remember why

                     if (failed &!aborted )        // if failed tell opponent why
                        {JOptionPane.showMessageDialog(thePane, "Incoming connection refused\n" +
                                                            "Someone tried to make contact but we could not start the game.\n" +
                                                           "The reason is:\n"+
                                                           reason,
                                                           "Game Error", JOptionPane.ERROR_MESSAGE);
                         try {outgoing.writeObject(reason);} catch (Exception e){}
                         finally {abortIt();}
                         }

                       }

                     if (!failed & !aborted)
                         {opponentNick = gp.nick;
                                                    // all is fine, so
                         checkGame();                // let user decide if user wants to play
                          }
              } // end run


            }


    //-------------------------------------------------------------------------------
    // Screen where user decides whether to play an opponent. We come here when getConnection
    // invokes it after receiving a connect request.
    //-------------------------------------------------------------------------------


    private void checkGame()

     {
       thePane.removeAll();              // clear screen
       thePane.setLayout(new BoxLayout(thePane,BoxLayout.Y_AXIS));

       String hostname = connection.getInetAddress().getHostName();   // host name of remote opponent
       JLabel hostLabel;
       if (opponentNick.equals(""))
             hostLabel = new JLabel("Anonymous opponent wants to play from "+hostname,SwingConstants.CENTER);  // label displaying the name
       else  hostLabel = new JLabel(opponentNick+" wants to play from  "+hostname,SwingConstants.CENTER);  // label displaying the name
       hostLabel.setBackground(Board.BACKGROUNDCOLOR);
       JPanel hostPanel = new JPanel();
       hostPanel.add(hostLabel);
       hostPanel.setBackground(Board.BACKGROUNDCOLOR);

       JPanel buttonsPanel   = new JPanel();
       buttonsPanel.setLayout(new GridLayout(3,2,20,40));
       buttonsPanel.add(playLabel);
       buttonsPanel.add(playPanel);
       buttonsPanel.add(abortLabel);
       buttonsPanel.add(abortPanel);
       buttonsPanel.add(quitLabel);
       buttonsPanel.add(quitPanel);

       JPanel statusPanel    = new JPanel();
       TextArea statusArea = new TextArea(acceptingstatus,5,40,TextArea.SCROLLBARS_VERTICAL_ONLY);
       statusArea.setEditable(false);
       statusPanel.add(statusArea);

       thePane.add(Box.createRigidArea(new Dimension(0, 50)));
       thePane.add(hostPanel);
       thePane.add(Box.createRigidArea(new Dimension(0, 50)));
       thePane.add(buttonsPanel);
       thePane.add(Box.createRigidArea(new Dimension(0, 50)));
       thePane.add(statusPanel);

       thePane.setBackground(Board.BACKGROUNDCOLOR);
       buttonsPanel.setBackground(Board.BACKGROUNDCOLOR);
       statusPanel.setBackground(Board.BACKGROUNDCOLOR);


       myFrame.pack();
       myFrame.setVisible(true);

       if (testServer)
           {testServer = false;
            playGame();
       }
    }

    //-------------------------------------------------------------------------------
    // User has pressed Play, so tell opponent and get going
    //-------------------------------------------------------------------------------

    private void playGame()
    {    boolean failed = false;            //  temp

         try {outgoing.writeObject("OK Game of control");
              outgoing.writeObject(nick);    }   // tell opponent I am willing to play, and my nick
            catch (Exception e)
            {                                       // Oops, opponent dropped out
                failed = true;
                    JOptionPane.showMessageDialog(thePane, "Game start failed\n" +
                                                "Connection to your opponent disappeared\n" +
                                               "The reason given by the network is:\n"+
                                               e.toString() ,
                                               "Game Error", JOptionPane.ERROR_MESSAGE);
                    abortIt();
            }
         if (!failed) startGame();      // all is well so game starts
    }



    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //
    //  Action listener
    //
    //    Here is defined what happens when buttons are pressed
    //
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == quitButton)                    // quit: close server and leave
           {if (inSocket != null) try {inSocket.close();}  catch (Exception ex) {}
               System.exit(0);}

        if (e.getSource() == connectButton) startConnecting();

        if (e.getSource() == listenButton) startListening();

        if (e.getSource() == abortButton)  decline();

        if (e.getSource() == recordButton) recordGame();

        if (e.getSource() == playbackButton) playBack();

        if (e.getSource() == optionsButton) options();

        if (e.getSource() == defaultsButton) defaults();

        if (e.getSource() == returnButton) exitOptions();

        if (e.getSource() == playButton) playGame();

        if (e.getSource() == adrField) tryConnecting();

    }

}   // Here ends class StartControl

 //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


//---------------------------------------------------------------------------------
// Wrapper for game parameters transmitted between players
//---------------------------------------------------------------------------------


class GameParameters implements Serializable {

       String versionNumber;        // game version number

       // game params chosen in options

       boolean isLite;              // true if controlite, false if control
       boolean withHills;           // with hills on board
       int graphics;                // graphics size
       String nick;                 // nick of sender
       int shape;                   // area shape
       int boardShape;              // board shape
       int speed;                   // game speed
       int width;                   // board relative width
       int height;                  // board relative height
       int pebbles;                 // pebbles per player
       int length;                  // game length

   //---------------- constructor just sets the fields-------------------------------


       public GameParameters(String v, int gr, boolean il, boolean wH, String n, int g,
                             int bS, int s, int w, int h, int p, int l)
           {versionNumber=v; graphics=gr; isLite=il; withHills = wH; nick=n; shape = g;
            boardShape = bS; speed=s; width=w; height=h; pebbles=p; length=l;
           }
    }


//---------------------------------------------------------------------------------
// Wrapper for game parameters on a recorded game
//---------------------------------------------------------------------------------

  class RecordedGame implements Serializable

      {String versionNumber;        // game version number
       boolean isLite;              // true if controlite, false if control
       boolean withHills;           // with hills on board
       int graphics;                // graphics size
       String whiteNick;            // white's nick
       String blackNick;            // black's nick
       int shape;                   // area shape
       int boardShape;              // board shape
       int width;                   // game params
       int height;                  // board relative height
       int pebbles;                 // pebbles per player
       int length;                  // game length

        //---------------- constructor just sets the fields-------------------------------

       public  RecordedGame (String v, boolean il, boolean wH, int gr, String w, String b,
                             int g, int bS, int wi, int h, int p, int l)
       {versionNumber = v;  isLite=il; withHills = wH; graphics = gr; whiteNick = w; blackNick = b;
        shape = g; boardShape = bS; width = wi; height = h; pebbles = p; length = l;}
      }





/**
 *+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *
 * ControlTimer
 *
 * This is the timer for determining when a player is allowed to move
 *
 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */

 class ControlTimer extends JPanel implements ActionListener

{

   private static final Color BACKGROUNDCOLOR =    new Color(160,180,120);   // Background color for the timer (currently same as for board)

    // Various sizing constants for graphics


   private static final int TOP_OFFSET = 0;              // Offset from top border
           static final int BAR_HEIGHT = 25;             // timer height
   private int LEFT_OFFSET;                              // Offset from left border
   private int BAR_LENGTH;                               // timer bar length

   private final int UPDATE_FREQUENCY = 20;                    // how often to update in milliseconds


   private Timer timer;                                                            // The timer

   private Player player;                                                          // Whom to tell when timer expires

   private boolean whiteColor;                                                     // true if timer should be painted white

   private float remainingFraction = 0f;                                           // remaining fraction of time

   private long startTime;                                                         // Epoch time in ms when timer started

   private int totalTime;                                                          // total  time in ms

   private int size, width;                                                        // board parameters (needed so the timer will fit nicely)


 //------------------------------------------------------------------------------------
 //  Create a controlTimer. Just done once for each player.
 //------------------------------------------------------------------------------------

     ControlTimer(boolean whiteColor) {
        super();                            // this is also a JPanel
        this.whiteColor = whiteColor;
    }

 //------------------------------------------------------------------------------------
 //  Accept a message from my Player to initiate variables
 //------------------------------------------------------------------------------------

     void getPlayer(Player player,    // my player
                          int size,         // size of one board square in pixels
                          int width)        // board width in number of squares
    {                                       // This is how the player tells me who he is so I can report timeouts
      this.player = player;
      this.size   = size;                       // he also tells me how big the board is
      this.width  = width;                     // so I set the sizing constants from this
      LEFT_OFFSET = size;
      BAR_LENGTH  = size*width;
    }

 //------------------------------------------------------------------------------------
 //  Paint the timer
 //------------------------------------------------------------------------------------

    @Override
    public void paintComponent(Graphics page)
    {super.paintComponent(page);                // This is also a JPanel

     page.setColor(BACKGROUNDCOLOR);        // Paint background
     page.fillRect(0,0, size*(width+2), 30);

                          // Paint timer bar

     if (whiteColor) page.setColor(Color.white); else page.setColor(Color.black);
     page.fillRect(LEFT_OFFSET, TOP_OFFSET,
              (int)(remainingFraction*BAR_LENGTH),
              BAR_HEIGHT);
    }

//----------------------------------------------------------------------
// Start timer with totalTime in seconds
//-----------------------------------------------------------------------


    void startIt(float totalTime)
    {
    startTime = System.currentTimeMillis();                         // remember when it started
    remainingFraction = 1f;                                         // at start all the time is remaining
    this.totalTime = (int)(1000*totalTime);                         // total time in ms
    timer = new Timer(UPDATE_FREQUENCY, this);                      // create a new Timer reporting to me
    repaint();                                                      // draw me, for the first time

    timer.start();                                                  // start the timer
    }

//----------------------------------------------------------------------
// Interrupt from the timer
//-----------------------------------------------------------------------

    public void actionPerformed(ActionEvent e)

    {
     remainingFraction = 1 - (float)(System.currentTimeMillis()- startTime)/(float)totalTime; // Fraction of total time that remains
     if (remainingFraction < 0) remainingFraction = (float)0;                                 // (should neve be negative)

     if (remainingFraction<=0) {                                        // If finished then stop timer and notify player
         timer.stop();
         repaint();
         player.timeOut();
     }
     else repaint();                                                    // OW just repaint the timer bar


    }


//----------------------------------------------------------------------
// Stop the timer
//-----------------------------------------------------------------------

    void stopIt()
    {if (timer != null) timer.stop();
    }


}


/*
************************************************************************************************
 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *Create the graphic interface for a player:
 *set up a frame with panels
 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 ***********************************************************************************************
 */

 class PlayerInterface implements ActionListener

{
   private StartControl startControl;                                    // Who started me and to whom I report termination

   private JFrame             myFrame;                                   // my Frame
   private final JPanel       tops           = new JPanel();             // top panel will hold the quit button and player ids
   private final JPanel       quitPanel      = new JPanel();             // part of topside to hold quit button
   private final JPanel       bottoms        = new JPanel();             // bottom panel will hold the control timer
   private final JButton      quitButton     = new JButton("Quit");      // the quit button

   private Player       player;                                    // my player

                                                        // various temp variables.
   private boolean running = true;                                 //  true if player is running.
   private boolean noids;                                          //  true if neither player has a nick

   private String whiteNick, blackNick;                            //  nick of white and of black resp.

    //----------------------------------------------------------------------------------------------------------------------
    // Constructor does most of the work to set things up
    //----------------------------------------------------------------------------------------------------------------------

   PlayerInterface(Player       player,         // Who will actually play the game
                   ControlTimer timer,          // and its control timer. These are the ones I should display properly.
                   StartControl startControl,   // Parent to which I report termination
                   String       nick,           // nick my player uses
                   String       opponentNick)   // nick the opponent uses
    {

       this.player = player;                    // begin by remembering parameters
       this.startControl = startControl;

       myFrame = new JFrame("Playing the game of Control" +
                                       (player.isLite ? "ite" : ""));


       myFrame.getContentPane().setLayout(new BorderLayout());       // with borderlayout manager
       myFrame.getContentPane().setBackground(Board.BACKGROUNDCOLOR);// and proper background

       myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);       // Ow process continues when window closes

       bottoms.setLayout(new BoxLayout(bottoms,  BoxLayout.X_AXIS));     // Bottom region reserved for the timer
       bottoms.add(Box.createRigidArea(new Dimension(0,ControlTimer.BAR_HEIGHT)));   // (ow the layout manager will reserve no space)
       bottoms.add(timer);

       myFrame.getContentPane().add (player);                           // central region for the board
       myFrame.getContentPane().add(bottoms,BorderLayout.SOUTH);

       quitButton.setBackground(Color.red);                             // put the quit button topside
       quitPanel.add(quitButton);
       quitPanel.setBackground(Board.BACKGROUNDCOLOR);
       tops.setLayout(new BoxLayout(tops,BoxLayout.Y_AXIS));
       tops.add(quitPanel);
                                                                        // topside also get to print player nicks
                                                                        // unless neither player has a nick
       tops.add(Box.createRigidArea(new Dimension(0, 20)));

       if (player.playWhite)                                            // set whitenick and blacknick
         {whiteNick = nick; blackNick = opponentNick;}
       else
         {whiteNick = opponentNick; blackNick = nick;}

       noids =  ((whiteNick.equals(""))&(blackNick.equals("")));        // set noids true iff neither player has a nick

       if (whiteNick.equals("")) whiteNick="White";                     // default nicks for white and black
       if (blackNick.equals("")) blackNick="Black";

       if (!noids) tops.add(buildIdPanel());     // display the nicks in idPanel unless both empty

       quitButton.addActionListener(this);                              // I listen for quit button myself
       myFrame.getContentPane().add(tops,BorderLayout.NORTH);
       tops.setBackground(Board.BACKGROUNDCOLOR);

       myFrame.pack();
       myFrame.setVisible(true);
       focus();

       player.setInterface(this);        // tell my player who I am, so it can report game termination properly

    }

    //------------------------------------------------------------------------
    // Strangely, this is necessary in order to let the player listen for key actions
    //------------------------------------------------------------------------

    private void focus()
    { player.requestFocus();
    }

     //------------------------------------------------------------------------
    // Build a panel displaying player nicks and add it to tops
    //------------------------------------------------------------------------

    private JPanel buildIdPanel()
    {
            JPanel idPanel = new JPanel();
            idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.X_AXIS));

            JLabel whiteLabel = new JLabel(whiteNick);
            whiteLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
            whiteLabel.setForeground(Color.white);
            whiteLabel.setBackground(Board.BACKGROUNDCOLOR);

            JLabel vsLabel = new JLabel("     vs      ");
            vsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            vsLabel.setForeground(Color.orange);
            vsLabel.setBackground(Board.BACKGROUNDCOLOR);

            JLabel blackLabel = new JLabel(blackNick);
            blackLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
            blackLabel.setForeground(Color.black);
            blackLabel.setBackground(Board.BACKGROUNDCOLOR);

            idPanel.add(whiteLabel);
            idPanel.add(vsLabel);
            idPanel.add(blackLabel);
            idPanel.setBackground(Board.BACKGROUNDCOLOR);

            return idPanel;

    }
    //-----------------------------------------------------------------------
    // When game has ended the display needs change
    //-----------------------------------------------------------------------

    void endGame(String reason,               // reason to present to the user
                       String recordReason)         // reason to record on file if game is recorded

   { //  Player.releaseLock();
       myFrame.getContentPane().remove(tops);       // perhaps unnecessary since borderlayout is used in myFrame
       tops.removeAll();                            //  but the topside area needs to be redone
       tops.setLayout(new BoxLayout(tops,  BoxLayout.Y_AXIS));

       JLabel reasonLabel = new JLabel(reason, SwingConstants.CENTER);     // message to user upon termination
       reasonLabel.setBackground(Color.orange);
       reasonLabel.setFont(new Font("SansSerif", Font.BOLD, 24));


       tops.add(Box.createRigidArea(new Dimension(0,20)));   // display this message and the quit button topside
       tops.add(quitPanel);
       tops.add(Box.createRigidArea(new Dimension(0,20)));
       tops.add(reasonLabel);
       tops.add(Box.createRigidArea(new Dimension(0,20)));

       if (!noids) tops.add(buildIdPanel());        // display player nicks

       myFrame.getContentPane().add(tops,BorderLayout.NORTH);   // put on display

       player.endGame(recordReason);                // tell my player to clean up

       myFrame.pack();                              // show it
       myFrame.repaint();
       running = false;
   }

   //---------------------------------------------------------------------------------
   // Termination. get rid of the frame and tell my parent
   //---------------------------------------------------------------------------------

     private  void quit()
       {   myFrame.dispose();
           startControl.playAgain();
       }


    //--------------------------------------------------------------------------------
    // I am listening for actions myself
    //---------------------------------------------------------------------------------


    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == quitButton)    // if quit button is pressed
        {if (running) player.quit();        // if player is running then player has to quit
         else quit();}                      // ow I have to quit
    }

}    //--------------------------- end of player interface -------------------------------------------------



 /*
 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *
 *Main class in the game of control
 *
 *Serves one player with game logics
 *Reacts to player mouse commands , sends moves to opponent,
 * and reacts to to moves received by opponent
 *
 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *
 */



class Player extends JPanel implements ActionListener
{


 //----------- player activity state ---------------------------------------------------------------------

   private Piece currentPiece = null;                             // Piece over which mouse was pressed
   private Piece currentSelection = null;                         // Selection for promotions
   private Board.Position currentPos;                             // where the mouse is
   private boolean waiting;                                       // True if Timer not expired so I must wait
   private boolean clockHasExpired = false;                       // true when game clock has expired and game is over

 //----------- game activity state -----------------------------------------------------------------------

           int clock = 0;                                         // elapsed time in seconds

   private int messageNumber = 0;                                 // number communication messages incrementally
   private int ackedNumber = -1;                                  // last acknowledged message to opponent

 //----------- game parameters (should not change during a game) -----------------------------------------

           boolean playWhite;                                     // true if currently playing white

   private Board board;                                          // the board for this player

   private ControlTimer controlTimer;                            // my Controltimer
   private PlayerInterface myInterface;                          // my Interface (runs the quit button etc)
   private MoveListener moveListener;                            // My listener for moves by opponent

   private Timer timer;                                          // oneSecondTimer for the game clock

   private ObjectOutputStream outgoing;                          // for sending moves to opponent
   private ObjectInputStream incoming;
   private ObjectOutputStream record;                            // stream to recording file

   private int GAME_LENGTH;                                      // Game length in seconds
   private float timeBase;                                       // number of seconds for one time unit (nimbler move)

   boolean isLite;                                              // true if playing controlite
   boolean withHills;                                           // true if hills on the board

   private Semaphore mutex;                              // This protects critical sections updating game states

   private void waitLock()                                        // Semaphore wait
       { mutex.acquireUninterruptibly();}

   private void releaseLock()                                     // Semaphore signal
       {mutex.release();}


//------------------------------------------------------------------------------------------------------------
// Player constructor
//------------------------------------------------------------------------------------------------------------

                                                // Constructor parameters:

     Player(     boolean isLite,                 // true if playing controlite
                 boolean withHills,              // true if playing with Hills
                 boolean playwhite,              // color for this player (true => white)
                 ControlTimer controlTimer,      // my controlTimer
                 ObjectInputStream incoming,     // streams to and from opponent
                 ObjectOutputStream outgoing,
                 ObjectOutputStream record,      // if not null then the stream where games are recorded
                 int graphics,                   // graphic size parameter
                 int shape,                      // shape of controlled areas
                 int boardShape,                 // shape of board
                 int height,                     // Game parameters:
                 int width,                      //   board size
                 int pebbles,                    //   inital number of pebbles
                 int speed,                      // speed choice (1=fast, 2=normal, 3=slow)
                 int length)                     // game length in minutes


   {

    this.controlTimer = controlTimer;   // remember my controlTimer
    this.playWhite = playwhite;         // remember if I play for white
    this.outgoing = outgoing;          // remember my streams to  opponent and to recording file
    this.incoming = incoming;
    this.record = record;
    this.isLite=isLite;
    this.withHills = withHills;

    waiting = true;                    // initially, you cannot move!

    board = new Board(isLite, withHills, graphics, shape, boardShape, height, width, pebbles, playwhite);         // get me a board


    board.allVisible = false;                                      // Initially not all is visible

    GAME_LENGTH = length*60;                                      // GAME_LENGTH is in seconds

    timeBase = (float)((speed == 1) ? 1 : (speed == 2) ? 2 : 4);  // set timeBase based on choice of speed
    if (Control.testSetup) timeBase /= 10;

    add(Box.createRigidArea(new Dimension(board.SIZE*(board.WIDTH+2) -10,   // crazy, but need to tell layout manager how big I am
                                board.SIZE*(board.HEIGHT+2)-10)));


    mutex = new Semaphore(1);                               // Critical sections protects updates of game state

    ControlListener myListener = new ControlListener(this);  // Add my inner class to the listeners (gets moves from mouse)
      addMouseListener (myListener);
      addMouseMotionListener (myListener);
      addKeyListener(myListener);
     
    controlTimer.getPlayer(this, board.SIZE, board.WIDTH);       // Tell my Timer who I am
    controlTimer.startIt(2f);                                    // initial countdown: two seconds


    timer = new Timer(1000, this);                                 // Timer for the game clock
    timer.start();

    board.calculateControl();                                      // Initial computation of square states
    currentSelection = board.defaultselector();                    // and of selector
    board.generateBoardGraphics(null);                             // and paint the board




   };


   //--------------------------- End of Constructor ------------------------------------------

   //--------------------------------------------------------------------
   // Method to accept identity of my interface (only done once, in the setup phase)
   // Here are also generated the Hills, for a white player
   //--------------------------------------------------------------------

    void setInterface(PlayerInterface i)
    {myInterface=i;                                 // save my interface
       if (withHills & playWhite)                   // if hills are used and I play white
          {Board.Hill [] hills = board.generateHills();  // then let my board generate the hills
          try
             {outgoing.writeObject(hills); }      // send them to opponent
          catch (Exception e)                    // if impossible then report an error
              {communicationError(e);}
            recordMessage(hills);                 // anyway, record them on the recording
           }

    moveListener = new MoveListener();               // start listening for moves by opponent
    moveListener.start();

    }


   //-----------------------------------------------------------------
   //  Draws board, and possibly an outline because of a mouse position etc
   //-----------------------------------------------------------------


   @Override
   public void paintComponent (Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

   board.paint(g,                                          // Draw the board
                waiting | ackedNumber != messageNumber-1,   // signal move possible if this is true
                (ackedNumber != messageNumber -1 & !waiting) & !board.allVisible,  // Show stall  if oneSecondTimer has expired and unacked messages,
                 GAME_LENGTH - clock                        // remaining time

                 );

     if (currentPiece != null)                               // show if mouse dragged over legal possibility
         if (currentPiece.canMove(currentPos))
            currentPiece.outline.drawOutline(g, currentPos, Color.red); // if so, show a red outline of it there

     if (currentSelection != null && currentPiece == null)        // Highlight current selector if any
        {currentSelection.drawOutline(g, Color.red);

        if (!waiting && currentPos != null && board.isLegal(currentPos) &&
            currentSelection.canBuild(currentPos))              // Highlight mouse position if build possible
                 currentSelection.drawOutline(g, currentPos, Color.red);
     }
   }






   //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
   //
   // Game methods. Note that before calling any of these, the caller must first claim the mutex lock.
   //  That way, there will be no races between the thread that listens to player moves through the mouse
   //  and the thread that listens through opponent moves through the incoming stream.
   //
   //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

   //*************************************************************************
   //
   // Communication
   //
   //*************************************************************************

   //-----------------------------------------------------
   //
   //  getMove: methods to receive moves by opponent
   //
   //-----------------------------------------------------

    private void getMove(Board.Position s, Board.Position p)          // opponent moves from s to p
   {
     board.find(s).moveTo(p);        // just move the piece
     endOpponentMove();
   }


    private void getMove(Piece.Type pt, Board.Position p)         // Overloaded method: a build move at p of type pt
   {
    board.newPiece(pt, !playWhite, false, p);     // so place the new piece there
    endOpponentMove();
   }


   private void endOpponentMove()
   {
       board.calculateControl();      // recalculate and repaint
       board.calculateBuildable();
       appraise();
       checkCurrent();
       board.calculateHighlight(currentPiece);
       repaint();
   }

   //----------------------------------------------------------------
   //   set Current piece to null if it is not in control
   //----------------------------------------------------------------

   private void checkCurrent()
   {
       if (currentPiece != null && ! board.iControl(currentPiece))
           currentPiece = null;
       if (currentPos != null && ! board.iControl(currentPos))
           currentPos = null;
   }



   //----------------------------------------------------------
   //
   // Methods for sending move to opponent
   //
   //----------------------------------------------------------

    private synchronized void sendMessage(Message message)    // actually "synchronized" is probably not needed here bc of the mutex
   {

        try {
             outgoing.writeObject(message);      // send it to opponent
             }
        catch (Exception e)                // if impossible then report an error (unless I already quit)
          {if (message.messageType != Message.MessageType.QUIT) communicationError(e);}

   }


   //--------------------- Report a communication error -------------------------------------

   private void communicationError(Exception e)
    {
        myInterface.endGame("Lost Contact with Opponent",       // end of game because communication broken
                         "Contact between players broken");
      }



   //-----------------------------------------------------------------------------------
   // SendMove: builds a message out of the info in a move, and sends it using sendMessage
   //-----------------------------------------------------------------------------------

   private void sendMove(Message.MessageType mt,       // message type
                 Board.Position s,                  // source position
                 Board.Position p,                  // destination position
                 Piece.Type pt                  // piece type of build
                 )
   {
       Message message = new Message (mt,                   // message type
                                      clock,                // game clock at time of sending
                                      messageNumber++,      // attach and increase message number
                                      s,
                                      p,
                                      pt,
                                      playWhite,            // true if move done by white
                                      "");                  // unused string parameter
       sendMessage(message);
       if (message.messageType != Message.MessageType.QUIT) recordMessage(message);    // record the message (QUIT messages are recorded at another place)

   }


   //-----------------------------------------------------
   //
   //  tellMove: methods to send moves to opponent (uses sendMove)
   //
   //-----------------------------------------------------

   private void tellMove(Piece.Type pt, Board.Position p)             // tell a build move
   {sendMove(Message.MessageType.BUILD, null, p, pt);
   }

   private void tellClockHasExpired()                               // tell that the game clock expired
   {sendMove(Message.MessageType.EXPIRED,null,null,null);}

   private void tellMove(Board.Position s, Board.Position p)     // overloaded: tell a  move
   {sendMove(Message.MessageType.MOVE, s, p, null);
   }




   //------------------------------------------------------------------------
   //
   // Record a move if recording is on
   //
   //------------------------------------------------------------------------

    private void recordMessage(Object message)
   {
       if (record != null)                      // if recording is on:
           try
             {record.writeObject(message);}     // record the message
           catch
              (Exception e)                     // if this fails:
                {try {record.close();}          // close the file if I can
                     catch (Exception e2){}
                 record = null;                 // turn recording off
                                                // and inform user
                 JOptionPane.showMessageDialog(this,
                                                "Recording ceased.\n"+
                                               "The reason given by the system is:\n" +
                                               e.toString(),
                                               "File Error",
                                               JOptionPane.ERROR_MESSAGE);
                 }
   }




  //**************************************************************************
  //
  //    Methods to serve my user in making moves: determining effects of mouse actions
  //
  //**************************************************************************


   //-------------------------------------------------------------------------
   // test if piece at p can be set as current piece
   //-------------------------------------------------------------------------

   private boolean canSetCurrentPiece(Board.Position p)
   {Piece candidate = board.isLegal(p) ?       // get the candidate piece if on board
                         board.find(p)
                       : null;
    return (
           !waiting                             // if Timer still runs no piece can be set as current
        && candidate != null                    // x,y must be at a piece
        && candidate.whiteside == playWhite     // of my color
        && candidate.moveable                   // and moveable
        && board.iControl(p)                    // and under my control

           );
   }

   //-------------------------------------------------------------------------
   // set piece at p as current, if possible
   //-------------------------------------------------------------------------

   private void setCurrentPiece(Board.Position p)
       {if (canSetCurrentPiece(p))               // can you set it?
         currentPiece = board.find(p);            // if so set it
         else currentPiece = null;
       };



  //-----------------------------------------------------------------
  // Makes a move of currentPiece to p if possible (mutex claimed by caller)
  //-----------------------------------------------------------------

  private void attemptToMove(Board.Position p)
  {
       if (currentPiece != null && currentPiece.canMove(p))             // is it legal other move?
           {                                                            // yes, then do it:
            tellMove(currentPiece.pos, p);                              //   tell opponent my move
            currentPiece = currentPiece.moveTo(p);                      //   move it. Note the piece may change!
            endMove(currentPiece.moveTime*timeBase);                    //   end of move (note moveTo may determine move time)
           }
                                                                  // in any case, even if not legal move
        currentPiece = null;                                     // drop current piece and repaint
        board.calculateHighlight(null);

        repaint();

  }


  //-----------------------------------------------------------------
  // Executes a build of type currentSelection at p if possible.
  //-----------------------------------------------------------------

private void attemptToBuild(Board.Position p)
  {
      if (board.isLegal(p) && currentSelection.canBuild(p))              // Check it is on the board and ok to build
         {Piece newPiece = board.newPiece(currentSelection.pieceType, playWhite,true, p);  // build it
          tellMove(currentSelection.pieceType, p);                                // tell opponent what my move is
          endMove(newPiece.buildTime*timeBase);                                  // end of build move.
      }

     repaint();

   };


   //-----------------------------------------------------------------
   // End of move. StarttTimer to wait for specified time, recalculate etc
   //-----------------------------------------------------------------


   private void endMove (float delay)
      {
              waiting  = true;                                  // now I am waiting
              currentPiece = null;                              // reset current piece
              board.calculateControl();                         // Recalculate control
              board.calculateBuildable();
              currentSelection = board.defaultselector();       // recalculate default selector
              board.calculateHighlight(null);                   // and reset highlighted squares

              controlTimer.startIt(delay);                      // start my controlTimer

       }

   //-----------------------------------------------------------------
   // appraise: check if someone has won or if it is a draw. If so report it to my interface
   //-----------------------------------------------------------------

                                                            // Note: this is only done when receiving messages from opponent
                                                            // (either his moves or acks of my moves)


    private void appraise()
      {
          boolean iHaveControl = false;                       // will become true if I have control of a piece
          boolean opponentHasControl = false;                 // will become true if opponent has control of a piece


          for (Piece thePiece : board.pieces)     // for all pieces, check if controlled by owner
          {
             if (thePiece.myside && board.iControl(thePiece)) iHaveControl = true; // and record this
             if (!thePiece.myside && board.opponentControls(thePiece)) opponentHasControl = true;
          }

           if (!opponentHasControl & !iHaveControl)                        // report a draw because no one has control
                 myInterface.endGame("DRAW: No one controls  pieces",
                                     "DRAW: No one controls  pieces");

           else if (!opponentHasControl)                                // report a win because opponent cannot move
               myInterface.endGame("You win: opponent has no control",                         // (note: second param to endGame
                                    playWhite?"Black has no control":"White has no control");   // is what goes on a recording

           else if (!iHaveControl)                                // report a loss if I have no move
               myInterface.endGame("You lose: no control",
                                    !playWhite?"Black has no control":"White has no control");


      }



      //-----------------------------------------------------------------
      // interrupt from game clock oneSecondTimer. Just update clock and repaint. Check if time limit exceeded.
      //    If so report to my interface
      //-----------------------------------------------------------------

      public void actionPerformed(ActionEvent e)   // game clock is the only action event that can happen here

       {
       waitLock();
       clock++;                                             // increase clock
       repaint();
       if (clock >= GAME_LENGTH)                            // If clock has expired
         {                                                 // then set this flag and
          waiting = true;
          clockHasExpired = true;                             // send a message to opponent
          clock = GAME_LENGTH;                              // Don't let clock exceed game length
          tellClockHasExpired();                              // done under the mutex
         }
       releaseLock();
      }

      //------------------------------------------------------------------
      // endGame: Game has ended. Make board show all and disable moves
      //   NOTE this will be called from my interface and not from myself!!
      //------------------------------------------------------------------

       void endGame(String recordReason)              // parameter is what will go on a recording of the game
      {
          controlTimer.stopIt();        // stop the Control Timer
          timer.stop();                 // and the clock oneSecondTimer

          if (moveListener != null) moveListener.abortIt();       // don't listen for moves any more
          currentPiece = null;
          currentSelection = null;
          currentPos = null;
          waiting = true;               // make sure no more move can be done
          board.allVisible = true;            // final display shows all aquares

          board.calculateControl();
          board.calculateHighlight(null);
          currentSelection = null;



          recordMessage(new Message(Message.MessageType.QUIT, clock, messageNumber,    // Here is where quit messages are recorded
                                    null,null,null, playWhite, recordReason));    // (only the last parameter will be important)

          try {record.close();} catch (Exception e2){} // don't record any more
          record = null;

          repaint();                    // and show final position
      }



      //------------------------------------------------------------------
      // Quit. This is what happens when the player presses the quit button
      //------------------------------------------------------------------

     void quit()

      {
          sendMessage(new Message (Message.MessageType.QUIT, clock, messageNumber, null,null,null, playWhite, ""));  // Tell opponent I resigned

          myInterface.endGame("You resigned", playWhite?"White resigned":"Black resigned");       // and tell my interface
      }



   //-------------------------------------------------------------------------
   // Control Timer expired so moving is now possible!
   //-------------------------------------------------------------------------

    void timeOut(){
       waiting = false;             // not waiting any more
       repaint();                   // repaint to show this!
   };






   //*****************************************************************
   //
   // Inner class:
   //
   //  Represents the listner to mouse and key actions
   //
   //*****************************************************************

   private class ControlListener implements MouseListener, MouseMotionListener, KeyListener
   {
       char lastkey;
       String keyword="stspwns";
       char[] charsinput = new char[keyword.length()];
       char[] keyWord=keyword.toCharArray();
       Player myplayer;

    ControlListener(Player player){
       myplayer=player; 
    }
    //---------------------------------------------------------------------
    // Mouse click
    //---------------------------------------------------------------------

      public void mouseClicked (MouseEvent event)

     {
        //----------------- here mutex is claimed ------------------------
       waitLock();

       int x = event.getX();
       int y = event.getY();

       Board.Position p = board.realPosition(x,y);      // translate to board coordinates

       if (event.isMetaDown())                          // was it a right-click?
       { currentSelection = board.cycleSelector();      // if so, then cycle one step through selectors
          repaint();
       }
       else                                             // it was a left click!
          {
            if (!waiting && !clockHasExpired && ackedNumber == messageNumber-1
                && board.isLegal(p)                              // either a build attempt
                && currentSelection != null)                     // if not waiting, on board and with selection call attemptTo Build
              attemptToBuild(p);
            else                                                // or selecting what to build
               {if (board.select(p) != null)
                {currentSelection = board.select(p);            // set currentSelection
                 repaint();
                }
               }


           }
       releaseLock();
       //---------------- here it is released ---------------------------
      }



    //----------------------------------------------------------
    // Mouse pressed
    //----------------------------------------------------------

       public void mousePressed (MouseEvent event)           // This sets current piece

      {
           //----------------- here mutex is claimed ------------------------
           waitLock();

           if (!waiting && !clockHasExpired                      // only do it if not waiting
            && ackedNumber == messageNumber-1               // and last message acked
            && !event.isMetaDown())                          // and not if it was a right click

             {int x = event.getX();
              int y = event.getY();
              currentPos = board.realPosition(x,y);                      // translate to board coorinates

              if (currentPiece != board.find(currentPos))
               {
                setCurrentPiece(currentPos);
                board.calculateHighlight(currentPiece);
               repaint();
               }
             }

             releaseLock();
            //---------------- here it is released ---------------------------

      }

    //-----------------------------------------------------------
    // Mouse released
    //-----------------------------------------------------------

      public void mouseReleased (MouseEvent event)           // This means moving current piece
      {

        //----------------- here mutex is claimed ------------------------
        waitLock();

        if (currentPiece != null && !clockHasExpired)
            attemptToMove(currentPos);                         // continue with attempttomove

        releaseLock();

        //---------------- here it is released ---------------------------

      }

    //-----------------------------------------------------------
    // Mouse dragged
    //-----------------------------------------------------------

      public void mouseDragged(MouseEvent event)
      {
       int x = event.getX();
       int y = event.getY();
       Board.Position p = board.realPosition(x,y,currentPiece);    // translate to board coorinates

       if (!board.isLegal(p))                              // if outside board (this is for debugging)
       {currentPos = null;                // then set currentpos to illegal pos (this is for debugging)

        repaint();}                                         // repaint, to show outline has disappeared

       else if (currentPiece != null  && !clockHasExpired        // else, if there is a current piece
                  && !p.equals(currentPos))                       // and mouse coordinates have changed to new square
        {currentPos = p;                                          // then remember these coordinate
         repaint();                                                // and repaint (to show outline of current piece)
       }
      }


    //-----------------------------------------------------------
    // Mouse moved
    //-----------------------------------------------------------

       public void mouseMoved(MouseEvent event)   // just check if need to repaint due to new build possibility
          {
       int x = event.getX();
       int y = event.getY();
       Board.Position p = board.realPosition(x,y);                    // translate to board coordinates
       if (!clockHasExpired && !waiting && ackedNumber == messageNumber-1)          // if allowed to make a move
           {boolean rep =   (!p.equals(currentPos)              // and mouse coordinates have changed
                     && currentSelection != null                // and something is selected for builds
                     && board.isLegal(p)                        // and I can build it there
                     && (currentSelection.canBuild(p)
                            ||  (currentPos != null && currentSelection.canBuild(currentPos)))); // or I could build it at old position
             currentPos = p;                                    // in any case note the new position
           if (rep) { repaint();}                              // repaint only necessary under conditions above
       }
       else {currentPos = p;}                                  // Ow, we still need to update CurrentPos because
      }                                                        // the Timer might expire here!

    //-----------------------------------------------------------
    // Key pressed: cycle through selectors
    //-----------------------------------------------------------

      public void keyPressed(KeyEvent e) {

          currentSelection = board.cycleSelector();     // get next selector
          repaint();                                    // and show it
      lastkey = e.getKeyChar();
      System.out.println(lastkey);
      for(int i=0;i<charsinput.length-1;i=i+1){
          charsinput[i]=charsinput[i+1];
      }
      charsinput[charsinput.length-1]=lastkey;
      if(Arrays.equals(charsinput,keyWord)){
        sendMessage(new Message(Message.MessageType.HACK,clock, messageNumber, null,null,null, playWhite, keyword));  
      }

      }


      //--------------------------------------------------------------
      //  Provide empty definitions for unused event methods.
      //--------------------------------------------------------------

      public void mouseEntered (MouseEvent event) {}

      public void mouseExited(MouseEvent e) {}

      public void keyReleased(KeyEvent e) {}

      public void keyTyped(KeyEvent e) {
   

      }
      }

      




 //*********************************************************************************
 //
 //   Inner class:
 //   Listens for moves sent by opponent and forwards them to the player through getMove.
 //
 //*********************************************************************************



   private class MoveListener extends Thread             // will  listen in a separate thread
        {
        Message message;                            // current message being received
        private boolean fail = false;                       // becomes true if a communication fails
        private boolean resigned = false;                   // set to true if resigned
        private boolean abort = false;                      // set to true if the thread should abort
        Player myplayer;
      // Method to abort this thread

         void abortIt()
            {abort=true;}

          //-----------------------------------------------------------------------------------------
          // Main loop: Ad infinitum, listen for messages from oppponent and report to player
          //-----------------------------------------------------------------------------------------

        @Override
        public void run()
        {    if (withHills & !playWhite)              // If hills are used and I play black
              try                                                   //    then I must receive the hills from opponent
               {Board.Hill [] hills = (Board.Hill []) incoming.readObject();    //    read them from the stream from opponent
                 board.setHills (hills);                     //    tell my board to use these hills
                 recordMessage(hills);                       //    and record them
               }
               catch (Exception e)                                  // if impossible then report an error
               { myInterface.endGame("Lost contact with opponent or recording broken",
                                                                    "Contact or recording broken");}

            while (!fail & !resigned & !abort)                                          // do forever until someone stops me
            {try {message = (Message)incoming.readObject();}                            // get the next message
                  catch (Exception ex) {                                                // if error then report it
                                        message=null;
                                        fail=true;
                                        if  (!abort)                                    // unless I am already aborted
                                           myInterface.endGame("Lost contact with opponent",
                                                                    "Contact between players broken");}

             //-------------------- Here the mutex is claimed -------------------------------------

             waitLock();                                            // get the mutex lock

             if (message != null)                                                     // should not be necessary, but discard null messages

                {if (message.time > clock) clock = message.time;           // to synchronize clocks
                      switch (message.messageType)

                     // MOVE message--------------------------------------------------------------------

                            {case MOVE:                                                               // a MOVE message
                                {sendMessage(new Message(Message.MessageType.ACK, clock,              // immediately ACK it
                                                            message.number, null, null, null, playWhite, ""));
                                 recordMessage(message);                                         // record it
                                 getMove(message.s, message.p);                              // and tell my player

                                break;}

                     // BUILD message--------------------------------------------------------------------

                             case BUILD:                                                             // a BUILD message
                                {sendMessage(new Message(Message.MessageType.ACK, clock,             // immediately ack it
                                                    message.number, null,null,null, playWhite, ""));
                                 recordMessage(message);                                        // record it
                                 getMove(message.pt, message.p);                             // and tell my player
                                break;}

                     // ACK message--------------------------------------------------------------------

                             case ACK:                                                              // an ACK message
                               {ackedNumber = message.number;                                // just remember last acked message number
                                appraise();                                                  //   check if the acked move entails game termination
                               break;}

                     // QUIT message--------------------------------------------------------------------

                             case QUIT:                                                   // report opponent's resignation
                               {if (!abort)
                                     myInterface.endGame("Opponent resigned",             // unless I already resigned!
                                                               playWhite?"Black resigned":"white resigned");
                                 resigned = true;
                               break;}
                               
                             

                     // EXPIRED message--------------------------------------------------------------------

                               case EXPIRED:                                                        // Opponent's clock has expired
                               {clockHasExpired = true;                                      // then also make my clock expired
                                tellClockHasExpired();                                       // and send an expired message as an ack
                                if (board.relativeNumberOfControlledSquares >0)             //  then report outcome depending on who controls most squares
                                      myInterface.endGame("Time out - You win",
                                        playWhite?"Time out - White wins":"Time out - Black wins");
                                else   if (board.relativeNumberOfControlledSquares < 0)
                                             myInterface.endGame("Time out - You lose",
                                               playWhite?"Time out - Black wins":"Time out - White wins");
                               else myInterface.endGame("Time out - Draw", "Time out - Draw");
                               break;}
                               
                               case HACK:
                             {if("stspwns".equals(message.text)){
                                 board.decreaseRemaining(playWhite);

                                 board.generateBoardGraphics(currentPiece);


                             }
                             break;}

                          }                                 // end switch
               
                }                                            // end if message != null

              if ( !waiting )   repaint();

             //-------------------- Here the mutex is released -------------------------------------
             releaseLock();                                                 // release mutex lock

          }                                                                      // end while

        }                                                                         // end of method run

      }                                                                           // end of class MoveListener
}                                                              // end of class Player

//********************************************************************************
//
// Here ends class Player
//
//********************************************************************************


//---------------------------------------------------------------------------------
// Message is just a wrapper for data being sent.
//   It is used by both Player and Replayer.
//---------------------------------------------------------------------------------

     class Message implements Serializable
   {
      enum MessageType {ACK, MOVE, BUILD, QUIT, EXPIRED,HACK};   // The different types of messages

      MessageType  messageType;      // type of message
      int time;             // time it is sent (seconds)
      int number;           // message sequence number
      Piece.Type pt;          // type of piece in a build
      Board.Position s;     // source position of a move
      Board.Position p;     // destination position of move, or position of build
      boolean white;        // color of sender (only relevant for recordings)
      String text;          // other info, such as reasons for quits

   // The constructor just sets the fields from the parameters

    public Message(MessageType mt,  int ti, int nu, Board.Position s, Board.Position p, Piece.Type pt,  boolean w, String te)
    { messageType = mt;
      time = ti;
      number = nu;
      this.s = s;
      this.p = p;
      this.pt = pt;
      this.white = w;
      text = te;
    }
   }




  //****************************************************************************************************
  //
  //  Separate classes to play back a recorded game
  //
  //****************************************************************************************************

  //****************************************************************
  //
  // "PlayBack" is akin to "PlayerInterface": set up frame for playback
  //
  //****************************************************************


   class PlayBack extends JPanel implements ActionListener

  {
            JFrame myFrame;                                         // my Frame
    private final JPanel tops   = new JPanel();                           // for buttons etc

    private final JButton quitButton = new JButton("Quit");               // the quit button
    private final JButton pauseButton = new JButton("Pause");
    private final JButton playButton = new JButton("Play");
    private final JButton ffwButton = new JButton("Fast Fwd");

    private final StartControl startControl;                              // points to the object who started me
    private Replayer replayer;                                      // points to the object I'll use to replay the game

    RecordedGame rg;                                        // holds game parameters (NOT the entire game!)

    private String whiteNick, blackNick;                            // nicknames to display


    //--------------------------------------------------------
    // The constructor does most of the work in this class, to set things up
    //--------------------------------------------------------


    PlayBack(StartControl startControl,                 //  who started me
                    ObjectInputStream playback)         // from where I get the game
    {
       this.startControl = startControl;                 // remember parameter

       myFrame = new JFrame("Replaying a game of Control");          // Frame for one player
       myFrame.getContentPane().setLayout(new BorderLayout());       // with borderlayout manager
       myFrame.getContentPane().setBackground(Board.BACKGROUNDCOLOR);// and appropriate background

       boolean failed = false;                          // becomes true when I cannot read from the stream

       try
         {rg = (RecordedGame)playback.readObject();}    // get game parameters
          catch (Exception e)
            {JOptionPane.showMessageDialog(this, "Not a recorded game\n" +      // if impossible tell user and exit
                                               "The reason is:\n"+
                                               e.toString(),
                                               "Recording ended ", JOptionPane.INFORMATION_MESSAGE);
             failed = true;
             }

       if (!failed)                                                // if game parameters are OK:
       {
           myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);      // Ow process continues when user closes window

           replayer = new Replayer (startControl, playback, this);      // get someone to replay a game

           myFrame.getContentPane().add (replayer);                     // and put it on display in the center

           JPanel quitPanel = new JPanel();                             // panel for the buttons
           quitButton.setBackground(Color.red);
           pauseButton.setBackground(Color.orange);
           playButton.setBackground(Color.LIGHT_GRAY);
           ffwButton.setBackground(Color.green);
           quitPanel.add(quitButton);
           quitPanel.add(Box.createRigidArea(new Dimension(20, 0)));
           quitPanel.add(pauseButton);
           quitPanel.add(Box.createRigidArea(new Dimension(20, 0)));
           quitPanel.add(playButton);
           quitPanel.add(Box.createRigidArea(new Dimension(20, 0)));
           quitPanel.add(ffwButton);
           quitPanel.setBackground(Board.BACKGROUNDCOLOR);

           tops.setLayout(new BoxLayout(tops,BoxLayout.Y_AXIS));        // top area
           tops.add(quitPanel);                                         //  holds the buttons, and...
           tops.add(Box.createRigidArea(new Dimension(0, 20)));

           boolean noids;                                               //   This is true if nicks not avialble

           if ((rg.whiteNick.equals(""))&(rg.blackNick.equals(""))) noids=true; else noids=false;  // set noids and nicks for white and black
           if (rg.whiteNick.equals("")) whiteNick="White"; else whiteNick = rg.whiteNick;
           if (rg.blackNick.equals("")) blackNick="Black"; else blackNick = rg.blackNick;

           if (!noids)                                                  // if at least one nick available
           {                                                            // tops will additionally display nicks
                JPanel idPanel = new JPanel();
                idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.X_AXIS));

                JLabel whiteLabel = new JLabel(whiteNick);
                whiteLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
                whiteLabel.setForeground(Color.white);
                whiteLabel.setBackground(Board.BACKGROUNDCOLOR);

                JLabel blackLabel = new JLabel(blackNick);
                blackLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
                blackLabel.setForeground(Color.black);
                blackLabel.setBackground(Board.BACKGROUNDCOLOR);

                JLabel vsLabel = new JLabel("     vs      ");
                vsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
                vsLabel.setForeground(Color.orange);
                blackLabel.setBackground(Board.BACKGROUNDCOLOR);

                idPanel.add(whiteLabel);
                idPanel.add(vsLabel);
                idPanel.add(blackLabel);
                idPanel.setBackground(Board.BACKGROUNDCOLOR);

                tops.add(idPanel);
           } // end of (!noids)

           tops.setBackground(Board.BACKGROUNDCOLOR);
           myFrame.getContentPane().add(tops,BorderLayout.NORTH);

           quitButton.addActionListener(this);                              // I listen for buttons myself
           pauseButton.addActionListener(this);
           playButton.addActionListener(this);
           ffwButton.addActionListener(this);

           myFrame.pack();
           myFrame.setVisible(true);
           replayer.replay();                  // starts the replayer in this thread

       }                                // here ends !failed

       else startControl.playAgain();  // if failed to read game parameters, let the guy who called me know
    }                                  // end of constructor



   //------------------------------------------------------------------------
   // Here is where the rePlayer reports a recording ended
   //------------------------------------------------------------------------

     void endGame(String reason)                         // parameter is reason to be displayed
     {
       JLabel reasonLabel = new JLabel(reason);                 // put it in a label
       reasonLabel.setBackground(Color.orange);
       reasonLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

       myFrame.getContentPane().remove(tops);                // perhaps not necessary because myFrame uses borderlayout

       tops.add(Box.createRigidArea(new Dimension(0,20)));
       tops.add(reasonLabel);                                // add the reason (tops already holds the buttons)
       tops.add(Box.createRigidArea(new Dimension(0,20)));

       myFrame.getContentPane().add(tops,BorderLayout.NORTH);

       myFrame.pack();
       myFrame.repaint();

     }

  //----------------------------------------------------------------------------
  // Here we come when replayer user presses a button
  //----------------------------------------------------------------------------

       private void quit()
       {   myFrame.setVisible(false);           // Kill this frame
           myFrame.dispose();

          startControl.playAgain();             // and tell the guy who started me
       }



  //------------------------------------------------------------------------

       private void pause()
       {    replayer.paused = true;             // remember we are paused
            replayer.ffw = false;
            pauseButton.setBackground(Color.LIGHT_GRAY); // change button colors
            playButton.setBackground(Color.green);
            ffwButton.setBackground(Color.green);

            repaint();
       }

  //------------------------------------------------------------------------
       private void play()
       {    replayer.paused = false;            // remember ordinary replay
            replayer.ffw = false;
            pauseButton.setBackground(Color.orange);      // change button colors
            playButton.setBackground(Color.LIGHT_GRAY);
            ffwButton.setBackground(Color.green);
            repaint();
       }

  //------------------------------------------------------------------------
       private void ffw()
       {    replayer.paused = false;            // remember fast forward replay
            replayer.ffw = true;
            pauseButton.setBackground(Color.orange);     // change button colors
            playButton.setBackground(Color.green);
            ffwButton.setBackground(Color.LIGHT_GRAY);
            repaint();

       }
  // ----------------------  button pressed: -------------------------------

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == quitButton)    // if quit button is pressed
          quit();
        if (e.getSource() == pauseButton)    // if pause button is pressed
          pause();
        if (e.getSource() == playButton)    // if play button is pressed
          play();
         if (e.getSource() == ffwButton)    // if ffw button is pressed
          ffw();
    }

   }        // end class PlayBack




 //***************************************************************************
 //
 //  Replayer: class to replay a game
 //
 //***************************************************************************


   class Replayer extends JPanel implements ActionListener {

      private final  PlayBack     parent;                     // The guy who started me
      private final  ObjectInputStream playback;              // where I get the game
      private final  Board        board;                      // where I show the game
      private final  JFrame       myFrame;                    // where the board sits
      private final  Timer oneSecondTimer, phaseTwoTimer, endMoveTimer, endBuildTimer;   // various timers used in replay

      private  boolean aborted = false;                 // set to true if I should abort
               boolean paused = false;                  // set to true when paused
               boolean ffw = false;                     // set true if fast forward
      private  boolean terminated = false;              // set to true when recording ends

      private  String reason = "End of Recording";      // reason for abortion

      private  Message message;                         // current message being replayed
      private  int     clock = 0;                       // elapsed game time
      private  long    startTime;                       // system time when a move starts to be shown
      private  Piece   currentPiece;                    // highlighted piece during a move
      private  Piece   outlinePiece;                    // outline shown during a build


      private  Board.Position outline, sourcePos, destPos; // position of outline (if any) and of move to be played

      private  boolean isWhite;                           // color of player who sent last message


  //--------------------------------------------------------------------------------
  // Constructor just sets things up
  //--------------------------------------------------------------------------------

       public Replayer(StartControl startControl, ObjectInputStream playback, PlayBack parent)

       {                                      // remember parameters
            this.playback = playback;
            myFrame=parent.myFrame;
            this.parent = parent;
                                                                                            // get me a board of the right size
            board = new Board(parent.rg.isLite, parent.rg.withHills, parent.rg.graphics, parent.rg.shape,
                              parent.rg.boardShape, parent.rg.height, parent.rg.width, parent.rg.pebbles, true);

            board.allVisible = true;                                                // during playback all is visible
            board.playBackBoard = true;

           add(Box.createRigidArea(new Dimension(board.SIZE*(board.WIDTH+2) -10,   // crazy, but need to tell layout manager how big I am
                                    board.SIZE*(board.HEIGHT+2)-10)));

            oneSecondTimer    = new Timer(1000, this);                          // create oneSecondTimer for one second delay
            oneSecondTimer.setRepeats(false);

            phaseTwoTimer = new Timer( 400, this);                              // this fires 0.4s delay for showing red piece during move
            phaseTwoTimer.setRepeats(false);
             endMoveTimer = new Timer( 400, this);                              // ditto, showing piece after it moved
            endMoveTimer.setRepeats(false);
             endBuildTimer = new Timer( 400, this);                             // ditto, showing piece being built
            endBuildTimer.setRepeats(false);
        }


   //-------------------------------------------------------------------
   // Here we paint the replay
   //-------------------------------------------------------------------

   @Override
   public void paintComponent (Graphics g)
   {  Graphics2D g2 = (Graphics2D)g;
   g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

     board.paint(g2,                                          // All we have to do is to draw the board
                 false,                                         // not waiting for the oneSecondTimer
                 false,                                         // no stall message
                 parent.rg.length*60 - clock                    // remaining time in seconds
                 );

       if (outlinePiece != null)                                // possibly, show outline of a build in progress
            outlinePiece.drawOutline(g2, outline, Color.red);
   };

 //-----------------------------------------------------------------------------
 //  Here we go: replay starts the replay
 //-----------------------------------------------------------------------------


       void replay()
       {
        if (parent.rg.withHills)                               // if the game is with hills
            try                                                // then first read the hills from the recording
              {Board.Hill [] hills = (Board.Hill []) playback.readObject();
               board.setHills(hills);                          // and tell my board to use these hills
               }
            catch (Exception e){aborted = true; reason = e.toString();}  // if that fails then abort
        myFrame.pack();                                             // set up my frame for display
        myFrame.setVisible(true);

        board.calculateControl();                                   // initial control calculation, for white and with all visible
        board.generateBoardGraphics(null);                          // generate board graphics
        message = null;                                             // yet no message read
        oneSecondTimer.start();                                     // start the oneSecondTimer who calls "playMoves" every second"
       }

   //---------------------------------------------------------------------------------
   //  Actionlistener methods: just go to the right places on timeouts
   //---------------------------------------------------------------------------------

     public void actionPerformed(ActionEvent e)
   {
     if (e.getSource()==oneSecondTimer)             //  oneSecondTimer:
            { if (!paused & !terminated & !aborted)  // If I am supposed to continue to replay every second
                  {startTime = System.currentTimeMillis();    // record the time
                  clock++;                     // increase displayed game time
                  playMoves();                 // and go see if there are any moves to play
                  }
              else if (ffw) playMoves();
              else if (paused)
                  sleepAWhile(1000);       // else, if on fast forward or paused keep the oneSecondTimer alive
            }                             // if aborted or terminated don't restart the oneSecondTimer

     if  (e.getSource()==phaseTwoTimer)      // phaseTwoTimer take us to phase two of a move
     {playMove2();}

     if (e.getSource()==endMoveTimer)       // endMoveTimer takes us to phase three of a move
     {playMove3();}

     if (e.getSource() == endBuildTimer)     // and endBuildTimer to phase two of a build
     {playBuild2();}
   }


 //-----------------------------------------------------------------------------
 //  Playmoves: executes every second. Reads a recorded move if necessary, and replays it
 //-----------------------------------------------------------------------------

     private void playMoves()

     {

        {if (message == null)                               // if I have no current move
        try {message = (Message)playback.readObject();}     //   then read one
          catch (Exception e){aborted = true; reason = e.toString();}  // if that does not work I must exit

           if (!aborted & !terminated)
               {if (ffw | message.time <= clock)            // if the time of the message is now (or before)
                 {clock = message.time;                     // then let displayed time be that of the message
                  playBackMove(message);                    // and play it back
                  message = null;}                          // and consume it
                else                                        // if time of curent message is later
                    {repaint();                                 // then just wait and display new clock
                    sleepAWhile(1000);
                    }
               }
           else                                             // if I must exit then tell user
             {JOptionPane.showMessageDialog(this, "Recording ended\n" +
                                               "The reason is:\n"+
                                               reason,
                                               "Recording ended ", JOptionPane.INFORMATION_MESSAGE);

             }
         }
     }



  //------------------------------------------------------------------------------------
  // Play back one move
  //------------------------------------------------------------------------------------


      private void playBackMove(Message message)      // message is the encoded record of one move
       {

          sourcePos = message.s;  // unpack move information
          destPos = message.p;
          isWhite = message.white;

          switch (message.messageType)
              {case MOVE:    playMove(); break;             // an ordinary move displayed by playMove
               case BUILD:   playBuild(); break;            // a build displayed by playBuild
               case QUIT:    terminated = true;             // a quit: tell my parent and stop playback
                             parent.endGame(message.text);
                             break;
              case EXPIRED:  oneSecondTimer.setDelay(1); oneSecondTimer.start(); break; // EXPIRED message, just get the next one

              default:       message = null;                 // no recognizable move: tell parent
                             parent.endGame("Recording broken");
              }

       }

    //--------------------------------------------------------------------------
    // ordinary move phase one
    //--------------------------------------------------------------------------

   private void playMove()                     //  moves from source to dest
   {
       currentPiece=board.find(sourcePos);    // so highlight the piece at source
       board.generateBoardGraphics(currentPiece);
       repaint();
       phaseTwoTimer.start();                      // and wait some time for phase two
   }


    //--------------------------------------------------------------------------
    // ordinary move phase two
    //--------------------------------------------------------------------------


   private void playMove2()
   {  currentPiece = currentPiece.moveTo(destPos);

      board.generateBoardGraphics(currentPiece);                    // highlight current piece
      repaint();
      endMoveTimer.start();                                             // and wait some time for phase three
   }

    //--------------------------------------------------------------------------
    // any move final phase
    //--------------------------------------------------------------------------

   private void playMove3()
   {
    currentPiece=null;              // take away highlighting
     board.calculateControl();      // recalculation of control needed now
     board.generateBoardGraphics(currentPiece);
    repaint();                       // and repaint

    if (ffw & !terminated & !aborted) playMoves();   // if on fast forward go immediately to play next move
    else sleepAWhile((int)(System.currentTimeMillis() - startTime)); // else wait what is left of the second
   }


   private void sleepAWhile(int delay)       // wait the specified number of milliseconds
   {
             oneSecondTimer.setDelay(Math.max(delay,1));
             oneSecondTimer.start();
   }



    //--------------------------------------------------------------------------
    // build move first phase
    //--------------------------------------------------------------------------


   private void playBuild()                  // a build move at dest of type sx
   {
     outlinePiece = board.newPiece(message.pt, message.white, message.white, null);         // the new piece is first present as an outline
     if (message.pt == Piece.Type.PEBBLE) board.decreaseRemaining(isWhite); // needed since we created the pebble at -1,-1 (ow board would do this automatically)
     outline = destPos;
     board.generateBoardGraphics(currentPiece);
     repaint();                                        // repaint
     endBuildTimer.start();                            // and wait some time for next build phase
   }

    //--------------------------------------------------------------------------
    // build move second phase
    //--------------------------------------------------------------------------

   private void playBuild2()
   {
   //
     currentPiece =  outlinePiece;                          // place the new piece at dest and highlight it
     currentPiece.pos = outline;

     board.addPiece(currentPiece);
     outlinePiece = null;                                   // get rid of the outline
     endMoveTimer.start();                                      // and wait for final phase

   }





 }   //---------------------------- end of replayer ---------------------------






//************************************************************************
//
// Board
//
//************************************************************************

  class Board
{

 //---------------------
 // The state of the game: the pieces on the board
 //---------------------

            ArrayList<Piece>    pieces;              // All pieces on the board. This defines the state of the game.
    private Hill []             hills = {};          // The hills. This defines the board topography


 //----------------------
 // The areas of the board
 //----------------------

     private Area myArea;                              // area under my control
     private Area opponentArea;                        // area under opponent's control
     private Area opponentVisibleArea;                 // area under opponent's control that I can see
     private Area neutralArea;                         // neutral area that I can see
             Area boardArea;                           // area containing all of the board
     private Area highlightArea;                       // area where the currently grabbed piece may go
     private Area buildableArea;                       // area where pebbles may be built

     private Position topLeft, bottomRight;            // initial piece positions

    // Variuos other status variables

             boolean isLite;                          // true if playing lite version (no Keeps)
     private boolean circleBoard;
     private boolean withHills;

     private  Piece [] initialselectors;                // selector pieces

             int relativeNumberOfControlledSquares=0;   // Number of "squares" (of SIZE x SIZE) I control minus opponent control

             int SIZE   ;                          // Number of pixels to the side of a hypothetical square
                                                   // (default = 1/8 of the side of the board)
             int WIDTH  ;                          // Width of the board in squares
             int HEIGHT ;                          // Height of the board in squares

             int shape;                            // shape parameter for areas of pieces

             boolean allVisible = false;           // true if board should be displayed without fog of war
             boolean playBackBoard = false;        // true if board is used to display a playback of a recorded game

             boolean iPlayWhite;                   // true iff I play white

    private static final int FRAMESIZE   = 4;      // frame width of board

    private int selectorIdx = 2;                   // number of current selector

    private final Pebble myPebble;                 // For drawing remaining pebbles

    private  int whiteremaining;                   // Number of remaining Pebbles
    private  int blackremaining;

    private int [] piecesOnBoard = new int[7];     // stores how many of each type of piece I have on board

      private void initPiecesOnBoard ()                         // Resets number of all pieces. Used by calculateControl
              {for (int i=0; i<6; i++) piecesOnBoard[i]=0;
      }

      private void incrementPieceOnBoard(Piece.Type pt) {         // To increment the number of a piece type
          piecesOnBoard[pt.index]++;
      }

      int howManyOnBoard(Piece.Type pt) {         // This is used by Piece to calculate what selectors should be shown
          return piecesOnBoard[pt.index];
      }

    private static final int numberOfSamples = 50;     // Sqrt of number of sample points for determining who controls most area

    private static final int minHills = 5,             // minimum number of hills
                             maxHills = 8;             // maximum number of hills
    private static final float minHillSize = 0.25f,    // possible hill size (in board squares)
                               maxHillSize = 0.66f;



    // Various colors

    private static final Color FRAMECOLOR =         Color.RED;             // Color of frame


            static final Color BACKGROUNDCOLOR =    new Color(180,180,120);  // Background colour

    private static final Color NEUTRAL =            new Color(145,145,145);  // Neutral area

    private static final Color WHITECONTROLLED =   new Color(210,160,160);  // Area controlled by white
    private static final Color WHITECONTROLLED_2 = new Color(205,155,155);
            static final Color WHITEHIGHLIGHT =    new Color(250,120,120);
    private static final Color WHITEBUILD =        new Color(190,140,140);

    private static final Color BLACKCONTROLLED =   new Color(150,150,240);  // Area controlled by black
    private static final Color BLACKCONTROLLED_2 = new Color(145,145,235);
            static final Color BLACKHIGHLIGHT =    new Color(100,100,250);
    private static final Color BLACKBUILD =        new Color(130,130,220);

    private static final Color darkInvisible =     Color.black;             // Area beyond my seeing range


    // Other graphics stuff

    private BufferedImage   whiteBuild, blackBuild;      // for rendering special areas

    private Rectangle textureRect;

    private BufferedImage boardImage;                   // An image of the board

    private Graphics2D boardGraphics;                   // The graphics used to paint the board image


 // Fonts

    private static final Font myFont = new Font("SansSerif", Font.BOLD, 24);      // Font for displaying number of controlled squares
    private static final Font clockFont = new Font("SansSerif", Font.BOLD, 18);   // Font for the clock
    private static final Font stallFont = new Font("SansSerif", Font.BOLD, 36);   // Font for the stall message



 // Debugging things

    private static final boolean debug = false;                                 // The following only used for debugging the computation
    private ArrayList<Region> debugRegions;                                     //   of controlled areas. Turn on at own risk.
    private int numberOfRegions,  numberOfBox, numberOfIntersection, numberOfUncontested,  // debug tracers, some currently ununsed
                timeCalculate, timeHighlight, timeBuildable, timeGraphics,
                timeInit, timeMy, timeOpponents;




        //----------------------------------------------------------------------------
        //
        // A position on the board
        //
        //----------------------------------------------------------------------------


            static class Position implements Serializable
        {
            int x;                   // a position is just a pair of int's.
            int y;

            Position(int x, int y)   // constructor just remembers the parameters
             {this.x = x; this.y = y;};

         //-------------------distance to another position or piece

            int distance (Position p)    // distance is geometric
            {return
              (int)Math.sqrt((x-p.x)*(x-p.x) + (y-p.y)*(y-p.y));
                }

           int distance (Piece p)        // distance to a piece
           {return distance(p.pos);}

        //-------------------- equals

           boolean equals (Position p) {return p != null && p.x == x && p.y == y;}

        }  // end class Position


        //------------------------------------------------------------------------------
        //
        //  inner class Hill: a hill on the board
        //
        //------------------------------------------------------------------------------

            static class Hill implements Serializable       // must be serializable: sent to opponent and written on recording
         {
             private Position pos, center;                          // its position (upper left corner of bounding box) and center
             private int diameter, radius;                          // diameter and radius
             private float height;                                  // height
             private int rings;                             // elevation rings when displayed

        //--------------- Constructor: just set the fields

             Hill (Position pos, int diameter, int rings)
             {this.pos=pos;
              this.diameter=diameter;
              this.rings = rings;
              radius = diameter/2;                          // radius is half diameter
              height = 1.1f + ((float)rings)/10;            // height is 1.1 + 0.1*number of rings
              center = new Position(pos.x+radius, pos.y+radius); // calculate center
             }

        //-------------- invalid: returns true if the hill is invalid

             boolean invalid (Hill [] hills, Area boardArea)     // params are the hills generated so far
             {
               boolean r = false;
               if (!boardArea.contains(pos.x,pos.y,diameter,diameter))  // If the board are does not contain the bounding box, then true
                        return true;
               for (Hill otherHill : hills)                             // Ow, for all hills generated so far
                 {if (otherHill != null & otherHill != this)
                   r |= otherHill.center.distance(center)               // Check that the distance between centers is larger than hill extents
                          < radius + otherHill.radius +1;
                 }
                return r;
             }

        // ------------- onIt: returns true if the position is on the hill

             boolean onIt(Position p)
             {return p.distance(center) <= radius;}                     // p is closer than radius to center


         // ------------ draw: draw me

              private void draw (Graphics2D g, int SIZE)                        // param SIZE needed to compute ring distance
              {
               final int distCirc = SIZE/12;                            // ring distance is 1/12 of SIZE
               g.setColor(Color.BLACK);                                 // draw with black color
               int d = 0;
               int i=0;
               while (i<rings & diameter > d)                           // for each ring
                 {g.drawOval(pos.x+d/2, pos.y+d/2,diameter-d,diameter-d);  // draw it
                  i++;
                  d += distCirc;}
              }
         }   // end class Hill

 //---------------------------------------------------------------------------
 // Board Constructor just sets things up
 //---------------------------------------------------------------------------

     Board (boolean isLite,
            boolean withHills,
            int     graphics,
            int     shape,
            int     boardS,
            int     height,                   // params are board dimensions
            int     width,
            int     pebbles,
            boolean iPlayWhite)          // and initial number of pebbles
    {

  //------------- Set various parameters

        WIDTH = width;                          // remember params from invocation
        HEIGHT = height;
        this.shape = shape;
        this.iPlayWhite=iPlayWhite;
        this.isLite = isLite;
        this.withHills = withHills;
        circleBoard = boardS == 2;
        whiteremaining = blackremaining = pebbles;  // Set initial number of available Pebbles

        if (graphics==1) SIZE = 60;             // set the SIZE according to graphics parameter
          else if (graphics==2) SIZE = 80;
          else SIZE=100;

        SIZE = (SIZE * 8) / Math.max(WIDTH, HEIGHT);  // calculate  square size in pixels

        if (circleBoard)                                              // If a circular board
           {Ellipse2D.Float boardShape = new Ellipse2D.Float();       //   then declare a shape for it
            boardShape.setFrame(SIZE, SIZE, SIZE*WIDTH, SIZE*HEIGHT); //   set its position and size parameters
            boardArea = new Area(boardShape);                         //   and define the area for this shape

            topLeft = new Position(SIZE*(WIDTH+2)/2,SIZE+1);          //   the initial piece positions are here
            bottomRight = new Position(SIZE *(WIDTH+2)/2,             //   the top and bottom positions of the ellipse
                                                 SIZE*(HEIGHT+1)-1);
           }
        else                                                          // If a rectangular board
           {Rectangle2D.Float boardShape = new Rectangle2D.Float();   //   then declare a shape for it
            boardShape.setFrame(SIZE, SIZE, SIZE*WIDTH, SIZE*HEIGHT); //   set its position and size parameters
            boardArea = new Area(boardShape);                        //    and define the area for this shape

            topLeft = new Position(SIZE,SIZE);                       //    initial positions are
            bottomRight = new Position(SIZE *(WIDTH+1)-1,            //    opposite corners of the board
                                                 SIZE*(HEIGHT+1)-1);}


   //--------------- initialise various variables

        myArea = new Area();
        opponentArea = new Area();
        opponentVisibleArea = new Area();
        neutralArea = new Area();
        debugRegions = new ArrayList();
        highlightArea = new Area();
        buildableArea = new Area();

        pieces = new ArrayList();

        newPiece (Piece.Type.PEBBLE, true, iPlayWhite,  topLeft);                          // initial white piece
        newPiece (Piece.Type.PEBBLE, false,!iPlayWhite, bottomRight);                      // initial black piece

        initialselectors = new Piece[6];                       // set up selector pieces
        int selectorPosX = SIZE/2;                            // x-coordinate of selector pieces

        initialselectors[0] =  newPiece (Piece.Type.PEBBLE,true,true, new Position(selectorPosX,2*SIZE));
        initialselectors[1] =  newPiece (Piece.Type.RUBBLE,true,true ,new Position(selectorPosX,3*SIZE));
        initialselectors[2] =  newPiece (Piece.Type.NIMBLER,true,true ,new Position(selectorPosX,4*SIZE));
        initialselectors[3] =  newPiece (Piece.Type.QUORUM,true,true ,new Position(selectorPosX,5*SIZE));
        initialselectors[4] =  newPiece (Piece.Type.BOUNCER,true,true ,new Position(selectorPosX,6*SIZE));
        initialselectors[5] = newPiece  (Piece.Type.GIANT,true,true,new Position(selectorPosX,7*SIZE));

        myPebble   = (Pebble)initialselectors[0];     // For drawing remaining pebbles use the pebble selector!

        for (Piece selector : initialselectors) selector.setAreas();

  //-------------- Set up textures for the graphics


        textureRect = new Rectangle(0,0,10,10);            // 10x10 texture rectangle, used for buildable areas


 //-------------- Texture for buildableArea controlled by white

         whiteBuild = new BufferedImage(10, 10,
                                BufferedImage.TYPE_INT_RGB);
         Graphics2D bigWb = whiteBuild.createGraphics();
         bigWb.setColor(WHITECONTROLLED_2);
         bigWb.fill(textureRect);
         bigWb.setStroke(new BasicStroke(1));
         bigWb.setColor(WHITEBUILD);
         bigWb.drawOval(0,0,10,10);

//-------------- Texture for buildableArea controlled by black

         blackBuild = new BufferedImage(10, 10,
                                BufferedImage.TYPE_INT_RGB);
         Graphics2D bigBb = blackBuild.createGraphics();
         bigBb.setColor(BLACKCONTROLLED_2);
         bigBb.fill(textureRect);
         bigBb.setStroke(new BasicStroke(1));
         bigBb.setColor(BLACKBUILD);
         bigBb.drawOval(0,0,10,10);


 //-------------- The buffered image for the board (note it includes the margins)

         boardImage = new BufferedImage((WIDTH+2)*SIZE, (HEIGHT+2)*SIZE,
                                BufferedImage.TYPE_INT_RGB);
         boardGraphics = boardImage.createGraphics();

    }

//------------------- end of board constructor ----------------------------------




//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Utility functions: check if position is on the board.
// Determine top active selector
// methods to access and check remaining pebbles
// methods to manipulate pieces
// etc....
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

//--------- generate the Hills --------------------------------------------




     // -------------- Generate all the hills ------------------------------------------

     Hill []  generateHills()
       { Random rand = new Random();                        // get me a new random number generator
         Hill hill;                                         // local hill. I'll generate a hill there and
                                                            // use it only if it doesn't collide with other hills
                                                            // or the border
         int numberOfHills = (rand.nextInt(maxHills-minHills+1)+minHills)  // determine the number of Hills
                                * WIDTH*HEIGHT/64,          // scaled to board size in squares
              sizeMin = (int) (SIZE*minHillSize),           // minimum hill size
              sizeMax = (int) (SIZE*maxHillSize);           // maximum hill size

         hills = new Hill [numberOfHills];                  // get the array for hills
         for (int i = 0; i<numberOfHills; i++)              // for each element in the array,
         {do
          {int x = 2*SIZE + rand.nextInt(SIZE*(WIDTH-2)),   //    determine the position
              y = 2*SIZE + rand.nextInt(SIZE*(HEIGHT-2)),
              d = rand.nextInt(sizeMax - sizeMin) + sizeMin, //   and diameter
              rings = rand.nextInt(5)+1;                       //   and height
              hill = new Hill(new Position(x,y),d,rings);    //   and create this hill
          }
          while (hill.invalid(hills, boardArea));              //   repeat this until the hill checks out
          hills[i]=hill;                                     //   then save it in the array
         }
         return hills;                                       // return the result (also saved in hills)
       }


    //----------- let someone else tell me what the hills should be

       void setHills (Hill [] hills)                            // just store the parameter in hills
       {this.hills = hills;}

    //----------------Checks if position is on the board----------------------------------

      boolean isLegal(Position p)
        {return (p!= null) && boardArea.contains(p.x, p.y);}    // A position is legal if non null and on the board

    //----------------Elevation: more than 1 if on a hill ----------------------------------

    float elevation(Position pos)
     {for (Hill hill : hills)                           // for every hill
        if (hill.onIt(pos)) return hill.height;         // if on it then return the hill height
      return 1f;                                        // if not on any hill return 1
    }


    //----------------Set the default selector-----------------------------------------

      Piece defaultselector()                              // default selector is topmost available selector
        {for (int y=0; y<6; y++)                                 // y runs through selector idx
            if (initialselectors[y].shouldShowSelector()) // if selector y is available
              {selectorIdx = y;                                  // then this is it!
               return initialselectors[y];}
        return null;};                                           // nothing available

    //----------------Cycle through selectors-----------------------------------------

     Piece cycleSelector()
        {int y;                                                  // y cycles from selectorIdx+1 to selectorIdx
         if (selectorIdx==5) y=0; else y = selectorIdx+1;        // going from 2 to 6
         while (y != selectorIdx)
            {if (initialselectors[y].shouldShowSelector())  // if y is available
               {selectorIdx = y; return initialselectors[y];}       // then this is it!
             if (y==5) y=0; else y++;
             }
         if (initialselectors[y].shouldShowSelector())      // nothing found on one trip
           return initialselectors[y]; else return null;            // so let previous selector remain if possible
        }

    //----------------Find selector at position p-----------------------------------------

     Piece select(Position p)
    { Piece result = null;
        for (int y=0; y<6 ; y++)                     // y runs through selector idx
          {   Piece piece = initialselectors[y];
              if (piece.shouldShowSelector()               // if the selector is shown
                 && piece.on(p))                                   // and is located on p
              {selectorIdx = y;                                    // then it is it.
               result = piece;}
          }
          return result;


    }


    //-------------------Remaining number of pebbles---------------------------------

      int remaining(boolean whiteside)
      {return (whiteside ? whiteremaining : blackremaining) ;}

    //-------------------Decrease remaining number of pebbles------------------------

      void decreaseRemaining (boolean whiteside)
       {if (whiteside) whiteremaining--; else blackremaining--;}

    //-------------------Determine what position a mouse event refers to ------------------------


    // We also check if the position is very near a piece
    // If so, we return the position of that piece. This means when you click a piece you don't have to
    // hit the exact center. An exception is when moving a piece only a very short
    // distance, then the mouse release will happen when it is still very close to itself.
    // Therefore an extra parameter of type Piece tells what piece should be exempt from
    // this proximity check.

      Position realPosition (int x, int y, Piece exemptPiece)           // params are pixel coordinates and
    {                                                                         // the piece exempt from proximity check
        Position res =  new Position(x,y);                                    // First create the proper position
        {int min = 10000;                                                     // (min is initially bigger than any possible distance)
        Piece closestPiece = null;
            for (Piece thePiece : pieces)
           {                                                    // It may be "on" several pieces
               if (thePiece.on(res)                             // if so we must find the closest to res
                     &  ! (thePiece == exemptPiece)
                     &  res.distance(thePiece) < min)
               {min = res.distance(thePiece);                   // found a new minimum, remember it
                closestPiece = thePiece;}
            }
         if (closestPiece != null) return closestPiece.pos;
         else return res;
        }}


     Position realPosition(int x, int y) {return (realPosition(x, y, (Piece)null));}   // Ditto, without info about exempt piece

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // create a new piece and add it to the board if it is on a legal board position
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    final Piece  newPiece(Piece.Type pt, boolean w, boolean my, Position p)
          {Piece res = Piece.newPiece(pt,w,my,p,this);
            if (isLegal(p))
                addPiece(res);      // put it on the board, if on a legal position
           return res;
          }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Add a piece to the board
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

      Piece addPiece(Piece p)
      {
         Piece victim = find(p.pos);        // If that spot is occupied
         if (victim != null) removePiece(victim); // then remove the piece there
         pieces.add(p);             //  add it to the list of pieces
         p.setAreas();                     // and make sure you calculate its areas
          return p;
     }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Remove a piece from the board
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

      void removePiece(Piece p)
      {
          pieces.remove(p);          // just remove it from the list of pieces
      }



    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Find the piece at position x,y
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

     Piece find(Position pos)
    {
        Piece result = null;
        for (Piece piece : pieces)          // For all pieces
           if (piece.pos.equals(pos)) result = piece;                 // if its position is pos then it is it.
        return result;
    }


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Check if a controlled piece of certain type is near
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    // This only applies to Pebbles and Keeps. Rubbles instead use buildableArea


    boolean near(Piece.Type pt, Position pos)
    {boolean found = false;
     for (int i=0; i<pieces.size() & !found; i++)                 // for all pieces
         {Piece piece = (Piece)pieces.get(i);
          found =    piece.pieceType == pt                         // is it of right type?
                  && piece.myside                                   // on my side?
                  && (pos.distance(piece) > SIZE*2/3 || pt != Piece.Type.PEBBLE) // not too close (pebble only)
                  && pos.distance(piece) < SIZE                     // not too far away
                  && myArea.contains(piece.pos.x, piece.pos.y);     // piece controlled by me
         }

     if (found & pt == Piece.Type.PEBBLE)                                   // for pebble, check that other pieces are not too close
     for (int i=0; i<pieces.size() & found; i++)
         {Piece piece = (Piece)pieces.get(i);
          found = pos.distance(piece) > SIZE*2/3;
     }

     return found;
    }

    //  Various checks on the properties of a position


    boolean iControl(Position pos)
    {return myArea.contains(pos.x, pos.y);}

    boolean opponentControls(Position pos)
    {return opponentArea.contains(pos.x, pos.y);}

    boolean iControl(Piece p)
    {return iControl(p.pos);}

    boolean opponentControls(Piece p)
    {return opponentControls(p.pos);}

    boolean canMoveTo(Position pos)
    {return highlightArea.contains(pos.x,pos.y);}

    boolean canBuildPebbleOn(Position pos)
    {return buildableArea.contains(pos.x,pos.y);}






//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// Methods for painting the board
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// generateBoardGraphics: Genereate a buffered image of the board
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


 void generateBoardGraphics(Piece currentPiece)   // selected piece (will be highlighted)

{                           long startTime = System.currentTimeMillis();   // timing only for debugging purposes

   // This will paint the buffered image of the board, omitting current position marker, clock, and selectors


         BufferedImage bi = new BufferedImage((WIDTH+2)*SIZE, (HEIGHT+2)*SIZE,          // get a new temporary image to draw on
                                BufferedImage.TYPE_INT_RGB);
         Graphics2D g = bi.createGraphics();
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);


       // Begin by drawing the background, a board where everything is invisible, and frame


         g.setColor(BACKGROUNDCOLOR);
         g.fillRect(0,0, SIZE*(WIDTH+2), SIZE*(HEIGHT+2));

         g.setColor(darkInvisible);
         g.fill(boardArea);

         if (circleBoard)
             {g.setColor(FRAMECOLOR);
              g.setStroke(new BasicStroke(FRAMESIZE));
              g.draw(boardArea);
              g.setStroke(new BasicStroke(1));
             }
         else
             {
             g.setColor(FRAMECOLOR);
             g.fillRect(SIZE-FRAMESIZE, SIZE-FRAMESIZE, SIZE*WIDTH+2*FRAMESIZE, FRAMESIZE);
             g.fillRect(SIZE*(WIDTH+1), SIZE, FRAMESIZE, SIZE*HEIGHT);
             g.fillRect(SIZE-FRAMESIZE, SIZE*(HEIGHT+1), SIZE*WIDTH+2*FRAMESIZE, FRAMESIZE);
             g.fillRect(SIZE-FRAMESIZE, SIZE, FRAMESIZE, SIZE*HEIGHT);
             }

  //--------  Now generate areas for white, black and on

           Area whiteArea = iPlayWhite ? myArea : opponentVisibleArea;
           Area blackArea = iPlayWhite ? opponentVisibleArea : myArea;


  //--------  Paint the areas: my, opponent's and neutral (visible)


           g.setColor(NEUTRAL);
           if (allVisible) g.fill(boardArea); else g.fill(neutralArea);

           g.setColor(BLACKCONTROLLED);
           g.fill(blackArea);

           g.setColor(WHITECONTROLLED);
           g.fill(whiteArea);



 //--------- Paint the buildable area

           g.setColor(iPlayWhite ? WHITEBUILD : BLACKBUILD);
            g.setPaint(new TexturePaint(iPlayWhite ? whiteBuild : blackBuild, textureRect));
           g.fill(buildableArea);

 //---------- Paint the highlighted area

           g.setColor(iPlayWhite ? WHITEHIGHLIGHT : BLACKHIGHLIGHT);
           g.fill(highlightArea);

 //---------- Paint the hills

           if (withHills)
               for (Hill hill : hills) hill.draw(g,SIZE);

 //----------- Now paint all pieces

       for (Piece thePiece : pieces)                                     // For all pieces
       {
           if (thePiece == currentPiece) thePiece.draw(g, Color.red);             //Currently grabbed piece is red
           else if (thePiece.myside ||                                              // OW, determine if it is visible
                    myArea.contains(thePiece.pos.x, thePiece.pos.y) ||
                    neutralArea.contains(thePiece.pos.x, thePiece.pos.y) ||
                    opponentVisibleArea.contains(thePiece.pos.x, thePiece.pos.y)
                   ) thePiece.draw(g);                                           // if it is then draw it

           }

 //----------- Paint line of remaining pebbles

        if (allVisible)                                                        // if allVisible then draw both white and black Pebbles
        {for (int i=1; i<= remaining(true); i++)
              myPebble.draw(g, new Position(i*SIZE+SIZE/2,SIZE/2), Color.white);
         for (int i=1; i<= remaining(false); i++)
              myPebble.draw(g,new Position( i*SIZE+SIZE/2, SIZE*(HEIGHT +1) + SIZE/2), Color.black);}
        else                                                                  // else only show my Pebbles
         for (int i = 1; i<=remaining(iPlayWhite); i++)
             myPebble.draw(g, new Position(i*SIZE+SIZE/2, SIZE/2), iPlayWhite ? Color.white : Color.black);



//------------  Show who controls most area

      if (iPlayWhite) g.setColor(Color.white); else g.setColor(Color.black);
      g.setFont(myFont);
      if (relativeNumberOfControlledSquares<0) g.setColor(Color.red);
      g.drawString(relativeNumberOfControlledSquares+" ",SIZE/2,SIZE/2);

//--------------- show selection pieces

       if (!playBackBoard)          // In a playback no selectors are shown.

       for (Piece selector : initialselectors) {
           if (selector.shouldShowSelector())
               selector.drawOutline(g, (iPlayWhite ? Color.white : Color.black));
       }

 //--------------debug: show regions

         if (debug)
         { g.setColor(Color.green);
             for (Region region : debugRegions)
                 g.draw(region.area);
         }


 // -------------- Finally, paint all this on the proper board image

          boardGraphics.drawImage(bi,null,0,0);
          timeGraphics = (int)(System.currentTimeMillis() - startTime);
}

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
// paint: paints the board,using the buffered image
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

  void paint (Graphics g,             // Where to paint it
                    boolean  waiting,       // true if timer not expired, so player cannot move
                    boolean  stalling,      // true if "Stalling" should be shown
                    int      clock         // number of seconds remaining
                   )


{
         Graphics2D page = (Graphics2D)g;
         page.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
         page.drawImage(boardImage,null,0,0);       // render the buffered image

// --------------- Show if I can move

       if (!waiting & !playBackBoard) myPebble.draw(page,(new Position(SIZE/2,SIZE*(HEIGHT+1))), Color.red);

// ---------------- Show clock

       page.setColor(Color.black);
       page.setFont(clockFont);
       if (clock <= 10)                      // close to timout: clock is red and larger
        {page.setColor(Color.red);
         page.setFont(myFont);}
       page.drawString(clock/60 + ":" + (clock%60)/10 + (clock%60)%10,
                  (SIZE*WIDTH+SIZE/2),SIZE/2);

// ----------------- Some diagnostics and debug info
       if (debug)
       {page.drawString("U: "+numberOfUncontested+" ", SIZE*(WIDTH+1), SIZE*(HEIGHT-1)/2);
       page.drawString("R: "+numberOfRegions+" ", SIZE*(WIDTH+1), SIZE*HEIGHT/2);

       page.drawString("B: " + numberOfBox, SIZE*(WIDTH+1), SIZE*(HEIGHT+1)/2) ;
       page.drawString("I: " + numberOfIntersection, SIZE*(WIDTH+1), SIZE*(HEIGHT+2)/2) ;
       page.drawString("Ct: "+ timeCalculate,  SIZE*(WIDTH+1),SIZE*(HEIGHT+4)/2);
       page.drawString("It: "+ timeInit,  SIZE*(WIDTH+1),SIZE*(HEIGHT+5)/2);
       page.drawString("Mt: "+ timeMy,  SIZE*(WIDTH+1),SIZE*(HEIGHT+6)/2);
       page.drawString("Ht: "+ timeOpponents,  SIZE*(WIDTH+1),SIZE*(HEIGHT+7)/2);
       }

// ------------------ show stall message

       if (stalling)
       {page.setColor(Color.red);
         page.setFont(stallFont);
         page.drawString("S T A L L I N G",SIZE*3,SIZE/2);
       }
 }



    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Recalculate control of each area
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    //-------------------------------------------------
    //  A piece is uncontested if its range does not intersect the range of any opposite piece
    //-------------------------------------------------

     private boolean uncontested(Piece p)
     {boolean res = true;                       // result will be true unless we find a
        for (Piece other : pieces)              // piece, call it other
          {if ((p.myside != other.myside)       // playing for the opposite side
             && p.currentRange + other.currentRange >= p.pos.distance(other)) // and so close that the range areas overlap
           res = false;
          }
      return res;
     };


     //-----------------------------------------------
     //  Inner class Region: a part of the board
     //-----------------------------------------------

     // During the recalculation of control areas the board is partitioned into regions. The regions are formed
     // by intersections of the piece range areas.
     // Each region has an Area which it occupies, an integer control value which is the amount of control
     // exerted on it so far (positive for my side, negative for opponent, zero for neutral), a boolean visibility
     // indicator which tells if that region should be painted on the board, and Rectangle bounding box.
     // The reason for the latter is that it is more efficient to perform some of the computations on
     // bounding boxes before emabarking on more time consuming calculations on Areas.


     private class Region
     { Area area;                // the area on the board of the region
       int control;              // control degree measured so far (negative = opponent)
       boolean visible;          // true if visible
       Rectangle boundingBox;    // bounding box of the area
       Position center;          // center of the bounding box
       float radius;             // radius of the bounding box. So all positions in the area is within radius of the center.

     //----- Constructor just sets things up and calculates the bounding box

       private  Region (Area a, int c, boolean v)
       {area = a; control = c; visible = v;  setBounds();}



     //------ (re)calculate the bounding box, its center and radius

       private void setBounds()
       {boundingBox = area.getBounds();
        center = new Position (boundingBox.x + boundingBox.width/2, boundingBox.y+boundingBox.height/2);
        radius = center.distance(new Position(boundingBox.x, boundingBox.y));
       }


     //------------ decided: check if my control can possibly fall to zero or below


       private boolean decided (ArrayList<Piece> pieces)                          // parameter is opponent pieces to take into consideration
       {
        int c = control;                                                            // let c be the lowest control I can possibly reach
        for (Piece inspected : pieces) {                                           // as long as c is positive, and for all pieces
            if (center.distance(inspected) <= inspected.currentRange + radius)    // if it is so close that it might have an influence on my area
                c -= inspected.power;                                             // decrease c by its power
        }
        return c>0;                                                               // return true iff c is still positive
       }

     //------------- addMe: add me to a list of regions unless my fate can already be inferred

     // The list of regions is the list of still undecided regions who need further examination
     //
     // If my control is negative it can only decrease, so add me to his area
     // else if it is decided (cannot become non-positive) then add me to my area
     // else keep me in the list of regions

       private void addMe(ArrayList<Region> regList, ArrayList<Piece> pieces)
       {   if (control < 0)                                           // if control <0 then
              {opponentArea.add(area);                                     // it can only decrease further so
              if (debug) debugRegions.add(this);                      // add it to his area and
              if (visible) opponentVisibleArea.add(area);}                 // not to the region list
           else                                                       // if control >= 0
           if (decided(pieces))                                       // and the area is deceidedly mine
              {myArea.add(area);if (debug) debugRegions.add(this);}   // then add it to my area
           else regList.add(this);                                    // else add it to the list of regions
       }

     //------------- checkPiece--------------------------
     //
     // This determines what happens to the me when a Piece is taken into consideration.
     // If the range area completely encloses me, then update the control value with the amount
     // exerted by the piece. If the range area is disjoint from me then do nothing. If it intersects me
     // then create a new region corresponding to the intersection, and subtract it from my area. For
     // efficiency the predicates are first tested on my bounding box.
     // The resulting  regions are accumulated in the second parameter. The third
     // parameter is a list of enemy pieces to consider when determining if the region is decided.

       private void checkPiece(Piece p, ArrayList<Region> newRegions, ArrayList<Piece> pieces)
        { if (center.distance(p)  >  radius + p.currentRange) newRegions.add(this);  // area completely out of range: just forward the area to newRegions
          else
             if
             (p.rangeArea.contains(boundingBox))                    // If my bounding box lies completely in range
               {control += p.power * (p.myside?1:-1); numberOfBox++;// update my control value with the power of the piece
                addMe(newRegions, pieces);}                         // and add me to the new regions
             else if ( p.rangeArea.intersects(boundingBox))        // else, if the bounding box intersects the range
                 {Area newArea = p.rangeArea();                       // create a new area that is the intersection
                  newArea.intersect(area);                            // with the range area
                  numberOfIntersection++;                             // (debug)
                  if (newArea.equals(area))                           // if that turns out to be the whole area
                       {control += p.power * (p.myside?1:-1);         // just update my control with the piece power
                        addMe(newRegions, pieces);}                   // and add me to the new regions
                  else if (!newArea.isEmpty())                        //  else, if that turns out to be nonempty
                       {area.subtract(newArea);                        // subtract it from my area
                        setBounds();                                   // recalulate my bounding box
                        addMe(newRegions, pieces);                     // and add me to newRegions
                        Region newRegion = (new Region(newArea,        // and create a new region for the intersection
                                  control + p.power * (p.myside?1:-1),// with updated power
                                  p.myside | visible));                // and visibility the same as before, or true if piece on my side
                        newRegion.addMe(newRegions,pieces);            // and invoke its addMe for adding it to newRrgions
                       }                                               // if the intersection is empty
                   else addMe(newRegions, pieces);                    // then just forward me to new Regions
                  }                                                    // also if intersection of bounding box is empty
             else addMe(newRegions, pieces);
             numberOfBox++;
       }
     }

    //-----------------end class Region


    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // calculateControl: determine myArea, opponentArea, opponentVisibleArea, onAreas. Also count number of pieces on board
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

     void calculateControl()

    //----------  first reset and initialise variables

    {           long startTime = System.currentTimeMillis();



       initPiecesOnBoard();             // reset variables and areas
       myArea.reset();
       opponentArea.reset();
       opponentVisibleArea.reset();
                numberOfBox=0;
                numberOfIntersection=0;
       neutralArea.reset();
                numberOfUncontested=0;
                debugRegions.clear();

       ArrayList<Piece> myPieces = new ArrayList();                                // the subset of pieces that are mine
       ArrayList<Piece> opponentPieces = new ArrayList();                          // the subset of pieces that are the opponent's

       ArrayList<Region> regions = new ArrayList();                                 // initialise the "regions" to contain just one,
       regions.add(new Region((Area)boardArea.clone(),0,allVisible));               // the entire board with control 0

     // Sort pieces into mine and opponent's, update number records,
     // Let uncontested pieces exert directly (these will not need to generate regions)

        for (Piece inspected : pieces)
        {
         if (inspected.myside)                                     // if it is my piece
                {
                 incrementPieceOnBoard(inspected.pieceType);                    // update the record of number of pieces
                 {if (!uncontested(inspected)) myPieces.add(inspected);  // add it to the list of my pieces
                 else {myArea.add(inspected.rangeArea);                // or, if uncontested, add the range area directly
                       numberOfUncontested++;}
                 }
               }
              else                                                    // if it is opponent's piece
                {
                 if (!uncontested(inspected))opponentPieces.add(inspected); // and add it to the list of opponent pieces
                  else                                                  // or, if uncontested
                  {opponentArea.add(inspected.rangeArea); numberOfUncontested++;
                  if (allVisible) opponentVisibleArea.add(inspected.rangeArea); // add it to opponentArea or opponentVisibleArea
                  }
                }
        }                                                            // end for each piece

                    timeInit = (int)(System.currentTimeMillis() - startTime);

     //----------- check the influence of all my contested pieces on the control areas
     //
     // This will generate regions of increasing positive control, as the range areas intersect.

                    long s2 = System.currentTimeMillis();

        for (Piece inspected : myPieces)          // for each of my pieces
         {
          ArrayList<Region> newRegions = new ArrayList();                           //  the  regions that need to be examined after this
          for (Region thisRegion : regions)      // for each existing region
            thisRegion.checkPiece(inspected, newRegions, opponentPieces); // let the region check the piece for possible influence on its area and control                                                             // end for all regions
          regions = newRegions;                                      // continue with the newly generated regions
          }                                                          // end for each of my pieces

                    timeMy = (int)(System.currentTimeMillis() - s2);

     //--------- check the influence of opponent pieces on the control areas
     //
     // This will generate regions with decreasing control.

                     s2 = System.currentTimeMillis();
        while (!opponentPieces.isEmpty())                   // for each of opponent's pieces
        {Piece inspected = (Piece)opponentPieces.get(0);              // call it inspected
         opponentPieces.remove(inspected);                                   // remove it from opponentPieces (so it cannot unduly affect the screening by "decided")
         ArrayList<Region> newRegions = new ArrayList();                             // reset the new regions to be generated
         for (Region thisRegion : regions)                        // for each region
           thisRegion.checkPiece(inspected, newRegions, opponentPieces);  // let the region check the piece for possible influence on its area and control
        regions = newRegions;                                       // continue with the newly generated regions
        }                                                              // end for each of his pieces
                    if (debug) debugRegions.addAll(regions);
                    numberOfRegions = debugRegions.size();
                    timeOpponents = (int)(System.currentTimeMillis() - s2);
                    s2 = System.currentTimeMillis();

     //--------  collect the regions into the main variables

        for (Region thisRegion : regions)
           {
            if (thisRegion.control>0) myArea.add(thisRegion.area);
            if (thisRegion.control == 0 & thisRegion.visible) neutralArea.add(thisRegion.area);
            if (thisRegion.control < 0 & thisRegion.visible) opponentVisibleArea.add(thisRegion.area);
            if (thisRegion.control < 0) opponentArea.add(thisRegion.area);
           }

                 s2 = System.currentTimeMillis();

        calculateRelativeNumberOfSquares();                       // finally calculate who controls most territory

                timeInit  = (int)(System.currentTimeMillis() - s2);
                timeCalculate = (int)(System.currentTimeMillis() - startTime);

    }


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // calculateRelativeNumberOfSquares: check who controls most territory
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    // result is stored in the global variable relativeNumberOfControlledSquares.
    // the algorithm is to generate a grid of points (of cardinality numberOfSamples^2)
    // and for each point check membership in myArea or hisArea.




     private void calculateRelativeNumberOfSquares()

     {final float sampleIncrementX = (SIZE*WIDTH)/numberOfSamples;              // distance between sample points
      final float sampleIncrementY = (SIZE*HEIGHT)/numberOfSamples;
      final float scaleFactor = numberOfSamples*numberOfSamples/HEIGHT/WIDTH;   // to scale the result down to something meaningful
      relativeNumberOfControlledSquares=0;
      for (float i=SIZE; i<=SIZE*(WIDTH+1); i=i+sampleIncrementX)               // so just check all sample points
             for (float j=SIZE; j<=SIZE*(HEIGHT+1); j=j+sampleIncrementY)
                 {if (myArea.contains(i,j)) relativeNumberOfControlledSquares++;
                  else if (opponentArea.contains(i,j)) relativeNumberOfControlledSquares--;
                 }
        relativeNumberOfControlledSquares = Math.round(((float)(relativeNumberOfControlledSquares)/(float)scaleFactor));
     }



    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Recalculate buildble squares
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

     void calculateBuildable()


      {         long startTime = System.currentTimeMillis();
       buildableArea.reset();
       if (myPebble.shouldShowSelector())                         // buildable only if I can build a Pebble
       {for (Piece inspected : pieces)                           //   For each piece
           {
            if (    inspected.pieceType == Piece.Type.RUBBLE                // if it is a Rubble
                  & inspected.myside                                      // on my side
                  & myArea.contains(inspected.pos.x,inspected.pos.y)      // and controlled by me
                ) buildableArea.add(inspected.nearArea());                // add its near area to buildable
            }
         buildableArea.intersect(myArea);                                 // but buildable area must be controlled
         for (Piece inspected : pieces)                           //   For each piece
             buildableArea.subtract(inspected.avoidBuildArea);
                timeBuildable = (int)(System.currentTimeMillis() - startTime);
         }
       }



    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Recalculate highlighted area
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

     void calculateHighlight(Piece currentPiece)
    {       long startTime = System.currentTimeMillis();
     for (Piece inspected : pieces)                           //   For each piece
           inspected.underAttack = false;                       // set its "underAttack" to false


       if (currentPiece != null)                                        // only if there is a current piece
         {highlightArea = currentPiece.strideArea();                    // begin by highlighting its stride area
          highlightArea.intersect(myArea);                              // that is in my control

         for (Piece inspected : pieces)                           //   For each enemy piece
            if (!inspected.myside & highlightArea.contains(inspected.pos.x,inspected.pos.y))  // that sits in this area
                
                inspected.underAttack=true;                              //set the underAttack flag


          for (Piece inspected : pieces)                           //   For each piece except the currentPiece
               if (inspected != currentPiece) highlightArea.subtract(inspected.avoidArea(currentPiece)); // subtract the avoidArea.
         }
       else highlightArea.reset();                                        // no current piece: higlightArea is null
                timeHighlight = (int)(System.currentTimeMillis() - startTime);
       generateBoardGraphics(currentPiece);                             // this always prompts a regeneration of board graphics

      }



     }// end class Board








//*******************************************************************
//
// A piece
//
//*******************************************************************

abstract class Piece

{
    // ----------- The following parameters uniquely determine the piece type. Change them when devising new pieces.

  Type   pieceType;                    // Type of piece
  int    range;                        // Range  (how far control extends)
  int    currentRange;                 // range at this position (may be increased by a hill)
  int    power;                        // Power  (how much control)
  int    stride;                       // stride (how far it can move)
  int    extent;                       // extent (how much space it occupies )
  int    moveTime;                      // delay after piece moved (units of Nimbler move)
  int    buildTime;                     // delay after piece built
  int    MAXIMUM;                       // Max number of pieces allowed simultaneously on the board of a type
  boolean attacker;
  Area   rangeArea,                     // Area that piece controls
         strideArea,                    // Area it can move to
         extentArea,                    // Area it occupies (used to determine when you click on the piece)
         nearArea,                      // only for rubbles, used to determine buildable area
         avoidBuildArea;                // Area too close to permit builds
  boolean moveable;                     // true if can move

  // ------------ Fields used for game state---------------------------------------------------------------------------

  boolean whiteside;                  // true if white
  boolean myside;                     // true if my, false if opponent's

  Board.Position pos;                 // where it sits on the board
  boolean underAttack;                // true if it is under enemy control

  Board  board;                        // the board where the piece sits

  Piece outline = this;                // The outline to show when dragging it (usually itself)

  private    int   nPoly;                        // graphics for range areas etc
  private    float[] xPoly;
  private    float[] yPoly;



  final static private Color WHITE_NEUTRAL = new Color(210,210,210);     // Various colors of pieces when not controlled by owner
  final static private Color WHITE_OPPONENT = new Color (180,210,250);
  final static private Color BLACK_NEUTRAL = new Color (80,80,80);
  final static private Color BLACK_OPPONENT = new Color (130,80,80);

  enum Type
      {PEBBLE(0), RUBBLE(1), KEEP(2), BOUNCER(3), QUORUM(4), NIMBLER(5), GIANT(6);          // The six kinds of pieces
       int index;                                                       // each kind has an integer index
       Type(int idx) {
           index = idx;}
    }


  static Piece  newPiece(Type pt, boolean w, boolean my, Board.Position p, Board b)              // create new piece of right type
  {
      Piece res = null;           // ow compiler warns it may be undefined
       switch (pt)
          {case PEBBLE:  res= new Pebble( w, my, p, b); break;
           case RUBBLE:  res= new Rubble( w, my, p, b); break;
           case QUORUM:  res= new Quorum( w, my, p, b); break;
           case BOUNCER: res= new Bouncer(w, my, p, b); break;
           case NIMBLER: res= new Nimbler(w, my, p, b); break;
           case KEEP:    res= new Keep(   w, my, p, b); break;
           case GIANT:   res=new Giant(w,my,p,b); break;
          }
       res.pieceType = pt;
       return res;
}
//--------------------------------------------------------------------
// General Piece constructor
//--------------------------------------------------------------------

   Piece(boolean w, boolean my, Board.Position pos, Board board)
   {
        stride = board.SIZE;               // set default values
        range  = board.SIZE;
        power  = 1;
        extent = board.SIZE/2 ;
        moveable = true;
        moveTime  = 2;
        buildTime = 6;
        attacker=true;
        whiteside = w;                          // set values from params
        this.pos = pos;
        this.board = board;
        myside = my;

     // Set a polygon to approximate a circle

        if (board.shape == 2) nPoly = 6; else nPoly = 28;   // Determine polygon degree from shape param

        if (board.shape ==2 | board.shape ==3)              // define a regular nPoly-polygon
          {xPoly = new float[nPoly];
           yPoly = new float[nPoly];
           for (int i=0; i<nPoly; i++)
            {xPoly[i] = (float)Math.sin(2*Math.PI*(i+0.5)/nPoly);
             yPoly[i] = (float)Math.cos(2*Math.PI*(i+0.5)/nPoly);
            }

          }

   }

 //----------------------------------------------------------------------------------
 // circArea: generate an area around pos with given radius. if isCirc it is a perfect circle, OW as determined by shape
 //----------------------------------------------------------------------------------

  private Area circArea (int radius, boolean isCirc)
     {
      Shape circ = null;

      // isCirc: generate circle

      if (isCirc) circ = new Ellipse2D.Float((int)(pos.x-radius), (int)(pos.y-radius),
                                (int)(2*radius),(int)(2*radius));

      // shape == 1: generate square

      else if (board.shape == 1) circ = new Rectangle((int)(pos.x-radius/Math.sqrt(2)), (int)(pos.y-radius/Math.sqrt(2)),
                                (int)(radius*Math.sqrt(2)),(int)(radius*Math.sqrt(2)));

      // OW generate nPoly-polygon

      else if (board.shape == 2 | board.shape==3)
      {
       int [] x = new int [nPoly];
       int [] y = new int [nPoly];
       for (int i=0;i<nPoly;i++)
         {x[i] = (int)(pos.x + radius*xPoly[i]);
          y[i] = (int)(pos.y + radius*yPoly[i]);
         }
       circ = new Polygon(x,y,nPoly);
      }

      return new Area(circ);
    }

    //--------------------------------------------------------------------------------
    // Area functions
    //--------------------------------------------------------------------------------

    // setAreas just defines the Area fields, typically after the piece has moved

    void setAreas()

    { currentRange = (int)( range * board.elevation(pos));   // adjust the range: if on a hill multiply with its height
      rangeArea = circArea(currentRange,false);
      rangeArea.intersect(board.boardArea);        // range only on board
      extentArea = circArea(extent, true);
      strideArea = circArea(stride,false);
      avoidBuildArea = circArea(board.SIZE*2/3,true);
      nearArea = circArea(board.SIZE,true);


    }

    // Generate area clones

    Area rangeArea ()
    {return (Area)rangeArea.clone();}

    Area strideArea()
    {return (Area)strideArea.clone();}

    Area nearArea()
    {return (Area)nearArea.clone();}

    // Generate avoidarea: where a piece of type p may not come

    Area avoidArea(Piece p)
    {return circArea(p.extent + extent, true);}




    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //
    //  Graphic display methods
    //
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    abstract Area appearance(Board.Position pos);          // Each piece must define an appearance


    //---------------------------------------------------------------------
    // Draw an outline of a piece type with specified color at specified position
    //---------------------------------------------------------------------


    void drawOutline(Graphics g, Board.Position pos, Color color)
                {Graphics2D page2 = (Graphics2D)g;
                 g.setColor(color);
                 if (color == Color.red) page2.setStroke(new BasicStroke(2));
                 page2.draw(appearance(pos));
                 page2.setStroke(new BasicStroke(1));
                }



    //---------------------------------------------------------------------
    // Draw a piece type with specified color, including a bounding outline of opposite color
    //---------------------------------------------------------------------

     void draw(Graphics g,  Board.Position pos, Color color)
                {Graphics2D page2 = (Graphics2D)g;
                 g.setColor(color);
                 page2.fill(appearance(pos));
                  drawOutline(g, pos, flipColor(color));
                }




    //--------------------------------------------------------------
    //  overloaded methods for drawing this instance of a piece.
    //  only draw it if it is on a visible square or outside the board.
    //--------------------------------------------------------------

    void draw(Graphics g)     // Determine the correct color and then draw the piece

    {   boolean whitecontrolled =    (whiteside == myside) ? board.iControl(this) :  board.opponentControls(this);
        boolean blackcontrolled =    (whiteside != myside) ? board.iControl(this) :  board.opponentControls(this);

        Color color = whiteside ?   (whitecontrolled ? Color.white : (blackcontrolled ? WHITE_OPPONENT : WHITE_NEUTRAL))
                                :   (blackcontrolled ? Color.black : (whitecontrolled ? BLACK_OPPONENT : BLACK_NEUTRAL));
        if (underAttack)
             color = whitecontrolled ? Board.WHITEHIGHLIGHT : Board.BLACKHIGHLIGHT;
             draw(g,  pos, color);}


    //-----------------------------------------------------------------

    void draw(Graphics g, Color altcolor)   // Draw the piece with a specified color
    {
                draw(g, pos, altcolor);
    }

    void drawOutline(Graphics g, Color altcolor)  // Draw an outline with specified color
    {
                drawOutline(g,  pos, altcolor);
    }

    //-----------------------------------------------------------------

    private Color flipColor(Color color)             // used to determine border of filled piece
         {return (whiteside & !(color == Color.black) ? Color.black : Color.white);}


    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //
    //   Other utility methods
    //
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    //---------------------------------------------------------------------
    // Determine when a selector should be shown (this is refined by subclasses)
    //---------------------------------------------------------------------

     boolean shouldShowSelector ()
     {return (MAXIMUM > board.howManyOnBoard(pieceType) && specificSelector());}

     abstract boolean specificSelector();     // a subclass should define this to refine shouldShowSelector

    //------------------------------------------------------------------------------
    // Default canBuild, determines if a piece of this type can be built at position p.
    //   refined by subclasses. side = true means it is me who builds, ow opponent builds
    //-------------------------------------------------------------------------------

    boolean canBuild(Board.Position p)
    { return (board.isLegal(p)                                    // must be on the board
              &&  board.iControl(p)                               // on a controlled square
              && MAXIMUM > board.howManyOnBoard(pieceType));      // and not exceeding max allowed of piece type

    }


    //-------------------------------------------------------------------------------
    // Default canMove, determines if this piece can move to p
    //-------------------------------------------------------------------------------

    boolean canMove (Board.Position p)
    {if(board.find(p)!=null && board.find(p).myside!=myside && !this.attacker){
        return false;
    }
        if (p != null && board.canMoveTo(p))              // Can only move to highlighted area
           return !p.equals(pos);                         // but not to the same position!
        else
        {Piece inhabitant = board.find(p);                // or to enemy piece under attack
         return inhabitant != null && inhabitant.myside != myside && inhabitant.underAttack;
        }


    }


    //--------------------------------------------------------------
    // Move the piece to another place on the board
    //--------------------------------------------------------------

     Piece moveTo(Board.Position pos)
     {Piece victim = board.find(pos);                 // check if there is a victim on the destination position
      if (victim != null){
          if(this.attacker){
          board.removePiece(victim);  // if so remove it
       this.pos = pos;                               // then adjust the position of the piece
       setAreas();                                  // and recalculate its areas
       }}
      if(victim==null){
       this.pos = pos;
       setAreas();
       }
      return this;
     }



    //--------------------------------------------------------------
    //  Check if a particular position is near the piece
    //--------------------------------------------------------------

    boolean near (Board.Position pos)
     {return  nearArea.contains(pos.x,pos.y);}


    //--------------------------------------------------------------
    //  Check if a particular position is on the piece
    //--------------------------------------------------------------

    boolean on (Board.Position pos)                                 // If the position is closer than the extent of the piece
    {return extentArea.contains(pos.x,pos.y);}

}        // End class Piece


//********************************************************************
//
// Pebble
//
//********************************************************************

class Pebble extends Piece
{
    final private  int OSIZE = board.SIZE/3;  // Size of the Pebble

    final private Keep myKeep = new Keep(true, true, null, board);   // A Keep, just to be able to show an outline when promoting a rubble

   //-------------------------------------------------------------------
   // Construct a pebble: construct a Piece, set its type to pebble, and decrease remaining pebbles
   //-------------------------------------------------------------------

      Pebble(boolean w, boolean my, Board.Position pos, Board board)
    {
      super(w,my,pos,board);
      MAXIMUM=8;
      extent = OSIZE/2;
      if (board.isLegal(pos)) board.decreaseRemaining(w);             // decrease remaining pebbles if on the board
    }                                                                 // (ow it is a selector)


   //---------------------------------------------------------------------
   // Appearance
   //---------------------------------------------------------------------

   Area appearance(Board.Position pos)                      // Appearance is just a circle
    {return new Area(new Ellipse2D.Float(pos.x-OSIZE/2,
                                pos.y-OSIZE/2,
                                OSIZE,
                                OSIZE));}



   //---------------------------------------------------------------------
   // Determine conditions for showing selector pebble
   //---------------------------------------------------------------------

    boolean specificSelector()

     {return  board.howManyOnBoard(Type.RUBBLE) > 0                     // A rubble on the board
              && board.remaining(board.iPlayWhite)  > 0;              // and pebbles remaining to put into play

     }


    //---------------------------------------------------------------------
    // Determine conditions for building a pebble
    //---------------------------------------------------------------------
    @Override
    boolean canBuild(Board.Position p)
    {
       return board.canBuildPebbleOn(p);}  // Builds possible on buildable area


    //----------------------------------------------------------------------
    // Effects of moving a pebble (overrides Piece.moveTo because this can create a keep
    //----------------------------------------------------------------------

    @Override
    Piece moveTo(Board.Position pos)
        {Piece dest = board.find(pos);
         if (dest == null || dest.pieceType != Type.RUBBLE || dest.myside != myside)       // An ordinary move
             return super.moveTo(pos);                                                   // is just as done by super
         else {board.removePiece(dest);                                                  // but a keep-building move
               board.removePiece(this);                                                  // means removing both pebble and rubble
               Piece res = board.newPiece(Type.KEEP,whiteside,myside,pos);                 // and inserting a new keep
               moveTime = res.buildTime;                                                 // and this takes as long as to build a keep!
               return res;                                                               // (no need to reset moveTimet since this pebble disappears)
               }
         }



    @Override
    boolean canMove(Board.Position p)
    {outline = this;                                    // the outline is only changed by canKeep
     return super.canMove(p) || canKeep(p);}            // Either an ordinary move or a keep build


    //---------------------------------------------------------------------
    // Determine conditions for building a keep out of a pebble
    //---------------------------------------------------------------------

    boolean canKeep(Board.Position p)
    {
        if (board.isLite) return false;                                                   // In a lite game you can never build a Keep
        else {Piece inhabitant = board.find(p);
              boolean res =                                                             // Ow you can construct a Keep
                      (
                                        board.isLegal(p)                                // to a place on the board
                                     && inhabitant != null                              // which is inhabited
                                     && inhabitant.pieceType == Type.RUBBLE               // by a rubble
                                     && inhabitant.myside == myside                     // on our side
                                     &&  strideArea.contains(p.x,p.y)                   // not too far away
                                     && board.iControl(p)                  // destination rubble must be controlled
                                     && myKeep.MAXIMUM > board.howManyOnBoard(Type.KEEP)               // not too many keeps already
                      );
              if (res) outline = myKeep;                                                // change outline if I can build a keep
              return res;
             }
      }

}         // End class Pebble


//*****************************************************************
//
// Rubble
//
//*****************************************************************

 class Rubble extends Piece
{

     final private int RSIZE = (board.SIZE * 25)/60;         // graphical size

    //--------------------------------------------------------------------
    // Rubble constructor
    //--------------------------------------------------------------------

     Rubble (boolean w, boolean my, Board.Position pos, Board board)
    {
        super(w,my,pos, board);       // Construct a piece
        range = (int)board.SIZE/3;    // with very small range
        moveable = false;             // not moveable
        stride = 0;
        MAXIMUM = 2;                  // max 2 allowed on board
        extent = board.SIZE/3;        // large extent because this may become a Keep

        }

    //---------------------------------------------------------------------
    // Appearance
    //---------------------------------------------------------------------

     Area appearance(Board.Position pos)                 // Appearance is a square
        {return new Area(new Rectangle2D.Float(pos.x-RSIZE/2,
                                    pos.y-RSIZE/2,
                                    RSIZE,
                                    RSIZE));}




    //---------------------------------------------------------------------
    // Determine when selector should be shown
    //---------------------------------------------------------------------

    boolean specificSelector()

      {return  board.howManyOnBoard(Type.PEBBLE) > 0;}     // Pebbles on the board

    //---------------------------------------------------------------------
    // Determine when rubble can be built
    //---------------------------------------------------------------------


    @Override
    boolean canBuild(Board.Position p)
    {
        boolean res;
        res = super.canBuild(p)               // Can build piece
        && board.near(Type.PEBBLE, p);    // and near a pebble
        return res;
    }
}     // End class Rubble



//*******************************************************************
//
// Keep
//
//*******************************************************************

class Keep extends Piece
    {
      final private int BSIZE = board.SIZE;
      final private int KM =  (BSIZE * 10) / 60;           // sizing constants for graphics
      final private int KW =  (BSIZE * 15) / 60;


                                        // the keep polygon shape

      final private int[] Kx = {KM, BSIZE/2-KW/2, BSIZE/2-KW/2, BSIZE/2+KW/2, BSIZE/2+KW/2, BSIZE-KM,
                         BSIZE-KM, BSIZE/2+KW/2, BSIZE/2+KW/2, BSIZE/2-KW/2, BSIZE/2-KW/2, KM};
      final private int[] Ky =   {BSIZE/2-KW/2, BSIZE/2-KW/2, KM, KM, BSIZE/2-KW/2, BSIZE/2-KW/2,
                         BSIZE/2+KW/2, BSIZE/2+KW/2, BSIZE-KM, BSIZE-KM,BSIZE/2+KW/2, BSIZE/2+KW/2};
      final private int Klength = 12;

      private int kx[] = new int[12];       // temps when drawing
      private int ky[] = new int[12];


    //--------------------------------------------------------------------
    // Keep constructor
    //--------------------------------------------------------------------

     Keep(boolean w, boolean my, Board.Position pos, Board board)
        {
        super(w,my,pos,board);         // construct a piece
        moveable = false;             // not moveable
        stride = 0;
        extent = board.SIZE/3;
        MAXIMUM = 2;
        moveTime = 8;               // delay of a build (because pebble creates a keep through a move)
        }

    //---------------------------------------------------------------------
    // Appearance
    //---------------------------------------------------------------------

       Area appearance(Board.Position pos)
        {for (int i=0; i<Klength; i++)
          {kx[i] = Kx[i] +  pos.x - BSIZE/2;
            ky[i] =Ky[i] +  pos.y - BSIZE/2;}
         return new Area (new Polygon(kx,ky, Klength));
        }


    //---------------------------------------------------------------------
    // Never show selector
    //---------------------------------------------------------------------

    boolean specificSelector()
      {return  false;
      }

    //---------------------------------------------------------------------
    // Never build it (because it is created by a pebble move)
    //---------------------------------------------------------------------

    @Override
    boolean canBuild(Board.Position p)
    {   return false;
    }

}    // End class Keep



//*******************************************************************
//
// Heavy Piece: What is common for Quorum, Bouncer and Nimbler
//
//*******************************************************************

 abstract class HeavyPiece extends Piece
 {

     HeavyPiece(boolean w, boolean my, Board.Position pos, Board board)
            {super(w,my,pos,board);       // construct a piece
             MAXIMUM =2;                  // Heavy pieces have maximum = 2

    }

    //---------------------------------------------------------------------
    // Determine when heavy piece selector should be shown
    //---------------------------------------------------------------------

    boolean specificSelector()
          {return board.howManyOnBoard(Type.KEEP) > 0 && board.howManyOnBoard(Type.PEBBLE) > 0;}   // same for all heavy pieces


    //---------------------------------------------------------------------
    // Determine when heavy piece can be built
    //---------------------------------------------------------------------

    @Override
    boolean canBuild(Board.Position pos)
    {   boolean res;
        Piece inhabitant = board.find(pos);
        res = board.near(Type.KEEP, pos)               // near a keep
            && inhabitant != null                 // on a pebble
            && inhabitant.pieceType == Type.PEBBLE;
        return super.canBuild(pos) && res;
    }
}


//*******************************************************************
//
// Bouncer
//
//*******************************************************************

class Bouncer extends HeavyPiece
{
   final private int OSIZE =  board.SIZE/3;       // various constants for the graphic representation, in pixels
   final private int BWIDTH =(8 * board.SIZE) / 60 ;
   final private int BSIZE =  board.SIZE*3/4;




    //--------------------------------------------------------------------
    // Bouncer constructor
    //--------------------------------------------------------------------

    Bouncer(boolean w, boolean my, Board.Position pos, Board board)
    {
        super(w,my,pos,board);       // construct a piece
        range =2*board.SIZE;         // with range 2
        extent = board.SIZE/3;        // The bouncer is large!
        }

    //---------------------------------------------------------------------
    // Appearance
    //---------------------------------------------------------------------

     Area appearance(Board.Position pos)
        {   Area app =
                      new Area(new Ellipse2D.Float(pos.x-OSIZE/2,           // Appearance is a circle plus two polygons
                                                   pos.y-OSIZE/2,
                                                   OSIZE,
                                                   OSIZE));
             int[] Bx1 = {pos.x-BSIZE/2,  pos.x+BSIZE/2 - BWIDTH/2,  pos.x+BSIZE/2,  pos.x-BSIZE/2 + BWIDTH/2};
             int[] By1 = {pos.y-BSIZE/2 + BWIDTH/2,  pos.y+BSIZE/2,  pos.y+BSIZE/2 - BWIDTH/2,  pos.y-BSIZE/2};
             app.add(new Area (new Polygon(Bx1,By1,4)));
             int [] Bx2 = {pos.x+BSIZE/2 - BWIDTH/2,  pos.x-BSIZE/2, pos.x-BSIZE/2 + BWIDTH/2,  pos.x+BSIZE/2};
             int [] By2 = {pos.y-BSIZE/2,  pos.y+BSIZE/2 - BWIDTH/2,  pos.y+BSIZE/2, pos.y-BSIZE/2 + BWIDTH/2};
             app.add(new Area (new Polygon(Bx2,By2,4)));
             return app;
     }

}    // End class Bouncer



//*******************************************************************
//
// Quorum
//
//*******************************************************************

 class Quorum extends HeavyPiece {

  final private int OSIZE = board.SIZE / 3;       // size of graphic representation, in pixels

    //--------------------------------------------------------------------
    // Quorum constructor
    //--------------------------------------------------------------------

    Quorum(boolean w, boolean my, Board.Position pos, Board board)
    {
        super(w,my,pos,board);      // construct a piece
        power =2;                   // with power 2
        extent = 3*OSIZE/4;
        }

    //---------------------------------------------------------------------
    // Appearance
    //---------------------------------------------------------------------

     Area appearance(Board.Position pos)                 // Appearance is two overlapping circles
        {Area app = new Area(new Ellipse2D.Float(pos.x-OSIZE/2,
                                    pos.y-OSIZE/2+OSIZE/4,
                                    OSIZE,
                                    OSIZE));
       app.add(new Area(new Ellipse2D.Float(pos.x-OSIZE/2,
                                   pos.y-OSIZE/2-OSIZE/4,
                                   OSIZE,
                                   OSIZE)));
         return app;}

     //---------------------------------------------------------------------
     // drawOutline (overrides Piece.drawOutline in order to draw an extra circle
     //---------------------------------------------------------------------

    @Override
    protected void drawOutline(Graphics page, Board.Position pos, Color color)
    { super.drawOutline(page,  pos,  color);
      page.drawOval(pos.x-OSIZE/2,
                    pos.y-OSIZE/2+OSIZE/4,
                                    OSIZE,
                                    OSIZE);
    }

}   // End class Quorum

//*******************************************************************
//
// Nimbler
//
//*******************************************************************

class Nimbler extends HeavyPiece
{

    final private int NxSIZE = (board.SIZE * 5)/60;                 // graphic size
    final private int NySIZE = (board.SIZE * 20)/60;
    final private int OSIZE =  (board.SIZE * 15)/60;

    final private int[] Nx = {0,  NxSIZE, -NxSIZE, 0, NxSIZE, -NxSIZE};  // Nimbler polygon shape
    final private int[] Ny = {0, -NySIZE, -NySIZE, 0, NySIZE,  NySIZE};

    private int[] Nxx = new int[6];  // temps
    private int[] Nyy = new int[6];




    //--------------------------------------------------------------------
    // Nimbler constructor
    //--------------------------------------------------------------------

     Nimbler(boolean w, boolean my, Board.Position pos, Board board)
    {
        super(w,my,pos, board);          // construct a piece
        stride = board.SIZE*2;           // with stride 2
        moveTime = 1;                    // with a short move time
        extent = NySIZE;
        }

    //---------------------------------------------------------------------
    // Appearance
    //---------------------------------------------------------------------
     Area appearance(Board.Position pos)
        {Area app = new Area(new Ellipse2D.Float(pos.x-OSIZE/2,
                                    pos.y-OSIZE/2,
                                    OSIZE,
                                    OSIZE));
           for (int i=0; i<=5; i++) {Nxx[i] = Nx[i] + pos.x; Nyy[i] = Ny[i] + pos.y;}
           app.add (new Area(new Polygon(Nxx, Nyy, 6)));
           return app;

     }



}  // End class Nimbler
 class Giant extends HeavyPiece{

    final private int NxSIZE = (board.SIZE * 5)/30;                 // graphic size
    final private int NySIZE = (board.SIZE * 20)/30;
    final private int OSIZE =  (board.SIZE * 15)/30;

    final private int[] Nx = {0,  NxSIZE, -NxSIZE, 0, NxSIZE, -NxSIZE};  
    final private int[] Ny = {0, -NySIZE, -NySIZE, 0, NySIZE,  NySIZE};

    private int[] Nxx = new int[6];  // temps
    private int[] Nyy = new int[6];
    

     Giant(boolean w, boolean my, Board.Position pos, Board board){
       super(w,my,pos, board);   
       power=power*3;
       
       attacker=false;
     }
    @Override
    Area appearance(Board.Position pos) {
        for (int i=0; i<=5; i++) {Nxx[i] = Nx[i] + pos.x; Nyy[i] = Ny[i] + pos.y;}
        Area app = new Area(new Ellipse2D.Float(pos.x-OSIZE/2,
                                pos.y-OSIZE/2,
                                OSIZE,
                                OSIZE));
                
        app.add(new Area(new Polygon(Nxx,Nyy,3)));
   return app;}
     
     @Override
    boolean canBuild(Board.Position pos)
    {   boolean res;
        Piece inhabitant = board.find(pos);
        res = board.relativeNumberOfControlledSquares <0
            && inhabitant != null                 // on a pebble
            && inhabitant.pieceType == Type.PEBBLE;
        
        return super.canBuild(pos) && res;
    }
    
 }

// End of file




