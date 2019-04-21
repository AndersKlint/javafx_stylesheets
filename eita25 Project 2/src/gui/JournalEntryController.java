package gui;

import java.net.URL;
import java.util.ResourceBundle;

import client.GUIClient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.JournalEntry;

public class JournalEntryController implements Initializable {

	@FXML
	private TextField journalID;
	@FXML
	private TextField patientName;
	@FXML
	private TextField date;
	@FXML
	private TextField doctor;
	@FXML
	private TextField nurse;
	@FXML
	private TextField patientID;
	@FXML
	private TextField division;
	@FXML
	private TextArea description;
	@FXML
	private Button saveButton;

	private JournalEntry currentJournal;

	@FXML
	private void onSavePressed() {
		boolean wrong = false;
		String[] params = new String[8];
		params[0] = journalID.getText();
		params[1] = patientName.getText();
		params[2] = date.getText();
		params[3] = doctor.getText();
		params[4] = nurse.getText();
		params[5] = patientID.getText();
		params[6] = division.getText();
		params[7] = description.getText();
		for (String s : params) {
			if (s.equals("")) {
				wrong = true;
				break;
			}
		}
		if (!wrong) {
			currentJournal = new JournalEntry(params);
			GUIClient.sendMessage("write@" + currentJournal.toString()); // tries to write
			MainGui.showJournalTableView();
		}
	}

	@FXML
	private void onCancelPressed() {
		MainGui.showJournalTableView();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		currentJournal = MainGui.CurrentJournalEntry; // communicating between controllers is hard. I'm open for
														// suggestions.
		if (currentJournal != null) {
			if (!MainGui.canEditCurrentJournal) {
				setDisableFields(true);
				saveButton.setText("Done");
			} else
				setDisableFields(false);
			populateFields();
		}
		else
			setDisableFields(false);
	}

	private void setDisableFields(boolean disable) {
		journalID.setDisable(disable);
		patientName.setDisable(disable);
		date.setDisable(disable);
		nurse.setDisable(disable);
		doctor.setDisable(disable);
		patientID.setDisable(disable);
		division.setDisable(disable);
		description.setDisable(disable);
		if (MainGui.getOccupation().equals("doctor")) {
			doctor.setDisable(true);
			doctor.setText(MainGui.getUserLoggedInID());
		}
	}

	private void populateFields() {
		journalID.setText(currentJournal.getJournalID());
		patientName.setText(currentJournal.getName());
		date.setText(currentJournal.getDate());
		doctor.setText(String.valueOf(currentJournal.getDoctor()));
		nurse.setText(String.valueOf(currentJournal.getNurse()));
		patientID.setText(String.valueOf(currentJournal.getPatientID()));
		division.setText(currentJournal.getDivision());
		description.setText(currentJournal.getDescription());
	}

}
