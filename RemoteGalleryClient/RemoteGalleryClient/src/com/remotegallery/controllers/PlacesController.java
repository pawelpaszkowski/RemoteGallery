package com.remotegallery.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.remotegallery.server.Database;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class PlacesController {
	private int currentImage = 0;
	private int numberOfImages = 0;
	ArrayList <byte[]> list= new ArrayList <>();
	ResultSet results = null;
	ByteArrayInputStream inputStream=null;// zbior obrazkow

	@FXML
	private MainController mainController;

	@FXML
	private HBox imageBox;

	@FXML
	private ImageView image1, image2;

	@FXML
	private ChoiceBox<String> choosedPlace;

	@FXML
	private Label numberOfPictures;

	@FXML
	private void initialize() throws RemoteException, NotBoundException {
		// TWORZENIE OBIEKTU BAZY -RMI
		Registry reg = LocateRegistry.getRegistry("localhost", 1099);
		Database database = (Database) reg.lookup("databaseRMI");

		numberOfPictures.setVisible(false);
		numberOfPictures.setText("0/0");
		ArrayList<String> results = new ArrayList<String>();
		results = database.choosingPlaces(SignInController.currentMail);

//		choosedPlace.getItems().add("Tokio(testowa)");
//		choosedPlace.getItems().add("Berlin(testowa)");
//		choosedPlace.getItems().add("Pary¿(testowa)");

		while (!results.isEmpty()) {
			choosedPlace.getItems().add(results.get(0));
			results.remove(0);
		}
	}


	@FXML
	public void displayImages() throws RemoteException, NotBoundException {
		// TWORZENIE OBIEKTU BAZY -RMI
		Registry reg = LocateRegistry.getRegistry("localhost", 1099);
		Database database = (Database) reg.lookup("databaseRMI");
		numberOfPictures.setVisible(true);
		numberOfImages = database.countChoosingPictures(choosedPlace.getValue(), SignInController.currentMail);
		System.out.println(numberOfImages);
		
		list = database.choosingPictures(choosedPlace.getValue(),
		 SignInController.currentMail);
		System.out.println("LLL"+list.get(0));
		displayImage(true);
		if (numberOfImages > 0)
			currentImage = 1;
		else
			currentImage = 0;
		numberOfPictures.setText(currentImage + "/" + numberOfImages);
	}

	@FXML
	public void next() {
		if (currentImage < numberOfImages) {
			displayImage(false);
			currentImage++;
		}
		numberOfPictures.setText(currentImage + "/" + numberOfImages);
	}

	@FXML
	public void previous() {
		if (currentImage > 1) {
			displayImage(true);
			currentImage--;
		}
		numberOfPictures.setText(currentImage + "/" + numberOfImages);
	}

	private void displayImage(Boolean goBack) {
		Image image;
		System.out.println(numberOfImages);

		try {


			if (!goBack) {
				if (list.size()!=0) {
					System.out.println("Lista"+list.get(0));
					inputStream = new ByteArrayInputStream(list.get(0));
					OutputStream outputStream = new FileOutputStream(new File("photo.jpg"));
					byte[] content = new byte[1024];
					int size = 0;
					while ((size = inputStream.read(content)) != -1)
						outputStream.write(content, 0, size);
					
					BufferedImage img = ImageIO.read(inputStream);
					File f = new File("photo2.jpg");
					ImageIO.write(img,  "jpg", f);
					

					inputStream.close();
					outputStream.close();

					image = new Image("file:photo2.jpg", 300, 300, true, true);
					// image1.setPreserveRatio(false);
					image1.setImage(image);

					System.out.println(image);
				} else {
					image1.setImage(null);
				}
			} 
//			else {
//				if (results.previous()) {
//					inputStream = results.getBinaryStream(2);
//					OutputStream outputStream = new FileOutputStream(new File("photo.jpg"));
//					byte[] content = new byte[1024];
//					int size = 0;
//					while ((size = inputStream.read(content)) != -1)
//						outputStream.write(content, 0, size);
//
//					inputStream.close();
//					outputStream.close();
//
//					image = new Image("file:photo.jpg", 300, 300, true, true);
//					// image1.setPreserveRatio(false);
//					image1.setImage(image);
//
//					System.out.println(image);
//				} else {
//					image1.setImage(null);
//				}
//			}

		}  catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(choosedPlace.getValue());
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

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}
}
