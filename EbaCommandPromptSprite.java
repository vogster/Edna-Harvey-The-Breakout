package de.daedalic.eba;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;

import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.GameFontManager;
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.Timer;
import com.golden.gamedev.util.ImageUtil;

@SuppressWarnings("serial")
public class EbaCommandPromptSprite extends Sprite {

	private String myActiveFont;
	private String myInactiveFont;
	private GameFontManager myGameFontManager;
	private Timer myTimer = new Timer(300);

	private String myText;

	public EbaCommandPromptSprite(String activeFont, String inactiveFont, GameFontManager fontManager, double x, double y) {
		super(ImageUtil.createImage(1, 1, Transparency.TRANSLUCENT), x, y);
		myActiveFont = activeFont;
		myInactiveFont = inactiveFont;
		myGameFontManager = fontManager;
		myTimer.setActive(false);
	}

    public void update(long elapsedTime) {
        if (myTimer.action(elapsedTime)) {
    		GameFont myFont;
    		Graphics2D g;

        	myTimer.setActive(false);

        	myFont = myGameFontManager.getFont("S" + myInactiveFont);
			g = getImage().createGraphics();
			for (int i = 1; i < 6; i++) {
				for (int j = 1; j < 6; j++) {
					myFont.drawString(g, myText, i, j);
				}
			}
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			myGameFontManager.getFont("F" + myInactiveFont).drawString(g, myText, 3, 3);
			g.dispose();
        }
    }

	public void change(String text, boolean activate) {
		GameFont myFont;
		Graphics2D g;

		setImage(ImageUtil.createImage(myGameFontManager.getFont("F" + myInactiveFont).getWidth(text) + 6, myGameFontManager.getFont("F" + myInactiveFont).getHeight() + 6, Transparency.TRANSLUCENT));

		if (activate) {
			myFont = myGameFontManager.getFont("S" + myActiveFont);
			g = getImage().createGraphics();
			for (int i = 1; i < 6; i++) {
				for (int j = 1; j < 6; j++) {
					myFont.drawString(g, text, i, j);
				}
			}
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			myGameFontManager.getFont("F" + myActiveFont).drawString(g, text, 3, 3);
			g.dispose();

			myText = text;

			myTimer.setDelay(300);
		} else {
			myFont = myGameFontManager.getFont("S" + myInactiveFont);
			g = getImage().createGraphics();
			for (int i = 1; i < 6; i++) {
				for (int j = 1; j < 6; j++) {
					myFont.drawString(g, text, i, j);
				}
			}
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			myGameFontManager.getFont("F" + myInactiveFont).drawString(g, text, 3, 3);
			g.dispose();
		}
		myTimer.setActive(activate);
	}

}
