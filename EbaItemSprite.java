package de.daedalic.eba;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.golden.gamedev.object.AnimatedSprite;
@SuppressWarnings("serial")
public class EbaItemSprite extends AnimatedSprite {
	int id;
	String bezeichnung;
	int inventarposition;
	String myDefaultAktion;
	int ansehenSkriptID;
	int benutzenSkriptID;
	int redenMitSkriptID;
	private Statement myStatement;
	private ResultSet myResultSet;

	public EbaItemSprite(BufferedImage[] initImages1, int initX, int initY, int initID, String initBezeichnung, int initInventarposition, String initDefaultAktion, int initAnsehenSkiptID, int initBenutzenSkriptID, int initRedenMitSkriptID) {
		super(initImages1, initX, initY);
		id = initID;
		bezeichnung = initBezeichnung;
		inventarposition = initInventarposition;
		myDefaultAktion = initDefaultAktion;
		ansehenSkriptID = initAnsehenSkiptID;
		benutzenSkriptID = initBenutzenSkriptID;
		redenMitSkriptID = initRedenMitSkriptID;
	}

	public boolean isItem(){
		return true;
	}
	public int getInventarposition() {
		return inventarposition;
	}
	public void setInventarposition(int inventarposition) {
		this.inventarposition = inventarposition;
	}
	public String getMyDefaultAktion() {
		return myDefaultAktion;
	}

	public int getAnsehenSkriptID() {
		return ansehenSkriptID;
	}
	public int getBenutzenSkriptID() {
		return benutzenSkriptID;
	}

	public int getRedenMitSkriptID() {
		return redenMitSkriptID;
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public int getId() {
		return id;
	}
	public boolean hasBenutzenSkript(){
		if(benutzenSkriptID>0){
			return true;
		}else{
			return false;
		}
	}

	public int getInventarBenutzeMitSkriptID(int Item1) {
        int SkriptID = 0;
            try {
                myStatement = EbaGameEngine.myConnection.createStatement();
                myResultSet = myStatement.executeQuery("SELECT * FROM InventarBenutzeMit WHERE InventarobjektID =" + id + " AND ZweiteInventarobjektID =" + Item1);
                if(myResultSet.next()){
                SkriptID = myResultSet.getInt("SkriptID");
                } else {
                	myResultSet = myStatement.executeQuery("SELECT * FROM InventarBenutzeMit WHERE InventarobjektID =" + Item1 + " AND ZweiteInventarobjektID =" + id);
                	myResultSet.next();
                	SkriptID = myResultSet.getInt("SkriptID");
                }
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
        return SkriptID;
    }

	public int getBenutzeMitSkriptID(int Item1) {
        int SkriptID = 0;
            try {
                myStatement = EbaGameEngine.myConnection.createStatement();
                myResultSet = myStatement.executeQuery("SELECT * FROM BenutzeMit WHERE InventarobjektID =" + id + " AND RaumobjektID =" + Item1);
                myResultSet.next();
                SkriptID = myResultSet.getInt("SkriptID");
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
        return SkriptID;
    }

	public void setInventarBenutzeMitSkriptID(int invoID, int skriptID){
		try {
            myStatement = EbaGameEngine.myConnection.createStatement();

            myStatement.executeUpdate("UPDATE Inventarbenutzemit SET SkriptID= "+ skriptID +" where InventarobjektID=" + getID()+ " AND Zweiteinventarobjektid = " + invoID);
            myStatement.executeUpdate("UPDATE Inventarbenutzemit SET SkriptID= "+ skriptID +" where InventarobjektID=" + invoID + " AND Zweiteinventarobjektid = " + getID());

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

	public void setBenutzeMitSkriptID(int roiID, int skriptID){
		try {
            myStatement = EbaGameEngine.myConnection.createStatement();

            myStatement.executeUpdate("UPDATE Benutzemit SET SkriptID= "+ skriptID +" where InventarobjektID=" + getID()+ " AND RaumobjektID = " + roiID);

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

	public void setBenutzenSkriptID(int skriptID) {
        benutzenSkriptID = skriptID;
		try {
                myStatement = EbaGameEngine.myConnection.createStatement();
                myStatement.executeUpdate("UPDATE Inventarobjekt SET BenutzenSkriptID= "+ skriptID +" where ID=" + getID());
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

	public void setAnsehenSkriptID(int skriptID) {
		ansehenSkriptID = skriptID;
        try {
            myStatement = EbaGameEngine.myConnection.createStatement();
            myStatement.executeUpdate("UPDATE Inventarobjekt SET AnsehenSkriptID= "+ skriptID +" where ID=" + getID());

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

	public void setRedenMitSkriptID(int skriptID) {
		redenMitSkriptID = skriptID;
		try {
            myStatement = EbaGameEngine.myConnection.createStatement();
            myStatement.executeUpdate("UPDATE Inventarobjekt SET RedenMitSkriptID= "+ skriptID +" where ID=" + getID());

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

}
