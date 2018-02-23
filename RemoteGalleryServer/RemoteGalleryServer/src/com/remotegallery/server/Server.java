package com.remotegallery.server;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.naming.Context;
import javax.naming.InitialContext;

import javafx.application.Application;
import javafx.stage.Stage;



public class Server extends Application{
	public static void main(String[] args) {
		try {
			Database database = new DatabaseImpl();
			
			// 1
			Registry reg = LocateRegistry.createRegistry(1099);
			reg.bind("databaseRMI", database);
			// 2
			Context context = new InitialContext();
			context.bind("rmi:databaseRMI2", database);
			System.out.println("OK...");
			launch(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
