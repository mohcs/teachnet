package messages.changroberts;

import java.awt.Color;

public class MaxMessage {
	@SuppressWarnings("unused")
	private final Color color = Color.BLUE;

	private final int value;

	public MaxMessage(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "There is a "+value;
	}

	public int getValue() {
		return value;
	}
}
