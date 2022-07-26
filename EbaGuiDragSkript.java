package de.daedalic.eba;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.golden.gamedev.engine.BaseInput;
import com.golden.gamedev.object.Sprite;

public class EbaGuiDragSkript extends EbaGameObject
{
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
     int[] slotX = { 0, 68, 69, 70, 67, 66 };
     int[] slotY = { 0, 103, 169, 241, 304, 380 };
     private EbaTopicSprite myTopic;
     int slotNummer = 0;
     int mySpriteID;
     public EbaGuiDragSkript(EbaGameEngine myGameEngine)
     {
          super(myGameEngine);
     }
     public void initResources()
     {
          super.initResources();
          myTopicCursorSprite = new Sprite(Eba.getImage("gui/ednajung/b_ansehen.png"));
          myTopicCursorSprite.setImmutable(true);
          myTopicCursorSprite.setActive(false);
          myPlayField.add(myTopicCursorSprite);
          myBefehlszeile = new EbaCommandPromptSprite(myActiveFont, myInactiveFont, fontManager, 10, 530);
          myTextSprites.add(myBefehlszeile);

          myScriptInterpreter = new EbaScriptInterpreter(myCharacterSprite, myBackgroundSprites, myObjectSprites, myTextSprites, myGuiSprites, myChoiceList, myEbaGameEngine, this);
          try
          {
               myStatement = EbaGameEngine.myConnection.createStatement();
               Sprite myTmpSprite[] = new Sprite[myObjectSprites.getSize()];
               myTmpSprite = myObjectSprites.getSprites();
               for (int i = 0; i < myObjectSprites.getSize(); i++)
               {
                    myResultSet1 = myStatement.executeQuery("SELECT * from Topic where raumobjektID=" + myTmpSprite[i].getID());
                    if (myResultSet1.next())
                    {
                         EbaTopicSprite myTopicSprite = new EbaTopicSprite(new BufferedImage[] { Eba.getImage(myResultSet1.getString("IconDatei")) }, 0, 0, myResultSet1.getInt("ID"), myResultSet1.getString("Bezeichnung"), myResultSet1.getInt("Inventarposition"), myResultSet1.getInt("Topicleistenposition"), myResultSet1.getInt("SkriptID"), myResultSet1.getInt("RaumobjektID"));
                         myTopicSprite.setDataID(myResultSet1.getString("Bezeichnung"));
                         myTopicSprite.setID(myResultSet1.getInt("ID"));
                         myTopicSprite.setImmutable(true);
                         if (myTopicSprite.getTopicposition() > 0)
                         {
                              myTopicSprite.setY(slotY[myTopicSprite.getTopicposition()]);
                              myTopicSprite.setX(slotX[myTopicSprite.getTopicposition()]);
                              myTopicSprite.setActive(true);
                         } else
                         {
                              myTopicSprite.setActive(false);
                         }
                         myTopicSprites.add(myTopicSprite);
                    }
               }
               myTmpSprite = new Sprite[myBackgroundSprites.getSize()];
               myTmpSprite = myBackgroundSprites.getSprites();
               for (int i = 0; i < myBackgroundSprites.getSize(); i++)
               {
                    myResultSet1 = myStatement.executeQuery("SELECT * from Topic where raumobjektID=" + myTmpSprite[i].getID());
                    while (myResultSet1.next())
                    {
                         EbaTopicSprite myTopicSprite = new EbaTopicSprite(new BufferedImage[] { Eba.getImage(myResultSet1.getString("IconDatei")) }, 0, 0, myResultSet1.getInt("ID"), myResultSet1.getString("Bezeichnung"), myResultSet1.getInt("Inventarposition"), myResultSet1.getInt("Topicleistenposition"), myResultSet1.getInt("SkriptID"), myResultSet1.getInt("RaumobjektID"));
                         myTopicSprite.setDataID(myResultSet1.getString("Bezeichnung"));
                         myTopicSprite.setID(myResultSet1.getInt("ID"));
                         myTopicSprite.setImmutable(true);
                         if (myTopicSprite.getTopicposition() > 0)
                         {
                              myTopicSprite.setY(slotY[myTopicSprite.getTopicposition()]);
                              myTopicSprite.setX(slotX[myTopicSprite.getTopicposition()]);
                              myTopicSprite.setActive(true);
                         } else
                         {
                              myTopicSprite.setActive(false);
                         }
                         myTopicSprites.add(myTopicSprite);
                    }
               }
          } catch (SQLException e)
          {
               // TODO Auto-generated catch block
               e.printStackTrace();
          } finally
          {
               if (myResultSet1 != null)
               {
                    try
                    {
                         myResultSet1.close();
                    } catch (SQLException e)
                    {
                         e.printStackTrace();
                    }
                    myResultSet1 = null;
               }
               if (myStatement != null)
               {
                    try
                    {
                         myStatement.close();
                    } catch (SQLException e)
                    {
                         e.printStackTrace();
                    }
                    myStatement = null;
               }
          }
     }
     public void update(long elapsedTime)
     {
          super.update(elapsedTime);
          if (dragAktiv == true)
          {
               myGuiSprites.setActive(false);
               myExit.setActive(false);
          } else
          {
               myGuiSprites.setActive(true);
               myExit.setActive(true);
          }
          if (!myScriptInterpreter.isRunning() && myCharacterSprite.isUnterbrochen())
          {
               isCommandComplete = false;
               myCommand = "";
          }
          if (isTopic)
          {
               isTopic = false;
          }
          if (isExit)
          {
               isExit = false;
          }
          if (isGuiElement)
          {
               isGuiElement = false;
          }
          // ON MOUSE OVER:
          mySprite = checkPosMouse(myTopicSprites, true);
          if (mySprite == null)
          {
               mySprite = checkPosMouse(myGuiSprites, true);
               mySpriteID = 0;
               
          } else
          {
        	  mySpriteID = mySprite.getID();
               if (mySprite.getID() > 0)
               {
                    isTopic = true;
               }
          }
          if (mySprite == null)
          {
               mySprite = checkPosMouseInteraction(myObjectSprites, true);
          } else
          {
               isGuiElement = true;
          }
          if (mySprite == null)
          {
               mySprite = checkPosMouse(myBackgroundSprites, true);
          }
          if (mySprite == null)
          {
               myDataID = "";
          } else
          {
               if (myDragStatus == DRAGGING)
               {
                    if (mySprite.getDataID().equals("Topicleiste"))
                    {
                         myDataID = "Положить";
                    }
                    if (mySprite.getDataID().equals("Эдна"))
                    {
                         myDataID = "Эдна";
                    }
               } else
               {
                    if (mySprite.getDataID() == "Выход")
                    {
                         isExit = true;
                    }
                    myDataID = (String) mySprite.getDataID();
               }
          }
          if (isCommandComplete)
          {
               if (!myCharacterSprite.isWalking())
               {
                    if (myCommandObject == null)
                    {
                         skriptID = 0;
                    }
                    if (myCommand != "")
                    {
                         if (myCommand == "Подойди к ")
                         {
                              myCommand = "";
                              isCommandComplete = false;
                         } else if (myCommand.startsWith("Посмотри на "))
                         {
                              myCommand = "";
                              isCommandComplete = false;
                              if (myCommandObject == null)
                              {
                              } else
                              {
                                   skriptID = ((EbaInteractionSprite) myCommandObject).getMyAnsehenSkript();
                              }
                              myScriptInterpreter.runScript(skriptID, 1);
                              zeilennummer++;
                         } else if ((myCommand.startsWith("Подойди к ")) && (myCommandObject.getDataID() == "Выход"))
                         {
                              myCommand = "";
                              isCommandComplete = false;
                              if (myCommandObject == null)
                              {
                              } else
                              {
                                   skriptID = ((EbaInteractionSprite) myCommandObject).getMyBenutzenSkript();
                                   if (skriptID <= 0)
                                   {
                                        skriptID = ((EbaExitSprite) myCommandObject).getAusgangID() * (-1);
                                   }
                              }
                              myScriptInterpreter.runScript(skriptID, 1);
                              zeilennummer++;
                         } else
                         {
                              myCommand = "";
                              isCommandComplete = false;
                         }
                    }
                    myBefehlszeilenText = myCommand;
               }
          } else if (myDataID == "")
          {
               if (myCommand == "")
               {
                    myBefehlszeilenText = "";
                    myCommandObject = null;
               } else
               {
                    if (myCommand == "Подойди к ")
                    {
                         myBefehlszeilenText = "";
                         myCommandObject = null;
                    } else
                    {
                         myBefehlszeilenText = myCommand;
                    }
               }
          } else if (myDataID == "EdnasInventar" || myDataID == "Topicleiste")
          {
               // do nothing
          } else
          { // DataID = Objekt
               if (myCommand == "")
               {
                    myBefehlszeilenText = myDataID;
                    if (isTopic)
                    {
                    } else if (isExit)
                    {
                         myBefehlszeilenText = "Подойди к " + ((EbaExitSprite) mySprite).getMyMouseoverText();
                    } else if (isGuiElement)
                    {
                         // Do nothing
                    } else
                    {
                    }
               } else if (isExit || isGuiElement)
               {
                    // do nothing
               } else
               {
                    myBefehlszeilenText = myCommand + myDataID;
               }
          }
          // ON MOUSE PRESSED:
          switch (bsInput.getMousePressed())
          {
          case BaseInput.NO_BUTTON:
               break;
          case MouseEvent.BUTTON1:
               // linke Maustaste:
               myDragStatus = STARTDRAG;
               if (myDataID == "")
               {
                    myEbaWalkableAreaMap.findWay((int) myCharacterSprite.getBaseX(), (int) myCharacterSprite.getBaseY(), getMouseX(), getMouseY());
                    walkHere = (int[]) myEbaWalkableAreaMap.pop();
                    myBefehlszeilenText = "Подойди к ";
                    myCommand = "Подойди к ";
                    isCommandComplete = true;
                    myCharacterSprite.walkTo(walkHere[0], walkHere[1]);
               } else
               {
                    if (isTopic)
                    {
                         if (mySprite.getID() > 0)
                         {
                              if (myCommand == "")
                              {
                                   // TODO: Подойди к Edna und Поговори с Edna ?ber
                                   // dieses Topic
                              }
                         }
                    } else if (mySprite.getDataID() == "Inventar")
                    {
                         // Do Nothing
                    } else if (mySprite.getDataID() == "К Эдне")
                    {
                         // Do Nothing
                    } else
                    { // ----mySprite ist ein Objekt--------
                         if (myCommand != "" && isExit)
                         {
                              // do nothing
                         } else
                         {
                              //
                              //
                              // if (!isTopic && !isGuiElement) {
                              if (isExit)
                              {
                                   myCommandObject = mySprite;
                                   if (((EbaInteractionSprite) myCommandObject).getWalkToX() < 0)
                                   {
                                        walkToX = (int) myCommandObject.getX();
                                        walkToY = (int) myCommandObject.getY();
                                   } else
                                   {
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
          }
          // ON MOUSE RELEASED:
          switch (bsInput.getMouseReleased())
          {
          case BaseInput.NO_BUTTON:
               break;
          case MouseEvent.BUTTON1:
               // linke Maustaste:


               // ablegen
               if ( myDragStatus == STARTDRAG && dragAktiv == true)
               {
                    EbaTopicSprite myNewOnMouseTopic;
                    if (myDataID != "")
                    {
                         Sprite myTmpTopic[];
                         myTmpTopic = myTopicSprites.getSprites();
                         for (int i = 0; i < myTopicSprites.getSize(); i++)
                         {
                              if (((EbaTopicSprite) myTmpTopic[i]).getDataID() == myDataID)
                              {
                                   myNewOnMouseTopic = (EbaTopicSprite) myTmpTopic[i];

                                       myNewOnMouseTopic.setActive(false);
                                       myTopic.setY(myNewOnMouseTopic.getY());
                                       myTopic.setX(myNewOnMouseTopic.getX());
                                       myTopic.setActive(true);
                                       myTopic.setTopicposition(myNewOnMouseTopic.getTopicposition());
                                       myTopicCursorSprite.setActive(false);
                                       myTopicCursorSprite.setImage(myNewOnMouseTopic.getImage());
                                       myTopicCursorSprite.setActive(true);
                                 	  if (myNewOnMouseTopic.getTopicposition()==1)
                                       {
                                            if(myTopic.getID()==10600101){
                                           	 skriptID = 106001;
                                            }
                                            if(myTopic.getID()==10600201){
                                           	 skriptID = 106002;
                                            }
                                            if(myTopic.getID()==10600301){
                                           	 skriptID = 106003;
                                            }
                                            if(myTopic.getID()==10600401){
                                           	 skriptID = 106004;
                                            }
                   						 if(myTopic.getID()==10600501){
                   							 skriptID = 106005;
                   						 }
                                       }
                                       if (myNewOnMouseTopic.getTopicposition()==2)
                                       {
                                            if(myTopic.getID()==10600101){
                                           	 skriptID = 106006;
                                            }
                                            if(myTopic.getID()==10600201){
                                           	 skriptID = 106007;
                                            }
                                            if(myTopic.getID()==10600301){
                                           	 skriptID = 106008;
                                            }
                                            if(myTopic.getID()==10600401){
                                           	 skriptID = 106009;
                                            }
                   						 if(myTopic.getID()==10600501){
                   							 skriptID = 106010;
                   						 }
                                       }
                                       if (myNewOnMouseTopic.getTopicposition()==3)
                                       {
                                            if(myTopic.getID()==10600101){
                                           	 skriptID = 106011;
                                            }
                                            if(myTopic.getID()==10600201){
                                           	 skriptID = 106012;
                                            }
                                            if(myTopic.getID()==10600301){
                                           	 skriptID = 106013;
                                            }
                                            if(myTopic.getID()==10600401){
                                           	 skriptID = 106014;
                                            }
                   						 if(myTopic.getID()==10600501){
                   							 skriptID = 106015;
                   						 }
                                       }
                                       if (myNewOnMouseTopic.getTopicposition()==4)
                                       {
                                            if(myTopic.getID()==10600101){
                                           	 skriptID = 106016;
                                            }
                                            if(myTopic.getID()==10600201){
                                           	 skriptID = 106017;
                                            }
                                            if(myTopic.getID()==10600301){
                                           	 skriptID = 106018;
                                            }
                                            if(myTopic.getID()==10600401){
                                           	 skriptID = 106019;
                                            }
                   						 if(myTopic.getID()==10600501){
                   							 skriptID = 106020;
                   						 }
                                       }
                                       if (myNewOnMouseTopic.getTopicposition()==5)
                                       {
                                            if(myTopic.getID()==10600101){
                                           	 skriptID = 106021;
                                            }
                                            if(myTopic.getID()==10600201){
                                           	 skriptID = 106022;
                                            }
                                            if(myTopic.getID()==10600301){
                                           	 skriptID = 106023;
                                            }
                                            if(myTopic.getID()==10600401){
                                           	 skriptID = 106024;
                                            }
                   						 if(myTopic.getID()==10600501){
                   							 skriptID = 106025;
                   						 }
                                       }

                                        myTopic = myNewOnMouseTopic;

                                   dragAktiv = true;
                                   //skriptID = ((EbaTopicSprite) myTopic).getSkriptID();
                                   if (skriptID > 0)
                                   {
                                        myScriptInterpreter.runScript(skriptID, 1);
                                        zeilennummer++;
                                   }
                              }
                         }
                    }

                    if (myDataID.equals("Питер") || myDataID.equals("Хоти и Моти") || myDataID.equals("Адриан") || myDataID.equals("Профессор Нок") || myDataID.equals("Человек-пчела"))
                    {
                         if (myDataID.equals("Питер"))
                         {
                              slotNummer = 1;
                              if(myTopic.getID()==10600101){
                             	 skriptID = 106001;
                              }
                              if(myTopic.getID()==10600201){
                             	 skriptID = 106002;
                              }
                              if(myTopic.getID()==10600301){
                             	 skriptID = 106003;
                              }
                              if(myTopic.getID()==10600401){
                             	 skriptID = 106004;
                              }
     						 if(myTopic.getID()==10600501){
     							 skriptID = 106005;
     						 }
                         }
                         if (myDataID.equals("Хоти и Моти"))
                         {
                              slotNummer = 2;
                              if(myTopic.getID()==10600101){
                             	 skriptID = 106006;
                              }
                              if(myTopic.getID()==10600201){
                             	 skriptID = 106007;
                              }
                              if(myTopic.getID()==10600301){
                             	 skriptID = 106008;
                              }
                              if(myTopic.getID()==10600401){
                             	 skriptID = 106009;
                              }
     						 if(myTopic.getID()==10600501){
     							 skriptID = 106010;
     						 }
                         }
                         if (myDataID.equals("Адриан"))
                         {
                              slotNummer = 3;
                              if(myTopic.getID()==10600101){
                             	 skriptID = 106011;
                              }
                              if(myTopic.getID()==10600201){
                             	 skriptID = 106012;
                              }
                              if(myTopic.getID()==10600301){
                             	 skriptID = 106013;
                              }
                              if(myTopic.getID()==10600401){
                             	 skriptID = 106014;
                              }
     						 if(myTopic.getID()==10600501){
     							 skriptID = 106015;
     						 }
                         }
                         if (myDataID.equals("Профессор Нок"))
                         {
                              slotNummer = 4;
                              if(myTopic.getID()==10600101){
                             	 skriptID = 106016;
                              }
                              if(myTopic.getID()==10600201){
                             	 skriptID = 106017;
                              }
                              if(myTopic.getID()==10600301){
                             	 skriptID = 106018;
                              }
                              if(myTopic.getID()==10600401){
                             	 skriptID = 106019;
                              }
     						 if(myTopic.getID()==10600501){
     							 skriptID = 106020;
     						 }
                         }
                         if (myDataID.equals("Человек-пчела"))
                         {
                              slotNummer = 5;
                              if(myTopic.getID()==10600101){
                             	 skriptID = 106021;
                              }
                              if(myTopic.getID()==10600201){
                             	 skriptID = 106022;
                              }
                              if(myTopic.getID()==10600301){
                             	 skriptID = 106023;
                              }
                              if(myTopic.getID()==10600401){
                             	 skriptID = 106024;
                              }
     						 if(myTopic.getID()==10600501){
     							 skriptID = 106025;
     						 }
                         }
                         myTopic.setY(slotY[slotNummer]);
                         myTopic.setX(slotX[slotNummer]);
                         myTopic.setActive(true);
                         myTopic.setTopicposition(slotNummer);

                         myTopicCursorSprite.setActive(false);
                         myCursorSprite.setActive(false);
                         myCursorSprite = myStandardCursorSprite;
                         myCursorSprite.setActive(true);
                         dragAktiv = false;
                         //skriptID = ((EbaTopicSprite) myTopic).getSkriptID();
                         if (skriptID > 0)
                         {
                              myScriptInterpreter.runScript(skriptID, 1);
                              zeilennummer++;
                         }
                    }
               }
               myDragStatus = STARTDROP;



               break;
          default:
               // rechte Maustaste:
               if (myDataID == "")
               {
               } else
               {
                    if (!isExit && !isGuiElement)
                    {
                         if (myCommand == "")
                         {
                              // TODO: Ansehen-Sktript
                              // myBefehlszeilenText = myCommand;
                              //myCommand = "Посмотри на " + myDataID;
                              //isCommandComplete = true;
                         } else
                         {
                              myBefehlszeilenText = "";
                              myCommand = "";
                              myCommandObject = null;
                         }
                    }
               }
          }
          
          
          if ((!isCommandComplete) && (myDataID != "") && (myDataID != myCommandObjectName))
          {
               myBefehlszeile.change(myBefehlszeilenText, false);
          } else
          {
               myBefehlszeile.change(myBefehlszeilenText, isCommandComplete);
          }
          if (bsInput.isMouseDown(1) && myDragStatus == STARTDRAG && dragAktiv == false )
          {

               Sprite myTmpTopic[];
               myTmpTopic = myTopicSprites.getSprites();
               for (int i = 0; i < myTopicSprites.getSize(); i++)
               {
            	  if (mySprite == null) {
            	  } else {
                    if (((EbaTopicSprite) myTmpTopic[i]).getRaumobjektID() == mySprite.getID())
                    {
                         myTopic = (EbaTopicSprite) myTmpTopic[i];

                         if (myTopic.getTopicposition() > 0)
                         {
                              // do nothing
                         } else
                         {
                              myTopicCursorSprite.setImage(myTopic.getImage());
                              myStandardCursorSprite.setActive(false);
                              myCursorSprite = myTopicCursorSprite;
                              myCursorSprite.setActive(true);
                              dragAktiv = true;
                         }
                    }
            	  }
               }
               myTmpTopic = myTopicSprites.getSprites();
               for (int i = 0; i < myTopicSprites.getSize(); i++)
               {
            	   if (mySprite == null) {
             	  } else {
                    if (((EbaTopicSprite) myTmpTopic[i]) == mySprite)
                    {
                         myTopic = (EbaTopicSprite) myTmpTopic[i];
                         if (myTopic.getTopicposition() > 0)
                         {
                              myTopicCursorSprite.setImage(myTopic.getImage());
                              myStandardCursorSprite.setActive(false);
                              myCursorSprite = myTopicCursorSprite;
                              myCursorSprite.setActive(true);
                              myTopic.setTopicposition(0);
                              myTopic.setActive(false);
                              myTopic.setX(0);
                              myTopic.setY(0);
                              dragAktiv = true;
                         } else
                         {
                         }
                    }
             	  }
               }

               myDragStatus = DRAGGING;
               myTmpTopic = null;
          }
          if (myDragStatus == STARTDROP)
          {
               myDragStatus = DROPPED;
          }

          myPlayField.update(elapsedTime);
     }
     // /////////////////////////////////////////////////////////
     // Private Methoden //
     // /////////////////////////////////////////////////////////
     void updateInventory()
     {
     }
     // /////////////////////////////////////////////////////////
     // ?ffentliche Methoden //
     // /////////////////////////////////////////////////////////
     public void deactivateItem(int inventarObjektID)
     {
          // TODO: deactivateTopic
          updateInventory();
     }
     public void activateItem(int inventarObjektID)
     {
          maximaleItemPosition++;
          // TODO: activateTopic
          updateInventory();
     }
}
