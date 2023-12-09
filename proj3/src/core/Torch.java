package core;

public class Torch {

    int x;
    int y;

    public Torch(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void put(double[][] lightLayer){
        for (int i = -6; i <= 6; i += 1){
            for (int j = -6; j <= 6; j += 1){
                int distance = Math.max(Math.abs(i), Math.abs(j));
                if (x + i >= 0 && x + i < lightLayer.length && y + j >= 0 && y + j < lightLayer[0].length){
                    lightLayer[x+i][y+j] = Math.min(1, lightLayer[x+i][y+j] + 1 - distance * 0.1);
                }
            }
        }
    }
}
