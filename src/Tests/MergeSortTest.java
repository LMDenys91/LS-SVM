package Tests;


import org.junit.Before;
import org.junit.Test;
import main.SortPair;
import main.MergeSort;
import static org.junit.Assert.*;

public class MergeSortTest {

	private int [] result;
	private int [] expected = {1,2,3,4,5,6,7,8};;
	
	@Before
	public void setUp() throws Exception {
		double [] sortList = {2,-5,3,0.5,-1,-8.7,-3,6};
		int [] outputList = {3,7,2,4,5,8,6,1};
		SortPair pair = new SortPair(outputList, sortList);
		result = MergeSort.mergeSort(pair).getOutput();
	}
	@Test
	public void test() throws Exception {
		for (int i = 0; i<result.length;i++) {
			System.out.println(result[i]);
		}
		assertArrayEquals(expected,result);
	}
	

}
