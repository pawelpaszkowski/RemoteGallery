package com.remotegallery.client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.naming.Context;
import javax.naming.InitialContext;

import com.remotegallery.server.Database;

//import com.remotegallery.server.Database;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class Client extends Application {

	public void start(Stage primaryStage) throws Exception{
		FXMLLoader loader= new FXMLLoader();
		loader.setLocation(this.getClass().getResource("../../../resources/fxml/mainContainer.fxml"));
		AnchorPane anchorPane= loader.load();
		Scene scene= new Scene(anchorPane);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Galeria zdjêæ i dokumentów powi¹zanych z miejscami.");
		primaryStage.getIcons().add(new Image("file:images/icon.png"));
		primaryStage.show();
	}

	public static void main(String[] args) throws RemoteException {
		// TODO Auto-generated method stub
		
		String url = "rmi://localhost/";
		Database database = null;
		try {
			// 1
			Registry reg = LocateRegistry.getRegistry("localhost", 1099);
			Database stub = (Database) reg.lookup("databaseRMI");
			System.out.println("Stworzono bazê danych");
			Context context = new InitialContext();
			Database database2 = (Database) context.lookup(url
					+ "databaseRMI2");
		} catch (Exception e) {
			e.printStackTrace();
		}

		launch(args);

		database.close();

		/*RMI*/
//		try {
//			System.out.println("Klient wystartowal");
//			ServerInt ds = (ServerInt) Naming.lookup("rmi://localhost/RemoteServer");
//			ds.getRmi("    ja klient mowie powiedz coœ");
//			ds.getRmi2("       druga metoda");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

}