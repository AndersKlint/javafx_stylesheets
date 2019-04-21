package gui.styling;

import java.util.Optional;

import gui.MainGui;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;

public class CustomTextInputDialog extends TextInputDialog {
	
	public static String showDialog() {
	Dialog<String> dialog = new Dialog<>();
	dialog.setTitle("PIN Dialog");
	//dialog.setHeaderText("Please enter PIN.");
	
	dialog.getDialogPane().getStylesheets().add(MainGui.getCurrentStylesheetPath());


	// Set the button types.
	ButtonType loginButtonType = new ButtonType("Confirm", ButtonData.OK_DONE);
	dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

	// Create the password and password labels and fields.
	GridPane grid = new GridPane();
	grid.setHgap(10);
	grid.setVgap(10);
	grid.setPadding(new Insets(32, 32, 16, 32));

	PasswordField password = new PasswordField() {
		@Override
		public void replaceText(int start, int end, String string) {
			if(string.matches("[\\d]*"))
				super.replaceText(start, end, string);
		}
	};
	password.setPromptText("enter pin");

	grid.add(new Label("PIN: "), 0, 0);
	grid.add(password, 1, 0);

	// Enable/Disable login button depending on whether a password was entered.
	Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
	loginButton.setDisable(true);
	
	// Do some validation (using the Java 8 lambda syntax).
	password.textProperty().addListener((observable, oldValue, newValue) -> {
	    loginButton.setDisable(newValue.trim().isEmpty());
	});

	dialog.getDialogPane().setContent(grid);

	// Request focus on the password field by default.
	Platform.runLater(() -> password.requestFocus());
	
	//set result = password on close
	dialog.setResultConverter(dialogButton -> {
	    if (dialogButton == loginButtonType) {
	        return password.getText();
	    }
	    return "0";
	});

	Optional<String> result = dialog.showAndWait();
	if (result.isPresent())
	    return result.get();
	return "0";
	}
}
