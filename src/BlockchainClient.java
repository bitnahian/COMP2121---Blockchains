import java.util.ArrayList;
import java.util.Scanner;

public class BlockchainClient {

    public static void main(String[] args) {

        if (args.length != 1) {
            return;
        }
        String configFileName = args[0];

        ServerInfoList pl = new ServerInfoList();
        pl.initialiseFromFile(configFileName);

        Scanner sc = new Scanner(System.in);

        while (true) {
            String message = sc.nextLine();
            // implement your code here
        }
    }

    public void unicast (int serverNumber, ServerInfo p, String message) {
        // implement your code here
    }

    public void broadcast (ServerInfoList pl, String message) {
        // implement your code here
    }

    public void multicast (ServerInfoList serverInfoList, ArrayList<Integer> serverIndices, String message) {
        // implement your code here
    }

    // implement any helper method here if you need any
}