package messages.twophasecommit;

import java.awt.Color;

public class AbortMessage {
	
	private final Color color = Color.RED;

	public AbortMessage() {
		
	}
	
	@Override
	public String toString() {
		return "abort";
	}
	
}
