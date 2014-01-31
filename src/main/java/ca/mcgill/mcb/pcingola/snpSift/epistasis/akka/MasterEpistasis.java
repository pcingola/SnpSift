package ca.mcgill.mcb.pcingola.snpSift.epistasis.akka;

import akka.actor.Actor;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import ca.mcgill.mcb.pcingola.akka.Master;
import ca.mcgill.mcb.pcingola.snpSift.epistasis.SnpSiftCmdEpistasis;

/**
 * Master agent
 * 
 * @author pablocingolani
 */
public class MasterEpistasis extends Master<Integer, String> {

	SnpSiftCmdEpistasis snpSiftCmdEpistasis;
	int idx = 0;

	@SuppressWarnings("serial")
	public MasterEpistasis(int numWorkers, final SnpSiftCmdEpistasis snpSiftCmdEpistasis) {
		super(new Props( //
				// Create a factory
				new UntypedActorFactory() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -7032441361243553723L;

					@Override
					public Actor create() {
						return new WorkerEpistasis(snpSiftCmdEpistasis);
					}

				}) //
				, numWorkers);

		this.snpSiftCmdEpistasis = snpSiftCmdEpistasis;
	}

	@Override
	public boolean hasNext() {
		return idx < snpSiftCmdEpistasis.getGenotypes().size();
	}

	@Override
	public Integer next() {
		return idx++;
	}
}
