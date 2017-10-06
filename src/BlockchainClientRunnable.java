import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Callable;


public class BlockchainClientRunnable implements Callable<String> {

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

    public String call() {

        try {
            if(!InetAddress.getByName(this.serverName).isReachable(2000)) {
                throw new Exception();
            }
        } catch (Exception e) {
            this.reply += "Server is not available\n\n";
            return reply;
        }

        try {

            Socket clientSocket = new Socket(serverName, portNumber);
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();


            clientHandler(clientSocket, inputStream, outputStream);
            clientSocket.close();

        } catch(Exception e) {
            //e.printStackTrace();
            this.reply += "Server is not available\n\n";
            return reply;
        }
        return reply;
    }

    private void clientHandler(Socket clientSocket, InputStream serverInputStream, OutputStream serverOutputStream) throws Exception{

        BufferedReader inputReader = new BufferedReader(new InputStreamReader(serverInputStream));
        PrintWriter outWriter = new PrintWriter(serverOutputStream, true);
        String output = "";
        String temp = "";
        outWriter.println(message);
        clientSocket.setSoTimeout(2000);
        Thread.sleep(5);
        while(inputReader.ready() && (temp = inputReader.readLine()) != null)
        {
            output += temp + "\n";
        }

        this.reply += output;
        outWriter.println("cc");
        outWriter.close();
    }

    public String getReply() {
        return reply;
    }

}