package core;

import tileengine.TERenderer;
import tileengine.TETile;

public class Main {
    public static void main(String[] args) {

        int WIDTH = 90;
        int HEIGHT = 50;
        int SEED = 273;
        double DEGREE = 0.5;

        TERenderer ter = new TERenderer();
        World world = new World(WIDTH, HEIGHT, SEED, DEGREE);
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] worldTiles = world.getTiles();

        ter.renderFrame(worldTiles);


    }
}
