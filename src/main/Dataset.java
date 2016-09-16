package main;

import java.io.FileReader;
import java.io.IOException;
import org.jblas.DoubleMatrix;
import au.com.bytecode.opencsv.CSVReader;

/*
 * This class represents a dataset. It can return data at certain indexes, return the label for an index, return random stratified train-test splits. 
 */


public class Dataset {

	private DoubleMatrix pred;
	private DoubleMatrix label;
	private DoubleMatrix mal;
	private DoubleMatrix nonMal;

	public Dataset(String predFile, String labelFile, int nObs, int nVar) throws IOException 
	{
		pred=loadPred(predFile, nObs, nVar);
		label=loadLabel(labelFile, nObs);
		findMal();
		if (pred.rows!=label.length) throw new IOException("Amount of observations differs in pred and label");

	}

	public Dataset(DoubleMatrix newPred, DoubleMatrix newLabel) {
		pred=newPred;
		label=newLabel;
		findMal();
	}

	public DoubleMatrix loadLabel(String fileName, int nObs) throws IOException, ArrayIndexOutOfBoundsException
	{
		double [] labelArray = new double [nObs];
		CSVReader reader = null;
		try
		{
			//Get the CSVReader instance with specifying the delimiter to be used
			reader = new CSVReader(new FileReader(fileName),',');
			String [] nextLine;
			int i=0;
			nextLine=reader.readNext();
			while ((nextLine = reader.readNext()) != null) 
			{
				int j=0;
				for(String token : nextLine)
				{
					if (j!=0) {
						try {
							if (Math.abs(Integer.parseInt(token))!=1) throw new IOException("Label file must contain only -1 and 1.");
							labelArray[i]=Integer.parseInt(token);
						}
						catch (NumberFormatException e) {
							System.out.println("NumberFormatException: "+e.getMessage());
						}
						catch (ArrayIndexOutOfBoundsException e) {
							System.out.println("Array Index out of bounds:" +e.getMessage());
						}
						i++;
						j++;
					}
					else j++;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		DoubleMatrix result = new DoubleMatrix(labelArray);
		return result;

	}

	public DoubleMatrix loadPred (String fileName, int nObs, int nVar) throws IOException, ArrayIndexOutOfBoundsException
	{
		double[][] predArray = new double [nObs][nVar-1];
		CSVReader reader = null;
		try
		{
			//Get the CSVReader instance with specifying the delimiter to be used
			reader = new CSVReader(new FileReader(fileName),',');
			String [] nextLine;
			//Read one line at a time
			int i=0;
			nextLine=reader.readNext();
			while ((nextLine = reader.readNext()) != null) 
			{
				int j=0;
				for(String token : nextLine)
				{
					try {
						if (j!=0) {
							predArray[i][j-1]=Double.parseDouble(token);
							j++;
						}
						else { 
							j++;
						}
					}
					catch (NumberFormatException e) {
						System.out.println("NumberFormatException: "+e.getMessage());
					}
					catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("Array Index out of bounds:" +e.getMessage());
					}

				}
				i++;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		DoubleMatrix result = new DoubleMatrix(predArray);
		return result;

	}

	public DoubleMatrix getMal() {
		return mal;
	}

	public void findMal() {	
		DoubleMatrix malTemp = DoubleMatrix.zeros(1,pred.columns);
		DoubleMatrix nonMalTemp = DoubleMatrix.zeros(1,pred.columns);
		boolean zeroMalRemoved=false;
		boolean zeroNonMalRemoved=false;
		for (int i = 0; i<pred.rows;i++) {
			if (label.get(i,0)==1) {
				if (!zeroMalRemoved) {
					malTemp=pred.getRow(i);
					zeroMalRemoved=true;
				}
				else {
					malTemp=DoubleMatrix.concatVertically(malTemp,pred.getRow(i));
				}
			}
			else {
				if (!zeroNonMalRemoved) {
					nonMalTemp=pred.getRow(i);
					zeroNonMalRemoved=true;
				}
				nonMalTemp=DoubleMatrix.concatVertically(nonMalTemp,pred.getRow(i));	
			}	
		}
		this.mal=malTemp;
		this.nonMal=nonMalTemp;
	}

	public DoubleMatrix getNonMal() {
		return nonMal;
	}

	public DoubleMatrix getPred() {
		return pred;
	}

	public DoubleMatrix getLabel() {
		return label;
	}

}
