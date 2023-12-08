package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

public class Zombie {

    int x;
    int y;
    int strength;
    Random RANDOM;

    public Zombie(int x, int y, int strength, Random RANDOM){
        this.strength = strength;
        this.x = x;
        this.y = y;
        this.RANDOM = RANDOM;
    }

    public void put(TETile[][] characterLayer){
        characterLayer[x][y] = Tileset.ZOMBIE;
    }

    public boolean move(TETile[][] worldTiles, TETile[][] characterLayer, boolean act){
        boolean alive = true;
        for (int i = -4; i <= 4; i += 1){
            for (int j = -4; j <= 4; j += 1){
                int distance = Math.max(Math.abs(i), Math.abs(j));
                if (x + i >= 0 && x + i < characterLayer.length && y + j >= 0 && y + j < characterLayer[0].length){
                    if (characterLayer[x+i][y+j] == Tileset.EXPLODED_BOMB || (distance == 1 && characterLayer[x+i][y+j] == Tileset.ARROW)){
                        alive = false;
                    }
                }
            }
        }
        if (alive && act){
            int num = RANDOM.nextInt(4);
            if (num == 0 && x - 1 >= 0 && worldTiles[x-1][y] != Tileset.WALL){
                x = x-1;
            }
            if (num == 1 && y - 1 >= 0 && worldTiles[x][y-1] != Tileset.WALL){
                y = y-1;
            }
            if (num == 2 && x + 1 < characterLayer.length && worldTiles[x+1][y] != Tileset.WALL){
                x = x+1;
            }
            if (num == 3 && y + 1 < characterLayer[0].length && worldTiles[x][y+1] != Tileset.WALL){
                y = y+1;
            }
        }
        return alive;
    }

}
