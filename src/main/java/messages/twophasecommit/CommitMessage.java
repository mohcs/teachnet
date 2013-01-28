package messages.twophasecommit;

import java.awt.Color;

public class CommitMessage {
	
	private final Color color = Color.GREEN;

	public CommitMessage() {
		
	}
	
	@Override
	public String toString() {
		return "commit";
	}
	
}
