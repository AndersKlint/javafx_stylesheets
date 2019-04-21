package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.LinkedList;
import java.util.List;

/* mock-class to test functionality */
public class JournalEntry {
	// string properties must be used to populate the tableView
	private StringProperty journalID;
	private StringProperty name;
	private StringProperty date;

	private int doctor, nurse, patientID;
	private String description;
	private String division;

	public JournalEntry(String[] strings) {
		this.journalID = new SimpleStringProperty(strings[0]);
		this.name = new SimpleStringProperty(strings[1]);
		this.date = new SimpleStringProperty(strings[2]);
		this.doctor = Integer.parseInt(strings[3]);
		this.nurse = Integer.parseInt(strings[4]);
		this.patientID = Integer.parseInt(strings[5]);
		this.division = strings[6];
		this.description = strings[7];
	}

	public JournalEntry(List<String> list) {
		update(list);
	}

	public void update(List<String> list) {
		this.journalID = new SimpleStringProperty(list.get(0));
		this.name = new SimpleStringProperty(list.get(1));
		this.date = new SimpleStringProperty(list.get(2));
		this.doctor = Integer.parseInt(list.get(3));
		this.nurse = Integer.parseInt(list.get(4));
		this.patientID = Integer.parseInt(list.get(5));
		this.division = list.get(6);
		this.description = list.get(7);
	}

	public String getJournalID() {
		return journalID.getValueSafe();
	}

	public int getPatientID() {
		return patientID;
	}

	public void setId(String id) {
		this.journalID.set(id);
	}

	public String getName() {
		return name.getValueSafe();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getDate() {
		return date.getValueSafe();
	}

	public void setDate(String date) {
		this.date.set(date);
	}

	public String toString() {
		return this.journalID.getValueSafe() + "@" + this.name.getValueSafe() + "@" + this.date.getValueSafe() + "@" + this.doctor + "@"+ this.nurse + "@" + this.patientID + "@" + this.division + "@" + this.description;
	}

	public String getDivision() {
		return division;
	}

	public int getDoctor() {
		return doctor;
	}

	public int getNurse() {
		return nurse;
	}

	public int getPatient() {
		return patientID;
	}
	
	public LinkedList<Integer> getAllPeople() {
		LinkedList<Integer> people = new LinkedList<Integer>();
		people.add(doctor);
		people.add(nurse);
		people.add(patientID);
		return people;
	}

	public String getDescription() {
		return description;
	}

}
