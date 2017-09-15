import java.io.*;
import java.net.Socket;

public class BlockchainServerRunnable implements Runnable{

    private Socket clientSocket;
    private Blockchain blockchain;

    public BlockchainServerRunnable(Socket clientSocket, Blockchain blockchain) {
        this.blockchain = blockchain;
        this.clientSocket = clientSocket;
    }

    public void run() {

        try (
                OutputStream outputStream = clientSocket.getOutputStream();
                InputStream inputStream = clientSocket.getInputStream()

        ) {

            serverHandler(inputStream, outputStream);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void serverHandler(InputStream clientInputStream, OutputStream clientOutputStream) {

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientInputStream));
        PrintWriter outWriter = new PrintWriter(clientOutputStream, true);

        String inputLine, outputLine = "";

        try {
            while ((inputLine = inputReader.readLine()) != null) {
                if (inputLine.equals("pb")) {
                    outputLine = blockchain.toString() + "\n";
                    outWriter.printf(outputLine);
                } else if (inputLine.equals("cc")) {
                    break;
                } else if (inputLine.matches("^tx.*")) {
                    if (blockchain.addTransaction(inputLine))
                        outputLine = "Accepted\n\n";
                    else
                        outputLine = "Rejected\n\n";
                    outWriter.printf("%s", outputLine);
                } else {
                    outputLine = "Error\n\n";
                    outWriter.printf("%s", outputLine);
                }
                outWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
