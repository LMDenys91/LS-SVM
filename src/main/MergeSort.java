package main;
import exceptions.IllegalDimensionsException;


public class MergeSort {

	//The elements of the outputList are shuffled according to the sorted indices of the sortingList.
	public static SortPair mergeSort(SortPair pair) throws IllegalDimensionsException {
		int [] outputList = pair.getOutput();
		double [] sortingList = pair.getSorting();
		if (outputList.length <= 1) {
			return pair;
		}

		if (outputList.length != sortingList.length) throw new IllegalDimensionsException ("Sorting and output lists should have same length.");

		// Split the array in half
		int[] firstOutput = new int[outputList.length / 2];
		int[] secondOutput = new int[outputList.length - firstOutput.length];

		double[] firstSorting = new double[outputList.length / 2];
		double[] secondSorting = new double[outputList.length - firstOutput.length];

		System.arraycopy(outputList, 0, firstOutput, 0, firstOutput.length);
		System.arraycopy(outputList, firstOutput.length, secondOutput, 0, secondOutput.length);

		System.arraycopy(sortingList, 0, firstSorting, 0, firstSorting.length);
		System.arraycopy(sortingList, firstSorting.length, secondSorting, 0, secondSorting.length);

		// Sort each half: dit moet iets teruggeven, hoe kan het dat die methodes hier gewoon los staan?
		SortPair firstPair = new SortPair(firstOutput, firstSorting);
		SortPair secondPair = new SortPair(secondOutput, secondSorting);

		firstPair=mergeSort(firstPair);
		secondPair=mergeSort(secondPair);

		// Merge the halves together, overwriting the original array
		pair = merge(firstPair, secondPair, outputList.length);
		return pair;
	}

	private static SortPair merge(SortPair firstPair, SortPair secondPair, int length) {
		int [] output = new int [length];
		double [] sorting = new double [length];

		int [] firstOutput = firstPair.getOutput();
		double [] firstSorting = firstPair.getSorting();
		int [] secondOutput = secondPair.getOutput();
		double [] secondSorting = secondPair.getSorting();

		// Merge both halves into the result array
		// Next element to consider in the first array
		int iFirst = 0;
		// Next element to consider in the second array
		int iSecond = 0;

		// Next open position in the result
		int j = 0;
		// As long as neither iFirst nor iSecond is past the end, move the
		// bigger element into the result.
		while (iFirst < firstOutput.length && iSecond < secondOutput.length) {
			if (firstSorting[iFirst] > secondSorting[iSecond]) {
				output[j] = firstOutput[iFirst];
				sorting[j] = firstSorting[iFirst];
				iFirst++;
			} 
			else {
				output[j] = secondOutput[iSecond];
				sorting[j] = secondSorting[iSecond];
				iSecond++;
			}
			j++;
		}
		// copy what's left
		System.arraycopy(firstOutput, iFirst, output, j, firstOutput.length - iFirst);
		System.arraycopy(firstSorting, iFirst, sorting, j, firstSorting.length - iFirst);
		System.arraycopy(secondOutput, iSecond, output, j, secondOutput.length - iSecond);
		System.arraycopy(secondSorting, iSecond, sorting, j, secondSorting.length - iSecond);
		return new SortPair(output, sorting);
	}
}
