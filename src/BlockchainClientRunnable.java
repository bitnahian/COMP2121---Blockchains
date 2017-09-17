import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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
            if(!InetAddress.getByName(this.serverName).isReachable(2000)) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.out.printf("%sServer is not available\n\n", getReply());
            return;
        }

        try {

            Socket clientSocket = new Socket(serverName, portNumber);
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            clientHandler(inputStream, outputStream);
            clientSocket.close();

        } catch(Exception e) {
            System.err.printf("%sServer is not available\n\n", getReply());
            return;
        }

    }

    private void clientHandler(InputStream serverInputStream, OutputStream serverOutputStream) throws Exception{

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(serverInputStream));
        PrintWriter outWriter = new PrintWriter(serverOutputStream, true);

        outWriter.println(message);
        long startTime = System.currentTimeMillis(); //fetch starting time
        // Time out of 2 seconds
        while( !inputReader.ready() && (System.currentTimeMillis()-startTime)<2000);
        String output = "";
        // Get reply from server
        if((output = inputReader.readLine()) == null)
            throw new Exception();

        System.out.printf("%s%s\n", getReply(), output);

        while(inputReader.ready())
        {
            System.out.printf("%s\n", inputReader.readLine());
        }

        outWriter.println("cc");
    }

    public String getReply() {
        return reply;
    }

}