package algorithms;

import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;
import java.util.Vector;

/**
 * Implementierung des Auswahl-Algorithmus auf Baeumen. Initial werden Knoten
 * weiss dargestellt, nach der Explosionsphase (Nachrichten vom Typ Boolean = 
 * false) werden sie rot, nach der Kontraktionsphase orange angezeigt. Alle 
 * ueber den Leader informierten Knoten sind am Ende in blau dargestellt.
 *
 * @author Tim Strehlow, Bjoern Stabel, Friedrich Maiwald
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
			if (!exploded) {
				exploded = true;
				color = Color.RED;
				for (int i = 0; i < checkInterfaces(); i++) {
					if (i == interf) continue;
					send(i, false);
				}
			} 
			// itself as leave detected, send id (end contraction phase for this node)
			if (checkInterfaces() == 1) {
				color = Color.ORANGE;
				caption = id + " (" + max + ")";
				send(0, id);
				contracted = true;
			}
		}
		// maximum / information message
		else if (message instanceof Integer) {
			if (!contracted) {
				// gather maximum from children
				max = Math.max(max, (Integer)message);
				contractions.add(interf);
				// informed by all leaves, send maximum to parent
				if (contractions.size() == (checkInterfaces() - 1)) {
					for (int i = 0; i < checkInterfaces(); i++) {
						if (!contractions.contains(i)) {
							send(i, Math.max(max, (Integer)message));
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
			if (contracted) {
				max = Math.max(max, (Integer)message);
				caption = id + " (" + max + ")";
				color = Color.BLUE;
				// inform neighbors except source interface
				for (int i = 0; i < checkInterfaces(); i++) {
					if (i == interf) continue;
					send (i, Math.max(max, (Integer)message));
				}

			}
		}
	}
}
