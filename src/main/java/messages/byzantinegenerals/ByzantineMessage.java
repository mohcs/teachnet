package messages.byzantinegenerals;

import java.util.ArrayList;
import java.util.List;

public class ByzantineMessage {

	private int[] idPath;

	private boolean value;

	private boolean isCorrupt;

	private List<ByzantineMessage> children;

	private boolean majority;

	public void addChild(ByzantineMessage msg) {
		children.add(msg);
	}

	public List<ByzantineMessage> getChildren() {
		return children;
	}

	public boolean getMajority() {
		return majority;
	}

	public void setMajority(boolean majority) {
		this.majority = majority;
	}

	public int[] getIdPath() {
		return idPath;
	}

	public boolean getValue() {
		return value;
	}

	public boolean isCorrupt() {
		return isCorrupt;
	}

	public ByzantineMessage(final int[] idPath, final boolean value,
			final boolean isCorrupt) {
		this.idPath = idPath;
		this.value = value;
		this.isCorrupt = isCorrupt;
		this.children = new ArrayList<ByzantineMessage>();
	}

	@Override
	public String toString() {
		String stringPresentation = new String();
		for (int i = 0; i < idPath.length; i++) {
			if (i != 0) {
				stringPresentation += " : ";
			}
			stringPresentation += idPath[i];
		}
		stringPresentation += " -> " + value;
		return stringPresentation;
	}

}
