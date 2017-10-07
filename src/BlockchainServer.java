import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockchainServer {

    static int POOL_SIZE = 40;

    public static void main(String[] args) {

        if (args.length != 1) {
            return;
        }

        int portNumber = 0;

        try {
            portNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            try {
                new BigInteger(args[0]);
            } catch (Exception e1) {
                System.err.println("Error: Port is not a number.");
                return;
            }
            System.err.println("Error: Port number caused overflow.");
            return;
        }

        Blockchain blockchain = new Blockchain();

        PeriodicCommitRunnable pcr = new PeriodicCommitRunnable(blockchain);
        Thread pct = new Thread(pcr);
        pct.start();

        // implement your code here

        ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE); //creating a pool of 5 threads

        ServerSocket serverSocket = null;
        try {
            if(portNumber < 1024 || portNumber > 65535)
                throw new IllegalArgumentException("Error: Illegal port number.\nPlease try values between 1024 and 65535");
            serverSocket = new ServerSocket(portNumber);

            // implement your code here.
            while(true) {
                Socket clientSocket = serverSocket.accept();
                Thread serverThread = new Thread(new BlockchainServerRunnable(clientSocket, blockchain));
                //serverThread.start();
                executor.submit(serverThread);

            }

        } catch (ConnectException e) {
            System.err.println("Error: Connection was closed. Please try again later.");
        } catch (IOException e) {
            executor.shutdown();
            System.err.println("Error: IOException occurred. Please try again later.");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

        pcr.setRunning(false);
        try {
            pct.join();
        } catch (InterruptedException e) {
            System.err.println("Error: Thread was interrupted and could not complete action.");
        }

    }

    // implement any helper method here if you need any
}
