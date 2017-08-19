import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class BlockchainClient {

	public static void main(String[] args) {

		if (args.length != 2) {
			return;
		}

		String serverName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		BlockchainClient bcc = new BlockchainClient();

		// implement your code here.
		try(
				Socket clientSocket = new Socket(serverName, portNumber);
				InputStream inputStream = clientSocket.getInputStream();
				OutputStream outputStream = clientSocket.getOutputStream();
				)
		{
			bcc.clientHandler(inputStream, outputStream);

		} catch(IOException e) {
			e.printStackTrace();
		}

	}

	public void clientHandler(InputStream serverInputStream, OutputStream serverOutputStream) {
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(serverInputStream));
		PrintWriter outWriter = new PrintWriter(serverOutputStream, true);

		String userInput = "";
		Scanner sc = new Scanner(System.in);

		// Write to the outputStream and listen through the inputStream
		try {
			while (sc.hasNextLine()) {
				userInput = sc.nextLine();
				System.out.println(userInput);
				if (userInput.equals("cc")) {
					outWriter.printf("%s", userInput);
					break;
				}
				else {
					outWriter.printf("%s", userInput);
					System.out.printf("%s", inputReader.readLine());
				}
			}
			// Close streams
			outWriter.close();
			inputReader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// implement helper functions here if you need any.
}
