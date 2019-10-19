/**
 * Tree superclass
 * Implements Search Structure Interface
 * Implements search() and getDepth() methods
 * @author Jennifer Manke
 */

abstract public class Tree implements SearchStruct{
	Node root;
	int counter;
	
	public Tree() {
		root = null;
	}
	
	/**
	 *  Normal binary tree search
	 *  @param key  key to be searched for
	 */
	public boolean search(int key) {
		Node current = root;
		counter = 0;
		
		while(current != null) {
			counter++;
			if(current.key == key) {
				return true;
			}
			else if(current.key > key) {
				current = current.left;
			}
			else {
				current = current.right;
			}
		}
		System.out.println("Key not found");
		return false;
	}
	
	public int getCounter() {
		return counter;
	}

	/**
	 * Method for computing depth of tree recursively
	 */
	public int getDepth() {
		return Depth(root);
	}
		
	private int Depth(Node node) {
		if(node == null) {
			return -1;
		}
		else {
			return (1 + Math.max(Depth(node.left), Depth(node.right)));
		}
	}
	
	/**
	 * Method to show tree
	 * Presentation: starts with root, then always shows leftmost path until "End of Path" is reached
	 * @param current  call function with root of tree
	 */
	public void printTree(Node current) {
		if(current == null) {
			System.out.println("End of Path");
		}
		else if(current != null) {
			System.out.println("Key: " + current.key);
			printTree(current.left);
			printTree(current.right);
		}
		return;
	}
	
	abstract public void insert(int key);
	
	abstract public void delete(int key);
}