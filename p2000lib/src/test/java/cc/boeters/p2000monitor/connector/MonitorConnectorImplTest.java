package cc.boeters.p2000monitor.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Ignore;
import org.junit.Test;

public class MonitorConnectorImplTest implements Runnable {

	protected int serverPort = 2000;
	protected ServerSocket serverSocket = null;
	protected boolean isStopped = false;
	protected Thread runningThread = null;

	private synchronized boolean isStopped() {
		return this.isStopped;
	}

	private void openServerSocket() {
		try {
			this.serverSocket = new ServerSocket(this.serverPort);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open port 2000", e);
		}
	}

	private void processClientRequest(Socket clientSocket) throws IOException {
		InputStream input = clientSocket.getInputStream();
		OutputStream output = clientSocket.getOutputStream();
		long time = System.currentTimeMillis();

		BufferedReader br = new BufferedReader(new InputStreamReader(
				MonitorConnectorImplTest.class
						.getResourceAsStream("/p2000capture.txt")));

		String line;

		while ((line = br.readLine()) != null) {

			output.write(line.concat("\r").getBytes());

			if (line.contains("END MSG")) {
				try {

					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		output.close();
		input.close();
		System.out.println("Request processed: " + time);
	}

	@Override
	public void run() {
		synchronized (this) {
			this.runningThread = Thread.currentThread();
		}
		openServerSocket();

		while (!isStopped()) {
			Socket clientSocket = null;
			try {
				clientSocket = this.serverSocket.accept();
			} catch (IOException e) {
				if (isStopped()) {
					System.out.println("Server Stopped.");
					return;
				}
				throw new RuntimeException("Error accepting client connection",
						e);
			}
			try {
				processClientRequest(clientSocket);
			} catch (IOException e) {
				stop();
			}
		}

		System.out.println("Server Stopped.");
	}

	public synchronized void stop() {
		this.isStopped = true;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Error closing server", e);
		}
	}

	@Ignore
	@Test
	public void test() {
		new MonitorConnectorImplTest().run();
	}

}
