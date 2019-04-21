package gui;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import java.util.LinkedList;

import client.GUIClient;
import gui.styling.CustomTextInputDialog;

public class LoginViewController {

	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Text failedAttemptsText;

	private int failedCounter;

	@FXML
	private void onLoginPressed() {
		if (tryAuthorise()) {
			MainGui.setUserLoggedInID(usernameField.getText());
			MainGui.showJournalTableView();
		}
	}

	@FXML
	private void keyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER)
			onLoginPressed();
	}

	@FXML
	private void onSwapTheme() {
		MainGui.swapTheme();
	}

	private boolean tryAuthorise() {
		try {
			int username = Integer.parseInt(usernameField.getText());
			String password = passwordField.getText();
			String pin = CustomTextInputDialog.showDialog();
			LinkedList<String> serverRespons = GUIClient.sendMessage("login@" + username + "@" + password + "@" + pin);
			if (serverRespons.get(1).equals("Success")) {
				MainGui.setOccupation(serverRespons.get(3));
				MainGui.setFailedLoginAttempts(serverRespons.get(4));
				return true;
			}
			passwordField.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (failedCounter == 0)
			failedAttemptsText.setVisible(true);
		failedAttemptsText.setText("Failed attempts: " + ++failedCounter);
		return false;
	}
}
