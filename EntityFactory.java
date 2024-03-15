package ressurser.chunkSystem.terrainGeneration;

import java.util.Random;

import ressurser.baseEntity.BaseEntity;
import ressurser.baseEntity.gameObject.GameObject;
import ressurser.baseEntity.tile.Tile;
import ressurser.main.GamePanel;

public class EntityFactory {

    ProceduralGeneration proceduralGen;
    GamePanel panel;

    public EntityFactory(ProceduralGeneration proceduralGen,GamePanel panel){
        this.proceduralGen = proceduralGen;
        this.panel = panel;
    }



    public GameObject getEntity(int worldX,int worldY){
        String biomeType =  proceduralGen.calculateBiomeString(worldX, worldY);
        int height = (int)(proceduralGen.getHeightValue(worldX,worldY)*1000);

        double vegetationVal = proceduralGen.getVegetationVal(worldX,worldY);
        double vegetationMidFreq = proceduralGen.getVegetationMidFreq(worldX,worldY);
        double vegetationHighFreq = proceduralGen.getVegetationHighFreq(worldX,worldY);



        
            int c;
            int b;
        
            Random r = new Random( (worldX*10000) + (worldY*100));
            b = (int)(r.nextDouble()*40);
            Random r2 = new Random( (worldY*100) + (worldX*10000));
            c = (int)(r2.nextDouble()*40);
            
            
            

        //vegatation
        if (biomeType!= "ocean" && biomeType!= "beach"){
            if (vegetationMidFreq>-0.5){
                if (vegetationHighFreq>0.4){
                    GameObject tree = new GameObject(panel,"highGreenTree",worldX+b-panel.tileSize+16,worldY+c-panel.tileSize*3+16,(short)(3*panel.tileSize),(short)(4*panel.tileSize),(short)(32),(short)(32),(short)(panel.tileSize+16),(short)(panel.tileSize*3+16),true);
                    tree.light = 20;
                    return tree;
                } //if (vegetationHighFreq>0.3){
                  //  return new GameObject(panel,"greenShrub",worldX+b,worldY+c,(short)(panel.tileSize*5),(short)(panel.tileSize*5),(short)(64),(short)(64),(short)(16),(short)(16),true);
                //}
            }
        }
        
        return null;
    }

}
