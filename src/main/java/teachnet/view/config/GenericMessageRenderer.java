package teachnet.view.config;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.reflect.Field;

import teachnet.view.MessageStatus;

public class GenericMessageRenderer extends MessageRenderer {
	private static final int RADIUS = 5;

	public void paint(Graphics g, MessageStatus status) throws RendererException {
		Point position = getPosition(status);

		Color color = null;
		String caption = null;
		Shape shape = null;

		if (status.payload instanceof Boolean) {
			color = (Boolean) status.payload ? Color.GREEN : Color.RED;
		}
		else if (status.payload instanceof Color) {
			color = (Color) status.payload;
		} else if (status.payload instanceof String) {
			caption = (String) status.payload;
		} else {
			caption = status.payload.toString();
			try {
				color = (Color) getMember(status.payload, "color");
			} catch (NoSuchFieldException ex) {
			}

			try {
				shape = (Shape) getMember(status.payload, "shape");
			} catch (NoSuchFieldException ex) {
			}
		}

		if (color == null) {
			color = Color.YELLOW;
		}
		if (shape == null) {
			shape = Shape.CIRCLE;
		}

		shape.draw(g, position, color, RADIUS);

		if (caption != null)
			g.drawString(caption, position.x + RADIUS + 2, position.y + RADIUS);
	}

	// These should be changed inside the AlgorithmInspector class.
	public static Object getMember(Object algo, String name)
			throws NoSuchFieldException {
		return getMember(algo.getClass(), algo, name);
	}

	public static Object getMember(Class<?> clazz, Object algo, String name)
			throws NoSuchFieldException {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);

			return field.get(algo);
		} catch (IllegalAccessException ex) {
			return null;
		} catch (SecurityException ex) {
		}
		return null;
	}
}