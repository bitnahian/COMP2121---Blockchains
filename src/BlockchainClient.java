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
            String message = "";

            while (sc.hasNextLine()) {
                message = sc.nextLine();
                if (message.equals("sd"))
                    break;
                else if(message.equals("ls"))
                    System.out.printf("%s\n", pl.toString());
                else if(message.matches("^ad\\|[^\\|]+\\|[0-9]+$")) // if it matches the ad|host|port format
                {
                    String[] parts = message.split("\\|");
                    int portNumber = Integer.parseInt(parts[2]);
                    String host = parts[1];
                    if(isPortVerified(portNumber) && isHostVerified(host))
                    {
                        pl.addServerInfo(new ServerInfo(host, portNumber));
                        System.out.printf("Succeeded\n\n");
                    }
                    else
                        System.out.printf("Failed\n\n");
                }
                else if(message.matches("^rm\\|[0-9]+$"))
                {
                    String[] parts = message.split("\\|");
                    int serverIndex = Integer.parseInt(parts[1]);
                    if(serverIndex >= pl.getServerInfos().size() || serverIndex < 0)
                        System.out.printf("Failed\n\n");
                    else {
                        pl.getServerInfos().set(serverIndex, null);
                        System.out.printf("Succeeded\n\n");
                    }
                }
                else if(message.matches("^up\\|[0-9]+\\|[^\\|]+\\|[0-9]+$"))
                {
                    String[] parts = message.split("\\|");
                    int serverIndex = Integer.parseInt(parts[1]);
                    String host = parts[2];
                    int portNumber = Integer.parseInt(parts[3]);

                    if((serverIndex >= pl.getServerInfos().size() || serverIndex < 0)
                            || !isPortVerified(portNumber) || !isHostVerified(host))
                        System.out.printf("Failed\n\n");
                    else {
                        ServerInfo serverInfo = pl.getServerInfos().get(serverIndex);
                        serverInfo.setHost(host);
                        serverInfo.setPort(portNumber);
                        System.out.printf("Succeeded\n\n");
                    }
                }
            }
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

    private static boolean isHostVerified(String host)
    {
        if(host.compareTo("localhost") == 0 ||
                host.matches("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b"))
        {
            // Regexp for IP pattern found from source : http://www.regular-expressions.info/regexbuddy/ipaccurate.html
            return true;
        }
        return false;
    }

    private static boolean isPortVerified(int portNumber)
    {
        if(portNumber > 65535 || portNumber < 1024) {
            return false;
        }
        return true;
    }

    // implement any helper method here if you need any
}