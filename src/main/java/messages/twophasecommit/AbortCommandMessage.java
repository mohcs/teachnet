package messages.twophasecommit;

import java.awt.Color;

public class AbortCommandMessage {
	
	private final Color color = Color.RED;

	public AbortCommandMessage() {
		
	}
	
	@Override
	public String toString() {
		return "abort command";
	}
	
}
