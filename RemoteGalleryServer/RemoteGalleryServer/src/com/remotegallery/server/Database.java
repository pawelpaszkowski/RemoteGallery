package com.remotegallery.server;
import java.io.InputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.ArrayList;

public interface Database extends Remote{
	//public static final String DRIVER = "com.mysql.jdbc.Driver";

	void executeStatement(String query) throws RemoteException;
	ResultSet executeQuery(String query) throws RemoteException;
	public boolean createTables() throws RemoteException;
	public String checkingIsEmailBusy(String email) throws RemoteException;
	public  boolean checkingIsOnlyEmailBusy(String emailField) throws RemoteException;
	public String selectName(String email) throws RemoteException;
	public String selectSurname(String email) throws RemoteException;
	public  void insertNewUser(String email, String password) throws RemoteException;
	public void deleteUser(String email) throws RemoteException;
	public  void updateUser(String email, String previousEmail, String name,
			String surname) throws RemoteException;
	public  void updatePassword(String email, String pasword)throws RemoteException;
	public  void insertRemindedPassword(String email, String userPassword) throws RemoteException;
	public ArrayList <String> choosingPlaces(String email) throws RemoteException;
//	public  ResultSet choosingPictures(ChoiceBox<String> choosedPlace, String email)throws RemoteException;
	public ArrayList <byte[]> choosingPictures(String choosedPlace, String email) throws RemoteException;
	public  int numberOfAddedPictures(String email) throws RemoteException;
	public int countChoosingPictures(String choosedPlace, String email) throws RemoteException;
	public  boolean checkingRemindedPassword(String email) throws RemoteException;
	public void close() throws RemoteException;
	public ArrayList<Double> findingAllMarkersX() throws RemoteException;
	public ArrayList<Double> findingAllMarkersY() throws RemoteException;
	//-------
	public void sendMessage(String email, String userPassword) throws RemoteException;
	public boolean verifyMail(String email) throws RemoteException;
	public boolean verifyName(String name) throws RemoteException;
	public void addFile(String name, byte[] file, long imageLength, String latitude, String longitude, String email, String place) throws RemoteException;
}
