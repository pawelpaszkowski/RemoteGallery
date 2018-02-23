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
import javafx.scene.layout.AnchorPane;

public class StatisticsController {
	@FXML
	private MainController mainController;

	@FXML
	Label labelTimeLogIn, labelNumberOfPhotos, labelNumberOfDocuments, labelNumberOfCurrentPhotos, labelNumberOfCurrentDocuments;

	@FXML
	private void initialize() throws RemoteException, NotBoundException{
		// TWORZENIE OBIEKTU BAZY -RMI
		Registry reg = LocateRegistry.getRegistry("localhost", 1099);
		Database database = (Database) reg.lookup("databaseRMI");
		int year=SignInController.dataOfLogIn.getYear();
		int mounth=SignInController.dataOfLogIn.getMonthValue();
		int day=SignInController.dataOfLogIn.getDayOfMonth();
		int hour=SignInController.dataOfLogIn.getHour();
		int minute=SignInController.dataOfLogIn.getMinute();
		labelTimeLogIn.setText(day +"."+ mounth +"."+year+"  godz. "+hour+":"+minute);

		int generalNumberOfAddedPhotos=database.numberOfAddedPictures(SignInController.currentMail);
		int generalNumberOfAddedDocuments=0;

		labelNumberOfPhotos.setText(""+generalNumberOfAddedPhotos);
		labelNumberOfDocuments.setText(""+generalNumberOfAddedDocuments);
		labelNumberOfCurrentPhotos.setText(""+SignInController.numberOfAddedPhotos);
		labelNumberOfCurrentDocuments.setText(""+SignInController.numberOfAddedDocuments);
	}

	@FXML
	public void goBack() {
		FXMLLoader loader= new FXMLLoader();
		loader.setLocation(this.getClass().getResource("../../../resources/fxml/menu.fxml"));
		AnchorPane anchorPane= null;
		try {
			anchorPane=loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MenuController menuController= loader.getController();
		menuController.setMainController(mainController);
		mainController.setScreen(anchorPane);
	}

	public void setMainController(MainController mainController){
		this.mainController=mainController;
	}
}
