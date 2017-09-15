import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ServerInfoList {

    ArrayList<ServerInfo> serverInfos;

    public ServerInfoList() {
        serverInfos = new ArrayList<>();
    }

    public void initialiseFromFile(String filename) throws IOException {

        BufferedReader bufferedReader = null;
        FileReader fileReader = null;

        int serverNum = 0;
        ArrayList<String> list = new ArrayList<>();

        // Using try with resources
        try (BufferedReader br =
                     new BufferedReader(new FileReader(filename))) {

            String currentLine = "";

            while((currentLine = br.readLine()) != null)
            {
                // Keep reading through file and check for
                //  i)      servers.num=<number>
                //  ii)     server<index>.host=<IP address>
                //  iii)    server<index>.port=<port_number>

                if(currentLine.contains("servers.num="))
                {
                    // Part i)
                    String numberOnly = currentLine.replaceAll("[^0-9]", "");
                    int num = Integer.parseInt(numberOnly);
                    serverNum = num > serverNum ? num : serverNum;
                }
                else if(currentLine.matches("^server[0-9]+\\.host=[^=]*$")) // Never thought this'd work LOL
                {
                    String[] parts = currentLine.split("=");
                    String numberOnly = parts[0].replaceAll("[^0-9]", "");
                    int serverIndex = Integer.parseInt(numberOnly);
                    System.out.println(serverIndex);
                    if(parts[1].compareTo("localhost") == 0 ||
                            parts[1].matches("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b"))
                    {
                        // Regexp for IP pattern found from source : http://www.regular-expressions.info/regexbuddy/ipaccurate.html
                        System.out.println(parts[1]);

                    }
                }

            }
            // 1st condition


            if(br != null) br.close();
        }


    }

    public ArrayList<ServerInfo> getServerInfos() {
        return serverInfos;
    }

    public void setServerInfos(ArrayList<ServerInfo> serverInfos) {
        this.serverInfos = serverInfos;
    }

    public boolean addServerInfo(ServerInfo newServerInfo) { 
        // implement your code here
        return false;
    }

    public boolean updateServerInfo(int index, ServerInfo newServerInfo) { 
        // implement your code here
        return false;
    }
    
    public boolean removeServerInfo(int index) { 
        // implement your code here
        return false;
    }

    public boolean clearServerInfo() { 
        // implement your code here
        return false;
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < serverInfos.size(); i++) {
            if (serverInfos.get(i) != null) {
                s += "Server" + i + ": " + serverInfos.get(i).getHost() + " " + serverInfos.get(i).getPort() + "\n";
            }
        }
        return s;
    }

    // implement any helper method here if you need any
}