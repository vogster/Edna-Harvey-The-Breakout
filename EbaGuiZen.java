package de.daedalic.eba;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.golden.gamedev.engine.BaseInput;
import com.golden.gamedev.object.Sprite;

public class EbaGuiZen extends EbaGameObject {
	private Sprite mySprite;
	private int[] walkHere;
	private boolean isCommandComplete = false;
	private String myCommand = "";
	private boolean isTopic = false;
	private boolean isExit = false;
	private boolean isGuiElement = false;
	private String myDataID = "";
	private Sprite myCommandObject;
	private String myBefehlszeilenText = "";
	private int walkToX;
	private int walkToY;
	private String myCommandObjectName = "";
	private int maximaleItemPosition;
	private Statement myStatement = null;
	private ResultSet myResultSet1 = null;
	private Sprite myTopicCursorSprite;
	final int STARTDRAG = 1;
	final int DRAGGING = 2;
	final int DROPPED = 0;
	final int STARTDROP = 3;
	boolean dragAktiv = false;
	private int myDragStatus;
	int[] slotX = { 100, 216, 332, 448, 564, 100, 216, 332, 448, 564, 100, 216, 332, 448, 564, 100, 216, 332, 448, 564, 100, 216, 332, 448, 564 };
	int[] slotY = { 68, 68, 68, 68, 68, 148, 148, 148, 148, 148, 228, 228, 228, 228, 228, 308, 308, 308, 308, 308, 388, 388, 388, 388, 388 };
	private EbaTopicSprite myTopic;
	int slotNummer = 0;
	private int myID;
	private boolean besetzt;
	private boolean isZen = false;
	
	public EbaGuiZen(EbaGameEngine myGameEngine) {
		super(myGameEngine);
	}
	public void initResources() {
		super.initResources();
		myTopicCursorSprite = new Sprite(Eba.getImage("gui/ednajung/b_ansehen.png"));
		myTopicCursorSprite.setImmutable(true);
		myTopicCursorSprite.setActive(false);
		myPlayField.add(myTopicCursorSprite);
		myBefehlszeile = new EbaCommandPromptSprite(myActiveFont, myInactiveFont, fontManager, 10, 530);
		myTextSprites.add(myBefehlszeile);

		myScriptInterpreter = new EbaScriptInterpreter(myCharacterSprite, myBackgroundSprites, myObjectSprites, myTextSprites, myGuiSprites, myChoiceList, myEbaGameEngine, this);
		try {
			myStatement = EbaGameEngine.myConnection.createStatement();
			Sprite myTmpSprite[] = new Sprite[myObjectSprites.getSize()];
			myTmpSprite = myObjectSprites.getSprites();
			for (int i = 0; i < myObjectSprites.getSize(); i++) {
				myResultSet1 = myStatement.executeQuery("SELECT * from Topic where raumobjektID=" + myTmpSprite[i].getID());
				if (myResultSet1.next()) {
					EbaTopicSprite myTopicSprite = new EbaTopicSprite(new BufferedImage[] { Eba.getImage(myResultSet1.getString("IconDatei")) }, 0, 0, myResultSet1.getInt("ID"), myResultSet1.getString("Bezeichnung"), myResultSet1.getInt("Inventarposition"), myResultSet1.getInt("Topicleistenposition"), myResultSet1.getInt("SkriptID"), myResultSet1.getInt("RaumobjektID"));
					myTopicSprite.setDataID(myResultSet1.getString("Bezeichnung"));
					myTopicSprite.setID(myResultSet1.getInt("ID"));
					myTopicSprite.setImmutable(true);
					if (myTopicSprite.getTopicposition() > 0) {
						myTopicSprite.setY(slotY[myTopicSprite.getTopicposition()-1]);
						myTopicSprite.setX(slotX[myTopicSprite.getTopicposition()-1]);
						myTopicSprite.setActive(true);
					} else {
						myTopicSprite.setActive(false);
					}
					myTopicZenSprites.add(myTopicSprite);
				}
			}
			myTmpSprite = new Sprite[myBackgroundSprites.getSize()];
			myTmpSprite = myBackgroundSprites.getSprites();
			for (int i = 0; i < myBackgroundSprites.getSize(); i++) {
				myResultSet1 = myStatement.executeQuery("SELECT * from Topic where raumobjektID=" + myTmpSprite[i].getID());
				while (myResultSet1.next()) {
					EbaTopicSprite myTopicSprite = new EbaTopicSprite(new BufferedImage[] { Eba.getImage(myResultSet1.getString("IconDatei")) }, 0, 0, myResultSet1.getInt("ID"), myResultSet1.getString("Bezeichnung"), myResultSet1.getInt("Inventarposition"), myResultSet1.getInt("Topicleistenposition"), myResultSet1.getInt("SkriptID"), myResultSet1.getInt("RaumobjektID"));
					myTopicSprite.setDataID(myResultSet1.getString("Bezeichnung"));
					myTopicSprite.setID(myResultSet1.getInt("ID"));
					myTopicSprite.setImmutable(true);
					if (myTopicSprite.getTopicposition() > 0) {
						myTopicSprite.setY(slotY[myTopicSprite.getTopicposition()-1]);
						myTopicSprite.setX(slotX[myTopicSprite.getTopicposition()-1]);
						myTopicSprite.setActive(true);
					} else {
						myTopicSprite.setActive(false);
					}
					myTopicZenSprites.add(myTopicSprite);
				}
			}
			

			myResultSet1 = myStatement.executeQuery("SELECT * from Raumobjekt where ID= 10590605");
			myResultSet1.next();
			isZen = myResultSet1.getBoolean("Aktiv");
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (myResultSet1 != null) {
				try {
					myResultSet1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				myResultSet1 = null;
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
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		if (dragAktiv == true) {
			myGuiSprites.setActive(false);
			myExit.setActive(false);
		} else {
			myGuiSprites.setActive(true);
			myExit.setActive(true);
		}
		if (!myScriptInterpreter.isRunning() && myCharacterSprite.isUnterbrochen()) {
			isCommandComplete = false;
			myCommand = "";
		}
		if (isTopic) {
			isTopic = false;
		}
		if (isExit) {
			isExit = false;
		}
		if (isGuiElement) {
			isGuiElement = false;
		}
		// ON MOUSE OVER:
		mySprite = checkPosMouse(myTopicZenSprites, true);
		if (mySprite == null) {
			mySprite = checkPosMouse(myGuiSprites, true);
		} else {
			if (mySprite.getID() > 0) {
				isTopic = true;
			}
		}
		if (mySprite == null) {
			mySprite = checkPosMouseInteraction(myObjectSprites, true);
		} else {
			isGuiElement = true;
		}
		if (mySprite == null) {
			mySprite = checkPosMouse(myBackgroundSprites, true);
		}
		if (mySprite == null) {
			myDataID = "";
		} else {
			if (myDragStatus == DRAGGING) {

			} else {
				if (mySprite.getDataID() == "Выход") {
					isExit = true;
				}
				myDataID = (String) mySprite.getDataID();
				myID = mySprite.getID();
			}
		}
		if (isCommandComplete) {
			if (!myCharacterSprite.isWalking()) {
				if (myCommandObject == null) {
					skriptID = 0;
				}
				if (myCommand != "") {
					if (myCommand == "Подойди к ") {
						myCommand = "";
						isCommandComplete = false;
					} else if (myCommand.startsWith("Посмотри на ")) {
						myCommand = "";
						isCommandComplete = false;
						if (myCommandObject == null) {
						} else {
							skriptID = ((EbaInteractionSprite) myCommandObject).getMyAnsehenSkript();
						}
						myScriptInterpreter.runScript(skriptID, 1);
						zeilennummer++;
					} else if ((myCommand.startsWith("Подойди к ")) && (myCommandObject.getDataID() == "Выход")) {
						myCommand = "";
						isCommandComplete = false;
						if (myCommandObject == null) {
						} else {
							skriptID = ((EbaInteractionSprite) myCommandObject).getMyBenutzenSkript();
							if (skriptID <= 0) {
								skriptID = ((EbaExitSprite) myCommandObject).getAusgangID() * (-1);
							}
						}
						myScriptInterpreter.runScript(skriptID, 1);
						zeilennummer++;
					} else {
						myCommand = "";
						isCommandComplete = false;
					}
				}
				myBefehlszeilenText = myCommand;
			}
		} else if (myDataID == "") {
			if (myCommand == "") {
				myBefehlszeilenText = "";
				myCommandObject = null;
			} else {
				if (myCommand == "Подойди к ") {
					myBefehlszeilenText = "";
					myCommandObject = null;
				} else {
					myBefehlszeilenText = myCommand;
				}
			}
		} else { // DataID = Objekt
			if (myCommand == "") {
				myBefehlszeilenText = myDataID;
				if (isTopic) {
				} else if (isExit) {
					myBefehlszeilenText = "Подойди к " + ((EbaExitSprite) mySprite).getMyMouseoverText();
				} else if (isGuiElement) {
					// Do nothing
				} else {
				}
			} else if (isExit || isGuiElement) {
				// do nothing
			} else {
				myBefehlszeilenText = myCommand + myDataID;
			}
		}
		// ON MOUSE PRESSED:
		switch (bsInput.getMousePressed()) {
		case BaseInput.NO_BUTTON:
			break;
		case MouseEvent.BUTTON1:
			// linke Maustaste:
			
			if(isZen &&  !isExit){
				System.out.println("ZEN!!!");
				skriptID = 105915;
				myScriptInterpreter.runScript(skriptID,1);
			}else{
			
			myDragStatus = STARTDRAG;
			if (myDataID == "") {
				myEbaWalkableAreaMap.findWay((int) myCharacterSprite.getBaseX(), (int) myCharacterSprite.getBaseY(), getMouseX(), getMouseY());
				walkHere = (int[]) myEbaWalkableAreaMap.pop();
				myBefehlszeilenText = "Подойди к ";
				myCommand = "Подойди к ";
				isCommandComplete = true;
				myCharacterSprite.walkTo(walkHere[0], walkHere[1]);

			} else if (myDataID.equals("Harke")){
				{
					skriptID = 105901;
                    myScriptInterpreter.runScript(skriptID, 1);
                    zeilennummer++;
               }
			} else {
				if (isTopic) {

				} else { // ----mySprite ist ein Objekt--------
					if (myCommand != "" && isExit) {
						// do nothing
					} else {
						//
						//
						// if (!isTopic && !isGuiElement) {
						if (isExit) {
							myCommandObject = mySprite;
							if (((EbaInteractionSprite) myCommandObject).getWalkToX() < 0) {
								walkToX = (int) myCommandObject.getX();
								walkToY = (int) myCommandObject.getY();
							} else {
								walkToX = ((EbaInteractionSprite) myCommandObject).getWalkToX();
								walkToY = ((EbaInteractionSprite) myCommandObject).getWalkToY();
							}
							myEbaWalkableAreaMap.findWay((int) myCharacterSprite.getBaseX(), (int) myCharacterSprite.getBaseY(), walkToX, walkToY);
							walkHere = (int[]) myEbaWalkableAreaMap.pop();
							myCommand = myBefehlszeilenText;
							myCommandObjectName = ((EbaExitSprite) mySprite).getMyMouseoverText();
							isCommandComplete = true;
							myCharacterSprite.walkTo(walkHere[0], walkHere[1], ((EbaInteractionSprite) mySprite).getStandbyBlickrichtung());
						}
					}
				}
			}
			
			myBefehlszeilenText = myCommand;
			}
			break;
		default:
		// rechte Maustaste:
		}
		// ON MOUSE RELEASED:
		switch (bsInput.getMouseReleased()) {
		case BaseInput.NO_BUTTON:
			break;
		case MouseEvent.BUTTON1:
			// linke Maustaste:

			// ablegen
			if (myDragStatus == STARTDRAG && dragAktiv == true) {
				if (myDataID.equals("Zengarten")) {

					if (myID == 10590201) {
						slotNummer = 0;
					}
					if (myID == 10590202) {
						slotNummer = 1;
					}
					if (myID == 10590203) {
						slotNummer = 2;
					}
					if (myID == 10590204) {
						slotNummer = 3;
					}
					if (myID == 10590205) {
						slotNummer = 4;
					}
					if (myID == 10590206) {
						slotNummer = 5;
					}
					if (myID == 10590207) {
						slotNummer = 6;
					}
					if (myID == 10590208) {
						slotNummer = 7;
					}
					if (myID == 10590209) {
						slotNummer = 8;
					}
					if (myID == 10590210) {
						slotNummer = 9;
					}
					if (myID == 10590211) {
						slotNummer = 10;
					}
					if (myID == 10590212) {
						slotNummer = 11;
					}
					if (myID == 10590213) {
						slotNummer = 12;
					}
					if (myID == 10590214) {
						slotNummer = 13;
					}
					if (myID == 10590215) {
						slotNummer = 14;
					}
					if (myID == 10590216) {
						slotNummer = 15;
					}
					if (myID == 10590217) {
						slotNummer = 16;
					}
					if (myID == 10590218) {
						slotNummer = 17;
					}
					if (myID == 10590219) {
						slotNummer = 18;
					}
					if (myID == 10590220) {
						slotNummer = 19;
					}
					if (myID == 10590221) {
						slotNummer = 20;
					}
					if (myID == 10590222) {
						slotNummer = 21;
					}
					if (myID == 10590223) {
						slotNummer = 22;
					}
					if (myID == 10590224) {
						slotNummer = 23;
					}
					if (myID == 10590225) {
						slotNummer = 24;
					}

					Sprite[] myTmpTopic = myTopicZenSprites.getSprites();
					besetzt = false;
					for (int i = 0; i < myTopicZenSprites.getSize(); i++) {
						if (((EbaTopicSprite) myTmpTopic[i]).getTopicposition() == (slotNummer + 1)) {
							besetzt = true;
						}
					}
					if (!besetzt) {
						try {
							int tempID = 0;
							if (slotNummer == 2) {
								tempID = 10590403;
							}
							if (slotNummer == 8) {
								tempID = 10590404;
							}
							if (slotNummer == 11) {
								tempID = 10590402;
							}
							if (slotNummer == 19) {
								tempID = 10590405;
							}
							if (slotNummer == 20) {
								tempID = 10590401;
							}
							if (tempID > 0){
							myStatement = EbaGameEngine.myConnection.createStatement();
							myStatement.executeUpdate("UPDATE raumobjekt SET aktiv=true where ID=" + tempID);
							myStatement.close();
							
							
							}
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

						myTopic.setY(slotY[slotNummer]);
						myTopic.setX(slotX[slotNummer]);
						myTopic.setActive(true);
						
						myTopic.setTopicposition(slotNummer + 1);
						
						myTopicCursorSprite.setActive(false);
						myCursorSprite.setActive(false);
						myCursorSprite = myStandardCursorSprite;
						myCursorSprite.setActive(true);
						dragAktiv = false;

					}

				}
			}
			myDragStatus = STARTDROP;

			break;
		default:
			// rechte Maustaste:
			if (myDataID == "") {
			} else {
				if (!isExit && !isGuiElement) {
					if (myCommand == "") {
						// TODO: Ansehen-Sktript
						// myBefehlszeilenText = myCommand;
						//myCommand = "Посмотри на " + myDataID;
						//isCommandComplete = true;
					} else {
						myBefehlszeilenText = "";
						myCommand = "";
						myCommandObject = null;
					}
				}
			}
		}
		if ((!isCommandComplete) && (myDataID != "") && (myDataID != myCommandObjectName)) {
			myBefehlszeile.change(myBefehlszeilenText, false);
		} else {
			myBefehlszeile.change(myBefehlszeilenText, isCommandComplete);
		}
		if (bsInput.isMouseDown(1) && myDragStatus == STARTDRAG && dragAktiv == false) {

			Sprite myTmpTopic[];
			myTmpTopic = myTopicZenSprites.getSprites();

			if(mySprite == null){
				
			}else{
				for (int i = 0; i < myTopicZenSprites.getSize(); i++) {
					if (((EbaTopicSprite) myTmpTopic[i]) == mySprite) {
						myTopic = (EbaTopicSprite) myTmpTopic[i];
						if (myTopic.getTopicposition() > 0) {
	
							try {
								int tempID = 0;
								if (myTopic.getTopicposition() == 3) {
									tempID = 10590403;
								}
								if (myTopic.getTopicposition() == 9) {
									tempID = 10590404;
								}
								if (myTopic.getTopicposition() == 12) {
									tempID = 10590402;
								}
								if (myTopic.getTopicposition() == 20) {
									tempID = 10590405;
								}
								if (myTopic.getTopicposition() == 21) {
									tempID = 10590401;
								}
								if (tempID > 0){
								myStatement = EbaGameEngine.myConnection.createStatement();
								myStatement.executeUpdate("UPDATE raumobjekt SET aktiv=false where ID=" + tempID);
								myStatement.close();
								}
							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (SQLException e) {
								e.printStackTrace();
							}finally {
								if (myStatement != null) {
									try {
										myStatement.close();
									} catch (SQLException e) {
										e.printStackTrace();
									}
									myStatement = null;
								}
							}
	
							myTopicCursorSprite.setImage(myTopic.getImage());
							myStandardCursorSprite.setActive(false);
							myCursorSprite = myTopicCursorSprite;
							myCursorSprite.setActive(true);
							myTopic.setTopicposition(0);
							myTopic.setActive(false);
							myTopic.setX(0);
							myTopic.setY(0);
							dragAktiv = true;
						} else {
						}
					}
				}
			}

			myDragStatus = DRAGGING;
			myTmpTopic = null;
		}
		if (myDragStatus == STARTDROP) {
			myDragStatus = DROPPED;
		}

		myPlayField.update(elapsedTime);
	}
	// /////////////////////////////////////////////////////////
	// Private Methoden //
	// /////////////////////////////////////////////////////////
	void updateInventory() {
	}
	// /////////////////////////////////////////////////////////
	// ?ffentliche Methoden //
	// /////////////////////////////////////////////////////////
	public void deactivateItem(int inventarObjektID) {
		updateInventory();
	}
	public void activateItem(int inventarObjektID) {
		maximaleItemPosition++;
		updateInventory();
	}
}
