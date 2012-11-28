package teachnet.view.config;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

public enum Shape {
	CIRCLE, RECTANGLE, RHOMBUS;

	public void draw(Graphics g, Point position, Color color, int radius) {
		final Graphics2D gr = (Graphics2D) g;
		gr.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		gr.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		switch (this) {
		case RECTANGLE:
			g.setColor(color);
			g.fillRect(position.x - radius, position.y - radius, radius * 2, radius * 2);

			g.setColor(Color.BLACK);
			g.drawRect(position.x - radius, position.y - radius, radius * 2, radius * 2);
			break;

		case RHOMBUS:
			g.setColor(color);
			final int[] xPoints = { position.x, position.x + radius, position.x, position.x - radius };
			final int[] yPoints = { position.y + radius, position.y, position.y - radius, position.y };
			g.fillPolygon(xPoints, yPoints, 4);

			g.setColor(Color.BLACK);
			g.drawPolygon(xPoints, yPoints, 4);
			break;

		default:
			g.setColor(color);
			g.fillOval(position.x - radius, position.y - radius, radius * 2, radius * 2);

			gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(Color.BLACK);
			g.drawOval(position.x - radius, position.y - radius, radius * 2, radius * 2);
		}
	}
}
