package core;

import tileengine.TETile;
import tileengine.Tileset;

public class Boss {

    int x;
    int y;

    public Boss(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void put(TETile[][] characterLayer, double[][] lightLayer){
        characterLayer[x][y] = Tileset.BOSS;
        for (int i = -4; i <= 4; i += 1){
            for (int j = -4; j <= 4; j += 1){
                int distance = Math.max(Math.abs(i), Math.abs(j));
                if (x + i >= 0 && x + i < lightLayer.length && y + j >= 0 && y + j < lightLayer[0].length){
                    lightLayer[x+i][y+j] = Math.min(1, lightLayer[x+i][y+j] + 1 - distance * 0.2);
                }
            }
        }
    }

    public boolean move(TETile[][] characterLayer, Main process){
        boolean alive = true;
        for (int i = -4; i <= 4; i += 1){
            for (int j = -4; j <= 4; j += 1){
                int distance = Math.max(Math.abs(i), Math.abs(j));
                if (x + i >= 0 && x + i < characterLayer.length && y + j >= 0 && y + j < characterLayer[0].length){
                    if (characterLayer[x+i][y+j] == Tileset.AVATAR){
                        process.avatarHealth -= 1;
                    }
                    if (characterLayer[x+i][y+j] == Tileset.EXPLODED_BOMB || (distance == 1 && characterLayer[x+i][y+j] == Tileset.ARROW)){
                        alive = false;
                    }
                }
            }
        }
        if (x == process.avatarX && y == process.avatarY){
            process.avatarHealth -= 1;
            return false;
        }
        return alive;
    }
}
