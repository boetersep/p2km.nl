package cc.boeters.p2000server.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class P2000Server implements Runnable {

	protected boolean isStopped = false;
	protected Thread runningThread = null;
	protected int serverPort;
	protected ServerSocket serverSocket = null;

	public P2000Server(int port) {
		this.serverPort = port;
	}

	private synchronized boolean isStopped() {
		return this.isStopped;
	}

	private void openServerSocket() {
		try {
			this.serverSocket = new ServerSocket(this.serverPort);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open port " + serverPort, e);
		}
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
			new Thread(new P2000ServerWorker(clientSocket,
					"Multithreaded Server")).start();
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

}
