public class BlockchainServer {

    public static void main(String[] args) {

        if (args.length != 1) {
            return;
        }

        int portNumber = Integer.parseInt(args[0]);
        Blockchain blockchain = new Blockchain();


        PeriodicCommitRunnable pcr = new PeriodicCommitRunnable(blockchain);
        Thread pct = new Thread(pcr);
        pct.start();

        // implement your code here

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);

            // implement your code here.
            while(true) {
                Socket clientSocket = serverSocket.accept();
                Thread serverThread = new Thread(new BlockchainClientRunnable(blockchain, clientSocket));
                serverThread.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        pcr.setRunning(false);
        pct.join();
    }

    // implement any helper method here if you need any
}
