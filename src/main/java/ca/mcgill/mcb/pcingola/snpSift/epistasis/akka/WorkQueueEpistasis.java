package ca.mcgill.mcb.pcingola.snpSift.epistasis.akka;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * A work queue  
 * 
 * @author pablocingolani
 */
public class WorkQueueEpistasis {

	int batchSize, showEvery;
	Class<? extends Actor> masterClazz;
	Props masterProps;

	public WorkQueueEpistasis(int batchSize, int showEvery, Props masterProps) {
		this.batchSize = batchSize;
		this.showEvery = showEvery;
		this.masterProps = masterProps;
	}

	public void run(boolean wait) {
		// Create an Akka system
		ActorSystem workQueue = ActorSystem.create("WorkQueueEpistasis");

		// Create the master
		ActorRef master;
		if (masterClazz != null) master = workQueue.actorOf(new Props(masterClazz), "masterEpistasis");
		else master = workQueue.actorOf(masterProps, "masterEpistasis");

		// Start processing
		master.tell(new StartMasterEpistasis(batchSize, showEvery));

		// Wait until completion
		if (wait) workQueue.awaitTermination();
	}
}
