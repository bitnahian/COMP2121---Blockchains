import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class BlockchainClient {

    public static void main(String[] args) {

        if (args.length != 1) {
            return;
        }
        String configFileName = args[0];

        ServerInfoList pl = new ServerInfoList();
        try {
            pl.initialiseFromFile(configFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner sc = new Scanner(System.in);

        while (true) {
            String message = sc.nextLine();
            // implement your code here
        }
    }

    public void unicast (int serverNumber, ServerInfo p, String message) throws InterruptedException {
        BlockchainClientRunnable bcr = new BlockchainClientRunnable(serverNumber, p.getHost(), p.getPort(), message);
        Thread unicast = new Thread(bcr);
        unicast.start();
        unicast.join();
    }

    public void broadcast (ServerInfoList pl, String message) throws InterruptedException {

        int serverNumber = 0;
        for(ServerInfo serverInfo : pl.getServerInfos())
        {
            unicast(serverNumber, serverInfo, message);
            serverNumber++;
        }
    }

    public void multicast (ServerInfoList serverInfoList, ArrayList<Integer> serverIndices, String message) throws InterruptedException {

        for(Integer serverNumber : serverIndices)
        {
            unicast(serverNumber, serverInfoList.getServerInfos().get(serverNumber), message);
        }
    }

    // implement any helper method here if you need any
}