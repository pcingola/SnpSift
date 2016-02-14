package org.snpsift.hwe;

import org.snpeff.akka.vcf.MasterVcf;

import akka.actor.Props;

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
