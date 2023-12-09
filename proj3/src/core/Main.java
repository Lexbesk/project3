package core;

import com.github.javaparser.utils.Pair;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    Set<Boss> bosses;
    int arrowNum;
    int torchNum;
    int bossNum = 0;
    int bombNum;
    int avatarHealth = 20;
    int x0;
    int y0;
    List<Pair<Integer, Integer>> huges = new ArrayList<>();
    List<Pair<Integer, Integer>> bossesPair = new ArrayList<>();
    Set<HugeTorch> hugeTorches = new HashSet<>();

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
        bosses = new HashSet<>();
        arrowNum = 5;
        torchNum = 20;
        bombNum = 50;
    }

    public String startGame(){
        Font font = new Font("Arial", Font.BOLD, 60);
        StdDraw.setFont(font);
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(255, 255, 255);
        StdDraw.text(45, 35, "End the Night");
        font = new Font("Monaco", Font.PLAIN, 20);
        StdDraw.setFont(font);
        StdDraw.text(45, 30, "You are about to enter the palace of the NightKing(s) (shown as #),");
        StdDraw.text(45, 26, "which is filled with annoying Zombies (shown as &) , to terminate the NightKing(s), to end the long night.");
        StdDraw.text(45, 22, "be careful !");
        font = new Font("Monaco", Font.PLAIN, 25);
        StdDraw.setFont(font);
        StdDraw.textLeft(20, 18, "press: N for new world(game)");
        StdDraw.textLeft(20, 14, "press: L to load previous state(if you have played before and quited)");
        StdDraw.textLeft(20, 10, "press: Q to quit");
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
                    StdDraw.clear(StdDraw.BLACK);
                    StdDraw.text(45, 30, "A new long night");
                    StdDraw.text(45, 20, "please enter a world seed then press S: (int or long)");
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
                            x0 = i;
                            y0 = j;
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
                if (hugeTorch.check_lighted(characterLayer, hugeTorches)){
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

    public void save(boolean phase1){
        // "seed,phase1,x0,y0,
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(seed).append(",");
        stringBuilder.append(phase1).append(",");
        stringBuilder.append(x0).append(",");
        stringBuilder.append(y0).append(",");
        stringBuilder.append(avatarX).append(",");
        stringBuilder.append(avatarY).append(",");
        stringBuilder.append(huge_torchNum).append(",");
        stringBuilder.append(xOff).append(",");
        stringBuilder.append(yOff).append(",");
        stringBuilder.append("\n");
        for (Torch torch : torches){
            if (torch instanceof HugeTorch){
                continue;
            }
            stringBuilder.append(torch.x).append(",").append(torch.y).append(";");
        }
        stringBuilder.append("\n");
        for (Boss boss : bosses){
            stringBuilder.append(boss.x).append(",").append(boss.y).append(";");
        }
        stringBuilder.append("\n");
        stringBuilder.append(avatarHealth);
        stringBuilder.append("\n");
        for (Torch torch : torches){
            if (torch instanceof HugeTorch hugeTorch){
                stringBuilder.append(hugeTorch.x).append(",").append(hugeTorch.y).append(",").append(hugeTorch.lighted).append(";");
            }
        }
        File file = new File("/Users/lebesk/Documents/CS-Courses/cs61b/project3/proj3/src/saveandload.txt");
        try {
            Files.writeString(file.toPath(), stringBuilder.toString());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean load(){
        File file = new File("/Users/lebesk/Documents/CS-Courses/cs61b/project3/proj3/src/saveandload.txt");
        String string;
        try {
            string = Files.readString(file.toPath());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        String[] strings = string.split("\n");
        String[] basics = strings[0].split(",");
        String[] torches = strings[1].split(";");
        String[] bosses = strings[2].split(";");
        String[] hugeTorches = strings[4].split(";");
        avatarHealth = Integer.parseInt(strings[3]);
        seed = Integer.parseInt(basics[0]);
        boolean phase1 = Boolean.parseBoolean(basics[1]);
        x0 = Integer.parseInt(basics[2]);
        y0 = Integer.parseInt(basics[3]);
        avatarX = Integer.parseInt(basics[4]);
        avatarY = Integer.parseInt(basics[5]);
        huge_torchNum = Integer.parseInt(basics[6]);
        xOff = Integer.parseInt(basics[7]);
        yOff = Integer.parseInt(basics[8]);
        this.RANDOM = new Random(seed);

        this.torches = new HashSet<>();
        for (String torch : torches) {
            String[] xy = torch.split(",");
            try {
                int x1 = Integer.parseInt(xy[0]);
                int y1 = Integer.parseInt(xy[1]);
                this.torches.add(new Torch(x1, y1));
            } catch (Exception ignored){
            }
        }

        this.bosses = new HashSet<>();
        for (String boss : bosses){
            String[] xy = boss.split(",");
            try{
                int x2 = Integer.parseInt(xy[0]);
                int y2 = Integer.parseInt(xy[1]);
                this.bosses.add(new Boss(x2, y2));
            } catch (Exception ignored){
            }
        }

        for (String huge : hugeTorches){
            String[] xyl = huge.split(",");
            try {
                int x2 = Integer.parseInt(xyl[0]);
                int y2 = Integer.parseInt(xyl[1]);
                boolean l2 = Boolean.parseBoolean(xyl[2]);
                this.torches.add(new HugeTorch(x2, y2, l2));
            } catch (Exception ignored){
            }
        }

        return phase1;
    }

    public void putBosses(){
        for (Boss boss : bosses){
            boss.put(characterLayer, lightLayer);
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
            if (!zombie.move(worldTiles, characterLayer, round % 2 == 1, this)){
                zombies.remove(zombie);
            }
        }
    }

    private  void updateBoss(TETile[][] characterLayer){
        Set<Boss> copyBoss = new HashSet<>(bosses);
        for (Boss boss : copyBoss){
            if (!boss.move(characterLayer, this)){
                bosses.remove(boss);
                bossNum -= 1;
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

    public void generateZombies2(TETile[][] worldTiles){
        for (HugeTorch hugeTorch : hugeTorches){
            zombies.add(new Zombie(hugeTorch.x, hugeTorch.y, 0, RANDOM));
        }
    }

    public void generateHugeTorches(TETile[][] worldTiles){
        for (int i = 0; i < WorldWidth; i += 1) {
            for (int j = 0; j < WorldHeight; j += 1) {
                if (worldTiles[i][j] == Tileset.FLOOR){
                    if (RANDOM.nextInt(500) == 1){
                        HugeTorch hugeTorch = new HugeTorch(i, j);
                        torches.add(hugeTorch);
                        huge_torchNum += 1;
                        huges.add(new Pair<>(i, j));
                        hugeTorches.add(hugeTorch);
                    }
                }
            }
        }
    }


    public void generateBosses(TETile[][] worldTiles, int num){
        while (bossNum < num) {
            for (int i = WorldWidth - 1; i >= 0; i -= 1) {
                for (int j = WorldHeight - 1; j >= 0; j -= 1) {
                    if (worldTiles[i][j] == Tileset.FLOOR) {
                        if (RANDOM.nextInt(500) == 1 && !bossesPair.contains(new Pair<>(i, j))) {
                            bosses.add(new Boss(i, j));
                            bossNum += 1;
                            if (bossNum == num) {
                                return;
                            }
                            bossesPair.add(new Pair<>(i, j));
                        }
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
        boolean phase1;
        long SEED;
        TETile[][] worldTiles;
        World world;
        if (input.equals("q")){
            System.out.println("exited game");
            System.exit(0);
            return;
        } else if (input.equals("l")) {
            phase1 = process.load();
            SEED = process.seed;
            world = new World(WorldWidth, WorldHeight, SEED, DEGREE, TIME);
            worldTiles = world.getTiles();
        } else {
            phase1 = true;
            SEED = Long.parseLong(input);
            world = new World(WorldWidth, WorldHeight, SEED, DEGREE, TIME);
            worldTiles = world.getTiles();
            process.setAvatar(SEED, worldTiles);
            process.generateHugeTorches(worldTiles);
            process.generateBosses(worldTiles, 2);
        }
        ter.initialize(WIDTH, HEIGHT);
        process.deltaTime = System.currentTimeMillis();
        long arrowTimer = System.currentTimeMillis();
        long zombieTimer = System.currentTimeMillis();
        int round = 0;
        boolean lightUp = false;
        boolean show_huge = false;
        boolean showboss = false;
        process.generateZombies(worldTiles);

        while (true){
            if (!phase1 && process.avatarX == process.x0 && process.avatarY == process.y0){
                Font font = new Font("Monaco", Font.BOLD, 50);
                StdDraw.setFont(font);
                StdDraw.setPenColor(250, 255, 255);
                StdDraw.text(45, 30, "Congratulations! YOU HAVE SAFELY ESCAPED");
                StdDraw.text(45, 20, "You have completed the mission");
                StdDraw.text(45, 10, "press Q to quit and save your record");
                StdDraw.show();
                while (true){
                    if (StdDraw.isKeyPressed(81)){
                        System.exit(0);
                    }
                }
            }
            if (process.bosses.isEmpty() && phase1){
                phase1 = false;
                lightUp = true;
                StdDraw.clear(new Color(0, 0, 0));
                Font font = new Font("Monaco", Font.BOLD, 40);
                StdDraw.setFont(font);
                StdDraw.setPenColor(255, 255, 255);
                StdDraw.text(45, 30, "Congratulations! All targets are down");
                StdDraw.text(45, 20, "but... do you remember where you were dropped down?");
                StdDraw.text(45, 10, "press H to continue");
                StdDraw.show();
                while (true){
                    if (StdDraw.isKeyPressed(72)){
                        break;
                    }
                }
                StdDraw.clear(new Color(0, 0, 0));
                StdDraw.text(45, 30, "Congratulations! All targets are down");
                StdDraw.text(45, 20, "NOW, RETURN TO STARTING LOCATION TO ESCAPE !");
                StdDraw.text(45, 10, "press H to continue");
                StdDraw.show();
                StdDraw.pause(1000);
                while (true){
                    if (StdDraw.isKeyPressed(72)){
                        break;
                    }
                }
            }
            round += 1;
            round = round % 100;
            if (StdDraw.isKeyPressed(16) && StdDraw.isKeyPressed(59) && StdDraw.isKeyPressed(81)){
                System.exit(0);
            }
            long time = System.currentTimeMillis() - process.deltaTime;
            long arrowTime = System.currentTimeMillis() - arrowTimer;
            if (time > 100) {
                if (StdDraw.isKeyPressed(81)){
                    process.save(phase1);
                    System.exit(0);
                }
                // i for show huge
                if (StdDraw.isKeyPressed(73)){
                    show_huge = !show_huge;
                }
                // o for light all up
                if (StdDraw.isKeyPressed(76)){
                    lightUp = !lightUp;
                }
                // k for show boss
                if (StdDraw.isKeyPressed(75)){
                    showboss = !showboss;
                }
                if (arrowTime > 5000){
                    arrowTimer = System.currentTimeMillis();
                    process.arrowNum += 1;
                    process.torchNum += 2;
                    process.generateZombies2(worldTiles);
                }
                process.update(worldTiles, round);
                if (process.avatarHealth <= 0){
                    Font font = new Font("Monaco", Font.BOLD, 30);
                    StdDraw.setFont(font);
                    StdDraw.setPenColor(255, 255, 255);
                    StdDraw.text(45, 25, "You are dead and thus have failed");
                    StdDraw.show();
                    StdDraw.pause(3000);
                    System.exit(0);
                }

                process.clearLightLayer();

                process.clearCharLayer();
                process.characterLayer[process.avatarX][process.avatarY] = Tileset.AVATAR;

                process.putLights();
                process.putBombs();
                process.putMovingTorches();
                process.putArrows();
                process.updateZombie(worldTiles, round);
                process.putZombies();
                process.putBosses();
                process.updateBoss(process.characterLayer);
                process.characterLayer[process.x0][process.y0] = Tileset.FLOWER;
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
            StdDraw.text(22, 48, "Bombs: " + process.bombNum);
            StdDraw.text(80, 48, "Unlighted HugeTorches: " + process.huge_torchNum);
            if (process.avatarHealth > 15) {
                StdDraw.text(30, 48, "Health: " + process.avatarHealth);
            } else{
                StdDraw.setPenColor(255, 10, 10);
                StdDraw.text(30, 48, "!!!Health!!!: " + process.avatarHealth);
                StdDraw.setPenColor(255, 255, 255);
            }
            StdDraw.textLeft(1, 44, "Zombies are born at unlighted HugeTorches(shown as ▒)!");
            StdDraw.textLeft(1, 42, "light them up!");
            StdDraw.textLeft(20, 2, "KEYS INFO: W/A/S/D:move, P+W/A/S/D:arrow, B:bomb, K:location of NightKing, T:torch(maybe for your way home), I:location of HugeTorches");
//            if (show_huge){
//                Pair<Integer, Integer> huge;
//                for (int i = 0; i < process.huges.size(); i += 1){
//                    huge = process.huges.get(i);
//                    StdDraw.text(80, 48 - (i+1) * 2, "(" + huge.a + ", " + huge.b + ")");
//                }
//            }

            if (show_huge){
                int jj = 0;
                for (HugeTorch torch : process.hugeTorches){
                    StdDraw.text(80, 48 - (jj+1) * 2, "(" + torch.x + ", " + torch.y + ")");
                    jj += 1;
                }
            }

            if (showboss){
                int ii = 0;
                for (Boss boss : process.bosses){
                    StdDraw.text(80, 48 - (ii+1) * 2, "(" + boss.x + ", " + boss.y + ")");
                    ii += 1;
                }
            }
            StdDraw.textLeft(3, 46, "NightKings");
            for (int i = 0; i < process.bosses.size(); i += 1){
                StdDraw.textLeft(10 + i, 46, "#");
            }
            StdDraw.show();
        }



    }
}
