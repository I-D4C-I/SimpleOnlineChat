import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TcpConnectionListener {

    private static final int PORT = 8189;
    private final ArrayList<TcpConnection> connections = new ArrayList<>();

    private ChatServer(){
        System.out.println("Server running...");
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true){

                try {
                    new TcpConnection(this, serverSocket.accept());
                }
                catch (IOException e){
                    System.out.println("TcpConnect Exception: " + e);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TcpConnection connection) {
        connections.add(connection);
        sendToAllConnections("Client connected: " + connection);
    }

    @Override
    public synchronized void onReceiveString(TcpConnection connection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TcpConnection connection) {
        connections.remove(connection);
        sendToAllConnections("Client disconnected: " + connection);
    }

    @Override
    public synchronized void onException(TcpConnection connection, Exception ex) {
        System.out.println("TcpConnect Exception: " + ex);
    }

    private  void sendToAllConnections(String value){
        System.out.println(value);
        for (var client:
             connections) {
            client.sendString(value);
        }
    }

    public static void main(String[] args) {
        new ChatServer();
    }
}
