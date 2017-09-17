public class ServerInfo {

    private String host;
    private int port;
    private int index;

    public ServerInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    // implement any helper method here if you need any
}
