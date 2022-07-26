package de.daedalic.eba;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;

import com.golden.gamedev.GameObject;
import com.golden.gamedev.engine.BaseInput;
import com.golden.gamedev.object.PlayField;
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.Timer;
import com.golden.gamedev.object.background.ImageBackground;

public class EbaGameObject extends GameObject {


	final int screenshotWidth = 110;

	final int screenshotHeight = 83;

	final int scaleMethod = 4;

	private BufferedImage myScreenshot;

	private BufferedImage myScreenshotScaled;

	PlayField myPlayField;

	public Sprite myCursorSprite;

	Sprite myStandardCursorSprite;

	Sprite myActiveCursorSprite;

	Sprite myExitNCursorSprite;

	Sprite myExitWCursorSprite;

	Sprite myExitSCursorSprite;

	Sprite myExitECursorSprite;

	Sprite myTalkToEdnaCursorSprite;

	EbaCommandPromptSprite myBefehlszeile;

	EbaSpriteGroup myBackgroundSprites;

	EbaSpriteGroup myObjectSprites;

	EbaSpriteGroup myTextSprites;

	EbaSpriteGroup myGuiSprites;

	EbaSpriteGroup myTopicSprites;
	EbaSpriteGroup myTopicZenSprites;
	
	EbaChoiceList myChoiceList;

	EbaSpriteGroup myMenueSprites;

	int myDragStatus=0;

	public String myActiveFont;

	public String myInactiveFont;

	String myTalkFont;

	String myThinkFont;

	EbaWalkableAreaMap myEbaWalkableAreaMap;

	EbaWalkableAreaMap myNscEbaWalkableAreaMap;

	EbaGameEngine myEbaGameEngine;

	EbaExitSprite myExit;

	private Statement myStatement = null;

	private ResultSet myResultSet = null;

	private ResultSet myResultSet2 = null;

	private ResultSet myResultSet3 = null;

	private ResultSet myResultSet4 = null;

	private ResultSet myResultSet5 = null;;

	private ResultSet myResultSet6 = null;

	private int myRoomNumber;

	public int zeilennummer;

	//Test. On remove -> activate commandcomplete in EbaGuiHarvey
	boolean isCommandComplete = false;

	public int skriptID;

	public EbaScriptInterpreter myScriptInterpreter;

	EbaPlayerSprite myCharacterSprite;

	public EbaNscSprite myEbaNscSprite;

	Timer myTimer = new Timer(1);

	int myTimerSkriptID = 1;

	private boolean isTimerSkriptStart = false;

	private ResultSet myResultSet7;

	private ResultSet myResultSet8;

	private boolean fadeInOut = false;

	private Color fadeColor;

	private float fadeAlpha = 0;

	private int fadeMilliseconds;

	public String myMusicFile = "";

//	public boolean musicOff=false;

	public boolean isInventoryOpen =  false;


	public EbaGameObject(EbaGameEngine myGameEngine) {
		super(myGameEngine);
		myRoomNumber = myGameEngine.nextGameID;
		myEbaGameEngine = myGameEngine;

	}

	public void initResources() {
		hideCursor();

		Sprite mySprite;

		try {

			myStatement = EbaGameEngine.myConnection.createStatement();
			myResultSet = myStatement.executeQuery("SELECT * FROM Raum WHERE Raum.ID =" + myRoomNumber);

			myResultSet.next();

			myPlayField = new PlayField(new ImageBackground(Eba.getBackgroundImage(myResultSet.getString("Bilddatei"))));

			if (myResultSet.getString("Musikdatei") != null) {
				myMusicFile = myResultSet.getString("Musikdatei");
			}

			bsSound.setLoop(false);
			bsSound.setVolume(2);

			bsMusic.setLoop(true);
			bsMusic.setVolume(2);

			//------------------Timer--------------------------------
			myResultSet6 = myStatement.executeQuery("SELECT * FROM Timer WHERE ID =" + myResultSet.getInt("TimerID"));

			myTimer.setActive(false);
			if (myResultSet6.next()) {
				myTimer = new Timer(myResultSet6.getInt("Dauer"));
				myTimer.setActive(myResultSet6.getBoolean("Aktiv"));
				myTimerSkriptID = myResultSet6.getInt("SkriptID");
			}

			// --------------------SpriteGroups------------------------

			myBackgroundSprites = new EbaSpriteGroup("Hintergrundobjekte");
			myPlayField.addGroup(myBackgroundSprites);

			myObjectSprites = new EbaSpriteGroup("Raumobjekte");
			myObjectSprites.setComparator(new Comparator<EbaInterfaceHasBase>() {
				public int compare(EbaInterfaceHasBase eihb1, EbaInterfaceHasBase eihb2) {
					return (int) (eihb1.getBaseY(eihb2.getBaseX()) - eihb2.getBaseY(eihb1.getBaseX()));
				}
			});

			myPlayField.addGroup(myObjectSprites);

			myTopicZenSprites = new EbaSpriteGroup("TopicZenSprites");
			myPlayField.addGroup(myTopicZenSprites);
			
			myTextSprites = new EbaSpriteGroup("TextSprites");
			myPlayField.addGroup(myTextSprites);

			myGuiSprites = new EbaSpriteGroup("GuiSprites");
			myPlayField.addGroup(myGuiSprites);

			myTopicSprites = new EbaSpriteGroup("TopicSprites");
			myPlayField.addGroup(myTopicSprites);

			myMenueSprites = new EbaHauptmenue(this, myEbaGameEngine);
			myPlayField.addGroup(myMenueSprites);
			myMenueSprites.setActive(false);

			myChoiceList = new EbaChoiceList(this);
			myPlayField.addGroup(myChoiceList);
			myChoiceList.setActive(false);

			// ------------------CharacterSprite---------------------------

			ResultSet myResultSet4 = myStatement.executeQuery("SELECT * FROM walkableareamap WHERE ID =" + myResultSet.getInt("walkableareamapid"));
			myResultSet4.next();
			myEbaWalkableAreaMap = new EbaWalkableAreaMap(myResultSet4.getString("arrayofboolean"));
			myNscEbaWalkableAreaMap = new EbaWalkableAreaMap(myResultSet4.getString("arrayofboolean"));

			if(myEbaGameEngine.mySaveGuiID != myResultSet.getInt("GuiID")){

			myResultSet2 = myStatement.executeQuery("SELECT * FROM GUI WHERE ID = " + myResultSet.getInt("GuiID"));
			myResultSet2.next();

			myActiveFont = myResultSet2.getString("ActiveFont");
			myEbaGameEngine.mySaveActiveFont = myActiveFont;
			myInactiveFont = myResultSet2.getString("InactiveFont");
			myEbaGameEngine.mySaveInactiveFont = myInactiveFont;
			myTalkFont = myResultSet2.getString("TalkFont");
			myEbaGameEngine.mySaveTalkFont = myTalkFont;
			myThinkFont = myResultSet2.getString("ThinkFont");
			myEbaGameEngine.mySaveThinkFont = myThinkFont;

			myEbaGameEngine.mySaveGuiID = myResultSet.getInt("GuiID");


			// ------------------CursorSprite---------------------------

			myStandardCursorSprite = new Sprite(Eba.getImage(myResultSet2.getString("StandardCursorDatei")));
			myActiveCursorSprite = new Sprite(Eba.getImage(myResultSet2.getString("AktivCursorDatei")));
//			myExitNCursorSprite = new Sprite(Eba.getImage(myResultSet2.getString("AusgangLinksCursorDatei")));
//			myExitNCursorSprite = new Sprite(Eba.getImage(myResultSet2.getString("AusgangRechtsCursorDatei")));
//			myExitNCursorSprite = new Sprite(Eba.getImage(myResultSet2.getString("AusgangVorneCursorDatei")));
			myExitNCursorSprite = new Sprite(Eba.getImage(myResultSet2.getString("AusgangHintenCursorDatei")));
			myTalkToEdnaCursorSprite = new Sprite(Eba.getImage(myResultSet2.getString("RedeMitEdnaCursorDatei")));

			myEbaGameEngine.mySaveStandardCursorSprite = myStandardCursorSprite;
			myEbaGameEngine.mySaveActiveCursorSprite = myActiveCursorSprite;
			myEbaGameEngine.mySaveExitNCursorSprite = myExitNCursorSprite;
			myEbaGameEngine.mySaveTalkToEdnaCursorSprite = myTalkToEdnaCursorSprite;

			}else{

				myActiveFont = myEbaGameEngine.mySaveActiveFont;
				myInactiveFont = myEbaGameEngine.mySaveInactiveFont;
				myTalkFont = myEbaGameEngine.mySaveTalkFont;
				myThinkFont = myEbaGameEngine.mySaveThinkFont;

				myStandardCursorSprite = myEbaGameEngine.mySaveStandardCursorSprite;
				myActiveCursorSprite = myEbaGameEngine.mySaveActiveCursorSprite;
				myExitNCursorSprite = myEbaGameEngine.mySaveExitNCursorSprite;
				myTalkToEdnaCursorSprite = myEbaGameEngine.mySaveTalkToEdnaCursorSprite;


			}

			myStandardCursorSprite.setImmutable(true);
			myStandardCursorSprite.setActive(true);
			myPlayField.add(myStandardCursorSprite);

			myActiveCursorSprite.setImmutable(true);
			myActiveCursorSprite.setActive(false);
			myPlayField.add(myActiveCursorSprite);

			myExitNCursorSprite.setImmutable(true);
			myExitNCursorSprite.setActive(false);
			myPlayField.add(myExitNCursorSprite);

//			myExitNCursorSprite.setImmutable(true);
//			myExitNCursorSprite.setActive(false);
//			myPlayField.add(myExitNCursorSprite);
//
//			myExitNCursorSprite.setImmutable(true);
//			myExitNCursorSprite.setActive(false);
//			myPlayField.add(myExitNCursorSprite);
//
//			myExitNCursorSprite.setImmutable(true);
//			myExitNCursorSprite.setActive(false);
//			myPlayField.add(myExitNCursorSprite);

			myTalkToEdnaCursorSprite.setImmutable(true);
			myTalkToEdnaCursorSprite.setActive(false);
			myPlayField.add(myTalkToEdnaCursorSprite);

			myCursorSprite = myPlayField.getExtraGroup().getActiveSprite();


			if(myEbaGameEngine.getMyTransRoomCharacterSprite()!= null && myResultSet.getInt("CharacterAnimationSetID") != myEbaGameEngine.getMyTransRoomCharacterSprite().getID()){
				myCharacterSprite = new EbaPlayerSprite(myRoomNumber, this, myEbaGameEngine.charakterBlickrichtung);
				myObjectSprites.add(myCharacterSprite);
				myEbaGameEngine.setMyTransRoomCharacterSprite(myCharacterSprite);

			}else if(myEbaGameEngine.getMyTransRoomCharacterSprite()== null){

				myCharacterSprite = new EbaPlayerSprite(myRoomNumber, this, myEbaGameEngine.charakterBlickrichtung);
				myObjectSprites.add(myCharacterSprite);
				myEbaGameEngine.setMyTransRoomCharacterSprite(myCharacterSprite);
			}else{

				myCharacterSprite =  myEbaGameEngine.getMyTransRoomCharacterSprite();
				myObjectSprites.add(myCharacterSprite);

				myCharacterSprite.setMyRoomNumber(myRoomNumber);
				myCharacterSprite.initialise(this, myResultSet.getInt("baseYatZeroScale"),myResultSet.getInt("baseYatFullScale"),myResultSet.getDouble("hspeed"),myResultSet.getDouble("vspeed"),myEbaGameEngine.charakterBlickrichtung );

			}

			//----------------------Raumobjekte------------------------------------------

			myResultSet = myStatement.executeQuery("SELECT * FROM Raumobjekt WHERE RaumId=" + myRoomNumber);

			while (myResultSet.next()) {
				myResultSet2 = myStatement.executeQuery("SELECT * FROM RaumobjektDarstellung WHERE RaumobjektId =" + myResultSet.getString("ID"));
				if (myResultSet2.next()) {

					mySprite = new EbaObjectSprite(Eba.getImage(myResultSet.getString("Bilddatei")), myResultSet.getDouble("PosX"), myResultSet.getDouble("PosY"), myResultSet2.getInt("GrundlinieAnfangX"), myResultSet2.getInt("GrundlinieAnfangY"), myResultSet2.getInt("GrundlinieEndeX"), myResultSet2.getInt("GrundlinieEndeY"), myResultSet.getBoolean("aktiv"));
					mySprite.setID(myResultSet.getInt("ID"));

					// bildfolge ja/nein
					if (myResultSet2.getInt("BildfolgeID") > 0) {
						int myArrayIndex = 0;

						//anzahl animationsbilder
						myResultSet7 = myStatement.executeQuery("SELECT SUM(Abweichendeanzeigedauer) as summeanzeigedauer FROM Animationsbild WHERE BildfolgeID =" + myResultSet2.getString("BildfolgeID"));
						myResultSet7.next();
						BufferedImage[] myImages = new BufferedImage[myResultSet7.getInt("summeanzeigedauer")];

						//bilder selber
						myResultSet7 = myStatement.executeQuery("SELECT * FROM Animationsbild WHERE BildfolgeID =" + myResultSet2.getString("BildfolgeID"));

						while (myResultSet7.next()) {
							for (int j = 1; j <= myResultSet7.getInt("AbweichendeAnzeigedauer"); j++) {
								myImages[myArrayIndex] = Eba.getImage(myResultSet7.getString("Bilddatei"));
								myArrayIndex++;
							}
						}

						((EbaObjectSprite) mySprite).setImages(myImages);

						// loop ja/nein
						myResultSet8 = myStatement.executeQuery("SELECT * from Bildfolge where ID =" + myResultSet2.getString("BildfolgeID"));
						while (myResultSet8.next()) {
							((EbaObjectSprite) mySprite).setLoopAnim(myResultSet8.getBoolean("Loop"));
							((EbaObjectSprite) mySprite).getAnimationTimer().setDelay(myResultSet8.getInt("Anzeigedauer"));
							((EbaObjectSprite) mySprite).setAnimate(myResultSet8.getBoolean("Loop"));

						}
					}

					myResultSet3 = myStatement.executeQuery("SELECT * FROM RaumobjektInteraktion WHERE RaumobjektId = " + myResultSet.getString("ID"));
					if (myResultSet3.next()) {
						mySprite.setDataID(myResultSet3.getString("BezeichnungOMO"));
						((EbaObjectSprite) mySprite).setWalkToX(myResultSet3.getInt("WalkToX"));
						((EbaObjectSprite) mySprite).setWalkToY(myResultSet3.getInt("WalkToY"));
						((EbaObjectSprite) mySprite).setStandbyBlickrichtung(myResultSet3.getString("StandbyBlickrichtung"));
						((EbaObjectSprite) mySprite).setMyDefaultAktion(myResultSet3.getString("DefaultAktion"));
						((EbaObjectSprite) mySprite).setMyAnsehenSkript(myResultSet3.getInt("AnsehenSkriptID"));
						((EbaObjectSprite) mySprite).setMyBenutzenSkript(myResultSet3.getInt("BenutzenSkriptID"));
						((EbaObjectSprite) mySprite).setMyNehmenSkript(myResultSet3.getInt("NehmenSkriptID"));
						((EbaObjectSprite) mySprite).setMyRedenMitSkript(myResultSet3.getInt("RedenMitSkriptID"));

						myResultSet5 = myStatement.executeQuery("SELECT * FROM Nsc WHERE RaumobjektId =" + myResultSet.getString("ID"));
						myResultSet4 = myStatement.executeQuery("SELECT * FROM Ausgang WHERE RaumobjektinteraktionID = " + myResultSet3.getString("ID"));

						if (myResultSet5.next()) {

							//---------------------------------------------------------------------------------
							//--------Raumobjekt, Raumobjektdarstellung, Raumobkjektinteraktion und NSC -------
							//---------------------------------------------------------------------------------

							myEbaNscSprite = new EbaNscSprite(myResultSet5.getInt("ID"), this, Eba.getImage(myResultSet.getString("Bilddatei")), myResultSet.getDouble("PosX"), myResultSet.getDouble("PosY"), myResultSet3.getInt("WalkToX"), myResultSet3.getInt("WalkToY"), myResultSet3.getString("StandbyBlickrichtung"), myResultSet3.getString("DefaultAktion"), myResultSet3.getInt("AnsehenSkriptID"), myResultSet3.getInt("BenutzenSkriptID"), myResultSet3.getInt("NehmenSkriptID"), myResultSet3.getInt("RedenMitSkriptID"), myResultSet.getBoolean("aktiv"));
							myEbaNscSprite.setActive(myResultSet.getBoolean("Aktiv"));
							myEbaNscSprite.setImmutable(true);
							myEbaNscSprite.setDataID(myResultSet5.getString("Bezeichnung"));
							myEbaNscSprite.setID(myResultSet.getInt("ID"));
							myObjectSprites.add(myEbaNscSprite);
						} else if (myResultSet4.next()) {

							//----------------------------------------------------------------------------
							//--------Raumobjekt, Raumobjektdarstellung, Raumobkjektinteraktion und Exit -
							//----------------------------------------------------------------------------

							myExit = new EbaExitSprite(Eba.getImage(myResultSet.getString("Bilddatei")), myResultSet.getDouble("PosX"), myResultSet.getDouble("PosY"), myResultSet3.getInt("WalkToX"), myResultSet3.getInt("WalkToY"), myResultSet3.getString("StandbyBlickrichtung"), "", 0, myResultSet3.getInt("BenutzenSkriptID"), 0, 0, myResultSet.getBoolean("aktiv"), myResultSet4.getInt("ID"), myResultSet4.getInt("ZielraumID"), myResultSet4.getInt("WalkinPointX"), myResultSet4.getInt("WalkinPointY"), myResultSet4.getString("Charakterblickrichtung"), myResultSet3.getString("BezeichnungOMO"));
							myExit.setDataID("Выход");
							myExit.setID(myResultSet.getInt("ID"));
							myExit.setAusgangID((myResultSet4.getInt("ID")));
							myBackgroundSprites.add(myExit);
							myExit.setImmutable(true);
							if (myResultSet2.getInt("BildfolgeID") > 0) {
								myExit.setImages(((EbaObjectSprite) mySprite).getImages());
								myExit.getAnimationTimer().setDelay(((EbaObjectSprite) mySprite).getAnimationTimer().getDelay());
								myExit.setLoopAnim(((EbaObjectSprite) mySprite).isLoopAnim());
								myExit.setAnimate(((EbaObjectSprite) mySprite).isAnimate());
							}
						} else {

							//---------------------------------------------------------------------
							//--------Raumobjekt, Raumobjektdarstellung und Raumobkjektinteraktion
							//---------------------------------------------------------------------

							mySprite.setImmutable(true);
							myObjectSprites.add(mySprite);
						}
					} else {

						myResultSet5 = myStatement.executeQuery("SELECT * FROM Nsc WHERE RaumobjektId =" + myResultSet.getString("ID"));

						if (myResultSet5.next()) {

							//---------------------------------------------------------------------
							//--------Raumobjekt, Raumobjektdarstellung und NSC -------------------
							//---------------------------------------------------------------------

							myEbaNscSprite = new EbaNscSprite(myResultSet5.getInt("ID"), this, Eba.getImage(myResultSet.getString("Bilddatei")), myResultSet.getDouble("PosX"), myResultSet.getDouble("PosY"), 0, 0, "", "", 0, 0, 0, 0, myResultSet.getBoolean("aktiv"));
							myEbaNscSprite.setActive(myResultSet.getBoolean("Aktiv"));
							myEbaNscSprite.setImmutable(true);
							myEbaNscSprite.setDataID(myResultSet5.getString("Bezeichnung"));
							myEbaNscSprite.setID(myResultSet.getInt("ID"));
							myObjectSprites.add(myEbaNscSprite);

						} else {

							//---------------------------------------------------------------------
							//--------Raumobjekt und Raumobjektdarstellung ------------------------
							//---------------------------------------------------------------------

							mySprite.setImmutable(true);
							myObjectSprites.add(mySprite);

						}
					}

				} else {
					myResultSet3 = myStatement.executeQuery("SELECT * FROM RaumobjektInteraktion WHERE RaumobjektId = " + myResultSet.getString("ID"));
					if (myResultSet3.next()) {
						mySprite = new EbaInteractionSprite(Eba.getImage(myResultSet.getString("Bilddatei")), myResultSet.getDouble("PosX"), myResultSet.getDouble("PosY"), myResultSet3.getInt("WalkToX"), myResultSet3.getInt("WalkToY"), myResultSet3.getString("StandbyBlickrichtung"), myResultSet3.getString("DefaultAktion"), myResultSet3.getInt("AnsehenSkriptID"), myResultSet3.getInt("BenutzenSkriptID"), myResultSet3.getInt("NehmenSkriptID"), myResultSet3.getInt("RedenMitSkriptID"), myResultSet.getBoolean("aktiv"));
						mySprite.setDataID(myResultSet3.getString("BezeichnungOMO"));
						mySprite.setID(myResultSet.getInt("ID"));
						myResultSet5 = myStatement.executeQuery("SELECT * FROM Nsc WHERE RaumobjektId =" + myResultSet.getString("ID"));
						myResultSet4 = myStatement.executeQuery("SELECT * FROM Ausgang WHERE RaumobjektinteraktionID = " + myResultSet3.getString("ID"));

						if (myResultSet5.next()) {

							//---------------------------------------------------------------------
							//--------Raumobjekt, Raumobjektinteraktion und NSC -------------------
							//---------------------------------------------------------------------

							myEbaNscSprite = new EbaNscSprite(myResultSet5.getInt("ID"), this, Eba.getImage(myResultSet.getString("Bilddatei")), myResultSet.getDouble("PosX"), myResultSet.getDouble("PosY"), myResultSet3.getInt("WalkToX"), myResultSet3.getInt("WalkToY"), myResultSet3.getString("StandbyBlickrichtung"), myResultSet3.getString("DefaultAktion"), myResultSet3.getInt("AnsehenSkriptID"), myResultSet3.getInt("BenutzenSkriptID"), myResultSet3.getInt("NehmenSkriptID"), myResultSet3.getInt("RedenMitSkriptID"), myResultSet.getBoolean("aktiv"));
							myEbaNscSprite.setActive(myResultSet.getBoolean("Aktiv"));
							myEbaNscSprite.setImmutable(true);
							myEbaNscSprite.setDataID(myResultSet5.getString("Bezeichnung"));
							myEbaNscSprite.setID(myResultSet.getInt("ID"));
							myBackgroundSprites.add(myEbaNscSprite);

						} else if (myResultSet4.next()) {

							//---------------------------------------------------------------------
							//--------Raumobjekt, Raumobjektinteraktion und Exit ------------------
							//---------------------------------------------------------------------

							myExit = new EbaExitSprite(Eba.getImage(myResultSet.getString("Bilddatei")), myResultSet.getDouble("PosX"), myResultSet.getDouble("PosY"), myResultSet3.getInt("WalkToX"), myResultSet3.getInt("WalkToY"), myResultSet3.getString("StandbyBlickrichtung"), "", 0, myResultSet3.getInt("BenutzenSkriptID"), 0, 0, myResultSet.getBoolean("aktiv"), myResultSet4.getInt("ID"), myResultSet4.getInt("ZielraumID"), myResultSet4.getInt("WalkinPointX"), myResultSet4.getInt("WalkinPointY"), myResultSet4.getString("Charakterblickrichtung"), myResultSet3.getString("BezeichnungOMO"));
							myExit.setDataID("Выход");
							myExit.setID(myResultSet.getInt("ID"));
							myExit.setAusgangID((myResultSet4.getInt("ID")));
							myBackgroundSprites.add(myExit);
							myExit.setImmutable(true);
						} else {

							//---------------------------------------------------------------------
							//--------Raumobjekt und Raumobjektinteraktion ------------------------
							//---------------------------------------------------------------------

							mySprite.setImmutable(true);
							((EbaInteractionSprite) mySprite).setAusgangID(-1);
							myBackgroundSprites.add(mySprite);
						}
					} else {

						//---------------------------------------------------------------------
						//--------Raumobjekt und NSC  -----------------------------------------
						//---------------------------------------------------------------------

						myResultSet5 = myStatement.executeQuery("SELECT * FROM Nsc WHERE RaumobjektId =" + myResultSet.getString("ID"));

						if (myResultSet5.next()) {
							myEbaNscSprite = new EbaNscSprite(myResultSet5.getInt("ID"), this, Eba.getImage(myResultSet.getString("Bilddatei")), myResultSet.getDouble("PosX"), myResultSet.getDouble("PosY"), 0, 0, "", "", 0, 0, 0, 0, myResultSet.getBoolean("aktiv"));
							myEbaNscSprite.setActive(myResultSet.getBoolean("Aktiv"));
							myEbaNscSprite.setImmutable(true);
							myEbaNscSprite.setDataID(myResultSet5.getString("Bezeichnung"));
							myEbaNscSprite.setID(myResultSet.getInt("ID"));
							myBackgroundSprites.add(myEbaNscSprite);

						}
					}
				}

			}

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
			if (myResultSet2 != null) {
				try {
					myResultSet2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				myResultSet2 = null;
			}
			if (myResultSet3 != null) {
				try {
					myResultSet3.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				myResultSet3 = null;
			}
			if (myResultSet4 != null) {
				try {
					myResultSet4.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				myResultSet4 = null;
			}
			if (myResultSet5 != null) {
				try {
					myResultSet5.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				myResultSet5 = null;
			}
			if (myResultSet6 != null) {
				try {
					myResultSet6.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				myResultSet6 = null;
			}
			if (myResultSet7 != null) {
				try {
					myResultSet7.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				myResultSet7 = null;
			}
			if (myResultSet8 != null) {
				try {
					myResultSet8.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				myResultSet8 = null;
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
		

		myBefehlszeile = new EbaCommandPromptSprite(myActiveFont,
				myInactiveFont, fontManager, 10, 530);
		myTextSprites.add(myBefehlszeile);
		
		myScriptInterpreter = new EbaScriptInterpreter(myCharacterSprite, myBackgroundSprites, myObjectSprites, myTextSprites, myGuiSprites, myChoiceList, myEbaGameEngine, this);

		//TODO: Lade Musik
		if (!myMusicFile.equals("")) {
			playMusic(myMusicFile);
			if (myEbaGameEngine.musicOn == false){
				bsMusic.stop(myMusicFile);
			}
		}
	}

	public void update(long elapsedTime) {

		if (myEbaGameEngine.skriptAktiv) {
			myScriptInterpreter.runScript(myEbaGameEngine.skriptID, myEbaGameEngine.zeilennummer);
			zeilennummer = myEbaGameEngine.zeilennummer + 1;
			skriptID = myEbaGameEngine.skriptID;
			myEbaGameEngine.skriptAktiv = false;

		}

		if (myScriptInterpreter.isRunning()) {
			if (!myScriptInterpreter.isPerforming()) {
				myScriptInterpreter.runScript(skriptID, zeilennummer);
				zeilennummer++;
			}
			

		} else {
			zeilennummer = 1;
			myCursorSprite.setActive(true);
			if (isTimerSkriptStart) {
				myScriptInterpreter.runScript(myTimerSkriptID, 1);
				skriptID = myTimerSkriptID;
				zeilennummer++;
				myTimer.setActive(false);
				isTimerSkriptStart = false;
			}
		}

		if (fadeInOut) {
			fadeAlpha = fadeAlpha + ((float) elapsedTime / (float) fadeMilliseconds);
			fadeInOut = (fadeAlpha > 0);
			if (fadeAlpha > 1) {
				fadeAlpha = 1;
			}
		}

		// ------ Timer abfragen
		if (myTimer.isActive() && myTimer.action(elapsedTime)) {
			isTimerSkriptStart = true;
		}

		String myDataID = "";
		EbaInteractionSprite mySprite = (EbaInteractionSprite) checkPosMouseInteraction(myObjectSprites, true);

		if (mySprite == null) {
			mySprite = (EbaInteractionSprite) checkPosMouse(myBackgroundSprites, true);
		}

		if (mySprite == null) {

		} else {
			myDataID = (mySprite.getDataID()).toString();
		}

		if (myDataID == "Выход" && myGuiSprites.isActive() && !myScriptInterpreter.isRunning() && !myMenueSprites.isActive() && myDragStatus!=2 && !isInventoryOpen) {
			myStandardCursorSprite.setActive(false);
			myCursorSprite = myExitNCursorSprite;
			myCursorSprite.setActive(true);
		} else if (myExitNCursorSprite.isActive()) {
			myExitNCursorSprite.setActive(false);
			myCursorSprite = myStandardCursorSprite;
			myCursorSprite.setActive(true);
		}
		myCursorSprite.setLocation(getMouseX() - (myCursorSprite.getWidth() / 2), getMouseY() - (myCursorSprite.getHeight() / 2));

		myPlayField.update(elapsedTime);

		switch (bsInput.getKeyPressed()) {
		case KeyEvent.VK_ESCAPE:

			if(myChoiceList.isActive()){
				
				myEbaGameEngine.bsSound.play("audio/soundfx/huuup000.wav");
				
			}else{
				if (myGuiSprites.isActive()) {
					if (!myScriptInterpreter.isRunning()) {
						myCharacterSprite.anhalten();
						myGuiSprites.setActive(false);
						myCursorSprite.setActive(false);
						myScreenshot = this.takeScreenShot();
						myScreenshotScaled = createBufferedImage(this.takeScreenShot().getScaledInstance(screenshotWidth, screenshotHeight, scaleMethod));
						myMenueSprites.setActive(true);
						myCursorSprite.setActive(true);
					} else {
						//TODO: Fast Forward
					}
				} else {
					myGuiSprites.setActive(true);
					myMenueSprites.setActive(false);
				}
			}
			break;

			//	Testcoding:

		case KeyEvent.VK_F1:
			// myScriptInterpreter.testSkript();
			break;

		case KeyEvent.VK_SPACE:

			Sprite[] myTmpSprite1 = new Sprite[myObjectSprites.getSize()];
			EbaTextSprite[] mySnoozeSprite = new EbaTextSprite[myObjectSprites.getSize()];
			myTmpSprite1 = myObjectSprites.getSprites();
			for (int i = 0; i < myObjectSprites.getSize(); i++) {

				if (myTmpSprite1[i] != null && myTmpSprite1[i].getClass() != EbaPlayerSprite.class) {

					EbaInteractionSprite myTmpInteraction = (EbaInteractionSprite) myTmpSprite1[i];
					if (((String)myTmpInteraction.getDataID()!=null) && ((String)myTmpInteraction.getDataID()!="") && (myTmpInteraction.isActive() )){

						int nearestX = (int) myTmpInteraction.getX()+(myTmpInteraction.getWidth()/2);
						int nearestY = (int) myTmpInteraction.getY()+(myTmpInteraction.getHeight()/2);
						if(myTmpInteraction.getImage().getRGB(nearestX-(int)myTmpInteraction.getX(),nearestY-(int)myTmpInteraction.getY())== 0){
							Point nearestPoint = new Point(nearestX,nearestY);
							double myDistance;
							double minDistance;
							minDistance = Double.MAX_VALUE;
							for (int x = 0; x < (int)myTmpInteraction.getWidth(); x++) {
								for (int y = 0; y < (int)myTmpInteraction.getHeight(); y++) {

									if (myTmpInteraction.getImage().getRGB(x,y)!= 0) {

										myDistance = nearestPoint.distance(myTmpInteraction.getX()+x, myTmpInteraction.getY()+y);

										if (minDistance > myDistance) {
											minDistance = myDistance;
											nearestX = (int)myTmpInteraction.getX()+x;
											nearestY = (int)myTmpInteraction.getY()+y;
										}
									}
								}
							}
						}

					mySnoozeSprite[i] = new EbaTextSprite((String)myTmpInteraction.getDataID(),"NscFontWeiss",fontManager,nearestX,nearestY,1000);
	            	myTextSprites.add(mySnoozeSprite[i]);
					}
				}
			}
			
			

			Sprite[] myTmpSprite2 = new Sprite[myBackgroundSprites.getSize()];
			EbaTextSprite[] mySnoozeSprite2 = new EbaTextSprite[myBackgroundSprites.getSize()];
			myTmpSprite2 = myBackgroundSprites.getSprites();
			for (int i = 0; i < myBackgroundSprites.getSize(); i++) {

				if (myTmpSprite2[i] != null) {

					EbaInteractionSprite myTmpInteraction = (EbaInteractionSprite) myTmpSprite2[i];
					if (((String)myTmpInteraction.getDataID()!=null) && ((String)myTmpInteraction.getDataID()!="") && (myTmpInteraction.isActive() )){


						int nearestX = (int) myTmpInteraction.getX()+(myTmpInteraction.getWidth()/2);
						int nearestY = (int) myTmpInteraction.getY()+(myTmpInteraction.getHeight()/2);

						if(myTmpInteraction.getImage().getRGB(nearestX-(int)myTmpInteraction.getX(),nearestY-(int)myTmpInteraction.getY())== 0){
							Point nearestPoint = new Point(nearestX,nearestY);
							double myDistance;
							double minDistance;
							minDistance = Double.MAX_VALUE;
							for (int x = 0; x < (int)myTmpInteraction.getWidth(); x++) {
								for (int y = 0; y < (int)myTmpInteraction.getHeight(); y++) {

									if (myTmpInteraction.getImage().getRGB(x,y)!= 0) {

										myDistance = nearestPoint.distance(myTmpInteraction.getX()+x, myTmpInteraction.getY()+y);

										if (minDistance > myDistance) {
											minDistance = myDistance;
											nearestX = (int)myTmpInteraction.getX()+x;
											nearestY = (int)myTmpInteraction.getY()+y;
										}
									}
								}
							}
						}

						String myTitel;
						if((String)myTmpInteraction.getDataID()== "Выход"){
							myTitel = ((EbaExitSprite)myTmpInteraction).getMyMouseoverText();
						}else{
							myTitel = (String)myTmpInteraction.getDataID();
						}
						mySnoozeSprite2[i] = new EbaTextSprite(myTitel,"NscFontWeiss",fontManager,nearestX,nearestY,1000);
	            	myTextSprites.add(mySnoozeSprite2[i]);
					}
				}
			}

			break;

		case KeyEvent.VK_F2:
			myEbaGameEngine.bsSound.play("audio/1032043501-1.wav");
			break;
		case KeyEvent.VK_F3:
			myEbaGameEngine.bsSound.play("audio/1032042501-1.wav");
			break;
		case KeyEvent.VK_F4:
			myEbaGameEngine.bsSound.play("audio/1032045601-1.wav");
			break;
		case KeyEvent.VK_F5:
			myEbaGameEngine.bsSound.play("audio/1032045201-1.wav");
			break;

			//	End of Testcoding

		case KeyEvent.VK_PERIOD:
			myCharacterSprite.shutUp();
			
			Sprite[] myTmpSprite = new Sprite[myObjectSprites.getSize()];
			myTmpSprite = myObjectSprites.getSprites();
			for (int i = 0; i < myObjectSprites.getSize(); i++) {
				if (myTmpSprite[i] != null && myTmpSprite[i].getClass() == EbaNscSprite.class) {
					EbaNscSprite myTmpNsc = (EbaNscSprite) myTmpSprite[i];
					myTmpNsc.shutUp();
				}
			}

			myTmpSprite = new Sprite[myBackgroundSprites.getSize()];
			myTmpSprite = myBackgroundSprites.getSprites();
			for (int i = 0; i < myBackgroundSprites.getSize(); i++) {
				if (myTmpSprite[i] != null && myTmpSprite[i].getClass() == EbaNscSprite.class) {
					EbaNscSprite myTmpNsc = (EbaNscSprite) myTmpSprite[i];
					myTmpNsc.shutUp();
				}
			}
			myTmpSprite = new Sprite[myTextSprites.getSize()];
			myTmpSprite = myTextSprites.getSprites();
			for (int i = 0; i < myTextSprites.getSize(); i++) {
				if ((myTmpSprite[i] != null)&&(myTmpSprite[i]!= myBefehlszeile)) {
					myTextSprites.remove(myTmpSprite[i]);

				}
			}

			if (myScriptInterpreter.isSayingSound){
				myEbaGameEngine.bsSound.getAudioRenderer(myScriptInterpreter.myAudioPfad).stop();
				bsSound.stopAll();
			}
		}
		
		switch (bsInput.getMouseReleased()) {
		case BaseInput.NO_BUTTON:
			break;
		case MouseEvent.BUTTON1:
			// linke Maustaste:

			
			break;
		default:
			// rechte Maustaste:
			myCharacterSprite.shutUp();
			Sprite[] myTmpSprite = new Sprite[myObjectSprites.getSize()];
			myTmpSprite = myObjectSprites.getSprites();
			for (int i = 0; i < myObjectSprites.getSize(); i++) {
				if (myTmpSprite[i] != null && myTmpSprite[i].getClass() == EbaNscSprite.class) {
					EbaNscSprite myTmpNsc = (EbaNscSprite) myTmpSprite[i];
					myTmpNsc.shutUp();
				}
			}
	
			myTmpSprite = new Sprite[myBackgroundSprites.getSize()];
			myTmpSprite = myBackgroundSprites.getSprites();
			for (int i = 0; i < myBackgroundSprites.getSize(); i++) {
				if (myTmpSprite[i] != null && myTmpSprite[i].getClass() == EbaNscSprite.class) {
					EbaNscSprite myTmpNsc = (EbaNscSprite) myTmpSprite[i];
					myTmpNsc.shutUp();
				}
			}
			myTmpSprite = new Sprite[myTextSprites.getSize()];
			myTmpSprite = myTextSprites.getSprites();
			for (int i = 0; i < myTextSprites.getSize(); i++) {
				if ((myTmpSprite[i] != null)&&(myTmpSprite[i]!=myBefehlszeile)) {
					myTextSprites.remove(myTmpSprite[i]);
	
				}
			}
	
			if (myScriptInterpreter.isSayingSound){
				myEbaGameEngine.bsSound.getAudioRenderer(myScriptInterpreter.myAudioPfad).stop();
				bsSound.stopAll();
			}
		}
		
	}

	public void render(Graphics2D g) {
		myPlayField.render(g);
		if (fadeInOut) {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
			g.setColor(fadeColor);
			g.fillRect(0, 0, 800, 600);
		}
	}

	public Sprite checkPosMouse(EbaSpriteGroup group, boolean pixelCheck) {
		Sprite[] sprites = group.getSprites();
		int size = group.getSize();
		for (int i = size - 1; i >= 0; i--) {
			if (sprites[i].isActive() && checkPosMouse(sprites[i], pixelCheck)) {
				return sprites[i];
			}
		}
		return null;
	}

	public Sprite checkPosMouseInteraction(EbaSpriteGroup group, boolean pixelCheck) {
		Sprite[] sprites = group.getSprites();
		int size = group.getSize();
		for (int i = size - 1; i >= 0; i--) {
			if (sprites[i].isActive() && ((EbaInterfaceHasBase) sprites[i]).hasInteraction() && checkPosMouse(sprites[i], pixelCheck)) {
				return sprites[i];
			}
		}
		return null;
	}

	public BufferedImage getScreenshotScaled() {
		return myScreenshotScaled;
	}

	public BufferedImage getScreenshot() {
		return myScreenshot;
	}

	public BufferedImage getScreenhotGray() {

		BufferedImage gray = new BufferedImage(myScreenshot.getWidth(), myScreenshot.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		((Graphics2D) gray.getGraphics()).drawImage(myScreenshot, new AffineTransform(), null);

		return gray;

	}

	public BufferedImage createBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		BufferedImage bi = new BufferedImage(screenshotWidth, screenshotHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bi;
	}

	public void fadeOut(Color fadeToColor, int milliseconds) {
		fadeInOut = true;
		fadeColor = fadeToColor;
		fadeAlpha = 0;
		fadeMilliseconds = milliseconds;
	}

	public void fadeIn(Color fadeFromColor, int milliseconds) {
		fadeInOut = true;
		fadeColor = fadeFromColor;
		fadeAlpha = 1;
		fadeMilliseconds = milliseconds * -1;
	}

	public void setFaded(Color fadeToColor) {
		fadeInOut = true;
		fadeColor = fadeToColor;
		fadeAlpha = 1;
		fadeMilliseconds = 1;
	}

	public void deactivateItem(int inventarObjektID) {
		//leerer Methodenstumpf f�r Vererbung an die GUIs
	}

	public void activateItem(int inventarObjektID) {
		//leerer Methodenstumpf f�r Vererbung an die GUIs
	}

	public void setTimerAktiv(boolean timerAktiv) {
		myTimer.setActive(timerAktiv);
	}

	public void setTimerLength(int milisekunden) {
		myTimer = new Timer(milisekunden);
	}

	public void zeilennummerInc() {
		zeilennummer++;
	}

	public int getRoomNumber() {
		return myRoomNumber;
	}

}
