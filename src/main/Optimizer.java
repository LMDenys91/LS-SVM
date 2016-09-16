package main;

import exceptions.NotYetSupportedException;

public class Optimizer {

	public static void optimize(lssvmClassifier classifier, double [] gammaValues, double [] sigmaValues) throws NotYetSupportedException {

		Kernel.Type type = classifier.getKernel().getType();
		switch (type) {

		case LINEAR:
			classifier.setGamma(oneDimSearch(classifier,gammaValues));
			classifier.trainModel();
			break;

		case RBF:
			classifier.setHyperParams(gridSearch(classifier, gammaValues, sigmaValues));
			classifier.trainModel();
			break;

		default: throw new NotYetSupportedException ("This kernel function is not yet supported.");	
		}

	}

	public static void optimize(lssvmClassifier classifier) throws NotYetSupportedException {
		double [] defaultGammas = {0.1,1,10,100};
		double [] defaultSigmas = {0.01,0.1,1,10};
		optimize(classifier, defaultGammas, defaultSigmas);
	}

	public static double oneDimSearch(lssvmClassifier classifier, double [] gammaValues) {
		double bestGamma=gammaValues[0];
		double bestPerformance=evaluatePerformance(classifier, bestGamma, 1);
		for (int i=1;i<gammaValues.length;i++) {
			double gamma=gammaValues[i];
			double performance = evaluatePerformance(classifier, gamma, 1);
			if (performance>bestPerformance) {
				bestPerformance=performance;
				bestGamma=gamma;
			}
		}
		return bestGamma;
	}

	public static double [] gridSearch(lssvmClassifier classifier, double [] gammaValues, double [] sigmaValues) {
		double bestGamma=gammaValues[0];
		double bestSigma=sigmaValues[0];
		double bestPerformance = evaluatePerformance(classifier, bestGamma, bestSigma);
		for (int i=0;i<sigmaValues.length;i++) {
			for (int j=0;j<gammaValues.length;j++) {
				double sigma=sigmaValues[i];
				double gamma=gammaValues[j];
				double performance = evaluatePerformance(classifier, gamma, sigma);
				if (performance>bestPerformance) {
					bestPerformance=performance;
					bestGamma=gamma;
					bestSigma=sigma;
				}
			}
		}	
		double [] bestHyperParams = {bestGamma, bestSigma};
		return bestHyperParams;
	}

	public static double evaluatePerformance(lssvmClassifier classifier, double gamma, double sigma) {
		Dataset data = classifier.getData().getTrainSet();
		double sumAUC=0;
		for (int i=1;i<=10;i++) {
			Split newSplit = new Split(data, 10, false, i);
			lssvmClassifier tempClassifier = new lssvmClassifier(newSplit, classifier.getKernel().getType(), gamma, sigma);
			sumAUC=sumAUC+tempClassifier.rocArea();
		}
		return sumAUC/10;
	}


}
