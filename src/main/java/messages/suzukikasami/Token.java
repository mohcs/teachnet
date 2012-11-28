package messages.suzukikasami;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;

import algorithms.SuzukiKasami;

public class Token {
	@SuppressWarnings("unused")
	private final Color color = new Color(240, 124, 0);

	public final Queue<Integer> waitingProcesses = new LinkedList<Integer>();
	public final int[] sequenceNumbers;

	public Token(int networkSize) {
		sequenceNumbers = new int[networkSize];
	}

	@Override
	public String toString() {
		StringBuilder sb = SuzukiKasami.arrayToString(sequenceNumbers);
		return "token(Q="+waitingProcesses+",L=["+sb+"])";
	}
}
