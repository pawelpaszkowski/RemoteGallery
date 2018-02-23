package com.remotegallery.controllers;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;

import com.remotegallery.server.Database;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class SignInController {

	private MainController mainController;

	// information about current session of user
	public static String currentMail;
	public static LocalDateTime dataOfLogIn;
	public static int numberOfAddedPhotos = 0;
	public static int numberOfAddedDocuments = 0;
	@FXML
	private TextField emailField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Label errors;
	@FXML
	private TitledPane errorPane;

	@FXML
	private void initialize() {
		errorPane.setVisible(false);
	}

	@FXML
	public void signIn() throws RemoteException, NotBoundException {
		boolean isDataOk = true;
		boolean isYourDataCorrect = false;
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
		if (passwordField.getLength() < 6) {
			errorMessage += "Has³o musi zawieraæ min. 6 znaków.\n";
			isDataOk = false;
		}
		if (passwordField.getLength() > 30) {
			errorMessage += "Has³o za d³ugie (maks. 30 znaków).\n";
			isDataOk = false;
		}

		if (isDataOk) {
			String userPassword = database.checkingIsEmailBusy(emailField.getText());

			if (userPassword == null) {
				errorMessage += "Brak podanego maila w bazie.\n";
				errors.setText(errorMessage);
				errorPane.setVisible(true);
			} else {
				if (userPassword.equals(passwordField.getText()))
					isYourDataCorrect = true;
				else {
					errorMessage += "Nieprawid³owe has³o.\n";
					errors.setText(errorMessage);
					errorPane.setVisible(true);
				}
			}
		}

		if (isYourDataCorrect) {

			currentMail = emailField.getText();
			dataOfLogIn = LocalDateTime.now();
			numberOfAddedPhotos = 0;
			numberOfAddedDocuments = 0;
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getResource("../../../resources/fxml/menu.fxml"));
			AnchorPane anchorPane = null;
			try {
				anchorPane = loader.load();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			MenuController menuController = loader.getController();
			menuController.setMainController(mainController);
			mainController.setScreen(anchorPane);
		} else {
			errors.setText(errorMessage);
			errorPane.setVisible(true);
		}
	}

	@FXML
	public void remindPassword() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("../../../resources/fxml/remindPassword.fxml"));
		AnchorPane anchorPane = null;
		try {
			anchorPane = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RemindPasswordController remindPasswordController = loader.getController();
		remindPasswordController.setMainController(mainController);
		mainController.setScreen(anchorPane);
	}

	@FXML
	public void goToRegisterForm() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("../../../resources/fxml/registration.fxml"));
		AnchorPane anchorPane = null;
		try {
			anchorPane = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RegistrationController registrationController = loader.getController();
		registrationController.setMainController(mainController);
		mainController.setScreen(anchorPane);
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	public static String getCurrentMail() {
		return currentMail;
	}

	public void setCurrentMail(String currentMail) {
		SignInController.currentMail = currentMail;
	}

	public static LocalDateTime getDataOfLogIn() {
		return dataOfLogIn;
	}

	public static void setDataOfLogIn(LocalDateTime dataOfLogIn) {
		SignInController.dataOfLogIn = dataOfLogIn;
	}

}
