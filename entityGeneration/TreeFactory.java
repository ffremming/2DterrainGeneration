package ressurser.chunkSystem.terrainGeneration.entityGeneration;

import ressurser.baseEntity.gameObject.GameObject;
import ressurser.main.GamePanel;

public class TreeFactory {
    
    public GameObject createTree(TreeTypes treeType,int x,int y,GamePanel panel){

        boolean solid = true;
        int worldX = x;
        int worldY = y;

        GameObject tree = null;

        switch(treeType){
            case BIRCH_M:
            tree = new GameObject(panel,"birch_M",worldX-panel.tileSize+16,worldY-panel.tileSize*3+16,(short)(3*panel.tileSize),(short)(4*panel.tileSize),(short)(32),(short)(32),(short)(panel.tileSize+16),(short)(panel.tileSize*3+16),true);
                break;
            case OAK_M:
            tree = new GameObject(panel,"oak_M",worldX-panel.tileSize+16,worldY-panel.tileSize*3+16,(short)(3*panel.tileSize),(short)(4*panel.tileSize),(short)(32),(short)(32),(short)(panel.tileSize+16),(short)(panel.tileSize*3+16),true);
             
                break;
            case PINE_M:
            tree = new GameObject(panel,"shrub_M",worldX-panel.tileSize+16,worldY-panel.tileSize*3+16,(short)(3*panel.tileSize),(short)(4*panel.tileSize),(short)(32),(short)(32),(short)(panel.tileSize+16),(short)(panel.tileSize*3+16),true);

                break;
            case SPRUCE_M:
            tree = new GameObject(panel,"spruce_M",worldX-panel.tileSize+16,worldY-panel.tileSize*3+16,(short)(3*panel.tileSize),(short)(4*panel.tileSize),(short)(32),(short)(32),(short)(panel.tileSize+16),(short)(panel.tileSize*3+16),true);

                break;
            default:
           
                break;
            
            
               
            
        }
        if (tree == null)
            return null;
        
        tree.light = 20;

        return tree;
        
    }


}
