package main;
import org.jblas.DoubleMatrix;

import exceptions.IllegalDimensionsException;
import exceptions.NotYetSupportedException;


public class Kernel {

	public enum Type {RBF,LINEAR,CLINICAL,POLY,SIGMOID};
	private Type type;
	private double sigma;
	private double [] range;

	public Kernel (Type type) {
		this.type=type;
	}

	public DoubleMatrix kernelMatrix (DoubleMatrix pred) throws NotYetSupportedException {
		int m = pred.rows;
		int n = pred.columns;
		DoubleMatrix omega = DoubleMatrix.zeros(m,m);
		switch(type)
		{
		case LINEAR:
			omega = pred.mmul(pred.transpose());
			return omega.div(pred.columns);
		case CLINICAL:
			range = new double[n];
			for (int i=0; i<n; i++) {
				DoubleMatrix var = pred.getColumn(i);
				range[i] = var.max()-var.min();
			}
			for (int j=0;j<m;j++) {
				for (int k=0;k<=j;k++) {
					double newValue = 0;
					for (int i=0;i<n;i++) {
						newValue = newValue + (range[i]-Math.abs(pred.get(j,i)-pred.get(k,i)))/range[i];
					}
					omega.put(j,k,newValue);
					omega.put(k,j,newValue);
				}
			}
			return omega;
		case RBF:
			for (int i=0;i<m;i++) {
				for (int j=0;j<m;j++) {
					DoubleMatrix temp = pred.getRow(i).sub(pred.getRow(j));
					temp = temp.mmul(temp.transpose());
					temp=temp.neg();
					temp=temp.div(Math.pow(sigma,2));
					//System.out.println("Temp dimensions:"+temp.rows+"x"+temp.columns);
					double pow = temp.get(0,0);
					omega.put(i,j,Math.exp(pow));
				}
			}
			return omega;
		default: throw new NotYetSupportedException ("Currently only Linear, Clinical Data and RBF kernels are supported.");
		}
	}

	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	public double getSigma() {
		return sigma;
	}

	public Type getType() {
		return type;
	}

	public double kernelFunction (DoubleMatrix a, DoubleMatrix b) throws IllegalDimensionsException, NotYetSupportedException {
		int m = a.rows;
		int n = a.columns;
		if (a.rows+b.rows!=2 || a.columns!=b.columns) throw new IllegalDimensionsException ("This method can only be used for vectors!");
		DoubleMatrix omega = DoubleMatrix.zeros(m,m);
		switch(type)
		{
		case LINEAR:
			omega = a.mmul(b.transpose());
			omega.div(n);
			return omega.get(0,0);
		case CLINICAL:
			double newValue = 0;
			for (int i=0;i<n;i++) {
				newValue = newValue + (range[i]-Math.abs(a.get(0,i)-b.get(0,i)))/range[i];
			}
			return newValue;
		case RBF:
			DoubleMatrix temp = a.sub(b);
			temp = temp.mmul(temp.transpose());
			temp=temp.neg();
			temp=temp.div(Math.pow(sigma,2));
			return temp.get(0,0);
		default: throw new NotYetSupportedException ("Currently only Linear, Clinical Data and RBF kernels are supported.");
		}
	}
}
