package ressurser.chunkSystem.terrainGeneration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javax.imageio.ImageIO;



import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ProceduralGeneration {
    
    static double avg = 0;
    static int amount = 0;
    static double sumValue = 0;

     int seed;
     int moistSeed;
     int tempSeed;
     int TILESIZE = 32;
     int continentalSeed;
     int peaksSeed;
     int vegetationSeed1;
     int vegetationSeed2;
     int vegetationSeed3;

   


    // koefficent
    double heightKoefficent = 1.5;
    double temperatureKoefficent = 0.2;
    double moistKoefficent = 2;


    //frequenzy:

    double standardFrequency = 0.008/24;
    final double RIVERFREQUENZY =  0.03/24;
    double moistFrequency = 0.05 /24;
    double heatFrequency = 0.004/24;
    double continentalFreq = 0.0008/24;
    double peaksFreq = 0.4/24;

    //rects of biomes
    ArrayList<Biome> biomeRects = new ArrayList<>();
    HashMap <Biome,Integer> colorMap = new HashMap<>();

    ArrayList<Biome> allBiomes = new ArrayList<>();

    //boolean modes
    boolean hightColor = false;

    //colors
    private int green = 0xA0C080;
	private int darkGreen = 0x408000;
	private int sand = 0xffe7c3;
	private int moss = 0x4E4E35;
	private int water = 0x0000ff;;
	private int mountain =0x808080 ;
	private int ice = 0xBFEFFF;
	private int savannaC = 0xCDBE70;
	private int sForest = 0x629632;
	private int rainFr = 0x78AB46;
	private int black =  0x000000;
    private int snowy =  0xffffff;

    //lvls
    double oceanLvl = 0;
    double beachLvl = 0.04;
    double shoreLvl = 0.07;
    double mountainLvl = 0.97;

    //global biomes:

    Biome beach;
    Biome ocean;
    Biome mountainn;
    Biome wetBeach;


    public ProceduralGeneration(){
        //Setting a random seed
        newSeed();

        

        

        //setup
        setupBiomes();
        

    }
    
    
    
    /**
     * iterates thought
     * @param worldX takes in real coord - converts it into tileCoord in function
     * @param iteration defines how many iterations the agorithms run. more is more detailed and sharp edges, more noise in edges. less is smoother.
     * 
     */
    private double getNoiseValue(long seed,double frequency,int worldX,int worldY,int iterations){

        int x = worldX/32;
        int y = worldY/32;

        double value = 0;

        //multiplier decreases by half every iteration
        double multiplier = 1;
        //size is how large the value is compared to original.
        double size = 0;
        
        for (int i = 0;i<iterations;i++){
            value += OpenSimplex2S.noise2_ImproveX(seed, x * frequency, y * frequency)*multiplier;
            frequency*=2;
            size += multiplier;
            multiplier/=2;
        }
        value = value/size;

        sumValue+= value;
        amount++;
       

        
		return value;
	}



    /**
     * @param worldX is real coordinates
     * 
     * function produces moist and heat noice, and returns the corresponding biome.
     * define frequency and seed inside.
     * 
     * can be used to get the right biome for generation
     */
    private Biome getBiome(int worldX,int worldY){
        //producing the moist noicevalue
        double moistValue = getMoistValue(worldX,worldY);

        //producing the heat noicevalue
        double heatValue = getTemperatureValue(worldX,worldY);
        
        double heightValue = getHeightValue(worldX,worldY);
        
        return getBiomeFromList(heatValue,moistValue,heightValue);

    }

    /**
     * searches thorught all biomes and finds out where the point is.
     * @throws Exception
     */
    private Biome getBiomeFromList(double heat,double moist,double height) {

        int heat2 = (int)(heat*50);
        int moist2 = (int)(moist*50);

        Point p = new Point(heat2,moist2);
        
        if (height<oceanLvl){
            //TODO
            //create a method that distributes ocean biome values...
            return ocean;
        }

        else if (height< beachLvl){
             //TODO
            //create a method that distributes shore biome values...
            return wetBeach;
        } else if (height<shoreLvl){
            
            return beach;
           
        }

        for (Biome biome:biomeRects){
            if (biome.contains(p)){
                return biome;
            }
        }
       
        //System.out.println("returning null, heat: "+ heat + ", moist: "+moist+", height: "+height);
        //TODO something wrong with this function/module
        return null;
      
        
    }
    /**
     * helper function.
     */
    private void drawBiomes(){

        BufferedImage image =  new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);;
        
        for (int temperature = -50;temperature<50;temperature ++){
            for (int moist = -50;moist<50;moist ++){
                 if (getBiomeFromList((double)temperature/50,(double)moist /50,1) != null){
                        int rgb = colorMap.get(getBiomeFromList((double)temperature/50,(double)moist /50,1));
                        image.setRGB(temperature +50, moist +50, rgb);
                    
            }
        }
    }
     try {
        ImageIO.write(image, "png", new File("biomes.png"));
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
        
    }


    private void setupBiomes(){
    Biome snowyTundra = new Biome(-51,-51,-25,0,"Snowy Tundra",snowy);
    Biome plains = new Biome(-25,-51,10,0,"grass",green);
    Biome desert = new Biome(10,-51,51,-10,"desert",sand);
    Biome snowyTaiga = new Biome(-51,0,-25,51,"snowy taiga",snowy);
    Biome seasonalForest = new Biome(-25,0,0,51,"seasonal forest",sForest);
    Biome savanna = new Biome(0,-10,51,10,"savanna",savannaC);
    Biome forest = new Biome(0,10,51,25,"forest",darkGreen);
    Biome swamp = new Biome(0,25,20,51,"plains",moss);
    Biome rainForest = new Biome(20,25,51,51,"rain forest",rainFr);



    ocean = new Biome(-1,oceanLvl,"ocean",water);
    beach = new Biome(oceanLvl,beachLvl,"beach",sand);
    wetBeach = new Biome(oceanLvl,beachLvl,"wetBeach",sand);
    mountainn = new Biome(1,0.85,"mountain",mountain);
   
    //others:
    

    biomeRects.add(snowyTundra);
    biomeRects.add(plains);
    biomeRects.add(desert);
    biomeRects.add(snowyTaiga);
    biomeRects.add(seasonalForest);
    biomeRects.add(savanna);
    biomeRects.add(forest);
    biomeRects.add(swamp);
    biomeRects.add(rainForest);
   

    

    colorMap.put(snowyTundra,ice);
    colorMap.put(snowyTaiga,ice);
    colorMap.put(plains,green);
    colorMap.put(desert,sand);
    colorMap.put(snowyTundra,ice);
    colorMap.put(seasonalForest,sForest);
    colorMap.put(savanna,savannaC);
    colorMap.put(forest,darkGreen);
    colorMap.put(rainForest,rainFr);
    colorMap.put(swamp,moss);

    

    }


    /**
     * retrieves the image to file
     */
    private void paintMap(int startX,int startY,int width,int height){

        try{
            BufferedImage image = getImage(startX,startY,width,height);
            ImageIO.write(image, "png", new File("noise.png"));
        }
            catch (IOException e){
            e.printStackTrace();
        }
	} 

    /**
     * generate and return the painted terrain-image
     */
    public BufferedImage getImage(int startX,int startY,int width,int height){
        
        int rgb;
        BufferedImage image = new BufferedImage(width-startX, height-startY, BufferedImage.TYPE_INT_RGB);
        for (int y = startY; y < height; y++)
        {
            for (int x = startX; x < width; x++){
                    
                    rgb = getColor(x*32,y*32);
                    image.setRGB(x-startX, y-startY, rgb);

            }
        }
		return image;
    }
        
    /**
     * this can be made way for advanced.
     */
    private int getColor(int worldX,int worldY){

        if (hightColor){
            return calculateHightColor(worldX,worldY);
        } else{
            return calculateBiomeColor(getBiome(worldX,worldY),worldX,worldY);
        }
        
        
    }

    /**
     * @param worldX and worldY - takes in coords, get the hightValue inside, then convertas the value to a color.
     */
    private int calculateHightColor(int worldX,int worldY){

        // Ensure the value is within the range [-1, 1]
        double value = getHeightValue(worldX,worldY);

        value = Math.max(-1.0, Math.min(1.0, value));
        
        // Map the value to the hue in the HSL color space
        float hue = (float) ((value + 1.0) / 2.0 * 360.0);
        
        // Create the corresponding color
        Color color = Color.getHSBColor(hue / 360.0f, 1.0f, 1.0f);
        
        // Convert the color to an RGB integer
        return color.getRGB();
    }

    private int calculateBiomeColor(Biome biome,int worldX,int worldY ){

        double height = getHeightValue(worldX,worldY);
        double TemperatureValue = getTemperatureValue(worldX,worldY);
        double continentalness = getNoiseValue(continentalSeed,continentalFreq,worldX,worldY,5);
        if (height < oceanLvl ){
            if (TemperatureValue< -0.5){
                //frozen water
                if (continentalness>0){
                    return ice;
                }
               
            }

            //water
            return water;
        
        }else if (height< beachLvl){
            if (TemperatureValue< -0.5){
                //snowy shore
                return snowy;
            }

            //wet beach OR Frozen beach?
            return sand;

        }else if (height<shoreLvl){

            if (TemperatureValue< -0.5){
                //snowy shore
                return snowy;
            }

            //normal beach or idk...
            return sand;
        
        } else if (height<mountainLvl){
            try{
                int color = biome.getColor();
               
                return color;
            } catch (NullPointerException e){
                
                //System.out.println(height+","+biome+"," + worldX +","+ worldY);
                return 0;
            }
            //return 0;
        } else {
            //System.out.println(height+","+biome+"," + worldX +","+ worldY);
            return mountainn.color;
        }
    }

    /** first try to create a function to make tiles.
     * not needed now.
     * 
     */
    private String calculateBiomeText(Biome biome,int worldX,int worldY ){

        double height = getHeightValue(worldX,worldY);
        if (height < oceanLvl){
            return "water";
        
            //can check what continental value is - how close to sea.
        }else if (height< beachLvl){
            return "beach";
        
        } else if (height< mountainLvl){
            try{
                
                return biome.getType();
            } catch (NullPointerException e){
                System.out.println( "feil: "+","+getTemperatureValue(worldX,worldY));
            }
            return null;

        }else {
        return mountainn.getType();
    }
    }


    /*
     * gets the name of the biome
     */
    public String calculateBiomeString(int worldX,int worldY){
        try{
            return getBiome(worldX,worldY).getType();
        } catch (Exception e){
            System.out.println("cant get type");
        }
        return "null";
        
    }

    private double getMoistValue(int worldX, int worldY){
        double moist = getNoiseValue(moistSeed,moistFrequency,worldX,worldY,5);

        double continentalness = getNoiseValue(continentalSeed,continentalFreq,worldX,worldY,3);

        double alteredMoist = getAlteredValue(moist,0.1,-continentalness);

        return moist;
    }


    private double getTemperatureValue(int worldX,int worldY){

        double temperature = getNoiseValue(tempSeed,heatFrequency,worldX,worldY,5);

        double height = getHeightValue(worldX,worldY);

        double alteredTemperature = getAlteredValue(temperature,temperatureKoefficent,-height);
        
        /* 
        if (alteredTemperature< -1 ){
            alteredTemperature = -1;
        }
        if (alteredTemperature<=-0.5){
            alteredTemperature=   -(Math.pow(alteredTemperature,4));
        }


        else if (alteredTemperature > 1){
            alteredTemperature = 1;
        }
        */
        return temperature;
    }

    private double getAlteredValue(double originalValue,double koefficent,double ChangeValue){
        double alteredValue = originalValue +(koefficent *(ChangeValue));
        if (alteredValue < -1){alteredValue = -1;} else if (alteredValue>1){alteredValue = 1;}
        return alteredValue;
    }

    /*
     * mainpualtes the hight value 
     * takes the original height value and adds continentalness*koefficent
     * should only return values between 1 and -1
     */
    public double getHeightValue(int worldX,int worldY){

        double alteredHeight;
        double height = getNoiseValue(seed,standardFrequency,worldX,worldY,6);

        double continentalness = getNoiseValue(continentalSeed,continentalFreq,worldX,worldY,6);
        
        double peakNoice = getNoiseValue(peaksSeed,peaksFreq,worldX,worldY,6);
        
        //not always a positive number, only takes the peaks.
        double peakValue = Math.pow(peakNoice,2)/3;

        double riverValue = getNoiseValue(10000,RIVERFREQUENZY,worldX,worldY,8);

        
        alteredHeight = height*continentalness;
        alteredHeight = getAlteredValue(height,heightKoefficent,continentalness) + peakValue;

        
        //testing:
       

        //we need continentalness, erosion and peeks

        if (alteredHeight< -1 ){
            alteredHeight = -1;
        }
        else if (alteredHeight > 1){
            alteredHeight = 1;
        }

        
        
        return alteredHeight;


    }

    public double getHeightValue2(int x, int y) {
        // Generate noise values for continentalness, peaks, and standard height
       
        double height = getNoiseValue(seed,standardFrequency,x,y,6);

        double continentalness = getNoiseValue(continentalSeed,continentalFreq,x,y,6);
        
        double peakNoice = getNoiseValue(peaksSeed,peaksFreq,x,y,6);
        
        // Apply weights to the noise values
        float continentalWeight = 0.3f;
        float peaksWeight = 0.5f;
        float heightWeight = 0.2f;

        // Combine noise values using weighted sum
        double finalHeight = (continentalness * continentalWeight) + (peakNoice * peaksWeight) + (height * heightWeight);

        // Normalize the final height value to be within the range [-1, 1]
        finalHeight = Math.max(-1, Math.min(1, finalHeight));

        return finalHeight;
    }

    private int getRandomSeed(){
        Random random = new Random();
        int seed = random.nextInt(0,9999);
        return seed;
    }

    /**
     *can be called by another object.
     *set new seed for all seed attributes
     */
    
    public void newSeed(){
        seed = getRandomSeed();
        tempSeed = getRandomSeed();
        moistSeed = getRandomSeed();
        continentalSeed = getRandomSeed();
        peaksSeed = getRandomSeed();
        vegetationSeed1 = getRandomSeed();
        vegetationSeed2 = getRandomSeed();
        vegetationSeed3 = getRandomSeed();
    }

    public static void main(String []args){
        ProceduralGeneration pg = new ProceduralGeneration();
        pg.paintMap(-1000,-1000,1000,1000);
        pg.drawBiomes();
    }

    public void setStandardFrequenzy(double frequency){
        standardFrequency = frequency;
    }

    public void setMoistFrequenzy(double frequency){
        moistFrequency = frequency;
    }

    public void setTempFrequenzy(double frequency){
        heatFrequency = frequency;
    }

    public void setK(double newValue){
        heightKoefficent = newValue;
    }

    public void setTemperatureK(double newKValue) {
        temperatureKoefficent = newKValue;
    }

    public void setContinentalFrequency(double newContinentalValue) {
        continentalFreq = newContinentalValue;
    }

     public void setPeaksFrequency(double newPeaksValue) {
        peaksFreq = newPeaksValue;
    }

    public void setHightColor() {
        if (hightColor){
            hightColor = false;
        } else {
            hightColor = true;
        }
    }

    public void setMoistK(double newKValue) {
        moistKoefficent = newKValue;
    }


    public double getVegetationVal(int x,int y){
        return getNoiseValue(vegetationSeed1,0.00001,x,y,5);
    }

    public double getVegetationMidFreq(int x,int y){
        return getNoiseValue(vegetationSeed2,0.005,x,y,5);
    }

    public double getVegetationHighFreq(int x,int y){
        return getNoiseValue(vegetationSeed3,0.05,x,y,5);
    }

    



    
   
    
}


