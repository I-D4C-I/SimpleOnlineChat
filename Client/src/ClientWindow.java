import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TcpConnectionListener {

    private static final String IP_ADDR = "127.0.0.1";
    private static final int PORT = 8189;
    private  TcpConnection connection;



    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    private final JTextArea log = new JTextArea();
    private final JTextField userNameField = new JTextField("User");
    private  final  JTextField inputField = new JTextField();

    private ClientWindow(){
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);
        inputField.addActionListener(this);

        add(log, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);
        add(userNameField, BorderLayout.NORTH);

        setVisible(true);
        try {
            connection = new TcpConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMessage("TcpConnect Exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = inputField.getText();
        if(msg.isBlank())
            return;
        inputField.setText(null);
        connection.sendString(userNameField.getText() + ": " + msg);
    }

    @Override
    public void onConnectionReady(TcpConnection connection) {
        printMessage("Connected");
    }

    @Override
    public void onReceiveString(TcpConnection connection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TcpConnection connection) {
        printMessage("Disconnected");
    }

    @Override
    public void onException(TcpConnection connection, Exception ex) {
        printMessage("TcpConnect Exception: " + ex);
    }

    private synchronized void printMessage(String msg){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }
}
