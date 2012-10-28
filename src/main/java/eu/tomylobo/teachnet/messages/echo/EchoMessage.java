package eu.tomylobo.teachnet.messages.echo;

import java.awt.Color;

public class EchoMessage {
	@SuppressWarnings("unused")
	private final Color color = Color.GREEN;

	@Override
	public String toString() {
		return "echo";
	}
}
