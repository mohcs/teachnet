package algorithms;

import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;
import java.util.Vector;

public class TreeElection extends BasicAlgorithm {
	Color color;
	String caption;
	Boolean exploded = false;
	Boolean contracted = false;
	Vector<Integer> contractions = new Vector<Integer>();
	int id;
	int max;
	int source;

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
				source  = interf;
				for (int i = 0; i < checkInterfaces(); i++) {
					if (i == interf) continue;
					send(i, false);
				}
			} 
			// itself as leave detected, send id
			if (checkInterfaces() == 1) {
				color = Color.ORANGE;
				caption = id + " (" + max + ")";
				send(0, id);
				contracted = true;
			}
		}
		// maximum message from leaves
		else if (message instanceof Integer) {
			if (!contracted) {
				max = Math.max(max, (Integer)message);
				contractions.add(interf);
				// informed by all leaves, send maximum to parent
				if (contractions.size() == (checkInterfaces() - 1)) {
					for (int i = 0; i < checkInterfaces(); i++) {
						if (!contractions.contains(i)) {
							send(i, Math.max(max, (Integer)message));
						}
					}
					contracted = true;
					color = Color.ORANGE;
					caption = id + " (" + max + ")";
					return;
				}
			}
			if (contracted) {
				max = Math.max(max, (Integer)message);
				caption = id + " (" + max + ")";
				color = Color.BLUE;
				for (int i = 0; i < checkInterfaces(); i++) {
					if (i == interf) continue;
					send (i, Math.max(max, (Integer)message));
				}

			}
		}
	}

}
