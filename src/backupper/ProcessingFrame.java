package backupper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
/**
 * Klasa sluzy do wyswietlenia jak daleko jestesmy z wysylaniem pliku. 
 * @author Mateusz
 *
 */
public class ProcessingFrame
{
	private int counter = 1;
	private JFrame processingFrame;
	private JLabel counterLabel;
	
	private int numberOfFiles;
	/**
	 * Konstruktor
	 * @param numberOfFiles Ilosc plikow do przeslania
	 */
	ProcessingFrame(int numberOfFiles)
	{
		this.numberOfFiles = numberOfFiles;
		processingFrame = new JFrame("PROCESSING...");
		processingFrame.setSize(new Dimension(300, 400));
		processingFrame.setLayout(new BorderLayout());
		counterLabel = new JLabel("Processing file: " + counter + " of " + numberOfFiles);
		processingFrame.add(counterLabel);
		processingFrame.pack();
		if(numberOfFiles != 0) EventQueue.invokeLater(new Runnable() {public void run() {processingFrame.setVisible(true);}});
	}
	/**
	 * Inkrementuje liczbe postepowania przesylania
	 */
	public void increment()
	{
		if(counter >= numberOfFiles) processingFrame.dispose();
		counter++;
		counterLabel.setText("Processing file: " + counter + " of " + numberOfFiles);
	}
}
