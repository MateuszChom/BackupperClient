package backupper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;

import Connection.ConnectionHandler;
import Connection.RequestedCommand;
import Filesystem.FileSystemModel;
import Filesystem.FileTreePanel;
import Filesystem.FilesystemElement;
import Filesystem.FilesystemTreePanel;
/**
 * Klasa odpowiadajaca za glowny wyglad interfejsu dla uzytkownika + obslugujaca wszelakie zdarzenia w nim
 * @author Mateusz
 *
 */
public class Interface
{

	private String localFileTreePath = "C:\\Backup_recived";
	
	private JFrame window;
	private Socket socket;
	private FileTreePanel leftFileTreePanel;
	private FilesystemTreePanel rightFileTreePanel;
	ConnectionHandler connection;
	
	JPanel rightPanel;
	JPanel leftPanel;
	
	private boolean busy;
		
	public Interface(ConnectionHandler connection)
	{
		localFileTreePath = Config.getLocalFilePath();
		busy = false;
		this.connection = connection;	
	}
	/**
	 * Inicjacja interfejsu
	 */
	public void launchWindow()
	{
		window = new JFrame("BACKUPPER");
		leftFileTreePanel = new FileTreePanel(new FileSystemModel(localFileTreePath));
		
		FilesystemElement fe = null;
		try {
			connection.writeObjectToSocket(RequestedCommand.GET_FILE_TREE);
			fe = (FilesystemElement)connection.getObjectFromSocket();
			System.out.println("Recived FileTreePanel");
		} 
		catch (IOException | ClassNotFoundException e) {
			ErrorMessage.show("Couldn't update file list");
		}
		
		rightFileTreePanel = new FilesystemTreePanel(fe);
				
		leftPanel = new JPanel();
		rightPanel = new JPanel();
		JPanel rightButtonPanel = new JPanel();
		
		JButton uploadButton = new JButton("UPLOAD");
		JButton downloadButton = new JButton("DOWNLOAD");
		JButton deleteButton = new JButton("DELETE");

		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(leftFileTreePanel, BorderLayout.CENTER);
		JPanel tmpPanel1 = new JPanel(new BorderLayout());
		tmpPanel1.add(uploadButton, BorderLayout.CENTER);
		leftPanel.add(tmpPanel1, BorderLayout.SOUTH);
		
		rightButtonPanel.setLayout(new BoxLayout(rightButtonPanel, BoxLayout.X_AXIS));
		JPanel tmpPanel2 = new JPanel(new BorderLayout());
		tmpPanel2.add(downloadButton, BorderLayout.CENTER);
		rightButtonPanel.add(tmpPanel2);
		JPanel tmpPanel3 = new JPanel(new BorderLayout());
		tmpPanel3.add(deleteButton, BorderLayout.CENTER);
		rightButtonPanel.add(tmpPanel3);
		
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(rightFileTreePanel, BorderLayout.CENTER);
		rightPanel.add(rightButtonPanel, BorderLayout.SOUTH);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, leftPanel, rightPanel);
		
		window.add(splitPane);
		
		window.pack();
		
		leftPanel.addComponentListener(new ComponentAdapter() {public void componentResized(ComponentEvent ce) {leftFileTreePanel.changeSize(new Dimension((int)ce.getComponent().getSize().getWidth(), (int)(ce.getComponent().getSize().getHeight() - uploadButton.getHeight())));}});
		rightPanel.addComponentListener(new ComponentAdapter() {public void componentResized(ComponentEvent ce) {rightFileTreePanel.changeSize(new Dimension((int)ce.getComponent().getSize().getWidth(), (int)(ce.getComponent().getSize().getHeight() - rightButtonPanel.getHeight())));}});
		
		window.addWindowListener(new WindowAdapter() {public void windowClosing(WindowEvent we) {System.exit(1);}});
		
		uploadButton.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ae) {uploadToServer();}});
		downloadButton.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ae) {downloadFromServer();}});
		deleteButton.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent ae) {deleteFromServer();}});
		
		EventQueue.invokeLater(new Runnable() {public void run() {window.setVisible(true);}});
		
		//Selection panel
		SelectionFrame sframe = new SelectionFrame("To upload", "To download");
	}
	/**
	 * Klasa sluzaca do wysylki pliczkow do serwera
	 */
	private void uploadToServer()
	{
		if(!busy)
		{
			busy = true;
			
			Vector<String> toUpload = leftFileTreePanel.getSelectedFiles();
			leftFileTreePanel.clearSelectedFilesVector();
			if(toUpload.isEmpty()) ErrorMessage.show(new String("Brak zaznaczonych plikow, zaznacz i powtorz operacje!"));

			FilesystemElement right_tree_root = rightFileTreePanel.getFilesystemElementRoot();
			
			Thread uploadingThread = new Thread() {public void run()
			{
				leftFileTreePanel.clearSelectedFilesVector();
				ProcessingFrame processingFrame= new ProcessingFrame(toUpload.size());
				
				for(String filepath : toUpload){
					//Sprawdzenie czy istnieje nowsza wersja pliku na serwerze
					FilesystemElement temp = right_tree_root.getFile(new File(filepath).getName());
					if(temp != null && temp.getLastModified() >= new File(filepath).lastModified()){
						ErrorMessage.show("Newer or the same file " + filepath.toString() + " is already on server.");
						System.out.println("Nowszy plik zostal wyslany.");
						processingFrame.increment();
					}
					else{
						System.out.println("Wyslano starszy plik.");
						String response = null;
						try {
							connection.writeObjectToSocket(RequestedCommand.PUSH_FILE);
							response = (String)connection.getObjectFromSocket();
							if(response.equals("READY")){
								Path path = Paths.get(filepath);
								String filename = path.getFileName().toString();
								connection.writeObjectToSocket(filename);
								connection.writeFileToSocket(filepath);
								
							}
							processingFrame.increment();
						} catch (Exception e) {
							ErrorMessage.show("Connection error. Restart application.");
							processingFrame.increment();
						}

					}
					
				}
				try {
					System.out.println("!!!");
					connection.writeObjectToSocket(RequestedCommand.GET_FILE_TREE);
					FilesystemElement fe = (FilesystemElement)connection.getObjectFromSocket();
					Dimension rightFileTreePanelSize = rightFileTreePanel.getSize();
					rightPanel.remove(rightFileTreePanel);
					rightFileTreePanel = new FilesystemTreePanel(fe);
					rightFileTreePanel.changeSize(rightFileTreePanelSize);
					rightPanel.add(rightFileTreePanel);
					window.pack();

				} catch (IOException | ClassNotFoundException e) {
					ErrorMessage.show("Couldn't update file list");
				}
				
				
				
				busy = false;
			}};
			uploadingThread.setName("uploadingThread");
			uploadingThread.start();
		}
		else
		{
			ErrorMessage.show(new String("Poprzednia operacja nie zostala ukonczona prosze czekac..."));
		}
	}
	/**
	 * Klasa za pomoca ktorej pobieramy pliki z serwera
	 */
	private void downloadFromServer()
	{
		if(!busy)
		{
			busy = true;
			
			Vector<String> toDownload = rightFileTreePanel.getSelectedFiles();////
			rightFileTreePanel.clearSelectedFilesVector();
			ProcessingFrame processingFrame= new ProcessingFrame(toDownload.size());
			
			System.out.println("Do wyslania jest: ");
			System.out.println(toDownload.size());
			
			if(toDownload.isEmpty()) ErrorMessage.show(new String("Brak zaznaczonych plikow, zaznacz i powtorz operacje!"));
				//ProcessingFrame processingFrame= new ProcessingFrame();
				
				Thread downloadingThread = new Thread() {public void run()
				{
					System.out.println("Got files selected to download");
					for(String filepath : toDownload){
						
						String response = null;
						try {
							connection.writeObjectToSocket(RequestedCommand.GET_FILE);
							response = (String)connection.getObjectFromSocket();
							if(response.equals("READY")){
								connection.writeObjectToSocket(filepath);
								Path path = Paths.get(filepath);
								String filename = path.getFileName().toString();
								System.out.println(filepath);
								System.out.println(filename);
								connection.getFileFromSocket(filename);
								System.out.println("getFilefromsoc");
							}
							processingFrame.increment();
						} catch (Exception e) {
							ErrorMessage.show("Connection error. Restart application.");
							processingFrame.increment();
						}
						
						Dimension leftFileTreePanelSize = leftFileTreePanel.getSize();
						
						leftPanel.remove(leftFileTreePanel);
						leftFileTreePanel = new FileTreePanel(new FileSystemModel(localFileTreePath));
						leftFileTreePanel.changeSize(leftFileTreePanelSize);
						leftPanel.add(leftFileTreePanel);
						window.pack();
						
						
						
					}
					busy = false;
				}};
				downloadingThread.setName("downloadingThread");
				downloadingThread.start();
			
		}
		else
		{
			ErrorMessage.show(new String("Poprzednia operacja nie zostala ukonczona prosze czekac..."));
		}
	}
	/**
	 * Klasa ktora usuwa pliki z serwera
	 */
	private void deleteFromServer()
	{
		if(!busy)
		{
			busy = true;
			Vector<String> toDelete = rightFileTreePanel.getSelectedFiles();
			rightFileTreePanel.clearSelectedFilesVector();
			ProcessingFrame processingFrame= new ProcessingFrame(toDelete.size());
			if(toDelete.isEmpty()) ErrorMessage.show(new String("Brak zaznaczonych plikow, zaznacz i powtorz operacje!"));

			
			Thread deletingThread = new Thread() {public void run()
			{
				for(String filepath : toDelete){
					String response = null;
					try {
						connection.writeObjectToSocket(RequestedCommand.DELETE_FILE);
						response = (String)connection.getObjectFromSocket();
						if(response.equals("READY")){
							connection.writeObjectToSocket(filepath);
						}
						processingFrame.increment();
					} catch (Exception e) {
						ErrorMessage.show("Connection error. Restart application.");
						processingFrame.increment();
					}
				
				}

				try {
					connection.writeObjectToSocket(RequestedCommand.GET_FILE_TREE);
					FilesystemElement fe = (FilesystemElement)connection.getObjectFromSocket();
					Dimension rightFileTreePanelSize = rightFileTreePanel.getSize();
					rightPanel.remove(rightFileTreePanel);
					rightFileTreePanel = new FilesystemTreePanel(fe);
					rightFileTreePanel.changeSize(rightFileTreePanelSize);
					rightPanel.add(rightFileTreePanel);
					window.pack();
				} catch (IOException | ClassNotFoundException e) {
					ErrorMessage.show("Couldn't update file list");
				}
				
				busy = false;
			}};
			deletingThread.setName("downloadingThread");
			deletingThread.start();
		}
		else
		{
			ErrorMessage.show(new String("Poprzednia operacja nie zostala ukonczona prosze czekac..."));
		}
	}
}
