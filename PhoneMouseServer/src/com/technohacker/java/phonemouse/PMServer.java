package com.technohacker.java.phonemouse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This is the Phone Mouse server. It handles requests
 * from the clients and spawns a PMClientHandler to attend
 * to the request
 * @author Technohacker
 *
 */
public class PMServer {
	/**
	 * This is the port to which the client will connect to
	 */
	private static final int PORT = 9559;
	public static void main(String[] args){
		ServerSocket srv = null;
		try {
			srv = new ServerSocket(PORT);
			while(true){
				Socket client = srv.accept();
				new PMClientHandler(client).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				srv.close();
			} catch (IOException | NullPointerException e) {
				e.printStackTrace();
			}
		}
	}
}
