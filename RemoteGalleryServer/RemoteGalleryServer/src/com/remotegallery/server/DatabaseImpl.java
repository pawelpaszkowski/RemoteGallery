package com.remotegallery.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class DatabaseImpl extends UnicastRemoteObject implements Database {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static final String DRIVER = "com.mysql.jdbc.Driver";
	private Connection conn;
	private Statement stat;
	private String kindOfDatabase = "jdbc:mysql://";
	private String adress = "localhost";
	private String dataBaseName = "remotegallery";
	private String port = "3310";
	private String userName = "root";
	private String password = "admin";
	private String baza = kindOfDatabase + adress + ":" + port;

	// konstruktor laczacy sie z baza danych i tworz¹cy baze danych gdy nie
	// zostala utworzona
	public DatabaseImpl() throws RemoteException {
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			System.err.println("Nie odnaleziono sterownika mysql.");
			e.printStackTrace();
		}

		try {
			conn = DriverManager.getConnection(baza, userName, password);
			stat = conn.createStatement();
			System.out.println("Po³¹czenie nawi¹zane.");
			synchronized (this) {
				try {
					if (stat.executeUpdate("USE " + dataBaseName) == 0)
						System.out.println("Baza danych zosta³a wybrana.");
				} catch (SQLException e) {
					System.out.println("Baza danych nie istnieje! Tworzenie bazy: ");
					// próba stworzenia bazy danych
					if (stat.executeUpdate("create Database " + dataBaseName) == 1) {
						System.out.println("Baza danych zosta³a utworzona.");
						if (stat.executeUpdate("USE " + dataBaseName) == 0)
							System.out.println("Baza danych zosta³a wybrana.");
						createTables();
						// insertData("insert.txt");
					}
				}
			}

		} catch (SQLException e) {
			System.err.println("Wyst¹pi³ problem podczas nawi¹zywania po³¹czenia z baz¹ danych.");
			e.printStackTrace();
		}
	}

	public synchronized void executeStatement(String query) throws RemoteException {
		try {
			// tworzenie tabeli jeœli nie zosta³a utworzona
			stat.execute(query);
		} catch (SQLException e) {
			System.err.println("Nie uda³o siê utworzyæ struktur bazy danych lub ich zaktualizowaæ.");
			e.printStackTrace();
		}
	}

	public synchronized ResultSet executeQuery(String query) throws RemoteException {
		ResultSet results = null;
		try {
			// tworzenie tabeli jeœli nie zosta³a utworzona
			results = stat.executeQuery(query);
		} catch (SQLException e) {
			System.err.println("Nie uda³o siê wykonaæ zapytania.");
			e.printStackTrace();
		}
		return results;
	}

	synchronized public boolean createTables() throws RemoteException {
		String createUser = "CREATE TABLE IF NOT EXISTS remoteGallery.user (email varchar(50) NOT NULL, password varchar(30) NOT NULL, name varchar(30), surname varchar(30), PRIMARY KEY(email))";
		String createImage = "CREATE TABLE IF NOT EXISTS remoteGallery.image (imageId int(10) unsigned NOT NULL auto_increment, imageTitle varchar(45) NOT NULL, imageData mediumblob NOT NULL, latitude double NOT NULL, longitude double NOT NULL, email varchar(50) NOT NULL,place varchar(45) NOT NULL, PRIMARY KEY  (imageId), FOREIGN KEY (email) REFERENCES user(email) ON UPDATE CASCADE ON DELETE CASCADE);";
		String createRemindedPassword = "CREATE TABLE IF NOT EXISTS remoteGallery.remindedPassword (email varchar(50) NOT NULL, date datetime NOT NULL, password varchar(30) NOT NULL, FOREIGN KEY (email) REFERENCES user(email) ON UPDATE CASCADE ON DELETE CASCADE);";
		// tworzenie tabel jeœli nie zosta³y utworzone
		executeStatement(createUser);
		executeStatement(createImage);
		executeStatement(createRemindedPassword);
		return true;
	}

	// funkcja automatycznie ladujaca dane do tabel z pliku tekstowego
	// synchronized private void insertData(String fileName) {
	// String insert;
	//
	// File file = new File(fileName);
	// Scanner in = null;
	// try {
	// in = new Scanner(file);
	// } catch (FileNotFoundException e) {
	// System.out.println("Problem z za³adowaniem pliku z danymi dla bazy
	// danych.");
	// e.printStackTrace();
	// }
	// while (in.hasNext()) {
	// insert = in.nextLine();
	// System.out.println(insert);
	// try {
	// stat.execute(insert);
	// System.out.println("Wstawiono dane do tabel.");
	// } catch (SQLException e) {
	// System.err.println("Nie uda³o siê wstawiæ danych do bazy.");
	// e.printStackTrace();
	// }
	// }
	// in.close();
	// }

	// zwraca haslo jesli uzytkownik jest w bazie
	synchronized public String checkingIsEmailBusy(String email) throws RemoteException {
		String checkingIsEmailBusy = "SELECT email, password FROM user WHERE email='" + email + "'";
		ResultSet results = executeQuery(checkingIsEmailBusy);

		try {
			if (results.next()) {
				// upewnienie sie co do wielkosci liter w mailu
				if (results.getString(1).equals(email)) {
					String userPassword = results.getString(2);
					results.close();
					return userPassword;
				} else {
					results.close();
					return null;
				}
			} else {
				results.close();
				return null;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	synchronized public boolean checkingIsOnlyEmailBusy(String email) throws RemoteException {
		String checkingIsEmailBusy = "SELECT email FROM user WHERE email='" + email + "'";
		ResultSet results = executeQuery(checkingIsEmailBusy);
		try {
			if (results.next()) {
				results.close();
				return true;
			} else {
				results.close();
				return false;
			}

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}

	synchronized public String selectName(String email) throws RemoteException {
		String selectUser = "SELECT name FROM user WHERE email='" + email + "'";
		ResultSet results = executeQuery(selectUser);
		String name = null;
		try {
			if (results.next()) {
				// upewnienie sie czy zgadza sie wielkosc liter
				name = results.getString(1);
				results.close();
				return name;
			}
			results.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}

	synchronized public String selectSurname(String email) throws RemoteException {
		String selectUser = "SELECT surname FROM user WHERE email='" + email + "'";
		ResultSet results = executeQuery(selectUser);
		String surname = null;
		try {
			if (results.next()) {
				// upewnienie sie czy zgadza sie wielkosc liter
				surname = results.getString(1);
				results.close();
				return surname;
			}
			results.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return surname;
	}

	synchronized public void insertNewUser(String email, String password) throws RemoteException {
		String addingNewUser = "INSERT INTO user (email, password) VALUES ('" + email + "','" + password + "')";
		executeStatement(addingNewUser);
	}

	synchronized public void deleteUser(String email) throws RemoteException {
		String deletingUserImages = "DELETE FROM image WHERE email='" + email + "';";
		String deletingRemindedPsswords = "DELETE FROM remindedpassword WHERE email='" + email + "';";
		String deletingUser = "DELETE FROM user WHERE email='" + email + "';";
		executeStatement(deletingUserImages);
		executeStatement(deletingRemindedPsswords);
		executeStatement(deletingUser);
	}

	synchronized public void updateUser(String email, String previousEmail, String name, String surname)
			throws RemoteException {
		String updateImage = "UPDATE image SET email = '" + email + "' WHERE email='" + previousEmail + "'";
		String updateRemindedPassword = "UPDATE remindedpassword SET email = '" + email + "' WHERE email='"
				+ previousEmail + "'";
		String updateUser = "UPDATE user SET email = '" + email + "', name = '" + name + "', surname= '" + surname
				+ "' WHERE email='" + previousEmail + "'";
		executeStatement(updateUser);
		executeStatement(updateImage);
		executeStatement(updateRemindedPassword);
	}

	synchronized public void updatePassword(String email, String pasword) throws RemoteException {
		String updatePassword = "UPDATE user SET password = '" + pasword + "' WHERE email='" + email + "'";
		executeStatement(updatePassword);
	}

	synchronized public void insertRemindedPassword(String email, String userPassword) throws RemoteException {
		String insertAnswer = "INSERT INTO remoteGallery.remindedPassword (email, date, password) VALUES ('" + email
				+ "', NOW(),'" + userPassword + "')";
		executeStatement(insertAnswer);
	}

	synchronized public ArrayList<String> choosingPlaces(String email) throws RemoteException {
		ArrayList<String> list = new ArrayList<String>();
		String choosingPlaces = "SELECT DISTINCT place FROM image WHERE email='" + email + "'";
		ResultSet results = executeQuery(choosingPlaces);
		try {
			while (results.next()) {
				list.add(results.getString(1));
			}
			results.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	 synchronized public ArrayList <byte[]> choosingPictures(String
	 choosedPlace, String email)
	 throws RemoteException {
	 ArrayList <byte[]> list= new ArrayList <>();
	 String choosingPictures = "SELECT imageData FROM image WHERE place='" +
	 choosedPlace
	 + "' AND email='" + email + "'";
	 ResultSet results = executeQuery(choosingPictures);
	 try {
	 while (results.next()) {
	 list.add(results.getBinaryStream(1).toString().getBytes());
	 }
	 results.close();
	 } catch (SQLException e) {
	 // TODO Auto-generated catch block
	 e.printStackTrace();
	 }
	 return list;
	 }

	synchronized public int numberOfAddedPictures(String email) throws RemoteException {
		String numberOfAddedPictures = "SELECT COUNT(*) FROM image WHERE email = '" + email + "';";
		int numberOfPictures = 0;
		ResultSet results = executeQuery(numberOfAddedPictures);
		try {
			if (results.next()) {
				numberOfPictures = results.getInt(1);
				results.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return numberOfPictures;
	}

	synchronized public int countChoosingPictures(String choosedPlace, String email) throws RemoteException {
		String countChoosingPictures = "SELECT COUNT(*) FROM image WHERE place='" + choosedPlace + "' AND email='"
				+ email + "'";
		int numberOfPictures = 0;
		ResultSet results = executeQuery(countChoosingPictures);
		try {
			if (results.next()) {
				numberOfPictures = results.getInt(1);
				results.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return numberOfPictures;
	}

	// sprawdzenie czy w przeciagu godziny nie zostalo juz wyslane przypomnienie
	synchronized public boolean checkingRemindedPassword(String email) throws RemoteException {
		String checkingRemindedPassword = "SELECT date FROM remindedPassword WHERE email='" + email
				+ "' AND date >= now()-INTERVAL 1 HOUR";
		ResultSet results = executeQuery(checkingRemindedPassword);
		try {
			if (results.next()) {
				results.close();
				return true;

			} else {
				results.close();
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	public void close() throws RemoteException {
		try {
			conn.close();
			System.out.println("Zamkniêto po³¹czenie.");
		} catch (SQLException e) {
			System.err.println("Wyst¹pi³ problem podczas zamykania po³¹czenia z baz¹ danych.");
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------

	public void sendMessage(String email, String userPassword) throws RemoteException {
		String host = "smtp.gmail.com";
		int port = 465;
		// Adres email osby która wysy³a maila
		String from = "remotegallery@gmail.com";
		// Has³o konta osoby wysylaj¹cej email
		String password = "remote11";

		// Adres email osoby, gdzie wysy³any jest email
		String to = email;
		// String to = "damian1995nowakowski@gmail.com";

		String messageSubject = "Przypomnienie has³a";
		// Treœæ wiadomoœci
		String messageContent = "Szanowny u¿ytkowniku,\nWiadomoœæ zosta³a wygenerowana automatycznie przez system.\n\n"
				+ "Twoje has³o to: " + userPassword
				+ "\nDla bezpieczeñstwa zalecamy zmianê has³a po zalogowaniu.\n\nPozdrawiamy,\nZespó³ Remote Gallery!\n\n"
				+ "Jeœli nie u¿ywa³eœ mechanizmu przypominania has³a w aplikacji Remote Gallery, przepraszamy!";

		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtps.auth", "true");
		// Zainicjowanie sesji
		Session mailSession = Session.getDefaultInstance(props);
		// mailSession.setDebug(true);

		// Tworzenie wiadomoœci email
		MimeMessage message = new MimeMessage(mailSession);

		try {
			message.setSubject(messageSubject);
			message.setContent(messageContent, "text/plain; charset=ISO-8859-2");
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			Transport transport = mailSession.getTransport();
			transport.connect(host, port, from, password);
			// wys³anie wiadomoœci
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			transport.close();

		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean verifyMail(String email) throws RemoteException {
		boolean isMailCorrect = false;
		int j = 0;

		// mail nie moze byc pusty
		if (email.length() == 0) {
			return isMailCorrect;
		}

		if (email.charAt(0) != '@' && email.charAt(0) != '.' && email.charAt(0) != '_' && email.charAt(0) != '-'
				&& email.charAt(0) > '9' || email.charAt(0) < '0') {
			for (int i = 1; i < email.length(); i++) {
				if (email.charAt(i) == '@') {
					j = i;
					break;
				}
			}

			if (j != 0) {
				for (j++; j < email.length(); j++) {

					if (!(email.charAt(j) >= 'a' && email.charAt(j) <= 'z'
							|| email.charAt(j) >= 'A' && email.charAt(j) <= 'Z'
							|| email.charAt(j) >= '0' && email.charAt(j) <= '9' || email.charAt(j) <= '.')) {
						isMailCorrect = false;
						break;
					}
					// cyfra po . np ktos@gmail.c2m
					if (isMailCorrect && email.charAt(j) >= '0' && email.charAt(j) <= '9') {
						isMailCorrect = false;
						break;
					}

					if (email.charAt(j - 2) != '.' && email.charAt(j - 1) == '.' && email.charAt(j) != '.') {
						if (!isMailCorrect)
							// cyfra zaraz po kropce
							if (email.charAt(j) >= '0' && email.charAt(j) <= '9') {
							isMailCorrect = false;
							break;
							} else
							isMailCorrect = true;
					}
				}
				if (isMailCorrect && email.charAt(email.length() - 1) == '.')
					isMailCorrect = false;
			}
		}
		return isMailCorrect;
	}

	public boolean verifyName(String name) throws RemoteException {
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) >= 'a' && name.charAt(i) <= 'z' || name.charAt(i) >= 'A' && name.charAt(i) <= 'Z') {

			} else {
				return false;
			}

		}
		return true;
	}

	synchronized public ArrayList<Double> findingAllMarkersX() throws RemoteException {
		String findMarkers = "SELECT * FROM image";
		ArrayList<Double> list = new ArrayList<Double>();
		ResultSet resultSet = null;
		try {
			resultSet = executeQuery(findMarkers);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {

			while (resultSet.next()) {
				list.add(resultSet.getDouble("latitude"));
				// resultSet.getDouble("longitude")
			}
			// resultSet.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	synchronized public ArrayList<Double> findingAllMarkersY() throws RemoteException {
		String findMarkers = "SELECT * FROM image";
		ArrayList<Double> list = new ArrayList<Double>();
		ResultSet resultSet = null;
		try {
			resultSet = executeQuery(findMarkers);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {

			while (resultSet.next()) {
				list.add(resultSet.getDouble("longitude"));
			}
			// resultSet.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public ResultSet findingMarkerWithLL(double latitude, double longitude) {
		String selectUser = "SELECT * FROM image WHERE latitude=" + latitude + " AND longitude= " + longitude;
		ResultSet results = null;
		try {
			results = executeQuery(selectUser);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	public void addFile(String name, byte[] file, long imageLength, String latitude, String longitude, String email, String place) throws RemoteException {
		try {
			// Double.parseDouble(longitude.getText())
			// connection = getConnection();
			ByteArrayInputStream in = new ByteArrayInputStream(file);
			PreparedStatement statement = null;
			statement = conn.prepareStatement(
					"insert into image(imageTitle, imageData, latitude, longitude, email, place) " + "values(?,?,?,?,?,?)");
			statement.setString(1, name);
			statement.setBinaryStream(2, in, (int) (imageLength));
			statement.setDouble(3, Double.parseDouble(latitude));
			statement.setDouble(4, Double.parseDouble(longitude));
			statement.setString(5, email);
			statement.setString(6, place);
			statement.executeUpdate();
			
		}  catch (SQLException e) {
			System.out.println("SQLException: - " + e);
		} 
	}

}
