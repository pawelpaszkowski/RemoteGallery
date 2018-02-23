package com.remotegallery.controllers;

import java.awt.MouseInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.remotegallery.server.Database;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

public class AddController implements Initializable, MapComponentInitializedListener{

	@FXML
	private MainController mainController;

	@FXML
	private TextField fileName;

	@FXML
	private TextField filePath;

	@FXML
	private TextField latitude;

	@FXML
	private TextField longitude;
	@FXML
    private TextField place;

	@FXML
    private GoogleMapView addMapView;

	private GoogleMap map;


	@FXML
	public void browseFile() {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("SWF", "*.swf"),
                new FileChooser.ExtensionFilter("TIF", "*.tif")
            );
		File file = fileChooser.showOpenDialog(null);
		if(file == null) System.out.println("NULL!");
		filePath.setText(file.getAbsolutePath());

	}

	@FXML
	public void addFile() throws RemoteException, NotBoundException, FileNotFoundException{
		// TWORZENIE OBIEKTU BAZY -RMI
		Registry reg = LocateRegistry.getRegistry("localhost", 1099);
		Database database = (Database) reg.lookup("databaseRMI");
		
        int mouseX = MouseInfo.getPointerInfo().getLocation().x;
        System.out.println(mouseX);
		File image = new File(filePath.getText());
		FileInputStream fileInputStream = null;
		fileInputStream = new FileInputStream(image);

		SignInController.numberOfAddedPhotos++;
		database.addFile(fileName.getText(), fileInputStream.toString().getBytes(), image.length(), latitude.getText(), longitude.getText(), SignInController.currentMail, place.getText());
		try {
			fileInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	@Override
    public void initialize(URL url, ResourceBundle rb) {
        addMapView.addMapInializedListener(this);
       // mapView.addMapInializedListener(() -> mapInitialized());
    }
	@Override
    public void mapInitialized() {
        MapOptions options = new MapOptions();

        options.center(new LatLong(47.606189, -122.335842))
                .zoomControl(true)
                .zoom(12)
                .overviewMapControl(false)
                .mapType(MapTypeIdEnum.ROADMAP);
        GoogleMap map = addMapView.createMap(options);

       map.addMouseEventHandler(null , UIEventType.click, (GMapMouseEvent event) -> {
    	   	LatLong latLong = event.getLatLong();
    	   	latitude.setText(latLong.getLatitude()+"");
    	   	longitude.setText(latLong.getLongitude()+"");
    	   	System.out.println(latLong.getLatitude()+" "+latLong.getLongitude());
    	   	MarkerOptions addMarkerOptions = new MarkerOptions();
    	   	addMarkerOptions.position( latLong )
    	   		.visible(Boolean.TRUE)
    	   		.title("My Marker");
    	   	Marker marker = new Marker( addMarkerOptions );
    	   	map.addMarker(marker);
    	   });
    }
}
