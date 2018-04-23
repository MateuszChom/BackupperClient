package backupper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import Connection.ConnectionHandler;
/**
 * Klasa zaczynajaca polaczenie i odpowiada za interfejs uzyszkownika
 * @author Mateusz
 *
 */
public class Backupper extends Thread
{
	private Interface backupperInterface;
	private static String serverAddress;
	private static int port;
	private static String USERNAME;
	private static String localPath;
	/**
	 * Konstruktor inicjujacy pola
	 */
	private Backupper(){
		localPath = Config.getLocalFilePath();
		serverAddress = Config.getServerAdress();
		port = Config.getPort();
		USERNAME = Config.getUsername();
	}
	/**
	 * Inicjalizacja klienta i wyslanie nazwy uzytkownika serwerowi.
	 * @param args
	 */
	public static void main(String[] args)
	{
		Backupper backupper = new Backupper();
		ConnectionHandler connection = null;
		
		Socket socket = null;
		
		while(socket == null){
			try {
				socket = new Socket(serverAddress, port);
				System.out.println("Polaczono z serwerem.");
				
			} catch (Exception e) {
				System.out.println("Brak polaczenia z serwerem!");
			}
		}
		connection = new ConnectionHandler(socket, localPath);
		backupper.backupperInterface = new Interface(connection);
		try {
			connection.writeObjectToSocket(USERNAME);
			System.out.println("Nazwa uzytkownika wyslana.");
		} catch (IOException e) {
			System.out.println("Blad przesylania ciagu znakow!");
		}
		backupper.backupperInterface.launchWindow();
	}
	
}
