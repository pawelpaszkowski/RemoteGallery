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
			errorMessage += "Aktualne has³o zbyt krótkie.\n";
			isDataOk = false;
		} else {
			// spradzenie czy zgadza sie aktualne has³o
			String password = database.checkingIsEmailBusy(SignInController.currentMail);

			if (password.equals(passwordField1.getText()))
				oldAndNewPasswordTheSame = true;
			else {
				errorPane.setText("Niepoprawne dane");
				errorMessage += "Podano nieprawid³owe aktualne has³o.\n";
				errors.setText(errorMessage);
				errorPane.setVisible(true);
				isDataOk = false;
			}
		}

		if (passwordField2.getLength() < 6)
		{
			errorMessage += "Nowe has³o musi mieæ min. 6 znaków\n";
			isDataOk = false;
		}
		if (passwordField2.getLength() > 30) {
			errorMessage += "Nowe has³o mo¿e mieæ do 30 znaków.\n";
			isDataOk = false;
		}
		if (!passwordField2.getText().equals(passwordField3.getText())) {
			errorMessage += "Podane, nowe has³a sa ró¿ne.\n";
			isDataOk = false;
		} else {
			if (oldAndNewPasswordTheSame && passwordField1.getText().equals(passwordField2.getText())) {
				errorMessage += "Aktualne i nowe has³a s¹ te same.\n";
				isDataOk = false;
			}
		}

		if (isDataOk) {
			errorPane.setText("Aktualizacja dokonana");
			errorMessage += "Zmieniono has³o.\n";
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
