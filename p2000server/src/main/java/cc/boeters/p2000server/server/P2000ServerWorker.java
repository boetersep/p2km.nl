package cc.boeters.p2000server.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**

 */
public class P2000ServerWorker implements Runnable {

	protected Socket clientSocket = null;
	protected String serverText = null;

	public P2000ServerWorker(Socket clientSocket, String serverText) {
		this.clientSocket = clientSocket;
		this.serverText = serverText;
	}

	@Override
	public void run() {
		try {
			InputStream input = clientSocket.getInputStream();
			OutputStream output = clientSocket.getOutputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					getClass().getResourceAsStream("/p2000capture.txt")));

			String line;

			while ((line = br.readLine()) != null) {
				output.write(line.concat("\r").getBytes());
				if (line.contains("END MSG")) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			output.close();
			input.close();
		} catch (IOException e) {

		}
	}
}