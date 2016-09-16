package main;
import java.util.Random;
import java.lang.Math;
import org.jblas.DoubleMatrix;

public class Split {

	private Dataset trainSet;
	private Dataset testSet;
	
	Split (Dataset parentSet) {
		this(parentSet, 3, true, 1);
	}
	
	Split (Dataset parentSet, int k) {
		this(parentSet, k, true, 1);
	}

	Split(Dataset parentSet, int k, boolean shuffle, int part) throws IllegalArgumentException {
		if (part > k) throw new IllegalArgumentException ("There are only "+k+ "partitions that can be selected");
		DoubleMatrix pred=parentSet.getPred();
		int nObs=pred.rows;
		int nVar=pred.columns;
		int nTest=(int) Math.floor(nObs/k);
		int nTrain=nObs-nTest;
		int nMal=parentSet.getMal().rows;
		int nNonMal = nObs-nMal;
		float floatMal = nMal;
		float floatObs = nObs;
		float frac=floatMal/floatObs;
		int nTestMal = (int) Math.ceil(nTest*frac);
		int nTestNonMal = nTest-nTestMal;
		int nTrainMal = nMal-nTestMal;
		int nTrainNonMal = nTrain-nTrainMal;

		DoubleMatrix malPred = parentSet.getMal();
		DoubleMatrix nMalPred = parentSet.getNonMal();
		if (shuffle) {
			shuffle(malPred);
			shuffle(nMalPred);	
		}

		DoubleMatrix trainPred = DoubleMatrix.zeros(nTrain,nVar);
		DoubleMatrix testPred = DoubleMatrix.zeros(nTest,nVar);
		
		DoubleMatrix trainLabelA = DoubleMatrix.ones(nTrainMal);
		DoubleMatrix trainLabelB = DoubleMatrix.ones(nTrainNonMal).neg();
		DoubleMatrix trainLabel = DoubleMatrix.concatVertically(trainLabelA,trainLabelB);
		
		DoubleMatrix testLabelA = DoubleMatrix.ones(nTestMal);
		DoubleMatrix testLabelB = DoubleMatrix.ones(nTestNonMal).neg();
		DoubleMatrix testLabel = DoubleMatrix.concatVertically(testLabelA,testLabelB);
		
		int [] testMalIndices = new int[nTestMal];
		int [] testNonMalIndices = new int[nTestNonMal];
		int [] trainMalIndices = new int[nTrainMal];
		int [] trainNonMalIndices = new int[nTrainNonMal];

		int startTest = (part-1)*nTestMal;
		int iTest=0;
		int iTrain=0;
		for (int i=0;i<nMal;i++) {
			if (i>=startTest && i<startTest+nTestMal) {
				testMalIndices[iTest]=i;
				iTest++;
			}
			else {
				trainMalIndices[iTrain]=i;
				iTrain++;
			}
		}
		
		startTest = (part-1)*nTestNonMal;
		iTest=0;
		iTrain=0;
		for (int i=0;i<nNonMal;i++) {
			if (i>=startTest && i<startTest+nTestNonMal) {
				testNonMalIndices[iTest]=i;
				iTest++;
			}
			else {
				trainNonMalIndices[iTrain]=i;
				iTrain++;
			}
		}
		
		//Putting malignant observations in test set
		int j=0;
		for (int i:testMalIndices) {
			DoubleMatrix nextRow = malPred.getRow(i);
			testPred.putRow(j,nextRow);
			j++;
		}
		//Putting malignant observations in train set
		int l=0;
		for (int i:trainMalIndices) {
			DoubleMatrix nextRow = malPred.getRow(i);
			trainPred.putRow(l,nextRow);
			l++;
		}
		//Putting non-malignant observations in test set
		for (int i:testNonMalIndices) {
			DoubleMatrix nextRow = nMalPred.getRow(i);
			testPred.putRow(j,nextRow);
			j++;
		}
		//Putting non-malignant observations in train set
		for (int i:trainNonMalIndices) {
			DoubleMatrix nextRow = nMalPred.getRow(i);
			trainPred.putRow(l,nextRow);
			l++;
		}		
		trainSet=new Dataset(trainPred,trainLabel);
		shuffle(trainSet);
		testSet=new Dataset(testPred,testLabel);
		shuffle(testSet);
	}

	public Dataset getTrainSet() {
		return trainSet;
	}

	public Dataset getTestSet() {
		return testSet;
	}
	
	

	// Implementing FisherÐYates shuffle
	private static void shuffle(DoubleMatrix matrix)
	{
		Random rnd = new Random();
		for (int i = matrix.rows - 1; i > 0; i--)
		{
			int index = rnd.nextInt(i + 1);
			// Simple swap
			matrix.swapRows(index,i);
		}
	}
	private static void shuffle (Dataset data) {
		DoubleMatrix predMatrix = data.getPred();
		DoubleMatrix labelMatrix = data.getLabel();
		Random rnd = new Random();
		for (int i = predMatrix.rows - 1; i > 0; i--)
		{
			int index = rnd.nextInt(i + 1);
			// Simple swap
			predMatrix.swapRows(index,i);
			labelMatrix.swapRows(index,i);
		}
	}

}
