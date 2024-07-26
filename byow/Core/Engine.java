package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;
import java.awt.*;
import java.io.File;
import static byow.Core.Utils.*;

public class Engine {
    TERenderer ter = new TERenderer();
    public static final int WIDTH = 80;
    public static final int HEIGHT = 50;
    public static final File CWD = new File(System.getProperty("user.dir"));
    private File stored = new File(CWD, "stored.txt");
    private World newWorld;
    private World limitedWorld;
    private int visionRestriction = 5;

    public void writeString(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);

        StdDraw.show();
        StdDraw.setFont();
    }

    public void drawMenu() {
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT - 10, "The Maze Game");
        StdDraw.text(WIDTH / 2, HEIGHT - 20, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT - 25, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT - 30, "Replay (R)");
        StdDraw.text(WIDTH / 2, HEIGHT - 35, "Quit (Q)");

        StdDraw.show();
        StdDraw.setFont();
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        Input input = new KeyInput();
        ter.initialize(WIDTH, HEIGHT);
        drawMenu();
        long seed = 0;

        String sd = "";
        String save = "";
        while (input.possibleNextInput()) {
            char c = input.getNextKey();
            save += c;
            if (c == 'N') {
                StdDraw.clear(Color.BLACK);
                StdDraw.show();
                c = input.getNextKey();
                save += c;
                while (c != 'S') {
                    sd += c;
                    writeString(sd);
                    c = input.getNextKey();
                    save += c;
                }
                seed = Long.parseLong(sd);
                if (stored.exists()) {
                    String old = readContentsAsString(stored);
                    old = old.substring(1, old.indexOf('S'));
                    if (!old.equals(sd)) {
                        stored.delete();
                    }
                }
                break;
            } else if (c == 'L') {
                if (stored.exists()) {
                    String savedWorld = readContentsAsString(stored);
                    ter.renderFrame(interactWithInputString(savedWorld));

                    interactWithWorld(input, save);
                } else {
                    System.exit(0);
                }
                break;
            } else if (c == 'R') {
                replay();
                return;
            } else if (c == ':') {
                c = input.getNextKey();
                if (c == 'Q') {
                    System.exit(0);
                }
            }
        }
        if (save.length() >= 2 && save.toCharArray()[save.length() - 2] == 'L') {
            newWorld = newWorld;
        } else if (save.length() == 1 && save.toCharArray()[0] == 'L') {
            newWorld = newWorld;
        } else {
            createWorld(seed);
            createLimitedWorld(seed);
        }

        interactWithWorld(input, save);
    }

    public void showCurrMouseTile(double currX, double currY, World currWorld) {
        TETile currMouseTile;
        if (currX > WIDTH - 1 || currY > HEIGHT - 1) {
            currMouseTile = Tileset.NOTHING;
        } else {
            currMouseTile = currWorld.returnWorld()[(int) currX][(int) currY];
        }
        Font font = new Font("Monaco", Font.BOLD, 14);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);

        String toBeShown = "";

        if (currMouseTile.description().equals("nothing")) {
            toBeShown = "You see a bunch of " + currMouseTile.description() + "ness. Scary.";
        } else if (currMouseTile.description().equals("floor")) {
            toBeShown = "You see a cold, hard " + currMouseTile.description() + ".";
        } else if (currMouseTile.description().equals("you")) {
            toBeShown = "That's you. What are you waiting for? Move!";
        } else if (currMouseTile.description().equals("crown")) {
            toBeShown = "You see a shiny crown. Maybe you should head over to it and pick it up.";
        } else if (currMouseTile.description().equals("flower")) {
            toBeShown = "You see a flower on the ground. How'd it get there?";
        } else if (currMouseTile.description().equals("grass")) {
            toBeShown = "You see some grass growing out of the ground. Cool.";
        } else {
            toBeShown = "You see a " + currMouseTile.description() + ".";
        }

        StdDraw.textLeft(2, HEIGHT - 1, toBeShown);
        boolean test = currWorld.getCurrTile().equals(Tileset.FLOOR);
        String grass;

        if (test) {
            grass = "Press G for grass!";
        } else {
            grass = "Press G to get rid of the grass!";
        }

        StdDraw.textRight(WIDTH - 1, HEIGHT -1, grass);

        StdDraw.show();
    }

    public TETile[][] interactWithInputString(String argInput) {
        Input input = new StringInput(argInput);
        long seed = 0;
        String stringSeed = "";
        String currWorld = "";
        while (input.possibleNextInput()) {
            char c = input.getNextKey();
            currWorld += c;
            if (c == 'N') {
                c = input.getNextKey();
                currWorld += c;
                while (c != 'S') {
                    stringSeed += c;
                    c = input.getNextKey();
                    currWorld += c;
                }
                seed = Long.parseLong(stringSeed);
                if (stored.exists()) {
                    String old = readContentsAsString(stored);
                    old = old.substring(1, old.indexOf('S'));
                    if (!old.equals(stringSeed)) {
                        stored.delete();
                    }
                }
                break;
            } else if (c == 'L') {
                if (stored.exists()) {
                    String savedWorld = readContentsAsString(stored);
                    interactWithInputString(savedWorld);
                }
                break;
            } else if (c == 'R') {
                replay();
                return newWorld.returnWorld();
            }
        }
        if (currWorld.length() >= 2 && currWorld.toCharArray()[currWorld.length() - 2] == 'L') {
            newWorld = newWorld;
        } else if (currWorld.length() == 1 && currWorld.toCharArray()[0] == 'L') {
            newWorld = newWorld;
        } else {
            createWorld(seed);
            createLimitedWorld(seed);
        }

        while (input.possibleNextInput()) {
            char c = input.getNextKey();
            currWorld += c;
            if (c == ':') {
                c = input.getNextKey();
                currWorld += c;
                if (c == 'Q') {
                    if (stored.exists()) {
                        String oldSting = readContentsAsString(stored);
                        currWorld = oldSting + currWorld;
                    }
                    writeContents(stored, currWorld.substring(0, currWorld.length() - 2));
                    break;
                }
            } else if (c == 'W') {
                newWorld.getAvatar().move(1);
                limitedWorld.getAvatar().move(1);
            } else if (c == 'D') {
                newWorld.getAvatar().move(2);
                limitedWorld.getAvatar().move(2);
            } else if (c == 'S') {
                newWorld.getAvatar().move(3);
                limitedWorld.getAvatar().move(3);
            } else if (c == 'A') {
                newWorld.getAvatar().move(4);
                limitedWorld.getAvatar().move(4);
            }
        }

        TETile[][] finalWorldFrame = newWorld.returnWorld();
        return finalWorldFrame;
    }

    private void createWorld(long input) {
        newWorld = new World(WIDTH, HEIGHT, input);
        newWorld.emptyWorld();
        newWorld.randomWorld();
    }

    private void createLimitedWorld(long input) {
        limitedWorld = new World(WIDTH, HEIGHT, input);
        limitedWorld.emptyWorld();
        limitedWorld.randomWorld();
    }

    public void interactWithWorld(Input input, String save) {
        boolean x = true;
        boolean FOVRestricted = false;

        World currWorld = newWorld;
        World otherWorld = limitedWorld;
        World switchPlaceholder = null;

        boolean currTileChange = false;

        while (x && !newWorld.getAvatar().wasCrown()) {
            showCurrMouseTile(StdDraw.mouseX(), StdDraw.mouseY(), currWorld);

            if (StdDraw.hasNextKeyTyped()) {
                char c = input.getNextKey();
                save += c;
                if (c == ':') {
                    c = input.getNextKey();
                    save += c;
                    if (c == 'Q') {
                        if (stored.exists()) {
                            String oldSting = readContentsAsString(stored);
                            save = oldSting + save;
                        }
                        writeContents(stored, save.substring(0, save.length() - 2));
                        System.exit(0);
                    }
                } else if (c == 'W') {
                    currWorld.getAvatar().move(1);
                    otherWorld.getAvatar().move(1);

                    if (FOVRestricted) {
                        TETile[][] toBeMoved = currWorld.returnWorld();
                        otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                    } else {
                        TETile[][] toBeMoved = otherWorld.returnWorld();
                        otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                    }
                } else if (c == 'D') {
                    currWorld.getAvatar().move(2);
                    otherWorld.getAvatar().move(2);

                    if (FOVRestricted) {
                        TETile[][] toBeMoved = currWorld.returnWorld();
                        otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                    } else {
                        TETile[][] toBeMoved = otherWorld.returnWorld();
                        otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                    }
                } else if (c == 'S') {
                    currWorld.getAvatar().move(3);
                    otherWorld.getAvatar().move(3);

                    if (FOVRestricted) {
                        TETile[][] toBeMoved = currWorld.returnWorld();
                        otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());

                    } else {
                        TETile[][] toBeMoved = otherWorld.returnWorld();
                        otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                    }
                } else if (c == 'A') {
                    currWorld.getAvatar().move(4);
                    otherWorld.getAvatar().move(4);

                    if (FOVRestricted) {
                        TETile[][] toBeMoved = currWorld.returnWorld();
                        otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                    } else {
                        TETile[][] toBeMoved = otherWorld.returnWorld();
                        otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                    }
                } else if (c == 'L') {

                    FOVRestricted = !FOVRestricted;

                    switchPlaceholder = currWorld;
                    currWorld = otherWorld;
                    otherWorld = switchPlaceholder;

                    if (FOVRestricted) {
                        TETile[][] toBeMoved = currWorld.returnWorld();
                        otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                    } else {
                        TETile[][] toBeMoved = otherWorld.returnWorld();
                        otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                    }
                } else if (c == 'G') {
                    currTileChange = !currTileChange;
                    if (currTileChange) {
                        if (!FOVRestricted) {
                            floorChanger(currWorld, Tileset.GRASS);
                        } else {
                            floorChanger(currWorld, Tileset.GRASS);
                            floorChanger(otherWorld, Tileset.GRASS);
                        }
                    } else {
                        if (!FOVRestricted) {
                            floorChanger(currWorld, Tileset.FLOOR);
                        } else {
                            floorChanger(currWorld, Tileset.FLOOR);
                            floorChanger(otherWorld, Tileset.FLOOR);
                        }
                    }
                }
            }
            ter.renderFrame(currWorld.returnWorld());
        }
        winScreen();
    }

    public void floorChanger(World currWorld, TETile desired) {

        currWorld.changeCurrTile(desired);

        TETile[][] currWorldTiles = currWorld.returnWorld();

        if (desired.equals(Tileset.FLOOR)) {
            for (int i = 0; i < WIDTH; i++) {
                currWorld.changeCurrTile(Tileset.FLOOR);

                for (int k = 0; k < HEIGHT; k++) {
                    if (currWorldTiles[i][k].equals(Tileset.GRASS)) {
                        currWorldTiles[i][k] = currWorld.getCurrTile();
                        newWorld.returnWorld()[i][k] = desired;
                    }
                }
            }
        } else {
            for (int i = 0; i < WIDTH; i++) {
                currWorld.changeCurrTile(Tileset.GRASS);

                for (int k = 0; k < HEIGHT; k++) {
                    if (currWorldTiles[i][k].equals(Tileset.FLOOR)) {
                        currWorldTiles[i][k] = currWorld.getCurrTile();
                    }
                }
            }
        }
    }

    public void otherWorldMover(TETile[][] otherWorld, int currPosX, int currPosY) {

        for (int i = 0; i < WIDTH; i ++) {
            for (int k = 0; k < HEIGHT; k ++) {
                if (i < currPosX - visionRestriction || i > currPosX + visionRestriction) {
                    otherWorld[i][k] = Tileset.NOTHING;
                } else if (k < currPosY - visionRestriction || k > currPosY + visionRestriction) {
                    otherWorld[i][k] = Tileset.NOTHING;
                } else {
                    otherWorld[i][k] = newWorld.returnWorld()[i][k];
                }
            }
        }
    }

    public void winScreen() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Congratulations! You found the Crown!");

        StdDraw.show();
        StdDraw.setFont();
    }

    public void replay() {
        if (!stored.exists()) {
            System.exit(0);
        }
        boolean FOVRestricted = false;

        World currWorld = newWorld;
        World otherWorld = limitedWorld;
        World switchPlaceholder = null;
        boolean currTileChange = false;

        String savedInputs = readContentsAsString(stored);
        Input input = new StringInput(savedInputs);
        String stringSeed = "";
        while (input.possibleNextInput()) {
            char c = input.getNextKey();
            if (c == 'N') {
                c = input.getNextKey();
                while (c != 'S') {
                    stringSeed += c;
                    c = input.getNextKey();
                }
                long seed = Long.parseLong(stringSeed);
                newWorld = new World(WIDTH, HEIGHT, seed);
                newWorld.emptyWorld();
                newWorld.randomWorld();

                currWorld = newWorld;
                limitedWorld = new World(WIDTH, HEIGHT, seed);
                limitedWorld.emptyWorld();
                limitedWorld.randomWorld();

                otherWorld = limitedWorld;

                ter.renderFrame(currWorld.returnWorld());
                StdDraw.pause(250);
            } else if (c == 'W') {
                currWorld.getAvatar().move(1);
                otherWorld.getAvatar().move(1);

                if (FOVRestricted) {
                    TETile[][] toBeMoved = currWorld.returnWorld();
                    otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                } else {
                    TETile[][] toBeMoved = otherWorld.returnWorld();
                    otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                }

                ter.renderFrame(currWorld.returnWorld());
                StdDraw.pause(500);
            } else if (c == 'D') {
                currWorld.getAvatar().move(2);
                otherWorld.getAvatar().move(2);

                if (FOVRestricted) {
                    TETile[][] toBeMoved = currWorld.returnWorld();
                    otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                } else {
                    TETile[][] toBeMoved = otherWorld.returnWorld();
                    otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                }

                ter.renderFrame(currWorld.returnWorld());
                StdDraw.pause(500);
            } else if (c == 'S') {
                currWorld.getAvatar().move(3);
                otherWorld.getAvatar().move(3);

                if (FOVRestricted) {
                    TETile[][] toBeMoved = currWorld.returnWorld();
                    otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                } else {
                    TETile[][] toBeMoved = otherWorld.returnWorld();
                    otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                }

                ter.renderFrame(currWorld.returnWorld());
                StdDraw.pause(500);
            } else if (c == 'A') {
                currWorld.getAvatar().move(4);
                otherWorld.getAvatar().move(4);

                if (FOVRestricted) {
                    TETile[][] toBeMoved = currWorld.returnWorld();
                    otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                } else {
                    TETile[][] toBeMoved = otherWorld.returnWorld();
                    otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                }
                ter.renderFrame(currWorld.returnWorld());
                StdDraw.pause(500);
            } else if (c == 'L') {
                FOVRestricted = !FOVRestricted;

                switchPlaceholder = currWorld;
                currWorld = otherWorld;
                otherWorld = switchPlaceholder;

                if (FOVRestricted) {
                    TETile[][] toBeMoved = currWorld.returnWorld();
                    otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                } else {
                    TETile[][] toBeMoved = otherWorld.returnWorld();
                    otherWorldMover(toBeMoved, currWorld.getAvatar().getX(), currWorld.getAvatar().getY());
                }
                ter.renderFrame(currWorld.returnWorld());
                StdDraw.pause(500);
            } else if (c == 'G') {
                currTileChange = !currTileChange;
                if (currTileChange) {
                    if (!FOVRestricted) {
                        floorChanger(currWorld, Tileset.GRASS);
                    } else {
                        floorChanger(currWorld, Tileset.GRASS);
                        floorChanger(otherWorld, Tileset.GRASS);
                    }
                } else {
                    if (!FOVRestricted) {
                        floorChanger(currWorld, Tileset.FLOOR);
                    } else {
                        floorChanger(currWorld, Tileset.FLOOR);
                        floorChanger(otherWorld, Tileset.FLOOR);
                    }
                }
                ter.renderFrame(currWorld.returnWorld());
                StdDraw.pause(500);
            }
        }
    }
}
