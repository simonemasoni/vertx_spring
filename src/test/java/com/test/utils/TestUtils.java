package com.test.utils;

import java.io.IOException;
import java.net.ServerSocket;

public class TestUtils {

	/**
	 * Returns a random not used port
	 * @return random not used port
	 */
	public static int getRandomPort() {
		try {
			int port = 0;
			ServerSocket socket = new ServerSocket(0);
			port = socket.getLocalPort();
			socket.close();
			return port;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
