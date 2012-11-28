package teachnet.view.config;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import teachnet.BroadcastDomain;
import teachnet.BroadcastInterface;
import teachnet.algorithm.BasicAlgorithm;
import teachnet.util.AlgorithmInspector;

public class GenericEdgeRenderer extends EdgeRenderer
{
	public void paint(Graphics g, BroadcastDomain d) throws RendererException {
		Graphics2D gr = (Graphics2D)g;
		gr.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		gr.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (d.interfaces.size() == 2) {
			int node1 = ((BroadcastInterface)d.interfaces.get(0)).node.id;
			int interf1 = ((BroadcastInterface)d.interfaces.get(0)).id;
			int node2 = ((BroadcastInterface)d.interfaces.get(1)).node.id;
			int interf2 = ((BroadcastInterface)d.interfaces.get(1)).id;

			BasicAlgorithm algo1 = this.view.getEngine().getNodes()[node1].getAlgorithm(0);
			BasicAlgorithm algo2 = this.view.getEngine().getNodes()[node2].getAlgorithm(0);

			int stroke = 1;
			try {
				Integer m1 = (Integer)AlgorithmInspector.getMember(algo1, "markInterface");
				Integer m2 = (Integer)AlgorithmInspector.getMember(algo2, "markInterface");

				if (m1.intValue() == interf1)
					stroke += 3;
				if (m2.intValue() == interf2)
					stroke += 3;
			} catch (NoSuchFieldException ex) {
			} catch (ClassCastException ex) {
			}
			gr.setStroke(new BasicStroke(stroke));
			super.paint(gr, d);
		}
		Point p1;
		if (d.interfaces.size() > 2) {
			p1 = this.view.getDomainPosition(d.id);

			for (BroadcastInterface i : d.interfaces) {
				int node = i.node.id;
				int interf = i.id;
				BasicAlgorithm algo = this.view.getEngine().getNodes()[node].getAlgorithm(0);
				int stroke = 1;
				try {
					Integer m = (Integer)AlgorithmInspector.getMember(algo, "markInterface");
					if (m.intValue() == interf)
						stroke += 3;
				} catch (NoSuchFieldException ex) {
				} catch (ClassCastException ex) {
				}
				gr.setStroke(new BasicStroke(stroke));

				Point p2 = this.view.getNodePosition(i.node.id);
				g.drawLine(p1.x, p1.y, p2.x, p2.y);
			}
		}
	}
}