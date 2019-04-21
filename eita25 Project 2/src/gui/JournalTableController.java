package gui;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import client.GUIClient;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.JournalEntry;

public class JournalTableController implements Initializable {

	// Note: the table data is represented by the journals list in MainGui. The
	// list is stored in MainGUI because it used in other controller classes as
	// well. I've tried a lot but can't come up with a better solution, due to
	// not fully understanding the FXML structure.
	@FXML
	private TableView<JournalEntry> table;
	@FXML
	private TableColumn<JournalEntry, String> idCol;
	@FXML
	private TableColumn<JournalEntry, String> nameCol;
	@FXML
	private TableColumn<JournalEntry, String> dateCol;
	@FXML
	private Button addEntryButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Label occupationText;
	@FXML
	private Label failedAttemptLabel;

	@FXML
	private void onLogOutPressed() {
		MainGui.logOut();
	}

	@FXML
	private void onAddEntryPressed() {
		MainGui.CurrentJournalEntry = null; // ugly but works (creates new entry
											// later in controller)
		MainGui.showAddJournalEntry();
	}

	@FXML
	private void onDeletePressed() {
		JournalEntry selectedJournal = table.getSelectionModel().getSelectedItem();
		if (selectedJournal != null) {
			String respons = GUIClient.sendMessage("delete@" + selectedJournal.getJournalID()).get(1);
			if (!respons.equals("Error")) {
				MainGui.getJournalList().remove(selectedJournal);
				table.getItems().setAll(MainGui.getJournalList());
			}
		}
	}
	
	private void onItemClicked(MouseEvent event, TableRow<JournalEntry> row) {
		if (event.getClickCount() == 2 && (!row.isEmpty())) {
			LinkedList<String> serverRespons = GUIClient.sendMessage("read@" + row.getItem().getJournalID());
			if (!serverRespons.get(1).equals("Error")) {
				MainGui.setCanEditCurrentJournal(serverRespons.get(1));
				MainGui.CurrentJournalEntry = row.getItem();
				MainGui.showAddJournalEntry();
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		occupationText.setText(MainGui.getOccupation());
		failedAttemptLabel.setText(" [" + MainGui.getFailedLoginAttempts() + (" failed attempt(s)]"));
		// read data from server and create journals
		createJournalsFromServerResponse(GUIClient.sendMessage("readmany"));
		// init table data
		idCol.setCellValueFactory(new PropertyValueFactory<JournalEntry, String>("journalID"));
		nameCol.setCellValueFactory(new PropertyValueFactory<JournalEntry, String>("name"));
		dateCol.setCellValueFactory(new PropertyValueFactory<JournalEntry, String>("date"));
		table.getItems().setAll(MainGui.getJournalList()); // populate table
		if (!MainGui.getCanCreate())
			addEntryButton.setDisable(true);
		if (!MainGui.getCanDelete())
			deleteButton.setDisable(true);

		// On entry pressed listener
		table.setRowFactory(tv -> {
			TableRow<JournalEntry> row = new TableRow<>();
			row.setOnMouseClicked(event -> onItemClicked(event, row));
			return row;
		});
	}

	private void createJournalsFromServerResponse(LinkedList<String> journalData) {
		MainGui.getJournalList().clear(); // reset list to avoid duplicates
		journalData.remove(0); // remove first element cause not used here
		for (int i = 0; i < journalData.size() - 8; i += 8) {
			String[] data = new String[8];
			data[0] = journalData.get(i);
			data[1] = journalData.get(i + 1);
			data[2] = journalData.get(i + 2);
			data[3] = journalData.get(i + 3);
			data[4] = journalData.get(i + 4);
			data[5] = journalData.get(i + 5);
			data[6] = journalData.get(i + 6);
			data[7] = journalData.get(i + 7);
			MainGui.addJournal(new JournalEntry(data));
		}

	}

}
