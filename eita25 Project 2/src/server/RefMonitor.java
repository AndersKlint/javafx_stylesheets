package server;

import java.util.HashMap;
import java.util.Random;

import model.Person;

public class RefMonitor {
	private HashMap<String, Person> ref;

	public RefMonitor() {
		ref = new HashMap<String, Person>();
	}

	private String createToken() {
		Long ts = System.currentTimeMillis();
		String timeStamp = ts.toString();
		Random rand = new Random();
		Integer random = rand.nextInt(899999) + 100000;
		String randString = random.toString();
		return timeStamp + randString;
	}

	public String addInfo(Person data) {
		cleanUp();
		String s = createToken();
		ref.put(s, data);
		//System.out.println("Added user" + data + " with token " + s);
		return s;
	}

	private void cleanUp() {
		for (String s : ref.keySet()) {
			if (checkExpired(s))
				ref.remove(s);
		}

	}

	public int getID(String token) throws ValidityException {
		checkValidity(token);
		return ref.get(token).getID();
	}

	public String getName(String token) throws ValidityException {
		checkValidity(token);
		return ref.get(token).getName();
	}

	public String getOccupation(String token) throws ValidityException {
		checkValidity(token);
		return ref.get(token).getOccupation();
	}

	public String getDivision(String token) throws ValidityException {
		checkValidity(token);
		return ref.get(token).getDivision();
	}

	private void checkValidity(String token) throws ValidityException {
		if (!ref.containsKey(token))
			throw new ValidityException("Your connection is invalid, please try to login again");
		if (checkExpired(token))
			throw new ValidityException("Your connection has expired, please try to login again");

	}

	public void removeToken(String token) {
		ref.remove(token);
	}

	public class ValidityException extends Exception {
		private static final long serialVersionUID = 1L;

		public ValidityException() {
			super();
		}

		public ValidityException(String message) {
			super(message);
		}

		public ValidityException(String message, Throwable cause) {
			super(message, cause);
		}

		public ValidityException(Throwable cause) {
			super(cause);
		}
	}

	private boolean checkExpired(String token) {
		Long ts = Long.valueOf((token.substring(0, token.length() - 6)));
		return System.currentTimeMillis() > (ts + 3600000);
	}

}
