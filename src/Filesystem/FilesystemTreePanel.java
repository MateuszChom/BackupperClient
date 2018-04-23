package Filesystem;


import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import backupper.SelectionFrame;
/**
 * Klasa ktora sluzy do wyswietlania panelu ze struktur systemu plikow w formie drzewa.
 * Jest tworzona na podstawie migawki systemu plikow z klasy FilesystemElement
 * @author Mateusz
 *
 */
public class FilesystemTreePanel extends JPanel implements MouseListener, Serializable
{
	private static final long serialVersionUID = 2L;
	/**Panel na ktorym wyswietlana jest struktura systemu plikow*/
	private JTree fileTree;
	/**TreeModel*/
	private FilesystemTreeModel treeModel;
	/**JScrollPane*/
	private JScrollPane scrollPane;
	/**Wektor przechowujacy Zaznaczone elementy*/
	private Vector<String> selectedFiles;
	
	/**
	 * Konstruktor
	 * @param fs Obiekt klasy FilesystemElement wskazujacy na konkretny katalog lub plik w systemie plikow
	 */
	public FilesystemTreePanel(FilesystemElement fs)
	{
		super();
		treeModel = new FilesystemTreeModel(fs);
		fileTree = new JTree(treeModel);
		scrollPane = new JScrollPane(fileTree);
		add(scrollPane);
		
		selectedFiles = new Vector<String>();
		
		fileTree.addMouseListener(this);
	}
	/**
	 * 
	 * @return Zwraca panel z drzewem systemu plikow
	 */
	public JTree getFileTree()
	{
		return fileTree;
	}
	public FilesystemElement getFilesystemElementRoot(){
		return (FilesystemElement) treeModel.getRoot();
	}
	/**
	 * Zmiana rozmiaru okna
	 * @param newSize Rozmiar okna
	 */
	public void changeSize(Dimension newSize)
	{
		setPreferredSize(newSize);
		scrollPane.setPreferredSize(newSize);
	}
	/**
	 * 
	 * @return Zwraca wektor zaznaczonych plikow
	 */
	public Vector<String> getSelectedFiles()
	{
		return (Vector<String>)selectedFiles.clone();
	}
	/**
	 * 
         * Czysci wektor zazn plikow(odznacza wszystkie elementy)
	 */
	public void clearSelectedFilesVector()
	{
		selectedFiles.clear();
		SelectionFrame.removeAllRightListElement();
	}
	/**
	 * Obsluga zdarzen myszy. Tutaj obsluga zaznaczenia elementow systemu plikow. 
	 */
	@Override
	public void mouseClicked(MouseEvent e)
	{
		
		System.out.println("Rozmiar wektora do przekazania");
		System.out.println(selectedFiles.size());
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			int selRow = fileTree.getRowForLocation(e.getX(), e.getY());
			if(selRow > -1)
			{
				fileTree.setSelectionRow(selRow);
				FilesystemElement tmp = (FilesystemElement)(fileTree.getLastSelectedPathComponent());
				String tmpPath = tmp.getFullPath();
				
				if(selectedFiles.contains(tmpPath)){
					selectedFiles.removeElement(tmpPath);
					SelectionFrame.removeRightListElement(tmpPath);
				}
				else {
					selectedFiles.addElement(tmpPath);
					SelectionFrame.addRightListElement(tmpPath);
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
