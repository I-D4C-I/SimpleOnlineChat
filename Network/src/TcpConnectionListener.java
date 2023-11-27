public interface TcpConnectionListener {

    void onConnectionReady(TcpConnection connection);
    void onReceiveString(TcpConnection connection, String value);
    void onDisconnect(TcpConnection connection);
    void onException(TcpConnection connection, Exception ex);
}
