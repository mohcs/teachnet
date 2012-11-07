package algorithms;

import teachnet.algorithm.BasicAlgorithm;

import java.awt.Color;

public class Example extends BasicAlgorithm {
	Color color = null;
	int markInterface = -1;
	String caption;

	public void setup(java.util.Map<String, Object> config) {
		int id = (Integer) config.get("node.id");
		caption = "" + id;
	}

	public void initiate() {
		int i;
		for (i = 0; i < checkInterfaces(); ++i) {
			send(i, 0);
			send(i, true);
		}
	}

	public void receive(int interf, Object message) {
		if (message instanceof Boolean) {
			send(interf, !(Boolean) message);
			return;
		}
		color = new Color(16 * (Integer) message);
		caption = "Got " + message;
		markInterface = interf;
		send((interf + 1) % checkInterfaces(), 1 + ((Integer) message));
	}
}
