import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class BlockchainClient {

    public static void main(String[] args) {

        if (args.length != 1) {
            return;
        }
        String configFileName = args[0];

        ServerInfoList pl = new ServerInfoList();
        pl.initialiseFromFile(configFileName);

        Scanner sc = new Scanner(System.in);
        BlockchainClient bc = new BlockchainClient();

        while (true) {
            String message = "";

            while (sc.hasNextLine()) {
                message = sc.nextLine();
                if (message.equals("sd")) {
                    return;
                }
                else if(message.equals("ls"))
                    System.out.printf("%s\n", pl.toString());
                else if(message.contains("ad")) // if it matches the ad|host|port format
                {
                    if(!message.matches("^ad\\|[^\\|]+\\|[0-9]+$")) {
                        System.out.printf("Failed\n\n");
                        continue;
                    }
                    String[] parts = message.split("\\|");
                    int portNumber = Integer.parseInt(parts[2]);
                    String host = parts[1];
                    String output = (pl.addServerInfo(new ServerInfo(host, portNumber))) ? "Succeeded\n\n" : "Failed\n\n";
                    System.out.printf("%s", output);
                }
                else if(message.contains("rm"))
                {
                    if(!message.matches("^rm\\|[0-9]+$")) {
                        System.out.printf("Failed\n\n");
                        continue;
                    }

                    String[] parts = message.split("\\|");
                    int serverIndex = Integer.parseInt(parts[1]);
                    String output = (pl.removeServerInfo(serverIndex)) ? "Succeeded\n\n" : "Failed\n\n";
                    System.out.printf("%s", output);
                }
                else if(message.contains("up"))
                {
                    if(!message.matches("^up\\|[0-9]+\\|[^\\|]+\\|[0-9]+$")) {
                        System.out.printf("Failed\n\n");
                        continue;
                    }

                    String[] parts = message.split("\\|");
                    int serverIndex = Integer.parseInt(parts[1]);
                    String host = parts[2];
                    int portNumber = Integer.parseInt(parts[3]);

                    String output = (pl.updateServerInfo(serverIndex, new ServerInfo(host, portNumber))) ? "Succeeded\n\n" : "Failed\n\n";
                    System.out.printf("%s", output);

                }
                else if(message.equals("cl"))
                {
                    String output = (pl.clearServerInfo()) ? "Succeeded\n\n" : "Failed\n\n";
                    System.out.printf("%s", output);
                }
                else if(message.matches("^tx.*"))
                {
                    bc.broadcast(pl, message);
                }
                else if(message.contains("pb"))
                {

                    if(message.equals("pb")) // Then broadcast
                    {
                        bc.broadcast(pl, message);
                        continue;
                    }
                    if(!message.matches("^pb(?:\\|\\d+)+")) // If it doesn't match format
                        continue;
                    else
                    {
                        String[] parts = message.split("\\|");
                        if(parts.length == 2) // Only 1 then unicast
                        {
                            int serverIndex = Integer.parseInt(parts[1]);
                            bc.unicast(serverIndex, pl.getServerInfos().get(serverIndex), "pb");
                        }
                        else if(parts.length > 2) // Time to multicast hue hue
                        {
                            ArrayList<Integer> indices = new ArrayList<>();
                            for(int i = 1; i < parts.length; ++i)
                                indices.add(Integer.parseInt(parts[i]));
                            bc.multicast(pl, indices, "pb");
                        }
                    }

                }
                else
                {
                    System.out.printf("Unknown Command\n\n");
                }

            }
        }
    }

    public void unicast (int serverNumber, ServerInfo p, String message) {
        if(p == null) {
            return;
        }
        BlockchainClientRunnable bcr = new BlockchainClientRunnable(serverNumber, p.getHost(), p.getPort(), message);
        try {
            Thread unicast = new Thread(bcr);
            unicast.start();
            unicast.join();
            System.out.printf("%s", bcr.getReply());
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }

    public void broadcast (ServerInfoList pl, String message) {

        int serverNumber = 0;
        for(ServerInfo serverInfo : pl.getServerInfos())
        {
            unicast(serverNumber, serverInfo, message);
            serverNumber++;
        }

    }

    public void multicast (ServerInfoList serverInfoList, ArrayList<Integer> serverIndices, String message){

        for(Integer serverNumber : serverIndices)
        {
            unicast(serverNumber, serverInfoList.getServerInfos().get(serverNumber), message);
        }
    }

    // implement any helper method here if you need any
}