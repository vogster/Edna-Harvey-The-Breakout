package de.daedalic.eba;

import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.sql.Statement;

import com.golden.gamedev.object.AnimatedSprite;

@SuppressWarnings("serial")
public class EbaTopicSprite extends AnimatedSprite {

	int id;
	String bezeichnung;
	int inventarposition;
	int topicposition;
	int skriptID;
	int raumobjektID;
	private Statement myStatement;

	public EbaTopicSprite(BufferedImage initImages1[], int initX, int initY, int initID, String initBezeichnung, int initInventarposition, int initTopicposition,int initSkriptID, int initRaumobjektID) {
		super(initImages1, initX, initY);
		id = initID;
		bezeichnung = initBezeichnung;
		inventarposition = initInventarposition;
		topicposition = initTopicposition;
		raumobjektID = initRaumobjektID;
		skriptID = initSkriptID;
	}
     public EbaTopicSprite(){
          super();
     }

	public boolean isItem(){
		return true;
	}

	public int getInventarposition() {
		return inventarposition;
	}

	public void setInventarposition(int inventarposition) {
		this.inventarposition = inventarposition;
	}



	public String getBezeichnung() {
		return bezeichnung;
	}


	public int getId() {
		return id;
	}


	public int getSkriptID() {
        return skriptID;
    }


	public void setSkriptID(int initSkriptID) {
		skriptID = initSkriptID;
        try {
            myStatement = EbaGameEngine.myConnection.createStatement();
            myStatement.executeUpdate("UPDATE Topic SET SkriptID= "+ skriptID +" where ID=" + getID());

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

	public int getTopicposition() {
		return topicposition;
	}

	public void setTopicposition(int initTopicPosition) {
		topicposition = initTopicPosition;
        try {
            myStatement = EbaGameEngine.myConnection.createStatement();
            myStatement.executeUpdate("UPDATE Topic SET TOPICLEISTENPOSITION= "+ topicposition +" where ID=" + getID());
        } catch (SQLException e) {
             System.out.println("SQLcrash;;;   " +e);
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

	public int getRaumobjektID() {
		return raumobjektID;
	}
}


