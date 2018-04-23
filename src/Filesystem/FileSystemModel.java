package Filesystem;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
/**
 * Klas implementujaca model drzewa w celu odtworzenia aktualnego stanu systemu plikow z danej sciezki.
 * @author Mateusz
 */
public class FileSystemModel implements TreeModel{
	private File root;
	private Vector listeners = new Vector();
	/**
	 * Konstruktor
	 * @param path sciezka z ktorej ma zostal odtworzony system plikow
	 */
	public FileSystemModel(String path)
	{
		
		root = new File(path);
		
	}
	
	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public Object getChild(Object parent, int index)
	{
		File directory = (File) parent;
	    String[] children = directory.list();
	    return new TreeFile(directory, children[index]);
	}

	@Override
	public int getChildCount(Object parent) {
		File parentFile = (File)parent;
		if(parentFile.isDirectory())
		{
			String fileList[] = parentFile.list();
			if(fileList != null) return fileList.length;
		}
		return 0;
	}

	@Override
	public boolean isLeaf(Object node) {
		File nodeFile = (File)node;
		return nodeFile.isFile();
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		File oldFile = (File) path.getLastPathComponent();
	    String fileParentPath = oldFile.getParent();
	    String newFileName = (String) newValue;
	    File targetFile = new File(fileParentPath, newFileName);
	    oldFile.renameTo(targetFile);
	    File parent = new File(fileParentPath);
	    int[] changedChildrenIndices = { getIndexOfChild(parent, targetFile) };
	    Object[] changedChildren = { targetFile };
	    fireTreeNodesChanged(path.getParentPath(), changedChildrenIndices, changedChildren);
	 
	  }
	 
	 private void fireTreeNodesChanged(TreePath parentPath, int[] indices, Object[] children) {
	   TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
	   Iterator iterator = listeners.iterator();
	   TreeModelListener listener = null;
	   while (iterator.hasNext()) {
	     listener = (TreeModelListener) iterator.next();
	     listener.treeNodesChanged(event);
	   }
	 }

	@Override
	public int getIndexOfChild(Object parent, Object child)
	{
		File directory = (File) parent;
	    File file = (File) child;
	    String[] children = directory.list();
	    for (int i = 0; i < children.length; i++) {
	      if (file.getName().equals(children[i])) {
	        return i;
	      }
	    }
	    return -1;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l)
	{
		listeners.add(l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l)
	{
		listeners.remove(l);
	}
	
	private class TreeFile extends File{
		private static final long serialVersionUID = 1L;
		
	    public TreeFile(File parent, String child)
	    {
	      super(parent, child);
	    }
	 
	    public String toString()
	    {
	      return getName();
	    }
	  }
}