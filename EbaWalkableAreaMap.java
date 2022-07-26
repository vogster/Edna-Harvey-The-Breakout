package de.daedalic.eba;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

@SuppressWarnings("serial")
public class EbaWalkableAreaMap extends Stack<int[]> {

	boolean[][] walkableAreaArray;

	int[][] grid = new int[800][600];

	Queue<Integer> myQueue = new LinkedList<Integer>();

	Line2D myLine2D;
	Rectangle myRectangle;

	public EbaWalkableAreaMap(String wamFileName) {
		try {
			walkableAreaArray = (boolean[][]) new ObjectInputStream(
					new FileInputStream(wamFileName)).readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void findWay(int fromX, int fromY, int toX, int toY) {

		Point myPoint;
		double myDistance;
		double minDistance;

		clear();
		myQueue.clear();
		for (int x = 0; x < 800; x++) {
			Arrays.fill(grid[x], -1);
		}

		if (!walkableAreaArray[fromX][fromY]) {
			myPoint = new Point(fromX, fromY);
			minDistance = Double.MAX_VALUE;
			for (int x = 0; x < 800; x++) {
				for (int y = 0; y < 600; y++) {
					if (walkableAreaArray[x][y]) {
						myDistance = myPoint.distance(x, y);
						if (minDistance > myDistance) {
							minDistance = myDistance;
							fromX = x;
							fromY = y;
						}
					}
				}
			}
		}

		if (!walkableAreaArray[toX][toY]) {
			myPoint = new Point(toX, toY);
			minDistance = Double.MAX_VALUE;
			for (int x = 0; x < 800; x++) {
				for (int y = 0; y < 600; y++) {
					if (walkableAreaArray[x][y]) {
						myDistance = myPoint.distance(x, y);
						if (minDistance > myDistance) {
							minDistance = myDistance;
							toX = x;
							toY = y;
						}
					}
				}
			}
		}

		boolean notFound = true;
		int n;
		int x = fromX;
		int y = fromY;

		myQueue.add(x);
		myQueue.add(y);

		grid[x][y] = 0;

		while (notFound) {
			x = (Integer) myQueue.remove();
			y = (Integer) myQueue.remove();

			if ((x == toX) && (y == toY)) {
				notFound = false;
			} else {
				n = grid[x][y] + 2;
				y--;
				if ((x >= 0) && (x < 800) && (y >= 0) && (y < 600)
						&& (grid[x][y] == -1) && (walkableAreaArray[x][y])) {
					grid[x][y] = n;
					myQueue.add(x);
					myQueue.add(y);
				}

				x++;
				y++;
				if ((x >= 0) && (x < 800) && (y >= 0) && (y < 600)
						&& (grid[x][y] == -1) && (walkableAreaArray[x][y])) {
					grid[x][y] = n;
					myQueue.add(x);
					myQueue.add(y);
				}

				y++;
				x--;
				if ((x >= 0) && (x < 800) && (y >= 0) && (y < 600)
						&& (grid[x][y] == -1) && (walkableAreaArray[x][y])) {
					grid[x][y] = n;
					myQueue.add(x);
					myQueue.add(y);
				}

				y--;
				x--;
				if ((x >= 0) && (x < 800) && (y >= 0) && (y < 600)
						&& (grid[x][y] == -1) && (walkableAreaArray[x][y])) {
					grid[x][y] = n;
					myQueue.add(x);
					myQueue.add(y);
				}

				n++;

				y--;
				if ((x >= 0) && (x < 800) && (y >= 0) && (y < 600)
						&& (grid[x][y] == -1) && (walkableAreaArray[x][y])) {
					grid[x][y] = n;
					myQueue.add(x);
					myQueue.add(y);
				}

				x++;
				x++;
				if ((x >= 0) && (x < 800) && (y >= 0) && (y < 600)
						&& (grid[x][y] == -1) && (walkableAreaArray[x][y])) {
					grid[x][y] = n;
					myQueue.add(x);
					myQueue.add(y);
				}
				y++;
				y++;
				if ((x >= 0) && (x < 800) && (y >= 0) && (y < 600)
						&& (grid[x][y] == -1) && (walkableAreaArray[x][y])) {
					grid[x][y] = n;
					myQueue.add(x);
					myQueue.add(y);
				}
				x--;
				x--;
				if ((x >= 0) && (x < 800) && (y >= 0) && (y < 600)
						&& (grid[x][y] == -1) && (walkableAreaArray[x][y])) {

					grid[x][y] = n;
					myQueue.add(x);
					myQueue.add(y);
				}
			}
		}

		// Grid ist jetzt für jeden relevanten (!) Punkt mit
		// ganzahligen Entfernungsangaben zum Startpunkt gefüllt.

		// DER RÜCKWEG:

		// Vom Zielpunkt aus in Richtung des niedrigsten umgebenden
		// Wertes ungleich -1 (!) bewegen. Die Richtung der Bewegung
		// wird durch dX und dY definiert bzw. festgehalten.

		// Dann den Zielpunkt auf den soeben betretenen Punkt setzen.
		// Vom Zielpunkt aus in Richtung des niedrigsten umgebenden
		// Wertes ungleich -1 (!) weiterbewegen. Wenn die Richtung der
		// Bewegung in dX und/oder dY abweicht, den Zielpunkt auf den
		// Stack legen, und dX und dY neu festhalten.

		// Den letzten Absatz wiederholen, bis der Zielpunkt der
		// Startpunkt ist, bzw. bis der Zielpunkt den Wert 0 hat.
		// Der Startpunkt darf nicht auf den Stack gelegt werden!

		int deltaX = 0; // alte Bewegungsrichtung X
		int deltaY = 0; // alte Bewegungsrichtung Y

		while ((fromX != toX) || (fromY != toY)) {

			x = toX; // Zielpunkt X
			y = toY; // Zielpunkt Y

			n = grid[x][y]; // Wert am Zielpunkt

			int dX = 0; // neue Bewegungsrichtung X
			int dY = 0; // neue Bewegungsrichtung Y

			y--;
			if ((x >= 0) && (x < 800) && (y >= 0) && (y < 600)
					&& (grid[x][y] != -1) && (grid[x][y] < n)) {
				n = grid[x][y];
				dX = 0;
				dY = -1;
			}

			x++;
			y++;
			if ((x >= 0) && (x < 800) && (y >= 0) && (y < 600)
					&& (grid[x][y] != -1) && (grid[x][y] < n)) {
				n = grid[x][y];
				dX = 1;
				dY = 0;
			}

			y++;
			x--;
			if ((x >= 0) && (x < 800) && (y >= 0) && (y < 600)
					&& (grid[x][y] != -1) && (grid[x][y] < n)) {
				n = grid[x][y];
				dX = 0;
				dY = 1;
			}

			y--;
			x--;
			if ((x >= 0) && (x < 800) && (y >= 0) && (y < 600)
					&& (grid[x][y] != -1) && (grid[x][y] < n)) {
				n = grid[x][y];
				dX = -1;
				dY = 0;
			}

			y--;
			if ((x >= 0) && (x < 800) && (y >= 0) && (y < 600)
					&& (grid[x][y] != -1) && (grid[x][y] < n)) {
				n = grid[x][y];
				dX = -1;
				dY = -1;
			}

			x++;
			x++;
			if ((x >= 0) && (x < 800) && (y >= 0) && (y < 600)
					&& (grid[x][y] != -1) && (grid[x][y] < n)) {
				n = grid[x][y];
				dX = 1;
				dY = -1;
			}

			y++;
			y++;
			if ((x >= 0) && (x < 800) && (y >= 0) && (y < 600)
					&& (grid[x][y] != -1) && (grid[x][y] < n)) {
				n = grid[x][y];
				dX = 1;
				dY = 1;
			}

			x--;
			x--;
			if ((x >= 0) && (x < 800) && (y >= 0) && (y < 600)
					&& (grid[x][y] != -1) && (grid[x][y] < n)) {
				n = grid[x][y];
				dX = -1;
				dY = 1;
			}

			// Hier steht in dX und dY die "neue" Richtung.
			// Wenn sich die Richtung geändert hat, muss der
			// (alte) Zielpunkt auf den Stack gelegt werden:

			if ((dX != deltaX) || (dY != deltaY)) {
				push(new int[] { toX, toY });
				deltaX = dX; // alte Richtung X = neue Richtung X
				deltaY = dY; // alte Richtung Y = neue Richtung Y
			}

			// Dann muss immer (!) der Zielpunkt auf den neuen
			// Punkt gesetzt werden, damit die Schleife funktioniert:

			toX = toX + dX;
			toY = toY + dY;
		}

		// Falls der Stack jetzt leer ist, müssen Startpunkt
		// und Zielpunkt identisch gewesen sein. Aber nix gut!

		if (empty()) {
			push(new int[] { fromX, fromY });
		}

		// Der Stack enthält jetzt alle Wegpunkte.
		// Aber dieser Weg kennt nur acht Richtungen.
		// Suchen wir also alle unnötigen Richtungswechsel:

		int stackSize = -1;

		while (stackSize != size()) {
			stackSize = size();

			// Wir greifen uns je drei hintereinander liegende Wegpunkte:
			for(int i=2; i < size(); i++) {
				fromX = ((int[])get(i-2))[0];
				fromY = ((int[])get(i-2))[1];
				deltaX = ((int[])get(i-1))[0];
				deltaY = ((int[])get(i-1))[1];
				toX = ((int[])get(i))[0];
				toY = ((int[])get(i))[1];

				myLine2D = new Line2D.Double(fromX, fromY, toX, toY); // Linie, die einen Punkt überspringt.
				myRectangle = myLine2D.getBounds();                   // Das diese Linie umgebende Rechteck.

				notFound = true;

				// Unbegehbare Punkte entlang der Linie suchen:
				for (x = myRectangle.x; x < (myRectangle.x + myRectangle.width); x++) {
					for (y = myRectangle.y; y < (myRectangle.y + myRectangle.height); y++) {
						if (myLine2D.ptSegDist(x, y) < 0.8) {
							notFound = notFound && walkableAreaArray[x][y];
						}
					}
				}

				// Wenn keine gefunden, den übersprungenen Punkt entfernen:
				if (notFound) {
					remove(i-1);
				}
			}
		}
	}
}
