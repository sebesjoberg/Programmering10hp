import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.nio.file.*;
import java.util.*;


/**
 *
 * @author ss691
 */
public class chattcontacts {

    public static void main(String args[]) {
        boolean isServer = true;
        int port = 7000;

        if (isServer == true) {
            new ServerContacts(port);

        }
        if (isServer == false) {
            new ClientContacts();
        }

    }
}

class ServerContacts {

    ServerSocket server;
    Socket socket;

    ServerContacts(int port) {

        try {
            server = new ServerSocket(port);

            socket = server.accept();

            new ChatParticipant(socket);

        } catch (IOException ex) {
            System.out.println("could not set up server");
        } finally {
            try {
                server.close();
            } catch (IOException ex) {

            }
        }

    }
}

class ClientContacts implements ActionListener{
    JButton b1,b2;
    JFrame frame;
    JFileChooser fc;
    String line1,line2,line3;
    Socket socket;
    String ip = "LocalHost";
   int port;
   String file_path="C:\\Users\\ss691\\contactsfchatt";
   PrintWriter print_line;
   String contactname;
    ClientContacts() {
        contacts();
        
//kontaktlista
    }
     private void contacts(){
    frame = new JFrame("choose contacts");
    fc = new JFileChooser();
    File kontakter = new File(file_path);
    fc.setCurrentDirectory(kontakter);
    b1 = new JButton("choose contact");
    b1.addActionListener(this);
    b2 = new JButton("create new contact");
    b2.addActionListener(this);
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(b1,BorderLayout.WEST);
    panel.add(b2,BorderLayout.EAST);
    frame.add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(300,300);
    frame.setVisible(true);
    
     }

    @Override
    public void actionPerformed(ActionEvent e) {
       if(e.getSource()==b1){
           frame.dispose();
               int retVal = fc.showOpenDialog(frame);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File selectedfile = fc.getSelectedFile();
                    StringBuilder sb = new StringBuilder();
                    sb.append(selectedfile.getName() + "\n");
                    
                   try {
                       line1 = Files.readAllLines(Paths.get(selectedfile.getPath())).get(0);
                       line2 = Files.readAllLines(Paths.get(selectedfile.getPath())).get(1);
                       line3 = Files.readAllLines(Paths.get(selectedfile.getPath())).get(2);
                   } catch (IOException ex) {    
                   }
                   port=Integer.parseInt(line1);
                   ip=line2;
                   contactname=line3;
                } 
                clientinitalizer();
                }
    if(e.getSource()==b2){
        frame.dispose();
       //be om port ip och namn,skapa fil med detta och starta chatten 
       JFrame dframe = new JFrame();
       line1=null;
       while(line1==null){
       line1 = JOptionPane.showInputDialog(dframe,
               "Please enter port number",
               "Port number");
    }
        line2=null;
        while(line2==null){
        line2 = JOptionPane.showInputDialog(dframe,
               "Please enter ip adress",
               "ip adress");
    }
    line3=null;
        while(line3==null){
        line3 = JOptionPane.showInputDialog(dframe,
               "Please enter users name",
               "Name");
    }
        String text = line1+"\n"+line2+"\n"+line3;
        writeFile(text,line3);
        port=Integer.parseInt(line1);
        ip=line2;
        contactname=line3;
        clientinitalizer();
    }
    
    }
    void writeFile(String text,String name){
        String path=file_path+"\\"+name;
        try {
            FileWriter write = new FileWriter(path);
            print_line = new PrintWriter(write);
            print_line.printf(text);
        } catch (IOException ex) {
            System.out.println("could not create contact");
        }
        finally{
            print_line.close();
        }
    }
void clientinitalizer(){
    try {
            socket = new Socket(ip, port);
            new ChatParticipantContacts(socket,contactname);
        } catch (IOException ex) {
           System.out.println("could not connect");
           System.exit(0);
        }
}}


final class ChatParticipantContacts implements ActionListener {

    JTextField textfield;
    JTextArea textarea;
    JButton exitbutton;
    JFrame frame;
    JPanel panel;
    private final String newline = "\n";
    JFrame myFrame;
    ObjectInputStream obinstream;
    ObjectOutputStream oboutstream;
    ObjectStreamManagerContacts obstreamman;
    MyObjectStreamListenerContacts obstreamlistener;
    String NAME="otherguy";
    int stream_number = 1;
    Socket socketToServer;
    Socket SOCKET;

    ChatParticipantContacts(Socket socket, String contactname) throws IOException {
        SOCKET = socket;
        NAME=contactname;
        createGUI();

        oboutstream = new ObjectOutputStream(socket.getOutputStream());
        obinstream = new ObjectInputStream(socket.getInputStream());
        obstreamlistener = new MyObjectStreamListenerContacts(textarea, this);
        obstreamman = new ObjectStreamManagerContacts(stream_number, obinstream, obstreamlistener);
    }
        ChatParticipantContacts(Socket socket) throws IOException {
        SOCKET = socket;
        createGUI();

        oboutstream = new ObjectOutputStream(socket.getOutputStream());
        obinstream = new ObjectInputStream(socket.getInputStream());
        obstreamlistener = new MyObjectStreamListenerContacts(textarea, this);
        obstreamman = new ObjectStreamManagerContacts(stream_number, obinstream, obstreamlistener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == textfield) {
            String text = textfield.getText(); 
            MessageContacts message = new MessageContacts(text);
            send(message);
            textarea.append(message.time + ": Me: " + message.meddelande + newline);
            textfield.setText("");
            textarea.setCaretPosition(textarea.getDocument().getLength());
        }
        if (e.getSource() == exitbutton) {
            if (SOCKET.isClosed()) {
                System.exit(0);
            } else {
                try {
                    SOCKET.close();
                    System.exit(0);
                } catch (IOException ex) {
                    System.out.println("Could not close");
                }

            }
        }
    }

    public void send(MessageContacts message) {
        try {
            oboutstream.writeObject(message);
        } catch (IOException ex) {
            System.exit(0);
        }

    }

    private void createGUI() {
        frame = new JFrame("Chatt");
        textfield = new JTextField(100);
        textfield.addActionListener(this);
        exitbutton = new JButton("Exit");
        exitbutton.addActionListener(this);
        textarea = new JTextArea(20, 100);
        textarea.setEditable(false);
        panel = new JPanel(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.add(textarea, BorderLayout.CENTER);
        panel.add(new JScrollPane(textarea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        panel.add(textfield, BorderLayout.SOUTH);
        panel.add(exitbutton, BorderLayout.EAST);
        frame.add(panel);

        frame.setSize(500, 500);
        frame.pack();
        frame.setVisible(true);
    }

}

class MessageContacts implements Serializable {

    final String meddelande;
    Calendar time_now = Calendar.getInstance();
    String time;

    MessageContacts(String mess) {

        meddelande = mess;
        timeGetter();
    }

    void timeGetter() {
        time = time_now.get(Calendar.HOUR_OF_DAY) + ":" + time_now.get(Calendar.MINUTE) + ":" + time_now.get(Calendar.SECOND);

    }
}

class ObjectStreamManagerContacts {

    private final ObjectInputStream theStream;
    private final ObjectStreamListenerContacts theListener;
    private final int theNumber;
    private volatile boolean stopped = false;

    /**
     *
     * This creates and starts a stream manager for a stream. The manager will
     * continually read from the stream and forward objects through the
     * objectReceived() method of the ObjectStreamListener parameter
     *
     *
     * @param number The number you give to the manager. It will be included in
     * all calls to readObject. That way you can have the same callback serving
     * several managers since for each received object you get the identity of
     * the manager.
     * @param stream The stream on which the manager should listen.
     * @param listener The object that has the callback objectReceived()
     *
     *
     */
    public ObjectStreamManagerContacts(int number, ObjectInputStream stream, ObjectStreamListenerContacts listener) {
        theNumber = number;
        theStream = stream;
        theListener = listener;
        new InnerListener().start();  // start to listen on a new thread.
    }

    // This private method accepts an object/exception pair and forwards them
    // to the callback, including also the manager number. The forwarding is scheduled
    // on the Swing thread through an anonymous inner class.
    private void callback(final Object object, final Exception exception) {
        SwingUtilities.invokeLater(
                new Runnable() {
            @Override
            public void run() {
                if (!stopped) {
                    theListener.objectReceived(theNumber, object, exception);
                    if (exception != null) {
                        closeManager();
                    }
                }
            }
        });
    }

    // This is where the actual reading takes place.
    private class InnerListener extends Thread {

        @Override
        public void run() {
            while (!stopped) {                            // as long as no one stopped me
                try {
                    callback(theStream.readObject(), null); // read an object and forward it
                } catch (Exception e) {                 // if Exception then forward it
                    callback(null, e);
                }

            }
            try {                   // I have been stopped: close stream
                theStream.close();
            } catch (IOException e) {
            }

        }
    }

    /**
     * Stop the manager and close the stream.
     *
     */
    public void closeManager() {
        stopped = true;
    }
}      // end of ObjectStreamManager

interface ObjectStreamListenerContacts {

    /**
     * This method is called whenever an object is received or an exception is
     * thrown.
     *
     * @param number The number of the manager as defined in its constructor
     * @param object The object received on the stream
     * @param exception The exception thrown when reading from the stream. Can
     * be IOException or ClassNotFoundException. One of name and exception will
     * always be null. In case of an exception the manager and stream are
     * closed.
 *
     */
    public void objectReceived(int number, Object object, Exception exception);
}

class MyObjectStreamListenerContacts implements ObjectStreamListenerContacts, ActionListener {
    JTextArea writeto;
    String newline = "\n";
    ChatParticipantContacts mychatter;
    JFrame FRAME;
    JPanel dpanel;
    MyObjectStreamListenerContacts(JTextArea textarea, ChatParticipantContacts chatpar) {
        writeto = textarea;
        mychatter = chatpar;
        FRAME = chatpar.frame;
        dpanel = new JPanel();
    }

    @Override
    public void objectReceived(int number, Object object, Exception exception) {
        //when exception!=null close thing and also show jdialog
        if (exception != null) {
            try {
                mychatter.SOCKET.close();
                createpopup();
            } catch (IOException ex) {
                writeto.append("exit failed");
            }
        }

        Message message = (Message) (object);
        if (message != null) {
            writeto.append(message.time + ": "+mychatter.NAME+": " + message.meddelande + newline);
            writeto.setCaretPosition(writeto.getDocument().getLength());
        }
    }

    void createpopup() {

        JDialog popup = new JDialog(FRAME, "connection lost");
        popup.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        JLabel label = new JLabel("The other user disconnected");
        JButton okbutton = new JButton("Exit");
        okbutton.addActionListener(this);
        dpanel.add(okbutton);
        dpanel.add(label);
        popup.add(dpanel);
        popup.setSize(200, 200);
        popup.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }
}

