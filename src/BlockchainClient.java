import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.regex.Pattern;


public class BlockchainClient {

    static int POOL_SIZE = 5;

    public static void main(String[] args) {

        if (args.length != 1) {
            return;
        }
        String configFileName = args[0];

        ServerInfoList pl = new ServerInfoList();
        pl.initialiseFromFile(configFileName);

        Scanner sc = new Scanner(System.in);
        BlockchainClient bc = new BlockchainClient();
        ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);

        String message = "";

        while (sc.hasNextLine()) {
            message = sc.nextLine();
            if (message.equals("sd")) {
                break;
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
                bc.broadcast(pl, message, pool);
            }
            else if(message.contains("pb"))
            {

                if(message.equals("pb")) // Then broadcast
                {
                    bc.broadcast(pl, message, pool);
                    continue;
                }
                if(!message.matches("^pb(?:\\|\\d+)+")) // If it doesn't match format
                    continue;
                else
                {
                    String[] parts = message.split("\\|");
                    if(parts.length == 2) // Only 1 then unicast
                    {
                        int serverIndex;
                        try {
                            serverIndex = Integer.parseInt(parts[1]);
                        } catch (NumberFormatException e) {
                            try {
                                new BigInteger(parts[1]);
                            } catch (Exception e1) {
                                System.err.println("Error: Index is not a number.");
                                continue;
                            }
                            System.err.println("Error: Index caused overflow.");
                            continue;
                        }
                        try {
                            bc.unicast(serverIndex, pl.getServerInfos().get(serverIndex), "pb", pool);
                        } catch (IndexOutOfBoundsException e) {
                            System.err.println("Error: Server with index: " + serverIndex + " does not exist.");
                        }
                    }
                    else if(parts.length > 2) // Time to multicast hue hue
                    {
                        ArrayList<Integer> indices = new ArrayList<Integer>();
                        for(int i = 1; i < parts.length; ++i)
                        {
                            try {
                                indices.add(Integer.parseInt(parts[i]));
                            } catch (NumberFormatException e) {
                                try {
                                    new BigInteger(parts[i]);
                                } catch (Exception e1) {
                                    System.err.println("Error: Index is not a number.");
                                    continue;
                                }
                                System.err.println("Error: Index caused overflow.");
                                continue;
                            }
                        }
                        bc.multicast(pl, indices, "pb", pool);
                    }
                }

            }
            else
            {
                System.out.printf("Unknown Command\n\n");
            }
        }
        shutdownAndAwaitTermination(pool);
    }

    public void unicast (int serverNumber, ServerInfo p, String message, ExecutorService pool) {
        if(p == null) {
            return;
        }
        BlockchainClientRunnable bcr = new BlockchainClientRunnable(serverNumber, p.getHost(), p.getPort(), message);
        try {
            Future<String> future = pool.submit(bcr);
            /*Thread unicast = new Thread(bcr);
            unicast.start();
            unicast.join();*/
            System.out.printf("%s", (String) future.get());
        } catch (Exception e)
        {
            System.err.println("Error: Thread was interrupted and could not complete action.");
        }

    }

    public void broadcast (ServerInfoList pl, String message, ExecutorService pool) {

        int serverNumber = 0;
        ArrayList<Future<String>> futures = new ArrayList<Future<String>>();
        //ArrayList<Thread> threads = new ArrayList<>();

        for(ServerInfo serverInfo : pl.getServerInfos())
        {
            /*unicast(serverNumber, serverInfo, message);
            serverNumber++;*/
            futures.add(pool.submit(new BlockchainClientRunnable(serverNumber,serverInfo.getHost(),serverInfo.getPort(), message)));
            //threads.get(serverNumber).start();
            serverNumber++;
        }

        String reply = getReplies(futures);

        System.out.printf("%s", reply);

    }

    public void multicast (ServerInfoList serverInfoList, ArrayList<Integer> serverIndices, String message,
                           ExecutorService pool){

        ArrayList<Future<String>> futures = new ArrayList<Future<String>>();
        //ArrayList<BlockchainClientRunnable> bcrs = new ArrayList<>();
        //ArrayList<Thread> threads = new ArrayList<>();
        //int i = 0;

        for (Integer serverNumber : serverIndices) {
            try {

                futures.add(pool.submit(new BlockchainClientRunnable(serverNumber,
                        serverInfoList.getServerInfos().get(serverNumber).getHost(),
                        serverInfoList.getServerInfos().get(serverNumber).getPort(), message)));
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Error: Server with index: " + serverNumber + " does not exist.");
                continue;
            }
            //threads.add(new Thread(bcrs.get(i)));
            //threads.get(i).start();
            //pool.submit(threads.get(i));
            //++i;
        }

        String reply = getReplies(futures);

        /*for(int j = 0; j < threads.size(); ++ j)
        {
            /*try {
                threads.get(j).join();
            } catch (InterruptedException e) {
                System.err.println("Error: Thread was interrupted and could not complete action.");
            }
            reply += bcrs.get(j).getReply();
        }*/

        System.out.printf("%s", reply);
    }

    public static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public static String getReplies(ArrayList<Future<String>> futures) {
        String reply = "";

        for(Future<String> future : futures) {
            try {
                reply += (String) future.get(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.err.println("Error: Thread was interrupted and could not complete action.");
            } catch (ExecutionException e) {
                e.printStackTrace();
                System.err.println("Error: Thread was interrupted and could not complete action.");
            } catch (TimeoutException e) {
                e.printStackTrace();
                System.err.println("Error: Thread took too long to finish.");
            }
        }
        return reply;
    }
}