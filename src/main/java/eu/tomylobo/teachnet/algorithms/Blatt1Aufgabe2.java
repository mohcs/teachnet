package eu.tomylobo.teachnet.algorithms;

import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import eu.tomylobo.teachnet.messages.flood.*;

/**
 * Implementieren Sie mit Hilfe des Simulationsframeworks teachnet (zu finden in
 * ISIS) den Flutungsalgorithmus mit Bestätigung. Testen Sie den Algorithmus auf
 * einem Ring und vergleichen Sie die Nachrichtenanzahl mit dem "Broadcast auf
 * unidirektionalen Ringen" auf derselben Topologie.
 */
public class Blatt1Aufgabe2 extends BasicAlgorithm {
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
		setInformed(true);
		for (int i = 0; i < checkInterfaces(); ++i) {
			explore(i);
		}
	}

	public void receive(int interf, Object message) {
		if (message instanceof ExplorerMessage) {
			if (!informed) {
				sourceInterface = interf;
				setInformed(true);
				for (int i = 0; i < checkInterfaces(); ++i) {
					if (i == interf)
						continue;

					explore(i);
				}
			}
			else {
				confirm(interf);
				update();
				return;
			}
		}
		else if (message instanceof ConfirmationMessage) {
			pending.remove(interf);
			update();
		}

		if (informed && pending.isEmpty()) {
			confirm(sourceInterface);
		}

		update();
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
