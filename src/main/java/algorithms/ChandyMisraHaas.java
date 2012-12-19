package algorithms;

import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;
import messages.chandymisrahaas.*;

/**
 * Implementieren Sie mit Hilfe des Simulations-Frameworks den Algorithmus von
 * Chandy, Misra und Haas zur verteilten Deadlockerkennung. Loesen Sie erkannte
 * Deadlocks durch eine einfache Behandlung auf.
 * 
 * @author Tim Strehlow, Bjoern Stabel, Friedrich Maiwald
 */

public class ChandyMisraHaas  extends BasicAlgorithm {
	
	private int id;
	String caption;
	private Color color = Color.WHITE;
	private boolean idle = false;

	public void setup(java.util.Map<String, Object> config) {
		id = (Integer) config.get("node.id");
		caption = Integer.toString(id);
	}
	
	@Override
	public void initiate() {
		requestRessource();
		if (id == 0) {
			send(0, new ProbeMessage(id, id, (id+1)%3));
		}
	}

	@Override
	public void receive(int arg0, Object message) {
		// probe message
		if (message instanceof ProbeMessage && idle) {
			// detect deadlock
			if (((ProbeMessage) message).getInitiator() == id) {
				// resign ressource access
				releaseRessource();
			}
			// send probe message further
			else {
				send(0, new ProbeMessage(((ProbeMessage) message).getInitiator(), id, (id+1)%3));
			}
		}
		// release ressource
		else if (message instanceof Boolean) {
			if ((Boolean)message == true && idle) {
				releaseRessource();
			}
		}
	}

	/**
	 * request / block ressource
	 */
	private void requestRessource() {
		send(0, false);
		color = Color.RED;
		idle = true;
	}
	
	/**
	 * release ressource
	 */
	private void releaseRessource() {
		send(0, true);
		color = Color.WHITE;
		idle = false;
	}
	
}
