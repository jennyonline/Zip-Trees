/** 
 * Implementation of Skip-Lists based on pseudocode for optimized Skip-Lists in "William Pugh. Skip lists: a probabilistic alternative to balanced trees. 
   Communications of the ACM, 33(6), 1990".
 * Memory- and time-efficient implementation:
 * Every node stores exactly as many pointers to other nodes as the level he is on -> fast traversing through the list, minimal memory needed
 * To keep the list as compact as possible, a "maxLevel" is computed, which caps the number of levels possible, depending on the expected number of elements in the list
 * Nodes do not store their own level, only maximum level of whole list is stored
 * @author Jennifer Manke
 * 
 * Constructor needs number of expected elements for the list
 * 
 * Numbering of levels starts with 0 
 * "maxLevel" is always greater than "level" -> "level" corresponds with last index of "pointer" array
 */

public class SkipList implements SearchStruct{
	/*
	 * head: first element (lower key than any possible real key), present on every level (pointer for every possible level available)
	 * tail: last element (higher key than any possible real key), no pointer 
	 * maxLevel: maximum number of levels for the list, depending on expected number of elements 
	 * prob: probability for an element to appear on the next level
	 * level: current maximum level of the Skip-List
	 */
	ListNode head;
	ListNode tail;
	int maxLevel;
	double prob = 0.5;
	int level;
	int counter;

	public SkipList(int size) {
		// Formula according to paper: maxLevel = log(1/prob)size
		maxLevel = (int)(Math.log(size)/Math.log(1/prob));
		level = 0;
		
		head = new ListNode(-1, maxLevel);
		tail = new ListNode(size+2,0);
		// Initially all pointer of head point at tail
		for(int i=0;i<head.pointer.length;i++) {
			head.pointer[i] = tail;
		}
	}
	
	// Data structure for the nodes, containing a key and a number of pointer according to their level
	private class ListNode{
		int key;
		ListNode[] pointer;
		
		public ListNode(int key, int level) {
			this.key = key;
			pointer = new ListNode[level];
		}
	}
	
	/**
	 * Method for searching for a key
	 * Starting at the top level ("head"), traverse every level from left to right, searching for the key
	 * At every last key greater than search key: go one level down at previous node
	 * If left neighbor on level 0 equals search key -> search key is present, otherwise not
	 * @param key  key to be looking for
	 */
	public boolean search(int key) {
		counter = 0;
		ListNode temp = head;
		for(int i=level; i>=0; i--) {
			while(temp.pointer[i].key < key) {
				counter ++;
				temp = temp.pointer[i];
			}
		}
		temp = temp.pointer[0];
		
		if(temp.key == key) {
			return true;
		}
		System.out.println("Key not found");
		return false;
	}

	// Depth corresponds to level
	public int getDepth() {
		return level;
	}

	/**
	 * Method for key insertion
	 * i)   Search for place for key to be inserted while storing the nodes that need to be updated later
	 * ii)  Add nodes to be updated for levels not yet existent
	 * iii) Update all nodes correspondingly
	 * @param key  key to be inserted
	 */
	public void insert(int key) {
		// update: stores all nodes that need to be updated
		ListNode[] update = new ListNode[maxLevel];
		ListNode temp = head;
			
		// Looking for right place
		for(int i = level; i >= 0; i--) {
			while(temp.pointer[i].key < key) {
				temp = temp.pointer[i];
			}
			update[i] = temp;
		}
		temp = temp.pointer[0];
		
		// New level
		int randLev = randomLevel();
		if(randLev > level) {
			for(int i=level+1; i <= randLev; i++) {
				update[i] = head;
				
			}
			level = randLev;
		}
		
		// Updating
		ListNode newNode = new ListNode(key,level+1);
		for(int i=0; i<=randLev;i++) {
			newNode.pointer[i] = update[i].pointer[i];
			update[i].pointer[i] = newNode;
		}
	}
	
	/**
	 * Method for determining randomly on how many levels a new node shall be present
	 * @return  number of levels new node shall be present
	 */
	private int randomLevel() {
		int randLev = 0;
		// Level needs to stay smaller than "maxLevel"
		while((Math.random() < prob) && (randLev < maxLevel-1)) {
			randLev++;
		}
		return randLev;
	}

	/** 
	 * Method for deleting a node with a given key
	 * Element to be deleted must be present in the list
	 * Analogous to "insert(int key)"
	 * @param key  key to be deleted
	 */
	public void delete(int key) {
		ListNode[] update = new ListNode[maxLevel];
		ListNode temp = head;
		
		for(int i = level; i >= 0; i--) {
			while(temp.pointer[i].key < key) {
				temp = temp.pointer[i];
			}
			update[i] = temp;
		}
		temp = temp.pointer[0];
		
		if(temp.key == key) {
			for(int i=0; i <= level; i++) {
				if(update[i].pointer[i] != temp) {
					break;
				}
				update[i].pointer[i] = temp.pointer[i];
			}
			
			// Adjust current level
			while((level > 0) && (head.pointer[level] == tail)) {
				level = level-1;
			}
		}
		
	}
	
	/**
	 * Method to show the current list
	 * Every level is printed in a separate line
	 */
	public void printList() {
		ListNode temp;
		
		for(int i = level; i>=0; i--) {
			System.out.print("Level " + i + ": ");
			temp = head;
			
			while(temp.key < tail.key) {
				System.out.print(temp.key + ", ");
				temp = temp.pointer[i];
				
			}
			// Representing tail
			System.out.print("999999 \n");
		}
	}
	
	public int getCounter() {
		return counter;
	}
}	