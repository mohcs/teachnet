package algorithms;

import java.awt.Color;

import messages.changroberts.MasterMessage;
import messages.changroberts.MaxMessage;

import teachnet.algorithm.BasicAlgorithm;

/**
 * Implementierung des Algorithmus von Chang und Roberts zur Auswahl eines
 * Knotens mit der hoechsten ID in einem Ring. Initial werden alle Knoten weiss
 * dargestellt. Der Knoten der die Wahl gewonnen hat wird rot dargestellt, alle
 * ueber die Auswahl informierten Knoten werden blau dargestellt.
 * 
 * @author Tim Strehlow, Bjoern Stabel, Friedrich Maiwald
 */
public class Max2 extends BasicAlgorithm {
	Color color;
	String caption;
	int id;
	int max;
	boolean init;

	@Override
	public void setup(java.util.Map<String, Object> config) {
		this.id = (Integer) config.get("node.id");
		this.max = this.id;
		this.color = Color.WHITE;
		this.caption = getCaption();
		this.init = false;
	}

	@Override
	public void initiate() {
		if (init) {
			return;
		}
		if (max > id)
			return;
		init = true;
		// Maximal-Wert initial auf eigene ID setzen
		this.max = this.id;
		send(0, new MaxMessage(this.id));
	}

	@Override
	public void receive(int interf, Object message) {
		// Verarbeiten der Benachrichtigung, dass die groesste ID gefunden ist
		if (message instanceof MasterMessage) {
			if (color != Color.RED) {
				color = Color.BLUE;
				// Naechsten Knoten informieren
				send(0, message);
			}
			return;
		}

		if (color != Color.WHITE)
			return;

		int value = ((MaxMessage) message).getValue();
		if (this.max < value) {
			// Dieser Knoten brauch nicht mehr als Initiator agieren, da er
			// nicht mehr gewinnen kann
			init = true;
			this.max = value;
			this.caption = getCaption();
			send(0, message);
		}
		else if (value == this.id) {
			// Dieser Knoten hat die Wahl gewonnen
			color = Color.RED;
			// Durch weiteren Ringdurchlauf alle informieren
			send(0, new MasterMessage());
		}
		else {
			init = true;
			send(0, new MaxMessage(this.max));
		}
	}

	/**
	 * @return Die Beschriftung des Knotens, bestehend aus der ID und dem
	 *         derzeitigen Maximal-Wert.
	 */
	private String getCaption() {
		return this.id + " / " + this.max;
	}
}
