package gui;

import java.io.IOException;

import client.GUIClient;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.JournalEntry;

public class MainGui extends Application {
	private static Stage primaryStage;
	private static BorderPane mainView;
	private static ObservableList<JournalEntry> journals;
	private static String occupation;
	private static String userLoggedInID;
	private static boolean canEdit, canCreate, canDelete;
	public static JournalEntry CurrentJournalEntry;
	public static boolean canEditCurrentJournal;
	private static String failedLoginAttempts;
	private static String currentStylesheetPath;

	@Override
	public void start(Stage ps) throws IOException {
		currentStylesheetPath = "gui/styling/girlStyle.css";
		String[] args = {"localhost", "9876"};
		try {
			GUIClient.initConnection(args);		// try connect to server
		} catch (Exception e) {
			e.printStackTrace();
		}
		primaryStage = ps;
		primaryStage.setTitle("Hospital Journals");
		journals = FXCollections.observableArrayList();
		showMainView();
		shownLoginView();
	}
	
	@Override
	public void stop() {
		try {
			GUIClient.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public static void logOut(){ // called upon logout
		GUIClient.sendMessage("logout");
		shownLoginView();
	}

	/*
	 * This is the main view and it's currently empty. Child views will be set as
	 * this view's center.
	 */
	private static void showMainView() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainGui.class.getResource("mainView.fxml"));
		mainView = loader.load();
		Scene scene = new Scene(mainView);
		scene.getStylesheets().add(currentStylesheetPath);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void shownLoginView() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainGui.class.getResource("loginView.fxml"));
		BorderPane view;
		try {
			view = loader.load();
			mainView.setCenter(view);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void showJournalTableView() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainGui.class.getResource("journalTableView.fxml"));
		BorderPane view;
		try {
			view = loader.load();
			mainView.setCenter(view);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void showAddJournalEntry() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainGui.class.getResource("journalEntryView.fxml"));
		BorderPane view;
		try {
			view = loader.load();
			mainView.setCenter(view);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ObservableList<JournalEntry> getJournalList() {
		return journals;
	}

	public static void addJournal(JournalEntry journal) {
		journals.add(journal);
	}

	public static boolean getCanEdit() {
		return canEdit;
	}
	
	public static boolean getCanCreate() {
		return canCreate;
	}	
	
	public static boolean getCanDelete() {
		return canDelete;
	}
	
	public static void setCanEditCurrentJournal(String edit) {
		if(edit.equals("true"))
			canEditCurrentJournal = true;
		else
			canEditCurrentJournal = false;
	}

	public static void setOccupation(String occupation) {
		MainGui.occupation = occupation;
		switch (occupation) { 
		case "doctor":
			canEdit = true;
			canCreate = true;
			canDelete = false;
			break;
		case "nurse":
			canEdit = true;
			canCreate = false;
			canDelete = false;
			break;
		case "government":
			canEdit = false;
			canCreate = false;
			canDelete = true;
			break;
		default:
			canEdit = false;
			canCreate = false;
			canDelete = false;
			break;
		}
		
	}

	public static String getOccupation() {
		return occupation;
	}
	
	public static void setFailedLoginAttempts(String attempts) {
		failedLoginAttempts = attempts;
	}

	
	public static String getFailedLoginAttempts() {
		return failedLoginAttempts;
	}

	public static void swapTheme() {
		Scene scene = primaryStage.getScene();
		if (scene.getStylesheets().get(0).equals("gui/styling/cleanModernStyle.css"))
			currentStylesheetPath = "gui/styling/girlStyle.css";
		else if (scene.getStylesheets().get(0).equals("gui/styling/girlStyle.css"))
			currentStylesheetPath = "gui/styling/edgyStyle.css";
		else
			currentStylesheetPath = "gui/styling/cleanModernStyle.css";
		scene.getStylesheets().clear();
		scene.getStylesheets().add(currentStylesheetPath);
	}
	
	public static String getCurrentStylesheetPath() {
		return currentStylesheetPath;
	}
	
	public static void setUserLoggedInID(String id) {
		userLoggedInID = id;
	}
	
	public static String getUserLoggedInID() {
		return userLoggedInID;
	}

}
