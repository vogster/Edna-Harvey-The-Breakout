package de.daedalic.eba;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.golden.gamedev.engine.BaseInput;
import com.golden.gamedev.object.AnimatedSprite;
import com.golden.gamedev.object.Sprite;

public class EbaGuiEdnajung extends EbaGameObject {

	private Sprite mySprite;

	private String myDataID;

	private String myBefehlszeilenText = "";

	private String myCommandObjectName = "";

	private String myCommand = "";

	private AnimatedSprite mySpriteAnsehen;

	private AnimatedSprite mySpriteNehmen;

	private AnimatedSprite mySpriteReden;

	private AnimatedSprite mySpriteBenutzen;

	private AnimatedSprite mySpriteItemleiste;

	private AnimatedSprite mySpriteZuharvey;

	private boolean isCommandComplete = false;

	private boolean isItem = false;

	private boolean isExit = false;

	private boolean isGuiElement = false;

	private String pressedButton = "";

	private char defaultAktion = '0';

	private int[] walkHere;

	private int walkToX;

	private int walkToY;

	private Statement myStatement = null;
	private ResultSet myResultSet = null;
	private AnimatedSprite myItem;

	private Sprite myCommandObject;
	private Sprite myCommandObject2;

	Sprite myTmpSprite[];

	public int maximaleItemPosition = 0;

	// Inventarzeilenverschiebung
	// bei �berf�lltem Inventar

	public int INVENTARZEILENHOEHE = 78;

	public int INVENTARZEILENBREITE = 75;

	public int YUNTERSTEINVENTARZEILE = 523;

	public int XUNTERSTEINVENTARZEILE = 401;

	public EbaItemSprite[] myItemSprites = new EbaItemSprite[100];

	public EbaGuiEdnajung(EbaGameEngine myGameEngine) {
		super(myGameEngine);

	}

	public void initResources() {
		super.initResources();

		// ------------------------------------------------------------
		// ----------- Initialisierung der Schaltfl�chen --------------
		// ------------------------------------------------------------

		mySpriteAnsehen = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/ednajung/b_ansehen.png"),
				Eba.getImage("gui/ednajung/b_ansehen_a.png"),
				Eba.getImage("gui/ednajung/b_ansehen_p.png") }, 0, 565);
		mySpriteAnsehen.setDataID("Посмотри на ");
		myGuiSprites.add(mySpriteAnsehen);

		mySpriteNehmen = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/ednajung/b_nehmen.png"),
				Eba.getImage("gui/ednajung/b_nehmen_a.png"),
				Eba.getImage("gui/ednajung/b_nehmen_p.png") }, 100, 565);
		mySpriteNehmen.setDataID("Возьми ");
		myGuiSprites.add(mySpriteNehmen);

		mySpriteReden = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/ednajung/b_reden.png"),
				Eba.getImage("gui/ednajung/b_reden_a.png"),
				Eba.getImage("gui/ednajung/b_reden_p.png") }, 200, 565);
		mySpriteReden.setDataID("Поговори с ");
		myGuiSprites.add(mySpriteReden);

		mySpriteBenutzen = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/ednajung/b_benutzen.png"),
				Eba.getImage("gui/ednajung/b_benutzen_a.png"),
				Eba.getImage("gui/ednajung/b_benutzen_p.png") }, 300, 565);
		mySpriteBenutzen.setDataID("Используй ");
		myGuiSprites.add(mySpriteBenutzen);

		mySpriteItemleiste = new AnimatedSprite(new BufferedImage[] { Eba
				.getImage("gui/ednajung/itemleiste.png") }, 398, 551);
		mySpriteItemleiste.setDataID("Inventar");
		myGuiSprites.add(mySpriteItemleiste);

		mySpriteZuharvey = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/ednajung/b_zuharvey.png"),
				Eba.getImage("gui/ednajung/b_zuharvey_a.png"),
				Eba.getImage("gui/ednajung/b_zuharvey_p.png") }, 698, 525);
		mySpriteZuharvey.setDataID("К Харви");
		mySpriteZuharvey.setImmutable(true);
		mySpriteZuharvey.setActive(true);
		myGuiSprites.add(mySpriteZuharvey);

//		myBefehlszeile = new EbaCommandPromptSprite(myActiveFont,
//				myInactiveFont, fontManager, 10, 530);
//		myTextSprites.add(myBefehlszeile);

//		myScriptInterpreter = new EbaScriptInterpreter(myCharacterSprite,
//				myBackgroundSprites, myObjectSprites, myTextSprites,
//				myGuiSprites, myChoiceList, myEbaGameEngine, this);

		myTmpSprite = new Sprite[myGuiSprites.getSize()];

		updateInventory();
	}

	public void update(long elapsedTime) {
		super.update(elapsedTime);

		defaultAktion = '0';

		if (!myScriptInterpreter.isRunning()
				&& myCharacterSprite.isUnterbrochen()) {
			isCommandComplete = false;
			myCommand = "";
		}

		if (isItem) {
			isItem = false;
		}

		if (isExit) {
			isExit = false;
		}

		if (isGuiElement) {
			isGuiElement = false;
		}

		
		if ( myScriptInterpreter.isRunning() || myMenueSprites.isActive() || myChoiceList.isActive()) {

		}else{
		
		myTmpSprite = myGuiSprites.getSprites();
		for (int i = 0; i < myGuiSprites.getSize(); i++) {
			if (myTmpSprite[i].getID() > 0) {
				((EbaItemSprite) myTmpSprite[i]).setFrame(0);
			}
		}

		
		
		// ON MOUSE OVER:

		mySprite = checkPosMouse(myGuiSprites, true);

		if (mySprite == null) {
			mySprite = checkPosMouseInteraction(myObjectSprites, true);
		} else {
			if (mySprite.getID() > 0) {
				// Nur Inventar-Items d�rfen in der GUI-Sprites Spritegroup
				// IDs > 0 bekommen
				((AnimatedSprite) mySprite).setFrame(1);
				isItem = true;
			}else {
				isGuiElement = true;
			}
		}

		if (mySprite == null) {
			mySprite = checkPosMouse(myBackgroundSprites, true);
		}

		if (mySprite == null) {
			myDataID = "";
		} else {
			if (mySprite.getDataID().equals("Выход")) {
				isExit = true;
			}

			if(mySprite.getDataID().equals("К Харви")&& (myCommand.startsWith("Используй")|| myCommand.startsWith("Возьми")|| myCommand.startsWith("Посмотри на")|| myCommand.startsWith("Поговори с"))){
				// Do nothing
			} else {
			myDataID = (String) mySprite.getDataID();
			}
		}

		mySpriteAnsehen.setFrame(0);
		mySpriteNehmen.setFrame(0);
		mySpriteReden.setFrame(0);
		mySpriteBenutzen.setFrame(0);
		mySpriteZuharvey.setFrame(0);

		if (bsInput.isMouseDown(MouseEvent.BUTTON1)) {
			if (myDataID.equals("Посмотри на ")) {
				pressedButton = "Посмотри на ";
				mySpriteAnsehen.setFrame(2);
			} else if (myDataID.equals("Возьми ")) {
				pressedButton = "Возьми ";
				mySpriteNehmen.setFrame(2);
			} else if (myDataID.equals("Поговори с ")) {
				pressedButton = "Поговори с ";
				mySpriteReden.setFrame(2);
			} else if (myDataID.equals("Используй ")) {
				pressedButton = "Используй ";
				mySpriteBenutzen.setFrame(2);
			} else if (myDataID.equals("К Харви")) {
				pressedButton = "К Харви";
				mySpriteZuharvey.setFrame(2);
			} else if (mySpriteAnsehen.getDataID() == pressedButton) {
				mySpriteAnsehen.setFrame(2);
			} else if (mySpriteNehmen.getDataID() == pressedButton) {
				mySpriteNehmen.setFrame(2);
			} else if (mySpriteReden.getDataID() == pressedButton) {
				mySpriteReden.setFrame(2);
			} else if (mySpriteBenutzen.getDataID() == pressedButton) {
				mySpriteBenutzen.setFrame(2);
			} else if (mySpriteZuharvey.getDataID() == pressedButton) {
				mySpriteZuharvey.setFrame(2);
			}
		} else {
			if (myDataID.equals("Посмотри на ")) {
				mySpriteAnsehen.setFrame(1);
			} else if (myDataID.equals("Возьми ")) {
				mySpriteNehmen.setFrame(1);
			} else if (myDataID.equals("Поговори с ")) {
				mySpriteReden.setFrame(1);
			} else if (myDataID.equals("Используй ")) {
				mySpriteBenutzen.setFrame(1);
			} else if (myDataID.equals("К Харви")) {
				mySpriteZuharvey.setFrame(1);
			} else {
			}
		}

		if (isCommandComplete) {
			
			if (!myCharacterSprite.isWalking()) {

				if (myCommandObject == null) {
					skriptID = 0;
				}

				if (myCommand != "") {

					if (myCommand.equals("Подойди к ")) {
						myCommand = "";
						isCommandComplete = false;

					} else if (myCommand.startsWith("Возьми ")) {
						myCommand = "";
						isCommandComplete = false;
						if (myCommandObject == null) {
						} else {
							skriptID = ((EbaInteractionSprite) myCommandObject)
									.getMyNehmenSkript();
						}
						myScriptInterpreter.runScript(skriptID, 1);
						zeilennummer++;

					} else if (myCommand.startsWith("Используй ")) {

						if ((myCommand.startsWith("Используй "))
								&& myCommand.contains(" c ")) {

							myCommand = "";
							isCommandComplete = false;
							if (myCommandObject == null) {
							} else {
								skriptID = ((EbaItemSprite) myCommandObject2)
										.getBenutzeMitSkriptID(myCommandObject
												.getID());
							}
							myScriptInterpreter.runScript(skriptID, 1);
							zeilennummer++;

						} else {

							myCommand = "";
							isCommandComplete = false;
							if (myCommandObject == null) {
							} else {
								skriptID = ((EbaInteractionSprite) myCommandObject)
										.getMyBenutzenSkript();
							}
							myScriptInterpreter.runScript(skriptID, 1);
							zeilennummer++;

						}
					} else if (myCommand.startsWith("Поговори с ")) {

						myCommand = "";
						isCommandComplete = false;
						if (myCommandObject == null) {
						} else {
							skriptID = ((EbaInteractionSprite) myCommandObject)
									.getMyRedenMitSkript();
						}
						myScriptInterpreter.runScript(skriptID, 1);
						zeilennummer++;

					} else if (myCommand.startsWith("Посмотри на ")) {

						myCommand = "";
						isCommandComplete = false;
						if (myCommandObject == null) {
						} else {
							skriptID = ((EbaInteractionSprite) myCommandObject)
									.getMyAnsehenSkript();
						}
						myScriptInterpreter.runScript(skriptID, 1);
						zeilennummer++;

					} else if ((myCommand.startsWith("Подойди к "))
							&& (myCommandObject.getDataID().equals("Выход"))) {

						myCommand = "";
						isCommandComplete = false;
						if (myCommandObject == null) {
						} else {
							skriptID = ((EbaInteractionSprite) myCommandObject)
									.getMyBenutzenSkript();
							if (skriptID <= 0) {
								skriptID = ((EbaExitSprite) myCommandObject)
										.getAusgangID()
										* (-1);
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
				if (myCommand.equals("Подойди к ")) {
					myBefehlszeilenText = "";
					myCommandObject = null;
				} else {
					myBefehlszeilenText = myCommand;
				}
			}
		} else if (myDataID.equals("Inventar")) {
			// Do nothing
		} else { // DataID = Objekt
			if (myCommand == "") {
				myBefehlszeilenText = myDataID;
				if (isItem) {
					if (((EbaItemSprite) mySprite).getMyDefaultAktion().equals(
							"a")) {
						mySpriteAnsehen.setFrame(1);
						defaultAktion = 'a';
					}
					if (((EbaItemSprite) mySprite).getMyDefaultAktion().equals(
							"b")) {
						mySpriteBenutzen.setFrame(1);
						defaultAktion = 'b';
					}
					if (((EbaItemSprite) mySprite).getMyDefaultAktion().equals(
							"n")) {
						mySpriteNehmen.setFrame(1);
						defaultAktion = 'n';
					}
					if (((EbaItemSprite) mySprite).getMyDefaultAktion().equals(
							"r")) {
						mySpriteReden.setFrame(1);
						defaultAktion = 'r';
					}

				} else if (isExit) {
					myBefehlszeilenText = ("Подойди к "
							+ ((EbaExitSprite) mySprite).getMyMouseoverText());
				} else if (isGuiElement) {
					// Do nothing
				} else {
					if (((EbaInteractionSprite) mySprite).getMyDefaultAktion()
							.equals("a")) {
						mySpriteAnsehen.setFrame(1);
						defaultAktion = 'a';
					}
					if (((EbaInteractionSprite) mySprite).getMyDefaultAktion()
							.equals("b")) {
						mySpriteBenutzen.setFrame(1);
						defaultAktion = 'b';
					}
					if (((EbaInteractionSprite) mySprite).getMyDefaultAktion()
							.equals("n")) {
						mySpriteNehmen.setFrame(1);
						defaultAktion = 'n';
					}
					if (((EbaInteractionSprite) mySprite).getMyDefaultAktion()
							.equals("r")) {
						mySpriteReden.setFrame(1);
						defaultAktion = 'r';
					}
				}
			} else if ((myDataID.equals("Посмотри на ")) || (myDataID.equals("Используй "))
					|| (myDataID.equals("Возьми ")) || (myDataID.equals("Поговори с "))) {
				myBefehlszeilenText = myCommand;
			} else if (myCommandObject2 != null) {
				if (myCommandObject2.getDataID() == myDataID) {
					myBefehlszeilenText = myCommand;
				} else if (isExit) {
					// do nothing
				} else {
					myBefehlszeilenText = myCommand + myDataID;
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

			if (myDataID == "") {
				myEbaWalkableAreaMap.findWay((int) myCharacterSprite
						.getBaseX(), (int) myCharacterSprite.getBaseY(),
						getMouseX(), getMouseY());
				walkHere = (int[]) myEbaWalkableAreaMap.pop();
				myBefehlszeilenText = "Подойди к ";
				myCommand = "Подойди к ";
				isCommandComplete = true;
				myCharacterSprite.walkTo(walkHere[0], walkHere[1]);

			} else {

				if (isItem) {

					if (mySprite.getID() > 0) {

						if (myCommand == "" || myCommand == "Возьми "	|| myCommand == "Используй ") {
							if (((EbaItemSprite) mySprite).hasBenutzenSkript()) {
								myCommand = "Используй " + mySprite.getDataID();
								skriptID = ((EbaItemSprite) mySprite)
										.getBenutzenSkriptID();
								myScriptInterpreter.runScript(skriptID, 1);
								zeilennummer++;
								myCommand = "";
							} else {
								
								myCommand = "Используй " + mySprite.getDataID() + " c ";
								myCommandObject2 = mySprite;
							}
						} else if (myCommand == "Поговори с ") {
							myCommand = "Поговори с " + mySprite.getDataID();
							skriptID = ((EbaItemSprite) mySprite)
									.getRedenMitSkriptID();
							myCommand = "";
							myScriptInterpreter.runScript(skriptID, 1);
							zeilennummer++;

						} else if (myCommand == "Посмотри на ") {
							myCommand = "Посмотри на " + mySprite.getDataID();
							skriptID = ((EbaItemSprite) mySprite)
									.getAnsehenSkriptID();
							myScriptInterpreter.runScript(skriptID, 1);
							zeilennummer++;
							myCommand = "";
						} else {
							if (!(myCommand.startsWith("Подойди к"))) {
								if(myCommandObject2 != mySprite){
								myCommand = myCommand + mySprite.getDataID();
								skriptID = ((EbaItemSprite) myCommandObject2)
										.getInventarBenutzeMitSkriptID(mySprite
												.getID());
								myScriptInterpreter.runScript(skriptID, 1);
								zeilennummer++;
								myCommand = "";
								} else {
									
									myBefehlszeilenText = "";
									myCommandObject = null;
									myCommandObject2 = null;
									myCommand = "";
								}
							}
						}
					}
				} else if (mySprite.getDataID() == "Посмотри на ") {
					myCommand = "Посмотри на ";
				} else if (mySprite.getDataID() == "Возьми ") {
					myCommand = "Возьми ";
				} else if (mySprite.getDataID() == "Поговори с ") {
					myCommand = "Поговори с ";
				} else if (mySprite.getDataID() == "Используй ") {
					myCommand = "Используй ";
				} else if (mySprite.getDataID() == "Inventar") {
					// Do Nothing
				} else if (mySprite.getDataID() == "К Харви") {
					// Do Nothing
				} else { // ----mySprite ist ein Objekt--------

					if (myCommand != "" && isExit) {
						// do nothing
					} else {
						myCommandObject = mySprite;

						if (!isItem) {
							if (((EbaInteractionSprite) myCommandObject)
									.getWalkToX() < 0) {
								walkToX = (int) myCommandObject.getX();
								walkToY = (int) myCommandObject.getY();
							} else {
								walkToX = ((EbaInteractionSprite) myCommandObject)
										.getWalkToX();
								walkToY = ((EbaInteractionSprite) myCommandObject)
										.getWalkToY();
							}
							myEbaWalkableAreaMap.findWay(
									(int) myCharacterSprite.getBaseX(),
									(int) myCharacterSprite.getBaseY(),
									walkToX, walkToY);
							walkHere = (int[]) myEbaWalkableAreaMap.pop();

							if (isExit) {
								myCommand = myBefehlszeilenText;
								myCommandObjectName = ((EbaExitSprite) mySprite)
										.getMyMouseoverText();
							} else {

								if (myCommand == "") {
									myCommand = "Подойди к " + myDataID;
								} else {
									if (isCommandComplete) {
									} else {
										myCommand = myCommand + myDataID;
									}
								}
								myCommandObjectName = myDataID;
							}

							isCommandComplete = true;

							myCharacterSprite.walkTo(walkHere[0], walkHere[1],
									((EbaInteractionSprite) mySprite)
											.getStandbyBlickrichtung());
						}

					}
				}
			}

			myBefehlszeilenText = myCommand;

			break;
		default:
			// rechte Maustaste:
			if (myDataID == "") {
				myBefehlszeilenText = "";
				myCommand = "";
			} else {
				// TODO: Default-Aktion des Objekts ?
				// myBefehlszeilenText = myCommand;
			}
		}

		// ON MOUSE RELEASED:

		switch (bsInput.getMouseReleased()) {
		case BaseInput.NO_BUTTON:
			break;
		case MouseEvent.BUTTON1:
			// linke Maustaste:

			pressedButton = "";
			if (myDataID == "Посмотри на ") {
				myCommand = "Посмотри на ";
			} else if (myDataID == "Возьми ") {
				myCommand = "Возьми ";
			} else if (myDataID == "Поговори с ") {
				myCommand = "Поговори с ";
			} else if (myDataID == "Используй ") {
				myCommand = "Используй ";

			} else if (myDataID == "К Харви") {

				// NSC-Edna im Harvey-Raum auf Ednas jetzige Koordinaten platzieren
				int walkInPointX = -1;
				int walkInPointY = -1;

				int v = Integer.parseInt( ((Integer)(myEbaGameEngine.getMyRoomNumber())).toString().substring(0, 4));
				int q = Integer.parseInt( ((Integer)(myEbaGameEngine.getMyRoomNumber())).toString().substring(4, 6));

				int ednaNscID = (v*10000) + 9900 +q - 50;
				int harveyNscID = (v*10000) + 9900 +q;


				try {

					myStatement = EbaGameEngine.myConnection.createStatement();
			        myResultSet = myStatement.executeQuery("SELECT * FROM raumobjekt where ID=" + harveyNscID);
			        if(myResultSet.next()){

			        walkInPointX = myResultSet.getInt("PosX");
	                walkInPointY = myResultSet.getInt("PosY");

			        }
					myStatement = EbaGameEngine.myConnection.createStatement();
			        myStatement.executeUpdate("UPDATE raumobjekt SET posx = " + myCharacterSprite.getBaseX() + " where ID=" + ednaNscID );
			        myStatement.executeUpdate("UPDATE raumobjekt SET posy = " + myCharacterSprite.getBaseY() + " where ID=" + ednaNscID );

			        myStatement.executeUpdate("UPDATE raumobjektinteraktion SET walktox = " + (myCharacterSprite.getBaseX()-110) + " where RaumobjektID=" + ednaNscID );
			        myStatement.executeUpdate("UPDATE raumobjektinteraktion SET walktoy = " + (myCharacterSprite.getBaseY()) + " where RaumobjektID=" + ednaNscID );

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

		        if ( walkInPointX < 0 || walkInPointY < 0){

		        	myTextSprites.add(myCharacterSprite.say("Придется сначала подойти к нему.","audio/0000-00.wav"));

		        } else {

				int nextRoom = myEbaGameEngine.getMyRoomNumber()-50;


                String charakterBlickrichtung = "w";

                myEbaGameEngine.walkInPointX = walkInPointX;
                myEbaGameEngine.walkInPointY = walkInPointY;
                myEbaGameEngine.charakterBlickrichtung = charakterBlickrichtung;

                myEbaGameEngine.nextGameID = nextRoom;

                finish();
		        }

			}
			break;
		default:
			// rechte Maustaste:
			if (myDataID == "") {
				myBefehlszeilenText = "";
				myCommand = "";
				myCommandObject = null;
				myCommandObject2 = null;
				isCommandComplete = false;
			} else {

				if (!isItem) {

					if (myCommand == "") {

						if (defaultAktion != '0') {
							if (defaultAktion == 'a') {
								myCommand = "Посмотри на " + myDataID;
								isCommandComplete = true;
							}
							if (defaultAktion == 'b') {
								myCommand = "Используй " + myDataID;
								isCommandComplete = true;
							}
							if (defaultAktion == 'n') {
								myCommand = "Возьми " + myDataID;
								isCommandComplete = true;
							}
							if (defaultAktion == 'r') {
								myCommand = "Поговори с " + myDataID;
								isCommandComplete = true;
							}
							myBefehlszeilenText = myCommand;
							myCommandObject = mySprite;

							if (((EbaInteractionSprite) mySprite).getWalkToX() < 0) {
								walkToX = (int) mySprite.getX();
								walkToY = (int) mySprite.getY();
							} else {
								walkToX = ((EbaInteractionSprite) mySprite)
										.getWalkToX();
								walkToY = ((EbaInteractionSprite) mySprite)
										.getWalkToY();
							}
							myEbaWalkableAreaMap.findWay(
									(int) myCharacterSprite.getBaseX(),
									(int) myCharacterSprite.getBaseY(),
									walkToX, walkToY);
							walkHere = (int[]) myEbaWalkableAreaMap.pop();
							myCharacterSprite.walkTo(walkHere[0], walkHere[1],
									((EbaInteractionSprite) mySprite)
											.getStandbyBlickrichtung());
						}
					} else {
						myBefehlszeilenText = "";
						myCommand = "";
						myCommandObject = null;
						myCommandObject2 = null;
						isCommandComplete = false;
					}
				} else {
					if (defaultAktion != '0') {
						if (defaultAktion == 'a') {
							skriptID = ((EbaItemSprite) mySprite)
									.getAnsehenSkriptID();
						}
						if (defaultAktion == 'b') {
							if (((EbaItemSprite) mySprite).hasBenutzenSkript()) {
								skriptID = ((EbaItemSprite) mySprite)
										.getBenutzenSkriptID();
							} else {
								myCommand = "Используй " + mySprite.getDataID() + " c ";
								myCommandObject2 = mySprite;
							}
						}
						if (defaultAktion == 'n') {
							skriptID = ((EbaItemSprite) mySprite)
									.getBenutzenSkriptID();
						}
						if (defaultAktion == 'r') {
							skriptID = ((EbaItemSprite) mySprite)
									.getRedenMitSkriptID();
						}
						myScriptInterpreter.runScript(skriptID, 1);
						zeilennummer++;
					}
				}
			}
		}

		if ((!isCommandComplete) && (myDataID != "") && (myDataID != myCommandObjectName)) {

			myBefehlszeile.change(myBefehlszeilenText, false);

		} else {

			myBefehlszeile.change(myBefehlszeilenText, isCommandComplete);

		}

		}
	}

	// /////////////////////////////////////////////////////////
	// Private Methoden //
	// /////////////////////////////////////////////////////////


	void updateInventory() {
		for (int i=1;i<=maximaleItemPosition;i++){
	    	myGuiSprites.remove(myItemSprites[i]);
	    }
		myItemSprites = new EbaItemSprite[8];
		maximaleItemPosition = 0;
		try {
			myStatement = EbaGameEngine.myConnection.createStatement();
			myResultSet = myStatement.executeQuery("SELECT * FROM Inventarobjekt WHERE GuiID= 3 AND Inventarposition >0");

			while (myResultSet.next()) {
				int inventarX = 354 + ((myResultSet.getInt("Inventarposition")*50) );
				int inventarY = 555;


				myItem = new EbaItemSprite(new BufferedImage[] {
						Eba.getImage(myResultSet.getString("IconDatei")+ ".png"),
						Eba.getImage(myResultSet.getString("IconDatei")+ "_a.png") },
						inventarX,
						inventarY,
						myResultSet.getInt("ID"),
						myResultSet.getString("Bezeichnung"),
						myResultSet.getInt("Inventarposition"),
						myResultSet.getString("DefaultAktion"),
						myResultSet.getInt("AnsehenSkriptID"),
						myResultSet.getInt("BenutzenSkriptID"),
						myResultSet.getInt("RedenMitSkriptID"));
				myItem.setID(myResultSet.getInt("ID"));
				myItem.setDataID(myResultSet.getString("Bezeichnung"));
				myItem.setImmutable(true);
				myItem.setActive(true);
				myGuiSprites.add(myItem);
				maximaleItemPosition ++;
				myItemSprites[maximaleItemPosition]=(EbaItemSprite)myItem;
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
	}

	// /////////////////////////////////////////////////////////
	// �ffentliche Methoden //
	// /////////////////////////////////////////////////////////

	public void deactivateItem(int inventarObjektID) {

		try {

	    	//Merk dir die alte Position
	    	myStatement = EbaGameEngine.myConnection.createStatement();
	        myResultSet = myStatement.executeQuery("SELECT * FROM Inventarobjekt WHERE GuiID= 3 AND ID=" + inventarObjektID);
	        myResultSet.next();
	        int altePosition = myResultSet.getInt("Inventarposition");
	        //Setze die Inventarposition auf 0
	        myStatement.executeUpdate("UPDATE inventarobjekt SET inventarposition=0 where ID=" + inventarObjektID);
	        //Setze die Position aller Inventargegenst�nde deren Position gr�sser war als die alte Position um 1 hinab
	        myResultSet = myStatement.executeQuery("SELECT * FROM Inventarobjekt WHERE GuiID= 3 AND Inventarposition >0");
			while (myResultSet.next()) {
				if(myResultSet.getInt("Inventarposition") >= altePosition){
				myStatement.executeUpdate("UPDATE inventarobjekt SET inventarposition="+ (myResultSet.getInt("Inventarposition")-1) +" where ID=" + myResultSet.getInt("ID"));
				}
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
		updateInventory();

	}

	public void activateItem(int inventarObjektID) {

		maximaleItemPosition++;
		try {
	        myStatement = EbaGameEngine.myConnection.createStatement();
	        myStatement.executeUpdate("UPDATE inventarobjekt SET inventarposition="+ maximaleItemPosition +" where ID=" + inventarObjektID);

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

		updateInventory();

	}
}
