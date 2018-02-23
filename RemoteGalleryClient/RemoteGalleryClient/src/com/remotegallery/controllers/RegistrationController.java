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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class RegistrationController {
	private MainController mainController;
	@FXML
	private TextField emailField;
	@FXML
	private PasswordField passwordField1;
	@FXML
	private PasswordField passwordField2;
	@FXML
	private Label errors;
	@FXML
	private TitledPane errorPane;

	@FXML
	private void initialize() {
		errorPane.setVisible(false);
	}

	public void register() throws RemoteException, NotBoundException {
		boolean isDataOk = true;
		boolean isEmailInDatabase = false;
		String errorMessage = "";
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

		if (!passwordField1.getText().equals(passwordField2.getText())) {
			errorMessage += "Podane has³a sa ró¿ne.\n";
			isDataOk = false;
		}

		if (passwordField1.getLength() < 6) {
			errorMessage += "Has³o musi zawieraæ min. 6 znaków.\n";
			isDataOk = false;
		}

		if (passwordField1.getLength() > 30) {
			errorMessage += "Has³o za d³ugie (maks. 30 znaków).\n";
			isDataOk = false;
		}

		if (isDataOk) {
			if (database.checkingIsOnlyEmailBusy(emailField.getText())) {
				errorMessage += "Podany login jest juz w bazie danych\n";
				errors.setText(errorMessage);
				errorPane.setVisible(true);
				isEmailInDatabase = true;
			} else
				database.insertNewUser(emailField.getText(), passwordField1.getText());

			if (!isEmailInDatabase) {
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

		} else {
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
