package de.daedalic.eba;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;

import com.golden.gamedev.engine.BaseAudioRenderer;
import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.GameFontManager;
import com.golden.gamedev.object.sprite.VolatileSprite;
import com.golden.gamedev.util.ImageUtil;

@SuppressWarnings("serial")
public class EbaTextSprite extends VolatileSprite{

	private boolean talkingWithSound = false;
	private String myAudiopfad = "";
	private EbaGameObject myEbaGameObject;
	
	

	public EbaTextSprite(String text, String font, GameFontManager fontManager, double x, double y, int milliseconds) {
		super(ImageUtil.splitImages(ImageUtil.createImage(fontManager.getFont("F" + font).getWidth(text) + 6, fontManager.getFont("F" + font).getHeight() + 6, Transparency.TRANSLUCENT), 1, 1), x - 3 - ((fontManager.getFont("F" + font).getWidth(text)) / 2), y - 6 - fontManager.getFont("F" + font).getHeight());
		//System.out.println("Der Satz '"+text+"' hat "+wordCount(text)+" Worte.");
	
		GameFont myFont = fontManager.getFont("S" + font);
		Graphics2D g = getImage().createGraphics();
		for (int i = 1; i < 6; i++) {
			for (int j = 1; j < 6; j++) {
				myFont.drawString(g, text, i, j);
			}
		}
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		fontManager.getFont("F" + font).drawString(g, text, 3, 3);
		g.dispose();
		getAnimationTimer().setDelay(milliseconds);
	}

	public EbaTextSprite(String message, String[] text, int longestRow, String font, GameFontManager fontManager, double x, double y, int milliseconds) {
		super(ImageUtil.splitImages(ImageUtil.createImage(fontManager.getFont("F" + font).getWidth(text[longestRow]) + 6, fontManager.getFont("F" + font).getHeight()*text.length + 6, Transparency.TRANSLUCENT), 1, 1), x - 3 - ((fontManager.getFont("F" + font).getWidth(text[longestRow])) / 2), y - 6 - fontManager.getFont("F" + font).getHeight()*text.length + 20 * (text.length-1));
		GameFont myFont = fontManager.getFont("S" + font);
		Graphics2D g = getImage().createGraphics();
		for (int m = 0; m < text.length; m++){
			for (int i = 1; i < 6; i++) {
				for (int j = 1; j < 6; j++) {
					if (m == longestRow){
						myFont.drawString(g, text[m], i, j + (20*m));
					} else {
						myFont.drawString(g, text[m], (i + (((fontManager.getFont("F" + font).getWidth(text[longestRow])-fontManager.getFont("F" + font).getWidth(text[m]))/2))), j + (20*m));
					}
				}
			}
		}
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		for (int m = 0; m < text.length; m++){
			if (m == longestRow){
				fontManager.getFont("F" + font).drawString(g, text[m], 3, 3+ (20*m));
			} else {
				fontManager.getFont("F" + font).drawString(g, text[m], (3+ (((fontManager.getFont("F" + font).getWidth(text[longestRow])-fontManager.getFont("F" + font).getWidth(text[m]))/2))), 3+ (20*m));
			}
		}
		g.dispose();
		getAnimationTimer().setDelay(milliseconds);
	}
	
	public EbaTextSprite(String text, String font, GameFontManager fontManager, double x, double y, String audiopfad, EbaGameObject GameObject) {
		super(ImageUtil.splitImages(ImageUtil.createImage(fontManager.getFont("F" + font).getWidth(text) + 6, fontManager.getFont("F" + font).getHeight() + 6, Transparency.TRANSLUCENT), 1, 1), x - 3 - ((fontManager.getFont("F" + font).getWidth(text)) / 2), y - 6 - fontManager.getFont("F" + font).getHeight());
				
		myAudiopfad = audiopfad;
		myEbaGameObject = GameObject;
		talkingWithSound = true;
		GameFont myFont = fontManager.getFont("S" + font);
		Graphics2D g = getImage().createGraphics();
		for (int i = 1; i < 6; i++) {
			for (int j = 1; j < 6; j++) {
				myFont.drawString(g, text, i, j);
			}
		}
		setDataID(text);
		setImmutable(true);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		fontManager.getFont("F" + font).drawString(g, text, 3, 3);
		g.dispose();
		getAnimationTimer().setDelay(30000);
	}
	
	public EbaTextSprite(String message, String[] text, int longestRow, String font, GameFontManager fontManager, double x, double y, String audiopfad, EbaGameObject GameObject) {
		super(ImageUtil.splitImages(ImageUtil.createImage(fontManager.getFont("F" + font).getWidth(text[longestRow]) + 6, fontManager.getFont("F" + font).getHeight()*text.length + 6, Transparency.TRANSLUCENT), 1, 1), x - 3 - ((fontManager.getFont("F" + font).getWidth(text[longestRow])) / 2), y - 6 - fontManager.getFont("F" + font).getHeight()*text.length + 20 * (text.length-1));

		
		
		myAudiopfad = audiopfad;
		myEbaGameObject = GameObject;
		talkingWithSound = true;
		GameFont myFont = fontManager.getFont("S" + font);
		Graphics2D g = getImage().createGraphics();
		for (int m = 0; m < text.length; m++){
			for (int i = 1; i < 6; i++) {
				for (int j = 1; j < 6; j++) {
					if (m == longestRow){
						myFont.drawString(g, text[m], i, j + (20*m));
					} else {
						myFont.drawString(g, text[m], (i + (((fontManager.getFont("F" + font).getWidth(text[longestRow])-fontManager.getFont("F" + font).getWidth(text[m]))/2))), j + (20*m));
					}
				}
			}
		}
		setDataID(message);
		setImmutable(true);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		for (int m = 0; m < text.length; m++){
			if (m == longestRow){
				fontManager.getFont("F" + font).drawString(g, text[m], 3, 3+ (20*m));
			} else {
				fontManager.getFont("F" + font).drawString(g, text[m], (3+ (((fontManager.getFont("F" + font).getWidth(text[longestRow])-fontManager.getFont("F" + font).getWidth(text[m]))/2))), 3+ (20*m));
			}
		}
		

		g.dispose();
		getAnimationTimer().setDelay(30000);
	}

	public void update(long elapsedTime) {
		super.update(elapsedTime);
		if (talkingWithSound) {

			BaseAudioRenderer myBaseAudioRenderer = myEbaGameObject.myEbaGameEngine.bsSound.getAudioRenderer(myAudiopfad);
			if( (myBaseAudioRenderer == null) || (myBaseAudioRenderer.getStatus() != 1)){

				setImmutable(false);
				setActive(false);

				talkingWithSound = false;
			}
		}
	}
	
	public int wordCount(String text) {
		int wordCount=0;
		boolean lastCharWasSpace = true;
		for (int i = 1; i < text.length(); i++) {
			if (lastCharWasSpace){
				if (!text.substring(i, i+1).equals(" ")){
					wordCount = wordCount+1;
					lastCharWasSpace = false;
				}
			} else {
				if (text.substring(i, i+1).equals(" ")){	
					lastCharWasSpace = true;
				}			
			}
		}
		return wordCount;
	}
	

}
