public class BlockchainClientRunnable implements Runnable {

    private String reply;

    public BlockchainClientRunnable(int serverNumber, String serverName, int portNumber, String message) {
        this.reply = "Server" + serverNumber + ": " + serverName + " " + portNumber + "\n"; // header string
    }

    public void run() {
        // implement your code here
        
    }

    public String getReply() {
        return reply;
    }

    // implement any helper method here if you need any
}