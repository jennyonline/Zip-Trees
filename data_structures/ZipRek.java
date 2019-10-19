/**
 * Recursive implementation of Zip-Trees, based on code in "Robert E Tarjan, Caleb C Levy, and Stephen Timmel. Zip trees. 
   In Workshop on Algorithms and Data Structures, pages 566–577. Springer, 2019".
 * @author Jennifer Manke
 * 
 * Conditions: elements for insertion must not already be in the tree
 *             elements to be deleted must be present in the tree
 */

public class ZipRek extends Tree{
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
	 * Recursive method to insert new key
	 * @param key  key to be inserted
	 */
	public void insert(int key) {
		Node newNode;
		int rank = geom_sample();
		newNode = new Node(key,rank);
		// Setting new root
		root = insertHelp(newNode, root);
	}
	
	// Insertion with implicit unzipping
	private Node insertHelp(Node newNode, Node node) {
		if(node == null) {
			return newNode;
		}
		if(newNode.key < node.key) {
			if(insertHelp(newNode, node.left) == newNode) {
				if(newNode.rank < node.rank) {
					node.left = newNode;
				}
				else {
					node.left = newNode.right;
					newNode.right = node;
					return newNode;
				}
			}
		}
		else {
			if(insertHelp(newNode, node.right) == newNode) {
				if(newNode.rank <= node.rank) {
					node.right = newNode;
				}
				else {
					node.right = newNode.left;
					newNode.left = node;
					return newNode;
				}
			}
		}
		return node;
	}
	
	/**
	 * Recursive method to delete certain key
	 * @param key  key to be deleted
	 */
	public void delete(int key){
		deleteHelp(key, root);
	}
	
	private Node deleteHelp(int key, Node node) {
		// Only occurs if node to be deleted is the root itself
		if(key == node.key) {
			root = zip(node.left, node.right);
			return root;
		}
		// If key to be deleted is smaller -> continue with left subtree
		if(key < node.key) {
			// Test if next node to be visited is required key; this makes sure that the first if-statement is only true if the root is to be deleted
			if(key == node.left.key) {
				node.left = zip(node.left.left, node.left.right);
			}
			else {
				deleteHelp(key, node.left);
			}
		}
		else {
			if(key == node.right.key) {
				node.right = zip(node.right.left, node.right.right);
			}
			else {
				deleteHelp(key, node.right);
			}
		}
		return node;
	}
	
	// Process of Zipping
	// x has smaller key than y
	private Node zip(Node x, Node y) {
		if(x == null) {
			return y;
		}
		if(y == null) {
			return x;
		}
		if(x.rank < y.rank) {
			y.left = zip(x, y.left);
			return y;
		}
		else {
			x.right = zip(x.right, y);
			return x;
		}
	}
}