package Filesystem;
import java.util.Vector;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Klas implementujaca model drzewa w celu odtworzenia drzewiastej struktury plikow z klasy FilesystemElement
 * @author Mateusz
 *
 */
public class FilesystemTreeModel extends DefaultTreeSelectionModel implements TreeModel{
	private static final long serialVersionUID = -4501553366967487458L;
	private Vector listeners = new Vector();
	/**Obiekt z ktorego zostanie odtworzona struktura plikow*/
	private FilesystemElement filesystem;
	/**
	 * Konstruktor
	 * @param filesystem Obiekt klasy FIlesystemElement wskazujacy na konkretny katalog lub plik w systemie plikow
	 */
	public FilesystemTreeModel(FilesystemElement filesystem){
		this.filesystem = filesystem;
	}
	
	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);		
	}
	@Override
	public Object getChild(Object parent, int index) {
		return FilesystemElement.class.cast(parent).getChildren().get(index);
	}
	@Override
	public int getChildCount(Object parent) {
		FilesystemElement par = FilesystemElement.class.cast(parent);
		if(par.isFile()){
			return 0;
		}
		else{
			return par.getChildren().size();
		}
	}
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		FilesystemElement par = FilesystemElement.class.cast(parent);
		FilesystemElement chil = FilesystemElement.class.cast(child);
		for(int i = 0; i < par.getChildren().size()-1; ++i){
			if(chil.equals(par.getChildren().get(i))){
				return i;
			}
		}
		return -1;
	}
	@Override
	public Object getRoot() {
		// TODO Auto-generated method stub
		return filesystem;
	}
	@Override
	public boolean isLeaf(Object node) {
		// TODO Auto-generated method stub
		return (FilesystemElement.class.cast(node)).isFile();
	}
	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
		
	}
	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub
		
	}
	private FilesystemTreeModel(){}

}
