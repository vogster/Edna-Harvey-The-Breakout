package de.daedalic.eba;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.sql.Statement;

import com.golden.gamedev.object.AnimatedSprite;

@SuppressWarnings("serial")
public class EbaInteractionSprite extends AnimatedSprite {

	int walkToX;
	int walkToY;
	int myAnsehenSkript;
	int myNehmenSkript;
	int myBenutzenSkript;
	int myRedenMitSkript;
	int myAusgangID=0;
	private Statement myStatement;
	private String myDefaultAktion;
	String myStandbyBlickrichtung;


	public EbaInteractionSprite(BufferedImage myBufferedImage, double x, double y, int walkToPointX, int walkToPointY, String standbyBlickrichtung, String defaultAktion, int AnsehenSkriptID, int BenutzenSkriptID, int NehmenSkriptID, int RedenMitSkriptID, boolean aktiv) {
		super(x - (myBufferedImage.getWidth() / 2), y - myBufferedImage.getHeight());
		BufferedImage[] myBufferedImages = new BufferedImage[1];
		myBufferedImages[0] = myBufferedImage;
		setImages(myBufferedImages);
		walkToX = walkToPointX;
		walkToY = walkToPointY;
		myStandbyBlickrichtung = standbyBlickrichtung;
		myDefaultAktion = defaultAktion;
		myAnsehenSkript = AnsehenSkriptID;
		myNehmenSkript = NehmenSkriptID;
		myBenutzenSkript = BenutzenSkriptID;
		myRedenMitSkript = RedenMitSkriptID;
		this.setActive(aktiv);
	}

	public void render(Graphics2D g, int x, int y) {
		// DO NOTHING
        if (this.isActive()){
		//g.drawImage(getImage(), x, y, null); // nur zum Debuggen
		//g.drawRect(x, y, width, height);     // nur zum Debuggen
        }
	}

	public int getWalkToX() {
		return walkToX;
	}

	public int getWalkToY() {
		return walkToY;
	}

	public String getStandbyBlickrichtung(){
		return myStandbyBlickrichtung;
	}

	public String getMyDefaultAktion() {
		return myDefaultAktion;
	}

	public int getMyAnsehenSkript() {
		return myAnsehenSkript;
	}

	public int getMyBenutzenSkript() {
		return myBenutzenSkript;
	}

	public int getMyNehmenSkript() {
		return myNehmenSkript;
	}

	public int getMyRedenMitSkript() {
		return myRedenMitSkript;
	}

	public int getAusgangID() {
		return myAusgangID;
	}

	public void setMyDefaultAktion(String myDefaultAktion) {
		this.myDefaultAktion = myDefaultAktion;
		try {
            myStatement = EbaGameEngine.myConnection.createStatement();
            myStatement.executeUpdate("UPDATE Raumobjektinteraktion SET DefaultAktion= "+ myDefaultAktion +" where raumobjektid="+ getID());

        } catch (SQLException e) {
        } finally {

            if (myStatement != null) {
                try {
                    myStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                myStatement = null;
            }
        }
	}

	public void setMyAnsehenSkript(int myAnsehenSkript) {
		this.myAnsehenSkript = myAnsehenSkript;
		try {
            myStatement = EbaGameEngine.myConnection.createStatement();
            myStatement.executeUpdate("UPDATE Raumobjektinteraktion SET AnsehenSkriptID= "+ myAnsehenSkript +" where raumobjektid="+ getID());

        } catch (SQLException e) {
        } finally {

            if (myStatement != null) {
                try {
                    myStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                myStatement = null;
            }
        }
	}

	public void setMyBenutzenSkript(int myBenutzenSkript) {
		this.myBenutzenSkript = myBenutzenSkript;
		try {
            myStatement = EbaGameEngine.myConnection.createStatement();
            myStatement.executeUpdate("UPDATE Raumobjektinteraktion SET BenutzenSkriptID= "+ myBenutzenSkript +" where raumobjektid="+ getID());

        } catch (SQLException e) {
        } finally {

            if (myStatement != null) {
                try {
                    myStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                myStatement = null;
            }
        }
	}

	public void setMyNehmenSkript(int myNehmenSkript) {
		this.myNehmenSkript = myNehmenSkript;
		try {
            myStatement = EbaGameEngine.myConnection.createStatement();
            myStatement.executeUpdate("UPDATE Raumobjektinteraktion SET NehmenSkriptID= "+ myNehmenSkript +" where raumobjektid="+ getID());

        } catch (SQLException e) {
        } finally {

            if (myStatement != null) {
                try {
                    myStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                myStatement = null;
            }
        }
	}

	public void setMyRedenMitSkript(int myRedenMitSkript) {
		this.myRedenMitSkript = myRedenMitSkript;
		try {
            myStatement = EbaGameEngine.myConnection.createStatement();
            myStatement.executeUpdate("UPDATE Raumobjektinteraktion SET RedenMitSkriptID= "+ myRedenMitSkript +" where Raumobjektid="+ getID());

        } catch (SQLException e) {
        } finally {

            if (myStatement != null) {
                try {
                    myStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                myStatement = null;
            }
        }
	}

	public void setAusgangID(int initAusgangID){
		this.myAusgangID = initAusgangID;
	}

}
