package core;

import com.github.javaparser.utils.Pair;
import net.sf.saxon.expr.Component;
import net.sf.saxon.expr.flwor.Tuple;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.*;

public class World {

    private int width;
    private int height;
    private TETile[][] tiles;
    private int seed;
    private List<Set<Pair<Integer, Integer>>> existFloors;
    private List<List<Pair<Integer, Integer>>> existFloorsList;
    // do not add in to this list manually (need to check repeat).
    private double degree;
    private final Random RANDOM;
    private int time;


    public World(int width, int height, int seed, double degree, int time){
        this.height = height;
        this.width = width;
        this.seed = seed;
        this.degree = degree;
        this.time = time;
        tiles = new TETile[width][height];
        for (int x = 0; x <width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
        existFloors = new ArrayList<>();
        existFloorsList = new ArrayList<>();
        RANDOM = new Random(seed);
        Arrays.fill(connected, false);
        generateWorld();
    }

    private void generateWorld(){
        boolean b = false;
        for (int i = 0; i < time; i ++) {
            existFloors = new ArrayList<>();
            existFloorsList = new ArrayList<>();
            generateRooms(0);
            b = connectRooms();
            while (!b) {
                generateRooms(1);
                b = connectRooms();
            }
        }
        generateWalls();
        System.out.println(b);
    }

    private void generateRooms(int time) {
        for (int j = 0; j < 5; j += 1){
            for (int i = 0; i < 9; i += 1){
                int left = RANDOM.nextInt(0, 7);
                int right = RANDOM.nextInt(left + 2, 10);
                int bottom = RANDOM.nextInt(0, 7);
                int top = RANDOM.nextInt(bottom + 2, 10);
                if (i == 0 && left == 0){
                    left = 1;
                }
                if (i == 8 && right == 9){
                    right -= 1;
                }
                if (j == 0 && bottom == 0){
                    bottom = 1;
                }
                if (j == 4 && top == 9){
                    top -= 1;
                }
                int px = 10 * i;
                int py = 10 * j;
                if (time == 0){
                    existFloors.add(new HashSet<>());
                    existFloorsList.add(new ArrayList<>());
                }
                if (RANDOM.nextInt(10) < degree * 10){
                    continue;
                }
                int index = i + j * 9;
                for (int x = px + left; x <= px + right; x += 1){
                    for (int y = py + bottom; y <= py + top; y += 1){
                        tiles[x][y] = Tileset.FLOOR;
                        Pair<Integer, Integer> newFloor = new Pair<>(x, y);
                        if (!existFloors.get(index).contains(newFloor)) {
                            existFloors.get(index).add(newFloor);
                            existFloorsList.get(index).add(newFloor);
                        }
                    }
                }
            }
        }
    }

    boolean[] connected = new boolean[5 * 9];

    private boolean connectRooms(){
        Arrays.fill(connected, false);
        connected[0] = true;
        int root = -1;

        for (int i = 0; i < 5 * 9; i += 1){
            if (existFloors.get(i).isEmpty()){
                connected[i] = true;
            } else if (root == -1){
                root = i;
            }
        }

        return dfs(root);
    }

    private boolean dfs(int root){
        boolean t = true;
        for (boolean b : connected){
            if (!b){
                t = false;
            }
        }
        if (t){
            return true;
        }
        List<Integer> neighbors = neighbors(root);
        Collections.shuffle(neighbors, RANDOM);
        for (int neighbor : neighbors){
            if (!existFloors.get(neighbor).isEmpty() && !connected[neighbor]){
                connectIJ(root, neighbor);
                connected[neighbor] = true;
                if (dfs(neighbor)){
                    return true;
                }
            }
        }
        return t;
    }

    private boolean haveNeighbor(int i){
        for (int j : neighbors(i)){
            if (!existFloors.get(j).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private List<Integer> neighbors(int i){
        int x = i % 9;
        int y = i / 9;
        List<Integer> res = new ArrayList<>();
        for (int b = -1; b <= 1; b++){
            for (int a = -1; a <= 1; a++){
                if (!(a == 0 && b == 0) && a + x >= 0 && a + x < 9 && b + y >= 0 && b + y < 5){
                    res.add(a + x + 9 * (b + y));
                }
            }
        }
        return res;
    }

    private void connectIJ(int i, int j){
        Pair<Integer, Integer> pi = existFloorsList.get(i).get(RANDOM.nextInt(existFloors.get(i).size()));
        Pair<Integer, Integer> pj = existFloorsList.get(j).get(RANDOM.nextInt(existFloors.get(j).size()));
        connectTwoPoints(pi, pj);
    }

    private void connectTwoPoints(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2){
        if (RANDOM.nextBoolean()){
            for (int i = Math.min(p1.a, p2.a); i <= Math.max(p1.a, p2.a); i ++){
                tiles[i][p1.b] = Tileset.FLOOR;
            }
            for (int i = Math.min(p1.b, p2.b); i <= Math.max(p1.b, p2.b); i += 1){
                tiles[p2.a][i] = Tileset.FLOOR;
            }
        } else{
            for (int i = Math.min(p1.a, p2.a); i <= Math.max(p1.a, p2.a); i ++){
                tiles[i][p2.b] = Tileset.FLOOR;
            }
            for (int i = Math.min(p1.b, p2.b); i <= Math.max(p1.b, p2.b); i += 1) {
                tiles[p1.a][i] = Tileset.FLOOR;
            }
        }
    }

    private void generateWalls(){
        for (int x = 0; x <width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y] == Tileset.FLOOR){
                    if (x + 1 < width && tiles[x+1][y] == Tileset.NOTHING){
                        tiles[x+1][y] = Tileset.WALL;
                    }
                    if (x - 1 >= 0 && tiles[x-1][y] == Tileset.NOTHING){
                        tiles[x-1][y] = Tileset.WALL;
                    }
                    if (y + 1 < height && tiles[x][y+1] == Tileset.NOTHING){
                        tiles[x][y+1] = Tileset.WALL;
                    }
                    if (y - 1 >= 0 && tiles[x][y-1] == Tileset.NOTHING){
                        tiles[x][y-1] = Tileset.WALL;
                    }
                    if (x + 1 < width && y + 1 < height && tiles[x+1][y+1] == Tileset.NOTHING){
                        tiles[x+1][y+1] = Tileset.WALL;
                    }
                    if (x + 1 < width && y - 1 >= 0 && tiles[x+1][y-1] == Tileset.NOTHING){
                        tiles[x+1][y-1] = Tileset.WALL;
                    }
                    if (x - 1 >= 0 && y + 1 < height && tiles[x-1][y+1] == Tileset.NOTHING){
                        tiles[x-1][y+1] = Tileset.WALL;
                    }
                    if (x - 1 >= 0 && y - 1 >= 0 && tiles[x-1][y-1] == Tileset.NOTHING){
                        tiles[x-1][y-1] = Tileset.WALL;
                    }
                }
            }
        }
    }


    public TETile[][] getTiles(){
        return tiles;
    }

}
