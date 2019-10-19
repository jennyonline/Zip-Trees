/**
 * Red-Black-Trees based on Java implementation (TreeMap) with some modifications in order to compute the tree depth
 * Only implements "SearchStruct", does not extend "Tree", as it does not need a root or a search or getDepth method
 * @author Jennifer Manke
 *
 * Depth cannot be obtained
 */


public class RBJava implements SearchStruct{
	TreeMapAdaptation<Integer,Integer> map;
	
	public RBJava() {
		map = new TreeMapAdaptation<Integer,Integer>();
	}

	public boolean search(int key) {
		if(map.containsKey(key)) {
			return true;
		}
		System.out.println("Key not found");
		return false;
	}

	public int getDepth() {
		return map.Depth(map.root);
	}

	public void insert(int key) {
		map.put(key,0);
	}

	public void delete(int key) {
		map.remove(key);
	}
	
	public int getCounter() {
		return map.counter;
	}
}