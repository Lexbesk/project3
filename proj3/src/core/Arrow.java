package core;

import tileengine.TETile;
import tileengine.Tileset;

public class Arrow{

    int x;
    int y;
    int x_speed;
    int y_speed;
    int stage;
    public Arrow(int x, int y, int x_speed, int y_speed) {
        this.x = x;
        this.y = y;
        this.x_speed = x_speed;
        this.y_speed = y_speed;
        this.stage = 0;

    }

    public void put(double[][] lightLayer, TETile[][] characterLayer){
        characterLayer[x][y] = Tileset.ARROW;
        for (int i = -2; i <= 2; i += 1){
            for (int j = -2; j <= 2; j += 1){
                int distance = Math.max(Math.abs(i), Math.abs(j));
                if (x + i >= 0 && x + i < lightLayer.length && y + j >= 0 && y + j < lightLayer[0].length){
                    lightLayer[x+i][y+j] = Math.min(1, lightLayer[x+i][y+j] + 1 - distance * 0.2);
                }
            }
        }
    }

    public boolean move(TETile[][] worldTiles){
        if (x + x_speed >= 0 && x + x_speed < worldTiles.length && y + y_speed >= 0 && y + y_speed < worldTiles[0].length){
            boolean b = true;
            for (int i = 0; i < Math.max(Math.abs(x_speed), Math.abs(y_speed)); i += 1) {
                if (worldTiles[(int) (x + Math.signum(x_speed) * Math.min(Math.abs(x_speed), i))][(int) (y + Math.signum(y_speed)*Math.min(Math.abs(y_speed), i))] == Tileset.WALL) {
                    b = false;
                    break;
                }
            }
            if (b) {
                x += x_speed;
                y += y_speed;
            }
        }
        stage += 1;
        return stage < 7;
    }
}
