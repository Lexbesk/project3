package core;

import com.github.javaparser.utils.Pair;
import net.sf.saxon.expr.flwor.Tuple;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.*;

public class World {

    private int width;
    private int height;
    private TETile[][] tiles;
    private int seed;
    private Set<Node> existFloors;
    private List<Node> existFloorsList;
    // do not add in to this list manually (need to check repeat).
    private double degree;
    private final Random RANDOM;

    private static class Node{
        int x;
        int y;

        public Node(int x, int y){
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return x * 1000 + y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Node obj1){
                return x == obj1.x && y == obj1.y;
            }
            return false;
        }
    }

    public World(int width, int height, int seed, double degree){
        this.height = height;
        this.width = width;
        this.seed = seed;
        this.degree = degree;
        tiles = new TETile[width][height];
        for (int x = 0; x <width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
        existFloors = new HashSet<>();
        existFloorsList = new ArrayList<>();
        RANDOM = new Random(seed);
        generateWorld0();
    }

    /* put floor to node position and add that node to list and set */
    private void putFloor(Node node){
        if (existFloors.contains(node)){
            return;
        }
        tiles[node.x][node.y] = Tileset.FLOOR;
        existFloors.add(node);
        existFloorsList.add(node);
    }

    private void generateWorld(){

    }

    private void generateIteration(){

    }






    private void generateWorld0(){
        for (int x = 0; x <width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
        int x0 = RANDOM.nextInt(width);
        int y0 = RANDOM.nextInt(height);
        Node node0 = new Node(x0, y0);
        putFloor(node0);
        Node pos = node0;
        int a = 0;
        while (degree * width * height > existFloors.size()){
            if (a == 0){
                buildRoom(pos, 2, 2);
//                int p = RANDOM.nextInt(existFloorsList.size());
//                pos = existFloorsList.get(p);
                a = 1;
            } else{
                Pair<Node, Integer> res = buildHallway(pos);
                pos = res.a;
                if (res.b == 1){
                    int p = RANDOM.nextInt(existFloorsList.size());
                    pos = existFloorsList.get(p);
                    a = 1;
                }
            }
        }
    }

    /* build room at pos (as center) */
    private void buildRoom(Node pos, int w, int h){
        for (int i = -w; i < w + 1; i += 1){
            for (int j = -h; j < h + 1; j += 1){
                if (pos.x + i >= 0 && pos.x + i < width && pos.y + j >= 0 && pos.y + j < height) {
                    putFloor(new Node(pos.x + i, pos.y + j));
                }
            }
        }
    }

    private boolean clear(Node pos, int rangeX, int rangeY){
        for (int i = -rangeX; i <= rangeX; i += 1){
            for (int j = -rangeY; j <= rangeY; j += 1){
                if (existFloors.contains(new Node(pos.x + i, pos.y + j))){
                    return false;
                }
            }
        }
        return true;
    }

    private Pair<Node, Integer> buildHallway(Node pos){
        int r = RANDOM.nextInt(4);
        int lowBound = 3;
        int highBound = 14;
        int bias = 3;
        int hit = 0;
        Node res = pos;
        if (r == 0){
            int l = RANDOM.nextInt(lowBound, highBound + bias);
            for (int i = 0; i < l; i += 1){
                if (pos.x + i < width){
                    Node newFloor = new Node(pos.x + i, pos.y);
                    if (!clear(newFloor, 2, 2)){
                        hit = 1;
                        break;
                    }
                    putFloor(newFloor);
                    res = newFloor;
                } else{
                    break;
                }
            }
        } else if(r == 1){
            int l = RANDOM.nextInt(lowBound, highBound + bias);
            for (int i = 0; i < l; i += 1){
                if (pos.x - i >= 0){
                    Node newFloor = new Node(pos.x - i, pos.y);
                    if (!clear(newFloor, 2, 2)){
                        hit = 1;
                        break;
                    }
                    putFloor(newFloor);
                    res = newFloor;
                } else{
                    break;
                }
            }
        } else if(r == 2){
            int l = RANDOM.nextInt(lowBound, highBound);
            for (int i = 0; i < l; i += 1){
                if (pos.y + i < height){
                    Node newFloor = new Node(pos.x, pos.y + i);
                    if (!clear(newFloor, 2, 2)){
                        hit = 1;
                        break;
                    }
                    putFloor(newFloor);
                    res = newFloor;
                } else{
                    break;
                }
            }
        } else{
            int l = RANDOM.nextInt(lowBound, highBound);
            for (int i = 0; i < l; i += 1){
                if (pos.y - i >= 0){
                    Node newFloor = new Node(pos.x, pos.y - i);
                    if (!clear(newFloor, 2, 2)){
                        hit = 1;
                        break;
                    }
                    putFloor(newFloor);
                    res = newFloor;
                } else{
                    break;
                }
            }
        }
        buildRoom(res, 1, 1);
        return new Pair<>(res, hit);
    }

    public TETile[][] getTiles(){
        return tiles;
    }

}
