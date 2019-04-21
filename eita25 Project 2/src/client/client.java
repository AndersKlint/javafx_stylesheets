package client;

import java.io.*;
import java.math.BigInteger;

import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
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
public class client {

	public static void main(String[] args) throws Exception {
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
				ks.load(new FileInputStream("clientkeystore"), password); // keystore password (storepass)
				ts.load(new FileInputStream("clienttruststore"), password); // truststore password (storepass);
				kmf.init(ks, password); // user password (keypass)
				tmf.init(ts); // keystore can be used as truststore here
				ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
				factory = ctx.getSocketFactory();
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}
			SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
			System.out.println("\nsocket before handshake:\n" + socket + "\n");

			/*
			 * send http request
			 *
			 * See SSLSocketClient.java for more information about why there is a forced
			 * handshake here when using PrintWriters.
			 */
			socket.startHandshake();

			SSLSession session = socket.getSession();
			X509Certificate cert = (X509Certificate) session.getPeerCertificateChain()[0];
			String subject = cert.getSubjectDN().getName();
			String issuer = cert.getIssuerDN().getName();
			BigInteger SN = cert.getSerialNumber();
			System.out.println(
					"certificate name (subject DN field) on certificate received from server:\n" + subject + "\n");
			System.out.println("socket after handshake:\n" + socket + "\n");
			System.out.println("issuer name (cert issuer DN field): " + issuer + "\n");
			System.out.println("serial number: " + SN);
			System.out.println("secure connection established\n\n");

			BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String msg, token;
			token = "";

			for (;;) {
				System.out.print(">");
				msg = read.readLine();

				System.out.print("sending '" + token + "@" + msg + "' to server...");

				out.println(token + "@" + msg + "@");
				out.flush();
				System.out.println("done");

				LinkedList<String> res = interpret(in);

				if (res.get(1).equals("Success") && res.getFirst().equals("")) {
					System.out.println("CHANGE TOKEN");
					token = res.get(2);
				}

				System.out.println("received '" + res.toString() + "' from server\n");
				if(res.get(1).equals("quit")) {
					break;
				}
			}

			in.close();
			out.close();
			read.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

}