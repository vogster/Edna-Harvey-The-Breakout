package de.daedalic.eba;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.golden.gamedev.engine.BaseInput;
import com.golden.gamedev.object.AnimatedSprite;
import com.golden.gamedev.object.Sprite;

public class EbaHauptmenue extends EbaSpriteGroup {

	static EbaFileIO myEbaFileIO;

	private Sprite mySprite;
	private String myDataID;

	private AnimatedSprite mySpriteLaden;
	private AnimatedSprite mySpriteSpeichern;
	private AnimatedSprite mySpriteOptionen;
	
	
	private AnimatedSprite mySpriteSpielstandScreenshot[] = new AnimatedSprite[10];
	private Sprite mySpritePNGs[] = new Sprite[10];

	private AnimatedSprite mySpriteBeenden;
	private AnimatedSprite mySpriteSpielen;

	private AnimatedSprite mySpriteOkay;
	private AnimatedSprite mySpriteOkayOpt; 	// Okay f�r Optionsmen�
	private AnimatedSprite mySpriteCancel;
	private AnimatedSprite mySpriteCancelOpt; 	// Cancel f�r Optionsmen�
	private AnimatedSprite mySpriteTextOpt;		// Textoptionen Текст вкл. oder aus
	private AnimatedSprite mySpriteSoundOpt;	// Soundoptionen Звук вкл.  oder aus
	private AnimatedSprite mySpriteMusicOpt;	// Musikoptionen Музыка вкл. oder aus

	private Sprite mySpriteInfoleiste;
	private Sprite mySpriteHauptmenueBackground;
	private Sprite mySpriteScreenshotGray;
	private Sprite mySpriteScreenshotFilter;
	private Sprite mySpriteOptionsLabel;
	private Sprite mySpriteLoadLabel;
	private Sprite mySpriteSaveLabel;
	
	private EbaCommandPromptSprite confirmationText;

	
	private EbaCommandPromptSprite musicText;
	private EbaCommandPromptSprite soundText;
	private EbaCommandPromptSprite textText;
	private EbaCommandPromptSprite geschwText;
	
	private AnimatedSprite mySpriteRegler;
	private AnimatedSprite mySpriteReglerleiste;
	boolean isReglerButtonActive = false;
	
	private boolean isAnyButtonActive = false;
	private boolean firstTimeOpt = false;
	
	private boolean isConfirmationButtonActive = false;
	private boolean isBeendenButtonActive = false;
	private boolean isLadenButtonActive = false;
	private boolean isSpeichernButtonActive = false;
	private boolean isLadenButtonpressed=false;
	private boolean isSpeichernButtonpressed=false;
	private int slot = 0;
	private int autsch = 0;   // Z�hler f�r die sprechende Infoleiste. Nur ein Gimmik.
	final static int textTeiler = 2;
	
	private BufferedImage myBufImage;

	private String pressedButton = "";

	private EbaGameObject myEbaGameObject;
	private EbaGameEngine myEbaGameEngine;
	
	private boolean mySoundOn = true;
	private boolean myMusicOn = true;
	private boolean myTextOn = true;
	
	public EbaHauptmenue(EbaGameObject myGameObject, EbaGameEngine myGameEngine) {
		super("Hauptmenue");

		myEbaGameObject = myGameObject;
		myEbaGameEngine = myGameEngine;


		// ***** Platzhalter f�r sp�teres Szenenbild in schwarz/weiss *****
		mySpriteScreenshotGray = new Sprite(Eba.getImage("gui/hauptmenue/filterscreen.png"), 0, 0);
		add(mySpriteScreenshotGray);
		mySpriteScreenshotGray.setDataID("ScreenshotGray");
		// ****************************************************************



		// ***** Lichteffekt �ber Szenenbild ******
		mySpriteScreenshotFilter = new Sprite(Eba.getImage("gui/hauptmenue/filterscreen.png"), 0, 0);
		add(mySpriteScreenshotFilter);
		mySpriteScreenshotFilter.setDataID("ScreenshotFilter");
		//*****************************************



		// ***** Hintergrundbild *****
		mySpriteHauptmenueBackground = new Sprite(Eba.getImage("gui/hauptmenue/bg_menu.png"), 135, 150);
		add(mySpriteHauptmenueBackground);
		mySpriteHauptmenueBackground.setDataID("HauptmenueBackground");
		//****************************

		// ***** Submenu-Label *****
		mySpriteOptionsLabel = new Sprite(Eba.getImage("gui/hauptmenue/b_optionen_i.png"), 120, 150);
		add(mySpriteOptionsLabel);
		mySpriteOptionsLabel.setDataID("OptionLabel");
		mySpriteOptionsLabel.setImmutable(true);
		mySpriteOptionsLabel.setActive(false);
		
		mySpriteLoadLabel = new Sprite(Eba.getImage("gui/hauptmenue/b_laden_i.png"), 120, 150);
		add(mySpriteLoadLabel);
		mySpriteLoadLabel.setDataID("LoadLabel");
		mySpriteLoadLabel.setImmutable(true);
		mySpriteLoadLabel.setActive(false);
		
		mySpriteSaveLabel = new Sprite(Eba.getImage("gui/hauptmenue/b_speichern_i.png"), 120, 150);
		add(mySpriteSaveLabel);
		mySpriteSaveLabel.setDataID("SaveLabelInactive");
		mySpriteSaveLabel.setImmutable(true);
		mySpriteSaveLabel.setActive(false);
		
		//****************************


		// ***** SpielstandScreenhots *****
		int i = 1;
		for (int y = 0; y < 3 ; y++)
		{
			for (int x = 0; x < 3; x++ )
			{
					mySpriteSpielstandScreenshot[i] = new AnimatedSprite (new BufferedImage[] {
					Eba.getImage("gui/hauptmenue/slot_empty.png"),
					Eba.getImage("gui/hauptmenue/slot_empty_a.png"),
					Eba.getImage("gui/hauptmenue/slot_empty_p.png")} , 220+x*130, 150+y*100);
					mySpriteSpielstandScreenshot[i].setImmutable(true);
					mySpriteSpielstandScreenshot[i].setDataID("Slot"+String.valueOf(i));

					myBufImage = Eba.getImage("EbaSaveGame/EbaSaveGame"+String.valueOf(i)+".png");
					mySpritePNGs[i] = new Sprite(myBufImage,229+x*130, 158+y*100);
					mySpritePNGs[i].setDataID("PNG "+String.valueOf(i));
					i++;
			}
		}
		// ********************************


		// ***** Button oben (Laden, Speichern, Optionen) *****
		mySpriteLaden = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b_laden.png"),
				Eba.getImage("gui/hauptmenue/b_laden_a.png"),
				Eba.getImage("gui/hauptmenue/b_laden_p.png"),
				Eba.getImage("gui/hauptmenue/b_laden_i.png")}, 120, 180);
		mySpriteLaden.setImmutable(true);
		mySpriteLaden.setDataID("Laden");
		add(mySpriteLaden);

		mySpriteSpeichern = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b_speichern.png"),
				Eba.getImage("gui/hauptmenue/b_speichern_a.png"),
				Eba.getImage("gui/hauptmenue/b_speichern_p.png"),
				Eba.getImage("gui/hauptmenue/b_speichern_i.png") }, 120, 210);
		mySpriteSpeichern.setImmutable(true);
		mySpriteSpeichern.setDataID("Speichern");
		add(mySpriteSpeichern);

		mySpriteOptionen = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b_optionen.png"),
				Eba.getImage("gui/hauptmenue/b_optionen_a.png"),
				Eba.getImage("gui/hauptmenue/b_optionen_p.png"),					/** TODO Button "Optionen" momentan ohne Mouseover **/
				Eba.getImage("gui/hauptmenue/b_optionen_i.png") }, 120, 150);
		mySpriteOptionen.setImmutable(true);
		mySpriteOptionen.setDataID("Optionen");
		add(mySpriteOptionen);
		// *****************************************************


		// ***** Button unten (Beenden, Spielen) *****
		mySpriteBeenden = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b_beenden.png"),
				Eba.getImage("gui/hauptmenue/b_beenden_a.png"),
				Eba.getImage("gui/hauptmenue/b_beenden_p.png"),
				Eba.getImage("gui/hauptmenue/b_beenden_i.png") }, 120, 390);
		mySpriteBeenden.setImmutable(true);
		mySpriteBeenden.setDataID("Beenden");
		add(mySpriteBeenden);


		mySpriteSpielen = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b_spielen.png"),
				Eba.getImage("gui/hauptmenue/b_spielen_a.png"),
				Eba.getImage("gui/hauptmenue/b_spielen_p.png"),
				Eba.getImage("gui/hauptmenue/b_spielen_i.png") }, 120, 420);
		mySpriteSpielen.setImmutable(true);
		mySpriteSpielen.setDataID("Spielen");
		add(mySpriteSpielen);
		// ********************************************



		// ***** Button "ok" und "cancel" werden erst bei Bedarf dem Menue hinzugefuegt ********
		mySpriteOkay = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b_okay.png"),
				Eba.getImage("gui/hauptmenue/b_okay_a.png"),
				Eba.getImage("gui/hauptmenue/b_okay_p.png") }, 552, 450);
		mySpriteOkay.setImmutable(true);
		mySpriteOkay.setDataID("Okay");
		add(mySpriteOkay);
		mySpriteOkay.setActive(false);

		mySpriteCancel = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b_chancel.png"),
				Eba.getImage("gui/hauptmenue/b_chancel_a.png"),
				Eba.getImage("gui/hauptmenue/b_chancel_p.png") }, 580, 450);
		mySpriteCancel.setImmutable(true);
		mySpriteCancel.setDataID("Cancel");
		//add(mySpriteCancel);
		//mySpriteCancel.setActive(false);

		// ************************************************************************************

		//************************Reglerleiste***********************
		mySpriteReglerleiste = new AnimatedSprite(new BufferedImage[]{
			Eba.getImage("gui/hauptmenue/reglerleiste.png")}, 240,400);
			mySpriteReglerleiste.setImmutable(true);
			mySpriteReglerleiste.setDataID("Reglerleiste");


		//************************Regler******************************
		mySpriteRegler = new AnimatedSprite(new BufferedImage[]{
				Eba.getImage("gui/hauptmenue/regler-normal.png"),
				Eba.getImage("gui/hauptmenue/regler-aktiv.png"),
				Eba.getImage("gui/hauptmenue/regler-pressed.png")}, 238, 390);
		mySpriteRegler.setImmutable(true);
		mySpriteRegler.setDataID("Regler");





	//****************************CancelOpt******************************************
		mySpriteCancelOpt = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b_chancel.png"),
				Eba.getImage("gui/hauptmenue/b_chancel_a.png"),
				Eba.getImage("gui/hauptmenue/b_chancel_p.png") }, 552, 450);
		mySpriteCancelOpt.setImmutable(true);
		mySpriteCancelOpt.setDataID("CancelOpt");


		//*********************OkayOpt***********************************************
		mySpriteOkayOpt= new AnimatedSprite(new BufferedImage[]{
				Eba.getImage("gui/hauptmenue/b_okay.png"),
				Eba.getImage("gui/hauptmenue/b_okay_a.png"),
				Eba.getImage("gui/hauptmenue/b_okay_p.png")}, 580, 450);
		mySpriteOkayOpt.setImmutable(true);
		mySpriteOkayOpt.setDataID("OkayOpt");

		//*******************Textoptionen Текст вкл. oder aus*****************************
		mySpriteTextOpt = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b-unchecked.png"),
				Eba.getImage("gui/hauptmenue/b-active.png"),
				Eba.getImage("gui/hauptmenue/b-checked.png") }, 238, 250);
		mySpriteTextOpt.setImmutable(true);
		mySpriteTextOpt.setDataID("TextOpt");
		
		if(myEbaGameEngine.textOn){
			mySpriteTextOpt.setFrame(2);
		} else {
			mySpriteTextOpt.setFrame(0);
		}
		

		//***********************Soundoptionen Звук вкл.  oder aus***********************
		mySpriteSoundOpt = new AnimatedSprite(new BufferedImage[] {
				Eba.getImage("gui/hauptmenue/b-unchecked.png"),
				Eba.getImage("gui/hauptmenue/b-active.png"),
				Eba.getImage("gui/hauptmenue/b-checked.png") }, 238, 290);
		mySpriteSoundOpt.setImmutable(true);
		mySpriteSoundOpt.setDataID("SoundOpt");
		
		if(myEbaGameEngine.soundOn){
			mySpriteSoundOpt.setFrame(2);
		} else {
			mySpriteSoundOpt.setFrame(0);
		}
		
		//**********************Musikoptionen Музыка вкл. oder aus************************
		mySpriteMusicOpt = new AnimatedSprite(new BufferedImage[]{
				Eba.getImage("gui/hauptmenue/b-unchecked.png"),
				Eba.getImage("gui/hauptmenue/b-active.png"),
				Eba.getImage("gui/hauptmenue/b-checked.png")}, 238,210);
		mySpriteMusicOpt.setImmutable(true);
		mySpriteMusicOpt.setDataID("MusicOpt");
		
		if(myEbaGameEngine.musicOn){
			mySpriteMusicOpt.setFrame(2);
		} else {
			mySpriteMusicOpt.setFrame(0);
		}
		
		// ************************************************************************************
		

		for (int ii = 1 ; ii < 10 ; ii++) {

			add(mySpriteSpielstandScreenshot[ii]);
			mySpriteSpielstandScreenshot[ii].setImmutable(true);
			mySpriteSpielstandScreenshot[ii].setActive(false);
			add(mySpritePNGs[ii]);
			mySpritePNGs[ii].setImmutable(true);
			mySpritePNGs[ii].setActive(false);
	    }

		add(mySpriteOkay);
		mySpriteOkay.setActive(false);
		add(mySpriteCancel);
		mySpriteCancel.setActive(false);


		// ***** Infozeile wird erst bei Bedarf angezeigt *****
		mySpriteInfoleiste = new Sprite(Eba.getImage("gui/hauptmenue/infoleiste.png"), 225, 452);
		mySpriteInfoleiste.setImmutable(true);
		add(mySpriteInfoleiste);
		mySpriteInfoleiste.setDataID("Infoleiste");
		mySpriteInfoleiste.setActive(false);

		//*******************************************************

		confirmationText = new EbaCommandPromptSprite("MenueFont", "MenueFont", myEbaGameEngine.fontManager, 235, 453);
		confirmationText.setImmutable(true);
		confirmationText.setDataID("Infoleiste");
		add(confirmationText);


		musicText= new EbaCommandPromptSprite("MenueFont", "MenueFont", myEbaGameEngine.fontManager, 280, 210);
		musicText.setImmutable(true);
		musicText.setDataID("Musiktext");
		add(musicText);

		soundText= new EbaCommandPromptSprite("MenueFont", "MenueFont", myEbaGameEngine.fontManager, 280, 290);
		soundText.setImmutable(true);
		soundText.setDataID("Soundtext");
		add(soundText);

		textText= new EbaCommandPromptSprite("MenueFont", "MenueFont", myEbaGameEngine.fontManager, 280, 250);
		textText.setImmutable(true);
		textText.setDataID("Texttext");
		add(textText);

		geschwText= new EbaCommandPromptSprite("MenueFont", "MenueFont", myEbaGameEngine.fontManager, 280, 350);
		geschwText.setImmutable(true);
		geschwText.setDataID("Texttext");
		add(geschwText);



	}

	public void update(long elapsedTime) {
		super.update(elapsedTime);

		
		
		mySpriteScreenshotGray.setImage(myEbaGameObject.getScreenhotGray());

		

		if(isReglerButtonActive){
			mySpriteRegler.setX(myEbaGameObject.getMouseX()-16);

			if ((myEbaGameObject.getMouseX()-14)< 148){
				mySpriteRegler.setX(148);
			}
			else if ((myEbaGameObject.getMouseX()-14)> 450){
				mySpriteRegler.setX(450);
			}
		}
		
		

			// ***** ON MOUSE OVER *****
			mySprite = myEbaGameObject.checkPosMouse(this, true);

			if (mySprite == null) {
				myDataID = "";
			} else {
				myDataID = (String) mySprite.getDataID();
			}

			if ((isAnyButtonActive) || (pressedButton != "")) {
				mySpriteLaden.setFrame(0);
				mySpriteSpeichern.setFrame(0);
				mySpriteOptionen.setFrame(0);
				mySpriteBeenden.setFrame(0);
				mySpriteSpielen.setFrame(0);
				mySpriteOkay.setFrame(0);
				mySpriteCancel.setFrame(0);
				mySpriteCancelOpt.setFrame(0);
				mySpriteOkayOpt.setFrame(0);
				mySpriteReglerleiste.setFrame(0);
				mySpriteRegler.setFrame(0);
				for (int ii=1; ii < 10; ii++)
					mySpriteSpielstandScreenshot[ii].setFrame(0);
				isAnyButtonActive = false;
			}

			if  ( myEbaGameObject.bsInput.isMouseDown(MouseEvent.BUTTON1) ) {


				if (myDataID == "Laden")  {
					mySpriteLaden.setFrame(2);
				} else if (myDataID == "Speichern") {
					mySpriteSpeichern.setFrame(2);
				} else if (myDataID == "Optionen") {
					mySpriteOptionen.setFrame(2);
				} else if (myDataID == "Beenden") {
					mySpriteBeenden.setFrame(2);
				} else if (myDataID == "Spielen") {
					mySpriteSpielen.setFrame(2);
				} else if (myDataID == "Okay") {
					mySpriteOkay.setFrame(2);
				} else if (myDataID == "Cancel") {
					mySpriteCancel.setFrame(2);

				} else if (mySpriteSpeichern.getDataID() == pressedButton) {
					mySpriteSpeichern.setFrame(2);
				} else if (mySpriteLaden.getDataID() == pressedButton) {
					mySpriteLaden.setFrame(2);
				} else if (mySpriteOptionen.getDataID() == pressedButton) {
					mySpriteOptionen.setFrame(2);
				} else if (mySpriteBeenden.getDataID() == pressedButton) {
					mySpriteBeenden.setFrame(2);
				} else if (mySpriteSpielen.getDataID() == pressedButton) {
					mySpriteSpielen.setFrame(2);
				} else if (mySpriteOkay.getDataID() == pressedButton) {
					mySpriteOkay.setFrame(2);
				} else if (mySpriteCancel.getDataID() == pressedButton) {
					mySpriteCancel.setFrame(2);
					
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
					
				} else for (int ii=1; ii < 10; ii++)
			         {
						if (myDataID != null) if ( (myDataID.equals("Slot"+ii) || ( myDataID.equals("PNG"+ii)) ) ) {
							mySpriteSpielstandScreenshot[ii].setFrame(2);
							break;

						} else if ( (mySpriteSpielstandScreenshot[ii].getDataID() == pressedButton) || (mySpritePNGs[ii].getDataID() == pressedButton) ){
									mySpriteSpielstandScreenshot[ii].setFrame(2);
									break;
								}
			         }
			} else {

				if  (myDataID == "Laden") {
					mySpriteLaden.setFrame(1);
					isAnyButtonActive = true;
				} else if (myDataID == "Speichern") {
					mySpriteSpeichern.setFrame(1);
					isAnyButtonActive = true;
				} else if (myDataID == "Optionen") {
					mySpriteOptionen.setFrame(1);
					isAnyButtonActive = true;
				} else if (myDataID == "Beenden") {
					mySpriteBeenden.setFrame(1);
					isAnyButtonActive = true;
				} else if (myDataID == "Spielen") {
					mySpriteSpielen.setFrame(1);
					isAnyButtonActive = true;
				} else if (myDataID == "Okay") {
					mySpriteOkay.setFrame(1);
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

				} else for (int ii=1; ii < 10; ii++)
			         {
						if (myDataID != null) if ( (myDataID.equals("Slot"+ii)) || ( myDataID.equals("PNG "+ii)) ) {
							mySpriteSpielstandScreenshot[ii].setFrame(1);
							isAnyButtonActive = true;
							break;
						}
						else {
			        	 		isAnyButtonActive = false;
						}
			         }
			}
			// ***** ENDE ON MOUSE OVER *****




			// ***** ON MOUSE PRESSED *****

			switch (myEbaGameObject.bsInput.getMousePressed()) {
			case BaseInput.NO_BUTTON:
				break;
			case MouseEvent.BUTTON1:
				// linke Maustaste:

				if ( !isConfirmationButtonActive) {

					//************** OPTIONSMEN�-KRAM *****************
					
					if (myDataID == "Optionen")
					{
					
					confirmationText.change("",false);
					mySpriteInfoleiste.setActive(false);
					mySpriteCancel.setActive(false);
					musicText.change("Музыка вкл. / выкл.",false);
					soundText.change("Звук вкл. / выкл.",false);
					textText.change("Текст вкл. / выкл.",false);
					add(musicText);
					add(soundText);
					add(textText);
					musicText.setActive(true);
					soundText.setActive(true);
					textText.setActive(true);
					
					mySpriteBeenden.setActive(false);
					mySpriteLaden.setActive(false);
					mySpriteSpielen.setActive(false);
					mySpriteSpeichern.setActive(false);
					mySpriteOptionen.setActive(false);
					
					mySpriteOptionsLabel.setActive(true);
					
					//mySpriteOptionsfenster.setActive(true);
				
//					for (int ii = 1 ; ii < 10 ; ii++) {
//
//						mySpriteSpielstandScreenshot[ii].setActive(false);
//						mySpritePNGs[ii].setActive(false);
//						}
					
					if (firstTimeOpt == false){
						firstTimeOpt = true;
						add(mySpriteSoundOpt);
						add(mySpriteTextOpt);
						add(mySpriteMusicOpt);
						add(geschwText);
						musicText.change("Музыка вкл. / выкл.",false);
						soundText.change("Звук вкл. / выкл.",false);
						textText.change("Текст вкл. / выкл.",false);
						geschwText.change("Скорость текста ",false);
						geschwText.setActive(false);
						add(mySpriteOkayOpt);
						add(mySpriteCancelOpt);
						add(mySpriteReglerleiste);
						mySpriteReglerleiste.setActive(false);
						add(mySpriteRegler);
						mySpriteRegler.setActive(false);
						//mySpriteOptionsfenster.setActive(true);
						mySpriteCancelOpt.setActive(true);
						mySpriteOkayOpt.setActive(true);
						mySpriteTextOpt.setActive(true);
						mySpriteSoundOpt.setActive(true);
						mySpriteMusicOpt.setActive(true);
						mySpriteInfoleiste.setActive(true);
						add(mySpriteMusicOpt);
						add(mySpriteTextOpt);
						add(mySpriteSoundOpt);
					
					}  
					if(firstTimeOpt == true)
					{
					mySoundOn = myEbaGameEngine.soundOn;
					myMusicOn = myEbaGameEngine.musicOn;
					myTextOn = myEbaGameEngine.textOn;
						
						
					mySpriteCancelOpt.setActive(true);
					mySpriteOkayOpt.setActive(true);
					mySpriteInfoleiste.setActive(true);
					mySpriteTextOpt.setActive(true); 
					musicText.change("Музыка вкл. / выкл.",true);
					soundText.change("Звук вкл. / выкл.",true);
					textText.change("Текст вкл. / выкл.",true);
					geschwText.change("Скорость текста ",false);
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
					if (myDataID == "Regler")
					{
						isReglerButtonActive = true;
					}


					if (myDataID == "TextOpt" )
					{
						if((myEbaGameEngine.textOn == true && myEbaGameEngine.soundOn == false)|| (myEbaGameEngine.textOn == true)){
							confirmationText.change("Текст выкл. / Звук вкл.",false);
							myEbaGameEngine.textOn = false;
							mySpriteTextOpt.setFrame(0);
							mySpriteSoundOpt.setFrame(2);
							myEbaGameEngine.soundOn = true;
							mySpriteReglerleiste.setActive(false);
							mySpriteRegler.setActive(false);
							geschwText.setActive(false);
							
						}
						else if(myEbaGameEngine.textOn == false){
							confirmationText.change("Текст вкл. ",false);
							myEbaGameEngine.textOn = true;
							mySpriteTextOpt.setFrame(2);

						}
					}


					if (myDataID == "SoundOpt")
					{

						if((myEbaGameEngine.soundOn == true && myEbaGameEngine.textOn == false)||(myEbaGameEngine.soundOn == true)){
							confirmationText.change("Звук выкл. / Текст вкл.",false);
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
							confirmationText.change("Звук вкл.  ",false);
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
							confirmationText.change("Музыка выкл. ",false);
							myEbaGameEngine.musicOn = false;
							mySpriteMusicOpt.setFrame(0);
							myEbaGameObject.bsMusic.stop(myEbaGameObject.myMusicFile);
							}
						else if(myEbaGameEngine.musicOn == false){
							confirmationText.change("Музыка вкл. ",false);
							myEbaGameEngine.musicOn = true;
							mySpriteMusicOpt.setFrame(2);
							myEbaGameObject.bsMusic.play(myEbaGameObject.myMusicFile);
							}
					}
					
					if (myDataID == "CancelOpt"){

						for (int ii = 1 ; ii < 10 ; ii++) {

							mySpriteSpielstandScreenshot[ii].setActive(false);
							mySpritePNGs[ii].setActive(false);

					    }
						myEbaGameObject.bsMusic.play(myEbaGameObject.myMusicFile);
						
						mySpriteOkayOpt.setActive(false);
						mySpriteCancelOpt.setActive(false);
						
						mySpriteInfoleiste.setActive(false);
						//mySpriteOptionsfenster.setActive(false);
						mySpriteReglerleiste.setActive(false);
						mySpriteRegler.setActive(false);
						
						
						
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
						
						mySpriteSoundOpt.setActive(false);
						mySpriteMusicOpt.setActive(false);
						mySpriteTextOpt.setActive(false);
						
						isReglerButtonActive=false;
						confirmationText.change("", false);
						musicText.change("", false);
						soundText.change("", false);
						textText.change("", false);
						geschwText.change("", false);
						
						
						
						mySpriteBeenden.setActive(true);
						mySpriteLaden.setActive(true);
						mySpriteSpielen.setActive(true);
						mySpriteSpeichern.setActive(true);
						mySpriteOptionen.setActive(true);
						
						mySpriteOptionsLabel.setActive(false);
					}


					if(myDataID == "OkayOpt"){
						mySpriteOkayOpt.setActive(false);
						mySpriteCancelOpt.setActive(false);
						mySpriteSoundOpt.setActive(false);
						mySpriteMusicOpt.setActive(false);
						mySpriteInfoleiste.setActive(false);
						//mySpriteOptionsfenster.setActive(false);
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
						mySpriteSpielen.setActive(true);
						mySpriteSpeichern.setActive(true);
						mySpriteOptionen.setActive(true);
						
						mySpriteOptionsLabel.setActive(false);
					}
					
					// ****************   ENDE OPTIONSMEN� *************
					
					
					
					
					if (myDataID == "Beenden") {

						isConfirmationButtonActive = true;
						isSpeichernButtonActive = false;
						isLadenButtonActive = false;
						isBeendenButtonActive = true;

						for (int ii = 1 ; ii < 10 ; ii++) {
							mySpriteSpielstandScreenshot[ii].setActive(false);
							mySpritePNGs[ii].setActive(false);
					    }

						confirmationText.change("Willst Du das Spiel wirklich beenden?",false);
						mySpriteOkay.setActive(true);
						mySpriteInfoleiste.setActive(true);
						mySpriteCancel.setActive(true);


					}
					if (myDataID == "Spielen") {

						for (int ii = 1 ; ii < 10 ; ii++) {
							mySpriteSpielstandScreenshot[ii].setActive(false);
							mySpritePNGs[ii].setActive(false);
					    }
						confirmationText.change("", false);

						myEbaGameObject.myGuiSprites.setActive(true);
						this.setActive(false);
						isSpeichernButtonActive = false;
						isLadenButtonActive = false;
						isBeendenButtonActive = false;
						mySpriteSpielen.setFrame(0);
						mySpriteInfoleiste.setActive(true);
					}

					if (myDataID == "Speichern") {
						if (!isSpeichernButtonActive)  {
							for (int ii = 1 ; ii < 10 ; ii++) {
								mySpriteSpielstandScreenshot[ii].setActive(true);
								mySpritePNGs[ii].setActive(true);
						    }

						isBeendenButtonActive =false;
						isLadenButtonActive = false;
						isSpeichernButtonActive = true;
						mySpriteCancel.setActive(true);
						mySpriteInfoleiste.setActive(true);

					}
						if(isSpeichernButtonpressed){
//							String mySaveGameName = EbaFileIO.getBezeichnung(slot);
//							if (!mySaveGameName.equals("Leer")){
//								confirmationText.change("Willst Du das Spiel in Slot "+slot+" speichern?", false);
//								mySpriteOkay.setActive(true);
//							} else {
//								mySpriteOkay.setActive(false);
//							}
						}

						mySpriteBeenden.setActive(false);
						mySpriteLaden.setActive(false);
						mySpriteSpielen.setActive(false);
						mySpriteSpeichern.setActive(false);
						mySpriteOptionen.setActive(false);
						
						mySpriteSaveLabel.setActive(true);
				}

					if (myDataID == "Laden") {
						if (!isLadenButtonActive)  {
							for (int ii = 1 ; ii < 10 ; ii++) {
								mySpriteSpielstandScreenshot[ii].setActive(true);
								mySpritePNGs[ii].setActive(true);
						    }

							isBeendenButtonActive =false;
							isSpeichernButtonActive = false;
							isLadenButtonActive = true;
							mySpriteCancel.setActive(true);
							mySpriteInfoleiste.setActive(true);

						  }
						if(isLadenButtonpressed){
//							String mySaveGameName = EbaFileIO.getBezeichnung(slot);
//							if (!mySaveGameName.equals("Leer") && slot!=0){
//							confirmationText.change("Willst Du das Spiel in Slot "+slot+" laden?", false);
//							mySpriteOkay.setActive(true);
//							} else {
//								confirmationText.change("Slot "+slot+" ist leer. Den willst Du nicht laden.", false);
//								mySpriteOkay.setActive(false);
//							}
						}
						
						mySpriteBeenden.setActive(false);
						mySpriteLaden.setActive(false);
						mySpriteSpielen.setActive(false);
						mySpriteSpeichern.setActive(false);
						mySpriteOptionen.setActive(false);
						
						mySpriteLoadLabel.setActive(true);
						
					}
					
					
						if (myDataID.startsWith("PNG") || (myDataID.startsWith("Slot"))){
							isLadenButtonpressed=true;
							isSpeichernButtonpressed=true;
							if(isSpeichernButtonActive){

								slot = Integer.valueOf(String.valueOf(myDataID.charAt(4)));

								//String mySaveGameName = EbaFileIO.getBezeichnung(slot);
								
								//if (!mySaveGameName.equals("Leer")){
									confirmationText.change("Сохранить игру в слот "+slot+" ?", false);
									mySpriteOkay.setActive(true);
								//} else {
								//	mySpriteOkay.setActive(false);
								//}
							}
							if(isLadenButtonActive){
								slot = Integer.valueOf(String.valueOf(myDataID.charAt(4)));
								String mySaveGameName = EbaFileIO.getBezeichnung(slot);
								if (!mySaveGameName.equals("Leer")){
									confirmationText.change("Загрузить игру из слота "+slot+" ?", false);
									mySpriteOkay.setActive(true);
								} else {
									confirmationText.change("Слот "+slot+" пуст. Его не загрузить.", false);
									mySpriteOkay.setActive(false);
								}
							}
						}
				}

				if (myDataID == "Okay") {

					if (isSpeichernButtonActive) {

						isSpeichernButtonActive = false;
						for(int ii = 1 ; ii < 10 ; ii++) {
							mySpriteSpielstandScreenshot[ii].setActive(true);
							mySpritePNGs[ii].setActive(true);

						}
						EbaFileIO.speicherSaveGame(slot, myEbaGameEngine, myEbaGameObject);
						EbaFileIO.ladeSaveGame(slot,myEbaGameEngine,myEbaGameObject);
						for(int ii = 1 ; ii < 10 ; ii++) {
							mySpriteSpielstandScreenshot[ii].setActive(true);
							mySpritePNGs[ii].setActive(true);

						}
					}

					if (isLadenButtonActive) {
						isLadenButtonActive = false;
						for(int ii = 1 ; ii < 10 ; ii++) {
							mySpriteSpielstandScreenshot[ii].setActive(true);
							mySpritePNGs[ii].setActive(true);
						}

						EbaFileIO.ladeSaveGame(slot,myEbaGameEngine,myEbaGameObject);
					}

					if (isBeendenButtonActive) {

						myEbaGameObject.parent.nextGameID = -1; // next locationNr or -1 to quit
						myEbaGameObject.finish();
//						myEbaGameObject.parent.nextGameID = 0;
//						myEbaGameObject.finish();
//						isBeendenButtonActive = false;
					}

					mySpriteOkay.setActive(false);
					mySpriteCancel.setActive(false);
					mySpriteInfoleiste.setActive(false);
					confirmationText.change("", false);
					
					mySpriteBeenden.setActive(true);
					mySpriteLaden.setActive(true);
					mySpriteSpielen.setActive(true);
					mySpriteSpeichern.setActive(true);
					mySpriteOptionen.setActive(true);
					
					mySpriteLoadLabel.setActive(false);
					mySpriteSaveLabel.setActive(false);
					
					slot = 0;
					
					}

				if (myDataID == "Cancel"){
				    for (int ii = 1 ; ii < 10 ; ii++) {
				    	mySpriteSpielstandScreenshot[ii].setActive(false);
						mySpritePNGs[ii].setActive(false);
				    }
					mySpriteOkay.setActive(false);
					mySpriteCancel.setActive(false);
					mySpriteInfoleiste.setActive(false);
					confirmationText.change("", false);
					isLadenButtonActive = false;
					isSpeichernButtonActive = false;
					isBeendenButtonActive = false;
					isConfirmationButtonActive = false;
					
					mySpriteBeenden.setActive(true);
					mySpriteLaden.setActive(true);
					mySpriteSpielen.setActive(true);
					mySpriteSpeichern.setActive(true);
					mySpriteOptionen.setActive(true);
					
					mySpriteLoadLabel.setActive(false);
					mySpriteSaveLabel.setActive(false);
					
					slot = 0;
					
				}


				if (myDataID == "Infoleiste"){
			    	autsch++;
			    	switch(autsch){
			    	case 1:
			    		confirmationText.change("Ой!", false);
			    		break;
			    	case 2:
			    		confirmationText.change("Эй!", false);
			    		break;
			    	case 3:
			    		confirmationText.change("Оставь это!", false);
			    		break;
			    	case 4:
			    		confirmationText.change("Кликай в другом месте!", false);
			    		autsch = 0;
			    		break;
			    	}


				} else {
					// DO NOTHING
				}


			break;

			default: break;

			}
			
			
			switch (myEbaGameObject.bsInput.getMouseReleased()) {
			case BaseInput.NO_BUTTON:
				break;
			case MouseEvent.BUTTON1:

					if(isReglerButtonActive){

						if ((myEbaGameObject.getMouseX()-14)<= 148){
						mySpriteRegler.setX(148);
						myEbaGameEngine.textPos = 148 / textTeiler;
						}
						else if ((myEbaGameObject.getMouseX()-14)> 450){
						mySpriteRegler.setX(450);
						myEbaGameEngine.textPos = 450 / textTeiler;

						}
						else{
							myEbaGameEngine.textPos = myEbaGameObject.getMouseX() / textTeiler;   // an dieser Schraube drehen und Скорость текста zu �ndern

						}
						isReglerButtonActive=false;

				}
			//
			// TODO evtl. ON MOUSE RELEASED Behandlung ergaenzen
			//
			// ***** ENDE ON MOUSE RELEASED *****


			}
			
		}
			// ***** ENDE ON MOUSE PRESSED *****




			// ***** ON MOUSE RELEASED *****
			//
			// TODO evtl. ON MOUSE RELEASED Behandlung ergaenzen
			//
			// ***** ENDE ON MOUSE RELEASED *****

}


