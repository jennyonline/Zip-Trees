/**
 * Framework for testing different implementations of binary search trees (and Skip-Lists) implementing the "SearchStruct" interface
 * Creates a random ArrayList consisting of pairwise different elements (representing keys) of a desired size and measures the time needed for
 * creating a tree out of these items, as well as the time needed for insertion, search for and deletion of a certain amount of items into that tree
 * Also measures the memory to store trees of a certain size
 * @author Jennifer Manke
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

public class Framework{
	/**
	 * repeat: how many times the execution shall be repeated, must be at least 2 (at the end of program division by (repeat-1))
	 * numbers: number of elements for the initial tree (must be greater than "elements")
	 * elements: number of elements to be inserted, searched for and deleted while time is being measured (must be smaller than "numbers")
	 */
	// recommended: 1.000, 1.000.000, 120.000
	static final int repeat = 1000;
	static final int numbers = 1000000;
	static final int elements = 120000;
	
	/**
	 * Method for creating a long random ArrayList of size "size" of pairwise different elements
	 * Initially a sorted list containing the elements from 0 to "size" is created, which is then shuffled randomly to obtain a random list
	 * @param size   number of elements for the ArrayList (as well as for the initial tree)
	 * @return       random ArrayList, to be used for creating a big, initial tree as well as inserting further elements 
	 */
	public static ArrayList<Integer> shuffleDiff(int size) {
		ArrayList<Integer> list = new ArrayList<Integer>();
			
		for(int i=0;i<size;i++) {
			list.add(i);
		}
		Collections.shuffle(list);
		return list;
	}
	
	/**
	 * Method for creating a small, random ArrayList of "size" size of pairwise different elements, with values in range of the elements to be inserted into the tree
	 * Too slow for creating big ArrayLists, but convenient to get small lists of random elements in the whole possible value-range of the trees to be searched for or to be deleted
	 * @param size  	number of elements for the ArrayList
	 * @param boundary  range for values to be chosen
	 * @return          random ArrayList, to be used for searching for and deletion of elements
	 */
	public static ArrayList<Integer> randomShortList(int size, int boundary) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		Random random = new Random();
		int value;
		
		for(int i=0; i < size; i++) {
			value = random.nextInt(boundary);
			while(list.contains(value)) {
				value = random.nextInt(boundary);
			}
			list.add(value);
		}
		return list;
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		if(elements > numbers) {
			System.out.println("Parameter numbers must be greater than or equal to parameter elements.");
			return;
		}
		
		/**
		 * To add a new tree for measuring: 
		 * i)  add tree to String list "names"
		 * ii) create new switch-case for new tree at the beginning of the first for-loop
		 */
		String[] names = {"AVLTree ", "ZipTree ", "ZipRek  ", "ZipOpt  ", "Treap   ", "SkipList", "RBJava  "};
		
		/*
		 * Initialization
		 * runtime/memoryStart: for computation of memory consumption
		 * timeStart: for computation of time
		 */
		SearchStruct tree;
		Iterator<Integer> iterator;
		Runtime runtime = Runtime.getRuntime();
		long memoryStart;
		long timeStart;
				
		// Arrays for time and memory measuring: outer array -> one for every tree-type; inner array -> one for every iteration
		double[][] createTimes = new double[names.length][repeat];
		double[][] insertTimes = new double[names.length][repeat];
		double[][] deleteTimes = new double[names.length][repeat];
		double[][] searchTimes = new double[names.length][repeat];
		long[][] usedMemory = new long[names.length][repeat];
		int[][] depths = new int[names.length][repeat];
		int[][] numberComparisons = new int[names.length][repeat];
		
		for(int i=0; i<repeat; i++) {
			if(i%10==0) {
				System.out.println(i + " fertig");
			}
			
			/**
			 * Every iteration uses the same lists for every tree-type, for better comparability.
			 * list: first elements ("numbers") for creation of initial tree, last elements ("elements") used for time measuring of insertion
			 * searchList/deleteList: random lists, instead of just picking random values. This approach was chosen to avoid the possibility of the same element being chosen twice
			 *                        for deletion and to reach a higher level of comparability by making sure that every tree-type gets to search for/delete the same elements
			 */
			ArrayList<Integer> list = new ArrayList<Integer>();
			ArrayList<Integer> searchList = new ArrayList<Integer>();
			ArrayList<Integer> deleteList = new ArrayList<Integer>();
			list = shuffleDiff(numbers+elements);	
			searchList = randomShortList(elements,numbers+elements);
			deleteList = randomShortList(elements,numbers+elements);
			
			/*
			 * For every tree-type:
			 * Time measuring of creation of a big, initial tree, as well as of inserting, searching for and deleting the chosen number of elements ("elements")
			 * Memory measuring of tree of size "numbers"
			 */
			for(int j=0; j < names.length; j++) {
				/*
				 * In order to get comparable results for time as well as for memory usage, the garbage collector "gc" is explicitly called before the creation of a new tree
				 * To make sure that only the current tree is measured in memory, only one tree gets created by the switch-case instruction
				 * runtime.totalMemory()/runtime.freeMemory(): returns the specified memory value in unit of Bytes
				 */
				runtime.gc();
		        memoryStart = runtime.totalMemory() - runtime.freeMemory();
				
		        switch(j) {
		        case 0:
					tree = new AVLTree();
					break;
		        case 1:
					tree = new ZipTree();
					break;
		        case 2:
					tree = new ZipRek();
					break;
		        case 3: 
					tree = new ZipOpt();
					break;
				case 4: 
					tree = new Treap();
					break;
				case 5:
					tree = new SkipList(numbers+elements);
					break;
				default:
					tree = new RBJava();
					break;
		        }
		        
		        // Creation of a tree, consisting of "numbers" many elements
		        iterator = list.iterator();
				timeStart = new Date().getTime();
				for(int k=0; k < numbers; k++){
					tree.insert(iterator.next());
				}
				createTimes[j][i] = new Date().getTime() - timeStart;
				
				/*
				 * Memory usage of tree of size "numbers" gets calculated
				 * To only measure the tree without memory used for insert operations, the garbage collector is called again
				 * Calling the garbage collector also leads to faster further execution as unneeded memory gets cleared
				 */
				runtime.gc();
				usedMemory[j][i] = (runtime.totalMemory() - runtime.freeMemory()) - memoryStart;
				
				// Insertion of "elements" many elements
				// Depth of resulting tree is determined
				timeStart = new Date().getTime();
				while(iterator.hasNext()){
					tree.insert(iterator.next());
				}
				insertTimes[j][i] = new Date().getTime() - timeStart;
				depths[j][i] = tree.getDepth();
				
				// Search for all elements contained in searchList
				iterator = searchList.iterator();
				timeStart = new Date().getTime();
				while(iterator.hasNext()) {
					tree.search(iterator.next());
					
				}
				searchTimes[j][i] = new Date().getTime() - timeStart;
				numberComparisons[j][i] = tree.getCounter();
				
				// Deletion of all elements contained in deleteList
				iterator = deleteList.iterator();
				timeStart = new Date().getTime();
				while(iterator.hasNext()) {
					tree.delete(iterator.next());
				}
				deleteTimes[j][i] = new Date().getTime() - timeStart;
			}
		}
		
		// Computing average time/memory needed for the different operations
		for(int j=0; j < names.length; j++) {
			double insert = 0.0;
			double search = 0.0;
			double delete = 0.0;
			double depth = 0.0;
			double create = 0.0;
			double memory = 0.0;
			double counter = 0.0;
			
			// As the JVM need warming up, the first value measured is not reliable and is therefore not considered for computation
			for(int i=1; i < repeat; i++) {
				insert += insertTimes[j][i];
				search += searchTimes[j][i];
				delete += deleteTimes[j][i];
				create += createTimes[j][i];
				depth += depths[j][i];
				memory += usedMemory[j][i];
				counter += numberComparisons[j][i];
			}
			
			create = create/(repeat-1);
			insert = insert/(repeat-1);			
			search = search/(repeat-1);			
			delete = delete/(repeat-1);			
			depth = depth/(repeat-1);
			memory = memory/(repeat-1);
			counter = counter/(repeat-1);
			
			System.out.println(names[j] + " || create: " + create + " || insert: " + insert + " || search: " + search + 
					" || delete: " + delete + " || depth: " + depth + "|| number of comparisons: " + counter + "|| memory: " + memory);
		}	
	}
}