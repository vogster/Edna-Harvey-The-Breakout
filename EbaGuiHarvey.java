package de.daedalic.eba;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.golden.gamedev.engine.BaseInput;
import com.golden.gamedev.object.AnimatedSprite;
import com.golden.gamedev.object.Sprite;

public class EbaGuiHarvey extends EbaGameObject {

	private Sprite mySprite;

	private AnimatedSprite mySpriteTopicleiste;

	private AnimatedSprite mySpriteZuEdna;
	private AnimatedSprite mySpriteZuEdnaInactive;
	private AnimatedSprite mySpriteEdnasInventar;

	private String pressedButton = "";

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
	private ResultSet myResultSet = null;
	ResultSet myResultSet1 = null;
	private Sprite myTopicCursorSprite;

	final int STARTDRAG = 1;
	final int DRAGGING = 2;
	final int DROPPED = 0;
	final int STARTDROP = 3;

	boolean alreadyWalking = false;

	private EbaTopicSprite myTopic;

	public EbaGuiHarvey(EbaGameEngine myGameEngine) {
		super(myGameEngine);
	}

	public void initResources() {
		super.initResources();

		myTopicCursorSprite = new Sprite(Eba.getImage("gui/ednajung/b_ansehen.png"));
		myTopicCursorSprite.setImmutable(true);
		myTopicCursorSprite.setActive(false);
		myPlayField.add(myTopicCursorSprite);

		mySpriteTopicleiste = new AnimatedSprite(new BufferedImage[] { Eba.getImage("gui/harvey/topicleiste.png"), Eba.getImage("gui/harvey/topicleiste_i.png") }, 0, 551);
		mySpriteTopicleiste.setDataID("Topicleiste");
		myGuiSprites.add(mySpriteTopicleiste);

		mySpriteZuEdna = new AnimatedSprite(new BufferedImage[] { Eba.getImage("gui/harvey/b_zuedna.png"), Eba.getImage("gui/harvey/b_zuedna_a.png"), Eba.getImage("gui/harvey/b_zuedna_p.png"), Eba.getImage("gui/harvey/b_zuedna_i.png") }, 698, 534);
		mySpriteZuEdna.setDataID("К Эдне");
		myGuiSprites.add(mySpriteZuEdna);
		mySpriteZuEdna.setImmutable(true);

		mySpriteZuEdnaInactive = new AnimatedSprite(new BufferedImage[] { Eba.getImage("gui/harvey/b_zuedna_i.png") }, 698, 534);
		mySpriteZuEdnaInactive.setDataID("");
		myGuiSprites.add(mySpriteZuEdnaInactive);
		mySpriteZuEdnaInactive.setImmutable(true);
		mySpriteZuEdnaInactive.setActive(false);
		
		int walkInPointX = -1;
		int walkInPointY = -1;
		int v = Integer.parseInt(((Integer) (myEbaGameEngine.getMyRoomNumber())).toString().substring(0, 4));
		int q = Integer.parseInt(((Integer) (myEbaGameEngine.getMyRoomNumber())).toString().substring(4, 6));
		int ednaNscID = (v * 10000) + 9900 + q;
		
		
		
		try {

			myStatement = EbaGameEngine.myConnection.createStatement();
			myResultSet = myStatement.executeQuery("SELECT * FROM raumobjekt where ID=" + ednaNscID);
			if (myResultSet.next()) {
				walkInPointX = myResultSet.getInt("PosX");
				walkInPointY = myResultSet.getInt("PosY");
			}


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
		
		if (walkInPointX < 0 || walkInPointY < 0) {
			

			mySpriteZuEdna.setActive(false);
			
			mySpriteZuEdnaInactive.setActive(true);

		}

		
		mySpriteEdnasInventar = new AnimatedSprite(new BufferedImage[] { Eba.getImage("gui/harvey/ednasinventar.png"), Eba.getImage("gui/harvey/ednasinventar_i.png") }, 630, 0);
		mySpriteEdnasInventar.setDataID("EdnasInventar");
		myGuiSprites.add(mySpriteEdnasInventar);
		mySpriteEdnasInventar.setActive(false);
		mySpriteEdnasInventar.setImmutable(true);

//		myBefehlszeile = new EbaCommandPromptSprite(myActiveFont, myInactiveFont, fontManager, 10, 520);
//		myTextSprites.add(myBefehlszeile);
//
//
//		myScriptInterpreter = new EbaScriptInterpreter(myCharacterSprite, myBackgroundSprites, myObjectSprites, myTextSprites, myGuiSprites, myChoiceList, myEbaGameEngine, this);

		try {
			myStatement = EbaGameEngine.myConnection.createStatement();

				int nn=0;
				String tmpRoomNumer = Integer.toString(myEbaGameEngine.myRoomNumber).substring(0,1)+"%";
				myResultSet1 = myStatement.executeQuery("SELECT * from Topic where raumobjektID  like '" +tmpRoomNumer+"'");

				while (myResultSet1.next()) {
					nn++;
					EbaTopicSprite myTopicSprite = new EbaTopicSprite(new BufferedImage[] {
							Eba.getImage(myResultSet1.getString("IconDatei"))},
							0,
							0, myResultSet1.getInt("ID"),
							myResultSet1.getString("Bezeichnung"),
							myResultSet1.getInt("Inventarposition"),
							myResultSet1.getInt("Topicleistenposition"),
							myResultSet1.getInt("SkriptID"),
							myResultSet1.getInt("RaumobjektID"));

					myTopicSprite.setDataID(myResultSet1.getString("Bezeichnung"));
					myTopicSprite.setID(myResultSet1.getInt("ID"));

					myTopicSprite.setImmutable(true);
					if (myTopicSprite.getTopicposition() > 0) {
						myTopicSprite.setY(555);
						myTopicSprite.setX((50*myTopicSprite.getTopicposition())-47);
						myTopicSprite.setActive(true);
					} else {

						myTopicSprite.setActive(false);
					}

					myTopicSprites.add(myTopicSprite);
				}

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

		myDataID = "";
		
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

		
		
		if ( myScriptInterpreter.isRunning() || myMenueSprites.isActive() || myChoiceList.isActive()) {


		}else{
		
		
		mySprite = checkPosMouse(myTopicSprites, true);

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
			if(!isTopic){	//Kann der Sprite bereits in TopicSprites gefunden und trotzdem kein Topic sein?
			isGuiElement = true;
			}
		}

		if (mySprite == null) {
			mySprite = checkPosMouse(myBackgroundSprites, true);
		}

		if (mySprite == null) {
			myDataID = "";
		} else {
			if (myDragStatus == DRAGGING) {
				if (mySprite.getDataID().equals("Topicleiste")) {
					myDataID = "Положить";
				}
				if (mySprite.getDataID().equals("Эдна")) {
					myDataID = "Эдна";
				}
			} else {
				if (mySprite.getDataID().equals("Выход")) {
					isExit = true;
				}
				myDataID = (String) mySprite.getDataID();
			}
		}

		

		mySpriteZuEdna.setFrame(0);
		
		if (pressedButton.equals("К Эдне")) {
			mySpriteZuEdna.setFrame(2);		
		}
		

		if (myDataID == "К Эдне") {
			mySpriteZuEdna.setFrame(1);
			myCommand = "К Эдне";
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

					} else if (myCommand.startsWith("Поговори с Эдной ")) {
						myCommand = "";
						isCommandComplete = false;
						skriptID = myTopic.getSkriptID();
						myScriptInterpreter.runScript(skriptID, 1);
						zeilennummer++;
						myDragStatus = DROPPED;
						myTopic=null;
						alreadyWalking = false;
						
					} else if (myCommand.startsWith("Что такое ")) {

						myCommand = "";
						isCommandComplete = false;
						if (myCommandObject == null) {
						} else {
							((EbaTopicSprite)myCommandObject).getRaumobjektID();


							try {
			                	myStatement = EbaGameEngine.myConnection.createStatement();
			                	myResultSet = myStatement.executeQuery("SELECT * FROM Raumobjektinteraktion WHERE RaumobjektID = "+((EbaTopicSprite)myCommandObject).getRaumobjektID());
			                	myResultSet.next();

			                	skriptID = myResultSet.getInt("BenutzenskriptID");

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
					
					} else if(myCommand.equals("К Эдне")){
						// Do Nothing
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
				} else if (myCommand.equals("К Эдне")){	
					myCommand = "";
					myBefehlszeilenText = "";
				} else {
					myBefehlszeilenText = myCommand;
				}
			}
		} else if (myDataID.equals("EdnasInventar") || myDataID.equals("Topicleiste")) {
			// do nothing
		} else if (myDataID.equals("К Эдне")) {
			
			myBefehlszeilenText = "К Эдне";
			
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

			if(!isExit){
			myDragStatus = STARTDRAG;
			}
			if (myDataID == "") {
				myEbaWalkableAreaMap.findWay((int) myCharacterSprite.getBaseX(), (int) myCharacterSprite.getBaseY(), getMouseX(), getMouseY());
				walkHere = (int[]) myEbaWalkableAreaMap.pop();
				myBefehlszeilenText = "Подойди к ";
				myCommand = "Подойди к ";
				isCommandComplete = true;
				myCharacterSprite.walkTo(walkHere[0], walkHere[1]);
				
				
				
			} else {

				//myCommandObject = mySprite;

				if (isTopic) {

					if (mySprite.getID() > 0) {
						if (myCommand == "") {
							// TODO: Подойди к Edna und Поговори с Edna �ber dieses Topic
						}
					}

				} else if (mySprite.getDataID().equals("Inventar")) {
					// Do Nothing
				} else if (mySprite.getDataID().equals("К Эдне")) {
										
					pressedButton = "К Эдне";
					mySpriteZuEdna.setFrame(2);
					myCommand = "К Эдне";
					myBefehlszeilenText = "К Эдне";
					isCommandComplete = true;
					
					
				} else if (mySprite.getDataID().equals("Эдна")) {

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
					myCommandObjectName = "Эдна";

					isCommandComplete = true;

					myCharacterSprite.walkTo(walkHere[0], walkHere[1], ((EbaInteractionSprite) mySprite).getStandbyBlickrichtung());

				} else { // ----mySprite ist ein Objekt--------

					if (myCommand != "" && isExit) {
						// do nothing

					} else {

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
						
			break;

		default:
			// rechte Maustaste:
			if (myDataID == "") {
				myBefehlszeilenText = "";
				myCommand = "";
			}
//			} else if (isTopic || isGuiElement || isExit) {
//				//do nothing
//			} else {
//
//				myCommandObject = mySprite;
//				myCommand = "Посмотри на " + myDataID;
//
//				if (((EbaInteractionSprite) myCommandObject).getWalkToX() < 0) {
//					walkToX = (int) myCommandObject.getX();
//					walkToY = (int) myCommandObject.getY();
//				} else {
//					walkToX = ((EbaInteractionSprite) myCommandObject).getWalkToX();
//					walkToY = ((EbaInteractionSprite) myCommandObject).getWalkToY();
//				}
//				myEbaWalkableAreaMap.findWay((int) myCharacterSprite.getBaseX(), (int)myCharacterSprite.getBaseY(), walkToX, walkToY);
//				walkHere = (int[]) myEbaWalkableAreaMap.pop();
//
//				myBefehlszeilenText = myCommand;
//				myCommandObjectName = myDataID;
//				isCommandComplete = true;
//
//				myCharacterSprite.walkTo(walkHere[0], walkHere[1], ((EbaInteractionSprite) mySprite).getStandbyBlickrichtung());
//			}
		}

		// ON MOUSE RELEASED:

		switch (bsInput.getMouseReleased()) {
		case BaseInput.NO_BUTTON:
			break;
		case MouseEvent.BUTTON1:
			// linke Maustaste:
			myDragStatus = STARTDROP;
			pressedButton = "";

			if (myCommand == "К Эдне") {

				// NSC-Harvey im Edna-Raum auf Harveys jetzige Koordinaten platzieren und Raum wechseln
				int walkInPointX = -1;
				int walkInPointY = -1;

				int v = Integer.parseInt(((Integer) (myEbaGameEngine.getMyRoomNumber())).toString().substring(0, 4));
				int q = Integer.parseInt(((Integer) (myEbaGameEngine.getMyRoomNumber())).toString().substring(4, 6));

				int harveyNscID = (v * 10000) + 9900 + q + 50;
				int ednaNscID = (v * 10000) + 9900 + q;
				
				
				
				try {

					myStatement = EbaGameEngine.myConnection.createStatement();
					myResultSet = myStatement.executeQuery("SELECT * FROM raumobjekt where ID=" + ednaNscID);
					if (myResultSet.next()) {

						walkInPointX = myResultSet.getInt("PosX");
						walkInPointY = myResultSet.getInt("PosY");

						myStatement = EbaGameEngine.myConnection.createStatement();
						myStatement.executeUpdate("UPDATE raumobjekt SET posx = " + myCharacterSprite.getBaseX() + " where ID=" + harveyNscID);
						myStatement.executeUpdate("UPDATE raumobjekt SET posy = " + myCharacterSprite.getBaseY() + " where ID=" + harveyNscID);

						myStatement.executeUpdate("UPDATE raumobjektinteraktion SET walktox = " + (myCharacterSprite.getBaseX()-110) + " where RaumobjektID=" + harveyNscID );
				        myStatement.executeUpdate("UPDATE raumobjektinteraktion SET walktoy = " + (myCharacterSprite.getBaseY()) + " where RaumobjektID=" + harveyNscID );


						myStatement.close();

					}


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
				
				if (walkInPointX < 0 || walkInPointY < 0) {
					
					myEbaGameEngine.bsSound.play("audio/soundfx/huuup000.wav");

				} else {

					int nextRoom = myEbaGameEngine.getMyRoomNumber() + 50;

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
				isCommandComplete = false;
			
			} else if (isGuiElement || isExit){
//				do nothing
			} else if (isTopic){
				myTopic=(EbaTopicSprite)mySprite;
			   
				if (myTopic.getTopicposition()>0) {

					if (myCommand == "") {

						// TODO: Ansehen-Sktript
						// myBefehlszeilenText = myCommand;

						myCommand = "Что такое " + myDataID;
						myCommandObject = mySprite;
						isCommandComplete = true;

					} else {
						
						myBefehlszeilenText = "";
						myCommand = "";
						myCommandObject = null;
						isCommandComplete = false;
						
					}
				}
			} else {
				myCommandObject = mySprite;
				myCommand = "Посмотри на " + myDataID;

				if (((EbaInteractionSprite) myCommandObject).getWalkToX() < 0) {
					walkToX = (int) myCommandObject.getX();
					walkToY = (int) myCommandObject.getY();
				} else {
					walkToX = ((EbaInteractionSprite) myCommandObject).getWalkToX();
					walkToY = ((EbaInteractionSprite) myCommandObject).getWalkToY();
				}
				myEbaWalkableAreaMap.findWay((int) myCharacterSprite.getBaseX(), (int)myCharacterSprite.getBaseY(), walkToX, walkToY);
				walkHere = (int[]) myEbaWalkableAreaMap.pop();

				myBefehlszeilenText = myCommand;
				myCommandObjectName = myDataID;
				isCommandComplete = true;

				myCharacterSprite.walkTo(walkHere[0], walkHere[1], ((EbaInteractionSprite) mySprite).getStandbyBlickrichtung());
			}
		

		}




		if ((!isCommandComplete) && (myDataID != "") && (myDataID != myCommandObjectName)) {

			myBefehlszeile.change(myBefehlszeilenText, false);

		} else {
			myBefehlszeile.change(myBefehlszeilenText, isCommandComplete);

		}

		if (bsInput.isMouseDown(1) && myDragStatus == STARTDRAG) {



				Sprite myTmpTopic[];
				myTmpTopic = myTopicSprites.getSprites();
				if (mySprite != null){
					for (int i = 0; i < myTopicSprites.getSize(); i++) {

						if(((EbaTopicSprite)myTmpTopic[i]).getRaumobjektID() == mySprite.getID()){

							if(((EbaTopicSprite)myTmpTopic[i]).getTopicposition()>0){
								// do nothing
							}else{
								myTopic = (EbaTopicSprite)myTmpTopic[i];
								myTopicCursorSprite.setImage(myTopic.getImage());

								myStandardCursorSprite.setActive(false);
								myCursorSprite = myTopicCursorSprite;
								myCursorSprite.setActive(true);
							}

						}
					}
				}


				myTmpTopic = myTopicSprites.getSprites();
				for (int i = 0; i < myTopicSprites.getSize(); i++) {

					if(((EbaTopicSprite)myTmpTopic[i])== mySprite){

						myTopic = (EbaTopicSprite)myTmpTopic[i];

						if(myTopic.getTopicposition()>0){

							myTopicCursorSprite.setImage(myTopic.getImage());

							myStandardCursorSprite.setActive(false);
							myCursorSprite = myTopicCursorSprite;
							myCursorSprite.setActive(true);
							myTopic.setTopicposition(0);
							myTopic.setActive(false);
							myTopic.setX(0);
							myTopic.setY(0);

						}else{
						}

					}
				}

			myDragStatus = DRAGGING;
			myTmpTopic = null;

		}

		if ((myDragStatus == STARTDROP)&&(myTopic != null)) {
			myTopicCursorSprite.setActive(false);
			myCursorSprite = myStandardCursorSprite;
			myCursorSprite.setActive(true);

			if (myDataID.equals("Положить")|| isTopic) {
				int slotNummer = (int)(Math.floor(getMouseX()/50)+1);
				int besetzt = 0;
				boolean istDa=false;
				Sprite myTmpTopic[];
				myTmpTopic = myTopicSprites.getSprites();
				while(besetzt<14){
					for (int i = 0; i < myTopicSprites.getSize(); i++) {
						if(((EbaTopicSprite)myTmpTopic[i]).getTopicposition() == slotNummer){
							istDa = true;
						}
					}
					if (istDa){
						besetzt++;
						if (slotNummer<14){
							
							
							
							slotNummer++;
						}else{
							slotNummer=1;
						}
					} else {
						myTopic.setY(555);
						myTopic.setX((50*slotNummer)-47);
						myTopic.setActive(true);
						myTopic.setTopicposition(slotNummer);
						besetzt=14;
					}
					istDa=false;
				}
			}
			
			
			else if (myDataID.equals("Эдна") && !alreadyWalking) {
				myTopic.setY(0);
				myTopic.setX(0);
				myTopic.setActive(false);
				myTopic.setTopicposition(0);
				//myPlayField.update(elapsedTime);

				myCommandObject = mySprite;
				myCommand = "Поговори с Эдной о " + myTopic.getBezeichnung();

				if (((EbaInteractionSprite) myCommandObject).getWalkToX() < 0) {
					walkToX = (int) myCommandObject.getX();
					walkToY = (int) myCommandObject.getY();
				} else {
					walkToX = ((EbaInteractionSprite) myCommandObject).getWalkToX();
					walkToY = ((EbaInteractionSprite) myCommandObject).getWalkToY();
				}
				myEbaWalkableAreaMap.findWay((int) myCharacterSprite.getBaseX(), (int) myCharacterSprite.getBaseY(), walkToX, walkToY);
				walkHere = (int[]) myEbaWalkableAreaMap.pop();

				myBefehlszeilenText = myCommand;
				myCommandObjectName = myDataID;
				isCommandComplete = true;

				myCharacterSprite.walkTo(walkHere[0], walkHere[1], ((EbaInteractionSprite) mySprite).getStandbyBlickrichtung());
				alreadyWalking = true;
				
			} else {
				myTopic.setY(0);
				myTopic.setX(0);
				myTopic.setActive(false);
				myTopic.setTopicposition(0);
			}
			if(!myCommand.startsWith("Поговори с Эдной")){
			myDragStatus = DROPPED;
			myTopic=null;
			}
		}
		
		}
		//myPlayField.update(elapsedTime);

	}

	// /////////////////////////////////////////////////////////
	// Private Methoden //
	// /////////////////////////////////////////////////////////

	void updateInventory() {

	}

	// /////////////////////////////////////////////////////////
	// �ffentliche Methoden //
	// /////////////////////////////////////////////////////////

	public void deactivateItem(int inventarObjektID) {

	}

	public void activateItem(int inventarObjektID) {

		maximaleItemPosition = 0;

		try {
			myStatement = EbaGameEngine.myConnection.createStatement();
			myResultSet = myStatement.executeQuery("Select * From inventarobjekt where guiid = '3' and inventarposition >'0'");
			while (myResultSet.next()){
			maximaleItemPosition++;
			}

	        myStatement.executeUpdate("UPDATE inventarobjekt SET inventarposition="+ (maximaleItemPosition+1) +" where ID=" + inventarObjektID);

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
}
