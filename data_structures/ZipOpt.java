/**
 * Our optimized Zip Tree implementation, based on ZipTree.java
 * Optimization: allowing equal ranks for left children as well as for right children -> more balanced tree
 * Result: faster insertion, search and deletion, as well as smaller depth
 * @author Jennifer Manke
 */

public class ZipOpt extends ZipTree{
	
	public void insert(int key) {
		Node newNode;
		int rank = geom_sample();
		newNode = new Node(key,rank);
		
		Node current = root;
		Node previous = null;
		
		/*
		 * Modification:
		 * Allowing rank ties for left children
		 */
		while((current != null) && (newNode.rank <= current.rank)){
			previous = current;
			if(newNode.key < current.key) {
				current = current.left;
			}
			else {
				current = current.right;
			}
		}
		
		if(current == root) {
			root = newNode;
		}
		else if(newNode.key < previous.key) {
			previous.left = newNode;
		}
		else {
			previous.right = newNode;
		}
		
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
}