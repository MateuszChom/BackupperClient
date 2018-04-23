package Filesystem;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import backupper.SelectionFrame;
/**
 * Klasa sluzaca do wyswietlania panelu ze struktura systemu plikow w formie drzewa.
 * Jest tworzona na podstawie obecnej struktury plik�w.
 * @author Mateusz
 */
public class FileTreePanel extends JPanel implements MouseListener{
	/**Panel na ktorym wyswietlana jest struktura systemu plikow*/
	private JTree fileTree;
	/**TreeModel*/
	private FileSystemModel fileSystemModel;
	/**JScrollPane*/
	private JScrollPane scrollPane;
	/**Wektor przechowujacy Zaznaczone elementy*/
	private Vector<String> selectedFiles;
	
	/**
	 * Konstruktor
	 * @param fs Obiekt klasy FileTreeModel wskazujacy na konkretny katalog lub plik w systemie plikow
	 */
	public FileTreePanel(FileSystemModel fsm)
	{
		super();
		fileSystemModel = fsm;
		fileTree = new JTree(fileSystemModel);
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
	 *Czysciciel wektora zazn plikow (odznacza wszystkie elementy)
	 */
	public void clearSelectedFilesVector()
	{
		selectedFiles.clear();
		SelectionFrame.removeAllLeftListElement();
	}
	/**
	 * Obsluga zdarzen myszy. Tutaj obsluga zaznaczenia elementow systemu plikow. 
	 */
	@Override
	public void mouseClicked(MouseEvent e)
	{
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			int selRow = fileTree.getRowForLocation(e.getX(), e.getY());
			
			if(selRow > -1)
			{
				fileTree.setSelectionRow(selRow);
	
				File tmp = (File)(fileTree.getLastSelectedPathComponent());
				String tmpPath = tmp.getAbsolutePath();
				
				
				if(selectedFiles.contains(tmpPath)) {
					selectedFiles.removeElement(tmpPath);
					SelectionFrame.removeLeftListElement(tmpPath);
				}
				else {
					selectedFiles.addElement(tmpPath);
					SelectionFrame.addLeftListElement(tmpPath);
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
