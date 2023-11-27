import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpConnection {

    private final Socket socket;
    private final Thread recieveThread;
    private final TcpConnectionListener eventListener;
    private  final BufferedReader inStream;
    private  final BufferedWriter outStream;

    public  TcpConnection(TcpConnectionListener listener ,Socket socket) throws IOException {
        this.eventListener = listener;
        this.socket = socket;

        inStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        outStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

        recieveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TcpConnection.this);
                    while (!recieveThread.isInterrupted()){
                        eventListener.onReceiveString(TcpConnection.this, inStream.readLine());
                    }

                }
                catch (IOException e){
                    eventListener.onException(TcpConnection.this, e);
                }
                finally {
                    disconnect();
                }
            }
        });
        recieveThread.start();
    }

    public TcpConnection(TcpConnectionListener listener, String ipAddr, int port) throws IOException{
        this(listener, new Socket(ipAddr, port));
    }

    public synchronized void sendString(String value){
        try {
            outStream.write(value + "\r\n");
            outStream.flush();
        }
        catch (Exception e){
            eventListener.onException(TcpConnection.this, e);
            disconnect();
        }
    }

    public synchronized  void  disconnect(){
        recieveThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TcpConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TcpConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
