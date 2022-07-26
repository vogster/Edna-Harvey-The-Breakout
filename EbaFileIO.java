package de.daedalic.eba;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

public class EbaFileIO
{

	static String quellVerzeichnis;
	static String zielVerzeichnis;

	private static Statement myStatement = null;
	private static ResultSet myResultSet = null;
	public static Connection myLoadConnection = null;

	static final String dbUser = "sa";
	static final String dbPassword = "";
	static final String dbPath = "jdbc:hsqldb:file:EbaSaveGame/"; // Windows

	//static final String dbPath = "jdbc:hsqldb:file:/home/felix/workspace/Edna bricht aus/EbaSaveGame/"; //Linux

	private static int myNextRoomID;


	public static void ladeSaveGame(int Spielstand,
			EbaGameEngine myEbaGameEngine, EbaGameObject myEbaGameObject)
	{
		ladeContent(Spielstand);
		
		myEbaGameEngine.walkInPointX = Preferences.userRoot().getInt("characterx"+Spielstand, 400);
		myEbaGameEngine.walkInPointY = Preferences.userRoot().getInt("charactery"+Spielstand, 520);
		myEbaGameEngine.charakterBlickrichtung = Preferences.userRoot().get("characterBlickrichtung"+Spielstand, "w");
		myEbaGameEngine.nextGameID = Preferences.userRoot().getInt("RaumID"+Spielstand, 100101);
		
		myEbaGameEngine.refreshConnection();
		myEbaGameObject.finish();

	}

	public static void ladeSaveGameOld(int Spielstand,
			EbaGameEngine myEbaGameEngine, EbaGameObject myEbaGameObject)
	{
		ladeContent(Spielstand);
		try
		{

			Class.forName("org.hsqldb.jdbcDriver");
			myLoadConnection = DriverManager.getConnection(dbPath, dbUser,
					dbPassword);

			myStatement = myLoadConnection.createStatement();
			myResultSet = myStatement
					.executeQuery("SELECT * FROM SAVEGAME WHERE ID="
							+ Spielstand);
			myResultSet.next();

			myEbaGameEngine.walkInPointX = myResultSet.getInt("characterx");
			myEbaGameEngine.walkInPointY = myResultSet.getInt("charactery");
			myEbaGameEngine.charakterBlickrichtung = myResultSet
					.getString("charakterBlickrichtung");
			myNextRoomID = myResultSet.getInt("RaumID");


		} catch (NumberFormatException e)
		{

			e.printStackTrace();
		} catch (SQLException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			if (myResultSet != null)
			{
				try
				{
					myResultSet.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
				myResultSet = null;
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
			if (myLoadConnection != null){
				try
				{
					myLoadConnection.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
				myLoadConnection = null;
			}
		}


		myEbaGameEngine.nextGameID = myNextRoomID;
		myEbaGameEngine.refreshConnection();
		myEbaGameObject.finish();

	}
	
	public static void speicherSaveGame(int Spielstand,
			EbaGameEngine myEbaGameEngine, EbaGameObject myEbaGameObject)
	{

		speicherContent(Spielstand, myEbaGameObject);
		
		Preferences.userRoot().putInt("characterx"+Spielstand, (int)myEbaGameObject.myCharacterSprite.getBaseX());
		Preferences.userRoot().putInt("charactery"+Spielstand, (int)myEbaGameObject.myCharacterSprite.getBaseY());
		Preferences.userRoot().put("charakterBlickrichtung"+Spielstand, "w");
		Preferences.userRoot().put("Bezeichnung"+Spielstand, "Game"+Spielstand);
		Preferences.userRoot().putInt("RaumID"+Spielstand, myEbaGameEngine.getMyRoomNumber());	
		
	}
	
	
	public static void speicherSaveGameOLD(int Spielstand,
			EbaGameEngine myEbaGameEngine, EbaGameObject myEbaGameObject)
	{
		//System.out.print( "Engine :" +myEbaGameEngine );
		//System.out.print( "Object :" +myEbaGameObject );
		speicherContent(Spielstand, myEbaGameObject);
		try
		{

			Class.forName("org.hsqldb.jdbcDriver");
			myLoadConnection = DriverManager.getConnection(dbPath, dbUser, dbPassword);
			myStatement = myLoadConnection.createStatement();
			myStatement.executeUpdate("UPDATE savegame SET characterx="+myEbaGameObject.myCharacterSprite.getBaseX()+" where ID=" + Spielstand);
			myStatement.executeUpdate("UPDATE savegame SET charactery="+myEbaGameObject.myCharacterSprite.getBaseY()+" where ID=" + Spielstand);
			myStatement.executeUpdate("UPDATE savegame SET charakterBlickrichtung='w' where ID=" + Spielstand);
			String Spielstandname = "Game"+Spielstand;
			myStatement.executeUpdate("UPDATE savegame SET Bezeichnung='"+Spielstandname+"'  where ID=" + Spielstand);
			myStatement.executeUpdate("UPDATE savegame SET RaumID="+myEbaGameEngine.getMyRoomNumber()+" where ID=" + Spielstand);

		} catch (NumberFormatException e)
		{

			e.printStackTrace();
		} catch (SQLException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
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
			if (myLoadConnection != null)
			{
			try
			{
				myLoadConnection.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
			myLoadConnection = null;
			}

		}
	}


	public static void ladeContent(int spielstand)
	{
		switch (spielstand)
		{

		case 0:
			quellVerzeichnis = "EbaStart/";
			break;
		case 1:
			quellVerzeichnis = "EbaSaveGame1/";
			break;
		case 2:
			quellVerzeichnis = "EbaSaveGame2/";
			break;
		case 3:
			quellVerzeichnis = "EbaSaveGame3/";
			break;
		case 4:
			quellVerzeichnis = "EbaSaveGame4/";
			break;
		case 5:
			quellVerzeichnis = "EbaSaveGame5/";
			break;
		case 6:
			quellVerzeichnis = "EbaSaveGame6/";
			break;
		case 7:
			quellVerzeichnis = "EbaSaveGame7/";
			break;
		case 8:
			quellVerzeichnis = "EbaSaveGame8/";
			break;
		case 9:
			quellVerzeichnis = "EbaSaveGame9/";
			break;
		}
		File dir = new File(quellVerzeichnis);
		File[] entries = dir.listFiles();
		for (int i = 0; i < entries.length; i++)
		{
			copyFile(entries[i].toString(), "EbaData/" + entries[i].getName());
		}
	}

	public static void speicherContent(int spielstand, EbaGameObject myEbaGameObject)
	{

		switch (spielstand)
		{

		case 1:
			zielVerzeichnis = "EbaSaveGame1/";
			break;
		case 2:
			zielVerzeichnis = "EbaSaveGame2/";
			break;
		case 3:
			zielVerzeichnis = "EbaSaveGame3/";
			break;
		case 4:
			zielVerzeichnis = "EbaSaveGame4/";
			break;
		case 5:
			zielVerzeichnis = "EbaSaveGame5/";
			break;
		case 6:
			zielVerzeichnis = "EbaSaveGame6/";
			break;
		case 7:
			zielVerzeichnis = "EbaSaveGame7/";
			break;
		case 8:
			zielVerzeichnis = "EbaSaveGame8/";
			break;
		case 9:
			zielVerzeichnis = "EbaSaveGame9/";
			break;
		}
		File dir = new File("EbaData/");
		File[] entries = dir.listFiles();
		for (int i = 0; i < entries.length; i++)
		{
			copyFile(entries[i].toString(), zielVerzeichnis
					+ entries[i].getName());
		}

		try	{
			zielVerzeichnis = (zielVerzeichnis.substring(0, 12));
			File file = new File("EbaSaveGame/" +zielVerzeichnis.substring(0,12) + ".png");
			ImageIO.write(myEbaGameObject.getScreenshotScaled(), "png", file);
		}
		catch (IOException e){
		}



	}

	public static void copyFile(String source, String destination)
	{
		//System.out.println("Kopiere: "+source+" nach: "+destination);
		try
		{
			File mySource = new File(source);
			int len = 32768;
			byte[] buff = new byte[(int) Math.min(len, mySource.length())];
			FileInputStream fis;

			fis = new FileInputStream(mySource);

			FileOutputStream fos = new FileOutputStream(destination);
			while (0 < (len = fis.read(buff)))
				fos.write(buff, 0, len);
			fos.flush();
			fos.close();

			fis.close();
		} catch (FileNotFoundException e)
		{

		} catch (IOException e)
		{

		}
	}

	public static String getBezeichnung(int Spielstand)
	{
		File dir = new File("EbaData/");
		File[] checkFiles = dir.listFiles();
		
		String myBezeichnung = Preferences.userRoot().get("Bezeichnung"+Spielstand, "Leer");
		
		for (int i = 0; i < checkFiles.length; i++)
		{
			File me = new File("EbaSaveGame"+Spielstand+"/"+checkFiles[i].getName());
			if (!me.exists()) {
				myBezeichnung = "Leer";
			}
		}
		return myBezeichnung;
	}
	
	public static String getBezeichnungOLD(int Spielstand)
	{
		String bezeichnung = "";

		try
		{

			Class.forName("org.hsqldb.jdbcDriver");
			myLoadConnection = DriverManager.getConnection(dbPath, dbUser,
					dbPassword);

			myStatement = myLoadConnection.createStatement();
			myResultSet = myStatement
					.executeQuery("SELECT * FROM SAVEGAME WHERE ID="
							+ Spielstand);
			myResultSet.next();

			bezeichnung = myResultSet.getString("Bezeichnung");

		} catch (NumberFormatException e)
		{

			e.printStackTrace();
		} catch (SQLException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			if (myResultSet != null)
			{
				try
				{
					myResultSet.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
				myResultSet = null;
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
			if (myLoadConnection != null){
				try
				{
					myLoadConnection.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
				myLoadConnection = null;
			}
		}
		return bezeichnung;
	}

}
