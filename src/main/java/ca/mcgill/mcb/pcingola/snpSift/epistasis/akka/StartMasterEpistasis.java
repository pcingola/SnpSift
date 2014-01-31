package ca.mcgill.mcb.pcingola.snpSift.epistasis.akka;

import ca.mcgill.mcb.pcingola.akka.msg.StartMaster;

/**
 * A message telling master process to start calculating
 * 
 * @author pablocingolani
 */
public class StartMasterEpistasis extends StartMaster {

	public StartMasterEpistasis(int batchSize, int showEvery) {
		super(batchSize, showEvery);
	}
}
