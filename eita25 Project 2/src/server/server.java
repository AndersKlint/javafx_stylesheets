package server;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

import server.RefMonitor.ValidityException;

import java.util.LinkedList;

public class server implements Runnable {
	private ServerSocket serverSocket = null;
	private static int numConnectedClients = 0;
	Database db;

	public server(ServerSocket ss) throws IOException {
		serverSocket = ss;
		db = new Database();
		newListener();
	}

	public void run() {
		try {
			SSLSocket socket = (SSLSocket) serverSocket.accept();
			newListener();
			SSLSession session = socket.getSession();
			X509Certificate cert = (X509Certificate) session.getPeerCertificateChain()[0];
			String subject = cert.getSubjectDN().getName();
			String issuer = cert.getIssuerDN().getName();
			BigInteger SN = cert.getSerialNumber();
			numConnectedClients++;
			System.out.println("client connected");
			System.out.println("client name (cert subject DN field): " + subject);
			System.out.println("issuer name (cert issuer DN field): " + issuer);
			System.out.println("serial number: " + SN);
			System.out.println(numConnectedClients + " concurrent connection(s)\n");

			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

			String res, token, type;
			res = token = type = "";
			while (!res.equals("quit")) {
				LinkedList<String> data = interpret(br);
				token = data.get(0);
				type = data.get(1);

				try {
					switch (type) {
					case "login":
						if (token.equals("")) {
							res = db.login(data.get(2), data.get(3), data.get(4));
						} else {
							res = "Error@Another user already logged in";
						}
						break;
					case "readmany":
						res = db.readMany(token);
						break;
					case "read":
						res = db.read(token, data.get(2));
						break;
					case "write":
						res = db.write(token, data);
						break;
					case "delete":
						res = db.delete(token, data.get(2));
						break;
					case "quit":
						res = type;
						if (!token.equals("")) {
							db.logout(token);
						}
						break;
					case "logout":
						res = "Success@";
						db.logout(token);
						token = "";
						break;
					default:
						res = "Error@Invalid request";
						break;
					}

				} catch (ValidityException e) {
					res = "Error@" + e.getMessage();
				}
				pw.println(token + "@" + res + "@");
				pw.flush();
			}

			socket.close();
			numConnectedClients--;
			System.out.println("client disconnected");
			System.out.println(numConnectedClients + " concurrent connection(s)\n");
		} catch (IOException e) {
			System.out.println("Client died: " + e.getMessage());
			e.printStackTrace();
			return;
		}
	}

	private void newListener() {
		(new Thread(this)).start();
	} // calls run()

	public static void main(String args[]) {
		System.out.println("\nServer Started\n");
		int port = -1;
		if (args.length >= 1) {
			port = Integer.parseInt(args[0]);
		}
		String type = "TLS";
		try {
			ServerSocketFactory ssf = getServerSocketFactory(type);
			ServerSocket ss = ssf.createServerSocket(port);
			((SSLServerSocket) ss).setNeedClientAuth(true); // enables client authentication
			new server(ss);
		} catch (IOException e) {
			System.out.println("Unable to start Server: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private LinkedList<String> interpret(BufferedReader br) {
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

	private static ServerSocketFactory getServerSocketFactory(String type) {
		if (type.equals("TLS")) {
			SSLServerSocketFactory ssf = null;
			try { // set up key manager to perform server authentication
				SSLContext ctx = SSLContext.getInstance("TLS");
				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				KeyStore ks = KeyStore.getInstance("JKS");
				KeyStore ts = KeyStore.getInstance("JKS");
				char[] password = "password".toCharArray();

				ks.load(new FileInputStream("serverkeystore"), password); // keystore password (storepass)
				ts.load(new FileInputStream("servertruststore"), password); // truststore password (storepass)
				kmf.init(ks, password); // certificate password (keypass)
				tmf.init(ts); // possible to use keystore as truststore here
				ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
				ssf = ctx.getServerSocketFactory();
				return ssf;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return ServerSocketFactory.getDefault();
		}
		return null;
	}
}
