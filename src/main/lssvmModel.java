package main;
import org.jblas.DoubleMatrix;


public class lssvmModel extends Model {
	
	private DoubleMatrix alpha;
	private double b;

	public lssvmModel(Dataset train) {
		super(train);
	}

	public DoubleMatrix getAlpha() {
		return alpha;
	}

	public void setAlpha(DoubleMatrix alpha) {
		this.alpha = alpha;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}
	
	
	

}
