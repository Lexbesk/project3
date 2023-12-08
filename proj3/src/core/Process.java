package core;
import java.awt.Font;
import edu.princeton.cs.algs4.StdDraw;
import java.awt.Color;
import java.util.Scanner;

public class Process {


    public String startGame(){
        Font font = new Font("Arial", Font.BOLD, 60);
        StdDraw.setFont(font);
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(255, 255, 255);
        StdDraw.text(45, 25, "Explore Worlds");
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char next = StdDraw.nextKeyTyped();
                if (next == 'q' || next == 'Q') {
                    return "q";
                } else if (next == 'l' || next == 'L') {
                    return "l";
                } else if (next == 'n' || next == 'N') {
                    font = new Font("Arial", Font.BOLD, 30);
                    StdDraw.setFont(font);
                    StdDraw.text(45, 20, "please enter a world seed:");
                    StdDraw.show();
                    StringBuilder seed = new StringBuilder();
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            next = StdDraw.nextKeyTyped();
                            if (Character.isDigit(next)) {
                                seed.append(next);
                                StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
                                StdDraw.filledRectangle(45, 15, 11, 2.5);
                                font = new Font("Arial", Font.PLAIN, 17);
                                StdDraw.setFont(font);
                                StdDraw.setPenColor(StdDraw.BLACK.brighter());
                                StdDraw.textLeft(35, 15, "Enter: " + seed.toString());
                                StdDraw.show();
                            } else if (next == 's' || next == 'S') {
                                return seed.toString();
                            }
                        }
                    }
                }
            }
        }
    }

}
