/**
 * Iterative implementation of Zip-Trees, based on code in "Robert E Tarjan, Caleb C Levy, and Stephen Timmel. Zip trees. 
   In Workshop on Algorithms and Data Structures, pages 566–577. Springer, 2019".
 * @author Jennifer Manke
 * 
 * Conditions: elements for insertion must not already be in the tree
 *             elements to be deleted must be present in the tree
 */

public class ZipTree extends Tree{
	/**
	 * Method for sampling from geometric distribution with mean 1 (probability = 0.5)
	 */		
	public int geom_sample() {
		int result = 0;
		while(Math.random() < 0.5) {
			result ++;
		}
		return result;
	}
	
	/**
	 * Method for inserting new node with key
	 * i)  Find node to be replaced by new node depending on key and sampled rank
	 * ii) Unzipping of subtree
	 * @param key  key to be inserted
	 */
	public void insert(int key) {
		Node newNode;
		int rank = geom_sample();
		newNode = new Node(key,rank);
		
		/*
		 * current: for storing the node to be replaced
		 * previous: for storing the parent of current
		 */
		Node current = root;
		Node previous = null;
		
		// Find node to be replaced
		while((current != null) && (newNode.rank < current.rank || ((newNode.rank == current.rank) && (newNode.key > current.key)))) {
			previous = current;
			if(newNode.key < current.key) {
				current = current.left;
			}
			else {
				current = current.right;
			}
		}
		
		// Insertion of newNode
		if(current == root) {
			root = newNode;
		}
		else if(newNode.key < previous.key) {
			previous.left = newNode;
		}
		else {
			previous.right = newNode;
		}
		
		// Preserving replaced node
		if(current == null) {
			return; 
		}
		if(newNode.key < current.key) {
			newNode.right = current;
		}
		else {
			newNode.left = current;
		}
		previous = newNode;
		
		/*
		 * Unzip
		 * current: moves along search path, stops temporarily when element changes relation (smaller or greater) to inserted key
		 * previous: last element with different relation to inserted element than "current"
		 * temp: starts at inserted element, changes to "previous" whenever "previous" changes; place for "current" to be inserted when it stops moving
		 */
		Node temp = null;
		
		while(current != null) {
			temp = previous;
			
			if(current.key < newNode.key) {
				while((current != null) && (current.key < newNode.key)){
					previous = current;
					current = current.right;
				}
			}
			else {
				while((current != null) && (current.key > newNode.key)){
					previous = current;
					current = current.left;
				}
			}
			
			if((temp.key > newNode.key) || ((temp == newNode) && (previous.key > newNode.key))) {
				temp.left = current;
			}
			else {
				temp.right = current;
			}
		}
	}
	
	/**
	 * Method for deletion of certain key
	 * i)   Find key in tree
	 * ii)  Replace node with a child, depending on rank
	 * iii) Zipping of subtrees of deleted node
	 * @param key  key to be deleted
	 */
	public void delete(int key){
		/* 
		 * current: at first node to be deleted; then replacing node
		 * previous: parent of "current"
		 * left: left child of "current", first part for zipping
		 * right: right child of "current", second part for zipping
		 */
		Node current = root;
		Node previous = null;
		Node left;
		Node right;
		
		// Find key
		while(key != current.key) {
			previous = current;
			if(key < current.key) {
				current = current.left;
			}
			else {
				current = current.right;
			}
		}
		
		left = current.left;
		right = current.right;
		
		// 0 or 1 child: child becomes replacing node
		if(left == null) {
			current = right;
		}
		else if(right == null) {
			current = left;
		}
		
		// 2 children: child with higher rank (or lower key in case of a tie) becomes replacing node
		else if(left.rank >= right.rank) {
			current = left;
		}
		else {
			current = right;
		}
		
		// Replace node 
		if(root.key == key) {
			root = current;
		}
		else if(key < previous.key) {
			previous.left = current;
		}
		else {
			previous.right = current;
		}
		
		/*
		 * Zip
		 * previous: builds zipped path depending on rank of the elements
		 */
		while(left != null && right != null) {
			if(left.rank >= right.rank) {
				while(left != null && left.rank >= right.rank){
					previous = left;
					left = left.right;
				}
				previous.right = right;
			}
			else {
				while(right != null && left.rank < right.rank){
					previous = right;
					right = right.left;
				}
				previous.left = left;
			}
		}
	}
}