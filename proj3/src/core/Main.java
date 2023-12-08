package core;

import com.github.javaparser.utils.Pair;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.*;
import java.awt.*;
import java.util.List;

public class Main {
    public Random RANDOM;

    long deltaTime;
    int avatarX = 90;
    int avatarY = 50;
    int huge_torchNum = 0;

    int xOff = -45;
    int yOff = -25;
    long seed;
    int WorldWidth;
    int WorldHeight;
    TETile[][] characterLayer;
    double[][] lightLayer;
    Set<TETile> blockObject;
    Set<Torch> torches;
    Set<Bomb> bombs;
    Set<MovingTorch> movingTorches;
    Set<Arrow> arrows;
    Set<Zombie> zombies;
    int arrowNum;
    int torchNum;
    int bombNum;
    List<Pair<Integer, Integer>> huges = new ArrayList<>();

    public Main(int WIDTH, int HEIGHT, int WorldWidth, int WorldHeight){
        characterLayer = new TETile[WorldWidth][WorldHeight];
        lightLayer = new double[WorldWidth][WorldHeight];
        for (int i = 0; i < WorldWidth; i += 1) {
            for (int j = 0; j < WorldHeight; j += 1) {
                lightLayer[i][j] = 0;
            }
        }
        this.WorldHeight = WorldHeight;
        this.WorldWidth = WorldWidth;
        blockObject = new HashSet<>();
        blockObject.add(Tileset.WALL);
        torches = new HashSet<>();
        bombs = new HashSet<>();
        movingTorches = new HashSet<>();
        arrows = new HashSet<>();
        zombies = new HashSet<>();
        arrowNum = 5;
        torchNum = 20;
        bombNum = 50;
    }

    public String startGame(){
        Font font = new Font("Arial", Font.BOLD, 60);
        StdDraw.setFont(font);
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(255, 255, 255);
        StdDraw.text(45, 25, "Explore Worlds");
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char next = StdDraw.nextKeyTyped();
                if (next == 'q' || next == 'Q') {
                    return "q";
                } else if (next == 'l' || next == 'L') {
                    return "l";
                } else if (next == 'n' || next == 'N') {
                    font = new Font("Arial", Font.BOLD, 30);
                    StdDraw.setFont(font);
                    StdDraw.text(45, 20, "please enter a world seed:");
                    StdDraw.show();
                    StringBuilder seed = new StringBuilder();
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            next = StdDraw.nextKeyTyped();
                            if (Character.isDigit(next)) {
                                seed.append(next);
                                StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
                                StdDraw.filledRectangle(45, 15, 11, 2.5);
                                font = new Font("Arial", Font.PLAIN, 17);
                                StdDraw.setFont(font);
                                StdDraw.setPenColor(StdDraw.BLACK.brighter());
                                StdDraw.textLeft(35, 15, "Enter: " + seed.toString());
                                StdDraw.show();
                            } else if (next == 's' || next == 'S') {
                                return seed.toString();
                            }
                        }
                    }
                }
            }
        }
    }


    public void setAvatar(long seed, TETile[][] worldTiles){
        RANDOM = new Random(seed);
        this.seed = seed;
        avatarX = -1;
        avatarY = -1;
        while (avatarX < 0) {
            for (int i = 0; i < WorldWidth; i += 1) {
                for (int j = 0; j < WorldHeight; j += 1) {
                    if (worldTiles[i][j] == Tileset.FLOOR){
                        if (RANDOM.nextInt(10) == 1){
                            avatarX = i;
                            avatarY = j;
                            characterLayer[i][j] = Tileset.AVATAR;
                            xOff = 45 - i;
                            yOff = 25 - j;
                            return;
                        }
                    }
                }
            }
        }
    }


    public void clearLightLayer(){
        for (int i = 0; i < WorldWidth; i += 1) {
            for (int j = 0; j < WorldHeight; j += 1) {
                lightLayer[i][j] = 0;
            }
        }
    }

    public void clearCharLayer(){
        for (int i = 0; i < WorldWidth; i += 1) {
            for (int j = 0; j < WorldHeight; j += 1) {
                characterLayer[i][j] = null;
            }
        }
    }


    public void putLights(){
        Torch self = new Torch(avatarX, avatarY);
        self.put(lightLayer);
        for (Torch torch : torches){
            torch.put(lightLayer);
            if (torch instanceof HugeTorch hugeTorch){
                if (hugeTorch.check_lighted(characterLayer)){
                    huge_torchNum -= 1;
                };
            }
        }
    }

    public void putBombs(){
        for (Bomb bomb : bombs){
            bomb.put(lightLayer, characterLayer);
        }
    }

    public void putMovingTorches(){
        for (MovingTorch movingTorch : movingTorches){
            movingTorch.put(lightLayer);
        }
    }

    public void putArrows(){
        for (Arrow arrow : arrows){
            arrow.put(lightLayer, characterLayer);
        }
    }

    public void putZombies(){
        for (Zombie zombie : zombies){
            zombie.put(characterLayer);
        }
    }

    private boolean isBlocked(int x, int y, TETile[][] worldTiles){
        for (TETile t : blockObject){
            if (worldTiles[x][y] == t){
                return true;
            }
        }
        return false;
    }

    public void update(TETile[][] worldTiles, int round){
        // a, s, d, w
        if (StdDraw.isKeyPressed(65) && avatarX - 1 >= 0){
            if (isBlocked(avatarX-1, avatarY, worldTiles)){
                return;
            }
            characterLayer[avatarX][avatarY] = null;
            avatarX -= 1;
            characterLayer[avatarX][avatarY] = Tileset.AVATAR;
            if (avatarX + xOff <= 20){
                xOff += 1;
            }
        }
        if (StdDraw.isKeyPressed(83) && avatarY - 1 >= 0){
            if (isBlocked(avatarX, avatarY-1, worldTiles)){
                return;
            }
            characterLayer[avatarX][avatarY] = null;
            avatarY -= 1;
            characterLayer[avatarX][avatarY] = Tileset.AVATAR;
            if (avatarY + yOff <= 10){
                yOff += 1;
            }
        }
        if (StdDraw.isKeyPressed(68) && avatarX + 1 < WorldWidth){
            if (isBlocked(avatarX+1, avatarY, worldTiles)){
                return;
            }
            characterLayer[avatarX][avatarY] = null;
            avatarX += 1;
            characterLayer[avatarX][avatarY] = Tileset.AVATAR;
            if (avatarX + xOff >= 70){
                xOff -= 1;
            }
        }
        if (StdDraw.isKeyPressed(87) && avatarY + 1 < WorldHeight){
            if (isBlocked(avatarX, avatarY+1, worldTiles)){
                return;
            }
            characterLayer[avatarX][avatarY] = null;
            avatarY += 1;
            characterLayer[avatarX][avatarY] = Tileset.AVATAR;
            if (avatarY + yOff >= 40){
                yOff -= 1;
            }
        }
        //press b for a bomb
        if (bombNum > 0 && StdDraw.isKeyPressed(66)) {
            bombs.add(new Bomb(avatarX, avatarY));
            bombNum -= 1;
        }
        //press m for moving torch ; t for a permanent one
        if (torchNum > 0) {
            //put a torch
            if (StdDraw.isKeyPressed(84)) {
                torches.add(new Torch(avatarX, avatarY));
                torchNum -= 1;
            }
            //press m for moving torch
            if (StdDraw.isKeyPressed(65) && StdDraw.isKeyPressed(77)) {
                movingTorches.add(new MovingTorch(avatarX, avatarY, -2, 0));
                torchNum -= 1;
            }
            if (StdDraw.isKeyPressed(83) && StdDraw.isKeyPressed(77)) {
                movingTorches.add(new MovingTorch(avatarX, avatarY, 0, -2));
                torchNum -= 1;
            }
            if (StdDraw.isKeyPressed(68) && StdDraw.isKeyPressed(77)) {
                movingTorches.add(new MovingTorch(avatarX, avatarY, 2, 0));
                torchNum -= 1;
            }
            if (StdDraw.isKeyPressed(87) && StdDraw.isKeyPressed(77)) {
                movingTorches.add(new MovingTorch(avatarX, avatarY, 0, 2));
                torchNum -= 1;
            }
        }
        //press p for arrow
        if (arrowNum > 0) {
            if (StdDraw.isKeyPressed(65) && StdDraw.isKeyPressed(80)) {
                arrows.add(new Arrow(avatarX, avatarY, -2, 0));
                arrowNum -= 1;
            }
            if (StdDraw.isKeyPressed(83) && StdDraw.isKeyPressed(80)) {
                arrows.add(new Arrow(avatarX, avatarY, 0, -2));
                arrowNum -= 1;
            }
            if (StdDraw.isKeyPressed(68) && StdDraw.isKeyPressed(80)) {
                arrows.add(new Arrow(avatarX, avatarY, 2, 0));
                arrowNum -= 1;
            }
            if (StdDraw.isKeyPressed(87) && StdDraw.isKeyPressed(80)) {
                arrows.add(new Arrow(avatarX, avatarY, 0, 2));
                arrowNum -= 1;
            }
        }

        //update bombs
        Set<Bomb> copy = new HashSet<>(bombs);
        for (Bomb bomb : copy){
            if (!bomb.update()){
                bombs.remove(bomb);
            }
        }
        //update movingTorches
        Set<MovingTorch> copyMovingTorches = new HashSet<>(movingTorches);
        for (MovingTorch movingTorch : copyMovingTorches){
            if (!movingTorch.move(worldTiles)){
                movingTorches.remove(movingTorch);
            }
        }
        //update arrows
        Set<Arrow> copyArrows = new HashSet<>(arrows);
        for (Arrow arrow : copyArrows){
            if (!arrow.move(worldTiles)){
                arrows.remove(arrow);
            }
        }
    }

    private  void updateZombie(TETile[][]worldTiles, int round){
        Set<Zombie> copyZombies = new HashSet<>(zombies);
        for (Zombie zombie : copyZombies){
            if (!zombie.move(worldTiles, characterLayer, round % 2 == 1)){
                zombies.remove(zombie);
            }
        }
    }

    public void generateZombies(TETile[][] worldTiles){
            for (int i = 0; i < WorldWidth; i += 1) {
                for (int j = 0; j < WorldHeight; j += 1) {
                    if (worldTiles[i][j] == Tileset.FLOOR){
                        if (RANDOM.nextInt(20) == 1){
                            zombies.add(new Zombie(i, j, 0, RANDOM));
                        }
                    }
                }
            }
    }

    public void generateHugeTorches(TETile[][] worldTiles){
        for (int i = 0; i < WorldWidth; i += 1) {
            for (int j = 0; j < WorldHeight; j += 1) {
                if (worldTiles[i][j] == Tileset.FLOOR){
                    if (RANDOM.nextInt(500) == 1){
                        torches.add(new HugeTorch(i, j));
                        huge_torchNum += 1;
                        huges.add(new Pair<>(i, j));
                    }
                }
            }
        }
    }



    public static void main(String[] args) {

        int WIDTH = 90;
        int HEIGHT = 50;
        int WorldWidth = 180;
        int WorldHeight = 100;
        double DEGREE = 0.6;
        int TIME = 1;

        //game begins

        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);


        Main process = new Main(WIDTH, HEIGHT, WorldWidth, WorldHeight);
        String input = process.startGame();

        // process input
        if (input.equals("q")){
            System.out.println("exited game");
            System.exit(0);
            return;
        }
        ter.initialize(WIDTH, HEIGHT);
        long SEED = Long.parseLong(input);
        World world = new World(WorldWidth, WorldHeight, SEED, DEGREE, TIME);
        TETile[][] worldTiles = world.getTiles();
        process.deltaTime = System.currentTimeMillis();
        process.setAvatar(SEED, worldTiles);
        long arrowTimer = System.currentTimeMillis();
        int round = 0;
        boolean lightUp = false;
        boolean show_huge = false;

        //generate zombies
        process.generateZombies(worldTiles);
        process.generateHugeTorches(worldTiles);

        while (true){
            round += 1;
            round = round % 100;
            if (StdDraw.isKeyPressed(16) && StdDraw.isKeyPressed(59) && StdDraw.isKeyPressed(81)){
                System.exit(0);
            }
            long time = System.currentTimeMillis() - process.deltaTime;
            long arrowTime = System.currentTimeMillis() - arrowTimer;
            if (time > 100) {
                if (StdDraw.isKeyPressed(73)){
                    show_huge = !show_huge;
                }
                if (arrowTime > 5000){
                    arrowTimer = System.currentTimeMillis();
                    process.arrowNum += 1;
                    process.torchNum += 2;
                }
                process.update(worldTiles, round);

                process.clearLightLayer();

                process.clearCharLayer();
                process.characterLayer[process.avatarX][process.avatarY] = Tileset.AVATAR;

                process.putLights();
                process.putBombs();
                process.putMovingTorches();
                process.putArrows();
                process.updateZombie(worldTiles, round);
                process.putZombies();
                process.deltaTime = System.currentTimeMillis();
                if (process.huge_torchNum == 0){
                    lightUp = true;
                }
            }
            StdDraw.clear(new Color(0, 0, 0));
            Font font = new Font("Monaco", Font.BOLD, 14);
            StdDraw.setFont(font);
            ter.renderFrame(worldTiles, process.characterLayer, process.lightLayer, process.xOff, process.yOff, lightUp);
            StdDraw.setPenColor(255, 255, 255);
            StdDraw.text(10, 2, "You're at x: " + process.avatarX + "/180  y: " + process.avatarY + "/100");
            StdDraw.text(6, 48, "Remaining.. Arrows: " + process.arrowNum);
            StdDraw.text(15, 48, "Torches: " + process.torchNum);
            StdDraw.text(80, 48, "HUGE Torches: " + process.huge_torchNum);
            if (show_huge){
                Pair<Integer, Integer> huge;
                for (int i = 0; i < process.huges.size(); i += 1){
                    huge = process.huges.get(i);
                    StdDraw.text(80, 48 - (i+1) * 2, "(" + huge.a + ", " + huge.b + ")");
                }
            }
            StdDraw.show();
        }

    }
}
