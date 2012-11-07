package algorithms;

import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import messages.flood.*;


/**
 * Implementieren Sie mit Hilfe des Simulationsframeworks teachnet (zu finden in
 * ISIS) den Flutungsalgorithmus mit Bestätigung. Testen Sie den Algorithmus auf
 * einem Ring und vergleichen Sie die Nachrichtenanzahl mit dem "Broadcast auf
 * unidirektionalen Ringen" auf derselben Topologie.
 */
public class FloodingWithConfirmation extends BasicAlgorithm {
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
			if (informed) {
				// Wenn ja, senden wir sofort eine Bestätigung zurück und sind fertig
				confirm(interf);
				return;
			}

			// Ansonsten ist dies der erste Explorer den wir empfangen und wir merken uns woher er kam.
			sourceInterface = interf;

			// Ausserdem markieren wir den Knoten als informiert.
			setInformed(true);

			// Und schliesslich senden wir weitere Explorer auf allen verbleibenden Interfaces aus.
			for (int i = 0; i < checkInterfaces(); ++i) {
				if (i == interf)
					continue;

				explore(i);
			}
		}
		else if (message instanceof ConfirmationMessage) {
			// Wenn wir eine Bestätigungsmeldung bekommen, haken wir das entsprechende Interface ab.
			pending.remove(interf);
			update();
		}

		// Keine ausstehenden Antworten?
		if (pending.isEmpty()) {
			// Ja => Bestätigung an das Interface des ersten Explorers schicken
			// Dieser Fall tritt ein, wenn ein Explorer von einem Blattknoten empfangen wird oder die letzte Bestätigungsmeldung eingegangen ist.
			confirm(sourceInterface);
		}
	}

	public void setInformed(boolean informed) {
		this.informed = informed;
		update();
	}

	private void explore(int interf) {
		pending.add(interf);
		send(interf, new ExplorerMessage());
		update();
	}

	private void confirm(int interf) {
		if (interf < 0) {
			return;
		}
		send(interf, new ConfirmationMessage());
	}

	public void update() {
		StringBuilder sb = new StringBuilder();

		sb.append(id);

		if (informed) {
			if (pending.isEmpty()) {
				color = Color.RED;
			}
			else {
				color = Color.GREEN;
				sb.append("("+pending.size()+")");
			}
		}
		else {
			color = Color.BLACK;
		}
		caption = sb.toString();
	}
}
