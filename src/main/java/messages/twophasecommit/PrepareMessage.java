package messages.twophasecommit;

import java.awt.Color;

public class PrepareMessage {
	
	private final Color color = Color.BLACK;

	public PrepareMessage() {
		
	}
	
	@Override
	public String toString() {
		return "prepare";
	}
	
}
