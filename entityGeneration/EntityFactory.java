package ressurser.chunkSystem.terrainGeneration.entityGeneration;

import java.util.Random;

import javax.imageio.ImageIO;

import ressurser.baseEntity.BaseEntity;
import ressurser.baseEntity.gameObject.GameObject;
import ressurser.baseEntity.tile.Tile;
import ressurser.chunkSystem.terrainGeneration.ProceduralGeneration;
import ressurser.main.GamePanel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;





public class EntityFactory {

    ProceduralGeneration proceduralGen;
    GamePanel panel;
    PlainsGeneration pg;

    public EntityFactory(ProceduralGeneration proceduralGen,GamePanel panel){
        this.proceduralGen = proceduralGen;
        this.panel = panel;
        this.pg = new PlainsGeneration(panel,this);
    }


    public GameObject getEntity(int worldX,int worldY){


        //figure out what biome:
        String biomeType =  proceduralGen.calculateBiomeString(worldX, worldY);

        switch (biomeType){
            case ("ocean"):

                return null;
            case("beach"):

                break;
            case("forest"):
            return pg.getPlainsEntity(worldX,worldY);
            case("plains"):
            return pg.getPlainsEntity(worldX,worldY);
            case("savannah"):
            return pg.getPlainsEntity(worldX,worldY);
        }
        return pg.getPlainsEntity(worldX,worldY);


            

        
        //return null;
    }


    

}
