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

public class AccountController {

	String name = "", surname = "";
	@FXML
	private MainController mainController;
	@FXML
	private TextField emailField, nameField, surnameField;
	@FXML
	private TitledPane errorPane;
	@FXML
	private Label errors;

	@FXML
	private void initialize() throws RemoteException, NotBoundException {
		// TWORZENIE OBIEKTU BAZY -RMI
		Registry reg = LocateRegistry.getRegistry("localhost", 1099);
		Database database = (Database) reg.lookup("databaseRMI");

		errorPane.setVisible(false);
		name = database.selectName(SignInController.currentMail);
		surname = database.selectSurname(SignInController.currentMail);

		if (name == null)
			name = "";
		if (surname == null)
			surname = "";

		emailField.setText(SignInController.currentMail);
		nameField.setText(name);
		surnameField.setText(surname);
	}

	@FXML
	public void goBack() {
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
	}

	@FXML
	public void editAccount() throws RemoteException, NotBoundException {
		// TWORZENIE OBIEKTU BAZY -RMI
		Registry reg = LocateRegistry.getRegistry("localhost", 1099);
		Database database = (Database) reg.lookup("databaseRMI");

		boolean isDataOk = true;
		String errorMessage = "";

		if (nameField.getText().length() > 30) {
			errorMessage += "Imiê za d³ugie (maks. 30 znaków).\n";
			isDataOk = false;
		}
		if (surnameField.getText().length() > 30) {
			errorMessage += "Nazwisko za d³ugie (maks. 30 znaków).\n";
			isDataOk = false;
		}
		if (emailField.getLength() > 50) {
			errorMessage += "Email za d³ugi (maks. 50 znaków).\n";
			isDataOk = false;
		}
		if (!database.verifyName(nameField.getText())) {
			errorMessage += "Imiê musi zawieraæ tylko litery.\n";
			isDataOk = false;
		}
		if (!database.verifyName(surnameField.getText())) {
			errorMessage += "Nazwisko musi zawieraæ tylko litery.\n";
			isDataOk = false;
		}
		if (!database.verifyMail(emailField.getText())) {
			errorMessage += "Niepoprawny email.\n";
			isDataOk = false;
		}

		// sprawdzenie czy dokonano zmian
		boolean changesOfData = false;
		if (isDataOk && name.equals(nameField.getText()) && surname.equals(surnameField.getText())
				&& SignInController.currentMail.equals(emailField.getText())) {
			errorMessage += "Nie dokonano ¿adnych modyfikacji.\n";
			errors.setText(errorMessage);
			errorPane.setVisible(true);
			errorPane.setText("Brak zmian");
			changesOfData = true;
		}

		if (isDataOk) {
			// sprawdzenie czy email nie jest juz zajêty
			if (database.checkingIsOnlyEmailBusy(emailField.getText())
					&& !emailField.getText().equals(SignInController.currentMail)) {
				errorMessage += "Podany email jest juz zajêty\n";
				errors.setText(errorMessage);
				errorPane.setText("Niepoprawne dane");
				errorPane.setVisible(true);
				isDataOk = false;
			}
		}
		//
		if (isDataOk && !changesOfData) {
			name = nameField.getText();
			surname = surnameField.getText();
			database.updateUser(emailField.getText(), SignInController.currentMail, name, surname);

			SignInController.currentMail = emailField.getText();
			errorMessage += "Pomyœlnie zmieniono dane.\n";
			errors.setText(errorMessage);
			errorPane.setVisible(true);
			errorPane.setText("Aktualizacja dokonana");

		} else {
			errors.setText(errorMessage);
			errorPane.setVisible(true);
			errorPane.setText("Niepoprawne dane");
		}
	}

	@FXML
	public void changePassword() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("../../../resources/fxml/changePassword.fxml"));
		AnchorPane anchorPane = null;
		try {
			anchorPane = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ChangePasswordController changePasswordController = loader.getController();
		changePasswordController.setMainController(mainController);
		mainController.setScreen(anchorPane);
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	@FXML
	public void deleteAccount() throws RemoteException, NotBoundException {
		// TWORZENIE OBIEKTU BAZY -RMI
		Registry reg = LocateRegistry.getRegistry("localhost", 1099);
		Database database = (Database) reg.lookup("databaseRMI");
		database.deleteUser(SignInController.currentMail);

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

}
