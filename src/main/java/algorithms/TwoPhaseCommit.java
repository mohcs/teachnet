package algorithms;

import java.awt.Color;
import java.util.Vector;

import messages.twophasecommit.AbortCommandMessage;
import messages.twophasecommit.AbortMessage;
import messages.twophasecommit.AcknowledgeMessage;
import messages.twophasecommit.CommitCommandMessage;
import messages.twophasecommit.CommitMessage;
import messages.twophasecommit.PrepareMessage;
import messages.twophasecommit.VoteAbortMessage;
import messages.twophasecommit.VoteCommitMessage;
import teachnet.algorithm.BasicAlgorithm;

/**
 * Implementieren Sie das 2PC Protokoll fuer verteilte Transaktionen. Testen Sie
 * ihre Implementierung mit folgenden Vorgaben:
 * 
 * a) Der Koordinator erhaelt ein Commit sowie ein Abort Command.
 * 
 * b) Alle Teilnehmer entscheiden sich fuer Vote Commit sowie ein Teilnehmer
 * entscheidet sich fuer Vote Abort.
 * 
 * @author Tim Strehlow 316594, Bjoern Stabel 222128, Friedrich Maiwald 350570
 *         Gruppe 08
 */

public class TwoPhaseCommit extends BasicAlgorithm {

	int id;
	String caption;
	Color color;
	Vector<Boolean> participants;
	int ackCounter = 0;

	@Override
	public void setup(java.util.Map<String, Object> config) {
		id = (Integer) config.get("node.id");
		caption = "" + id;

		// hard coded: node 0 is the coordinator
		if (id == 0) {
			color = Color.BLACK;
		} else {
			color = Color.WHITE;	
		}
	}

	@Override
	public void initiate() {
		// hardcoded: node 4 initiates an abort command
		if (id % 4 == 0) {
			send(0, new AbortCommandMessage());
		} 
		// hardcoded: other nodes initiate a commit command
		else {
			send(0, new CommitCommandMessage());
		}
	}

	@Override
	public void receive(int interf, Object message) {
		// commit command from participant
		if (message instanceof CommitCommandMessage) {
			participants = new Vector<Boolean>(checkInterfaces() - 1);
			color = Color.GRAY;
			for (int i = 0; i < checkInterfaces(); i++) {
				send(i, new PrepareMessage());
			}
		}
		// abort command from participant
		else if (message instanceof AbortCommandMessage) {
			color = Color.GRAY;
			for (int i = 0; i < checkInterfaces(); i++) {
				send(i, new AbortMessage());
			}
		}
		// prepare message from coordinator
		else if (message instanceof PrepareMessage) {
			// success with a probability of 75%
			double d = Math.random();
			if (d < 0.75) {
				send(interf, new VoteCommitMessage());
				color = Color.GREEN;
			} else {
				send(interf, new VoteAbortMessage());
				color = Color.RED;
			}
			
		}
		// vote commit message from participant
		else if (message instanceof VoteCommitMessage) {
			participants.add(true);
			checkParticipants();
		}
		// vote abort message from participant
		else if (message instanceof VoteAbortMessage) {
			participants.add(false);
			checkParticipants();
		}
		// commit message from coordinator
		else if (message instanceof CommitMessage) {
			send(interf, new AcknowledgeMessage());
			color = Color.WHITE;
		}
		// abort message from coordinator
		else if (message instanceof AbortMessage) {
			send(interf, new AcknowledgeMessage());
			color = Color.WHITE;
		}
		// acknowledge message from participant
		else if (message instanceof AcknowledgeMessage) {
			ackCounter++;
			if (ackCounter == checkInterfaces() - 1) {
				ackCounter = 0;
				color = Color.BLACK;
			}
		}
	}

	/**
	 * Checks all received answers. If all participants have answered, the
	 * commit messages respectively the abort messages are send
	 */
	private void checkParticipants() {

		// return if not all answer were received yet
		if (participants.size() < checkInterfaces() - 1) {
			return;
		}

		// loop through all answer and check if there was any abort vote
		boolean consensus = true;
		for (int i = 0; i < participants.size(); i++) {
			if (participants.get(i) == Boolean.FALSE) {
				consensus = false;
			}
		}

		// if positive consensus was found send commit messages to all
		// participants
		if (consensus) {
			for (int i = 0; i < checkInterfaces(); i++) {
				send(i, new CommitMessage());
			}
		}
		// else send abort messages to all participants
		else {
			for (int i = 0; i < checkInterfaces(); i++) {
				send(i, new AbortMessage());
			}
		}
	}

}
