import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import static java.lang.Thread.sleep;

public class BlockchainServer {

	private Blockchain blockchain;

	public BlockchainServer() {
		blockchain = new Blockchain();
	}

	// getters and setters
	public void setBlockchain(Blockchain blockchain) {
		this.blockchain = blockchain;
	}

	public Blockchain getBlockchain() {
		return blockchain;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			return;
		}
		int portNumber = Integer.parseInt(args[0]);
		BlockchainServer bcs = new BlockchainServer();

		// implement your code here.
        while(true)
        {
            try (
                    ServerSocket serverSocket = new ServerSocket(portNumber);
                    Socket clientSocket = serverSocket.accept();
                    OutputStream outputStream = clientSocket.getOutputStream();
                    InputStream inputStream = clientSocket.getInputStream();

            ) {
                // If successful handle the request

                bcs.serverHandler(inputStream, outputStream);
                clientSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

	public void serverHandler(InputStream clientInputStream, OutputStream clientOutputStream) {

		BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientInputStream));
		PrintWriter outWriter = new PrintWriter(clientOutputStream, true);

		String inputLine, outputLine = "";

        try {
                while((inputLine = inputReader.readLine()) != null)
                {
                   if(inputLine.equals("pb"))
                   {
                       outputLine = blockchain.toString() + "\n";
                       outWriter.printf(outputLine);
                   }
                   else if(inputLine.equals("cc"))
                   {
                       break;
                   }
                   else if(inputLine.matches("^tx.*"))
                   {
                        switch(blockchain.addTransaction(inputLine))
                        {
                            case 0: outputLine = "Rejected\n\n";
                                    break;
                            case 1: outputLine = "Accepted\n\n";
                                    break;
                            case 2: outputLine = "Accepted\n\n";
                        }
                        outWriter.printf("%s", outputLine);
                   }
                   else
                   {
                       outputLine = "Error\n\n";
                       outWriter.printf("%s", outputLine);
                   }
                }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // implement your code here.

	}

	// implement helper functions here if you need any.
}
