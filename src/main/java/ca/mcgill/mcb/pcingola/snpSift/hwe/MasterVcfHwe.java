package ca.mcgill.mcb.pcingola.snpSift.hwe;

import akka.actor.Props;
import ca.mcgill.mcb.pcingola.akka.vcf.MasterVcf;

/**
 * A simple demo of a master process
 * 
 * @author pablocingolani
 */
public class MasterVcfHwe extends MasterVcf<String> {

	public MasterVcfHwe(int numWorkers) {
		super(new Props(WorkerVcfHwe.class), numWorkers);
	}
}
