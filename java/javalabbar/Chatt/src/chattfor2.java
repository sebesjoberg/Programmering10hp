
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;

public class chattfor2 {

    public static void main(String args[]) {

        int port = 4000;

            try{
            new Client(port);
            }
            catch(Exception e) {
                new Server(port);
            }


    }
}

class Server {

    ServerSocket server;
    Socket socket;

    Server(int port) {

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

class Client {

    Socket socket;
    String ip = "LocalHost";

    Client(int port) throws Exception {

        socket = new Socket(ip, port);
        new ChatParticipant(socket);

//kontaktlista
    }

}

final class ChatParticipant implements ActionListener {

    JTextField textfield;
    JTextArea textarea;
    JButton exitbutton;
    JFrame frame;
    JPanel panel;
    private final String newline = "\n";
    JFrame myFrame;
    ObjectInputStream obinstream;
    ObjectOutputStream oboutstream;
    ObjectStreamManager obstreamman;
    MyObjectStreamListener obstreamlistener;
    String name;
    int stream_number = 1;
    Socket socketToServer;
    Socket SOCKET;

    ChatParticipant(Socket socket) throws IOException {
        SOCKET = socket;
        createGUI();

        oboutstream = new ObjectOutputStream(socket.getOutputStream());
        obinstream = new ObjectInputStream(socket.getInputStream());
        obstreamlistener = new MyObjectStreamListener(textarea, this);
        obstreamman = new ObjectStreamManager(stream_number, obinstream, obstreamlistener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == textfield) {
            String text = textfield.getText(); //this i the text to send
            Message message = new Message(text);
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

    public void send(Message message) {
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

class Message implements Serializable {

    final String meddelande;
    Calendar time_now = Calendar.getInstance();
    String time;

    Message(String mess) {

        meddelande = mess;
        timeGetter();
    }

    void timeGetter() {
        time = time_now.get(Calendar.HOUR_OF_DAY) + ":" + time_now.get(Calendar.MINUTE) + ":" + time_now.get(Calendar.SECOND);

    }
}

class ObjectStreamManager {

    private final ObjectInputStream theStream;
    private final ObjectStreamListener theListener;
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
    public ObjectStreamManager(int number, ObjectInputStream stream, ObjectStreamListener listener) {
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

interface ObjectStreamListener {

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

class MyObjectStreamListener implements ObjectStreamListener, ActionListener {
    JTextArea writeto;
    String newline = "\n";
    ChatParticipant mychatter;
    JFrame FRAME;
    JPanel dpanel;
    MyObjectStreamListener(JTextArea textarea, ChatParticipant chatpar) {
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
            writeto.append(message.time + ": Otherguy: " + message.meddelande + newline);
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
