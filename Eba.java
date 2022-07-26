package de.daedalic.eba;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import com.golden.gamedev.GameLoader;

public class Eba {

	static MediaTracker myMediaTracker;

	public static void main(String[] args) {
		boolean fullScreenMode = true;
//		boolean fullScreenMode = false;
//		boolean bufferStrategy = true;
		boolean bufferStrategy = false;
        for (String arg: args) {
        	if (arg.length() > 0) {
	            switch (arg.charAt(0)) {
		            case 'w':  fullScreenMode = false; break;
		            case 'W':  fullScreenMode = false; break;
		            case 'b':  bufferStrategy = true; break;
		            case 'B':  bufferStrategy = true; break;
	        	}
            }
        }		
		EbaFileIO.ladeContent(0); //Startdatenbank als WorkingDB bereitstellen

		GameLoader myGameLoader = new GameLoader();
		myMediaTracker = new MediaTracker(myGameLoader);
		myGameLoader.MINIMUM_VERSION = "1.6";
		myGameLoader.setup(new EbaGameEngine(), new Dimension(800, 600), fullScreenMode, bufferStrategy);
		myGameLoader.start();
	}

	static BufferedImage getBackgroundImage(String path){
		Image myImage = Toolkit.getDefaultToolkit().getImage(path);
		myMediaTracker.addImage(myImage,0);
		try {
			myMediaTracker.waitForAll();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myMediaTracker.removeImage(myImage);
		BufferedImage myBufferedImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		Graphics2D myGraphics2D = myBufferedImage.createGraphics();
		myGraphics2D.drawImage(myImage,0,0,null);
		myGraphics2D.dispose();
		myImage.flush();
		return myBufferedImage;
	}

	static BufferedImage getImage(String path){
		Image myImage = Toolkit.getDefaultToolkit().getImage(path);
		myMediaTracker.addImage(myImage,0);
		try {
			myMediaTracker.waitForAll();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myMediaTracker.removeImage(myImage);
		BufferedImage myBufferedImage = new BufferedImage(myImage.getWidth(null), myImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D myGraphics2D = myBufferedImage.createGraphics();
		myGraphics2D.drawImage(myImage,0,0,null);
		myGraphics2D.dispose();
		myImage.flush();
		return myBufferedImage;
	}
}