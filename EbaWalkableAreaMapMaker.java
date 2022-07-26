package de.daedalic.eba;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

@SuppressWarnings("serial")
public class EbaWalkableAreaMapMaker extends JFrame {

	public static void main(String[] args) throws IOException {
		new EbaWalkableAreaMapMaker();
	}

	public EbaWalkableAreaMapMaker() throws IOException {
		BufferedImage myBufferedImage = getBufferedImageFromFile("Quelle für Walkable Area Map laden:");
		boolean[][] walkableAreaArray = new boolean[800][600];
		for (int x=0; x<800; x++) {
			for (int y=0; y<600; y++) {
				walkableAreaArray[x][y] = (myBufferedImage.getRGB(x,y) == -1);
			}
		}
		ObjectOutputStream myObjectOutputStream = getObjectOutputStreamIntoFile("Walkable Area Map speichern:");
		myObjectOutputStream.writeObject(walkableAreaArray);
	}

	private BufferedImage getBufferedImageFromFile(String dialogTitle) throws IOException {
	    JFileChooser myJFileChooser = new JFileChooser();

	    myJFileChooser.setFileFilter(new FileFilter() {
	    	public boolean accept(File myFile) {
	            return myFile.isDirectory() || myFile.getName().toLowerCase().endsWith(".gif");
	        }
	        public String getDescription() {
	            return "*.gif";
	        }
	    } );

	    myJFileChooser.setDialogTitle(dialogTitle);
	    int returnVal = myJFileChooser.showOpenDialog(null);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
			return ImageIO.read(myJFileChooser.getSelectedFile());
	    } else {
	        return null;
	    }
	}

    public ObjectOutputStream getObjectOutputStreamIntoFile(String dialogTitle) throws IOException {
    	final String postFix = ".wam";
        JFileChooser myJFileChooser = new JFileChooser();
        myJFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);

        myJFileChooser.setFileFilter(new FileFilter() {
        	public boolean accept(File myFile) {
				return myFile.isDirectory() || myFile.getName().toLowerCase().endsWith(postFix);
            }
            public String getDescription() {
                return "*" + postFix;
            }
        } );

        myJFileChooser.setDialogTitle(dialogTitle);
        int returnVal = myJFileChooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
	        File myFile = myJFileChooser.getSelectedFile();
        	if (!myFile.getName().toLowerCase().endsWith(postFix)) {
        		myFile = new File(myFile.getAbsolutePath() + postFix);
            }
        	return new ObjectOutputStream(new FileOutputStream(myFile));
        } else {
        	return null;
        }
    }

}