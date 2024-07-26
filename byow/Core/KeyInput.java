package byow.Core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

public class KeyInput implements Input {

    @Override
    public char getNextKey() {
        char c;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                c = Character.toUpperCase(StdDraw.nextKeyTyped());
                return c;
            }
        }
    }

    @Override
    public boolean possibleNextInput() {
        return true;
    }
}
