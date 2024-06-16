package ressurser.chunkSystem.terrainGeneration.entityGeneration;

import ressurser.baseEntity.gameObject.GameObject;
import ressurser.main.GamePanel;

public class VegetationFactory {


    public GameObject createBush(BushTypes bushType,int x,int y,GamePanel panel){

        boolean solid = true;
        int worldX = x;
        int worldY = y;

        

        switch(bushType){
            case GREENBUSH_M:
            return new GameObject(panel,"wildGrass",worldX,worldY,(panel.tileSize),panel.tileSize,64,64,panel.tileSize,panel.tileSize,false);
                
            default:
           
                break;
            
            
               
            
        }
        return null;
        
    }
}
