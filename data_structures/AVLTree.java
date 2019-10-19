/**
 * Implementation of AVL-Trees, based on Modula-2 code for optimized AVL-Trees in "Niklaus Wirth. Algorithms + Data Structures = Programs. Prentice-Hall, 1976".
 * Memory- and time-efficient implementation:
 * Using a Stack for storing the search path in insert- and delete-operations and storing a "balance"-parameter representing the necessary condition for each node, instead of their heights.
 * The first concept enables us to move backwards on the search path and to do the necessary rotations without having to store and update the parent node for every node; only temporary memory usage.
 * The second concept avoids having to update the height of each node in the search path during deletion/insertion. The balance parameter instead must only be updated for some nodes.
 * @author Jennifer Manke
 *
 * To enable the possible use of data type "byte" for the balance, the values are set different from the paper mentioned above, even if data type "int" is used in the actual implementation 
 * To achieve a generic usability of the "Node" class, the balance attribute is renamed as "rank" in this implementation
 * "rank"== -1: left subtree is deeper
 * "rank"==  0: both subtrees are equally deep
 * "rank"==  1: right subtree is deeper
 *  
 * Condition for deletion: element to be deleted must be present in the tree
 */

import java.util.Stack;

public class AVLTree extends Tree{
	/** 
	 * path: stores search path -> nodes visited and direction of movement ("left"==1: next node visited is left child)
	 * counter: number of rotations
	 */
	Stack<Step> path;
	int counter; 
	
	public AVLTree() {
		counter = 0;
	}
	
	private class Step{
		Node node;
		int left;
	
		private Step(Node node, int left) {
			this.node = node;
			this.left = left;
		}
	}
	
	/**
	 * Method for insertion of new key
	 * i)   traverse tree to find leaf where key needs to be inserted; store visited path
	 * ii)  insert key
	 * iii) check search path, updating "rank" values, rotating if conditions are violated; stop either at root or after rotation
	 * @param key   key to be inserted
	 */
	public void insert(int key) {
		Node current = root;
		Node newNode = new Node(key);
		path = new Stack<Step>();
		Step step = null;
		
		// find leaf where key needs to be inserted, storing search path in "path"; inserting newNode containing the key
		if(root == null) {
			root = newNode;
		}
		
		while(current != null) {
			if(current.key > key) {
				step = new Step(current,1);
				path.push(step);
				if(current.left == null) {
					current.left = newNode;
					break;
				}
				current = current.left;				
			}
			else{
				step = new Step(current,0);
				path.push(step);
				if(current.right == null) {
					current.right = newNode;
					break;
				}
				current = current.right;
			}
		}
		
		// Check search path, update balances, rotate if necessary
		while(!path.empty()) {
			/*
			 * swap: whether root itself needs to be updated
			 * last: current last object on search path
			 * temp, left: current last node and direction on search path, respectively
			 */
			boolean swap = false;
			Step last = path.pop();
			Node temp = last.node;
			int left = last.left;
			
			if(temp == root) {
				swap = true;
			}
			
			/*
			 * "left"==1: insertion has happened at left subtree
			 * if "rank"== -1: left subtree was already deeper than right one before insertion -> right-rotation or left-right-double-rotation necessary
			 * After rotation parent node needs new top node as a child
			 * Otherwise: only balances need to be updated
			 */
			if(left == 1) {
				if(temp.rank == 1) {
					temp.rank = 0;
					break;
				}
				else if(temp.rank == 0) {
					temp.rank = -1;
				}
				else {
					Node temp1 = temp.left;
					
					// Right-rotation
					if(temp1.rank == -1) {
						counter++;
						temp.left = temp1.right;
						temp1.right = temp;
						temp.rank = 0;
						temp = temp1;
						tellParent(temp,swap);
						
					}
					else {
						// Left-right-double-rotation
						counter = counter + 2;
						Node temp2 = temp1.right;
						temp1.right = temp2.left;
						temp2.left = temp1;
						temp.left = temp2.right;
						temp2.right = temp;
						if(temp2.rank == -1) {
							temp.rank = 1;
						}
						else {
							temp.rank = 0;
						}
						if(temp2.rank == 1) {
							temp1.rank = -1;
						}
						else {
							temp1.rank = 0;
						}
						temp = temp2;
						tellParent(temp,swap);
					}
					temp.rank = 0;
					break;
				}
			}
			
			// Analogous to "left"==1 
			else if(left == 0) {
				if(temp.rank == -1) {
					temp.rank = 0;
					break;
				}
				else if(temp.rank == 0) {
					temp.rank = 1;
				}
				else {
					Node temp1 = temp.right;
					
					// Left-rotation
					if(temp1.rank == 1) {
						counter++;
						temp.right = temp1.left;
						temp1.left = temp;
						temp.rank = 0;
						temp = temp1;
						tellParent(temp,swap);
					}
					else {
						// Right-left-double-rotation
						counter = counter + 2;
						Node temp2 = temp1.left;
						temp1.left = temp2.right;
						temp2.right = temp1;
						temp.right = temp2.left;
						temp2.left = temp;
						if(temp2.rank == 1) {
							temp.rank = -1;
						}
						else {
							temp.rank = 0;
						}
						if(temp2.rank == -1) {
							temp1.rank = 1;
						}
						else {
							temp1.rank = 0;
						}
						temp = temp2;
						tellParent(temp,swap);
					}
					temp.rank = 0;
					break;
				}
			}
		}
	}
	
	/**
	 * Method to update the child references of the parent node after a rotation (top node has changed in the rotation, so child has changed, too)
	 * @param new_top  top node after rotation -> new child of parent node
	 * @param swap     whether root needs to be updated  
	 */
	private void tellParent(Node new_top, boolean swap) {
		Step change = null;
		
		if(swap) {
			root = new_top;
		}
		else {
			change = path.peek();
				
			if(change.left == 0) {
				change.node.right = new_top;
			}
			else {
				change.node.left = new_top;
			}
		}
	}
	
	/**
	 * Method for deletion of a specified key
	 * i)   traverse tree to find node containing key to be deleted; store search path (condition: element must be present in the tree)
	 * ii)  delete node (node has 0 children: just delete node; node has 1 child: replace node by child; node has 2 children: replace node by its predecessor)
	 * iii) check search path, updating "rank" values, rotating if conditions are violated; stop at root
	 * @param key   key to be deleted
	 */
	public void delete(int key) {
		Node current = root;
		path = new Stack<Step>();
		
		// find node to be deleted, storing search path in "path"; in the end "current" is node to be deleted -> deletion depending on number of children
		while(current != null) {
			if(current.key == key) {
				break;
			}
			else if(current.key > key) {
				Step step = new Step(current,1);
				path.push(step);
				current = current.left;
			}
			else {
				Step step = new Step(current,0);
				path.push(step);
				current = current.right;
			}
		}
		
		// 1 or 0 children -> replace node by child, update parent node ("temp") correspondingly
		if(current.right == null) {
			if(current == root) {
				root = current.left;
			}
			else {
				Step temp = path.peek();
				if(temp.left == 1) {
					temp.node.left = current.left;
				}
				else {
					temp.node.right = current.left;
				}
			}
		}
		else if(current.left == null) {
			if(current == root) {
				root = current.right;
			}
			else {
				Step temp = path.peek();
				if(temp.left == 1) {
					temp.node.left = current.right;
				}
				else {
					temp.node.right = current.right;
				}
			}
		}
		
		/* 
		 * 2 children -> get node with greatest key in left subtree (= predecessor) and replace they key of the node to be deleted with its key
		 * Store search path as predecessor is the node where the balance updates need to start
		 */
		else {
			/*
			 * temp: node to be replaced
			 * current: replacement node
			 */
			Node temp = current;
			Step step = new Step(current,1);
			path.push(step);
			// Keep possible left child of replacement node
			current = current.left;
			
			while(current.right != null) {
				step = new Step(current,0);
				path.push(step);
				current = current.right;
			}
			temp.key = current.key;
			
			// Update parent node with new child
			if(current == root) {
				root = current.right;
			}
			else {
				Step temp1 = path.peek();
				if(temp1.left == 1) {
					temp1.node.left = current.left;
				}
				else {
					temp1.node.right = current.left;
				}
			}
		}

		// Balance updates and possible rotations for each node in the search path; called method depends on direction stored in search path
		boolean swap = true;
		
		while(!path.empty() && swap) {
			Step last = path.pop();
			if(last.left == 1) {
				swap = leftRot(last.node);
			}
			else {
				swap = rightRot(last.node);
			}
		}		
	}
	
	/**
	 * Method for left-rotation and right-left-double-rotation for deletion
	 * Calling this method implies a deeper right subtree
	 * @param node  node whose rank needs to be updated and checked
	 * @return      whether further updates are necessary
	 */
	private boolean leftRot(Node node){
		// swap: whether root itself needs to be updated
		Node temp1;
		Node temp2;
		
		boolean swap = false;
		if(node == root) {
			swap = true;
		}
		
		/*
		 * if "rank"== 1: right subtree was already deeper than left one before deletion -> left-rotation or right-left-double-rotation necessary
		 * After rotation parent node needs new top node as a child
		 * Otherwise: only balances need to be updated
		 */
		if(node.rank == -1) {
			node.rank = 0;
		}
		else if(node.rank == 0) {
			node.rank = 1;
			return false;
		}
		else { 
			//Left-rotation
			counter ++;
			temp1 = node.right;
			if(temp1.rank >= 0) {
				node.right = temp1.left;
				temp1.left = node;
				if(temp1.rank == 0) {
					node.rank = 1;
					temp1.rank = -1;
					tellParent(temp1, swap);
					// No further updates necessary
					return false;
				}
				else {
					node.rank = 0;
					temp1.rank = 0;
				}
				node = temp1;
				tellParent(node,swap);
			}
			
			
			else { 
				// Right-left-double-rotation
				counter = counter + 2;
				temp2 = temp1.left;
				temp1.left = temp2.right;
				temp2.right = temp1;
				node.right = temp2.left;
				temp2.left = node;
				if(temp2.rank == 1) {
					node.rank = -1;
				}
				else {
					node.rank = 0;
				}
				if(temp2.rank == -1) {
					temp1.rank = 1;
				}
				else {
					temp1.rank = 0;
				}
				node = temp2;
				temp2.rank = 0;
				tellParent(node,swap);
			}
		}
		return true;
	}
	
	/**
	 * Method for right-rotation and left-right-double-rotation for deletion
	 * Analogous to "leftRot(Node node)"
	 * Calling this method implies a deeper left subtree
	 * @param node  node whose rank needs to be updated and checked
	 * @return      whether further updates are necessary
	 */
	private boolean rightRot(Node node) {
		Node temp1;
		Node temp2;
		
		boolean swap = false;
		if(node == root) {
			swap = true;
		}
		
		if(node.rank == 1) {
			node.rank = 0;
		}
		
		else if(node.rank == 0) {
			node.rank = -1;
			return false;
		}
		
		else { 
			// Right-rotation
			counter++;
			temp1 = node.left;
			if(temp1.rank <=0) {
				node.left = temp1.right;
				temp1.right = node;
				if(temp1.rank == 0) {
					node.rank = -1;
					temp1.rank = 1;
					tellParent(temp1,swap);
					return false;
				}
				else {
					node.rank = 0;
					temp1.rank = 0;
				}
				node = temp1;
				tellParent(node,swap);
			}
			
			else {
				// Left-right-double-rotation
				counter = counter + 2;
				temp2 = temp1.right;
				temp1.right = temp2.left;
				temp2.left = temp1;
				node.left = temp2.right;
				temp2.right = node;
				if(temp2.rank == -1) {
					node.rank = 1;
				}
				else {
					node.rank = 0;
				}
				if(temp2.rank == 1) {
					temp1.rank = -1;
				}
				else {
					temp1.rank = 0;
				}
				node = temp2;
				temp2.rank = 0;
				tellParent(node,swap);
			}
		}
		return true;
	}	
}