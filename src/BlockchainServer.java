import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockchainServer {

    public static void main(String[] args) {

        if (args.length != 1) {
            return;
        }

        int portNumber = 0;

        try {
            portNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        Blockchain blockchain = new Blockchain();


        PeriodicCommitRunnable pcr = new PeriodicCommitRunnable(blockchain);
        Thread pct = new Thread(pcr);
        pct.start();

        // implement your code here

        ServerSocket serverSocket = null;
        try {
            if(portNumber < 1024 || portNumber > 65535)
                throw new IllegalArgumentException();
            serverSocket = new ServerSocket(portNumber);

            // implement your code here.
            while(true) {
                Socket clientSocket = serverSocket.accept();
                Thread serverThread = new Thread(new BlockchainServerRunnable(clientSocket, blockchain));
                serverThread.start();

            }

        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        pcr.setRunning(false);
        try {
            pct.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    // implement any helper method here if you need any
}
