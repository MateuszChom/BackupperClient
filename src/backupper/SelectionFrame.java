package backupper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
/**
 * Klasa okna wyswietlajacego zaznaczone pliki
 * @author Mateusz
 *
 */
public class SelectionFrame {
	static JFrame frame;
	static JList left_list;
	static JList right_list;
	static DefaultListModel left_model;
	static DefaultListModel right_model;
	/**
	 * 
	 * @param leftList Nazwa lewego panelu
	 * @param rightList Nazwa prawego panelu
	 */
	SelectionFrame(String leftList, String rightList)
	{
		frame = new JFrame("Selected files");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLayout(new GridLayout());
		frame.setPreferredSize(new Dimension(400, 500));
		JPanel left_panel = new JPanel(new BorderLayout());	
		JPanel right_panel = new JPanel(new BorderLayout());	
		left_model = new DefaultListModel();
		right_model = new DefaultListModel();
		left_list = new JList(left_model);
		right_list = new JList(right_model);
		
		left_panel.add(new JLabel(leftList), BorderLayout.NORTH);
		right_panel.add(new JLabel(rightList), BorderLayout.PAGE_START);
		left_panel.add(left_list, BorderLayout.CENTER);
		right_panel.add(right_list, BorderLayout.CENTER);
		frame.add(left_list);
		frame.add(right_list);
		frame.pack();
		EventQueue.invokeLater(new Runnable() {public void run() {frame.setVisible(true);}});
	}
	/**
	 * Dodaje element do lewej listy
	 * @param str Nazwa elementu
	 */
	public static void addLeftListElement(String str){
		left_model.addElement(str);
	}
	/**
	 * Dodaje element do prawje listy
	 * @param str Nazwa elementu
	 */
	public static void addRightListElement(String str){
		right_model.addElement(str);
	}
	/**
	 * Usuwa element z lewej listy
	 * @param str Nazwa elementu do usuni�cia
	 */
	public static void removeLeftListElement(String str){
		left_model.removeElement(str);
	}
	/**
	 * Usuwa element z prawej listy
	 * @param str Nazwa elementu do usuni�cia
	 */
	public static void removeRightListElement(String str){
		right_model.removeElement(str);
	}
	/**
	 * Usuwa wszystkie pozycje z lewej listy
	 */
	public static void removeAllLeftListElement(){
		left_model.clear();
	}
	/**
	 * Usuwa wszystkie pozycje z prawej listy
	 */
	public static void removeAllRightListElement(){
		right_model.clear();
	}
}
