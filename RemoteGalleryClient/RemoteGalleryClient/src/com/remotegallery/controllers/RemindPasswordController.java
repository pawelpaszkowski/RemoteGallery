package com.remotegallery.controllers;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.remotegallery.server.Database;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class RemindPasswordController {
	private MainController mainController;
	@FXML
	private TextField emailField;
	@FXML
	private Label errors;
	@FXML
	private TitledPane errorPane;

	@FXML
	private void initialize() {
		errorPane.setVisible(false);
	}

	@FXML
	public void remindPassword() throws RemoteException, NotBoundException {
		boolean isDataOk = true;
		boolean isYourDataCorrect = false;
		boolean sendedEmail = false;
		String errorMessage = "";
		String userPassword = "";
		// TWORZENIE OBIEKTU BAZY -RMI
		Registry reg = LocateRegistry.getRegistry("localhost", 1099);
		Database database = (Database) reg.lookup("databaseRMI");

		if (!database.verifyMail(emailField.getText())) {
			errorMessage += "Email nieprawid³owy.\n";
			isDataOk = false;
		}
		if (emailField.getLength() > 50) {
			errorMessage += "Email za d³ugi (maks. 50 znaków).\n";
			isDataOk = false;
		}

		if (isDataOk) {
			// sprawdzenie czy podany mail jest w bazie
			userPassword = database.checkingIsEmailBusy(emailField.getText());

			if (userPassword != null)
				isYourDataCorrect = true;
			else {
				errorPane.setText("Niepoprawne dane");
				errorMessage += "Brak podanego maila w bazie.\n";
				errors.setText(errorMessage);
				errorPane.setVisible(true);
			}

			if (isYourDataCorrect) {

				// sprawdzenie czy do godziny wstecz nie wyslano przypomnienia
				 sendedEmail=database.checkingRemindedPassword(emailField.getText());
				// wysylanie maila
				if (!sendedEmail) {
					database.sendMessage(emailField.getText(), userPassword);
					database.insertRemindedPassword(emailField.getText(), userPassword);
					errorPane.setText("Wys³ano potwierdzenie");
					errorMessage += "SprawdŸ swoj¹ skrzynkê pocztow¹.\n";
					errors.setText(errorMessage);
					errorPane.setVisible(true);
					sendedEmail = true;
				} else {
					errorPane.setText("B³¹d wysy³ania");
					errorMessage += "Ju¿ wys³ano przypomnienie.\n";
					errorMessage += "Spróbuj za jakiœ czas.\n";
					errors.setText(errorMessage);
					errorPane.setVisible(true);
				}
			}
		} else {
			errorPane.setText("Niepoprawne dane");
			errors.setText(errorMessage);
			errorPane.setVisible(true);
		}

	}

	@FXML
	public void cancel() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("../../../resources/fxml/signin.fxml"));
		AnchorPane anchorPane = null;
		try {
			anchorPane = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SignInController signInController = loader.getController();
		signInController.setMainController(mainController);
		mainController.setScreen(anchorPane);
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

}
