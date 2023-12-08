package core;

import tileengine.TETile;
import tileengine.Tileset;

public class HugeTorch extends Torch {

    boolean lighted;
    public HugeTorch(int x, int y) {
        super(x, y);
        lighted = false;
    }

    @Override
    public void put(double[][] lightLayer){

        if (lighted) {
            for (int i = -10; i <= 10; i += 1) {
                for (int j = -10; j <= 10; j += 1) {
                    int distance = Math.max(Math.abs(i), Math.abs(j));
                    if (x + i >= 0 && x + i < lightLayer.length && y + j >= 0 && y + j < lightLayer[0].length) {
                        lightLayer[x + i][y + j] = Math.min(1, lightLayer[x + i][y + j] + 1 - distance * 0.07);
                    }
                }
            }
        }
    }

    public boolean check_lighted(TETile[][] characterLayer){
        characterLayer[x][y] = Tileset.HUGE_TORCH;
        for (int i = -1; i <= 1; i += 1) {
            for (int j = -1; j <= 1; j += 1) {
                if (x + i >= 0 && x + i < characterLayer.length && y + j >= 0 && y + j < characterLayer[0].length) {
                    if (characterLayer[x+i][y+j] == Tileset.AVATAR && !lighted){
                        lighted = true;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
