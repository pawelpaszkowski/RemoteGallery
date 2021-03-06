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
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class ChangePasswordController {

	@FXML
	private MainController mainController;
	@FXML
	private PasswordField passwordField1, passwordField2, passwordField3;
	@FXML
	private TitledPane errorPane;
	@FXML
	private Label errors;

	@FXML
	private void initialize() {
		errorPane.setVisible(false);
	}

	@FXML
	public void goBack() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("../../../resources/fxml/account.fxml"));
		AnchorPane anchorPane = null;
		try {
			anchorPane = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AccountController accountController = loader.getController();
		accountController.setMainController(mainController);
		mainController.setScreen(anchorPane);
	}

	@FXML
	public void changePassword() throws RemoteException, NotBoundException {
		// TWORZENIE OBIEKTU BAZY -RMI
		Registry reg = LocateRegistry.getRegistry("localhost", 1099);
		Database database = (Database) reg.lookup("databaseRMI");
		boolean isDataOk = true, oldAndNewPasswordTheSame = false;
		String errorMessage = "";

		if (passwordField1.getLength() < 6) {
			errorMessage += "Aktualne has這 zbyt kr鏒kie.\n";
			isDataOk = false;
		} else {
			// spradzenie czy zgadza sie aktualne has這
			String password = database.checkingIsEmailBusy(SignInController.currentMail);

			if (password.equals(passwordField1.getText()))
				oldAndNewPasswordTheSame = true;
			else {
				errorPane.setText("Niepoprawne dane");
				errorMessage += "Podano nieprawid這we aktualne has這.\n";
				errors.setText(errorMessage);
				errorPane.setVisible(true);
				isDataOk = false;
			}
		}

		if (passwordField2.getLength() < 6)
		{
			errorMessage += "Nowe has這 musi mie� min. 6 znak闚\n";
			isDataOk = false;
		}
		if (passwordField2.getLength() > 30) {
			errorMessage += "Nowe has這 mo瞠 mie� do 30 znak闚.\n";
			isDataOk = false;
		}
		if (!passwordField2.getText().equals(passwordField3.getText())) {
			errorMessage += "Podane, nowe has豉 sa r騜ne.\n";
			isDataOk = false;
		} else {
			if (oldAndNewPasswordTheSame && passwordField1.getText().equals(passwordField2.getText())) {
				errorMessage += "Aktualne i nowe has豉 s� te same.\n";
				isDataOk = false;
			}
		}

		if (isDataOk) {
			errorPane.setText("Aktualizacja dokonana");
			errorMessage += "Zmieniono has這.\n";
			errors.setText(errorMessage);
			errorPane.setVisible(true);
			database.updatePassword(SignInController.currentMail, passwordField2.getText());
		} else {
			errorPane.setText("Niepoprawne dane");
			errors.setText(errorMessage);
			errorPane.setVisible(true);
			errorPane.setText("Niepoprawne dane");
		}
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

}
