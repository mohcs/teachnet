package algorithms;

import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;
import messages.chandymisrahaas.*;

/**
 * Implementieren Sie mit Hilfe des Simulations-Frameworks den Algorithmus von
 * Chandy, Misra und Haas zur verteilten Deadlockerkennung. Loesen Sie erkannte
 * Deadlocks durch eine einfache Behandlung auf.
 * 
 * @author Tim Strehlow, Bjoern Stabel, Friedrich Maiwald (Gruppe 8)
 */

public class ChandyMisraHaas  extends BasicAlgorithm {
	
	private int id;
	private int numberOfAccess = 0;
	String caption;
	private Color color = Color.WHITE;
	private boolean idle = false;
	private int requestInterf;

	public void setup(java.util.Map<String, Object> config) {
		id = (Integer) config.get("node.id");
		caption = Integer.toString(id) + " (" + numberOfAccess + ")";
	}
	
	@Override
	public void initiate() {
		requestRessource();
	}

	@Override
	public void receive(int interf, Object message) {
		if (message instanceof RequestMessage) {
			if (idle) {
				requestInterf = interf;
				// hardcoded: node 0 starts deadlock detection
				if (id == 0) {
					send(0, new ProbeMessage(id, id, (id+1)%3));
				}
			}
			else {
				send(interf, new ReleaseMessage());
			}
		}
		// probe message
		else if (message instanceof ProbeMessage && idle) {
			// detect deadlock
			if (((ProbeMessage) message).getInitiator() == id) {
				// resign/delay ressource access
				releaseRessource();
				// still waiting for ressource access
				idle = true;
				color = Color.RED;
			}
			// send probe message further
			else {
				send(0, new ProbeMessage(((ProbeMessage) message).getInitiator(), id, (id+1)%3));
			}
		}
		// release ressource
		else if (message instanceof ReleaseMessage && idle) {
			// do and count ressource access
			numberOfAccess++;
			updateCaption();
			// send only release message if to nodes still waiting
			if (requestInterf > 0) {
				releaseRessource();
			}
			else {
				idle = false;
				color = Color.WHITE;
			}
		}
	}

	/**
	 * request / block ressource
	 */
	private void requestRessource() {
		send(0, new RequestMessage(id));
		color = Color.RED;
		idle = true;
	}
	
	/**
	 * release ressource
	 */
	private void releaseRessource() {
		send(requestInterf, new ReleaseMessage());
		color = Color.WHITE;
		idle = false;
		requestInterf = -1;
	}
	
	/**
	 * 
	 */ 
	private void updateCaption() {
		caption = id + " (" + numberOfAccess + ")";
	}
}
