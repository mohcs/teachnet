package algorithms;

import java.awt.Color;

import messages.suzukikasami.RequestMessage;
import messages.suzukikasami.Token;

import teachnet.algorithm.BasicAlgorithm;

/**
 * Implementieren Sie die verbesserte Token Ring-Loesung (Suzuki und Kasami,
 * 1985) fuer den gegenseitigen Ausschluss mit Hilfe des Simulationsframeworks.
 * 
 * Veranlassen Sie in (pseudo-)zufaelligen Abstaenden einen zufaellig
 * ausgewaehlten Knoten dazu, die Ressource anzufordern.
 * 
 * Waehlen Sie die Zufallswerte fuer den Abstand sinnvoll, das heisst, es sollte
 * sowohl Zeiten in der Simulation geben, zu denen wenige oder gar keine
 * Anforderungen vorliegen, als auch Zeiten, zu denen sehr viele Anforderungen
 * vorliegen.
 * 
 * @author Tim Strehlow, Bjoern Stabel, Friedrich Maiwald
 */
public class SuzukiKasami extends BasicAlgorithm {
	@SuppressWarnings("unused")
	private Color color = null; // Faerbt Knoten ein
	@SuppressWarnings("unused")
	private int markInterface = -1; // Hebt Kante n hervor
	@SuppressWarnings("unused")
	private String caption; // Beschriftung des Knotens

	int id;
	int networkSize;

	// Die letzte Sequenznummer die der Knoten benutzt hat
	int sequenceNumber = 0;
	// Enthaelt das Token, waehrend der Knoten es innehat
	Token token = null;
	// Die Liste der hoechsten Sequenznummern aller Prozesse
	int[] sequenceNumbers;

	// Wahr, wenn Zugriff gewuenscht ist.
	boolean wantAccess;

	@Override
	public void setup(java.util.Map<String, Object> config) {
		// Konfiguration auslesen
		id = (Integer) config.get("node.id");
		networkSize = (Integer) config.get("network.size");

		// Liste R_i initialisieren
		sequenceNumbers = new int[networkSize];

		// Das Token startet (arbitraer) bei Knoten 0.
		if (id == 0)
			token = new Token(networkSize);

		update();
	}

	@Override
	public void initiate() {
		// Anforderung senden
		requestResource();
	}

	public void receive(int interf, Object message) {
		try {
			if (message instanceof Token) {
				token = (Token) message;

				// Wenn Zugriff gewuenscht, zugreifen
				if (wantAccess) {
					doAccess();
				}

				// Alle Prozesse an die Queue anfuegen...
				//TODO: sort by sequence number
				for (int requesterId = 0; requesterId < sequenceNumbers.length; ++requesterId) {
					// ...wenn dieser Prozess von noch ausstehenden Anfragen weiss.
					if (sequenceNumbers[requesterId] <= token.sequenceNumbers[requesterId])
						continue;

					token.sequenceNumbers[requesterId] = sequenceNumbers[requesterId];

					// Doppelungen verhindern
					if (token.waitingProcesses.contains(requesterId))
						continue;

					token.waitingProcesses.add(requesterId);
				}

				// Alle Anfragen dieses Prozesses als bearbeitet markieren...
				token.sequenceNumbers[id] = sequenceNumbers[id];

				// ...und ihn aus der Queue entfernen
				token.waitingProcesses.remove(id);

				// Wenn die Queue leer ist, verbleibt das Token bei diesem Knoten
				if (token.waitingProcesses.isEmpty())
					return;

				// Ansonsten wird das Token an den naechsten Prozess weitergeschickt
				passToken();
			}
			else if (message instanceof RequestMessage) {
				final RequestMessage resourceRequestMessage = (RequestMessage) message;

				final int requesterId = resourceRequestMessage.nodeId;

				// Wissen aus dem Request fuer das naechste vorbeikommende Tokens speichern
				if (sequenceNumbers[requesterId] <= resourceRequestMessage.sequenceNumber) {
					sequenceNumbers[requesterId] = resourceRequestMessage.sequenceNumber;
				}

				// Pruefen, ob wir das Token haben
				if (hasToken()) {
					// Wenn im Token schon die selbe oder eine hoehere Sequenznummer als im Request steht, ist nichts zu tun. 
					if (resourceRequestMessage.sequenceNumber <= token.sequenceNumbers[requesterId])
						return;

					// Anfrage zur Queue hinzufuegen...
					token.waitingProcesses.add(requesterId);

					// ...und Token losschicken
					passToken();
					return;
				}

				// Wenn die time to live ueberschritten ist, wird die Nachricht nicht weitergeschickt. 
				if (--resourceRequestMessage.ttl == 0)
					return;

				// Ansonsten leiten wir die Anfrage einfach weiter.
				send(0, message);
			}
		}
		finally {
			update();
		}
	}

	public void requestResource() {
		// Dieser Prozess will zugreifen
		wantAccess = true;
		sequenceNumbers[id] = ++sequenceNumber;

		// Wenn dieser Prozess das Token innehat...
		if (hasToken()) {
			// ...Anfrage direkt bearbeiten.
			doAccess();
			return;
		}

		// Nachricht mit der naechsten Sequenznummer und TTL =  n-1 schicken
		send(0, new RequestMessage(id, sequenceNumber, networkSize - 1));
		update();
	}

	public void doAccess() {
		wantAccess = false;
		update();
	}

	public boolean hasToken() {
		return token != null;
	}

	public void passToken() {
		// Token auslesen
		final Token token = this.token;

		// Token vor dem Senden auf null setzen
		this.token = null;

		// Token senden
		send(0, token);

		update();
	}

	public static StringBuilder arrayToString(int[] sequenceNumbers) {
		final StringBuilder sb = new StringBuilder();
		for (int e : sequenceNumbers) {
			if (sb.length() != 0)
				sb.append(", ");
			sb.append(e);
		}
		return sb;
	}

	public void update() {
		StringBuilder sb = new StringBuilder();

		sb.append(id);
		sb.append("(seq=");
		sb.append(sequenceNumber);
		sb.append(",R_i=[");
		sb.append(arrayToString(sequenceNumbers));
		sb.append("])");
		if (hasToken()) {
			if (wantAccess) {
				color = Color.WHITE;
			}
			else {
				color = new Color(240, 124, 0);
			}
			sb.append(", ");
			sb.append(token);
		}
		else if (wantAccess) {
			color = new Color(57, 166, 228);
		}
		else {
			color = Color.BLACK;
		}

		caption = sb.toString();
	}
}
