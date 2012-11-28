package messages.suzukikasami;

import java.awt.Color;

public class RequestMessage {
	@SuppressWarnings("unused")
	private final Color color = new Color(57, 166, 228);
	public final int sequenceNumber;
	public final int nodeId;
	public int ttl;

	public RequestMessage(int nodeId, int sequenceNumber, int ttl) {
		this.nodeId = nodeId;
		this.sequenceNumber = sequenceNumber;
		this.ttl = ttl;
	}

	@Override
	public String toString() {
		return "request#"+sequenceNumber+" from "+nodeId+"(ttl="+ttl+")";
	}
}
