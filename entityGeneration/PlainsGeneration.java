package ressurser.chunkSystem.terrainGeneration.entityGeneration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import ressurser.baseEntity.gameObject.GameObject;
import ressurser.baseEntity.tile.Tile;
import ressurser.chunkSystem.terrainGeneration.ProceduralGeneration;
import ressurser.main.GamePanel;


import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class PlainsGeneration {
    
    GamePanel panel;
    EntityFactory entityFactory;
    ProceduralGeneration proceduralGen;
    public PlainsGeneration(GamePanel panel,EntityFactory entityF){
        this.panel = panel;
        this.entityFactory = entityF;
        proceduralGen = entityFactory.proceduralGen;
        drawVegetationScatter();
    }

    /**the interface of this object. */
    public GameObject getPlainsEntity(int x,int y){
        TreeFactory tf = new TreeFactory(); //TODO this should be universal
        VegetationFactory vf = new VegetationFactory();
        if (treePlacement(x,y,1000)){

            return tf.createTree(TreeTypes.OAK_M,x,y,panel);
        }
        if (bushPlacement(x,y,1001)){
            return vf.createBush(BushTypes.GREENBUSH_M,x,y,panel);
        }

        
        //the densityTreshold tells whenever a tree should be placed - it 
        //double treeDensitityTreshold = Math.abs(lowFreq*0.6) + Math.abs(midFreq*0.3) + Math.abs(highFreq*0.3);
        
            return null;
        

    }

    /**
     * this method only returns different kinds of the grass tile.
     */
    public Tile getPlainsTile(int x,int y){
       return getGrassTile(x,y);
       
    }

    private Tile getGrassTile(int x,int y){
        //height for Tile information
        int height = (int)(proceduralGen.getHeightValue(x,y)*1000);

        //vegetation inormality
        double value = proceduralGen.getVegetationHighFreq(x,y)+1;
        double intervals = 3;
        
        int number = (int)(value*intervals);
        number ++;
        
        if (number>6){number = 6;}

        String streng = "grass"+number;
        if (number <2){streng = "grass";}

        return new Tile(panel,streng,x,y,height);
    }

    /** */
    private boolean treePlacement(int x,int y,int seed){
        return  vegetationplacement(x ,y, 1.0, 1.0, 0.01, 0.1,seed,0.85) ||staticScatter(x*64,y*64,1,seed,0.9);
    }

    private boolean bushPlacement(int x,int y,int seed){
        return  vegetationplacement(x ,y, 1.0, 1.0, 0.01, 0.1,seed,0.87) ||staticScatter(x*64,y*64,1,seed,0.8);
    }


    /**
     * trendFreq = 0.002
     * baseFreq = 0.3
     * moist = 0.4
     * 
     */
    private boolean vegetationplacement(int x,int y,double moistKoeffisient,double heightKoeffiesient,double baseFreq,double trendFreq,int number,double treshold){

        double lowFreq = proceduralGen.getVegetationVal(x,y);
        double midFreq = proceduralGen.getVegetationMidFreq(x,y);
        double highFreq = proceduralGen.getVegetationHighFreq(x,y);
        
        double moistness = proceduralGen.getMoistValue(x,y);
        double height = proceduralGen.getHeightValue(x,y);

        //hight moist == likely for vegetation
        //high height == non likely for vegetation
        double largerDensityVariable =  Math.abs(proceduralGen.getNoiseValue(100,x,y,trendFreq,5));
        double randomDensityTreshold = Math.abs(lowFreq*0.6) + Math.abs(midFreq*0.3) + Math.abs(highFreq*0.3);

        double treshold2 = treshold*(randomDensityTreshold*0.6- 0.4*moistness +height*0.1 + largerDensityVariable*0.3 + 0.3);

        //density of all tries of generating a vegetation
        double tryDensity = proceduralGen.getNoiseValue(1001,x,y,0.3,1);

        return (tryDensity>(treshold2));
    }


    /**@param treshold should be around 0.9 */
    private boolean staticScatter(int x,int y,double baseFreq,int seed,double treshold){
        return proceduralGen.getNoiseValue(seed,x,y,(baseFreq),1)>treshold;
    }

    /**@param treshold should be around 0.9 */
    private boolean staticScatterWithTrend(int x,int y,double baseFreq,double trendFreq,int seed,double treshold){
        double largerDensityVariable =  Math.abs(proceduralGen.getNoiseValue(seed,x,y,trendFreq,1));
        return proceduralGen.getNoiseValue(seed,x,y,(baseFreq),1)*0.8+largerDensityVariable*0.2>treshold*treshold;
    }
    
    /**help tool to draw the scattering of vegetation
     * helps with modeling and looks at a large area at a time.
     */
    private void drawVegetationScatter() {
        int size = 500;
        System.out.println(size);
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        
        for (int x = -size/2; x < size/2; x++) {
            for (int y = -size/2; y < size/2; y++) {
                boolean vegPlacement;
                boolean bushPlacement = staticScatterWithTrend(x*64,y*64,1,0.002,1,0.88);
                vegPlacement = treePlacement(x*64, y*64,1);
                bushPlacement = bushPlacement(x*64,y*64,2);
                double height = proceduralGen.getHeightValue(x*64, y*64);
                Color color;
                if (height < 0) {
                    color = Color.BLUE;
                } else {
                    
                    if (vegPlacement){color = Color.GREEN;} else if(bushPlacement){color = Color.BLUE;}
                        else {color = Color.WHITE;}
                    
                    
                   
                }
                image.setRGB(x + size/2, y + size/2, color.getRGB());
                
            }
        }
        
        try {
            ImageIO.write(image, "png", new File("vegetation_scatter.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /*
    private GameObject plainsGeneration2(int x,int y){
        TreeFactory tf = new TreeFactory(); //TODO this should be universale
        int height = (int)(proceduralGen.getHeightValue(x,y)*1000);
        double lowFreq = proceduralGen.getVegetationVal(x,y);
        double midFreq = proceduralGen.getVegetationMidFreq(x,y);
        double highFreq = proceduralGen.getVegetationHighFreq(x,y);

        

        double densityTreshold = Math.abs(lowFreq*0.4) + Math.abs(midFreq*0.3) + Math.abs(highFreq*0.3);
        //density treshold could be anywhere between 0 and 1

        ArrayList<Double> noiseSet = proceduralGen.getNoiseValueSet(x,y,0.1,5);
        //needs certain kinds of trees
        //needs certain kinds of bushes, sticks, rocks etc..
        //use a certain kind of density


        double placementProb = Math.abs(proceduralGen.getAnyNoiseValue(2,x,y,0.1,1));
        

        System.out.println(noiseSet.get(0));
       
        //check for certain kind of vegetation
        if (noiseSet.get(0) > 0.5){
           // certain kind of trees? trees;
            System.out.println("density"+densityTreshold);
            System.out.println("placeprob"+placementProb);

           double probability = densityTreshold;
           System.out.println("probability"+probability);
           System.out.println("noooo"+probability);
           if (probability> 0.8){
            System.out.println("yeahhhhh"+probability);

                // figure out what tree to choose from a certain cluster:

                double probabilitydistribution = Math.abs(proceduralGen.getAnyNoiseValue(5,x,y,0.5,1));

                if (probabilitydistribution < 0.3){
                    return tf.createTree(TreeTypes.OAK_M,x,y,panel);

                } else if (probabilitydistribution < 0.6){
                    return tf.createTree(TreeTypes.OAK_M,x,y,panel);

                } else {
                    return tf.createTree(TreeTypes.OAK_M,x,y,panel);
                }

                }
        }

        //check for certain kind of vegetation
        //value below is the probaiblity of trying to create given entity
        if (noiseSet.get(1) > 0.5){
            // certain kind of trees? trees;
 
            double probability =  densityTreshold;
            if (probability> 0.8){
 
                // figure out what tree to choose from a certain cluster:

                double probabilitydistribution = Math.abs(proceduralGen.getAnyNoiseValue(5,x,y,0.5,1));

                if (probabilitydistribution < 0.3){
                    return tf.createTree(TreeTypes.BIRCH_M,x,y,panel);

                } else if (probabilitydistribution < 0.6){
                    return tf.createTree(TreeTypes.BIRCH_M,x,y,panel);

                } else {
                    return tf.createTree(TreeTypes.BIRCH_M,x,y,panel);
                }
            }
         }

          //check for certain kind of vegetation
        //value below is the probaiblity of trying to create given entity
        if (noiseSet.get(3) > 0.5){
            // certain kind of trees? trees;
 
            double probability =  densityTreshold;
            if (probability> 0.8){
 
                // figure out what tree to choose from a certain cluster:

                double probabilitydistribution = Math.abs(proceduralGen.getAnyNoiseValue(5,x,y,0.5,1));

                if (probabilitydistribution < 0.3){
                    return tf.createTree(TreeTypes.SPRUCE_M,x,y,panel);

                } else if (probabilitydistribution < 0.6){
                    return tf.createTree(TreeTypes.SPRUCE_M,x,y,panel);

                } else {
                    return tf.createTree(TreeTypes.SPRUCE_M,x,y,panel);
                }
            }
         }

          //check for certain kind of vegetation
        //value below is the probaiblity of trying to create given entity
        if (noiseSet.get(2) > 0.5){
            // certain kind of trees? trees;
 
            double probability =  densityTreshold;
            if (probability> 0.1){
 
                // figure out what tree to choose from a certain cluster:

                double probabilitydistribution = Math.abs(proceduralGen.getAnyNoiseValue(5,x,y,0.5,1));

                if (probabilitydistribution < 0.3){
                    return tf.createTree(TreeTypes.PINE_M,x,y,panel);

                } else if (probabilitydistribution < 0.6){
                    return tf.createTree(TreeTypes.PINE_M,x,y,panel);

                } else {
                    return tf.createTree(TreeTypes.PINE_M,x,y,panel);
                }
            }
         }

        return null;


    } */
}
