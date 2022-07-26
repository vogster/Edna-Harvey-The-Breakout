package de.daedalic.eba;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.golden.gamedev.object.Sprite;


public class EbaScriptInterpreter {
	final static String audioFileSuffix = ".wav";

    EbaPlayerSprite myPlayerSprite;
    EbaNscSprite myNscSprite;
    EbaSpriteGroup myBackgroundSprites;
    EbaSpriteGroup myObjectSprites;
    EbaSpriteGroup myTextSprites;
    EbaSpriteGroup myGuiSprites;
    EbaChoiceList myChoiceList;
    int myScriptID;
    EbaGameEngine myEbaGameEngine;
    EbaGameObject myEbaGameObject;
    String myZeile = "";
    boolean skriptLaeuft = false;
    boolean zeileLaeuft = false;
    boolean parallelPerformance = false;

    // Datenbankeigenschaften
    private Statement myStatement = null;
    private ResultSet myResultSet = null;
	private ResultSet myResultSet2 = null;
	private boolean nochnichtRendern;
	private boolean exit;
	private int nextRoom;
	private int walkInPointX;
	private int walkInPointY;
	private String charakterBlickrichtung;
	public String myAudioPfad;
	private EbaTextSprite saySound;
	public boolean isSayingSound = false;


    public EbaScriptInterpreter(EbaPlayerSprite CharacterSprite, EbaSpriteGroup BackgroundSprites, EbaSpriteGroup ObjectSprites, EbaSpriteGroup TextSprites, EbaSpriteGroup GuiSprites, EbaChoiceList ChoiceList, EbaGameEngine initEbaGameEngine, EbaGameObject initEbaGameObject) {

        myPlayerSprite = CharacterSprite;
        myBackgroundSprites = BackgroundSprites;
        myObjectSprites = ObjectSprites;
        myTextSprites = TextSprites;
        myGuiSprites = GuiSprites;
        myChoiceList = ChoiceList;
        myEbaGameEngine = initEbaGameEngine;
        myEbaGameObject = initEbaGameObject;


        myNscSprite = myEbaGameObject.myEbaNscSprite;

        if (skriptLaeuft = false) {
            myPlayerSprite.status = 0;
        }
        if (myEbaGameEngine.skriptAktiv == true) {
            skriptLaeuft = true;
        }

    }

    public void runScript(int scriptID, int initN) {

    	if (exit){

            myEbaGameEngine.walkInPointX = walkInPointX;
            myEbaGameEngine.walkInPointY = walkInPointY;
            myEbaGameEngine.charakterBlickrichtung = charakterBlickrichtung;

            myEbaGameEngine.skriptAktiv = true;
            myEbaGameEngine.skriptID = scriptID;
            myEbaGameEngine.nextGameID = nextRoom;

            myEbaGameObject.finish();

    	}

        skriptLaeuft = true;
        zeileLaeuft = true;

        myEbaGameObject.myCursorSprite.setActive(false);

        myScriptID = scriptID;
        int n = initN;

        do{
        	nochnichtRendern = false;

        if (myScriptID < 0) {
            defaultExit(myScriptID * (-1));
        } else {

            String myZeile = getSkriptZeile(n);
            if ((myZeile == "") && (n ==1)){
               	myZeile = "say(Я не знаю, что делать.)";
            }

            //System.out.println("Skript: "+myScriptID+" | Zeile: "+myZeile);

            // -------- IFACTIVE -----------------------------------------

            if (myZeile.startsWith("ifactive(")) {
            	try {
                	myStatement = EbaGameEngine.myConnection.createStatement();
                	myResultSet = myStatement.executeQuery("SELECT * FROM Raumobjekt WHERE ID = "+Integer.parseInt(myZeile.substring(9, 17)));
                	myResultSet.next();

                	if(myResultSet.getBoolean("AKTIV")){
                		myZeile=myZeile.substring(19,myZeile.length());
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

            // -------- CHANGEPC    -----------------------------------------

            if (myZeile.startsWith("changepc(")) {
            	try {
                	myStatement = EbaGameEngine.myConnection.createStatement();
                	myResultSet = myStatement.executeQuery("SELECT * FROM Characteranimationset WHERE ID = "+Integer.parseInt(myZeile.substring(9, 15)));
                	myResultSet.next();

                	myStatement.executeUpdate("UPDATE Raum SET CharacteranimationsetID = "+Integer.parseInt(myZeile.substring(9, 15))+" WHERE ID=" + myEbaGameEngine.myRoomNumber );

                	myObjectSprites.remove(myPlayerSprite);
                	//EbaPlayerSprite myNewCharacterSprite = new EbaPlayerSprite(myEbaGameObject.getRoomNumber(), myEbaGameObject,myPlayerSprite.getBlickrichtung());
                	//myEbaGameObject.myCharacterSprite = myNewCharacterSprite;

                	myPlayerSprite = new EbaPlayerSprite(myEbaGameObject.getRoomNumber(), myEbaGameObject,myPlayerSprite.getBlickrichtung());
                	myObjectSprites.add(myPlayerSprite);
                	myPlayerSprite.initialise(myEbaGameObject, (int)myPlayerSprite.getBaseYatZeroScale(), (int)myPlayerSprite.getBaseYatFullScale(), myPlayerSprite.getHorizontalSpeed(),myPlayerSprite.getVerticalSpeed(),myPlayerSprite.getBlickrichtung());
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

            // -------- IFITEMACTIVE -----------------------------------------

            if (myZeile.startsWith("ifitemactive(")) {
            	try {
                	myStatement = EbaGameEngine.myConnection.createStatement();
                	myResultSet = myStatement.executeQuery("SELECT * FROM Inventarobjekt WHERE ID = "+Integer.parseInt(myZeile.substring(13, 17)));
                	myResultSet.next();

                	if(myResultSet.getInt("Inventarposition")>0){
                		myZeile=myZeile.substring(19,myZeile.length());
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


            // --------SAY----------------------------
            if (myZeile.startsWith("say(")) {
            	myAudioPfad = "audio/"+myScriptID+"-"+n+ audioFileSuffix;
                myTextSprites.add(myPlayerSprite.say(myZeile.substring(4, myZeile.length() - 1),myAudioPfad));

            }

            // --------THINK---------------------------
            if (myZeile.startsWith("think(")) {
            	myAudioPfad = "audio/"+myScriptID+"-"+n+ audioFileSuffix;
                myTextSprites.add(myPlayerSprite.think(myZeile.substring(6, myZeile.length() - 1),myAudioPfad));
            }

            // --------SAYNSC----------------------------

            if (myZeile.startsWith("saynsc(")) {
            	myAudioPfad = "audio/"+myScriptID+"-"+n+ audioFileSuffix;
            	myNscSprite = (EbaNscSprite)myObjectSprites.getSprite(Integer.parseInt(myZeile.substring(7,15)));
                if (myNscSprite == null){
                	myNscSprite = (EbaNscSprite)myBackgroundSprites.getSprite(Integer.parseInt(myZeile.substring(7,15)));
                }
                if (myNscSprite == null){
                }else{
                	myTextSprites.add(myNscSprite.say(myZeile.substring(16, myZeile.length() - 1),myAudioPfad));
                }
            }


            // --------SAYSOUND----------------------------

            //---------Entweder: saysound(x,y,Message) mit x und y jeweils dreistellig
            //------------------->>Gibt einen Bildschirmtext an (x/y) im InactiveFont aus
            //---------Oder    : saysound(char,Message)
            //------------------->>Gibt einen Bildschirmtext an der Position des Spielercharacters aus

            if (myZeile.startsWith("saysound(")) {
            	myAudioPfad = "audio/" + myScriptID + "-" + n + audioFileSuffix;
            	int x;
            	int y;
            	String message;
            	int milliseconds;
            	if (myZeile.startsWith("saysound(char,")) {
            		x=(int)(myPlayerSprite.getBaseX());
                	y=(int)(myPlayerSprite.getY() - 10);
                	message = myZeile.substring(14,myZeile.length() - 1);

                } else {
	            	x=Integer.parseInt(myZeile.substring(9,12));
	            	y=Integer.parseInt(myZeile.substring(13,16));
	            	message = myZeile.substring(17,myZeile.length() - 1);

                }
            	milliseconds = (int) message.length() * 75;
        		if (message.length() < 15) {
        			milliseconds = (int) (milliseconds * 2);
        		}

        		//
        		File audioFile = new File(myAudioPfad);

        		if (audioFile.exists()) {
        			myEbaGameObject.myEbaGameEngine.bsSound.play(myAudioPfad);
        			isSayingSound = true;
        			
        			if(message.length() < 40){
        				saySound = new EbaTextSprite(message,"InactiveFont",myEbaGameObject.fontManager,x,y,myAudioPfad, myEbaGameObject);
        			}else{
        				String[] messages = myPlayerSprite.divideZeile(message);
        				saySound = new EbaTextSprite(message, messages, myPlayerSprite.longestRow(messages, myEbaGameObject.myInactiveFont),"InactiveFont",myEbaGameObject.fontManager,x,y,myAudioPfad, myEbaGameObject);
        			}
        			
        			if ((saySound.getWidth() + saySound.getX() + 10) > 800) {
        				saySound.setX(790 - saySound.getWidth());
        			}
        			if (saySound.getX() < 10) {
        				saySound.setX(10);
        			}
        			

        			myTextSprites.add(saySound);

        		} else {
        			
        			if(message.length() < 40){
        				saySound = new EbaTextSprite(message,"InactiveFont",myEbaGameObject.fontManager,x,y,milliseconds);
        			}else{
        				String[] messages = myPlayerSprite.divideZeile(message);
        				saySound = new EbaTextSprite(message, messages, myPlayerSprite.longestRow(messages, myEbaGameObject.myInactiveFont), "InactiveFont",myEbaGameObject.fontManager,x,y,milliseconds);
        			}
        				
        			if ((saySound.getWidth() + saySound.getX() + 10) > 800) {
        				saySound.setX(790 - saySound.getWidth());
        			}
        			if (saySound.getX() < 10) {
        				saySound.setX(10);
        			}
        			
        			myPlayerSprite.warten(milliseconds);
        			myTextSprites.add(saySound);
        		}
        		isPerforming();
            }

            if (myZeile.startsWith("saysoundfile(")) {
            	
            	int x;
            	int y;
            	String message;
            	int milliseconds;
            	if (myZeile.startsWith("saysoundfile(char,")) {
            		
            		myAudioPfad = "audio/soundfx/" + myZeile.substring(18,26) + audioFileSuffix;
            		
            		x=(int)(myPlayerSprite.getBaseX());
                	y=(int)(myPlayerSprite.getY() - 10);
                	message = myZeile.substring(27,myZeile.length() - 1);

                } else {
                	
                	myAudioPfad = "audio/soundfx/" + myZeile.substring(21,29) + audioFileSuffix;
                	
	            	x=Integer.parseInt(myZeile.substring(13,16));
	            	y=Integer.parseInt(myZeile.substring(17,20));
	            	message = myZeile.substring(30,myZeile.length() - 1);

                }
            	milliseconds = (int) message.length() * 75;
        		if (message.length() < 15) {
        			milliseconds = (int) (milliseconds * 2);
        		}

        		//
        		File audioFile = new File(myAudioPfad);

        		if (audioFile.exists()) {
        			myEbaGameObject.myEbaGameEngine.bsSound.play(myAudioPfad);
        			isSayingSound = true;
        			
        			if(message.length() < 40){
        				saySound = new EbaTextSprite(message,"InactiveFont",myEbaGameObject.fontManager,x,y,myAudioPfad, myEbaGameObject);
        			}else{
        				String[] messages = myPlayerSprite.divideZeile(message);
        				saySound = new EbaTextSprite(message, messages, 0,"InactiveFont",myEbaGameObject.fontManager,x,y,myAudioPfad, myEbaGameObject);
        			}
        			
        			if ((saySound.getWidth() + saySound.getX() + 10) > 800) {
        				saySound.setX(790 - saySound.getWidth());
        			}
        			if (saySound.getX() < 10) {
        				saySound.setX(10);
        			}
        			
        			myTextSprites.add(saySound);

        		} else {
        			
        			if(message.length() < 40){
        				saySound = new EbaTextSprite(message,"InactiveFont",myEbaGameObject.fontManager,x,y,milliseconds);
        			}else{
        				String[] messages = myPlayerSprite.divideZeile(message);
        				saySound = new EbaTextSprite(message, messages, 0, "InactiveFont",myEbaGameObject.fontManager,x,y,milliseconds);
        			}
        			
        			if ((saySound.getWidth() + saySound.getX() + 10) > 800) {
        				saySound.setX(790 - saySound.getWidth());
        			}
        			if (saySound.getX() < 10) {
        				saySound.setX(10);
        			}
        				
        			myPlayerSprite.warten(milliseconds);
        			myTextSprites.add(saySound);
        		}
        		isPerforming();
            }
            
            
            if (myZeile.startsWith("saysoundp(")) {
            	myAudioPfad = "audio/" + myScriptID + "-" + n + audioFileSuffix;
            	int x;
            	int y;
            	String message;
            	int milliseconds;
            	if (myZeile.startsWith("saysoundp(char,")) {
            		x=(int)(myPlayerSprite.getBaseX());
                	y=(int)(myPlayerSprite.getY() - 10);
                	message = myZeile.substring(15,myZeile.length() - 1);
                } else {
	            	x=Integer.parseInt(myZeile.substring(10,13));
	            	y=Integer.parseInt(myZeile.substring(14,17));
	            	message = myZeile.substring(18,myZeile.length() - 1);
                }
            	milliseconds = (int) message.length() * 75;
        		if (message.length() < 15) {
        			milliseconds = (int) (milliseconds * 2);
        		}
        		File audioFile = new File(myAudioPfad);
        		if (audioFile.exists()) {
        			  
        			myEbaGameObject.myEbaGameEngine.bsSound.play(myAudioPfad);
        			isSayingSound = true;
        			
        			if(message.length() < 40){
        				saySound = new EbaTextSprite(message,"InactiveFont",myEbaGameObject.fontManager,x,y,myAudioPfad, myEbaGameObject);
        			}else{
        				String[] messages = myPlayerSprite.divideZeile(message);
        				saySound = new EbaTextSprite(message, messages, 0,"InactiveFont",myEbaGameObject.fontManager,x,y,myAudioPfad, myEbaGameObject);
        			}
        			
        			if ((saySound.getWidth() + saySound.getX() + 10) > 800) {
        				saySound.setX(790 - saySound.getWidth());
        			}
        			if (saySound.getX() < 10) {
        				saySound.setX(10);
        			}
        			
        			myTextSprites.add(saySound);
        			
        		}else {
        			
        			if(message.length() < 40){
        				saySound = new EbaTextSprite(message,"InactiveFont",myEbaGameObject.fontManager,x,y,milliseconds);
        			}else{
        				String[] messages = myPlayerSprite.divideZeile(message);
        				saySound = new EbaTextSprite(message, messages, 0,"InactiveFont",myEbaGameObject.fontManager,x,y,milliseconds);
        			}
        			
        			if ((saySound.getWidth() + saySound.getX() + 10) > 800) {
        				saySound.setX(790 - saySound.getWidth());
        			}
        			if (saySound.getX() < 10) {
        				saySound.setX(10);
        			}
        			
        			myPlayerSprite.warten(milliseconds);
        			myTextSprites.add(saySound);
        			
        			}
        		parallelPerformance=true;
            }

            // --------CHOICE------------------------------

            if (myZeile.startsWith("choice(")) {
            	myGuiSprites.setActive(false);
            	myChoiceList.invoke(Integer.parseInt(myZeile.substring(7,15)));
            	myChoiceList.setActive(true);

            }

            //--------ACTIVATECHOICE-----------------------
            if (myZeile.startsWith("activatechoice(")) {
            	myChoiceList.setAuswahlAktiv(Integer.parseInt(myZeile.substring(15,23)),Integer.parseInt(myZeile.substring(24,26)),Boolean.parseBoolean(myZeile.substring(27,myZeile.length() - 1)));
            	nochnichtRendern = true;
            }

            //--------CHANGEROISKRIPT-----------------------
            if (myZeile.startsWith("changeroiskript(")) {
            	boolean found = false;
            	Sprite myTmpSprite[] = new Sprite[myObjectSprites.getSize()];
                myTmpSprite = myObjectSprites.getSprites();
                for (int i = 0; i < myObjectSprites.getSize(); i++) {
                    if (myTmpSprite[i].getID() == Integer.parseInt(myZeile.substring(16, 24))) {
                        EbaInteractionSprite myTmpEbaInteractionSprite = (EbaInteractionSprite) myTmpSprite[i];
                    	found = true;
                        if(myZeile.substring(25,26).equals("a")){
                    		myTmpEbaInteractionSprite.setMyAnsehenSkript(Integer.parseInt(myZeile.substring(27,myZeile.length() - 1)));
                        }
                    	if(myZeile.substring(25,26).equals("b")){
                    		myTmpEbaInteractionSprite.setMyBenutzenSkript(Integer.parseInt(myZeile.substring(27,myZeile.length() - 1)));
                        }
                    	if(myZeile.substring(25,26).equals("n")){
                    		myTmpEbaInteractionSprite.setMyNehmenSkript(Integer.parseInt(myZeile.substring(27,myZeile.length() - 1)));
                        }
                    	if(myZeile.substring(25,26).equals("r")){
                    		myTmpEbaInteractionSprite.setMyRedenMitSkript(Integer.parseInt(myZeile.substring(27,myZeile.length() - 1)));
                        }
                    }
                }
                myTmpSprite = new Sprite[myBackgroundSprites.getSize()];

                myTmpSprite = myBackgroundSprites.getSprites();
                for (int i = 0; i < myBackgroundSprites.getSize(); i++) {
                	if (myTmpSprite[i].getID() == Integer.parseInt(myZeile.substring(16, 24))) {
                		found = true;
                        EbaInteractionSprite myTmpEbaInteractionSprite = (EbaInteractionSprite) myTmpSprite[i];
                    	if(myZeile.substring(25,26).equals("a")){
                    		myTmpEbaInteractionSprite.setMyAnsehenSkript(Integer.parseInt(myZeile.substring(27,myZeile.length() - 1)));
                        }
                    	if(myZeile.substring(25,26).equals("b")){
                    		myTmpEbaInteractionSprite.setMyBenutzenSkript(Integer.parseInt(myZeile.substring(27,myZeile.length() - 1)));
                        }
                    	if(myZeile.substring(25,26).equals("n")){
                    		myTmpEbaInteractionSprite.setMyNehmenSkript(Integer.parseInt(myZeile.substring(27,myZeile.length() - 1)));
                        }
                    	if(myZeile.substring(25,26).equals("r")){
                    		myTmpEbaInteractionSprite.setMyRedenMitSkript(Integer.parseInt(myZeile.substring(27,myZeile.length() - 1)));
                        }
                    }
                }

                // Der ScriptInterpreter muss den Datenbankzugriff selbst machen, wenn sich das
                // zu �ndernde Raumobjekt nicht im aktuellen Raum befindet.

                if (!found){
	                try {
	                    myStatement = EbaGameEngine.myConnection.createStatement();
	                    if (myZeile.substring(25,26).equals("a")){
	                    	myStatement.executeUpdate("UPDATE Raumobjektinteraktion SET AnsehenSkriptID= "+ Integer.parseInt(myZeile.substring(27,myZeile.length() - 1)) +" where raumobjektid="+ Integer.parseInt(myZeile.substring(16, 24)));
	                    } else if (myZeile.substring(25,26).equals("b")){
	                    	myStatement.executeUpdate("UPDATE Raumobjektinteraktion SET BenutzenSkriptID= "+ Integer.parseInt(myZeile.substring(27,myZeile.length() - 1)) +" where raumobjektid="+ Integer.parseInt(myZeile.substring(16, 24)));
	                    } else if (myZeile.substring(25,26).equals("n")){
	                    	myStatement.executeUpdate("UPDATE Raumobjektinteraktion SET NehmenSkriptID= "+ Integer.parseInt(myZeile.substring(27,myZeile.length() - 1)) +" where raumobjektid="+ Integer.parseInt(myZeile.substring(16, 24)));
	                    } else if (myZeile.substring(25,26).equals("r")){
	                    	myStatement.executeUpdate("UPDATE Raumobjektinteraktion SET RedenMitSkriptID= "+ Integer.parseInt(myZeile.substring(27,myZeile.length() - 1)) +" where raumobjektid="+ Integer.parseInt(myZeile.substring(16, 24)));
	                    }
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
                nochnichtRendern = true;
            }

            //--------CHANGEINVOSKRIPT-----------------------
            if (myZeile.startsWith("changeinvoskript(")) {
            	Sprite myTmpSprite[] = new Sprite[myGuiSprites.getSize()];
                myTmpSprite = myGuiSprites.getSprites();
                for (int i = 0; i < myGuiSprites.getSize(); i++) {
                    if (myTmpSprite[i].getID() == Integer.parseInt(myZeile.substring(17, 21))) {
                        EbaItemSprite myTmpItemSprite = (EbaItemSprite) myTmpSprite[i];
                    	if(myZeile.substring(22,23).equals("a")){
                    		myTmpItemSprite.setAnsehenSkriptID(Integer.parseInt(myZeile.substring(24,myZeile.length() - 1)));
                        }
                    	if(myZeile.substring(22,23).equals("b")){
                    		myTmpItemSprite.setBenutzenSkriptID(Integer.parseInt(myZeile.substring(24,myZeile.length() - 1)));
                        }
                    	if(myZeile.substring(22,23).equals("r")){
                    		myTmpItemSprite.setRedenMitSkriptID(Integer.parseInt(myZeile.substring(24,myZeile.length() - 1)));
                        }
                    }
                }
                nochnichtRendern = true;
            }


//          --------CHANGEINVOIMG-----------------------
            if (myZeile.startsWith("changeinvoimg(")) {
            	Sprite myTmpSprite[] = new Sprite[myGuiSprites.getSize()];
                myTmpSprite = myGuiSprites.getSprites();
                for (int i = 0; i < myGuiSprites.getSize(); i++) {
                    if (myTmpSprite[i].getID() == Integer.parseInt(myZeile.substring(14, 18))) {
                        EbaItemSprite myTmpItemSprite = (EbaItemSprite) myTmpSprite[i];

                        myTmpItemSprite.setImages(new BufferedImage[] {
        						Eba.getImage(myZeile.substring(19, myZeile.length() - 1)+ ".png"),
        						Eba.getImage(myZeile.substring(19, myZeile.length() - 1)+ "_a.png") });

                    }
                }


                try {
                    myStatement = EbaGameEngine.myConnection.createStatement();
                    myStatement.executeUpdate("UPDATE Inventarobjekt SET IconDatei= '"+ myZeile.substring(19, myZeile.length() - 1) +"' where id="+ Integer.parseInt(myZeile.substring(14, 18)));

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

                nochnichtRendern = true;
            }



//          --------CHANGEBMSKRIPT-----------------------
            if (myZeile.startsWith("changebmskript(")) {
            	boolean found = false;
            	Sprite myTmpSprite[] = new Sprite[myGuiSprites.getSize()];
                myTmpSprite = myGuiSprites.getSprites();
                for (int i = 0; i < myGuiSprites.getSize(); i++) {
                    if (myTmpSprite[i].getID() == Integer.parseInt(myZeile.substring(15, 19))) {
                    	found = true;
                    	EbaItemSprite myTmpItemSprite = (EbaItemSprite) myTmpSprite[i];
                    		myTmpItemSprite.setBenutzeMitSkriptID(Integer.parseInt(myZeile.substring(20,28)),Integer.parseInt(myZeile.substring(29,myZeile.length() - 1)));
                    }
                }

                if (!found){
	                try {
	                    myStatement = EbaGameEngine.myConnection.createStatement();
	                    myStatement.executeUpdate("UPDATE BenutzeMit " +
	                    		"SET SkriptID= "+ Integer.parseInt(myZeile.substring(29,myZeile.length() - 1)) +
	                    		" where RAUMOBJEKTID="+ Integer.parseInt(myZeile.substring(20,28))+
	                    		"AND InventarobjektID = " + Integer.parseInt(myZeile.substring(15, 19)));
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
                nochnichtRendern = true;
            }

//          --------CHANGEINVOBMSKRIPT-----------------------
            if (myZeile.startsWith("changeinvobmskript(")) {
            	Sprite myTmpSprite[] = new Sprite[myGuiSprites.getSize()];
                myTmpSprite = myGuiSprites.getSprites();
                boolean found = false;
                for (int i = 0; i < myGuiSprites.getSize(); i++) {
                	found = true;
                    if (myTmpSprite[i].getID() == Integer.parseInt(myZeile.substring(19, 23))) {
                    	EbaItemSprite myTmpItemSprite = (EbaItemSprite) myTmpSprite[i];
                    		myTmpItemSprite.setInventarBenutzeMitSkriptID(Integer.parseInt(myZeile.substring(24,28)),Integer.parseInt(myZeile.substring(29,myZeile.length() - 1)));
                    }
                }
                if (!found){
	                try {
	                    myStatement = EbaGameEngine.myConnection.createStatement();

	                    myStatement.executeUpdate("UPDATE Inventarbenutzemit SET SkriptID= "+ Integer.parseInt(myZeile.substring(29,myZeile.length() - 1)) +" where InventarobjektID=" + Integer.parseInt(myZeile.substring(24,28))+ " AND Zweiteinventarobjektid = " + Integer.parseInt(myZeile.substring(19, 23)));
	                    myStatement.executeUpdate("UPDATE Inventarbenutzemit SET SkriptID= "+ Integer.parseInt(myZeile.substring(29,myZeile.length() - 1)) +" where InventarobjektID=" + Integer.parseInt(myZeile.substring(19, 23)) + " AND Zweiteinventarobjektid = " + Integer.parseInt(myZeile.substring(24,28)));

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
                nochnichtRendern = true;
            }

//          --------CHANGECLSKRIPT-----------------------
            if (myZeile.startsWith("changeclskript(")) {
            	myChoiceList.setAuswahlSkriptID(Integer.parseInt(myZeile.substring(15, 23)),Integer.parseInt(myZeile.substring(24, 26)),Integer.parseInt(myZeile.substring(27, myZeile.length() - 1)));
            	nochnichtRendern = true;
            }



//          --------CHANGEZIELRAUM-----------------------
            if (myZeile.startsWith("changezielraum(")) {
            	Sprite myTmpSprite[] = new Sprite[myBackgroundSprites.getSize()];
                myTmpSprite = myBackgroundSprites.getSprites();
                for (int i = 0; i < myBackgroundSprites.getSize(); i++) {
                	if (myTmpSprite[i].getID() == Integer.parseInt(myZeile.substring(15, 23))) {
                    	EbaExitSprite myTmpExitSprite = (EbaExitSprite) myTmpSprite[i];
                    	myTmpExitSprite.setZielraumID(Integer.parseInt(myZeile.substring(24,myZeile.length() - 1)));
                    }
                }
                nochnichtRendern = true;
            }

            // --------SKRIPT----------------------------

            if (myZeile.startsWith("skript(")) {
            	myEbaGameObject.skriptID = Integer.parseInt(myZeile.substring(7, myZeile.length() - 1));
                myEbaGameObject.zeilennummer=0;
            	this.runScript(Integer.parseInt(myZeile.substring(7, myZeile.length() - 1)), 1);
            	myEbaGameObject.zeilennummer++;
            }



            // --------EXIT----------------------------

            if (myZeile.startsWith("exit(")) {

                try {
                	myStatement = EbaGameEngine.myConnection.createStatement();
                	myResultSet = myStatement.executeQuery("SELECT * FROM RaumobjektInteraktion WHERE RaumobjektID = "+Integer.parseInt(myZeile.substring(5, 13)));
                	myResultSet.next();

                	int myRoiID = myResultSet.getInt("ID");

                    myResultSet = myStatement.executeQuery("SELECT * FROM Ausgang WHERE ZielraumID = " + Integer.parseInt(myZeile.substring(14, 20)) + " AND RaumobjektInteraktionId = " + myRoiID);
                    myResultSet.next();


                    walkInPointX = myResultSet.getInt("walkInPointX");
                    walkInPointY = myResultSet.getInt("walkInPointY");
                    charakterBlickrichtung = myResultSet.getString("charakterBlickrichtung");
                    nextRoom = Integer.parseInt(myZeile.substring(14, 20));

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
                    if (myResultSet2 != null) {
                        try {
                            myResultSet2.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        myResultSet2 = null;
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

                  myEbaGameEngine.zeilennummer = n + 1;

                exit = true;
            }

            //--------PARAMETRISCHER EXIT----------------------------

            if (myZeile.startsWith("paramexit(")) {


            	exit = true;

                nextRoom = Integer.parseInt(myZeile.substring(10, 16));
                walkInPointX = Integer.parseInt(myZeile.substring(17, 20));
                walkInPointY = Integer.parseInt(myZeile.substring(21, 24));
                charakterBlickrichtung = myZeile.substring(25, 26);

                  myEbaGameEngine.zeilennummer = n + 1;


            }

            //-------FadeOut

            if (myZeile.startsWith("fadeout(")) {
            	Color myColor = Color.BLACK;
            	if(myZeile.substring(8, 13).equals("black")){
            		myColor = Color.BLACK;
            	}
            	if(myZeile.substring(8, 13).equals("white")){
            		myColor = Color.WHITE;
            	}
                myEbaGameObject.fadeOut(myColor ,Integer.parseInt(myZeile.substring(14, 18)));
                myPlayerSprite.warten(Integer.parseInt(myZeile.substring(14, 18)));
            }
            
            //-------FadeOutTempomorph

            if (myZeile.startsWith("tempomorphen")) {
            	
            	myEbaGameObject.fadeOut(Color.WHITE ,7000);
                myPlayerSprite.warten(7000);
                
                
                myEbaGameObject.myEbaGameEngine.bsSound.play("audio/soundfx/h-tempom.wav");
                
                
            }
            
            //-------FadeOutTempomorph

            if (myZeile.startsWith("tempomorph2")) {
            	
            	myEbaGameObject.fadeOut(Color.WHITE ,7000);
                myPlayerSprite.warten(7000);
                
                
                myEbaGameObject.myEbaGameEngine.bsSound.play("audio/soundfx/h-tempo2.wav");
                
                
            }
            
            //-------FadeIn

            if (myZeile.startsWith("fadein(")) {
            	Color myColor = Color.BLACK;
            	if(myZeile.substring(7, 12).equals("black")){
            		myColor = Color.BLACK;
            	}
            	if(myZeile.substring(7, 12).equals("white")){
            		myColor = Color.WHITE;
            	}
                myEbaGameObject.fadeIn(myColor ,Integer.parseInt(myZeile.substring(13, 17)));
                myPlayerSprite.warten(Integer.parseInt(myZeile.substring(13, 17)));
            }


            //--------SCANIMATE---------------------------
            if (myZeile.startsWith("animatesc(")) {
                myPlayerSprite.actionMode(Integer.parseInt(myZeile.substring(10, 12)),Integer.parseInt(myZeile.substring(13, 17)));
            }

            //--------ANIMATENSC----------------------------

            if (myZeile.startsWith("animatensc(")) {
            	myNscSprite = (EbaNscSprite)myObjectSprites.getSprite(Integer.parseInt(myZeile.substring(11,19)));
                if (myNscSprite == null){
                	myNscSprite = (EbaNscSprite)myBackgroundSprites.getSprite(Integer.parseInt(myZeile.substring(11,19)));
                }
                if (myNscSprite != null){
                	myNscSprite.actionMode(Integer.parseInt(myZeile.substring(20, 22)),Integer.parseInt(myZeile.substring(23, 27)));
                }
            }


            //--------PARALLEL SCANIMATE---------------------------
            if (myZeile.startsWith("animatescp(")) {
                myPlayerSprite.actionMode(Integer.parseInt(myZeile.substring(11, 13)),Integer.parseInt(myZeile.substring(14, 18)));
                parallelPerformance = true;
            }

            //--------PARALLEL ANIMATENSC----------------------------

            if (myZeile.startsWith("animatenscp(")) {
            	myNscSprite = (EbaNscSprite)myObjectSprites.getSprite(Integer.parseInt(myZeile.substring(12,20)));
                if (myNscSprite == null){
                	myNscSprite = (EbaNscSprite)myBackgroundSprites.getSprite(Integer.parseInt(myZeile.substring(12,20)));
                }
                if (myNscSprite != null){
                	myNscSprite.actionMode(Integer.parseInt(myZeile.substring(21, 23)),Integer.parseInt(myZeile.substring(24, 28)));
                }
                parallelPerformance = true;
            }


            // --------WALK---------------------------
            if (myZeile.startsWith("walk(")) {
            	int[] walkHere;
            	myEbaGameObject.myEbaWalkableAreaMap.findWay(
						(int) myPlayerSprite.getBaseX(),
						(int) myPlayerSprite.getBaseY(),
						Integer.parseInt(myZeile.substring(5, 8)), Integer.parseInt(myZeile.substring(9, 12)));
				walkHere = (int[]) myEbaGameObject.myEbaWalkableAreaMap.pop();
                myPlayerSprite.walkTo(walkHere[0], walkHere[1]);
            }

            // --------WALK P---------------------------
            if (myZeile.startsWith("walkp(")) {
            	int[] walkHere;
            	myEbaGameObject.myEbaWalkableAreaMap.findWay(
						(int) myPlayerSprite.getBaseX(),
						(int) myPlayerSprite.getBaseY(),
						Integer.parseInt(myZeile.substring(6, 9)), Integer.parseInt(myZeile.substring(10, 13)));
				walkHere = (int[]) myEbaGameObject.myEbaWalkableAreaMap.pop();
                myPlayerSprite.walkTo(walkHere[0], walkHere[1]);
                parallelPerformance = true;
            }


            // --------WALK NSC----------------------------

            if (myZeile.startsWith("walknsc(")) {
            	myNscSprite = (EbaNscSprite)myObjectSprites.getSprite(Integer.parseInt(myZeile.substring(8,16)));
                if (myNscSprite == null){
                	myNscSprite = (EbaNscSprite)myBackgroundSprites.getSprite(Integer.parseInt(myZeile.substring(8,16)));
                }
                if (myNscSprite != null){
                    int[] walkHere;
                    myEbaGameObject.myNscEbaWalkableAreaMap.findWay(
          				(int) myNscSprite.getBaseX(),
          				(int) myNscSprite.getBaseY(),
          				Integer.parseInt(myZeile.substring(17, 20)), Integer.parseInt(myZeile.substring(21, 24)));
          			walkHere = (int[]) myEbaGameObject.myNscEbaWalkableAreaMap.pop();
          			myNscSprite.walkTo(walkHere[0], walkHere[1]);
                }
            }

            // --------PARALLEL WALK NSC----------------------------


            if (myZeile.startsWith("walknscp(")) {
            	myNscSprite = (EbaNscSprite)myObjectSprites.getSprite(Integer.parseInt(myZeile.substring(9,17)));
                if (myNscSprite == null){
                	myNscSprite = (EbaNscSprite)myBackgroundSprites.getSprite(Integer.parseInt(myZeile.substring(9,17)));
                }
                if (myNscSprite != null){
                    int[] walkHere;
                    myEbaGameObject.myNscEbaWalkableAreaMap.findWay(
          				(int) myNscSprite.getBaseX(),
          				(int) myNscSprite.getBaseY(),
          				Integer.parseInt(myZeile.substring(18, 21)), Integer.parseInt(myZeile.substring(22, 25)));
          			walkHere = (int[]) myEbaGameObject.myNscEbaWalkableAreaMap.pop();
          			myNscSprite.walkTo(walkHere[0], walkHere[1]);
          			parallelPerformance = true;
                }
            }



            // --------FREE WALK---------------------------
            if (myZeile.startsWith("freewalk(")) {
            	myPlayerSprite.walkTo(Integer.parseInt(myZeile.substring(9, 12)), Integer.parseInt(myZeile.substring(13, 16)));
            }


            // --------FREE WALK NSC----------------------------

            if (myZeile.startsWith("freewalknsc(")) {
            	myNscSprite = (EbaNscSprite)myObjectSprites.getSprite(Integer.parseInt(myZeile.substring(12,20)));
                if (myNscSprite == null){
                	myNscSprite = (EbaNscSprite)myBackgroundSprites.getSprite(Integer.parseInt(myZeile.substring(12,20)));
                }
                if (myNscSprite != null){
          			myNscSprite.walkTo(Integer.parseInt(myZeile.substring(21, 24)), Integer.parseInt(myZeile.substring(25, 28)));
                }
            }

            // -------- PUT NSC ----------------------

            if (myZeile.startsWith("putnsc(")) {

            	myNscSprite = (EbaNscSprite)myObjectSprites.getSprite(Integer.parseInt(myZeile.substring(7,15)));
                if (myNscSprite == null){
                	myNscSprite = (EbaNscSprite)myBackgroundSprites.getSprite(Integer.parseInt(myZeile.substring(7,15)));
                }
                if (myNscSprite != null){
                	myNscSprite.setX(Integer.parseInt(myZeile.substring(16, 19)));
                	myNscSprite.setX(Integer.parseInt(myZeile.substring(20, 23)));

                	try {
	                    myStatement = EbaGameEngine.myConnection.createStatement();
	                    myStatement.executeUpdate("UPDATE Raumobjekt SET posx= "+ (Integer.parseInt(myZeile.substring(16, 19))) +" where id="+ Integer.parseInt(myZeile.substring(7,15)));
	                    myStatement.executeUpdate("UPDATE Raumobjekt SET posy= "+ (Integer.parseInt(myZeile.substring(16, 19))) +" where id="+ Integer.parseInt(myZeile.substring(20, 23)));

	                    myStatement.close();
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
                nochnichtRendern = true;
            }
            
            
            // -------- PUT NSC ----------------------

            if (myZeile.startsWith("putsc(")) {
            	
            	myPlayerSprite.setX(Integer.parseInt(myZeile.substring(6, 9)) );
            	myPlayerSprite.setY(Integer.parseInt(myZeile.substring(10, 13)));
            	nochnichtRendern = true;
            	
            }
            // --------WAIT---------------------------
            if (myZeile.startsWith("wait(")) {
                myPlayerSprite.warten(Integer.parseInt(myZeile.substring(5, 9)));
            }

            // --------ITEM ACTIVATE ---------------------------------------
            // -----So wie es jetzt ist nur f�r das Edna-Gui g�ltig --------
            // ---- TODO: F�r Harvey und andere GUIs verallgemeinern -------
            // -------------------------------------------------------------

            if (myZeile.startsWith("itemactivate(")) {
            	myEbaGameObject.activateItem(Integer.parseInt(myZeile.substring(13, 17)));
            }

            if (myZeile.startsWith("itemactivatesound(")) {
            	myEbaGameObject.activateItem(Integer.parseInt(myZeile.substring(18, 22)));
            myPlayerSprite.warten(0100);
            
            
            myEbaGameObject.myEbaGameEngine.bsSound.play("audio/soundfx/nehmen_2.wav");
            
            }

            // --------ITEM DEACTIVATE ----------------

            if (myZeile.startsWith("itemdeactivate(")) {
            	myEbaGameObject.deactivateItem(Integer.parseInt(myZeile.substring(15, 19)));
            	//nochnichtRendern = true;
            }

            // --------ACTIVATE RAUMOBJEKT-------------
            if (myZeile.startsWith("activate(")) {
                try {
                    myStatement = EbaGameEngine.myConnection.createStatement();
                    myStatement.executeUpdate("UPDATE raumobjekt SET aktiv=true where ID=" + Integer.parseInt(myZeile.substring(9, 17)));
                    myStatement.close();

                    Sprite myTmpSprite[] = new Sprite[myObjectSprites.getSize()];
                    myTmpSprite = myObjectSprites.getSprites();
                    for (int i = 0; i < myObjectSprites.getSize(); i++) {
                        if (myTmpSprite[i].getID() == Integer.parseInt(myZeile.substring(9, 17))) {
                            myTmpSprite[i].setActive(true);
                        }
                    }
                    myTmpSprite = new Sprite[myBackgroundSprites.getSize()];

                    myTmpSprite = myBackgroundSprites.getSprites();
                    for (int i = 0; i < myBackgroundSprites.getSize(); i++) {
                        if (myTmpSprite[i].getID() == Integer.parseInt(myZeile.substring(9, 17))) {
                            myTmpSprite[i].setActive(true);
                        }
                    }

                } catch (NumberFormatException e) {

                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                nochnichtRendern = true;
            }

            // --------DEACTIVATE RAUMOBJEKT-------------
            if (myZeile.startsWith("inactivate(")) {
                try {
                    myStatement = EbaGameEngine.myConnection.createStatement();
                    myStatement.executeUpdate("UPDATE raumobjekt SET aktiv=false where ID=" + Integer.parseInt(myZeile.substring(11, 19)));
                    myStatement.close();

                    Sprite myTmpSprite[] = new Sprite[myObjectSprites.getSize()];
                    myTmpSprite = myObjectSprites.getSprites();
                    for (int i = 0; i < myObjectSprites.getSize(); i++) {
                        if (myTmpSprite[i].getID() == Integer.parseInt(myZeile.substring(11, 19))) {
                            myTmpSprite[i].setActive(false);
                        }
                    }
                    myTmpSprite = new Sprite[myBackgroundSprites.getSize()];
                    myTmpSprite = myBackgroundSprites.getSprites();
                    for (int i = 0; i < myBackgroundSprites.getSize(); i++) {
                        if (myTmpSprite[i].getID() == Integer.parseInt(myZeile.substring(11, 19))) {
                            myTmpSprite[i].setActive(false);
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
                nochnichtRendern = true;
            }

            // ---------ACTIVATE TIMER---

            if (myZeile.startsWith("activatetimer(")) {
            	try {
                    myStatement = EbaGameEngine.myConnection.createStatement();
                    myResultSet = myStatement.executeQuery("SELECT * FROM Raum WHERE ID = "+myEbaGameEngine.getCurrentGameID());
                    myResultSet.next();
                    if( myResultSet.getInt("TimerID") == Integer.parseInt(myZeile.substring(14, 20))){
                    	myResultSet2 = myStatement.executeQuery("SELECT * FROM Timer WHERE ID = "+Integer.parseInt(myZeile.substring(14, 20)));
                    	myResultSet2.next();
                    	myEbaGameObject.setTimerLength(myResultSet2.getInt("Dauer"));
                    	myEbaGameObject.setTimerAktiv( Boolean.parseBoolean(myZeile.substring(21, myZeile.length() - 1)) );
                    }
                    myStatement.executeUpdate("UPDATE timer SET aktiv="+myZeile.substring(21, myZeile.length() - 1)+" where ID=" + Integer.parseInt(myZeile.substring(14, 20)));
                    myStatement.close();
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
                        myStatement = null;
                    }
                	if (myResultSet2 != null) {
                        try {
                        	myResultSet2.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        myStatement = null;
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
                nochnichtRendern = true;
            }


            // ---------Animate----------

            if (myZeile.startsWith("animate(")) {
                Sprite myTmpSprite[] = new Sprite[myObjectSprites.getSize()];
                myTmpSprite = myObjectSprites.getSprites();
                for (int i = 0; i < myObjectSprites.getSize(); i++) {
                    if (myTmpSprite[i].getID() == Integer.parseInt(myZeile.substring(8, 16))) {
                        myTmpSprite[i].setActive(true);
                        ((EbaObjectSprite) myTmpSprite[i]).setAnimate(true);
                    }
                }
                myTmpSprite = new Sprite[myBackgroundSprites.getSize()];

                myTmpSprite = myBackgroundSprites.getSprites();
                for (int i = 0; i < myBackgroundSprites.getSize(); i++) {
                    if (myTmpSprite[i].getID() == Integer.parseInt(myZeile.substring(8, 16))) {
                        myTmpSprite[i].setActive(true);
                        ((EbaObjectSprite) myTmpSprite[i]).setAnimate(true);
                    }
                }
            }

            //---------Blickrichtung----------

            if (myZeile.startsWith("lookat(")) {
            	myPlayerSprite.lookReallyAt(myZeile.substring(7,8));
            	nochnichtRendern = true;
            }

            //---------NSC - Blickrichtung ----------

            if (myZeile.startsWith("nsclookat(")) {
            	myNscSprite = (EbaNscSprite)myObjectSprites.getSprite(Integer.parseInt(myZeile.substring(10,18)));
                if (myNscSprite == null){
                	myNscSprite = (EbaNscSprite)myBackgroundSprites.getSprite(Integer.parseInt(myZeile.substring(10,18)));
                }
                if (myNscSprite != null){
                	myNscSprite.lookReallyAt(myZeile.substring(19,20));
                }
            }

            // --------ENDE-------------------

            if (myZeile.startsWith("end") || myZeile.equals("")) {
                skriptLaeuft = false;
            }

        }

        if(nochnichtRendern){
        	myEbaGameObject.zeilennummerInc();
        	n++;
        }

    	}while(nochnichtRendern);
    }

    private void defaultExit(int AusgangID) {
		try {
            myStatement = EbaGameEngine.myConnection.createStatement();
            myResultSet = myStatement.executeQuery("SELECT * FROM Ausgang WHERE ID =" + AusgangID);
            myResultSet.next();
            myEbaGameEngine.walkInPointX = myResultSet.getInt("walkInPointX");
            myEbaGameEngine.walkInPointY = myResultSet.getInt("walkInPointY");
            myEbaGameEngine.charakterBlickrichtung = myResultSet.getString("charakterBlickrichtung");
            myEbaGameEngine.nextGameID = myResultSet.getInt("ZielraumID");
            myResultSet.close();
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
        myEbaGameObject.finish();

    }

    public boolean isRunning() {
        return skriptLaeuft;
    }

    public boolean isPerforming() {
    	//System.out.println("L�uft gerade ein Soundeffekt? "+isSayingSound);
    	if ( myNscSprite != null && !parallelPerformance){
	        if (!myPlayerSprite.isTalking() && !myPlayerSprite.isWalking() && !myPlayerSprite.isThinking() && !myPlayerSprite.isActing() && !myNscSprite.isTalking() && !myNscSprite.isWalking() && !myNscSprite.isActing()) {
	            if (isSayingSound){
	            	//System.out.println(myAudioPfad);
	            	if (myEbaGameObject.myEbaGameEngine.bsSound.getAudioRenderer(myAudioPfad).getStatus() != 1){
	        			isSayingSound = false;
	        			zeileLaeuft = false;
	    	        }
	            } else {
	            	zeileLaeuft = false;
	            }
	        }
    	}else if ( myNscSprite != null && parallelPerformance){
//	        if (!myPlayerSprite.isTalking() && !myPlayerSprite.isWalking() && !myPlayerSprite.isThinking() && !myPlayerSprite.isActing() && !myNscSprite.isTalking()) {
	            if (isSayingSound){
	            	if (myEbaGameObject.myEbaGameEngine.bsSound.getAudioRenderer(myAudioPfad).getStatus() != 1){
	        			isSayingSound = false;
	        			zeileLaeuft = false;
	        			parallelPerformance = false;
	    	        }
	            } else {
	            	zeileLaeuft = false;
	            	parallelPerformance = false;
	            }
//	        }

    	}else{
    		if (!myPlayerSprite.isTalking() && !myPlayerSprite.isWalking() && !myPlayerSprite.isThinking() && !myPlayerSprite.isActing()) {
	            if (isSayingSound){
	            	if (myEbaGameObject.myEbaGameEngine.bsSound.getAudioRenderer(myAudioPfad).getStatus() != 1){
	        			isSayingSound = false;
	        			zeileLaeuft = false;
	    	        }
	            } else {
	            	zeileLaeuft = false;
	            }
    		}
    	}
        return zeileLaeuft;
    }

    public void testSkript() {
    	try {
        	myStatement = EbaGameEngine.myConnection.createStatement();
        	myResultSet = myStatement.executeQuery("SELECT * FROM skript");
        	
        	while (myResultSet.next()) {
        		String myLine = myResultSet.getString("skriptaktion");
        		if (myLine == null || myLine.equals("")){
        			
        		}else{ 		
	        		if(myLine.contains("say(") 
	        				|| myLine.contains("think(") 
	        				|| myLine.contains("saynsc(") 
	        				|| myLine.contains("saynscp(") 
	        				|| myLine.contains("saysound(")
	        				|| myLine.contains("saysoundp(")){
	        			
	        			String mytestAudioPfad = "audio/" + myResultSet.getInt("ID") + "-" + myResultSet.getInt("ZEILENNUMMER") + audioFileSuffix;
	        			File audioFile = new File(mytestAudioPfad);
	        			
	        			if(audioFile.exists()){
	        				
	        			} else {
	        				        			
	        			String apo = "'";
	        			if(myLine.contains(apo)){
	     
	        			}else{
	        			myResultSet2 = myStatement.executeQuery("SELECT * FROM skript WHERE skriptaktion = "+apo+myLine+apo);
	        			boolean allreadyFound = false;
		        			while (myResultSet2.next()) {
		        				if(allreadyFound == false){
		        					
			        				String mytestAudioPfad2 = "audio/" + myResultSet2.getInt("ID") + "-" + myResultSet2.getInt("ZEILENNUMMER") + audioFileSuffix;
				        			File audioFile2 = new File(mytestAudioPfad2);
				        			
				        			if(!mytestAudioPfad2.equals(mytestAudioPfad)){
				        				if(audioFile2.exists()){
				        					
				        					//EbaFileIO.copyFile("audio/"+audioFile2.getName(), "audioNew/" + audioFile.getName());
				        					allreadyFound = true;
				        				}
				        				
				        			}
		        				}
		        			
		        			}
	        			}
		
	        			}
	        		}
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
    }
    
    public String getSkriptZeile(int myZeilenNummer) {
        String myZeile = "";
        try {
            myStatement = EbaGameEngine.myConnection.createStatement();
            myResultSet = myStatement.executeQuery("SELECT * FROM Skript WHERE ID =" + myScriptID + "AND ZEILENNUMMER = " + myZeilenNummer);
            while (myResultSet.next()) {
                myZeile = myResultSet.getString("Skriptaktion");

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

        return myZeile;
    }


}
