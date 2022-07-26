package de.daedalic.eba;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.golden.gamedev.engine.BaseInput;
import com.golden.gamedev.object.AnimatedSprite;
import com.golden.gamedev.object.Sprite;

public class EbaGuiStart extends EbaGameObject {

	private Sprite mySprite;
	private String myDataID;

	private AnimatedSprite mySpriteNeu;
	private AnimatedSprite mySpriteLaden;
	private AnimatedSprite mySpriteOptionen;
	private AnimatedSprite mySpriteBeenden;

	private AnimatedSprite mySpriteOkay;
	private AnimatedSprite mySpriteOkayOpt; 	// Okay für Optionsmenü
	private AnimatedSprite mySpriteCancel;
	private AnimatedSprite mySpriteCancelOpt; 	// Cancel für Optionsmenü
	private AnimatedSprite mySpriteTextOpt;		// Textoptionen Text an oder aus
	private AnimatedSprite mySpriteSoundOpt;	// Soundoptionen Sound an oder aus
	private AnimatedSprite mySpriteMusicOpt;	// Musikoptionen Musik an oder aus

	private Sprite mySpriteOptionsLabel;
	private Sprite mySpriteLoadLabel;
	private Sprite mySpriteSaveLabel;
	
	boolean isAnyButtonActive = false;
	private String pressedButton = "";

	private AnimatedSprite mySpriteSpielstandScreenshot[] = new AnimatedSprite[10];
	private Sprite mySpritePNGs[] = new Sprite[10];
	private BufferedImage myBufImage;

	private int slot = 0;
	private int autsch = 0;   // Zähler für die sprechende Infoleiste. Nur ein Gimmik.
	final static int textTeiler = 2;

	private Sprite mySpriteInfoleiste;
	private Sprite mySpriteOptionsfenster;
	private AnimatedSprite mySpriteRegler;
	private AnimatedSprite mySpriteReglerleiste;
	boolean isReglerButtonActive = false;

	private EbaCommandPromptSprite confirmationText;

	private EbaCommandPromptSprite musicText;
	private EbaCommandPromptSprite soundText;
	private EbaCommandPromptSprite textText;
	private EbaCommandPromptSprite geschwText;
	private boolean firstTimeOpt = false;
	private boolean mySoundOn = true;
	private boolean myMusicOn = true;
	private boolean myTextOn = true;

	public EbaGuiStart(EbaGameEngine myGameEngine) {
		super(myGameEngine);

	}

	public void initResources() {
		super.initResources();

		//*****************************************

		// ***** Submenu-Label *****
		mySpriteOptionsLabel = new Sprite(Eba.getImage("gui/hauptmenue/b_optionen_i.png"), 20, 300);
		myGuiSprites.add(mySpriteOptionsLabel);
		mySpriteOptionsLabel.setDataID("OptionLabel");
		mySpriteOptionsLabel.setImmutable(true);
		mySpriteOptionsLabel.setActive(false);
		
		mySpriteLoadLabel = new Sprite(Eba.getImage("gui/hauptmenue/b_laden_i.png"), 20, 300);
		myGuiSprites.add(mySpriteLoadLabel);
		mySpriteLoadLabel.setDataID("LoadLabel");
		mySpriteLoadLabel.setImmutable(true);
		mySpriteLoadLabel.setActive(false);
		
		mySpriteSaveLabel = new Sprite(Eba.getImage("gui/hauptmenue/b_speichern_i.png"), 20, 300);
		myGuiSprites.add(mySpriteSaveLabel);
		mySpriteSaveLabel.setDataID("SaveLabelInactive");
		mySpriteSaveLabel.setImmutable(true);
		mySpriteSaveLabel.setActive(false);
		
		//****************************

	// fertisch

		// ***** Button oben (Laden, Speichern, Optionen) *****
		mySpriteLaden = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b_laden.png"),
				Eba.getImage("gui/hauptmenue/b_laden_a.png"),
				Eba.getImage("gui/hauptmenue/b_laden_p.png") }, 20, 360);
		mySpriteLaden.setImmutable(true);
		mySpriteLaden.setDataID("Laden");
		myGuiSprites.add(mySpriteLaden);

		mySpriteNeu = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b_neu.png"),
				Eba.getImage("gui/hauptmenue/b_neu_a.png"),
				Eba.getImage("gui/hauptmenue/b_neu_p.png") }, 20, 330);
		mySpriteNeu.setImmutable(true);
		mySpriteNeu.setDataID("Neu");
		myGuiSprites.add(mySpriteNeu);

		mySpriteOptionen = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b_optionen.png"),
				Eba.getImage("gui/hauptmenue/b_optionen_a.png"),					/** TODO Button "Optionen" momentan ohne Mouseover **/
				Eba.getImage("gui/hauptmenue/b_optionen_p.png") }, 20, 300);
		mySpriteOptionen.setImmutable(true);
		mySpriteOptionen.setDataID("Optionen");
		myGuiSprites.add(mySpriteOptionen);
		// *****************************************************

		//***** Optionsmenue ******
		mySpriteOptionsfenster = new Sprite(Eba.getImage("gui/hauptmenue/bg-optionsmenu.png"), 120, 230);
		mySpriteOptionsfenster.setImmutable(true);
		mySpriteOptionsfenster.setDataID("mySpriteOptionsfenster");
		myGuiSprites.add(mySpriteOptionsfenster);
		mySpriteOptionsfenster.setActive(false);
		// *****************************************************

		// ***** Button unten (Beenden, Spielen) *****
		mySpriteBeenden = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b_beenden.png"),
				Eba.getImage("gui/hauptmenue/b_beenden_a.png"),
				Eba.getImage("gui/hauptmenue/b_beenden_p.png") }, 20, 390);
		mySpriteBeenden.setImmutable(true);
		mySpriteBeenden.setDataID("Beenden");
		myGuiSprites.add(mySpriteBeenden);

		// ********************************************



		// ***** Button "ok" und "cancel" werden erst bei Bedarf dem Menue hinzugefuegt ********
		mySpriteOkay = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b_okay.png"),
				Eba.getImage("gui/hauptmenue/b_okay_a.png"),
				Eba.getImage("gui/hauptmenue/b_okay_p.png") }, 510, 260);
		mySpriteOkay.setImmutable(true);
		mySpriteOkay.setDataID("Okay");

		mySpriteCancel = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b_chancel.png"),
				Eba.getImage("gui/hauptmenue/b_chancel_a.png"),
				Eba.getImage("gui/hauptmenue/b_chancel_p.png") }, 510, 230);
		mySpriteCancel.setImmutable(true);
		mySpriteCancel.setDataID("Cancel");
		// ************************************************************************************

		// ***** Button "Regler"  und "CancelOpt" werden erst bei Bedarf dem Menue hinzugefuegt ********

		//************************Reglerleiste***********************
		mySpriteReglerleiste = new AnimatedSprite(new BufferedImage[]{
			Eba.getImage("gui/hauptmenue/reglerleiste.png")}, 150,450);
			mySpriteReglerleiste.setImmutable(true);
			mySpriteReglerleiste.setDataID("Reglerleiste");


		//************************Regler******************************
		mySpriteRegler = new AnimatedSprite(new BufferedImage[]{
				Eba.getImage("gui/hauptmenue/regler-normal.png"),
				Eba.getImage("gui/hauptmenue/regler-aktiv.png"),
				Eba.getImage("gui/hauptmenue/regler-pressed.png")}, 148, 440);
		mySpriteRegler.setImmutable(true);
		mySpriteRegler.setDataID("Regler");





	//****************************CancelOpt******************************************
		mySpriteCancelOpt = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b_chancel.png"),
				Eba.getImage("gui/hauptmenue/b_chancel_a.png"),
				Eba.getImage("gui/hauptmenue/b_chancel_p.png") }, 470, 490);
		mySpriteCancelOpt.setImmutable(true);
		mySpriteCancelOpt.setDataID("CancelOpt");


		//*********************OkayOpt***********************************************
		mySpriteOkayOpt= new AnimatedSprite(new BufferedImage[]{
				Eba.getImage("gui/hauptmenue/b_okay.png"),
				Eba.getImage("gui/hauptmenue/b_okay_a.png"),
				Eba.getImage("gui/hauptmenue/b_okay_p.png")}, 440,490);
		mySpriteOkayOpt.setImmutable(true);
		mySpriteOkayOpt.setDataID("OkayOpt");

		//*******************Textoptionen Text an oder aus*****************************
		mySpriteTextOpt = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b-unchecked.png"),
				Eba.getImage("gui/hauptmenue/b-active.png"),
				Eba.getImage("gui/hauptmenue/b-checked.png") }, 148, 300);
		mySpriteTextOpt.setImmutable(true);
		mySpriteTextOpt.setDataID("TextOpt");

		//***********************Soundoptionen Sound an oder aus***********************
		mySpriteSoundOpt = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b-unchecked.png"),
				Eba.getImage("gui/hauptmenue/b-active.png"),
				Eba.getImage("gui/hauptmenue/b-checked.png") }, 148, 340);
		mySpriteSoundOpt.setImmutable(true);
		mySpriteSoundOpt.setDataID("SoundOpt");

		//**********************Musikoptionen Musik an oder aus************************
		mySpriteMusicOpt = new AnimatedSprite(new BufferedImage[]{
				Eba.getImage("gui/hauptmenue/b-unchecked.png"),
				Eba.getImage("gui/hauptmenue/b-active.png"),
				Eba.getImage("gui/hauptmenue/b-checked.png")}, 148,260);
		mySpriteMusicOpt.setImmutable(true);
		mySpriteMusicOpt.setDataID("MusicOpt");
		// ************************************************************************************

		int i = 1;
		for (int y = 0; y < 3 ; y++)
		{
			for (int x = 0; x < 3; x++ )
			{
					mySpriteSpielstandScreenshot[i] = new AnimatedSprite (new BufferedImage[] {
					Eba.getImage("gui/hauptmenue/slot_empty.png"),
					Eba.getImage("gui/hauptmenue/slot_empty_a.png"),
					Eba.getImage("gui/hauptmenue/slot_empty_p.png")} , 120+x*130, 230+y*100);
					mySpriteSpielstandScreenshot[i].setImmutable(true);
					mySpriteSpielstandScreenshot[i].setDataID("Slot"+String.valueOf(i));
					myBufImage = Eba.getImage("EbaSaveGame/EbaSaveGame"+String.valueOf(i)+".png");
					mySpritePNGs[i] = new Sprite(myBufImage,129+x*130, 238+y*100);
					mySpritePNGs[i].setDataID("PNG "+String.valueOf(i));
					i++;
			}
		}


		for (int ii = 1 ; ii < 10 ; ii++) {

			myGuiSprites.add(mySpriteSpielstandScreenshot[ii]);
			mySpriteSpielstandScreenshot[ii].setImmutable(true);
			mySpriteSpielstandScreenshot[ii].setActive(false);
			myGuiSprites.add(mySpritePNGs[ii]);
			mySpritePNGs[ii].setImmutable(true);
			mySpritePNGs[ii].setActive(false);
		}

		myGuiSprites.add(mySpriteOkay);
		mySpriteOkay.setActive(false);
		myGuiSprites.add(mySpriteCancel);
		mySpriteCancel.setActive(false);

		// ***** Infozeile wird erst bei Bedarf angezeigt *****
		mySpriteInfoleiste = new Sprite(Eba.getImage("gui/hauptmenue/infoleiste.png"), 125, 202);
		mySpriteInfoleiste.setImmutable(true);
		myGuiSprites.add(mySpriteInfoleiste);
		mySpriteInfoleiste.setDataID("Infoleiste");
		mySpriteInfoleiste.setActive(false);

		//*******************************************************

		confirmationText = new EbaCommandPromptSprite("MenueFont", "MenueFont", myEbaGameEngine.fontManager, 135, 203);
		confirmationText.setImmutable(true);
		confirmationText.setDataID("Infoleiste");
		myGuiSprites.add(confirmationText);

		musicText= new EbaCommandPromptSprite("MenueFont", "MenueFont", myEbaGameEngine.fontManager, 190, 260);
		musicText.setImmutable(true);
		musicText.setDataID("Musiktext");
		myGuiSprites.add(musicText);

		soundText= new EbaCommandPromptSprite("MenueFont", "MenueFont", myEbaGameEngine.fontManager, 190, 340);
		soundText.setImmutable(true);
		soundText.setDataID("Soundtext");
		myGuiSprites.add(soundText);

		textText= new EbaCommandPromptSprite("MenueFont", "MenueFont", myEbaGameEngine.fontManager, 190, 300);
		textText.setImmutable(true);
		textText.setDataID("Texttext");
		myGuiSprites.add(textText);

		geschwText= new EbaCommandPromptSprite("MenueFont", "MenueFont", myEbaGameEngine.fontManager, 190, 400);
		geschwText.setImmutable(true);
		geschwText.setDataID("Texttext");
		myGuiSprites.add(geschwText);


	}

	public void update(long elapsedTime) {
		super.update(elapsedTime);

		if (myMenueSprites.isActive()) {
			myGuiSprites.setActive(true);
			myMenueSprites.setActive(false);
		}

		if(isReglerButtonActive){
			mySpriteRegler.setX(getMouseX()-16);

			if ((getMouseX()-14)< 148){
				mySpriteRegler.setX(148);
			}
			else if ((getMouseX()-14)> 450){
				mySpriteRegler.setX(450);
			}
		}


			// ***** ON MOUSE OVER *****
			mySprite = checkPosMouse(myGuiSprites, true);

			if (mySprite == null) {
				myDataID = "";
			} else {
				myDataID = (String) mySprite.getDataID();
			}

			if ((isAnyButtonActive) || (pressedButton != "")) {
				mySpriteLaden.setFrame(0);
				mySpriteNeu.setFrame(0);
				mySpriteOptionen.setFrame(0);
				mySpriteBeenden.setFrame(0);
				mySpriteOkay.setFrame(0);
				mySpriteCancel.setFrame(0);
				mySpriteCancelOpt.setFrame(0);
				mySpriteOkayOpt.setFrame(0);
				mySpriteReglerleiste.setFrame(0);
				mySpriteRegler.setFrame(0);
				isAnyButtonActive = false;
			}

			if (bsInput.isMouseDown(MouseEvent.BUTTON1)) {
				if (myDataID == "Laden") {
					mySpriteLaden.setFrame(2);
				} else if (myDataID == "Neu") {
					mySpriteNeu.setFrame(2);
				} else if (myDataID == "Optionen") {
					mySpriteOptionen.setFrame(2);
				} else if (myDataID == "Beenden") {
					mySpriteBeenden.setFrame(2);
				} else if (mySpriteNeu.getDataID() == pressedButton) {
					mySpriteNeu.setFrame(2);
				} else if (mySpriteLaden.getDataID() == pressedButton) {
					mySpriteLaden.setFrame(2);
				} else if (mySpriteOptionen.getDataID() == pressedButton) {
					mySpriteOptionen.setFrame(2);
				} else if (mySpriteBeenden.getDataID() == pressedButton) {
					mySpriteBeenden.setFrame(2);
					
				} else if (mySpriteRegler.getDataID() == pressedButton) {
					mySpriteRegler.setFrame(2);
				} else if (mySpriteCancel.getDataID() == pressedButton) {
					mySpriteCancel.setFrame(2);
				} else if (mySpriteCancelOpt.getDataID() == pressedButton) {
					mySpriteCancelOpt.setFrame(2);
				} else if(mySpriteOkayOpt.getDataID() == pressedButton){
					mySpriteOkayOpt.setFrame(2);
				} else if (mySpriteSoundOpt.getDataID() == pressedButton) {
					mySpriteSoundOpt.setFrame(2);
				} else if (mySpriteTextOpt.getDataID() == pressedButton) {
					mySpriteTextOpt.setFrame(2);
				} else if (mySpriteMusicOpt.getDataID()== pressedButton) {
					mySpriteMusicOpt.setFrame(2);
				} else if (mySpriteOkay.getDataID() == pressedButton) {
					mySpriteOkay.setFrame(2);
				}
			} else {
				if (myDataID == "Laden") {
					mySpriteLaden.setFrame(1);
					isAnyButtonActive = true;
				} else if (myDataID == "Neu") {
					mySpriteNeu.setFrame(1);
					isAnyButtonActive = true;
				} else if (myDataID == "Optionen") {
					mySpriteOptionen.setFrame(1);
					isAnyButtonActive = true;
				} else if (myDataID == "Beenden") {
					mySpriteBeenden.setFrame(1);
					isAnyButtonActive = true;
				} else if (myDataID == "Cancel") {
					mySpriteCancel.setFrame(1);
					isAnyButtonActive = true;
				} else if (myDataID == "CancelOpt") {
					mySpriteCancelOpt.setFrame(1);
					isAnyButtonActive = true;
				} else if (myDataID == "OkayOpt") {
					mySpriteOkayOpt.setFrame(1);
					isAnyButtonActive = true;

				} else if (myDataID == "Okay") {
					mySpriteOkay.setFrame(1);
					isAnyButtonActive = true;}

				else {
					isAnyButtonActive = false;
				}
			}
			// ***** ENDE ON MOUSE OVER *****




			// ***** ON MOUSE PRESSED *****

			switch (bsInput.getMousePressed()) {
			case BaseInput.NO_BUTTON:
				break;
			case MouseEvent.BUTTON1:
				// linke Maustaste:

				if (myDataID == "") {
					mySprite = checkPosMouse(myGuiSprites, true);

					if (mySprite != null) {


						// TODO Aktionen bei gedrueckter Maustaste ergaenzen

					}
				}
				if (myDataID == "Optionen")
					{
					confirmationText.change("",false);
					mySpriteInfoleiste.setActive(false);
					mySpriteCancel.setActive(false);
					musicText.change("Musik an / aus",false);
					soundText.change("Sound an / aus",false);
					textText.change("Text an / aus",false);
					myGuiSprites.add(musicText);
					myGuiSprites.add(soundText);
					myGuiSprites.add(textText);
					musicText.setActive(true);
					soundText.setActive(true);
					textText.setActive(true);
					mySpriteOptionsfenster.setActive(true);
					
					mySpriteBeenden.setActive(false);
					mySpriteLaden.setActive(false);
					mySpriteNeu.setActive(false);
					mySpriteOptionen.setActive(false);
					
					mySpriteOptionsLabel.setActive(true);
					
				
					for (int ii = 1 ; ii < 10 ; ii++) {

						mySpriteSpielstandScreenshot[ii].setActive(false);
						mySpritePNGs[ii].setActive(false);
						}
					if (firstTimeOpt == false){
						firstTimeOpt = true;
						
						mySoundOn = myEbaGameEngine.soundOn;
						myMusicOn = myEbaGameEngine.musicOn;
						myTextOn = myEbaGameEngine.textOn;
						
						myGuiSprites.add(mySpriteSoundOpt);
						mySpriteSoundOpt.setFrame(2);
						myGuiSprites.add(mySpriteTextOpt);
						mySpriteTextOpt.setFrame(2);
						myGuiSprites.add(mySpriteMusicOpt);
						mySpriteMusicOpt.setFrame(2);
						myGuiSprites.add(geschwText);
						musicText.change("Musik an / aus",false);
						soundText.change("Sound an / aus",false);
						textText.change("Text an / aus",false);
						geschwText.change("Textgeschwindigkeit ",false);
						geschwText.setActive(false);
						myGuiSprites.add(mySpriteOkayOpt);
						myGuiSprites.add(mySpriteCancelOpt);
						myGuiSprites.add(mySpriteReglerleiste);
						mySpriteReglerleiste.setActive(false);
						myGuiSprites.add(mySpriteRegler);
						mySpriteRegler.setActive(false);
						mySpriteOptionsfenster.setActive(true);
						mySpriteCancelOpt.setActive(true);
						mySpriteOkayOpt.setActive(true);
						mySpriteTextOpt.setActive(true);
						mySpriteSoundOpt.setActive(true);
						mySpriteMusicOpt.setActive(true);
						mySpriteInfoleiste.setActive(true);
						myGuiSprites.add(mySpriteMusicOpt);
						mySpriteMusicOpt.setFrame(2);
						myGuiSprites.add(mySpriteTextOpt);
						mySpriteTextOpt.setFrame(2);
						myGuiSprites.add(mySpriteSoundOpt);
						mySpriteSoundOpt.setFrame(2);
					
					}  
					if(firstTimeOpt == true)
					{
					mySpriteCancelOpt.setActive(true);
					mySpriteOkayOpt.setActive(true);
					mySpriteInfoleiste.setActive(true);
					mySpriteTextOpt.setActive(true); 
					musicText.change("Musik an / aus",true);
					soundText.change("Sound an / aus",true);
					textText.change("Text an / aus",true);
					geschwText.change("Textgeschwindigkeit ",false);
					geschwText.setActive(false);
					mySpriteInfoleiste.setActive(true);
					mySpriteSoundOpt.setActive(true);
					mySpriteMusicOpt.setActive(true);
					if(myEbaGameEngine.soundOn == false)
					{
						mySpriteRegler.setActive(true);
						mySpriteReglerleiste.setActive(true);
						geschwText.setActive(true);
					}
					
					}
					}
					
					
					

				if (myDataID == "Laden"){

					for (int ii = 1 ; ii < 10 ; ii++) {

						mySpriteSpielstandScreenshot[ii].setActive(true);
						mySpritePNGs[ii].setActive(true);

				    }
					confirmationText.change("",false);
					geschwText.setActive(false);
					musicText.setActive(false);
					soundText.setActive(false);
					textText.setActive(false);
					mySpriteReglerleiste.setActive(false);
					mySpriteRegler.setActive(false);
					mySpriteInfoleiste.setActive(false);
					mySpriteOptionsfenster.setActive(false);
					mySpriteCancelOpt.setActive(false);
					mySpriteOkayOpt.setActive(false);
					mySpriteTextOpt.setActive(false);
					mySpriteSoundOpt.setActive(false);
					mySpriteMusicOpt.setActive(false);

					mySpriteInfoleiste.setActive(true);
					mySpriteCancel.setActive(true);

					mySpriteBeenden.setActive(false);
					mySpriteLaden.setActive(false);
					mySpriteNeu.setActive(false);
					mySpriteOptionen.setActive(false);
					
					mySpriteLoadLabel.setActive(true);
					
//					myLadenDialog.setActive(true);
				}


				if (myDataID == "Regler")
				{
					isReglerButtonActive = true;
				}


				if (myDataID == "TextOpt" )
				{
					if((myEbaGameEngine.textOn == true && myEbaGameEngine.soundOn == false)|| (myEbaGameEngine.textOn == true)){
						confirmationText.change("Text aus / Sound an",false);
						myEbaGameEngine.textOn = false;
						mySpriteTextOpt.setFrame(0);
						mySpriteSoundOpt.setFrame(2);
						myEbaGameEngine.soundOn = true;
						mySpriteReglerleiste.setActive(false);
						mySpriteRegler.setActive(false);
						geschwText.setActive(false);
						
					}
					else if(myEbaGameEngine.textOn == false){
						confirmationText.change("Text an ",false);
						myEbaGameEngine.textOn = true;
						mySpriteTextOpt.setFrame(2);

					}
				}


				if (myDataID == "SoundOpt")
				{

					if((myEbaGameEngine.soundOn == true && myEbaGameEngine.textOn == false)||(myEbaGameEngine.soundOn == true)){
						confirmationText.change("Sound aus / Text an",false);
						myEbaGameEngine.soundOn = false;
						myEbaGameEngine.textOn = true;
						mySpriteReglerleiste.setActive(true);
						mySpriteRegler.setActive(true);
						geschwText.setActive(true);
						isReglerButtonActive = false;
						mySpriteTextOpt.setFrame(2);
						mySpriteTextOpt.setActive(false);
						mySpriteSoundOpt.setFrame(0);
						
						mySpriteTextOpt.setActive(true);
					}
					else if(myEbaGameEngine.soundOn == false){
						confirmationText.change("Sound an ",false);
						myEbaGameEngine.soundOn=true;

						mySpriteReglerleiste.setActive(false);
						mySpriteRegler.setActive(false);
						geschwText.setActive(false);
						myEbaGameEngine.textPos = 148 / textTeiler;
						mySpriteTextOpt.setActive(true);
						mySpriteSoundOpt.setFrame(2);
						mySpriteTextOpt.setFrame(2);
					}

				}


				if (myDataID == "MusicOpt")
				{
					if(myEbaGameEngine.musicOn == true){
						confirmationText.change("Musik aus ",false);
						myEbaGameEngine.musicOn = false;
						mySpriteMusicOpt.setFrame(0);
						bsMusic.stop(myMusicFile);
						}
					else if(myEbaGameEngine.musicOn == false){
						confirmationText.change("Musik an ",false);
						myEbaGameEngine.musicOn = true;
						mySpriteMusicOpt.setFrame(2);
						bsMusic.play(myMusicFile);
						}
				}


			    if (myDataID.startsWith("PNG") || (myDataID.startsWith("Slot"))){

					slot = Integer.valueOf(String.valueOf(myDataID.charAt(4)));	
			    	
					String mySaveGameName = EbaFileIO.getBezeichnung(slot);
										
					if (!mySaveGameName.equals("Leer")){
					confirmationText.change("Willst Du das Spiel in Slot "+slot+" laden?", false);

					mySpriteOkay.setActive(true);
					} else {
						confirmationText.change("Slot "+slot+" ist leer. Den willst Du nicht laden.", false);
						mySpriteOkay.setActive(false);
					}
				}


				if (myDataID == "Okay"){
					String mySaveGameName = EbaFileIO.getBezeichnung(slot);
					if(!mySaveGameName.equals("Leer")){
						EbaFileIO.ladeSaveGame(slot,myEbaGameEngine,this);
					} else {
						confirmationText.change("Glaub mir. Den willst Du nicht laden.", false);
					}
					
					mySpriteBeenden.setActive(true);
					mySpriteLaden.setActive(true);
					mySpriteNeu.setActive(true);
					mySpriteOptionen.setActive(true);
					
					mySpriteLoadLabel.setActive(false);
					mySpriteSaveLabel.setActive(false);
				}


				if (myDataID == "Cancel"){

					for (int ii = 1 ; ii < 10 ; ii++) {

						mySpriteSpielstandScreenshot[ii].setActive(false);
						mySpritePNGs[ii].setActive(false);

				    }

					mySpriteOkay.setActive(false);
					mySpriteCancel.setActive(false);
					mySpriteInfoleiste.setActive(false);
					mySpriteOptionsfenster.setActive(false);
					mySpriteTextOpt.setActive(false);
					mySpriteReglerleiste.setActive(false);
					mySpriteRegler.setActive(false);
					confirmationText.change("", false);
					
					mySpriteBeenden.setActive(true);
					mySpriteLaden.setActive(true);
					mySpriteNeu.setActive(true);
					mySpriteOptionen.setActive(true);
					
					mySpriteLoadLabel.setActive(false);
					mySpriteSaveLabel.setActive(false);

				}


				if (myDataID == "CancelOpt"){

					for (int ii = 1 ; ii < 10 ; ii++) {

						mySpriteSpielstandScreenshot[ii].setActive(false);
						mySpritePNGs[ii].setActive(false);

				    }
					bsMusic.play(myMusicFile);
					mySpriteOkayOpt.setActive(false);
					mySpriteCancelOpt.setActive(false);
					mySpriteInfoleiste.setActive(false);
					mySpriteOptionsfenster.setActive(false);
					mySpriteReglerleiste.setActive(false);
					mySpriteRegler.setActive(false);
					mySpriteTextOpt.setActive(false);
					isReglerButtonActive=false;
					confirmationText.change("", false);
					musicText.change("", false);
					soundText.change("", false);
					textText.change("", false);
					geschwText.change("", false);
					
					myEbaGameEngine.soundOn = mySoundOn;
					myEbaGameEngine.musicOn = myMusicOn;
					myEbaGameEngine.textOn = myTextOn;
					
					if(mySoundOn){
						
						mySpriteSoundOpt.setFrame(2);
					} else {
						
						mySpriteSoundOpt.setFrame(0);
					}
					if(myMusicOn){
						
						mySpriteMusicOpt.setFrame(2);
					} else {
						
						mySpriteMusicOpt.setFrame(0);
					}
					if(myTextOn){
						
						mySpriteTextOpt.setFrame(2);
					} else {
						
						mySpriteTextOpt.setFrame(0);
					}
					mySpriteCancelOpt.setActive(false);
					mySpriteSoundOpt.setActive(false);
					mySpriteMusicOpt.setActive(false);
					
					mySpriteBeenden.setActive(true);
					mySpriteLaden.setActive(true);
					mySpriteNeu.setActive(true);
					mySpriteOptionen.setActive(true);
					
					mySpriteOptionsLabel.setActive(false);
					
				}


				if(myDataID == "OkayOpt"){
					mySpriteOkayOpt.setActive(false);
					mySpriteCancelOpt.setActive(false);
					mySpriteSoundOpt.setActive(false);
					mySpriteMusicOpt.setActive(false);
					mySpriteInfoleiste.setActive(false);
					mySpriteOptionsfenster.setActive(false);
					mySpriteReglerleiste.setActive(false);
					mySpriteRegler.setActive(false);
					mySpriteTextOpt.setActive(false);
					confirmationText.change("", false);
					musicText.change("", false);
					soundText.change("", false);
					textText.change("", false);
					geschwText.change("", false);
					
					mySpriteBeenden.setActive(true);
					mySpriteLaden.setActive(true);
					mySpriteNeu.setActive(true);
					mySpriteOptionen.setActive(true);
					
					mySpriteOptionsLabel.setActive(false);
					
				}


				if (myDataID == "Neu"){
			    	myEbaGameEngine.nextGameID = 000002;
					this.finish();
				}


				if (myDataID == "Beenden"){

					parent.nextGameID = -1; // next locationNr or -1 to quit
					finish();
				}



				if (myDataID == "Infoleiste"){
			    	autsch++;
			    	switch(autsch){
			    	case 1:
			    		confirmationText.change("Autsch!", false);
			    		break;
			    	case 2:
			    		confirmationText.change("Hey!", false);
			    		break;
			    	case 3:
			    		confirmationText.change("Lass das!", false);
			    		break;
			    	case 4:
			    		confirmationText.change("Klick woanders hin, Mann!", false);
			    		autsch = 0;
			    		break;
			    	}


				} else {
					// DO NOTHING
				}
				break;

			}
			// ***** ENDE ON MOUSE PRESSED *****




			// ***** ON MOUSE RELEASED *****

			switch (bsInput.getMouseReleased()) {
			case BaseInput.NO_BUTTON:
				break;
			case MouseEvent.BUTTON1:

					if(isReglerButtonActive){

						if ((getMouseX()-14)<= 148){
						mySpriteRegler.setX(148);
						myEbaGameEngine.textPos = 148 / textTeiler;
						}
						else if ((getMouseX()-14)> 450){
						mySpriteRegler.setX(450);
						myEbaGameEngine.textPos = 450 / textTeiler;

						}
						else{
							myEbaGameEngine.textPos = getMouseX() / textTeiler;   // an dieser Schraube drehen und Textgeschwindigkeit zu ändern

						}
						isReglerButtonActive=false;

				}
			//
			// TODO evtl. ON MOUSE RELEASED Behandlung ergaenzen
			//
			// ***** ENDE ON MOUSE RELEASED *****


			}
	}
}
