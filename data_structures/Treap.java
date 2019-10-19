/** 
 * Implementation of Treaps based on pseudocode for optimized Treaps in "Raimund Seidel and Cecilia R Aragon. Randomized search trees. Algorithmica, 16(4-5):464–497, 1996".
 * Ranks are assigned to keys, so that resulting tree is binary-tree-ordered with respect to keys and max-heap-ordered with respect to ranks
 * Ranks are real-valued and chosen independently from a uniform distribution over a large set, so that the resulting probability for rank ties is small 
 * @author Jennifer Manke
 */

import java.util.Random;

public class Treap extends Tree{
	/** 
	 * Method for inserting a new key recursively
	 * i)   Find right place to insert new node
	 * ii)  Insert new node with corresponding key
	 * iii) Rotate nodes until max-heap-property for ranks is restored
	 * @param key  key to be inserted
	 */
	public void insert(int key) {
		root = insertHelp(key,root);
	}
	
	private Node insertHelp(int key, Node node) {
		// Insert new node
		if(node == null) {
			Random random = new Random();
			float rank = (float) random.nextDouble();
			Node newNode = new Node(key,rank);
			return newNode;
		}
		
		// Find right place and restore Treap-property
		else if(key < node.key) {
			node.left = insertHelp(key,node.left);
			if(node.left.rank > node.rank) {
				node = rightRot(node);
			}
		}
		else {
			node.right = insertHelp(key,node.right);
			if(node.right.rank > node.rank) {
				node = leftRot(node);
			}
		}
		return node;
	}
	
	// Right-rotation
	private Node rightRot(Node node) {
		Node temp;
		temp = node.left;
		node.left = temp.right;
		temp.right = node;
		return temp;
	}
	
	// Left-rotation
	private Node leftRot(Node node) {	
		Node temp;
		temp = node.right;
		node.right = temp.left;
		temp.left = node;
		return temp;
	}

	/**
	 * Method to delete a certain given key
	 * Condition: Element to be deleted must be present in the tree
	 * i)  Search for key 
	 * ii) Delete node depending on number of children
	 * @param key  key to be deleted
	 */
	public void delete(int key) {
		root = deleteHelp(key,root);
	}
	
	// Search
	private Node deleteHelp(int key, Node node) {
		if(key < node.key) {
			node.left = deleteHelp(key, node.left);
		}
		else if(key > node.key) {
			node.right = deleteHelp(key, node.right);
		}
		else {
			node = deleteRoot(node);
		}
		return node;
	}
	
	// Delete node
	private Node deleteRoot(Node node) {
		// 0 or 1 child: replace node through child
		if(node.left == null) {
			return node.right;
		}
		else if(node.right == null){
			return node.left;
		}
		
		// 2 children: rotate node down until it becomes a leaf, then delete it
		else if(node.left.rank > node.right.rank) {
			node = rightRot(node);
			node.right = deleteRoot(node.right);
		}
		else {
			node = leftRot(node);
			node.left = deleteRoot(node.left);
		}
		return node;
	}
}