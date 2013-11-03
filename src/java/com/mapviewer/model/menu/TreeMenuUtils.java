package com.mapviewer.model.menu;

import com.mapviewer.exceptions.XMLFilesException;
import java.util.ArrayList;

/**
 * This class contains static methods for the manipulation of the user tree menu
 * @author Olmo Zavala Romero
 */
public class TreeMenuUtils {

	/**
	 * Copies a complete tree into a new tree. This is useful because
	 * if not the childs are passed by reference and all the users share
	 * the same menu options. 
	 * @param {TreeNode}currNode
	 * @return TreeNode which is the new tree
	 */
	public static TreeNode copyCurrentTree(TreeNode currNode){

		TreeNode newNode = new TreeNode(true, null, null, false);
		ArrayList<TreeNode> childs = null;
		MenuEntry menuEntry;

		newNode.setRoot(currNode.isRoot());
		newNode.setSelected(currNode.isSelected());
		newNode.setNode(currNode.getNode());

		if (currNode.getHasChilds()) {
			childs = new ArrayList<>();

			for (int i = 0; i < currNode.getChilds().size(); i++) {
				childs.add(copyCurrentTree(currNode.getChilds().get(i)));
			}

			newNode.setChilds(childs);
			newNode.setHasChilds(true);
		}

		return newNode;
	}

	/**
	 * This recursive method traverse the tree from a root node. 
	 * It also print the result in an easy to understand way
	 * @param {TreeNode} rootNode TreeNode Root node where the traverse occur
	 */
	public static void traverseTree(TreeNode rootNode) {
		if (!rootNode.isRoot()) {
			System.out.print(rootNode.getNode().getId());
		}

		if (rootNode.getChilds() != null) {
			System.out.print("{");
			for (int i = 0; i < rootNode.getChilds().size(); i++) {
				System.out.print(" -");
				TreeMenuUtils.traverseTree(rootNode.getChilds().get(i));
			}
			System.out.print("}");
		}
	}

	/**
	 * Obtains the array of the entries of the MenuEntry{} by the user based on the selected tree. 
	 * @param {TreeNode} rootNode 
	 * @return MenuEntry[]
	 */
	public static MenuEntry[] obtieneMenuSeleccionado(TreeNode rootNode) throws XMLFilesException {
		ArrayList<MenuEntry> selectedMenu = new ArrayList<>();
		TreeNode actualNode = rootNode;//copies the root node into a temp node

		//It is used to catch exceptions. This can happen when the menu that is being
		// searched on the current tree does not exists. 
		boolean found = true;
		while (actualNode.getChilds() != null && found) {//while the node has children
			found = false;
			for (int i = 0; i < actualNode.getChilds().size(); i++) {//cover all node children
				if (actualNode.getChilds().get(i).isSelected()) {//check to see if the node the is selected one
					selectedMenu.add(actualNode.getChilds().get(i).getNode());//We add this node to the list of MenuEntry final
					actualNode = actualNode.getChilds().get(i);//modify the node with its gildren
					found = true;
					break;//break if found the selected node
				}
			}
		}

		if(!found){
			throw new XMLFilesException("The selected menu does not exists, please "
					+ "refresh the website");
		}
		MenuEntry[] selecteMenuArray = new MenuEntry[selectedMenu.size()];
		return selectedMenu.toArray(selecteMenuArray);//make list into array and return. 
	}

	/**
	 *Obtains an array of entries of nodes that are selected but only of the first level. 
	 * @param {TreeNode} rootNode root node
	 * @return {Array{}} MenuEntry[] Array that contains the menu of the first level of the three
	 */
	public static MenuEntry[] obtieneNodosDePrimerNivel(TreeNode rootNode) {
		ArrayList<MenuEntry> selectedMenu = new ArrayList<>();
		TreeNode actualNode = rootNode;
		//Recorremos todos los nodos del primer nivel y los que esten seleccionados
		//se agregan al menu seleccionado
		for (int i = 0; i < actualNode.getChilds().size(); i++) {
			if (actualNode.getChilds().get(i).isSelected()) {
				selectedMenu.add(actualNode.getChilds().get(i).getNode());
			}
		}
		MenuEntry[] selecteMenuArray = new MenuEntry[selectedMenu.size()];
		return selectedMenu.toArray(selecteMenuArray);
	}

//	public static TreeNode updateVectorialMenu(String[] menus){

//	}
}
