/**
 * Search Structure Interface
 * Needs to be implemented to use Framework
 * @author Jennifer Manke
 */

public interface SearchStruct{
	public boolean search(int key);
	public int getDepth();	
	public void insert(int key);
	public void delete(int key);
	public int getCounter();
}