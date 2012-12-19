package messages.chandymisrahaas;

import java.awt.Color;

public class RequestMessage {
	
	int id;
	private final Color color = Color.RED;
	
	public RequestMessage(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "request";
	}

}