package de.daedalic.eba;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.GameEngine;
import com.golden.gamedev.GameObject;
import com.golden.gamedev.engine.BaseAudio;
import com.golden.gamedev.engine.BaseIO;
import com.golden.gamedev.engine.BaseLoader;
import com.golden.gamedev.engine.audio.JOrbisOggRenderer;
import com.golden.gamedev.engine.audio.WaveRenderer;
import com.golden.gamedev.engine.input.AWTInput;
import com.golden.gamedev.engine.timer.SystemTimer;
import com.golden.gamedev.object.Background;
import com.golden.gamedev.object.GameFontManager;
import com.golden.gamedev.object.font.SystemFont;

public class EbaGameEngine extends GameEngine {

	// Datenbank-Eigenschaften:
    private Statement myStatement = null;
    private ResultSet myResultSet = null;
    public static Connection myConnection = null;
    String dbUser = "sa";
    String dbPassword = "";

    String dbPath = "jdbc:hsqldb:file:EbaData/";

    String guiKlasse = null;
    int walkInPointX = 260;
    int walkInPointY = 520;
    String charakterBlickrichtung = "w";
    int myRoomNumber =0;

    public boolean skriptAktiv=false;
    public int skriptID;
    public int zeilennummer;

    public EbaPlayerSprite myTransRoomCharacterSprite=null;
    public String mySaveActiveFont;
    public String mySaveInactiveFont;
    public String mySaveTalkFont;
    public String mySaveThinkFont;
    public int mySaveGuiID=-99999;

	public Sprite mySaveStandardCursorSprite;
	public Sprite mySaveActiveCursorSprite;
	public Sprite mySaveExitNCursorSprite;
	public Sprite mySaveTalkToEdnaCursorSprite;
	public float xPos = 0.05f;
//	public boolean talkingWithSound =false ;
	public int textPos = 75;
	public boolean musicOn = true;//Schalter für Musik an oder aus
	public boolean soundOn = true;//Schalter für Sound an oder aus
	public boolean textOn = true; // Schalter für Text an oder aus

	public boolean isInventoryLocked = true; // Schalter für das Inventar-Schloss an oder aus

    public EbaGameEngine() {
    	{ distribute = true; }
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			myConnection = DriverManager.getConnection(dbPath, dbUser,
					dbPassword);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public GameObject getGame(int nextRoomNumber) {
		myRoomNumber = nextRoomNumber;
		if (nextRoomNumber == 0) {
			// TODO Hier alle Fonts laden:
			Color myEdnaColor = new Color(208, 141, 230);
			Color myHarveyColor = new Color(109, 213, 63);
			Color myColorRed = new Color(190, 111, 87);
			Color myInactiveColor = new Color(136, 136, 136);
			Color myActiveColor = new Color(243, 239, 171);
			Color myColorYellow = new Color(228, 192, 82);
			Color myColorYellow2 = new Color(232, 227, 143);
			Color myColorOrange = new Color(255, 175, 103);
			Color myColorGreygreen = new Color(164, 186, 120);
			Color myColorBlue = new Color(137, 205, 222);
			Color myColorGrey = new Color(205, 204, 196);
			Color myColorLind = new Color(156, 210, 152);
			Color myColorStahlblau = new Color(144, 147, 196);

			prepareFont("EdnaFont", "DejaVuSans.ttf", 18, myEdnaColor, Color.BLACK);
			prepareFont("HarveyFont", "DejaVuSans.ttf", 18, myHarveyColor, Color.BLACK);

			prepareFont("NscFontRot", "DejaVuSans.ttf", 18, myColorRed, Color.BLACK);
			prepareFont("NscFontGelb", "DejaVuSans.ttf", 18, myColorYellow, Color.BLACK);
			prepareFont("NscFontOrange", "DejaVuSans.ttf", 18, myColorOrange, Color.BLACK);
			prepareFont("NscFontGreygreen", "DejaVuSans.ttf", 18, myColorGreygreen, Color.BLACK);
			prepareFont("NscFontBlau", "DejaVuSans.ttf", 18, myColorBlue, Color.BLACK);
			prepareFont("NscFontGrau", "DejaVuSans.ttf", 18, myColorGrey, Color.BLACK);
			prepareFont("NscFontHellgelb", "DejaVuSans.ttf", 18, myColorYellow2, Color.BLACK);
			prepareFont("NscFontLind", "DejaVuSans.ttf", 18, myColorLind, Color.BLACK);
			prepareFont("NscFontStahlblau", "DejaVuSans.ttf", 18, myColorStahlblau, Color.BLACK);
			prepareFont("NscFontWeiss", "DejaVuSans.ttf", 18, Color.WHITE, Color.BLACK);

			prepareFont("TestFont", "DejaVuSans.ttf", 18, Color.YELLOW, Color.BLACK);

			prepareFont("ActiveFont", "DejaVuSans.ttf", 18, myActiveColor, Color.BLACK);
			prepareFont("InactiveFont", "DejaVuSans.ttf", 18, myInactiveColor, Color.BLACK);

			prepareFont("MenueFont", "DejaVuSans.ttf", 14, Color.WHITE, Color.BLACK);

            //bsSound.setLoop(true);
		}


        try {
            myStatement = myConnection.createStatement();
            myResultSet = myStatement.executeQuery("SELECT * from RAUM,GUI where Raum.ID =" + nextRoomNumber +" AND GUI.ID = Raum.GUIID");

            while (myResultSet.next()) {
                guiKlasse = myResultSet.getString("JavaKlasse");
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

            if (myStatement != null) {
                try {
                    myStatement.close();
                } catch (SQLException e) {
                	e.printStackTrace();
                }
                myStatement = null;
            }
        }

		System.gc();
		EbaGameObject myEbaGameObject = new EbaGameObject(this);

		try {
			myEbaGameObject = (EbaGameObject)Class.forName(guiKlasse).getConstructor(EbaGameEngine.class).newInstance(this);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return myEbaGameObject;
	}

	private void prepareFont(String name, String fontFile, int size, Color color, Color strokeColor) {
		try {
			Font myFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontFile));
			fontManager.putFont("F" + name, new SystemFont(myFont.deriveFont(Font.PLAIN, size), color));
			fontManager.putFont("S" + name, new SystemFont(myFont.deriveFont(Font.PLAIN, size), strokeColor));
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getMyRoomNumber() {
		return myRoomNumber;
	}

	public void refreshConnection() {
		try {
			myStatement = myConnection.createStatement();
			myStatement.executeQuery("SHUTDOWN");
			myConnection = null;
		    String dbUser = "sa";
		    String dbPassword = "";
		    String dbPath = "jdbc:hsqldb:file:EbaData/";
			Class.forName("org.hsqldb.jdbcDriver");
			myConnection = DriverManager.getConnection(dbPath, dbUser,
					dbPassword);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    protected void initEngine()
    {
    	if(bsTimer == null)
        {
            bsTimer = new SystemTimer();
        }
        if(bsIO == null)
        {
            bsIO = new BaseIO(getClass());
        }
        if(bsLoader == null)
        {
            bsLoader = new BaseLoader(bsIO, Color.MAGENTA);
        }
        if(bsInput == null)
        {
            bsInput = new AWTInput(bsGraphics.getComponent());
        }
        if(bsMusic == null)
        {
        	//bsMusic = new BaseAudio(bsIO, new WaveRenderer());
        	bsMusic = new BaseAudio(bsIO, new JOrbisOggRenderer());
            bsMusic.setExclusive(true);
            bsMusic.setLoop(true);
        }
        if(bsSound == null)
        {
        	//bsSound = new BaseAudio(bsIO, new JOrbisOggRenderer());
            bsSound = new BaseAudio(bsIO, new WaveRenderer());
            bsSound.setExclusive(true);
        }
        if(fontManager == null)
        {
            fontManager = new GameFontManager();
        }
        bsTimer.setFPS(100);
        Background.screen = bsGraphics.getSize();
    }

	public void setMyTransRoomCharacterSprite(EbaPlayerSprite myTempTransRoomCharacterSprite) {
		myTransRoomCharacterSprite = myTempTransRoomCharacterSprite;
	}

	public EbaPlayerSprite getMyTransRoomCharacterSprite() {
		return myTransRoomCharacterSprite;
	}

}
