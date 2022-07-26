package de.daedalic.eba;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class EbaObjectSprite extends EbaInteractionSprite implements EbaInterfaceHasBase {

	// Grundlinie:
	private int x1;

	private int y1;

	private int x2;

	private int y2;

	public EbaObjectSprite(BufferedImage myBufferedImage, double x, double y, int baseLineX1, int baseLineY1, int baseLineX2, int baseLineY2, boolean aktiv) {
		super(myBufferedImage, x, y, 0, 0, "", "", 0, 0, 0, 0, aktiv);
		x1 = baseLineX1;
		y1 = baseLineY1;
		x2 = baseLineX2;
		y2 = baseLineY2;
	}

	public void render(Graphics2D g, int x, int y) { //nur zum Debuggen
		if (this.isActive()) {
			g.drawImage(getImage(), x, y, null);

			//g.drawRect(x, y, width, height);		//nur zum Debuggen
			//g.drawLine(x1, y1, x2, y2);				//nur zum Debuggen
		}
	}

	protected void updateAnimation() {
		super.updateAnimation();

		// Nach Animation deaktivieren:
		setActive(isAnimate());
	}

	public void setWalkToX(int x) {
		walkToX = x;
	}

	public void setWalkToY(int y) {
		walkToY = y;
	}

	public void setStandbyBlickrichtung(String standbyBlickrichtung) {
		myStandbyBlickrichtung = standbyBlickrichtung;
	}

	public double getBaseX() {
		return getX() + (width / 2);
	}

	public double getBaseY() {
		return getY() + height;
	}

	public double getBaseY(double x) {
		int deltaY = (y2 - y1);
		int deltaX = (x2 - x1);
		double ze = (double) deltaY / (double) deltaX;
		double deltaXedna = (x - ((double) x1));
		double ergebnis = ((ze * deltaXedna) + (double) y1);
		return ergebnis;
	}

	public boolean hasInteraction() {
		return (getDataID() != null);
	}

}
