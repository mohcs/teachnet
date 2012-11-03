import java.awt.Color;

import teachnet.algorithm.BasicAlgorithm;

public class ChangRoberts extends BasicAlgorithm {
	Color color;
	String caption;
	int id;
	int max;
	boolean init = false;

	public void setup(java.util.Map<String, Object> config) {
		this.id = (Integer) config.get("node.id");
		this.max = this.id;
		this.color = Color.WHITE;
		this.caption = getCaption();
	}

	@Override
	public void initiate() {
		if (init) {
			return;
		}
		init = true;
		this.max = this.id;
		send(0, this.max);
	}

	@Override
	public void receive(int arg0, Object arg1) {
		if (arg1 instanceof Boolean) {
			if (color != Color.red) {
				color = Color.blue;
				send(0, true);
			}
			return;
		}

		int value = (Integer) arg1;
		if (this.max < value) {
			init = true;
			this.max = value;
			this.caption = getCaption();
			send(0, this.max);
		}
		if (value == this.id) {
			color = Color.red;
			send(0, true);
		}
	}

	private String getCaption() {
		return this.id + " / " + this.max;
	}
}
