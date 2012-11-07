package algorithms;

import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import messages.echo.*;


/**
 * Implementieren Sie mit Hilfe von teachnet den Echo-Algorithmus und überprüfen
 * Sie die Richtigkeit der Nachrichtenanzahl (2e).
 */
public class Echo extends BasicAlgorithm {
	@SuppressWarnings("unused")
	private Color color = null; // Färbt Knoten ein
	@SuppressWarnings("unused")
	private int markInterface = -1; // Hebt Kante n hervor
	@SuppressWarnings("unused")
	private String caption; // Beschriftung des Knotens

	int id;
	private boolean informed = false;
	private Set<Integer> pending = new HashSet<Integer>();
	private int sourceInterface = -1;

	public void setup(java.util.Map<String, Object> config) {
		id = (Integer) config.get("node.id");
		// Anfangs markieren wir alle Interfaces aller Knoten.
		for (int i = 0; i < checkInterfaces(); ++i) {
			pending.add(i);
		}

		update();
	}

	public void initiate() {
		// Der Startknoten ist immer informiert.
		setInformed(true);
		// Anfangs wird ein Explorer auf jede mit dem Startknoten verbundene Kante gesendet.
		// 'explore' übernimmt dabei auch das markieren der Interfaces mit ausstehenden Antworten.
		for (int i = 0; i < checkInterfaces(); ++i) {
			explore(i);
		}
	}

	public void receive(int interf, Object message) {
		if (message instanceof ExplorerMessage) {
			// Wenn ein Explorer empfangen wird, checken wir zunächst, ob wir bereits informiert wurden.
			if (!informed) {
				// Dies ist der erste Explorer den wir empfangen und wir merken uns woher er kam.
				sourceInterface = interf;

				// Ausserdem markieren wir den Knoten als informiert.
				setInformed(true);

				markInterface = interf;
				// Und schliesslich senden wir weitere Explorer auf allen verbleibenden Interfaces aus.
				for (int i = 0; i < checkInterfaces(); ++i) {
					if (i == interf)
						continue;
					explore(i);
				}
			}
		}

		// Sowohl bei einem Explorer als auch einem Echo haken wir das entsprechende Interface ab.
		pending.remove(interf);
		update();

		// Keine ausstehenden Antworten?
		if (pending.isEmpty()) {
			// Ja => Echo an das Interface des ersten Explorers schicken
			echo(sourceInterface);
		}
	}

	public void setInformed(boolean informed) {
		this.informed = informed;
		update();
	}

	private void explore(int interf) {
		send(interf, new ExplorerMessage());
	}

	private void echo(int interf) {
		if (interf < 0) {
			return;
		}
		send(interf, new EchoMessage());
	}

	public void update() {
		StringBuilder sb = new StringBuilder();

		sb.append(id);

		if (informed) {
			if (pending.isEmpty()) {
				color = Color.GREEN;
			}
			else {
				color = Color.RED;
				sb.append("("+pending.size()+")");
			}
		}
		else {
			color = Color.WHITE;
		}
		caption = sb.toString();
	}
}
