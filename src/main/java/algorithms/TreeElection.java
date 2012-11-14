package algorithms;

import java.awt.Color;
import java.util.Vector;

import teachnet.algorithm.BasicAlgorithm;

/**
 * Implementierung des Auswahl-Algorithmus auf Baeumen. Initial werden Knoten
 * weiss dargestellt, nach der Explosionsphase (Nachrichten vom Typ Boolean =
 * false) werden sie rot, nach der Kontraktionsphase orange angezeigt. Alle
 * ueber den Leader informierten Knoten sind am Ende in blau dargestellt.
 * 
 * @author Tim Strehlow 316594, Bjoern Stabel 222128, Friedrich Maiwald 350570
 *         Gruppe 08
 * 
 */

public class TreeElection extends BasicAlgorithm {
	Color color;
	String caption;
	Boolean exploded = false;
	Boolean contracted = false;
	Vector<Integer> contractions = new Vector<Integer>();
	int id;
	int max;

	@Override
	public void setup(java.util.Map<String, Object> config) {
		id = (Integer) config.get("node.id");
		max = id;
		caption = "" + id;
		color = Color.WHITE;
	}

	@Override
	public void initiate() {
		exploded = true;

		// If a leaf is initiating we immediately send a contraction message
		if (checkInterfaces() == 1) {
			color = Color.ORANGE;
			caption = id + " (" + max + ")";
			send(0, id);
			contracted = true;
			return;
		}

		color = Color.RED;
		// send explosion message to all neighbors
		for (int i = 0; i < checkInterfaces(); i++) {
			send(i, false);
		}
	}

	@Override
	public void receive(int interf, Object message) {
		// explosion message
		if (message instanceof Boolean) {
			if (contracted) {
				// if the node is already contracted ignore the explosion
				// message
				return;
			}
			if (!exploded) {
				exploded = true;
				color = Color.RED;
				sendToAllButOne(interf, false);
			}
			// itself as leave detected, send id (end contraction phase for this
			// node)
			if (checkInterfaces() == 1) {
				color = Color.ORANGE;
				caption = id + " (" + max + ")";
				send(0, id);
				contracted = true;
			}
		}
		// maximum / information message
		else if (message instanceof Integer) {
			max = Math.max(max, (Integer) message);
			if (!exploded) {
				contractions.add(interf);
				exploded = true;
				color = Color.RED;
				sendToAllButOne(interf, false);
			} else if (!contracted) {
				// gather maximum from children
				contractions.add(interf);
				// informed by all leaves, send maximum to parent
				if (contractions.size() == (checkInterfaces() - 1)) {
					for (int i = 0; i < checkInterfaces(); i++) {
						if (!contractions.contains(i)) {
							send(i, Math.max(max, (Integer) message));
							break;
						}
					}
					// end contraction phase for this node
					contracted = true;
					color = Color.ORANGE;
					caption = id + " (" + max + ")";
					return;
				}
			}
			// information about leader
			else if (contracted) {
				caption = id + " (" + max + ")";
				color = Color.BLUE;
				// inform neighbors except source interface
				sendToAllButOne(interf, Math.max(max, (Integer) message));
			}
		}
	}

	/**
	 * Helper-Method to send an Object to all interfaces except one.
	 * 
	 * @param ifToLeaveOut
	 *            - the interface to be left out
	 * @param msg
	 *            - the Object to be sent
	 */
	private void sendToAllButOne(int ifToLeaveOut, Object msg) {
		for (int i = 0; i < checkInterfaces(); i++) {
			if (i == ifToLeaveOut) {
				continue;
			}
			send(i, msg);
		}
	}
}
