package de.daedalic.eba;

import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.SpriteGroup;

public class EbaSpriteGroup extends SpriteGroup {

	private Sprite[] myTmpSprite;


	public EbaSpriteGroup(String initName) {
		super(initName);
	}


	public Sprite getSprite(int id){
		myTmpSprite = this.getSprites();
        for (int i = 0; i < this.getSize(); i++) {
            if (myTmpSprite[i].getID() == id) {
            	return myTmpSprite[i];
            }else{
            }
        }
        return null;
	}

	public Sprite getSprite(String dataId){
		myTmpSprite = this.getSprites();
        for (int i = 0; i < this.getSize(); i++) {
            if (myTmpSprite[i].getDataID() == dataId) {
                return myTmpSprite[i];
            }
        }
        return null;
	}

}
