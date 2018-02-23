package com.remotegallery.controllers;

import java.io.IOException;
import java.net.URL;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import netscape.javascript.JSObject;

public class MapsController implements Initializable, MapComponentInitializedListener {

	@FXML
	private MainController mainController;

	@FXML
	private GoogleMapView mapView;

	private GoogleMap map;

	@FXML
	public void addContent() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("../../../resources/fxml/addContent.fxml"));
		AnchorPane anchorPane = null;
		try {
			anchorPane = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AddController addController = loader.getController();
		addController.setMainController(mainController);
		mainController.setScreen(anchorPane);
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

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		mapView.addMapInializedListener(this);
		// mapView.addMapInializedListener(() -> mapInitialized());
	}
	// @Override
	// public void mapInitialized() {
	// mapView.addMapInializedListener(this);
	// MapOptions options = new MapOptions();
	//
	// options.center(new LatLong(47.606189, -122.335842))
	// .zoomControl(true)
	// .zoom(12)
	// .overviewMapControl(false)
	// .mapType(MapTypeIdEnum.ROADMAP);
	// GoogleMap map = mapView.createMap(options);
	// MarkerOptions markerOptions = new MarkerOptions();
	//
	// markerOptions.position( new LatLong(47.6, -122.3) )
	// .visible(Boolean.TRUE)
	// .title("My Marker");
	// Marker marker = new Marker( markerOptions );
	//
	// map.addMarker(marker);
	//// map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {
	//// System.out.println("Hello world");
	//// });
	//
	//
	// }

	@Override
	public void mapInitialized() {
		// TWORZENIE OBIEKTU BAZY -RMI
		Database database = null;
		try {
			Registry reg = LocateRegistry.getRegistry("localhost", 1099);
			database = (Database) reg.lookup("databaseRMI");
		} catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MapOptions options = new MapOptions();

		options.center(new LatLong(47.606189, -122.335842)).zoomControl(true).zoom(12).overviewMapControl(false)
				.mapType(MapTypeIdEnum.ROADMAP);
		GoogleMap map = mapView.createMap(options);

		ArrayList<Double> x = new ArrayList<Double>();
		ArrayList<Double> y = new ArrayList<Double>();
		try {
			x = database.findingAllMarkersX();
			y = database.findingAllMarkersY();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MarkerOptions addMarkerOptions;
		LatLong latLong = null;

		while (!x.isEmpty()) {
			latLong = new LatLong(x.get(0), y.get(0));
			addMarkerOptions = new MarkerOptions();
			addMarkerOptions.position(latLong).visible(Boolean.TRUE).title("My Marker");
			Marker marker = new Marker(addMarkerOptions);
			map.addMarker(marker);
			x.remove(0);
			y.remove(0);
			map.addUIEventHandler(marker, UIEventType.click, (JSObject obj) -> {


			});
		}

		// map.addMouseEventHandler(null , UIEventType.click, (GMapMouseEvent
		// event) -> {
		// LatLong latLong = event.getLatLong();
		// ResultSet resultSet2 =
		// Database.findingMarkerWithLL(latLong.getLatitude(),latLong.getLongitude());
		// System.out.println(latLong.getLatitude()+" "+latLong.getLongitude());
		//
		//
		// try {
		// while(resultSet2.next()){
		// Stage stage = new Stage(StageStyle.UNDECORATED);
		// stage.initOwner(mapView.getScene().getWindow());
		//
		//
		// Pane layout = new Pane();
		// layout.setStyle("-fx-background-color: paleturquoise;");
		// layout.setPrefSize(40, 40);
		// try {
		// ImageView img = new ImageView(new
		// Image(resultSet2.getBinaryStream(2)));
		// layout.getChildren().add(img);
		// } catch (Exception e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// layout.setOnMouseClicked(e -> stage.close());
		// stage.setScene(new Scene(layout));
		//
		// stage.show();
		// }
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// });
		// ----------------------------------------

		// map.addMouseEventHandler(null , UIEventType.click, (GMapMouseEvent
		// event) -> {
		// LatLong latLong = event.getLatLong();
		// System.out.println(latLong.getLatitude()+" "+latLong.getLongitude());
		// MarkerOptions addMarkerOptions = new MarkerOptions();
		// addMarkerOptions.position( latLong )
		// .visible(Boolean.TRUE)
		// .title("My Marker");
		// Marker marker = new Marker( addMarkerOptions );
		// map.addMarker(marker);
		// });
	}
}
