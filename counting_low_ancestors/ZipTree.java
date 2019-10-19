/**
 * Adapted iterative ZipTree version for counting low and high ancestors in a Zip-Tree.
 * Based on code in "Robert E Tarjan, Caleb C Levy, and Stephen Timmel. Zip trees. In Workshop on Algorithms and Data Structures, pages 566–577. Springer, 2019".
 * @author Jennifer Manke
 * 
 * In order to count the low/high ancestors, the search method has been adapted accordingly, storing the ancestors in "lowAncestor" and "highAncestor", which are attributes of the tree in this class.
 * "k" is also an attribute of the tree here, representing the rank until which the ancestors shall be counted (see Lemma 2 for details).
 * The creation of trees as well as the computation of the number of ancestors happen in the added main method.
 * The delete and insert methods have not been changed compared to the normal iterative version.
 * In order to achieve a more compact representation, neither a tree superclass nor a SearchStruct interface is implemented here, only focusing on the necessary parts.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class ZipTree{
Node root;
	
	public ZipTree() {
		root = null;
	}
	
	// Geometric distribution
	public int geom_sample() {
		int result = 0;
		while(Math.random() < 0.5) {
			result ++;
		}
		return result;
	}
	
	/*
	 *  Variables needed for counting low ancestors of a node x
	 *  lowAncestor/highAncestor: for counting number of low/high ancestors of x
	 *  k: k value from Lemma 2
	 */
	
	int lowAncestor = 0;
	int highAncestor = 0;
	int k = 1; 
		
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
	
	// Adapted for counting high and low ancestors
	public boolean search(int key) {
		Node current = root;
		// Start counting at 0 for every new search
		lowAncestor = 0;
		highAncestor = 0;
			
		while(current != null) {
			if(current.key == key) {
				return true;
			}
			
			// Count ancestors only if they are of appropriate rank
			else if(current.key < key && current.rank <= k) {
				current = current.right;
				lowAncestor ++;
			}
			
			// Tree needs to be traversed even if rank of nodes is greater than k, but these nodes must not be counted
			else if(current.key < key) {
				current = current.right;
				
			}
			
			// Analogous for high ancestor
			else if(current.key > key && current.rank <= k){
				highAncestor ++;
				current = current.left;
			}
			
			else{
				current = current.left;
			}
		}
		System.out.println("Key not found");
		return false;
	}
	
	// Method to create a random list without double elements efficiently
	public static ArrayList<Integer> shuffleDiff(int size) {
		ArrayList<Integer> list = new ArrayList<Integer>();
			
		for(int i=0; i<size; i++) {
			list.add(i);
		}
		Collections.shuffle(list);
		return list;
	}        
 
	public static void main(String[] args) {
		/*
		 * numbers: number of nodes in underlying tree
		 * repeat: times ancestors shall be counted
		 * lowAncestor/highAncestor: for computing average of number of low/high ancestors
		 * searchKey: first key to compute ancestors for
		 * numberKeys: how many times a new key shall be determined
		 */
		int numbers = 100000;
        int repeat = 1000;
		float lowAncestor = (float) 0.0;
		float highAncestor = (float) 0.0;
		int searchKey = 9000;
		int numberKeys = 9;
		
		// For computing the average over all keys
		float lowResult = (float) 0.0;
		float highResult = (float) 0.0;
				
		ZipTree tree = new ZipTree();
		
		for(int j=0; j<numberKeys; j++) {
			// For each new key, the counted ancestors must be reset
			lowAncestor = (float) 0.0;
			highAncestor = (float) 0.0;
			
			for(int i=0; i<repeat; i++) {
				// Creation of Zip-Tree
				tree = new ZipTree();
				Iterator<Integer> iterator;
				ArrayList<Integer> list = new ArrayList<Integer>();
				
				list = shuffleDiff(numbers);	
				
				iterator = list.iterator();
				while(iterator.hasNext()) {
					tree.insert(iterator.next());
				}
				
				// Searching for key and counting ancestors
				tree.search(searchKey);
				lowAncestor += tree.lowAncestor;
				highAncestor += tree.highAncestor;
			}
			
			// Compute average for one key
			lowResult += lowAncestor/(float) repeat;
			highResult += highAncestor/(float) repeat;
			searchKey+=10000;
		}
		
		lowResult = lowResult/numberKeys;
		highResult = highResult/numberKeys;
		
		System.out.println("Number of low ancestors: " + lowResult);
		System.out.println("Number of high ancestors: " + highResult);
		System.out.println("k: " + tree.k);
	}
}