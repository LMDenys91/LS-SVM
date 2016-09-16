package main;

import org.jblas.DoubleMatrix;
import mloss.roc.Curve;
import exceptions.IllegalDimensionsException;
import exceptions.IllegalVectorElementsException;

public abstract class Classifier {

	protected Split data;
	protected Model model;

	public Classifier(Split split) {
		this.data=split;
		instantiateModel();
	}
	
	public Classifier(Split split, Model model) {
		this.data=split;
		this.model=model;
	}

	public abstract void instantiateModel();
	public abstract void trainModel();
	public abstract DoubleMatrix classify(DoubleMatrix x, boolean continuous);
	public abstract DoubleMatrix classify(DoubleMatrix x);
	
	public Split getData() {
		return data;
	}

	public Model getModel() {
		return model;
	}

	public double classifyOne(DoubleMatrix x, boolean continuous) throws IllegalDimensionsException {
		if (x.rows>1) throw new IllegalDimensionsException ("This method can only be used to classify a vector.");
		return classify(x,continuous).get(0,0);
	}
	
	// A list containing the true label for each example in the order of classified most likely positive to classified most likely negative.
	public int [] rankedLabels() {
		Dataset test = data.getTestSet();
		DoubleMatrix classified = classify(test.getPred(),true);
		DoubleMatrix label = test.getLabel();
		double [] sortingList = classified.toArray();
		int [] outputList = label.toIntArray();
		SortPair pair = new SortPair(outputList, sortingList);
		int [] result = MergeSort.mergeSort(pair).getOutput();
		return result;
	}
	
	public double rocArea() {
		Curve curve = new Curve(rankedLabels());
		return curve.rocArea();
	}

	public int[] confusionMatrix() throws IllegalDimensionsException, IllegalVectorElementsException {
		Dataset test = data.getTestSet();
		DoubleMatrix classified = classify(test.getPred());
		DoubleMatrix label = test.getLabel();
		int nMal=0;
		for (int i = 0; i<label.rows;i++) {
			if (label.get(i,0)==1) nMal=nMal+1;
		}	
		if (classified.rows!=label.rows) throw new IllegalDimensionsException ("Dimensions must be equal");
		int TP = 0;
		int FP = 0;
		int TN = 0;
		int FN = 0;
		for (int i=0;i<label.rows;i++) {
			double predClass = classified.get(i,0);
			double labClass = label.get(i,0);
			if (predClass==-1.0 && labClass==-1.0) TN=TN+1;
			else if (predClass==1.0 && labClass==-1.0) FP=FP+1;
			else if (predClass==-1.0 && labClass==1.0) FN=FN+1;
			else if (predClass==1.0 && labClass == 1.0) TP=TP+1;
			else throw new IllegalVectorElementsException("Vector elements should all be +1 or -1."+predClass+","+labClass);
		}
		int [] result = {TP,FP,TN,FN};
		System.out.println("TP:"+TP+", FP:"+FP+", TN:"+TN+", FN:"+FN);
		return result;
	}

	

}
