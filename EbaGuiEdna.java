package de.daedalic.eba;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.golden.gamedev.engine.BaseInput;
import com.golden.gamedev.object.AnimatedSprite;
import com.golden.gamedev.object.Sprite;

public class EbaGuiEdna extends EbaGameObject {

	private Sprite mySprite;
	private String myDataID;
	private String myBefehlszeilenText = "";
	private String myCommandObjectName = "";
	private String myCommand = "";

	private AnimatedSprite myInventory;
	private AnimatedSprite mySpriteAnsehen;
	private AnimatedSprite mySpriteNehmen;
	private AnimatedSprite mySpriteReden;
	private AnimatedSprite mySpriteBenutzen;

	//private boolean isCommandComplete = false;
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
	private AnimatedSprite mySpriteSchloss;

	Sprite myTmpSprite[];

	public int maximaleItemPosition = 0;
	private AnimatedSprite mySpriteUp;
	private AnimatedSprite mySpriteDown;
	private int numberOfInventoryShifts = 0; // Index der Inventarzeilenverschiebung bei �berf�lltem Inventar



	public int INVENTARZEILENHOEHE = 78;
	public int INVENTARZEILENBREITE = 75;
	public int YUNTERSTEINVENTARZEILE= 523;
	public int XUNTERSTEINVENTARZEILE= 401;
	public EbaItemSprite[] myItemSprites = new EbaItemSprite[100];



	public EbaGuiEdna(EbaGameEngine myGameEngine) {
		super(myGameEngine);

	}

	public void initResources() {
		super.initResources();


		//------------------------------------------------------------
		//----------- Initialisierung der Schaltfl�chen --------------
		//------------------------------------------------------------

		mySpriteAnsehen = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/edna/b_ansehen.png"),
				Eba.getImage("gui/edna/b_ansehen_a.png"),
				Eba.getImage("gui/edna/b_ansehen_p.png") }, 0, 565);
		mySpriteAnsehen.setDataID("Посмотри на ");
		myGuiSprites.add(mySpriteAnsehen);

		mySpriteNehmen = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/edna/b_nehmen.png"),
				Eba.getImage("gui/edna/b_nehmen_a.png"),
				Eba.getImage("gui/edna/b_nehmen_p.png") }, 100, 565);
		mySpriteNehmen.setDataID("Возьми ");
		myGuiSprites.add(mySpriteNehmen);

		mySpriteReden = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/edna/b_reden.png"),
				Eba.getImage("gui/edna/b_reden_a.png"),
				Eba.getImage("gui/edna/b_reden_p.png") }, 200, 565);
		mySpriteReden.setDataID("Поговори с ");
		myGuiSprites.add(mySpriteReden);

		mySpriteBenutzen = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/edna/b_benutzen.png"),
				Eba.getImage("gui/edna/b_benutzen_a.png"),
				Eba.getImage("gui/edna/b_benutzen_p.png") }, 300, 565);
		mySpriteBenutzen.setDataID("Используй ");
		myGuiSprites.add(mySpriteBenutzen);

		myInventory = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/edna/b_inventar.png"),
				Eba.getImage("gui/edna/b_inventar_a.png"),
				Eba.getImage("gui/edna/b_inventar_p.png"),
				Eba.getImage("gui/edna/gui_inventarani_1.png"),
				Eba.getImage("gui/edna/gui_inventarani_2.png"),
				Eba.getImage("gui/edna/gui_inventarani_3.png"),
				Eba.getImage("gui/edna/gui_inventarani_4.png"),
				Eba.getImage("gui/edna/gui_inventarani_5.png"),
				Eba.getImage("gui/edna/gui_inventarani_6.png"),
				Eba.getImage("gui/edna/inventar_offen.png") },400, 0);
		myInventory.setDataID("Inventar");
		myGuiSprites.add(myInventory);

		updateInventory();

		mySpriteUp = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/edna/b_up.png"),
				Eba.getImage("gui/edna/b_up_a.png"),
				Eba.getImage("gui/edna/b_up_p.png") }, 774, 53);
		mySpriteUp.setDataID("up");
		mySpriteUp.setImmutable(true);
		mySpriteUp.setActive(false);
		myGuiSprites.add(mySpriteUp);

		mySpriteDown = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/edna/b_down.png"),
				Eba.getImage("gui/edna/b_down_a.png"),
				Eba.getImage("gui/edna/b_down_p.png") }, 774, 522);
		mySpriteDown.setDataID("down");
		mySpriteDown.setImmutable(true);
		mySpriteDown.setActive(false);
		myGuiSprites.add(mySpriteDown);


		mySpriteSchloss = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/edna/b_keinschloss.png"),
				Eba.getImage("gui/edna/b_keinschloss_a.png"),
				Eba.getImage("gui/edna/b_keinschloss_p.png"),
				Eba.getImage("gui/edna/b_schloss.png"),
				Eba.getImage("gui/edna/b_schloss_a.png"),
				Eba.getImage("gui/edna/b_schloss_p.png") }, 755, 568);
		mySpriteSchloss.setDataID("Schloss_zu");
		mySpriteSchloss.setFrame(4);
		myGuiSprites.add(mySpriteSchloss);



//		myScriptInterpreter = new EbaScriptInterpreter(myCharacterSprite,
//				myBackgroundSprites, myObjectSprites, myTextSprites,
//				myGuiSprites, myChoiceList, myEbaGameEngine, this);

		myTmpSprite = new Sprite[myGuiSprites.getSize()];

	}

	public void update(long elapsedTime) {
		super.update(elapsedTime);

		defaultAktion = '0';

		if(!myScriptInterpreter.isRunning() && myCharacterSprite.isUnterbrochen()){
			isCommandComplete = false;
			myCommand = "";
		}

		if(isItem){
			isItem = false;
		}

		if(isExit){
			isExit = false;
		}

		if(isGuiElement){
			isGuiElement = false;
		}

		if ( myScriptInterpreter.isRunning() || myMenueSprites.isActive() || myChoiceList.isActive()) {

			if(isInventoryOpen){
				myInventory.setFrame(0);
				myInventory.setDataID("Inventar");
				mySpriteUp.setActive(false);
				mySpriteDown.setActive(false);
				isInventoryOpen = false;
				}
				Sprite myTmpSprite[] = new Sprite[myGuiSprites.getSize()];
				myTmpSprite = myGuiSprites.getSprites();
				for (int i = 0; i < myGuiSprites.getSize(); i++) {
					if (myTmpSprite[i].getID() > 0) {
						myTmpSprite[i].setActive(false);
					}
				}
		}else{

			if(isInventoryOpen){
			myTmpSprite = myGuiSprites.getSprites();
			for (int i = 0; i < myGuiSprites.getSize(); i++) {
				if (myTmpSprite[i].getID() > 0) {
					((EbaItemSprite) myTmpSprite[i]).setFrame(0);
				}
			}
			}

			//	ON MOUSE OVER:

			mySprite = checkPosMouse(myGuiSprites, true);

			if (mySprite == null) {
				mySprite = checkPosMouseInteraction(myObjectSprites, true);
			} else {
				if (mySprite.getID() > 0) {
					// Nur Inventar-Items d�rfen in der GUI-Sprites Spritegroup IDs > 0 bekommen
					((AnimatedSprite) mySprite).setFrame(1);
					isItem = true;
				} else {
					isGuiElement = true;
				}
			}

			if (mySprite == null) {
				mySprite = checkPosMouse(myBackgroundSprites, true);
			}

			if (mySprite == null) {
				myDataID = "";
			} else {
				if(!isInventoryOpen && mySprite.getDataID().equals("Выход")){
					isExit = true;
				}
				myDataID = (String) mySprite.getDataID();
			}

			if(isInventoryOpen && !isItem && !isGuiElement){
				myDataID = "";
			}

			if (myEbaGameEngine.isInventoryLocked){
					mySpriteAnsehen.setFrame(0);
					mySpriteNehmen.setFrame(0);
					mySpriteReden.setFrame(0);
					mySpriteBenutzen.setFrame(0);
					mySpriteUp.setFrame(0);
					mySpriteDown.setFrame(0);
					if (!isInventoryOpen){
						myInventory.setFrame(0);
					}
					mySpriteSchloss.setFrame(4);

				if (bsInput.isMouseDown(MouseEvent.BUTTON1)) {
					if (myDataID == "Посмотри на ") {
						pressedButton = "Посмотри на ";
						mySpriteAnsehen.setFrame(2);
					} else if (myDataID == "Возьми ") {
						pressedButton = "Возьми ";
						mySpriteNehmen.setFrame(2);
					} else if (myDataID == "Поговори с ") {
						pressedButton = "Поговори с ";
						mySpriteReden.setFrame(2);
					} else if (myDataID == "Используй ") {
						pressedButton = "Используй ";
						mySpriteBenutzen.setFrame(2);
					} else if (myDataID == "Inventar") {
						pressedButton = "Inventar";
						myInventory.setFrame(2);
					} else if (myDataID == "up") {
						pressedButton = "up";
						mySpriteUp.setFrame(2);
					} else if (myDataID == "down") {
						pressedButton = "down";
						mySpriteDown.setFrame(2);
					} else if (myDataID == "Schloss_zu") {
						pressedButton = "Schloss_zu";
						mySpriteSchloss.setFrame(6);
					} else if (mySpriteAnsehen.getDataID() == pressedButton) {
						mySpriteAnsehen.setFrame(2);
					} else if (mySpriteNehmen.getDataID() == pressedButton) {
						mySpriteNehmen.setFrame(2);
					} else if (mySpriteReden.getDataID() == pressedButton) {
						mySpriteReden.setFrame(2);
					} else if (mySpriteBenutzen.getDataID() == pressedButton) {
						mySpriteBenutzen.setFrame(2);
					} else if (myInventory.getDataID() == pressedButton) {
						myInventory.setFrame(2);
					} else if (mySpriteUp.getDataID() == pressedButton) {
						mySpriteUp.setFrame(2);
					} else if (mySpriteDown.getDataID() == pressedButton) {
						mySpriteDown.setFrame(2);
					} else if (mySpriteSchloss.getDataID() == pressedButton) {
						mySpriteSchloss.setFrame(6);
					}
				} else {
					if (myDataID == "Посмотри на ") {
						mySpriteAnsehen.setFrame(1);
					} else if (myDataID == "Возьми ") {
						mySpriteNehmen.setFrame(1);
					} else if (myDataID == "Поговори с ") {
						mySpriteReden.setFrame(1);
					} else if (myDataID == "Используй ") {
						mySpriteBenutzen.setFrame(1);
					} else if (myDataID == "Inventar") {
						if (!isInventoryOpen){
							myInventory.setFrame(1);
						}
					} else if (myDataID == "up") {
						pressedButton = "up";
						mySpriteUp.setFrame(1);
					} else if (myDataID == "down") {
						pressedButton = "down";
						mySpriteDown.setFrame(1);
					} else if (myDataID == "Schloss_zu") {
						mySpriteSchloss.setFrame(5);
					} else {
					}
				}

			}else{
					mySpriteAnsehen.setFrame(0);
					mySpriteNehmen.setFrame(0);
					mySpriteReden.setFrame(0);
					mySpriteBenutzen.setFrame(0);
					mySpriteSchloss.setFrame(0);
					mySpriteUp.setFrame(0);
					mySpriteDown.setFrame(0);

				if (bsInput.isMouseDown(MouseEvent.BUTTON1)) {
					if (myDataID == "Посмотри на ") {
						pressedButton = "Посмотри на ";
						mySpriteAnsehen.setFrame(2);
					} else if (myDataID == "Возьми ") {
						pressedButton = "Возьми ";
						mySpriteNehmen.setFrame(2);
					} else if (myDataID == "Поговори с ") {
						pressedButton = "Поговори с ";
						mySpriteReden.setFrame(2);
					} else if (myDataID == "Используй ") {
						pressedButton = "Используй ";
						mySpriteBenutzen.setFrame(2);
					} else if (myDataID == "up") {
						pressedButton = "up";
						mySpriteUp.setFrame(2);
					} else if (myDataID == "down") {
						pressedButton = "down";
						mySpriteDown.setFrame(2);
					} else if (myDataID == "Schloss_auf") {
						pressedButton = "Schloss_auf";
						mySpriteSchloss.setFrame(2);
					} else if (mySpriteAnsehen.getDataID() == pressedButton) {
						mySpriteAnsehen.setFrame(2);
					} else if (mySpriteNehmen.getDataID() == pressedButton) {
						mySpriteNehmen.setFrame(2);
					} else if (mySpriteReden.getDataID() == pressedButton) {
						mySpriteReden.setFrame(2);
					} else if (mySpriteBenutzen.getDataID() == pressedButton) {
						mySpriteBenutzen.setFrame(2);
					} else if (mySpriteUp.getDataID() == pressedButton) {
						mySpriteUp.setFrame(2);
					} else if (mySpriteDown.getDataID() == pressedButton) {
						mySpriteDown.setFrame(2);
					} else if (mySpriteSchloss.getDataID() == pressedButton) {
						mySpriteSchloss.setFrame(2);
					}
				} else {
					if (myDataID == "Посмотри на ") {
						mySpriteAnsehen.setFrame(1);
					} else if (myDataID == "Возьми ") {
						mySpriteNehmen.setFrame(1);
					} else if (myDataID == "Поговори с ") {
						mySpriteReden.setFrame(1);
					} else if (myDataID == "Используй ") {
						mySpriteBenutzen.setFrame(1);
					} else if (myDataID == "up") {
						pressedButton = "up";
						mySpriteUp.setFrame(1);
					} else if (myDataID == "down") {
						pressedButton = "down";
						mySpriteDown.setFrame(1);
					} else if (myDataID == "Schloss_auf") {
						mySpriteSchloss.setFrame(1);
					} else {
					}
				}

				if (myDataID == "Inventar") {
					myInventory.setDataID("Inventar�ffnend");
					myInventory.setAnimate(true);
					isInventoryOpen = true;

				}


			}

			if (isInventoryOpen) {

				if (myInventory.getDataID() == "InventarOffen") {

					if ((getMouseX() < 390) && (getMouseY() < 560)) {

						// Inventory schliessen

						myInventory.setFrame(0);
						mySpriteUp.setActive(false);
						mySpriteDown.setActive(false);
						myInventory.setDataID("Inventar");
						isInventoryOpen = false;
						myTmpSprite = myGuiSprites.getSprites();
						for (int i = 0; i < myGuiSprites.getSize(); i++) {
							if (myTmpSprite[i].getID() > 0) {
								myTmpSprite[i].setActive(false);
							}
						}
					}


				} else {
					if (!myInventory.isAnimate()) {

						while(numberOfInventoryShifts>0){
							downShiftInventoryRow();
						}
						numberOfInventoryShifts=0;

						if(maximaleItemPosition > 35){
							mySpriteUp.setActive(true);
						}
						myInventory.setDataID("InventarOffen");
						myInventory.setFrame(9);

						myTmpSprite = myGuiSprites.getSprites();
						for (int i = 0; i < myGuiSprites.getSize(); i++) {
							if (myTmpSprite[i].getID() > 0) {
								if (((EbaItemSprite)myTmpSprite[i]).getInventarposition()<36){
									myTmpSprite[i].setActive(true);
								} else {
									myTmpSprite[i].setActive(false);
								}
							}
						}
					}
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

						}else if (myCommand.startsWith("Возьми ")) {
							myCommand = "";
							isCommandComplete = false;
							if (myCommandObject == null) {
							} else {
								skriptID = ((EbaInteractionSprite) myCommandObject).getMyNehmenSkript();
							}
							myScriptInterpreter.runScript(skriptID, 1);
							zeilennummer++;

						} else if (myCommand.startsWith("Используй ")) {

							if ((myCommand.startsWith("Используй "))
									&& myCommand.contains(" с ")) {

								myCommand = "";
								isCommandComplete = false;
								if (myCommandObject == null) {
								} else {
									skriptID = ((EbaItemSprite) myCommandObject2).getBenutzeMitSkriptID(myCommandObject.getID());
								}
								myScriptInterpreter.runScript(skriptID, 1);
								zeilennummer++;

							} else {

								myCommand = "";
								isCommandComplete = false;
								if (myCommandObject == null) {
								} else {
									skriptID = ((EbaInteractionSprite) myCommandObject).getMyBenutzenSkript();
								}
								myScriptInterpreter.runScript(skriptID, 1);
								zeilennummer++;

							}
						} else if (myCommand.startsWith("Поговори с ")) {

							myCommand = "";
							isCommandComplete = false;
							if (myCommandObject == null) {
							} else {
								skriptID = ((EbaInteractionSprite) myCommandObject).getMyRedenMitSkript();
							}
							myScriptInterpreter.runScript(skriptID, 1);
							zeilennummer++;

						} else if (myCommand.startsWith("Посмотри на ")) {

							myCommand = "";
							isCommandComplete = false;
							if (myCommandObject == null) {
							} else {
								skriptID = ((EbaInteractionSprite) myCommandObject).getMyAnsehenSkript();
							}
							myScriptInterpreter.runScript(skriptID, 1);
							zeilennummer++;

						} else if ( (myCommand.startsWith("Подойди к ")) && (myCommandObject.getDataID()=="Выход") ) {

							myCommand = "";
							isCommandComplete = false;
							if (myCommandObject == null) {
							} else {
								skriptID = ((EbaInteractionSprite) myCommandObject).getMyBenutzenSkript();
								if (skriptID <= 0) {
									skriptID = ((EbaExitSprite) myCommandObject).getAusgangID()* (-1);
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
			} else if ((myDataID == "Inventar�ffnend")
					|| (myDataID == "InventarOffen")
					|| (myDataID == "Inventar")
					|| (myDataID == "up")
					|| (myDataID == "down")
					||( myDataID == "Schloss_auf")
					||( myDataID == "Schloss_zu")) {
				// Do nothing!
				
			} else if ( myDataID == "") {
				
				if( myCommand == ""){
					myBefehlszeilenText = "";
					myCommandObject = null;
				}else{
					if(myCommand == "Подойди к "){
						myBefehlszeilenText = "";
						myCommandObject = null;
					} else {
						myBefehlszeilenText = myCommand;
					}
				}
			} else {   //DataID = Objekt
				if(myCommand == ""){
					myBefehlszeilenText = myDataID;
					if(isItem){
						if(((EbaItemSprite)mySprite).getMyDefaultAktion().equals("a")){
							mySpriteAnsehen.setFrame(1);
							defaultAktion = 'a';
						}
						if(((EbaItemSprite)mySprite).getMyDefaultAktion().equals("b")){
							mySpriteBenutzen.setFrame(1);
							defaultAktion = 'b';
						}
						if(((EbaItemSprite)mySprite).getMyDefaultAktion().equals("n")){
							mySpriteNehmen.setFrame(1);
							defaultAktion = 'n';
						}
						if(((EbaItemSprite)mySprite).getMyDefaultAktion().equals("r")){
							mySpriteReden.setFrame(1);
							defaultAktion = 'r';
						}

					}else if(isExit){
						myBefehlszeilenText = "Подойди к " + ((EbaExitSprite)mySprite).getMyMouseoverText();
					} else if (isGuiElement){
						//Do nothing
					} else {
						if(((EbaInteractionSprite)mySprite).getMyDefaultAktion().equals("a")){
							mySpriteAnsehen.setFrame(1);
							defaultAktion = 'a';
						}
						if(((EbaInteractionSprite)mySprite).getMyDefaultAktion().equals("b")){
							mySpriteBenutzen.setFrame(1);
							defaultAktion = 'b';
						}
						if(((EbaInteractionSprite)mySprite).getMyDefaultAktion().equals("n")){
							mySpriteNehmen.setFrame(1);
							defaultAktion = 'n';
						}
						if(((EbaInteractionSprite)mySprite).getMyDefaultAktion().equals("r")){
							mySpriteReden.setFrame(1);
							defaultAktion = 'r';
						}
					}
				}else if((myDataID == "Посмотри на ") || (myDataID == "Используй ") || (myDataID == "Возьми ")	|| (myDataID == "Поговори с ")){
					myBefehlszeilenText = myCommand;
				}else if(myCommandObject2!=null){
					if (myCommandObject2.getDataID()==myDataID){
						myBefehlszeilenText = myCommand;
					}else if (isExit){
						// do nothing
					} else {
						myBefehlszeilenText = myCommand + myDataID;
					}
				}else if (isExit || isGuiElement){
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
					if (!isInventoryOpen){
					myEbaWalkableAreaMap.findWay(
							(int) myCharacterSprite.getBaseX(),
							(int) myCharacterSprite.getBaseY(),
							getMouseX(),
							getMouseY());
					walkHere = (int[]) myEbaWalkableAreaMap.pop();
					myBefehlszeilenText = "Подойди к ";
					myCommand = "Подойди к ";
					isCommandComplete = true;
					myCharacterSprite.walkTo(walkHere[0], walkHere[1]);
					}

				} else {

					if (isItem) {
						
						if (mySprite.getID() > 0) {

							if (myCommand == "" || myCommand == "Возьми " || myCommand == "Используй ") {
								if (((EbaItemSprite) mySprite).hasBenutzenSkript()) {
									myCommand = "Используй " + mySprite.getDataID();
									skriptID = ((EbaItemSprite) mySprite).getBenutzenSkriptID();
									myScriptInterpreter.runScript(skriptID, 1);
									zeilennummer++;
									myCommand = "";
								} else {
									myCommand = "Используй " + mySprite.getDataID() + " с ";
									myCommandObject2 = mySprite;
								}
							} else if (myCommand == "Поговори с ") {
								myCommand = "Поговори с " + mySprite.getDataID();
								skriptID = ((EbaItemSprite) mySprite).getRedenMitSkriptID();
								myCommand = "";
								myScriptInterpreter.runScript(skriptID, 1);
								zeilennummer++;

							} else if (myCommand == "Посмотри на ") {
								myCommand = "Посмотри на " + mySprite.getDataID();
								skriptID = ((EbaItemSprite) mySprite).getAnsehenSkriptID();
								myScriptInterpreter.runScript(skriptID, 1);
								zeilennummer++;
								myCommand = "";
							} else {
								if (!(myCommand.startsWith("Подойди к"))) {
									if(myCommandObject2 != mySprite){
									myCommand = myCommand + mySprite.getDataID();
									skriptID = ((EbaItemSprite) myCommandObject2).getInventarBenutzeMitSkriptID(mySprite.getID());
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
						isCommandComplete = false;
					} else if (mySprite.getDataID() == "Возьми ") {
						myCommand = "Возьми ";
						isCommandComplete = false;
					} else if (mySprite.getDataID() == "Поговори с ") {
						myCommand = "Поговори с ";
						isCommandComplete = false;
					} else if (mySprite.getDataID() == "Используй ") {
						myCommand = "Используй ";
						isCommandComplete = false;
					} else if (mySprite.getDataID() == "Inventar�ffnend") {
						// Do Nothing
						isCommandComplete = false;
					} else if (mySprite.getDataID() == "InventarOffen") {
						// Do Nothing
						isCommandComplete = false;
					} else if (mySprite.getDataID() == "up") {
						upShiftInventoryRow();
						
					} else if (mySprite.getDataID() == "down") {
						downShiftInventoryRow();
					} else if (mySprite.getDataID() == "Schloss_zu"){
						mySprite.setDataID("Schloss_auf");
						myEbaGameEngine.isInventoryLocked = false;
						myInventory.setDataID("Inventar�ffnend");
						myInventory.setAnimate(true);
						isInventoryOpen = true;
						myCommand = "";
						isCommandComplete = false;
					} else if (mySprite.getDataID() == "Schloss_auf"){
						mySprite.setDataID("Schloss_zu");
						myEbaGameEngine.isInventoryLocked = true;
						myInventory.setFrame(0);
						mySpriteUp.setActive(false);
						mySpriteDown.setActive(false);
						myInventory.setDataID("Inventar");
						isInventoryOpen = false;
						myTmpSprite = myGuiSprites.getSprites();
						for (int i = 0; i < myGuiSprites.getSize(); i++) {
							if (myTmpSprite[i].getID() > 0) {
								myTmpSprite[i].setActive(false);
							}
						}
						myCommand = "";
						isCommandComplete = false;
					} else if ((mySprite.getDataID() == "Inventar")&&(myEbaGameEngine.isInventoryLocked)){
						myInventory.setDataID("Inventar�ffnend");
						myInventory.setAnimate(true);
						isInventoryOpen = true;
						isCommandComplete = false;
					} else {		// ----mySprite ist ein Objekt--------


						if (myCommand != "" && isExit){
							//do nothing
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

								if (isExit){
									myCommand = myBefehlszeilenText;
									myCommandObjectName = ((EbaExitSprite)mySprite).getMyMouseoverText();
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

								myCharacterSprite.walkTo(walkHere[0], walkHere[1],((EbaInteractionSprite)mySprite).getStandbyBlickrichtung());
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
					//myBefehlszeilenText = myCommand;
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

				}
				break;
			default:
				// rechte Maustaste:
				if (myDataID == "") {

					myBefehlszeilenText="";
					myCommand="";
					myCommandObject=null;
					myCommandObject2=null;
					isCommandComplete = false;
					
				} else {

					if(!isItem){

						if(myCommand == ""){

						if (defaultAktion != '0'){
							if (defaultAktion == 'a'){
								myCommand = "Посмотри на "+myDataID;
								isCommandComplete = true;
							}
							if (defaultAktion == 'b'){
								myCommand = "Используй "+myDataID;
								isCommandComplete = true;
							}
							if (defaultAktion == 'n'){
								myCommand = "Возьми "+myDataID;
								isCommandComplete = true;
							}
							if (defaultAktion == 'r'){
								myCommand = "Поговори с "+myDataID;
								isCommandComplete = true;
							}
							myBefehlszeilenText = myCommand;
							myCommandObject = mySprite;

							if (((EbaInteractionSprite) mySprite).getWalkToX() < 0) {
								walkToX = (int) mySprite.getX();
								walkToY = (int) mySprite.getY();
							} else {
								walkToX = ((EbaInteractionSprite) mySprite).getWalkToX();
								walkToY = ((EbaInteractionSprite) mySprite).getWalkToY();
							}
							myEbaWalkableAreaMap.findWay(
									(int) myCharacterSprite.getBaseX(),
									(int) myCharacterSprite.getBaseY(),
									walkToX, walkToY);
							walkHere = (int[]) myEbaWalkableAreaMap.pop();
							myCharacterSprite.walkTo(walkHere[0], walkHere[1],((EbaInteractionSprite) mySprite).getStandbyBlickrichtung());
						}
						}else{
							myBefehlszeilenText="";
							myCommand="";
							myCommandObject=null;
							myCommandObject2=null;
							isCommandComplete = false;
							
						}
					} else {
						if (defaultAktion != '0'){
							if (defaultAktion == 'a'){
								skriptID = ((EbaItemSprite)mySprite).getAnsehenSkriptID();
							}
							if (defaultAktion == 'b'){
								if (((EbaItemSprite) mySprite).hasBenutzenSkript()) {
									skriptID = ((EbaItemSprite)mySprite).getBenutzenSkriptID();
								} else {
									myCommand = "Используй " + myDataID + " с ";
									myCommandObject2 = mySprite;
								}
							}
							if (defaultAktion == 'n'){
								skriptID = ((EbaItemSprite)mySprite).getBenutzenSkriptID();
							}
							if (defaultAktion == 'r'){
								skriptID = ((EbaItemSprite)mySprite).getRedenMitSkriptID();
							}
							
							myScriptInterpreter.runScript(skriptID, 1);
							zeilennummer++;
						}
					}
				}
			}


			if ((!isCommandComplete) && (myDataID != "") && (myDataID != myCommandObjectName)) {

				myBefehlszeile.change(myBefehlszeilenText, false);

			}
			else{

				myBefehlszeile.change(myBefehlszeilenText, isCommandComplete);

			}
		}
	}

	///////////////////////////////////////////////////////////
	//                Private Methoden                       //
	///////////////////////////////////////////////////////////

	void upShiftInventoryRow(){

		numberOfInventoryShifts++;

		if(numberOfInventoryShifts>0){
			mySpriteDown.setActive(true);
		}

		if ( 35 + 5*numberOfInventoryShifts >= maximaleItemPosition){
			mySpriteUp.setActive(false);
		}

		for (int i = 1; i<=maximaleItemPosition; i++){
			if (myItemSprites[i].getY()==YUNTERSTEINVENTARZEILE){
				myItemSprites[i].setActive(false);
			}
			if (myItemSprites[i].getY()==YUNTERSTEINVENTARZEILE-(7*INVENTARZEILENHOEHE)){
				myItemSprites[i].setActive(true);
			}
			myItemSprites[i].setY((myItemSprites[i].getY())+ INVENTARZEILENHOEHE);

		}



	}

	void downShiftInventoryRow(){
		numberOfInventoryShifts--;

		if ( 35 + 5*numberOfInventoryShifts < maximaleItemPosition){
			mySpriteUp.setActive(true);
		}
		if (numberOfInventoryShifts < 1){
			mySpriteDown.setActive(false);
		}

		for (int i = 1; i<=maximaleItemPosition; i++){
			if (myItemSprites[i].getY()== YUNTERSTEINVENTARZEILE + (INVENTARZEILENHOEHE)){
				myItemSprites[i].setActive(true);
			}
			if (myItemSprites[i].getY()==YUNTERSTEINVENTARZEILE-(6*INVENTARZEILENHOEHE)){
				myItemSprites[i].setActive(false);
			}
			myItemSprites[i].setY((myItemSprites[i].getY())- INVENTARZEILENHOEHE);

		}

	}

	void setMySpriteUpActive(boolean active){
		mySpriteUp.setActive(active);
	}

	void setMySpriteDownActive(boolean active){
		mySpriteDown.setActive(active);
	}


	void updateInventory(){

		for (int i=1;i<=maximaleItemPosition;i++){
	    	myGuiSprites.remove(myItemSprites[i]);
	    }
		myItemSprites = new EbaItemSprite[100];
		maximaleItemPosition = 0;
		numberOfInventoryShifts = 0;
		try {
			myStatement = EbaGameEngine.myConnection.createStatement();
			myResultSet = myStatement.executeQuery("SELECT * FROM Inventarobjekt WHERE GuiID= 0 AND Inventarposition >0");

			while (myResultSet.next()) {
				int inventarX;
				int inventarY;

				inventarX = XUNTERSTEINVENTARZEILE + (((myResultSet.getInt("Inventarposition")-1)%5) * INVENTARZEILENBREITE);
				inventarY = YUNTERSTEINVENTARZEILE - (((myResultSet.getInt("Inventarposition")-1)/5) * INVENTARZEILENHOEHE);

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
				myItem.setActive(false);
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



	///////////////////////////////////////////////////////////
	//                �ffentliche Methoden                   //
	///////////////////////////////////////////////////////////

	public void deactivateItem(int inventarObjektID){


	    try {

	    	//Merk dir die alte Position
	    	myStatement = EbaGameEngine.myConnection.createStatement();
	        myResultSet = myStatement.executeQuery("SELECT * FROM Inventarobjekt WHERE GuiID= 0 AND ID=" + inventarObjektID);
	        myResultSet.next();
	        int altePosition = myResultSet.getInt("Inventarposition");
	        //Setze die Inventarposition auf 0
	        if(altePosition > 0){
	        myStatement.executeUpdate("UPDATE inventarobjekt SET inventarposition=0 where ID=" + inventarObjektID);
	        //Setze die Position aller Inventargegenst�nde deren Position gr�sser war als die alte Position um 1 hinab
	        myResultSet = myStatement.executeQuery("SELECT * FROM Inventarobjekt WHERE GuiID= 0 AND Inventarposition >0");
			while (myResultSet.next()) {
				if(myResultSet.getInt("Inventarposition") >= altePosition){
				myStatement.executeUpdate("UPDATE inventarobjekt SET inventarposition="+ (myResultSet.getInt("Inventarposition")-1) +" where ID=" + myResultSet.getInt("ID"));
				}
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


	public void activateItem(int inventarObjektID){
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
