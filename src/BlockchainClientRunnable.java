import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class BlockchainClientRunnable implements Runnable {

    private String reply;

    String serverName;
    int portNumber;
    String message;

    public BlockchainClientRunnable(int serverNumber, String serverName, int portNumber, String message) {

        this.serverName = serverName;
        this.portNumber = portNumber;
        this.message = message;

        this.reply = "Server" + serverNumber + ": " + serverName + " " + portNumber + "\n"; // header string
    }

    public void run() {

        try {
            if(!InetAddress.getByName(this.serverName).isReachable(1000)) {
                throw new Exception();
            }
        } catch (Exception e) {
            this.reply += "Server is not available\n\n";
            return;
        }

        try {

            Socket clientSocket = new Socket(serverName, portNumber);
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            clientHandler(inputStream, outputStream);
            clientSocket.close();

        } catch(Exception e) {
            this.reply += "Server is not available\n\n";
            return;
        }

    }

    private void clientHandler(InputStream serverInputStream, OutputStream serverOutputStream) throws Exception{

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(serverInputStream));
        PrintWriter outWriter = new PrintWriter(serverOutputStream, true);

        long startTime = System.currentTimeMillis(); //fetch starting time
        outWriter.println(message);
        // Time out of 2 seconds
        Thread.sleep(50);
        String output = "";
        while(inputReader.ready())
        {
            output += inputReader.readLine() + "\n";
            if (System.currentTimeMillis()-startTime > 2000)
                throw new Exception();
        }

        this.reply += output;
        outWriter.println("cc");
        outWriter.close();
    }

    public String getReply() {
        return reply;
    }

}