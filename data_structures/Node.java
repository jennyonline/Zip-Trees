/**
 * Data structure for use by all trees
 * @author Jennifer Manke
 */

public class Node{
	/*
	 * This class shall be used by all trees
	 * Therefore it must enable the storage of an int rank for Zip-Trees, a floating point rank for Treaps and a balance for AVL-Trees (byte value)
	 * As in the JVM all data types get 32 bit of memory, all the requirements are met by introducing a "rank" attribute of data type "float"
	 * Values are computed in Tree classes, passed to constructor
	 */
	int key;
	Node left;
	Node right;
	float rank;
		
	// Used by AVL-Trees
	public Node(int key) {
		this.key = key;
		left = null;
		right = null;
		rank = 0;
	}
	
	// Used by Zip-Trees and Treaps
	public Node(int key, float rank) {
		this.key = key;
		left = null;
		right = null;
		this.rank = rank;
	}
}