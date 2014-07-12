package ca.mcgill.mcb.pcingola.snpSift;

import ca.mcgill.mcb.pcingola.fileIterator.MatrixEntry;
import ca.mcgill.mcb.pcingola.fileIterator.MatrixEntryFileIterator;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;

/**
 * Convert allele 'matrix' file into Covariance matrix
 * 
 * Note: Only variants with two possible alleles. I.e. the matrix has three possible values in each cell:
 * 		- 0, for allele 0/0
 * 		- 1, for allele 0/1 or 1/0
 * 		- 2, for allele 1/1
 * 
 * @author pcingola
 */
public class SnpSiftCmdCovarianceMatrix extends SnpSift {

	public static int SHOW_EVERY = 10000;
	String matrixFile;
	double mean[];
	double matrix[][];

	public SnpSiftCmdCovarianceMatrix() {
		super(null, null);
	}

	public SnpSiftCmdCovarianceMatrix(String[] args) {
		super(args, "alleleMatrix");
	}

	/**
	 * Calculate covariance matrix
	 */
	public void covariance() {
		// Initialize
		int len = mean.length;
		matrix = new double[len][len];
		for (int i = 0; i < len; i++)
			for (int j = 0; j < len; j++)
				matrix[i][j] = 0;

		// Iterate on file
		int count = 0, lineNum = 1;
		MatrixEntryFileIterator mfile = new MatrixEntryFileIterator(matrixFile);
		for (MatrixEntry m : mfile) {
			int scores[] = m.getValues();

			for (int i = 0; i < len; i++)
				for (int j = i; j < len; j++)
					matrix[i][j] += (scores[i] - mean[i]) * (scores[j] - mean[j]);

			Gpr.showMark(lineNum++, SHOW_EVERY);
			count++;
		}

		// Fill up lower half
		for (int i = 0; i < len; i++)
			for (int j = i + 1; j < len; j++)
				matrix[j][i] = matrix[i][j];

		// Divide by N
		if (count > 0) {
			for (int i = 0; i < len; i++)
				for (int j = 0; j < len; j++)
					matrix[j][i] /= count;
		}

		// Calculate sigma (and divide by sigma)
		for (int i = 0; i < len; i++) {
			double p = mean[i] / 2.0;
			double sigma = Math.sqrt(p * (1.0 - p));

			if (sigma == 0) throw new RuntimeException("Sigma is zero for column " + i + " (mean = " + mean[i] + "). This should never happen!");

			for (int j = 0; j < len; j++)
				matrix[j][i] /= sigma;
		}
	}

	/**
	 * Calculate mean
	 */
	public void mean() {
		long sum[] = null;
		int count = 0, lineNum = 1;
		MatrixEntryFileIterator mfile = new MatrixEntryFileIterator(matrixFile);
		for (MatrixEntry m : mfile) {
			// Get scores
			int scores[] = m.getValues();
			if (sum == null) sum = new long[scores.length]; // Initialize sum?

			// Add
			for (int i = 0; i < scores.length; i++)
				sum[i] += scores[i];

			Gpr.showMark(lineNum++, SHOW_EVERY);
			count++;
		}

		// Calculate the mean
		mean = new double[sum.length];
		if (count > 0) {
			for (int i = 0; i < mean.length; i++) {
				mean[i] = ((double) sum[i]) / count;
			}
		}
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parse(String[] args) {
		if (args.length != 1) usage(null);
		matrixFile = args[0];
	}

	/**
	 * Process the whole file
	 */
	@Override
	public void run() {
		Timer.showStdErr("Pass1: Processing file '" + matrixFile + "'");
		mean();

		Timer.showStdErr("Pass2: Processing file '" + matrixFile + "'");
		covariance();

		System.out.println(this);
		Timer.showStdErr("Done");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < matrix.length; i++) {
			String sep = "";

			for (int j = 0; j < matrix[i].length; j++) {
				sb.append(sep + matrix[i][j]);
				sep = "\t";
			}

			sb.append('\n');
		}

		return sb.toString();
	}

	/**
	 * Show usage message
	 * @param msg
	 */
	@Override
	public void usage(String msg) {
		if (msg != null) {
			System.err.println("Error: " + msg);
			showCmd();
		}
		showVersion();
		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar covMat allele.matrix.txt > cov.matrix.txt");
		System.exit(1);
	}
}
