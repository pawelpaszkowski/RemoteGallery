package com.remotegallery.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class MainController {

	@FXML
	private AnchorPane mainPane;

	@FXML
	public void initialize() {
		FXMLLoader loader= new FXMLLoader();
		loader.setLocation(this.getClass().getResource("../../../resources/fxml/signin.fxml"));
		AnchorPane anchorPane= null;
		try {
			anchorPane=loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SignInController signInCntroller= loader.getController();
		signInCntroller.setMainController(this);
		setScreen(anchorPane);
	}

	public void setScreen(AnchorPane anchorPane){
		mainPane.getChildren().add(anchorPane);
	}

}
