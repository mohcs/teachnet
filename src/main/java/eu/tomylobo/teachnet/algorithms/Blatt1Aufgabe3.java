package eu.tomylobo.teachnet.algorithms;

import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import eu.tomylobo.teachnet.messages.echo.*;

/**
 * Implementieren Sie mit Hilfe von teachnet den Echo-Algorithmus und überprüfen
 * Sie die Richtigkeit der Nachrichtenanzahl (2e).
 */
public class Blatt1Aufgabe3 extends BasicAlgorithm {
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
		for (int i = 0; i < checkInterfaces(); ++i) {
			pending.add(i);
		}

		update();
	}

	public void initiate() {
		setInformed(true);
		for (int i = 0; i < checkInterfaces(); ++i) {
			explore(i);
		}
	}

	public void receive(int interf, Object message) {
		if (!informed) {
			sourceInterface = interf;
			if (message instanceof EchoExplorerMessage) {
				setInformed(true);
				for (int i = 0; i < checkInterfaces(); ++i) {
					if (i == interf)
						continue;

					explore(i);
				}
			}
		}

		pending.remove(interf);

		if (pending.isEmpty()) {
			confirm(sourceInterface);
		}

		update();
	}

	public void setInformed(boolean informed) {
		this.informed = informed;
		update();
	}

	private void explore(int interf) {
		send(interf, new EchoExplorerMessage());
		update();
	}

	private void confirm(int interf) {
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
			}
		}
		else {
			color = Color.WHITE;
		}
		caption = sb.toString();
	}
}
