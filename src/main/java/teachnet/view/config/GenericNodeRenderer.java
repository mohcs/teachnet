package teachnet.view.config;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import teachnet.algorithm.BasicAlgorithm;
import teachnet.util.AlgorithmInspector;

public class GenericNodeRenderer extends NodeRenderer {
	private static final int RADIUS = 10;

	public void paint(Graphics g, int id) throws RendererException {
		Point position = this.view.getNodePosition(id);
		BasicAlgorithm algo = this.view.getEngine().getNodes()[id].getAlgorithm(0);

		Color color = Color.white;
		try {
			color = (Color) AlgorithmInspector.getMember(algo, "color");
		} catch (NoSuchFieldException ex) {
		} catch (ClassCastException ex) {
		}

		String caption = null;
		try {
			caption = (String) AlgorithmInspector.getMember(algo, "caption");
		} catch (NoSuchFieldException ex) {
		} catch (ClassCastException ex) {
		}

		Shape shape = null;
		try {
			shape = (Shape) AlgorithmInspector.getMember(algo, "shape");
		} catch (NoSuchFieldException ex) {
		}

		if (shape == null)
			shape = Shape.CIRCLE;

		shape.draw(g, position, color, RADIUS);

		if (caption != null)
			g.drawString(caption, position.x + 15, position.y + 5);
	}
}