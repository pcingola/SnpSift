package ca.mcgill.mcb.pcingola.snpSift.epistasis.akka;

import ca.mcgill.mcb.pcingola.akka.Worker;
import ca.mcgill.mcb.pcingola.snpSift.epistasis.SnpSiftCmdEpistasis;

/**
 * Worker agent 
 * 
 * @author pablocingolani
 */
public class WorkerEpistasis extends Worker<Integer, String> {

	SnpSiftCmdEpistasis snpSiftCmdEpistasis; // Used only to show errors

	public WorkerEpistasis(SnpSiftCmdEpistasis snpSiftCmdEpistasis) {
		super();
		this.snpSiftCmdEpistasis = snpSiftCmdEpistasis;
	}

	@Override
	public String calculate(Integer idx) {
		String out = "";
		try {
			out = snpSiftCmdEpistasis.runEpistasis(idx);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return out.isEmpty() ? null : out;
	}

}
