package messages.twophasecommit;

import java.awt.Color;

public class CommitCommandMessage {
	
	private final Color color = Color.GREEN;

	public CommitCommandMessage() {
		
	}
	
	@Override
	public String toString() {
		return "commit command";
	}
	
}
