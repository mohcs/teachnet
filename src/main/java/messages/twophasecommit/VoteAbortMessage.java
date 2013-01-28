package messages.twophasecommit;

import java.awt.Color;

public class VoteAbortMessage {

	private final Color color = Color.RED;
	
	public VoteAbortMessage() {
		
	}
	
	@Override
	public String toString() {
		return "vote abort";
	}
	
}
