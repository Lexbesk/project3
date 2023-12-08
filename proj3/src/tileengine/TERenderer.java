package tileengine;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.awt.Font;

/**
 * Utility class for rendering tiles. You do not need to modify this file. You're welcome
 * to, but be careful. We strongly recommend getting everything else working before
 * messing with this renderer, unless you're trying to do something fancy like
 * allowing scrolling of the screen or tracking the avatar or something similar.
 */
public class TERenderer {
    private static final int TILE_SIZE = 16;
    private int width;
    private int height;
    private int xOffset;
    private int yOffset;

    /**
     * Same functionality as the other initialization method. The only difference is that the xOff
     * and yOff parameters will change where the renderFrame method starts drawing. For example,
     * if you select w = 60, h = 30, xOff = 3, yOff = 4 and then call renderFrame with a
     * TETile[50][25] array, the renderer will leave 3 tiles blank on the left, 7 tiles blank
     * on the right, 4 tiles blank on the bottom, and 1 tile blank on the top.
     * @param w width of the window in tiles
     * @param h height of the window in tiles.
     */
    public void initialize(int w, int h, int xOff, int yOff) {
        this.width = w;
        this.height = h;
        this.xOffset = xOff;
        this.yOffset = yOff;
        StdDraw.setCanvasSize(width * TILE_SIZE, height * TILE_SIZE);
        Font font = new Font("Monaco", Font.BOLD, TILE_SIZE - 2);
        StdDraw.setFont(font);      
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.clear(new Color(0, 0, 0));

        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    /**
     * Initializes StdDraw parameters and launches the StdDraw window. w and h are the
     * width and height of the world in number of tiles. If the TETile[][] array that you
     * pass to renderFrame is smaller than this, then extra blank space will be left
     * on the right and top edges of the frame. For example, if you select w = 60 and
     * h = 30, this method will create a 60 tile wide by 30 tile tall window. If
     * you then subsequently call renderFrame with a TETile[50][25] array, it will
     * leave 10 tiles blank on the right side and 5 tiles blank on the top side. If
     * you want to leave extra space on the left or bottom instead, use the other
     * initializatiom method.
     * @param w width of the window in tiles
     * @param h height of the window in tiles.
     */
    public void initialize(int w, int h) {
        initialize(w, h, 0, 0);
    }

    /**
     * Takes in a 2d array of TETile objects and renders the 2d array to the screen, starting from
     * xOffset and yOffset.
     *
     * If the array is an NxM array, then the element displayed at positions would be as follows,
     * given in units of tiles.
     *
     *              positions   xOffset |xOffset+1|xOffset+2| .... |xOffset+world.length
     *                     
     * startY+world[0].length   [0][M-1] | [1][M-1] | [2][M-1] | .... | [N-1][M-1]
     *                    ...    ......  |  ......  |  ......  | .... | ......
     *               startY+2    [0][2]  |  [1][2]  |  [2][2]  | .... | [N-1][2]
     *               startY+1    [0][1]  |  [1][1]  |  [2][1]  | .... | [N-1][1]
     *                 startY    [0][0]  |  [1][0]  |  [2][0]  | .... | [N-1][0]
     *
     * By varying xOffset, yOffset, and the size of the screen when initialized, you can leave
     * empty space in different places to leave room for other information, such as a GUI.
     * This method assumes that the xScale and yScale have been set such that the max x
     * value is the width of the screen in tiles, and the max y value is the height of
     * the screen in tiles.
     * @param world the 2D TETile[][] array to render
     */
    public void renderFrame(TETile[][] world, TETile[][] layer, double[][] lightLayer, int xOff, int yOff, boolean lightUp) {
        int numXTiles = world.length;
        int numYTiles = world[0].length;
        for (int x = 0; x < numXTiles; x += 1) {
            for (int y = 0; y < numYTiles; y += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                if (x + xOff >= 0 && x + xOff < width && y + yOff >= 0 && y + yOff < height) {
                    if (lightUp){
                        world[x][y].draw(x + xOff, y + yOff);
                    } else {
                        Color color = world[x][y].backgroundColor;
                        int blue = color.getBlue();
                        int red = color.getRed();
                        int green = color.getGreen();
                        blue = (int) Math.round(blue * lightLayer[x][y]);
                        red = (int) Math.round(red * lightLayer[x][y]);
                        green = (int) Math.round(green * lightLayer[x][y]);
                        world[x][y].backgroundColor = new Color(red, green, blue);
                        Color text_color = world[x][y].textColor;
                        int text_blue = text_color.getBlue();
                        int text_red = text_color.getRed();
                        int text_green = text_color.getGreen();
                        text_blue = (int) Math.round(text_blue * lightLayer[x][y]);
                        text_red = (int) Math.round(text_red * lightLayer[x][y]);
                        text_green = (int) Math.round(text_green * lightLayer[x][y]);
                        world[x][y].textColor = new Color(text_red, text_green, text_blue);
                        String filepath = world[x][y].filepath;
//                    world[x][y].filepath = null;
                        world[x][y].draw(x + xOff, y + yOff);
                        world[x][y].backgroundColor = color;
                        world[x][y].textColor = text_color;
//                    world[x][y].filepath = filepath;
                    }
                    if (layer[x][y] != null){
                        if (lightUp){
                            layer[x][y].draw(x + xOff, y + yOff);
                        } else {
                            Color color1 = layer[x][y].backgroundColor;
                            int blue1 = color1.getBlue();
                            int red1 = color1.getRed();
                            int green1 = color1.getGreen();
                            blue1 = (int) Math.round(blue1 * lightLayer[x][y]);
                            red1 = (int) Math.round(red1 * lightLayer[x][y]);
                            green1 = (int) Math.round(green1 * lightLayer[x][y]);
                            layer[x][y].backgroundColor = new Color(red1, green1, blue1);
                            Color text_color1 = layer[x][y].textColor;
                            int text_blue1 = text_color1.getBlue();
                            int text_red1 = text_color1.getRed();
                            int text_green1 = text_color1.getGreen();
                            text_blue1 = (int) Math.round(text_blue1 * lightLayer[x][y]);
                            text_red1 = (int) Math.round(text_red1 * lightLayer[x][y]);
                            text_green1 = (int) Math.round(text_green1 * lightLayer[x][y]);
                            layer[x][y].textColor = new Color(text_red1, text_green1, text_blue1);
                            String filepath1 = world[x][y].filepath;
//                    world[x][y].filepath = null;
                            layer[x][y].draw(x + xOff, y + yOff);
                            layer[x][y].backgroundColor = color1;
                            layer[x][y].textColor = text_color1;
//                    world[x][y].filepath = filepath;
                        }
                    }
                }
            }
        }
    }

}
