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

public class PG {

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

    // Coefficients
    double heightCoefficient = 1.5;
    double temperatureCoefficient = 0.2;
    double moistCoefficient = 2;

    // Frequencies
    double standardFrequency = 0.008 / 24;
    final double RIVER_FREQUENCY = 0.03 / 24;
    double moistFrequency = 0.05 / 24;
    double heatFrequency = 0.004 / 24;
    double continentalFreq = 0.0008 / 24;
    double peaksFreq = 0.4 / 24;

    // Rectangles of biomes
    ArrayList<Biome> biomeRects = new ArrayList<>();
    HashMap<Biome, Integer> colorMap = new HashMap<>();
    ArrayList<Biome> allBiomes = new ArrayList<>();

    // Boolean modes
    boolean heightColor = false;

    // Colors
    private int green = 0xA0C080;
    private int darkGreen = 0x408000;
    private int sand = 0xffe7c3;
    private int moss = 0x4E4E35;
    private int water = 0x0000ff;
    private int mountain = 0x808080;
    private int ice = 0xBFEFFF;
    private int savannaC = 0xCDBE70;
    private int sForest = 0x629632;
    private int rainFr = 0x78AB46;
    private int black = 0x000000;
    private int snowy = 0xffffff;

    // Levels
    double oceanLvl = 0;
    double beachLvl = 0.04;
    double shoreLvl = 0.07;
    double mountainLvl = 0.97;

    // Global biomes
    Biome beach;
    Biome ocean;
    Biome mountainn;
    Biome wetBeach;

    public PG() {
        newSeed();
        setupBiomes();
    }

    /**
     * Iterates through the noise generation
     * @param worldX takes in real coord - converts it into tileCoord in function
     * @param iteration defines how many iterations the algorithms run. more is more detailed and sharp edges, more noise in edges. less is smoother.
     */
    private double getNoiseValue(long seed, double frequency, int worldX, int worldY, int iterations) {
        int x = worldX / 32;
        int y = worldY / 32;
        double value = 0;
        double multiplier = 1;
        double size = 0;

        for (int i = 0; i < iterations; i++) {
            value += OpenSimplex2S.noise2_ImproveX(seed, x * frequency, y * frequency) * multiplier;
            frequency *= 2;
            size += multiplier;
            multiplier /= 2;
        }
        value /= size;
        sumValue += value;
        amount++;
        return value;
    }

    /**
     * Function produces moist and heat noise, and returns the corresponding biome.
     * @param worldX is real coordinates
     */
    private Biome getBiome(int worldX, int worldY) {
        double moistValue = getMoistValue(worldX, worldY);
        double heatValue = getTemperatureValue(worldX, worldY);
        double heightValue = getHeightValue(worldX, worldY);
        return getBiomeFromList(heatValue, moistValue, heightValue);
    }

    /**
     * Searches through all biomes and finds out where the point is.
     */
    private Biome getBiomeFromList(double heat, double moist, double height) {
        int heat2 = (int) (heat * 50);
        int moist2 = (int) (moist * 50);
        Point p = new Point(heat2, moist2);

        if (height < oceanLvl) {
            return ocean;
        } else if (height < beachLvl) {
            return wetBeach;
        } else if (height < shoreLvl) {
            return beach;
        }

        for (Biome biome : biomeRects) {
            if (biome.contains(p)) {
                return biome;
            }
        }
        return null;
    }

    private void drawBiomes() {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        for (int temperature = -50; temperature < 50; temperature++) {
            for (int moist = -50; moist < 50; moist++) {
                Biome biome = getBiomeFromList((double) temperature / 50, (double) moist / 50, 1);
                if (biome != null) {
                    int rgb = colorMap.get(biome);
                    image.setRGB(temperature + 50, moist + 50, rgb);
                }
            }
        }

        try {
            ImageIO.write(image, "png", new File("biomes.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupBiomes() {
        Biome snowyTundra = new Biome(-51, -51, -25, 0, "Snowy Tundra", snowy);
        Biome plains = new Biome(-25, -51, 10, 0, "grass", green);
        Biome desert = new Biome(10, -51, 51, -10, "desert", sand);
        Biome snowyTaiga = new Biome(-51, 0, -25, 51, "snowy taiga", snowy);
        Biome seasonalForest = new Biome(-25, 0, 0, 51, "seasonal forest", sForest);
        Biome savanna = new Biome(0, -10, 51, 10, "savanna", savannaC);
        Biome forest = new Biome(0, 10, 51, 25, "forest", darkGreen);
        Biome swamp = new Biome(0, 25, 20, 51, "plains", moss);
        Biome rainForest = new Biome(20, 25, 51, 51, "rain forest", rainFr);

        ocean = new Biome(-1, oceanLvl, "ocean", water);
        beach = new Biome(oceanLvl, beachLvl, "beach", sand);
        wetBeach = new Biome(oceanLvl, beachLvl, "wetBeach", sand);
        mountainn = new Biome(1, 0.85, "mountain", mountain);

        biomeRects.add(snowyTundra);
        biomeRects.add(plains);
        biomeRects.add(desert);
        biomeRects.add(snowyTaiga);
        biomeRects.add(seasonalForest);
        biomeRects.add(savanna);
        biomeRects.add(forest);
        biomeRects.add(swamp);
        biomeRects.add(rainForest);

        colorMap.put(snowyTundra, ice);
        colorMap.put(snowyTaiga, ice);
        colorMap.put(plains, green);
        colorMap.put(desert, sand);
        colorMap.put(snowyTundra, ice);
        colorMap.put(seasonalForest, sForest);
        colorMap.put(savanna, savannaC);
        colorMap.put(forest, darkGreen);
        colorMap.put(rainForest, rainFr);
        colorMap.put(swamp, moss);
    }

    /**
     * Retrieves the image to file
     */
    private void paintMap(int startX, int startY, int width, int height) {
        try {
            BufferedImage image = getImage(startX, startY, width, height);
            ImageIO.write(image, "png", new File("noise.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate and return the painted terrain-image
     */
    public BufferedImage getImage(int startX, int startY, int width, int height) {
        BufferedImage image = new BufferedImage(width - startX, height - startY, BufferedImage.TYPE_INT_RGB);
        for (int y = startY; y < height; y++) {
            for (int x = startX; x < width; x++) {
                int rgb = getColor(x * 32, y * 32);
                image.setRGB(x - startX, y - startY, rgb);
            }
        }
        return image;
    }

    private int getColor(int worldX, int worldY) {
        if (heightColor) {
            return calculateHeightColor(worldX, worldY);
        } else {
            return calculateBiomeColor(getBiome(worldX, worldY), worldX, worldY);
        }
    }

    /**
     * Takes in coords, gets the heightValue inside, and returns the color.
     */
    private int calculateHeightColor(int worldX, int worldY) {
        double heightValue = getHeightValue(worldX, worldY);
        double d = 1 / 255.0;
        int rgb = Color.HSBtoRGB((float) heightValue, 1, 1);

        if (heightValue < oceanLvl) {
            return Color.HSBtoRGB(100, 0.1f, 0.1f);
        } else if (heightValue < beachLvl) {
            return Color.HSBtoRGB(50, 0.3f, 0.3f);
        } else if (heightValue < shoreLvl) {
            return Color.HSBtoRGB(20, 0.5f, 0.5f);
        } else if (heightValue < mountainLvl) {
            return Color.HSBtoRGB(20, 0.7f, 0.7f);
        } else {
            return Color.HSBtoRGB(10, 0.7f, 0.7f);
        }
    }

    private int calculateBiomeColor(Biome biome, int worldX, int worldY) {
        if (biome == null) {
            return black;
        }
        return biome.getColor();
    }

    private double getHeightValue(int worldX, int worldY) {
        return getNoiseValue(seed, standardFrequency, worldX, worldY, 12) * heightCoefficient;
    }

    private double getMoistValue(int worldX, int worldY) {
        return getNoiseValue(moistSeed, moistFrequency, worldX, worldY, 12) * moistCoefficient;
    }

    private double getTemperatureValue(int worldX, int worldY) {
        return getNoiseValue(tempSeed, heatFrequency, worldX, worldY, 12) * temperatureCoefficient;
    }

    private void newSeed() {
        seed = new Random().nextInt();
        moistSeed = new Random().nextInt();
        tempSeed = new Random().nextInt();
        continentalSeed = new Random().nextInt();
        peaksSeed = new Random().nextInt();
        vegetationSeed1 = new Random().nextInt();
        vegetationSeed2 = new Random().nextInt();
        vegetationSeed3 = new Random().nextInt();
    }

    public void setSeed(int seed) {
        this.seed = seed;
        moistSeed = seed * seed - 1;
        tempSeed = seed * seed - 2;
        continentalSeed = seed * seed - 3;
        peaksSeed = seed * seed - 4;
        vegetationSeed1 = seed * seed - 5;
        vegetationSeed2 = seed * seed - 6;
        vegetationSeed3 = seed * seed - 7;
    }
    public static void main(String []args){
        PG pg = new PG();
        pg.paintMap(-1000,-1000,1000,1000);
        pg.drawBiomes();
    }
}
