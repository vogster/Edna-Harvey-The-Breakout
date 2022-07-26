package de.daedalic.eba;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import com.golden.gamedev.engine.BaseInput;
import com.golden.gamedev.object.AnimatedSprite;
import com.golden.gamedev.object.Sprite;

public class EbaGuiSkriptOnKlick extends EbaGameObject
{
     private Sprite mySprite;
     private String myDataID;
     private String myBefehlszeilenText = "";
     private String myCommandObjectName = "";
     private String myCommand = "";

     private AnimatedSprite mySpriteBenutzen;
     private boolean isCommandComplete = false;
     private boolean isItem = false;
     private boolean isExit = false;
     private boolean isGuiElement = false;
     private String pressedButton = "";
     private char defaultAktion = '0';
     private int[] walkHere;
     private int walkToX;
     private int walkToY;
     private Sprite myCommandObject;
     private Sprite myCommandObject2;
     Sprite myTmpSprite[];
     public EbaItemSprite[] myItemSprites = new EbaItemSprite[100];
     public EbaGuiSkriptOnKlick(EbaGameEngine myGameEngine)
     {
          super(myGameEngine);
     }
     public void initResources()
     {
          super.initResources();
          // ------------------------------------------------------------
          // ----------- Initialisierung der Schaltfl?chen --------------
          // ------------------------------------------------------------
          mySpriteBenutzen = new AnimatedSprite(new BufferedImage[] { Eba.getImage("gui/edna/b_benutzen.png"), Eba.getImage("gui/edna/b_benutzen_a.png"), Eba.getImage("gui/edna/b_benutzen_p.png") }, 300, 565);
          mySpriteBenutzen.setDataID("Используй ");
          myGuiSprites.add(mySpriteBenutzen);
          myBefehlszeile = new EbaCommandPromptSprite(myActiveFont, myInactiveFont, fontManager, 10, 530);
          myTextSprites.add(myBefehlszeile);
          myScriptInterpreter = new EbaScriptInterpreter(myCharacterSprite, myBackgroundSprites, myObjectSprites, myTextSprites, myGuiSprites, myChoiceList, myEbaGameEngine, this);
          myTmpSprite = new Sprite[myGuiSprites.getSize()];
     }
     public void update(long elapsedTime)
     {
          super.update(elapsedTime);
          // alle guis aus
          // mySpriteNehmen.setActive(false);
          // mySpriteReden.setActive(false);
          mySpriteBenutzen.setActive(false);
          // myInventory.setActive(false);
          // mySpriteAnsehen.setActive(false);
          // mySpriteSchloss.setActive(false);
          defaultAktion = '0';
          if (!myScriptInterpreter.isRunning() && myCharacterSprite.isUnterbrochen())
          {
               isCommandComplete = false;
               myCommand = "";
          }
          if (isItem)
          {
               isItem = false;
          }
          if (isExit)
          {
               isExit = false;
          }
          if (isGuiElement)
          {
               isGuiElement = false;
          }
          if (myScriptInterpreter.isRunning() || myMenueSprites.isActive() || myChoiceList.isActive())
          {
          } else
          {
               // ON MOUSE OVER:
               mySprite = checkPosMouse(myGuiSprites, true);
               if (mySprite == null)
               {
                    mySprite = checkPosMouseInteraction(myObjectSprites, true);
               } else
               {
                    if (mySprite.getID() > 0)
                    {
                         // Nur Inventar-Items d?rfen in der GUI-Sprites
                         // Spritegroup
                         // IDs > 0 bekommen
                         ((AnimatedSprite) mySprite).setFrame(1);
                         isItem = true;
                    } else
                    {
                         isGuiElement = true;
                    }
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
                    if (mySprite.getDataID() == "Выход")
                    {
                         isExit = true;
                    }
                    myDataID = (String) mySprite.getDataID();
               }
               mySpriteBenutzen.setFrame(0);
               if (bsInput.isMouseDown(MouseEvent.BUTTON1))
               {
                    if (myDataID == "Используй ")
                    {
                         pressedButton = "Используй ";
                         mySpriteBenutzen.setFrame(2);
                    } else if (mySpriteBenutzen.getDataID() == pressedButton)
                    {
                         mySpriteBenutzen.setFrame(2);
                    }
               } else
               {
                    if (myDataID == "Используй ")
                    {
                         mySpriteBenutzen.setFrame(1);
                    } else
                    {
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
                              } else if (myCommand.startsWith("Используй "))
                              {
                                   if ((myCommand.startsWith("Используй ")) && myCommand.contains(" c "))
                                   {
                                        myCommand = "";
                                        isCommandComplete = false;
                                        if (myCommandObject == null)
                                        {
                                        } else
                                        {
                                             skriptID = ((EbaItemSprite) myCommandObject2).getBenutzeMitSkriptID(myCommandObject.getID());
                                        }
                                        myScriptInterpreter.runScript(skriptID, 1);
                                        zeilennummer++;
                                   } else
                                   {
                                        myCommand = "";
                                        isCommandComplete = false;
                                        if (myCommandObject == null)
                                        {
                                        } else
                                        {
                                             skriptID = ((EbaInteractionSprite) myCommandObject).getMyBenutzenSkript();
                                        }
                                        myScriptInterpreter.runScript(skriptID, 1);
                                        zeilennummer++;
                                   }
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
               } else
               { // DataID = Objekt
                    if (myCommand == "")
                    {
                         myBefehlszeilenText = myDataID;
                         if (isItem)
                         {
                              if (((EbaItemSprite) mySprite).getMyDefaultAktion().equals("b"))
                              {
                                   mySpriteBenutzen.setFrame(1);
                                   defaultAktion = 'b';
                              }
                              // exit
                         } else if (isExit)
                         {
                              myBefehlszeilenText = "Подойди к " + ((EbaExitSprite) mySprite).getMyMouseoverText();
                         } else if (isGuiElement)
                         {
                              // Do nothing
                         } else
                         {
                              if (((EbaInteractionSprite) mySprite).getMyDefaultAktion().equals("b"))
                              {
                                   mySpriteBenutzen.setFrame(1);
                                   defaultAktion = 'b';
                              }
                         }
                    } else if ((myDataID == "Посмотри на ") || (myDataID == "Используй ") || (myDataID == "Возьми ") || (myDataID == "Поговори с "))
                    {
                         myBefehlszeilenText = myCommand;
                    } else if (myCommandObject2 != null)
                    {
                         if (myCommandObject2.getDataID() == myDataID)
                         {
                              myBefehlszeilenText = myCommand;
                         } else if (isExit)
                         {
                              // do nothing
                         } else
                         {
                              myBefehlszeilenText = myCommand + myDataID;
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
                    if (myDataID == "")
                    {
                    } else
                    {
                         if (isItem)
                         {
                              if (mySprite.getID() > 0)
                              {
                                   if (myCommand == "" || myCommand == "Возьми " || myCommand == "Используй ")
                                   {
                                        if (((EbaItemSprite) mySprite).hasBenutzenSkript())
                                        {
                                             myCommand = "Используй " + mySprite.getDataID();
                                             skriptID = ((EbaItemSprite) mySprite).getBenutzenSkriptID();
                                             myScriptInterpreter.runScript(skriptID, 1);
                                             zeilennummer++;
                                             myCommand = "";
                                        } else
                                        {
                                             myCommand = "Используй " + mySprite.getDataID() + " c ";
                                             myCommandObject2 = mySprite;
                                        }
                                   } else
                                   {
                                        if (!(myCommand.startsWith("Подойди к")))
                                        {
                                             myCommand = myCommand + mySprite.getDataID();
                                             skriptID = ((EbaItemSprite) myCommandObject2).getInventarBenutzeMitSkriptID(mySprite.getID());
                                             myScriptInterpreter.runScript(skriptID, 1);
                                             zeilennummer++;
                                             myCommand = "";
                                        }
                                   }
                              }
                         } else if (mySprite.getDataID() == "Используй ")
                         {
                              myCommand = "Используй ";
                         } else
                         { // ----mySprite ist ein Objekt--------
                              if (myCommand != "" && isExit)
                              {
                                   // do nothing
                              } else
                              {
                                   myCommandObject = mySprite;
                                   if (!isItem)
                                   {
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
                                        if (isExit)
                                        {
                                             myCommand = myBefehlszeilenText;
                                             myCommandObjectName = ((EbaExitSprite) mySprite).getMyMouseoverText();
                                        } else
                                        {
                                             // benutzen
                                             if (myCommand == "")
                                             {
                                                  myCommand = "Используй " + myDataID;
                                             } else
                                             {
                                                  if (isCommandComplete)
                                                  {
                                                  } else
                                                  {
                                                       myCommand = myCommand + myDataID;
                                                  }
                                             }
                                             myCommandObjectName = myDataID;
                                        }
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
                    if (myDataID == "")
                    {
                         myBefehlszeilenText = "";
                         myCommand = "";
                    } else
                    {
                         // TODO: Default-Aktion des Objekts ?
                         // myBefehlszeilenText = myCommand;
                    }
               }
               // ON MOUSE RELEASED:
               switch (bsInput.getMouseReleased())
               {
               case BaseInput.NO_BUTTON:
                    break;
               case MouseEvent.BUTTON1:
                    // linke Maustaste:
                    pressedButton = "";
                    if (myDataID == "Посмотри на ")
                    {
                         myCommand = "Посмотри на ";
                    } else if (myDataID == "Возьми ")
                    {
                         myCommand = "Возьми ";
                    } else if (myDataID == "Поговори с ")
                    {
                         myCommand = "Поговори с ";
                    } else if (myDataID == "Используй ")
                    {
                         myCommand = "Используй ";
                    }
                    break;
               default:
                    // rechte Maustaste:
                    if (myDataID == "")
                    {
                    } else
                    {
                         if (!isItem)
                         {
                              if (myCommand == "")
                              {
                                   if (defaultAktion != '0')
                                   {
                                        if (defaultAktion == 'b')
                                        {
                                             myCommand = "Используй " + myDataID;
                                             isCommandComplete = true;
                                        }
                                        myBefehlszeilenText = myCommand;
                                        myCommandObject = mySprite;
                                        if (((EbaInteractionSprite) mySprite).getWalkToX() < 0)
                                        {
                                             walkToX = (int) mySprite.getX();
                                             walkToY = (int) mySprite.getY();
                                        } else
                                        {
                                             walkToX = ((EbaInteractionSprite) mySprite).getWalkToX();
                                             walkToY = ((EbaInteractionSprite) mySprite).getWalkToY();
                                        }
                                        myEbaWalkableAreaMap.findWay((int) myCharacterSprite.getBaseX(), (int) myCharacterSprite.getBaseY(), walkToX, walkToY);
                                        walkHere = (int[]) myEbaWalkableAreaMap.pop();
                                        myCharacterSprite.walkTo(walkHere[0], walkHere[1], ((EbaInteractionSprite) mySprite).getStandbyBlickrichtung());
                                   }
                              } else
                              {
                                   myBefehlszeilenText = "";
                                   myCommand = "";
                                   myCommandObject = null;
                                   myCommandObject2 = null;
                              }
                         } else
                         {
                              if (defaultAktion != '0')
                              {
                                   if (defaultAktion == 'b')
                                   {
                                        if (((EbaItemSprite) mySprite).hasBenutzenSkript())
                                        {
                                             skriptID = ((EbaItemSprite) mySprite).getBenutzenSkriptID();
                                        } else
                                        {
                                             myCommand = "Используй " + myDataID + " c ";
                                             myCommandObject2 = mySprite;
                                        }
                                   }
                                   myScriptInterpreter.runScript(skriptID, 1);
                                   zeilennummer++;
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
          }
     }
}