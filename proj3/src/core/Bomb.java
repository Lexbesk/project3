package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.Random;

public class Bomb {

    int x;
    int y;
    int stage;

    public Bomb(int x, int y){
        this.x = x;
        this.y = y;
        this.stage = 0;
    }

    public void put(double[][] lightLayer, TETile[][] characterLayer){
        if (stage <= 10){
            characterLayer[x][y] = Tileset.BOMB;
        } else{
            characterLayer[x][y] = Tileset.EXPLODED_BOMB;
        }
        for (int i = -4; i <= 4; i += 1){
            for (int j = -4; j <= 4; j += 1){
                int distance = Math.max(Math.abs(i), Math.abs(j));
                if (((distance >= (stage - 10) / 2 || distance < (stage - 14) / 2) && (stage >= 10)) || (stage < 10 && distance != 0)){
                    continue;
                }
                if (x + i >= 0 && x + i < lightLayer.length && y + j >= 0 && y + j < lightLayer[0].length){
                    lightLayer[x+i][y+j] = Math.min(1, lightLayer[x+i][y+j] + 1 - distance * 0.2);
                }
            }
        }
    }

    public boolean update(){
        this.stage += 1;
        return stage < 22;
    }
}
