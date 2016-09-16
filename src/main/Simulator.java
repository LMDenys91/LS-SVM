package main;

import java.io.IOException;


public class Simulator {


	public static void main(String[] args) throws IOException {
		Dataset dataset = new Dataset("src/scaledPred.csv","src/label.csv", 2467, 64);	
		Split split = new Split(dataset);
		lssvmClassifier classifier = new lssvmClassifier(split,Kernel.Type.RBF,100,1);
		System.out.println("Starting optimization.");
		Optimizer.optimize(classifier);
		System.out.println("AUC: "+classifier.rocArea());
		classifier.confusionMatrix();
		System.out.println("Classification finished.");
	}
}
