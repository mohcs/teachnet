package messages.twophasecommit;

import java.awt.Color;

public class VoteCommitMessage {
	
	private final Color color = Color.GREEN;

	public VoteCommitMessage() {
		
	}
	
	@Override
	public String toString() {
		return "vote commit";
	}
	
}
