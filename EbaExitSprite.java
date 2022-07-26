package de.daedalic.eba;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



@SuppressWarnings("serial")
public class EbaExitSprite extends EbaInteractionSprite {

	int myAusgangID;
	int myZielraumID;
	int myWalkinPointX;
	int myWalkinPointY;
	String myMouseoverText;
	String myCharakterBlickrichtung;
	private Statement myStatement;
	private ResultSet myResultSet;


	public EbaExitSprite(BufferedImage myBufferedImage, double x,
			double y, int walkToPointX, int walkToPointY, String standbyBlickrichtung, String defaultAktion, int AnsehenSkriptID,
			int BenutzenSkriptID, int NehmenSkriptID, int RedenMitSkriptID,
			boolean aktiv,
			int initAusgangID, int initZielraumID,
			int initWalkinPointX, int initWalkinPointY,
			String initCharakterBlickrichtung,
			String initMouseOverText) {

		super(myBufferedImage, x, y, walkToPointX, walkToPointY, standbyBlickrichtung, defaultAktion,
				AnsehenSkriptID, BenutzenSkriptID, NehmenSkriptID,
				RedenMitSkriptID, aktiv);

		myAusgangID = initAusgangID;
		myZielraumID = initZielraumID;
		myWalkinPointX = initWalkinPointX;
		myWalkinPointY = initWalkinPointY;
		myCharakterBlickrichtung = initCharakterBlickrichtung;
		myMouseoverText = initMouseOverText;
	}



	public int getAusgangID() {
		return myAusgangID;
	}

	public void setAusgangID(int myAusgangID) {
		this.myAusgangID = myAusgangID;
	}

	public String getCharakterBlickrichtung() {
		return myCharakterBlickrichtung;
	}

	public void setCharakterBlickrichtung(String myCharakterBlickrichtung) {
		this.myCharakterBlickrichtung = myCharakterBlickrichtung;
	}


	public int getWalkinPointX() {
		return myWalkinPointX;
	}

	public void setWalkinPointX(int myWalkinPointX) {
		this.myWalkinPointX = myWalkinPointX;
	}

	public int getWalkinPointY() {
		return myWalkinPointY;
	}

	public void setWalkinPointY(int myWalkinPointY) {
		this.myWalkinPointY = myWalkinPointY;
	}

	public String getMyMouseoverText() {
		return myMouseoverText;
	}

	public int getZielraumID() {
		return myZielraumID;
	}

	public void setZielraumID(int myZielraumID) {
		this.myZielraumID = myZielraumID;
		try {

            myStatement = EbaGameEngine.myConnection.createStatement();
            myResultSet = myStatement.executeQuery("SELECT * FROM Raumobjektinteraktion WHERE RaumobjektID =" + getID());
            myResultSet.next();
            myStatement.executeUpdate("UPDATE Ausgang SET ZielraumID= "+ myZielraumID +" where RaumobjektinteraktionID = " + myResultSet.getInt("ID"));

        } catch (SQLException e) {
        } finally {
        	if (myResultSet != null) {
                try {
                    myResultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                myResultSet = null;
            }
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

    protected void updateAnimation()
    {
    	super.updateAnimation();

    	// Nach Animation deaktivieren:
    	setActive(isAnimate());
    }

    public void render(Graphics2D g, int x, int y) {
        // DO NOTHING
        if (this.isActive()){
        g.drawImage(getImage(), x, y, null); // nur zum Debuggen
        //g.drawRect(x, y, width, height);     // nur zum Debuggen
        }
    }




}
