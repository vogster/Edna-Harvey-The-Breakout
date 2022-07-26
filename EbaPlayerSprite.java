package de.daedalic.eba;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Stack;

import com.golden.gamedev.engine.BaseAudioRenderer;
import com.golden.gamedev.object.AnimatedSprite;
import com.golden.gamedev.object.GameFontManager;
import com.golden.gamedev.object.Timer;

@SuppressWarnings("serial")

public class EbaPlayerSprite extends AnimatedSprite implements EbaInterfaceHasBase {

	private BufferedImage renderMe;
	private double scaleFactor;

	final int WALKING = 0;
	final int TALKING = 1;
	final int THINKING = 2;
	final int WAITING = 3;
	final int ACTING = 4;

	double baseYatZeroScale; // Y-Koordinate vom Fluchtpunkt
	double baseYatFullScale; // Y-Koordinate bei Skalierung = 1

	double scaleFactorDivisor; // baseYatFullScale - baseYatZeroScale - super.getHeight()

	double x;
	double y;

	double hSpeed; // horizontale Geschwindigkeit in Pixeln pro Millisekunde

	double vSpeed; // vertikale Geschwindigkeit in Pixeln pro Millisekunde

	double cSpeed; // aktuelle Geschwindigkeit

	int status;

	int walkToX;

	int walkToY;

	int walkingAnzeigedauer;

	int[] walkingFrames;

	String talkingFont;

	String thinkingFont;

	Stack<int[]> walkToPoints;

	GameFontManager myGameFontManager;

	private int[] nextWalkToPoint;

	private EbaTextSprite myEbaTextSprite;

	private long tempMilliseconds;

	private Statement myStatement = null;

	private ResultSet myResultSet = null;

	private ResultSet myResultSet2 = null;

	private ResultSet myResultSet3 = null;
	private ResultSet myResultSet4;
	private ResultSet myResultSet5 = null;

	private int myRoomNumber;

	private EbaGameObject myEbaGameObject;

	//private EbaGameEngine myEbaGameEngine;

	private int[] linksStart = new int[30];

	private int[] linksEnde = new int[30];

	private int[] rechtsStart = new int[30];

	private int[] rechtsEnde = new int[30];

	private int[] vorneStart = new int[30];

	private int[] vorneEnde = new int[30];

	private int[] hintenStart = new int[30];

	private int[] hintenEnde = new int[30];

	private int[] actingStartFrame = new int[30];
	private int[] actingEndFrame = new int[30];
	private int i; // max. Index der Aktionsmodi

	private int[] anzeigedauer = new int[30];

	private boolean nachLinks;

	private boolean nachOben;

	private int startFrame;
	private int endeFrame;

	private int startFrameAlt = -1;
	private int endeFrameAlt = -1;

	private String myStandbyBlickrichtung;

	private Timer myTimer = new Timer(1);
//	private Timer myTimer2 = new Timer(1);

	private boolean isUnterbrochen;
	private boolean talkingWithSound = false;
	private String myAudioPfad;
//	private int myStatus = 1;
	private String myCharacterBlickrichtung;



	// *****************************
	// ******* KONSTRUKTOREN *******
	// *****************************
	public EbaPlayerSprite(int initRoomNumber, EbaGameObject initGameObject, String initCharakterBlickrichtung) {

		myRoomNumber = initRoomNumber;


		try {
			myStatement = EbaGameEngine.myConnection.createStatement();

			ResultSet myResultSet = myStatement.executeQuery("SELECT * FROM Raum WHERE ID =" + myRoomNumber);
			myResultSet.next();

			BufferedImage[] myImages = new BufferedImage[200];
			int myArrayIndex = 0;
			this.setID( (myResultSet.getInt("CharacterAnimationSetID")));

			myResultSet5 = myStatement.executeQuery("SELECT * from Aktionsmodus");
			while (myResultSet5.next())
			{
				i=myResultSet5.getInt("ID");

				myResultSet2 = myStatement.executeQuery("SELECT * FROM CharacterAnimationSet WHERE ID =" + myResultSet.getInt("CharacterAnimationSetID") + "AND AktionsmodusID =" + i);
				while (myResultSet2.next()) {
					myResultSet3 = myStatement.executeQuery("SELECT * FROM Animationsbild WHERE BildfolgeID =" + myResultSet2.getInt("LinksBildfolgeID"));
					linksStart[i] = myArrayIndex;
					while (myResultSet3.next()) {
						for (int j = 1; j <= myResultSet3.getInt("AbweichendeAnzeigedauer"); j++) {
							myImages[myArrayIndex] = Eba.getImage(myResultSet3.getString("Bilddatei"));
							myArrayIndex++;
						}
					}
					linksEnde[i] = myArrayIndex - 1;
					rechtsStart[i] = myArrayIndex;
					myResultSet3 = myStatement.executeQuery("SELECT * FROM Animationsbild WHERE BildfolgeID =" + myResultSet2.getInt("RechtsBildfolgeID"));
					while (myResultSet3.next()) {
						for (int j = 1; j <= myResultSet3.getInt("AbweichendeAnzeigedauer"); j++) {
							myImages[myArrayIndex] = Eba.getImage(myResultSet3.getString("Bilddatei"));
							myArrayIndex++;
						}
					}
					rechtsEnde[i] = myArrayIndex - 1;
					vorneStart[i] = myArrayIndex;
					myResultSet3 = myStatement.executeQuery("SELECT * FROM Animationsbild WHERE BildfolgeID =" + myResultSet2.getInt("VorneBildfolgeID"));
					while (myResultSet3.next()) {
						for (int j = 1; j <= myResultSet3.getInt("AbweichendeAnzeigedauer"); j++) {
							myImages[myArrayIndex] = Eba.getImage(myResultSet3.getString("Bilddatei"));
							myArrayIndex++;
						}
					}
					vorneEnde[i] = myArrayIndex - 1;
					hintenStart[i] = myArrayIndex;
					myResultSet3 = myStatement.executeQuery("SELECT * FROM Animationsbild WHERE BildfolgeID =" + myResultSet2.getInt("HintenBildfolgeID"));
					while (myResultSet3.next()) {
						for (int j = 1; j <= myResultSet3.getInt("AbweichendeAnzeigedauer"); j++) {
							myImages[myArrayIndex] = Eba.getImage(myResultSet3.getString("Bilddatei"));
							myArrayIndex++;
						}
					}
					hintenEnde[i] = myArrayIndex - 1;

					myResultSet4 = myStatement.executeQuery("SELECT * FROM Bildfolge WHERE ID =" + myResultSet2.getInt("LinksBildfolgeID"));
					myResultSet4.next();
					anzeigedauer[i] = myResultSet4.getInt("Anzeigedauer");
				}

			}

			super.setImages(myImages);


			initialise(initGameObject,myResultSet.getInt("baseYatZeroScale"),myResultSet.getInt("baseYatFullScale"),myResultSet.getDouble("hspeed"),myResultSet.getDouble("vspeed"),initCharakterBlickrichtung );

//			baseYatZeroScale = myBaseYatZeroScale;
//			baseYatFullScale = myBaseYatFullScale;
//			walkToPoints = myEbaGameObject.myEbaWalkableAreaMap;



		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (myResultSet != null) {
				try {
					myResultSet.close();
					myResultSet2.close();
					myResultSet3.close();
					myResultSet4.close();
					myResultSet5.close();
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

	// ************************************
	// ******* ÖFFENTLICHE METHODEN *******
	// ************************************

	public void lookAt(String myCharakterBlickrichtung){

		if(myStandbyBlickrichtung != ""){
			blickRichtung(myCharakterBlickrichtung);
		}
		status = WAITING;
		setAnimationFrame(actingStartFrame[WAITING], actingEndFrame[WAITING]);
		getAnimationTimer().setDelay(anzeigedauer[WAITING]);
		startFrameAlt = -1;
		endeFrameAlt = -1;
	}


	public void lookReallyAt(String myCharakterBlickrichtung){

		// Hallo Olaf, bin recht Müde und die einzige Möglichkeit
		// die mir eingefallen ist, die lookat-Methode für den
		// Skript-Interpreter zu benutzen ist dieser unschöne Klon
		// Sag mir bitte bescheid, wenn dir was besseres einfällt,
		// Dann muss ich den NSC und den Scriptinterpreter anpassen.

		blickRichtung(myCharakterBlickrichtung);

		status = WAITING;
		setAnimationFrame(actingStartFrame[WAITING], actingEndFrame[WAITING]);
		getAnimationTimer().setDelay(anzeigedauer[WAITING]);
		startFrameAlt = -1;
		endeFrameAlt = -1;
	}


	public void actionMode(int indexAktionsmodus, int miliseconds){
		if ((indexAktionsmodus > 3) && (indexAktionsmodus <= i)) {
			//status = indexAktionsmodus; // Änderung von Poki
			status = ACTING;              // Brauche einen definierten Status für alle Zusatzanimationen
			                              // als Abbruchbedingung.
			                              // Bislang immer mit "isActing" abgeprüft.
			setAnimationFrame(actingStartFrame[indexAktionsmodus], actingEndFrame[indexAktionsmodus]);
			
			System.out.println("Aktionsmodus: " + indexAktionsmodus);
			System.out.println("Delay: " + anzeigedauer[indexAktionsmodus]);
			
			getAnimationTimer().setDelay(anzeigedauer[indexAktionsmodus]);
			startFrameAlt = -1;
			endeFrameAlt = -1;
			myTimer = new Timer(miliseconds);
			myTimer.setActive(true);
//			myTimer2 = new Timer(miliseconds);
//			myTimer2.setActive(true);
		}
	}


	public EbaTextSprite say(String message,String audioPfad) {
		//	File langfile = new File(audioPfad);
		//	long length = langfile.length();
		//	long milliseconds = langfile.length();
		myAudioPfad = audioPfad;

		int milliseconds = (int) message.length() * myEbaGameObject.myEbaGameEngine.textPos;
		if (message.length() < 15 ) {
	    	milliseconds = (int) (milliseconds * 2);
		}
		tempMilliseconds = System.currentTimeMillis() + milliseconds;
		status = TALKING;

		setAnimationFrame(actingStartFrame[TALKING], actingEndFrame[TALKING]);
		getAnimationTimer().setDelay(anzeigedauer[TALKING]);

		File audioFile = new File(audioPfad);

		//wenn Schalter soundOn auf true und der AuioFile existiert dann spiele den Audiopfad

		if(myEbaGameObject.myEbaGameEngine.soundOn==true){
			if (audioFile.exists()) {
				//File file = new File(audioPfad);
				myEbaGameObject.myEbaGameEngine.bsSound.play(audioPfad);
				talkingWithSound = true;
				
				
				if(message.length() < 40){
					myEbaTextSprite = new EbaTextSprite(message, talkingFont, myGameFontManager, getBaseX(), getY(),audioPfad, myEbaGameObject);
				}else{
					String[] messages = divideZeile(message);
					myEbaTextSprite = new EbaTextSprite(message, messages, longestRow(messages, talkingFont), talkingFont, myGameFontManager, getBaseX(), getY(),audioPfad, myEbaGameObject);
				}
			} else {
				if(message.length() < 40){
					myEbaTextSprite = new EbaTextSprite(message, talkingFont, myGameFontManager, getBaseX(), getY(), milliseconds);
				}else{
					String[] messages = divideZeile(message);
					myEbaTextSprite = new EbaTextSprite(message, messages, longestRow(messages, talkingFont), talkingFont, myGameFontManager, getBaseX(), getY(), milliseconds);
				}
			}
		}
		if(myEbaGameObject.myEbaGameEngine.soundOn==false){
			if(message.length() < 40){
				myEbaTextSprite = new EbaTextSprite(message, talkingFont, myGameFontManager, getBaseX(), getY(), milliseconds);
			}else{
				String[] messages = divideZeile(message);
				myEbaTextSprite = new EbaTextSprite(message, messages, longestRow(messages, talkingFont), talkingFont, myGameFontManager, getBaseX(), getY(), milliseconds);
			}
		}
		if ((myEbaTextSprite.getWidth() + myEbaTextSprite.getX() + 10) > 800) {
			myEbaTextSprite.setX(790 - myEbaTextSprite.getWidth());
		}
		if (myEbaTextSprite.getX() < 10) {
			myEbaTextSprite.setX(10);
		}
		if(myEbaGameObject.myEbaGameEngine.textOn == true){
			return myEbaTextSprite;}

		else{
			if (!audioFile.exists()) {
				myEbaTextSprite.setActive(true);
				return myEbaTextSprite;
			}
			myEbaTextSprite.setActive(false);
			return myEbaTextSprite;
		}
	}
	
	public EbaTextSprite think(String message, String audioPfad) {
		myAudioPfad = audioPfad;

		int milliseconds = (int) message.length() * myEbaGameObject.myEbaGameEngine.textPos;
		if (message.length() < 15) {

			milliseconds = (int) (milliseconds * 2);
		}
		tempMilliseconds = System.currentTimeMillis() + milliseconds;
		status = THINKING;
		setAnimationFrame(actingStartFrame[THINKING], actingEndFrame[THINKING]);
		getAnimationTimer().setDelay(anzeigedauer[THINKING]);

		File audioFile = new File(audioPfad);
		if(myEbaGameObject.myEbaGameEngine.soundOn==true){
			if (audioFile.exists()) {
				myEbaGameObject.myEbaGameEngine.bsSound.play(audioPfad);
				talkingWithSound = true;
				
				if(message.length() < 40){
					myEbaTextSprite = new EbaTextSprite(message, thinkingFont, myGameFontManager, getBaseX(), getY(),audioPfad, myEbaGameObject);
				}else{
					String[] messages = divideZeile(message);
					myEbaTextSprite = new EbaTextSprite(message, messages, longestRow(messages, thinkingFont), thinkingFont, myGameFontManager, getBaseX(), getY(),audioPfad, myEbaGameObject);
				}
				
				

			} else {
				
				if(message.length() < 40){
					myEbaTextSprite = new EbaTextSprite(message, thinkingFont, myGameFontManager, getBaseX(), getY(), milliseconds);
				}else{
					String[] messages = divideZeile(message);
					myEbaTextSprite = new EbaTextSprite(message, messages, longestRow(messages, thinkingFont), thinkingFont, myGameFontManager, getBaseX(), getY(), milliseconds);
				}
			
			}
		}
		if(myEbaGameObject.myEbaGameEngine.soundOn==false){
			
			
			if(message.length() < 40){
				myEbaTextSprite = new EbaTextSprite(message, thinkingFont, myGameFontManager, getBaseX(), getY(), milliseconds);
			}else{
				String[] messages = divideZeile(message);
				myEbaTextSprite = new EbaTextSprite(message, messages, longestRow(messages, thinkingFont), thinkingFont, myGameFontManager, getBaseX(), getY(), milliseconds);
			}
		}
		if ((myEbaTextSprite.getWidth() + myEbaTextSprite.getX() + 10) > 800) {
			myEbaTextSprite.setX(790 - myEbaTextSprite.getWidth());
		}
		if (myEbaTextSprite.getX() < 10) {
			myEbaTextSprite.setX(10);
		}
		if(myEbaGameObject.myEbaGameEngine.textOn == true){
			return myEbaTextSprite;}

		else{
			if (!audioFile.exists()) {
				myEbaTextSprite.setActive(true);
				return myEbaTextSprite;
			}
			myEbaTextSprite.setActive(false);
			return myEbaTextSprite;
		}

	}


	public void shutUp() {
		if (status == ACTING || status == TALKING || status == THINKING ){
			if (myEbaTextSprite!=null){
				myEbaTextSprite.setActive(false);
			}
			myTimer.setActive(false);
			//myTimer2.setActive(false);
			myEbaGameObject.bsSound.stopAll();
			status = WAITING;
			talkingWithSound = false;
			setAnimationFrame(actingStartFrame[WAITING], actingEndFrame[WAITING]);
			getAnimationTimer().setDelay(anzeigedauer[WAITING]);

		}
	}



	public void walkTo(int x, int y, String standbyBlickrichtung){
		walkTo(x,y);
		myStandbyBlickrichtung = standbyBlickrichtung;
	}


	public void walkTo(int x, int y) {

		myStandbyBlickrichtung ="";

		walkToY = (int) (y - (super.getHeight() * ((y - baseYatZeroScale) / (baseYatFullScale - baseYatZeroScale))));
		walkToX = (int) (x - ((super.getWidth() / 2) * ((y - baseYatZeroScale) / (baseYatFullScale - baseYatZeroScale))));

		double deltaX = Math.abs(x - getBaseX());
		double deltaY = Math.abs(y - getBaseY());

		nachLinks = (x < getBaseX());
		nachOben = (y < getBaseY());

		if (nachLinks) {
			if (nachOben) {
				// nach links oben bzw. links hinten
				blickRichtung("n");

			} else {
				// nach links unten bzw. links vorn
				blickRichtung("w");

			}
		} else {
			if (nachOben) {
				// nach rechts oben bzw. rechts hinten
				blickRichtung("o");

			} else {
				// nach rechts unten bzw. rechts vorn
				blickRichtung("s");

			}
		}

		if (deltaX > deltaY) {
			if (nachLinks) {
				// nach links
				startFrame = linksStart[0];
				endeFrame = linksEnde[0];

			} else {
				// nach rechts
				startFrame = rechtsStart[0];
				endeFrame = rechtsEnde[0];

			}
		} else {
			if (nachOben) {
				// nach oben bzw. hinten
				startFrame = hintenStart[0];
				endeFrame = hintenEnde[0];

			} else {
				// nach unten bzw. vorn
				startFrame = vorneStart[0];
				endeFrame = vorneEnde[0];

			}
		}

		if ((startFrame != startFrameAlt) || (endeFrame != endeFrameAlt)) {
			setAnimationFrame(startFrame, endeFrame);
			getAnimationTimer().setDelay(anzeigedauer[WALKING]);
			startFrameAlt = startFrame;
			endeFrameAlt = endeFrame;
		}

		cSpeed = ((hSpeed * deltaX) + (vSpeed * deltaY)) / (deltaX + deltaY);
		status = WALKING;
	}

	public void warten(int miliseconds) {
		status = ACTING;
		myTimer = new Timer(miliseconds);
		myTimer.setActive(true);
	}

	public boolean isTalking() {
		return (status == TALKING);
	}

	public boolean isThinking() {
		return (status == THINKING);
	}

	public boolean isWalking() {
		return (status == WALKING);
	}

	public boolean isWaiting() {
		return (status == WAITING);
	}

	public boolean isActing() {
		return (status == ACTING);
	}

	public double getBaseX() {
		return getX() + (renderMe.getWidth() / 2);
	}

	public double getBaseY() {
		return getY() + renderMe.getHeight();
	}

	public double getBaseY(double x) {
		return getY() + renderMe.getHeight();
	}

	public boolean hasInteraction() {
		return (getDataID() != null);
	}

	public void anhalten() {
		walkToPoints.clear();
		walkTo((int)getBaseX(),(int)getBaseY());
		isUnterbrochen = true;
	}

	public boolean isUnterbrochen(){
		return isUnterbrochen;
	}

	// ********************************
	// ******* PRIVATE METHODEN *******
	// ********************************

	private void reScale() {

		// Workaraund:
		// getY() gibt sporadisch NaN zurück!
		// TODO: ?
		if (((Double)getY()).toString().equals("NaN")) {
			setX(x);
			setY(y);
			walkToX = (int)x;
			walkToY = (int)y;
		} else {
			x = getX();
			y = getY();
		}

		scaleFactor = Math.abs((y - baseYatZeroScale) / scaleFactorDivisor);
		if (scaleFactor < 0.001) {
			scaleFactor = 0.001;
		}

		renderMe = new BufferedImage((int) Math.ceil(width * scaleFactor), (int) Math.ceil(height * scaleFactor), BufferedImage.TYPE_INT_ARGB);
		Graphics2D myGraphics2D = renderMe.createGraphics();
		myGraphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		myGraphics2D.scale(scaleFactor, scaleFactor);
		myGraphics2D.drawImage(super.getImage(), null, 0, 0);
	}

	// ****************************************
	// ******* ÜBERSCHREIBENDE METHODEN *******
	// ****************************************

	public void update(long elapsedTime) {
		super.update(elapsedTime);
		isUnterbrochen = false;

		if (status == WALKING) {
			if (moveTo(elapsedTime, walkToX, walkToY, cSpeed)) {
				if (walkToPoints.empty()) {
					lookAt(myStandbyBlickrichtung);
				} else {
					nextWalkToPoint = (int[]) walkToPoints.pop();
					walkTo(nextWalkToPoint[0], nextWalkToPoint[1], myStandbyBlickrichtung);
				}
			}
		}
		if (status == TALKING) {

			if (talkingWithSound) {

				BaseAudioRenderer myBaseAudioRenderer = myEbaGameObject.myEbaGameEngine.bsSound.getAudioRenderer(myAudioPfad);
				if( (myBaseAudioRenderer == null) || (myBaseAudioRenderer.getStatus() != 1)){
					
					myEbaTextSprite.setImmutable(false);
					myEbaTextSprite.setActive(false);
					status = WAITING;
					setAnimationFrame(actingStartFrame[WAITING], actingEndFrame[WAITING]);
					getAnimationTimer().setDelay(anzeigedauer[WAITING]);
					talkingWithSound = false;
				}
			}
			else if(System.currentTimeMillis() > tempMilliseconds) {

				status = WAITING;
				setAnimationFrame(actingStartFrame[WAITING], actingEndFrame[WAITING]);
				getAnimationTimer().setDelay(anzeigedauer[WAITING]);
			}
		}


		if (status == THINKING) {
				if (talkingWithSound) {
					BaseAudioRenderer myBaseAudioRenderer = myEbaGameObject.myEbaGameEngine.bsSound.getAudioRenderer(myAudioPfad);
					if( (myBaseAudioRenderer == null) || (myBaseAudioRenderer.getStatus() != 1)){

					myEbaTextSprite.setImmutable(false);
					myEbaTextSprite.setActive(false);

					status = WAITING;
					setAnimationFrame(actingStartFrame[WAITING], actingEndFrame[WAITING]);
					getAnimationTimer().setDelay(anzeigedauer[WAITING]);
					talkingWithSound = false;
					}
				}
				else if	(System.currentTimeMillis() > tempMilliseconds) {

				status = WAITING;
				setAnimationFrame(actingStartFrame[WAITING], actingEndFrame[WAITING]);
				getAnimationTimer().setDelay(anzeigedauer[WAITING]);
			}
		}

		if (myTimer.isActive() && myTimer.action(elapsedTime)) {
			//if(!myTimer2.isActive()){
				setAnimationFrame(actingStartFrame[WAITING], actingEndFrame[WAITING]);
				getAnimationTimer().setDelay(anzeigedauer[WAITING]);
			//}
			status = WAITING;
			myTimer.setActive(false);
		}

//		if (myTimer2.isActive() && myTimer2.action(elapsedTime)) {
//			//if(!myTimer.isActive()){
//				setAnimationFrame(actingStartFrame[WAITING], actingEndFrame[WAITING]);
//				getAnimationTimer().setDelay(anzeigedauer[WAITING]);
//			//}
//			status = WAITING;
//			myTimer2.setActive(false);
//		}

		reScale();
	}

	public void render(Graphics2D g, int x, int y) {
		g.drawImage(renderMe, null, x, y);
		// g.drawRect(x, y, getWidth(), getHeight()); // nur zum Debuggen
	}

	public int getWidth() {
		return renderMe.getWidth();
	}

	public int getHeight() {
		return renderMe.getHeight();
	}

	public BufferedImage getImage() {
		return renderMe;
	}

	private void blickRichtung(String myCharakterBlickrichtung){
		if (myCharakterBlickrichtung.equals("w")) {
			for (int indexAktionsmodus=1; indexAktionsmodus <= i; indexAktionsmodus++) {
				actingStartFrame[indexAktionsmodus] = linksStart[indexAktionsmodus];
				actingEndFrame[indexAktionsmodus] = linksEnde[indexAktionsmodus];
			}
			myCharacterBlickrichtung = "w";

		} else if (myCharakterBlickrichtung.equals("s")) {
			for (int indexAktionsmodus=1; indexAktionsmodus <= i; indexAktionsmodus++) {
				actingStartFrame[indexAktionsmodus] = vorneStart[indexAktionsmodus];
				actingEndFrame[indexAktionsmodus] = vorneEnde[indexAktionsmodus];
			}
			myCharacterBlickrichtung = "s";

		} else if (myCharakterBlickrichtung.equals("n")) {
			for (int indexAktionsmodus=1; indexAktionsmodus <= i; indexAktionsmodus++) {
				actingStartFrame[indexAktionsmodus] = hintenStart[indexAktionsmodus];
				actingEndFrame[indexAktionsmodus] = hintenEnde[indexAktionsmodus];
			}
			myCharacterBlickrichtung = "n";

		} else {
			for (int indexAktionsmodus=1; indexAktionsmodus <= i; indexAktionsmodus++) {
				actingStartFrame[indexAktionsmodus] = rechtsStart[indexAktionsmodus];
				actingEndFrame[indexAktionsmodus] = rechtsEnde[indexAktionsmodus];
			}
			myCharacterBlickrichtung = "o";

		}
	}

	public void initialise(EbaGameObject myInitGameObject, int myBaseYatZeroScale, int myBaseYatFullScale, double myHspeed, double myVspeed, String myInitCharakterblickrichtung) {
		myTimer.setActive(false);
		//myTimer2.setActive(false);
		myEbaGameObject = myInitGameObject;
		baseYatZeroScale = myBaseYatZeroScale;
		baseYatFullScale = myBaseYatFullScale;

		status = WAITING;

//		hSpeed = myResultSet.getDouble("hspeed");
//		vSpeed = myResultSet.getDouble("vspeed");
		hSpeed = myHspeed;
		vSpeed = myVspeed;

		blickRichtung(myInitCharakterblickrichtung);

		talkingFont = myEbaGameObject.myTalkFont;
		thinkingFont = myEbaGameObject.myThinkFont;
		myGameFontManager = myEbaGameObject.fontManager;

		walkToPoints = myEbaGameObject.myEbaWalkableAreaMap;

		setAnimationFrame(actingStartFrame[WAITING], actingEndFrame[WAITING]);
		getAnimationTimer().setDelay(anzeigedauer[WAITING]);

		setLoopAnim(true);
		setAnimate(true);


		setX(myEbaGameObject.myEbaGameEngine.walkInPointX - ((super.getWidth() / 2) * ((myEbaGameObject.myEbaGameEngine.walkInPointY - baseYatZeroScale) / (baseYatFullScale - baseYatZeroScale))));
		setY(myEbaGameObject.myEbaGameEngine.walkInPointY - (super.getHeight() * ((myEbaGameObject.myEbaGameEngine.walkInPointY - baseYatZeroScale) / (baseYatFullScale - baseYatZeroScale))));

		scaleFactorDivisor = baseYatFullScale - baseYatZeroScale - super.getHeight();
		reScale();
	}

	public int getMyRoomNumber() {
		return myRoomNumber;
	}

	public void setMyRoomNumber(int myRoomNumber) {
		this.myRoomNumber = myRoomNumber;
	}

	public String getBlickrichtung() {
		return myCharacterBlickrichtung;
	}

	public double getBaseYatZeroScale() {
		return baseYatZeroScale;
	}

	public double getBaseYatFullScale() {
		return baseYatFullScale;
	}
	
	public String[] divideZeile(String text) {
		
		final int MAXZEICHENZAHL = 40; 
		final int MAXZEILENZAHL = 20;
		
		
		boolean keinWortMehrDa = false;
		
//		System.out.println("Text hat "+text.length()+" Zeichen. Dividing into "+zeilenZahl+" parts...");
//		
		int erstesFeldDieserZeile = 0;
		int aktuelleZeile = 0;
		
		String[] Zeilen = new String[MAXZEILENZAHL];
		Zeilen[aktuelleZeile] = text;
		
		int i;
		int erstesFeldDerNaechstenZeile;
		int letztesFeldDieserZeile;
		
		while( Zeilen[aktuelleZeile].length()> MAXZEICHENZAHL && !keinWortMehrDa ){
			
			i = 0;
//			System.out.println("Die Zeilenlänge "+Zeilen[aktuelleZeile].length()+" ist größer als "+MAXZEICHENZAHL);
//			System.out.println("Suche den Zeilenanfang nach Leerzeichen ab: ");
			while (Zeilen[aktuelleZeile].substring(i, i+1).equals(" ") || i == Zeilen[aktuelleZeile].length()){
				i++;
//				System.out.println("Leerzeichen am Zeilenanfang gefunden!");
			}
			erstesFeldDieserZeile = i;
//			System.out.println("Zeile beginnt bei: "+i);
//			System.out.println();
			
			i = MAXZEICHENZAHL;
			erstesFeldDerNaechstenZeile = 0;
			letztesFeldDieserZeile = 0;
			
//			System.out.println("Suche das werdende Zeilenende nach Zeichen ab: ");
			while((!Zeilen[aktuelleZeile].substring(i, i+1).equals(" ")) && (i>0)){
//				System.out.println(i+": '"+Zeilen[aktuelleZeile].substring(i, i+1)+"'");
				erstesFeldDerNaechstenZeile = i+1;
				i = i-1;
			}
//			System.out.print(i+": '"+Zeilen[aktuelleZeile].substring(i, i+1)+"'");
			
			if (i==0){
				Zeilen[aktuelleZeile]= Zeilen[aktuelleZeile].substring(erstesFeldDieserZeile,Zeilen[aktuelleZeile].length());
//				System.out.println(" --- Bis zum Zeilenanfang durchmarschiert!");
//				System.out.println("Speichere den String '"+Zeilen[aktuelleZeile].substring(erstesFeldDerNaechstenZeile,Zeilen[aktuelleZeile].length())+"' in Zeile Nummer:"+(aktuelleZeile));
//				System.out.println();
//				System.out.println("----------------------------------------------");
//				System.out.println("----------------------------------------------");
//				System.out.println("Suche nach dem Ende des überlangen Wortes. Zeilenende bei:"+Zeilen[aktuelleZeile].length());
//				
				i = MAXZEICHENZAHL;
				while((!Zeilen[aktuelleZeile].substring(i, i+1).equals(" ")) && (i<(Zeilen[aktuelleZeile].length()-1))){
				    
//					System.out.println(i+": '"+Zeilen[aktuelleZeile].substring(i, i+1)+"'");
				    
				    letztesFeldDieserZeile = i;
					i = i+1;
				}
				
//				System.out.println("Letztes Feld dieser Zeile:"+letztesFeldDieserZeile);
//				System.out.println("Zeichen im letzten Feld dieser Zeile:" +Zeilen[aktuelleZeile].substring(letztesFeldDieserZeile, letztesFeldDieserZeile+1));
//				
				if(Zeilen[aktuelleZeile].substring(i, i+1).equals(" ")){	
//					System.out.println(" --- Leeres Feld gefunden!");
					
					while((Zeilen[aktuelleZeile].substring(i, i+1).equals(" ")) && (i<Zeilen[aktuelleZeile].length()-1)){
//						System.out.println(i+": '"+Zeilen[aktuelleZeile].substring(i, i+1)+"'");
						i = i+1;
					}
					if(!(Zeilen[aktuelleZeile].substring(i, i+1).equals(" "))){
//						System.out.println("Nächstes Wort gefunden!");
						erstesFeldDerNaechstenZeile = i+1;
						
//						System.out.println("aktuelle Zeile: "+ aktuelleZeile);
//						System.out.println("Zeilen insgesamt: "+ MAXZEILENZAHL);
//						System.out.println("Erstes Feld der nächsten Zeile: "+ erstesFeldDerNaechstenZeile);
//						System.out.println("Erstes Feld dieser Zeile: "+ erstesFeldDieserZeile);
//						System.out.println("Letztes Feld dieser Zeile: "+ letztesFeldDieserZeile);
//						System.out.println("Zeilenlänge: "+ Zeilen[aktuelleZeile].length());
//						
						Zeilen[aktuelleZeile+1]= Zeilen[aktuelleZeile].substring(erstesFeldDerNaechstenZeile-1,Zeilen[aktuelleZeile].length());
						Zeilen[aktuelleZeile]= Zeilen[aktuelleZeile].substring(erstesFeldDieserZeile,letztesFeldDieserZeile+1);
						

						
					}else{
//						System.out.println("Kein Wort mehr da!");
						keinWortMehrDa = true;
					}
				}else{
//					System.out.println("Überlanges Wort geht bis zum Zeilenende --- ");
					keinWortMehrDa = true;
				}
			} else {
//				System.out.println(" --- Erstes Leerzeichen gefunden!");
				erstesFeldDerNaechstenZeile = i+1;
//				System.out.println("Speichere den String '"+Zeilen[aktuelleZeile].substring(erstesFeldDerNaechstenZeile,Zeilen[aktuelleZeile].length())+"' in Zeile Nummer:"+(aktuelleZeile+1));
				Zeilen[aktuelleZeile+1]= Zeilen[aktuelleZeile].substring(erstesFeldDerNaechstenZeile,Zeilen[aktuelleZeile].length());
				i = i-1;
				
				//System.out.println("Suche ab Position "+i+" nach weiteren Leerzeichen.");
				boolean zeilenanfang = true;
				while(Zeilen[aktuelleZeile].substring(i, i+1).equals(" ") && zeilenanfang){
					//System.out.println(i+": '"+Zeilen[aktuelleZeile].substring(i, i+1)+"' --- Noch ein Leerzeichen gefunden!");
					i = i-1;
					if(i == 0){
						zeilenanfang = false;
//						System.out.println("Leer bis Zeilenanfang!");
						i = 1;
					}
				}
				letztesFeldDieserZeile = i+1;
				
//				System.out.println("Keine weiteren Leerzeichen mehr gefunden.");
//				System.out.println("Das letzte Feld dieser Zeile lautet also:"+(letztesFeldDieserZeile-1)+". Es enthält das Zeichen: '"+Zeilen[aktuelleZeile].substring(i, i+1)+"'");
				
				if (!zeilenanfang){
					Zeilen[aktuelleZeile+1]= Zeilen[aktuelleZeile].substring(erstesFeldDerNaechstenZeile-1,Zeilen[aktuelleZeile].length());
//					System.out.println(" --- Zeilenanfang gefunden!");
				} else {
					
//					System.out.println("Speichere den String '"+Zeilen[aktuelleZeile].substring(erstesFeldDieserZeile,letztesFeldDieserZeile)+"' in Zeile Nummer:"+(aktuelleZeile));
					Zeilen[aktuelleZeile]= Zeilen[aktuelleZeile].substring(erstesFeldDieserZeile,letztesFeldDieserZeile);
					
				}
			}
//			System.out.println("Suche weiter in der nächsten Zeile.");
			
			if (!keinWortMehrDa){
				if (Zeilen[aktuelleZeile+1].length()==0){
					keinWortMehrDa= true;
				} else if (wordCount(Zeilen[aktuelleZeile+1])==0){
					keinWortMehrDa= true;
				} else {
					aktuelleZeile = aktuelleZeile+1;
//					System.out.println("Zeilenumbruch!");
				}
			}
						
		}
		
		int echteZeilenZahl = MAXZEILENZAHL;
		
		for(int m = 0; m < MAXZEILENZAHL; m++){
			//System.out.println("In  Zeile "+m+" steht: '"+Zeilen[m]+"'");
			
			
			if(Zeilen[m]==null){
				echteZeilenZahl = echteZeilenZahl-1;
				Zeilen[m]="XXXX";
			}
			
		}
		String[] echteZeilen = new String[echteZeilenZahl];
		
		for(int m = 0; m < echteZeilenZahl; m++){
			echteZeilen[m] = Zeilen[m];
		}
		
		return echteZeilen;
		
	}

	public int wordCount(String text) {
		int wordCount=0;
		boolean lastCharWasSpace = true;
		for (int i = 1; i < text.length(); i++) {
			if (lastCharWasSpace){
				if (!text.substring(i, i+1).equals(" ")){
					wordCount = wordCount+1;
					lastCharWasSpace = false;
				}
			} else {
				if (text.substring(i, i+1).equals(" ")){	
					lastCharWasSpace = true;
				}			
			}
		}
		return wordCount;
	}
	
	public int longestRow(String[] zeilen,  String font) {
		int longestRow=0;
		

		//System.out.println(longestRow+": "+zeilen[longestRow]);
		for (int i = 1; i < zeilen.length; i++) {
			if (myGameFontManager.getFont("F" + font).getWidth(zeilen[i]) > myGameFontManager.getFont("F" + font).getWidth(zeilen[longestRow])){
				longestRow = i;
				//System.out.println("<<<<<<<<<< neue längste Reihe");
			}
			//System.out.println(i+": "+zeilen[i]);
		}
		//System.out.println();
		//System.out.println("Längste Reihe: "+longestRow+" --- "+zeilen[longestRow]);
		return longestRow;
	}
	
}
