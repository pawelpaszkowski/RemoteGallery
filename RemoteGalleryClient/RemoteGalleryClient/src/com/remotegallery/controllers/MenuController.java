package com.remotegallery.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class MenuController {

	@FXML
	private MainController mainController;

	@FXML
	public void goToMap() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("../../../resources/fxml/maps.fxml"));
		AnchorPane anchorPane = null;
		try {
			anchorPane = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MapsController mapsController = loader.getController();
		mapsController.setMainController(mainController);
		mainController.setScreen(anchorPane);
	}

	@FXML
	public void goToPlaces() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("../../../resources/fxml/places.fxml"));
		AnchorPane anchorPane = null;
		try {
			anchorPane = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PlacesController placesController = loader.getController();
		placesController.setMainController(mainController);
		mainController.setScreen(anchorPane);
	}

	@FXML
	public void goToStatistics() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("../../../resources/fxml/statistics.fxml"));
		AnchorPane anchorPane = null;
		try {
			anchorPane = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StatisticsController statisticsController = loader.getController();
		statisticsController.setMainController(mainController);
		mainController.setScreen(anchorPane);
	}

	@FXML
	public void manageAccount() {
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
	public void logOut() {
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
