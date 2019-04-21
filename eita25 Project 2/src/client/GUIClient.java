package client;

import java.io.*;
import javax.net.ssl.*;
import java.security.KeyStore;
import java.util.LinkedList;

/*
 * This example shows how to set up a key manager to perform client
 * authentication.
 *
 * This program assumes that the client is not inside a firewall.
 * The application can be modified to connect to a server outside
 * the firewall by following SSLSocketClientWithTunneling.java.
 */
public class GUIClient {
	private static PrintWriter serverOut;
	private static BufferedReader serverIn;
	private static String token;
	private static SSLSocket socket;

	public static void initConnection(String[] args) throws Exception {
		token = "";
		String host = null;
		int port = -1;
		for (int i = 0; i < args.length; i++) {
			System.out.println("args[" + i + "] = " + args[i]);
		}
		if (args.length < 2) {
			System.out.println("USAGE: java client host port");
			System.exit(-1);
		}
		try { /* get input parameters */
			host = args[0];
			port = Integer.parseInt(args[1]);
		} catch (IllegalArgumentException e) {
			System.out.println("USAGE: java client host port");
			System.exit(-1);
		}

		try { /* set up a key manager for client authentication */
			SSLSocketFactory factory = null;
			try {
				char[] password = "password".toCharArray();
				KeyStore ks = KeyStore.getInstance("JKS");
				KeyStore ts = KeyStore.getInstance("JKS");
				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				SSLContext ctx = SSLContext.getInstance("TLS");
				ks.load(new FileInputStream("clientkeystore"), password); // keystore
																			// password
																			// (storepass)
				ts.load(new FileInputStream("clienttruststore"), password); // truststore
																			// password
																			// (storepass);
				kmf.init(ks, password); // user password (keypass)
				tmf.init(ts); // keystore can be used as truststore here
				ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
				factory = ctx.getSocketFactory();
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}
			socket = (SSLSocket) factory.createSocket(host, port);

			/*
			 * send http request
			 *
			 * See SSLSocketClient.java for more information about why there is a forced
			 * handshake here when using PrintWriters.
			 */
			socket.startHandshake();
			System.out.println("secure connection established\n\n");

			serverOut = new PrintWriter(socket.getOutputStream(), true);
			serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static LinkedList<String> sendMessage(String msg) {
		serverOut.println(token + "@" + msg + "@");
		serverOut.flush();
		LinkedList<String> res = interpret(serverIn);
		if (res.get(1).equals("Success") && res.getFirst().equals("")) {
			token = res.get(2);
		}
		return res;
	}

	private static LinkedList<String> interpret(BufferedReader br) {
		LinkedList<String> data = new LinkedList<>();
		StringBuilder sb = new StringBuilder();
		try {
			int c = br.read();
			while (br.ready()) {
				char z = (char) c;
				if (z == '@') {
					data.add(sb.toString());

					if (sb.length() > 0) {
						sb.delete(0, sb.length());
					}
				} else {
					sb.append(z);
				}
				c = br.read();
			}
		} catch (Exception e) {

		}

		return data;
	}

	public static void closeConnection() throws IOException {
		sendMessage("quit");
		serverIn.close();
		serverOut.close();
		socket.close();
	}

}
