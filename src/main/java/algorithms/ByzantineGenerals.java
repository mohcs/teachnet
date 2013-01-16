package algorithms;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import messages.byzantinegenerals.ByzantineMessage;
import messages.byzantinegenerals.InitiateMessage;
import teachnet.algorithm.BasicAlgorithm;

/**
 * Implementation of the byzantine generals algorithm. White nodes are loyal
 * generals, red nodes are disloyal. Green nodes are loyal leaders, yellow nodes
 * are disloyal leaders.
 * 
 * @author Tim Strehlow 316594, Bjoern Stabel 222128, Friedrich Maiwald 350570
 *         Gruppe 08
 */

public class ByzantineGenerals extends BasicAlgorithm {

	private int id;
	String caption;
	private Color color = Color.WHITE;

	private boolean isLoyal;
	private boolean isLeader;

	private boolean currentValue;

	private List<ByzantineMessage> receivedMsgs;
	private int[] interfaceToNodeMap;

	private boolean isInitiated;

	private int m;
	private int n;
	private int totalMsgCount;
	private int[] disloyalIds;

	public void setup(java.util.Map<String, Object> config) {
		this.id = (Integer) config.get("node.id");

		this.isLoyal = true;

		String disloyalGenerals = (String) config.get("disloyalGenerals");
		String[] disloyalGeneralsArray = disloyalGenerals.split(",");
		this.disloyalIds = new int[disloyalGeneralsArray.length];
		for (int i = 0; i < disloyalGeneralsArray.length; i++) {
			this.disloyalIds[i] = Integer.parseInt(disloyalGeneralsArray[i]);
			if (this.disloyalIds[i] == this.id) {
				this.isLoyal = false;
			}
		}

		this.m = disloyalIds.length;
		this.n = (Integer) config.get("network.size");

		totalMsgCount = getTotalMessageCount();

		// Z. Zt. wird einfach immer der Knoten mit der ID 0 der Leader
		this.isLeader = (id == (Integer) config.get("leadingGeneral"));
		if (this.isLeader) {
			this.color = Color.green;
		}
		this.currentValue = false;
		this.receivedMsgs = new ArrayList<ByzantineMessage>();

		if (!this.isLoyal) {
			this.color = Color.red;
		}
		this.isInitiated = false;

		if (!this.isLoyal && this.isLeader) {
			this.color = Color.yellow;
		}
		this.interfaceToNodeMap = new int[checkInterfaces()];
		for (int i = 0; i < this.interfaceToNodeMap.length; i++) {
			this.interfaceToNodeMap[i] = -1;
		}

		updateCaption();
	}

	@Override
	public void initiate() {
		// we rely on the config to call initiate() two times
		if (this.isLeader) {
			// At first InitiateMessages are sent to inform the other nodes
			// about this node's id
			if (!this.isInitiated) {
				for (int i = 0; i < checkInterfaces(); i++) {
					send(i, new InitiateMessage(this.id));
				}
				this.isInitiated = true;
			} else {
				// the second time initiate() is called the real algorithm
				// starts
				for (int i = 0; i < checkInterfaces(); i++) {
					// if the leader is loyal it will send a message containing
					// "true", otherwise a random boolean
					boolean valueToSend = isLoyal ? true : new Random()
							.nextBoolean();
					final ByzantineMessage msgToSend = new ByzantineMessage(
							new int[] { this.id }, valueToSend, false);
					send(i, msgToSend);
				}
			}
		}
	}

	@Override
	public void receive(int interf, Object message) {
		if (message instanceof InitiateMessage) {
			// If we receive an InitiateMessage we save the id value coming with
			// it to the belonging interface id
			if (interfaceToNodeMap[interf] != -1) {
				return;
			}
			InitiateMessage msg = (InitiateMessage) message;
			interfaceToNodeMap[interf] = msg.getId();
			if (!this.isInitiated) {
				for (int i = 0; i < checkInterfaces(); i++) {
					send(i, new InitiateMessage(this.id));
				}
				this.isInitiated = true;
			}
		}

		if (message instanceof ByzantineMessage) {
			final ByzantineMessage receivedMsg = (ByzantineMessage) message;

			// drop messages that already contain this node's id
			if (contains(receivedMsg.getIdPath(), this.id)) {
				return;
			}

			// save message to list
			this.receivedMsgs.add(receivedMsg);

			// construct new message
			final int[] oldIdPath = receivedMsg.getIdPath();

			if (oldIdPath.length < getRounds()) {
				// construct newIdPath from oldIdPath + this node's id
				final int[] newIdPath = new int[oldIdPath.length + 1];
				System.arraycopy(oldIdPath, 0, newIdPath, 0, oldIdPath.length);
				newIdPath[newIdPath.length - 1] = this.id;

				boolean valueToSend = receivedMsg.getValue();
				boolean corruptValueToSend = receivedMsg.isCorrupt();

				// if this node is not loyal, revert the message's value
				if (!isLoyal) {
					valueToSend = !valueToSend;
					corruptValueToSend = true;
				}

				final ByzantineMessage msgToSend = new ByzantineMessage(
						newIdPath, valueToSend, corruptValueToSend);

				for (int i = 0; i < checkInterfaces(); i++) {
					// Message is sent to all nodes, except those that are
					// already in the message's path
					if (i != interf
							&& !contains(newIdPath, interfaceToNodeMap[i])) {
						send(i, msgToSend);
					}
				}
			} else {
				if (this.receivedMsgs.size() == totalMsgCount / (n - 1)) {
					buildMajority();
				}
			}
		}

	}

	private void buildMajority() {
		// Build tree
		ByzantineMessage shortestPathMsg = getMsgWithShortestPath();
		buildMajorityRecursively(shortestPathMsg);
		this.currentValue = shortestPathMsg.getMajority();
		updateCaption();
	}

	private void buildMajorityRecursively(final ByzantineMessage msgToStart) {

		buildParents(msgToStart);
		if (msgToStart.getChildren().size() > 0) {
			int trueCount = 0, falseCount = 0;
			for (ByzantineMessage msg : msgToStart.getChildren()) {
				buildMajorityRecursively(msg);
				if (msg.getMajority()) {
					trueCount++;
				} else {
					falseCount++;
				}
			}
			if (msgToStart.getValue()) {
				trueCount++;
			} else {
				falseCount++;
			}
			msgToStart.setMajority(trueCount >= falseCount);

		} else {
			msgToStart.setMajority(msgToStart.getValue());
		}

	}

	private void buildParents(final ByzantineMessage parentMessage) {
		for (final ByzantineMessage msg : receivedMsgs) {
			if (msg.getIdPath().length == parentMessage.getIdPath().length + 1) {
				if (parentMessage.containsChild(msg)) {
					break;
				}
				boolean isParent = true;
				for (int i = 0; i < parentMessage.getIdPath().length; i++) {
					if (parentMessage.getIdPath()[i] != msg.getIdPath()[i]) {
						isParent = false;
						break;
					}
				}
				if (isParent) {
					parentMessage.addChild(msg);
				}
			}
		}
	}

	// Helper Methods

	private boolean contains(int[] array, int value) {
		for (final int arrValue : array) {
			if (arrValue == value)
				return true;
		}
		return false;
	}

	private ByzantineMessage getMsgWithShortestPath() {
		int shortestPathLength = Integer.MAX_VALUE;
		ByzantineMessage shortestPathMsg = null;
		for (final ByzantineMessage msg : receivedMsgs) {
			if (msg.getIdPath().length < shortestPathLength) {
				shortestPathMsg = msg;
				shortestPathLength = msg.getIdPath().length;
			}
		}

		return shortestPathMsg;
	}

	/**
	 * Calculates the total message count for the set number of total nodes and
	 * disloyal ones.
	 * 
	 * @return
	 */
	private int getTotalMessageCount() {
		int sum = 0;
		for (int i = 0; i <= m; i++) {
			sum += (n - 1 - i) * (factorial(n - 1) / factorial(n - 1 - i));
		}

		return sum;
	}

	private int factorial(int n) {
		if (n == 0 || n == 1) {
			return 1;
		}
		int factorial = 1;
		for (int i = 1; i <= n; i++) {
			factorial *= i;
		}
		return factorial;
	}

	private int getRounds() {
		return m + 1;
	}

	private void updateCaption() {

		this.caption = Integer.toString(id) + " value: " + this.currentValue;
	}
}
