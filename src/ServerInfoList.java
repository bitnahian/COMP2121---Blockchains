import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class ServerInfoList {

    private ArrayList<ServerInfo> serverInfos;
    private ArrayList<Verifier> verifiers;

    public ServerInfoList() {

        serverInfos = new ArrayList<>();
        verifiers = new ArrayList<>();
    }

    private class Verifier {

        private boolean hostFound;
        private boolean portFound;
        private int index;
        private int port;
        private String host;

        public  Verifier(boolean hostFound, boolean portFound, int index)
        {
            this.hostFound = hostFound;
            this.portFound = portFound;
            this.index = index;
            this.port = Integer.MAX_VALUE;
            this.host = null;

        }

        protected void setHost(String host) {

            this.host = host;
            this.hostFound = true;
            verifyHost(this.host);
        }

        protected void setPort(int port) {

            this.port = port;
            this.portFound = true;
            verifyPort(this.port);
        }

        public boolean isVerified() {
            if(hostFound && portFound)
                return true;
            else
                return false;
        }

        private void verifyPort(int portNumber)
        {
            if(portNumber > 65535 || portNumber < 1024) {
                this.portFound = false;
                //System.out.println("Rejected Port");
            }
        }

        private void verifyHost(String host)
        {
            if(host.compareTo("localhost") == 0 ||
                    host.matches("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b") ||
                    host.matches("(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:)" +
                            "{1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}" +
                            "(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}" +
                            "|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}" +
                            "(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|" +
                            ":((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]" +
                            "{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.)" +
                            "{3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]" +
                            "|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))"))
            {
                // Regexp for IP pattern found from source : http://www.regular-expressions.info/regexbuddy/ipaccurate.html
                this.hostFound = true;
            }
            else {
                this.hostFound = false;
                //System.out.println("Rejected Host");

            }
        }

    }

    public void initialiseFromFile(String filename){

        int serverNum = 0;
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
                    serverNum = num;
                }
                else if(currentLine.matches("^server[0-9]+\\.host=[^=]*$")) // Never thought this'd work LOL
                {
                    // Check if serverIndex already has either port/host
                    String[] parts = currentLine.split("=");
                    if(!(parts.length > 1))
                    {
                        System.out.printf("Failed\n\n");
                        continue;
                    }
                    String numberOnly = parts[0].replaceAll("[^0-9]", "");
                    int serverIndex = Integer.parseInt(numberOnly);

                    // System.out.println(serverIndex);

                    Verifier verifier;
                    // If server with same index exists for port/host
                    if((verifier = verifierContainsIndex(serverIndex)) != null)
                        verifier.setHost(parts[1]);
                    else
                    {
                        verifier = new Verifier(true, false, serverIndex);
                        verifiers.add(verifier);
                        verifier.setHost(parts[1]);
                    }

                }
                else if(currentLine.matches("^server[0-9]+\\.port=[^=]*$"))
                {
                    String[] parts = currentLine.split("=");
                    if(!(parts.length > 1))
                    {
                        System.out.printf("Failed\n\n");
                        continue;
                    }
                    String numberOnly = parts[0].replaceAll("[^0-9]", "");
                    int serverIndex = Integer.parseInt(numberOnly);
                    //System.out.println(serverIndex);

                    int portNumber = Integer.parseInt(parts[1]);

                    Verifier verifier;
                    // If server with same index exists for port/host
                    if((verifier = verifierContainsIndex(serverIndex)) != null)
                    {
                        verifier.setPort(portNumber);
                    }
                    else
                    {
                        verifier = new Verifier(false, true, serverIndex);
                        verifiers.add(verifier);
                        verifier.setPort(portNumber);
                    }

                }
            }

            for(Verifier verifier : verifiers)
            {
                if(verifier.index >= 0 && verifier.index < serverNum)
                {
                    if(verifier.isVerified())
                        serverInfos.add(new ServerInfo(verifier.host, verifier.port));
                    else
                        serverInfos.add(null);
                }
            }

            if(br != null) br.close();
        } catch (FileNotFoundException e) {
            System.err.println("Error: File " + filename + " not found.");
        } catch (IOException e) {
            System.err.println("Error: IOException occurred. Was unable to read from file.");
        }


    }

    public Verifier verifierContainsIndex(int index) {
        for(Verifier verifier : verifiers)
        {
            if(verifier.index == index)
                return verifier;
        }
        return null;
    }

    public ArrayList<ServerInfo> getServerInfos() {
        return serverInfos;
    }

    public void setServerInfos(ArrayList<ServerInfo> serverInfos) {
        this.serverInfos = serverInfos;
    }

    public boolean addServerInfo(ServerInfo newServerInfo) {
        if(isPortVerified(newServerInfo.getPort()) && isHostVerified(newServerInfo.getHost()))
        {
            serverInfos.add(newServerInfo);
            return true;
        }
        return false;
    }

    public boolean updateServerInfo(int index, ServerInfo newServerInfo) {

        if((index >= serverInfos.size() || index < 0)
                || !isPortVerified(newServerInfo.getPort()) || !isHostVerified(newServerInfo.getHost()))
            return false;
        else {
            this.serverInfos.set(index, newServerInfo);
            return true;
        }

    }
    
    public boolean removeServerInfo(int index) {
        if(index >= serverInfos.size() || index < 0)
            return false;
        else {
            serverInfos.set(index, null);
            return true;
        }
    }

    public boolean clearServerInfo() { 
        if(serverInfos.contains(null))
        {
            serverInfos.removeAll(Collections.singleton(null));
            return true;
        }
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

    private static boolean isHostVerified(String host)
    {
        if(host.compareTo("localhost") == 0 ||
                host.matches("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b") ||
                host.matches("(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:)" +
                        "{1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}" +
                        "(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}" +
                        "|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}" +
                        "(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|" +
                        ":((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]" +
                        "{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.)" +
                        "{3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]" +
                        "|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))"))
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