package main;

import java.util.ArrayList;

import org.jblas.DoubleMatrix;
import org.jblas.Solve;


public class lssvmClassifier extends Classifier {

	private double gamma;
	private Kernel kernel;
	private lssvmModel model;
	
	public lssvmClassifier(Split split, Kernel.Type type, double g, double sigma) {
		super(split);
		this.gamma=g;
		this.kernel = new Kernel (type);
		kernel.setSigma(sigma);
		trainModel();
	}
	
	public lssvmClassifier(Split split, Kernel.Type type, ArrayList<Double> hyperParams) {
		super(split);
		this.kernel=new Kernel(type);
		this.gamma=hyperParams.get(0).doubleValue();
		if (hyperParams.size()>1) kernel.setSigma(hyperParams.get(1).doubleValue());
		else kernel.setSigma(1);
		trainModel();
	}
	
	public lssvmClassifier(Split split, Kernel.Type type, double g) {
		super(split);
		this.gamma=g;
		this.kernel = new Kernel (type);
		kernel.setSigma(1);
		trainModel();
	}
	
	public lssvmClassifier(Split split, double g) {
		super(split);
		this.gamma=g;
		this.kernel = new Kernel (Kernel.Type.LINEAR);
		kernel.setSigma(1);
		trainModel();
	}
	
	public lssvmClassifier(Split split, Kernel.Type type) {
		super(split);
		this.gamma=1;
		this.kernel = new Kernel (type);
		kernel.setSigma(1);
		trainModel();
	}
	
	public lssvmClassifier(Split split) {
		super(split);
		this.gamma=1;
		this.kernel = new Kernel (Kernel.Type.LINEAR);
		kernel.setSigma(1);
		trainModel();
	}
	
	@Override
	public lssvmModel getModel() {
		return this.model;
	}
	
	public void setModel(lssvmModel model) {
		this.model = model;
	}
	
	@Override
	public void instantiateModel() {
		Dataset trainSet = data.getTrainSet();
		this.model = new lssvmModel(trainSet);
	}
	
	@Override
	public void trainModel() {
		DoubleMatrix pred = data.getTrainSet().getPred();
		DoubleMatrix omega = kernel.kernelMatrix(pred);
		DoubleMatrix Y = data.getTrainSet().getLabel();
		int N = Y.rows;
		DoubleMatrix zeros = DoubleMatrix.zeros(1,1);
		DoubleMatrix ones = DoubleMatrix.ones(N,1);
		DoubleMatrix eye = DoubleMatrix.eye(N);
		DoubleMatrix gammaEye = eye.div(gamma);
		DoubleMatrix Aupper = DoubleMatrix.concatHorizontally(zeros,Y.transpose().neg());
		DoubleMatrix Alower = DoubleMatrix.concatHorizontally(Y,omega.add(gammaEye));
		DoubleMatrix A = DoubleMatrix.concatVertically(Aupper,Alower);
		DoubleMatrix B = DoubleMatrix.concatVertically(zeros,ones);
		DoubleMatrix X = Solve.solve(A,B);
		model.setB(X.get(0,0));
		DoubleMatrix alpha = DoubleMatrix.zeros(N,1);
		for (int i=0;i<N;i++) {
			alpha.put(i,0,X.get(i+1,0));
		}
		model.setAlpha(alpha);
	}
	
	//return 1 if malignant, -1 if non-malignant
	@Override 
	public DoubleMatrix classify(DoubleMatrix x, boolean continuous) {
		int m = x.rows;
		DoubleMatrix classifications = DoubleMatrix.zeros(m,1);
		DoubleMatrix Y = data.getTrainSet().getLabel();
		int n = Y.rows;
		DoubleMatrix pred = data.getTrainSet().getPred();
		DoubleMatrix alpha = model.getAlpha();
		double B = model.getB();
		for (int i=0;i<m;i++) {
			double adder = 0;
			DoubleMatrix tempx = x.getRow(i);
			for (int j=1;j<n;j++) {
				double psi = kernel.kernelFunction(pred.getRow(j),tempx);
				adder = adder + psi*alpha.get(j,0)*Y.get(j,0)+B; 
			}
			if (!continuous) {
				adder=Math.signum(adder);
				if(adder==0.0) adder=Math.signum(1);
			}
			classifications.put(i,0,adder);
		}
		return classifications;
	}
	@Override
	public DoubleMatrix classify (DoubleMatrix x) {
		return classify(x, false);
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
	}
	
	public double getGamma() {
		return this.gamma;
	}

	public Kernel getKernel() {
		return kernel;
	}
	
	public ArrayList<Double> getHyperParams() {
		ArrayList<Double> hyperParams = new ArrayList<Double>(1);
		hyperParams.add(gamma);
		if (kernel.getType() == Kernel.Type.RBF) hyperParams.add(kernel.getSigma());
		return hyperParams;
	}
	
	public void setHyperParams(double [] hyperParams) {
		this.gamma=hyperParams[0];
		if (hyperParams.length>1) kernel.setSigma(hyperParams[1]);
		else kernel.setSigma(1);
		trainModel();
	}

}
