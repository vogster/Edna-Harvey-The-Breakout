package de.daedalic.eba;

import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.golden.gamedev.engine.BaseInput;
import com.golden.gamedev.object.Sprite;

public class EbaChoiceList extends EbaSpriteGroup {

	private Sprite mySprite;
	private int myID =0;

	boolean isAnyButtonActive = false;

	private Sprite myChoiceListBg = null;

	private EbaGameObject myEbaGameObject;
	private Statement myStatement;
	private ResultSet myResultSet;
	private EbaTextSprite[] myEbaTextSprite = new EbaTextSprite[10];
	private String[] myAuswahltext = new String[10];
	private int[] mySkriptID = new int[10];
	private int myChoiceListID=0;

	private int n = 0;

	public EbaChoiceList(EbaGameObject myGameObject) {
		super("ChoiceList");
		myEbaGameObject = myGameObject;

	}

	public void invoke(int ChoiceListID){
		myChoiceListID = ChoiceListID;
		try {
            myStatement = EbaGameEngine.myConnection.createStatement();
            myResultSet = myStatement.executeQuery("SELECT * FROM ChoiceListe WHERE ID=" +myChoiceListID);
            while(myResultSet.next()&& n<=10){

            	if(myResultSet.getBoolean("Aktiv")){
            	n++;
            	myEbaTextSprite[n] = new EbaTextSprite(n+": "+myResultSet.getString("Auswahltext"), myEbaGameObject.myActiveFont, myEbaGameObject.fontManager, 0, 615 - (n*25), 1);
            	myAuswahltext[n] = myResultSet.getString("Auswahltext");
            	mySkriptID[n] = myResultSet.getInt("SkriptID");
            	myEbaTextSprite[n].setX(18);
            	myEbaTextSprite[n].setID(n);
            	this.add(myEbaTextSprite[n]);
            	}

            }
        } catch (NumberFormatException e) {

            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
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
        myChoiceListBg = new Sprite(Eba.getImage("gui/edna/bg_choicelist_"+n+".png"), 0, 313);
		myChoiceListBg.setID(-1);
		myChoiceListBg.setImmutable(true);
		this.add(myChoiceListBg);
	}

	public void setAuswahlAktiv(int myChoiceListID,int myAuswahlnummer, boolean active){
		n=0;
		try {
            myStatement = EbaGameEngine.myConnection.createStatement();
            myStatement.executeUpdate("UPDATE ChoiceListe SET aktiv= " + active +" where ID=" + myChoiceListID + " AND Auswahlnummer =" + myAuswahlnummer);

        } catch (NumberFormatException e) {

            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
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

	public void setAuswahlSkriptID(int choiceListID, int auswahlNummer, int skriptID) {
		try {
                myStatement = EbaGameEngine.myConnection.createStatement();
                myStatement.executeUpdate("UPDATE ChoiceListe SET SkriptID= "+ skriptID +" where ID=" + choiceListID + " AND AUSWAHLNUMMER = " + auswahlNummer);
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

	public void update(long elapsedTime) {
		super.update(elapsedTime);

			for (int i=n; i>0; i--){
				remove(getSprite(i));
			}

			for (int i=n; i>0; i--){
				if (i == myID){
					myEbaTextSprite[i] = new EbaTextSprite(myAuswahltext[i], myEbaGameObject.myActiveFont, myEbaGameObject.fontManager, 0, 336 + (i*25) + ((10-n)*25), 1);
					myEbaTextSprite[i].setX(18);
					myEbaTextSprite[i].setID(i);
					this.add(myEbaTextSprite[i]);
				}else{
					myEbaTextSprite[i] = new EbaTextSprite(myAuswahltext[i], myEbaGameObject.myInactiveFont, myEbaGameObject.fontManager, 0, 336 + (i*25) + ((10-n)*25), 1);
					myEbaTextSprite[i].setX(18);
					myEbaTextSprite[i].setID(i);
					this.add(myEbaTextSprite[i]);
				}
			}

			// ***** ON MOUSE OVER *****
			mySprite = myEbaGameObject.checkPosMouse(this, true);

			if (mySprite == null) {
				myID = 0;
			} else {
				myID = (int) mySprite.getID();
			}


			// ***** ENDE ON MOUSE OVER *****




			// ***** ON MOUSE PRESSED *****

			switch (myEbaGameObject.bsInput.getMousePressed()) {
			case BaseInput.NO_BUTTON:
				break;
			case MouseEvent.BUTTON1:
				// linke Maustaste:

				if(mySprite==null){
					// do nothing
				} else {
					if(mySprite.getID()>0){
						myEbaGameObject.skriptID=mySkriptID[mySprite.getID()];
						myEbaGameObject.zeilennummer = 1;
						myEbaGameObject.myScriptInterpreter.runScript(mySkriptID[mySprite.getID()], 1);
						myEbaGameObject.zeilennummer++;
						n=0;
						myEbaGameObject.myGuiSprites.setActive(true);
						remove(getSprite(-1));
						this.setActive(false);
					}
				}

			break;

			default: break;

			}
		}

	}


