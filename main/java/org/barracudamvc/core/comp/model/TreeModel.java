package org.barracudamvc.core.comp.model;


import java.util.List;

import org.barracudamvc.core.comp.Contextual;
import org.barracudamvc.core.comp.model.Model;


public interface TreeModel extends Model, Contextual {
	
	public TreeModel getRoot();
	public List<TreeModel> getChildren();
	public TreeModel getParent();
	public TreeModel getNextSibling();
	public TreeModel getPreviousSibling();
	public Object getItem();
	public String getId();
	public void resetModel();
	public boolean isRoot();
	public boolean isLeaf();
	public boolean isSelected();
	public int height();
	public int size();
	public int depth();

}
