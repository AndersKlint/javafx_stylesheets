package server;

import model.Person;
import model.JournalEntry;
import model.SHA512;
import server.RefMonitor.ValidityException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Database {
	static Handler fileHandler = null;
	private static final Logger LOGGER = Logger.getLogger(Database.class.getClass().getName());
	private ArrayList<Person> people;
	private ArrayList<JournalEntry> journals;
	private RefMonitor ref;
	private CSV csvrj;
	private CSV csvrp;

	public Database() {
		people = new ArrayList<>();
		journals = new ArrayList<>();
		ref = new RefMonitor();
		initiateData();
		setupLogger();
		LOGGER.info("------------------Database initiated--------------------");
	}

	private void initiateData() {
		csvrp = new CSV("people.csv");
		csvrj = new CSV("journals.csv");

		ArrayList<String[]> strings = csvrp.readFromFile();
		for (String[] s : strings) {
			people.add(new Person(s));
		}

		ArrayList<String[]> stringsj = csvrj.readFromFile();
		for (String[] s : stringsj) {
			journals.add(new JournalEntry(s));
		}
	}

	public static void setupLogger() {

		try {
			fileHandler = new FileHandler("./logfile.log");// file
			SimpleFormatter simple = new SimpleFormatter();
			fileHandler.setFormatter(simple);

			LOGGER.addHandler(fileHandler);// adding Handler for file

		} catch (IOException e) {
		}

	}

	public String login(String usern, String passw, String pin) throws ValidityException {
		for (Person p : people) {
			if (p.getID() == Integer.parseInt(usern)) {
				String hash = SHA512.hash(SHA512.hash(passw) + p.getSalt());

				if (hash.equals(p.getPassword())) {
					if (p.getPin() == Integer.parseInt(pin)) {
						int failed = p.resetFail();
						String token = ref.addInfo(p);
						LOGGER.info(usern + " logged in after " + failed + " failed attempts and was granted token: "
								+ token);
						return success(token + "@" + ref.getOccupation(token) + "@" + failed);
					} else {
						p.incFail();
						LOGGER.info(usern + " tried to log in but failed at pin check");
						return error("Invalid PIN");
					}
				} else {
					p.incFail();
				}
			}
		}
		LOGGER.info(usern + " tried to log in but failed at username and password check");
		return error("Invalid username or password");
	}

	public String write(String token, LinkedList<String> data) throws ValidityException {
		int id = Integer.parseInt(data.get(2)); // Hamtar JournID
		if (ref.getOccupation(token).equals("doctor")) {
			for (JournalEntry je : journals) {
				int journalID = Integer.parseInt(je.getJournalID());
				if (journalID == id && je.getDoctor() == ref.getID(token)) {
					je.update(data.subList(2, data.size()));
					csvrj.editInFile(je.toString());
					LOGGER.info(ref.getID(token) + " edited journal nr." + journalID);
					return success("Journal updated");
				}
			}
			// L輍g till i journals.csv p� n嶓ot smart s酹t
			LOGGER.info(ref.getID(token) + " added a journal with nr." + id);
			JournalEntry je = new JournalEntry(data.subList(2, data.size()));
			journals.add(je);
			csvrj.addToFile(je.toString());
			return success("Journal added");
		}
		LOGGER.info(ref.getID(token) + " tried to edit journal nr." + id + " but access was denied");
		return error("Access denied.");
	}

	public String delete(String token, String journID) throws ValidityException {
		int id = ref.getID(token);
		if (ref.getOccupation(token).equals("government")) {
			// Remove journID
			LOGGER.info(id + " removed journal nr." + journID);
			csvrj.deleteFromFile(Integer.parseInt(journID));
			for(JournalEntry je : journals) {
				if(je.getJournalID().equals(journID)) {
					journals.remove(je);
					break;
				}
			}
			return success("Journal removed");
		}
		LOGGER.info(id + " tried to delete journal nr." + journID + " but access was denied");
		return error("Access denied");
	}

	public String readMany(String token) throws ValidityException {
		StringBuilder sb = new StringBuilder();
		String occ = ref.getOccupation(token);
		for (JournalEntry je : journals) {
			int userID = ref.getID(token);
			switch (occ) {
			case "doctor":
				if (ref.getDivision(token).equals(je.getDivision()) || userID == je.getDoctor()) {
					append(sb, je, userID);
				}
				break;
			case "nurse":
				if (ref.getDivision(token).equals(je.getDivision()) || userID == je.getNurse()) {
					append(sb, je, userID);
				}
				break;
			case "patient":
				if (userID == je.getPatient()) {
					append(sb, je, userID);
				}
				break;
			case "government":
				append(sb, je, userID);
			}
		}
		return sb.toString();
	}

	private void append(StringBuilder sb, JournalEntry je, int userID) {
		sb.append(je.toString() + "@");
		LOGGER.info(userID + " can see journal nr." + je.getJournalID());
	}

	public String read(String token, String JournID) throws ValidityException {
		int id = Integer.parseInt(JournID);
		int userID = ref.getID(token);
		String occ = ref.getOccupation(token);
		for (JournalEntry je : journals) {
			int journID = Integer.parseInt(je.getJournalID());
			switch (occ) {
			case "doctor":
				if (journID == id && je.getDoctor() == userID) {
					LOGGER.info(userID + " read journal nr." + journID);
					return "true";
				}
				break;
			case "nurse":
				if (journID == id && je.getNurse() == userID) {
					LOGGER.info(userID + " read journal nr." + journID);
					return "true";
				}
				break;
			}
		}
		LOGGER.info(userID + " read journal nr." + id + " but write access was denied");
		return "false";
	}

	public String unavailable() {
		return error("Endpoint not implemented");
	}

	private String error(String description) {
		return "Error@" + description;
	}

	private String success(String description) {
		return "Success@" + description;
	}

	public void logout(String token) throws ValidityException {
		LOGGER.info(ref.getID(token) + " has logged out");
		ref.removeToken(token);
	}

}
