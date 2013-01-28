package messages.twophasecommit;

import java.awt.Color;

public class AcknowledgeMessage {
	
	private final Color color = Color.WHITE;

	public AcknowledgeMessage() {
		
	}
	
	@Override
	public String toString() {
		return "ack";
	}
	
}
