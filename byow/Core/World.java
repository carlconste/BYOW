package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.*;

public class World {
    private TETile[][] world;
    private Avatar avatar;
    private int width;
    private int height;
    private Random rand;
    private TETile currTile = Tileset.FLOOR;
    /** allRooms is a list of all created rooms */
    private ArrayList<Room> allRooms = new ArrayList<>();

    public static void main(String[] args) {
        Engine e = new Engine();
        e.ter.initialize(80, 50);

        TETile[][] finalWorldFrame = e.interactWithInputString(args[1]);

        e.ter.renderFrame(finalWorldFrame);
    }

    /** World constructor that takes in width, height, and a seed */
    public World(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.rand = new Random(seed);
        this.world = new TETile[width][height];
    }

    /** Returns the current TETile state of the world */
    public TETile[][] returnWorld() {
        return this.world;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void changeCurrTile(TETile tile) {
        currTile = tile;
    }

    public TETile getCurrTile() {
        return currTile;
    }

    /** Fills in the world with Nothing tiles */
    public void emptyWorld() {
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                this.world[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void placeCrown() {
        int num = rand.nextInt(1, allRooms.size() - 1);
        Room r = allRooms.get(num);
        world[(r.width / 2) + r.bLeft.x] [(r.height / 2) + r.bLeft.y] = Tileset.CROWN;
    }

    /** Creates origin room, where all created rooms and paths will come from. */
    public void randomWorld() {
        Point originPoint = new Point(rand.nextInt(30, 60), rand.nextInt(15, 30));
        Room originRoom = new Room(rand.nextInt(5, 10), rand.nextInt(3, 8), originPoint);
        buildFromPoint(originRoom);
        allRooms.add(originRoom);
        avatar = new Avatar((originRoom.width) / 2 + originRoom.bLeft.x,
                (originRoom.height) / 2 + originRoom.bLeft.y);

        recursiveRooms(originRoom);
        placeCrown();
    }

    /** Recursively creates rooms from an origin room until
     * branchRooms is greater than the allocated amount of rooms for that room
     * or until a room goes out of bounds. */
    public void recursiveRooms(Room originRoom) {
        int branchRooms = 0;
        int randRoomAttempts = 0;
        int roomNum = rand.nextInt(1, 4);
        while (branchRooms < roomNum) {
        //while (allRooms.size() < 4) {
            int dir = rand.nextInt(1, 5);
            Point wallPoint = randomWallPoint(originRoom, dir);
            Room randRoom = randomRoom(wallPoint);
            randRoomAttempts += 1;
            if (!wallCollide(randRoom) && !randRoom.overlap()) {
                allRooms.add(randRoom);
                buildFromRoom(randRoom, originRoom);
                branchRooms += 1;
                recursiveRooms(randRoom);
            } else if (wallCollide(randRoom) || randRoomAttempts > 3) {
                return;
            }
        }
    }

    /** Uses Tileset and TETile to create approved rooms in world off a Point */
    public void buildFromPoint(Room room) {
        for (int i = 0; i < room.width + 2; i++) {
            Point bottomWall = new Point(room.bLeft.x - 1, room.bLeft.y - 1);
            Point topWall = new Point(room.tRight.x + 1, room.tRight.y + 1);
            world[bottomWall.x + i][bottomWall.y] = Tileset.WALL;
            world[topWall.x - i][topWall.y] = Tileset.WALL;
        }

        for (int i = 0; i < room.height; i++) {
            Point leftWall = new Point(room.bLeft.x - 1, room.bLeft.y);
            Point rightWall = new Point(room.tRight.x + 1, room.tRight.y);
            world[leftWall.x][leftWall.y + i] = Tileset.WALL;
            world[rightWall.x][rightWall.y - i] = Tileset.WALL;
        }

        for (int i = 0; i < room.height; i++) {
            Point start = new Point(room.bLeft.x, room.bLeft.y + i);
            for (int k = 0; k < room.width; k++) {
                world[start.x + k][start.y] = Tileset.FLOOR;
            }
        }
    }

    /** Uses Tileset and TETile to create approved rooms in world off another room */
    public void buildFromRoom(Room newRoom, Room originRoom) {
        buildFromPoint(newRoom);

        if (newRoom.dir == 1) {
            HashSet<Integer> tWall = wallPoints(originRoom, 1);
            HashSet<Integer> newBWall = wallPoints(newRoom, 3);
            int y = newRoom.bLeft.y - 1;

            for (Integer p : newBWall) {
                if (tWall.contains(p)) {
                    world[p][y] = Tileset.FLOOR;
                }
            }
        } else if (newRoom.dir == 2) {
            HashSet<Integer> rWall = wallPoints(originRoom, 2);
            HashSet<Integer> newLWall = wallPoints(newRoom, 4);
            int x = newRoom.bLeft.x - 1;

            for (Integer p : rWall) {
                if (newLWall.contains(p)) {
                    world[x][p] = Tileset.FLOOR;
                }
            }
        } else if (newRoom.dir == 3) {
            HashSet<Integer> bWall = wallPoints(originRoom, 3);
            HashSet<Integer> newTWall = wallPoints(newRoom, 1);
            int y = newRoom.tRight.y + 1;

            for (Integer p : newTWall) {
                if (bWall.contains(p)) {
                    world[p][y] = Tileset.FLOOR;
                }
            }
        } else if (newRoom.dir == 4) {
            HashSet<Integer> lWall = wallPoints(originRoom, 4);
            HashSet<Integer> newRWall = wallPoints(newRoom, 2);
            int x = newRoom.tRight.x + 1;

            for (Integer p : newRWall) {
                if (lWall.contains(p)) {
                    world[x][p] = Tileset.FLOOR;
                }
            }
        }
    }

    /** Creates and returns a set of all the points of
     * the dir designated wall of a room */
    public HashSet<Integer> wallPoints(Room room, int dir) {
        HashSet<Integer> points = new HashSet<>();

        if (dir == 1) {
            Point topWall = new Point(room.tRight.x, room.tRight.y + 1);
            for (int i = 0; i < room.width; i++) {
                points.add(topWall.x - i);
            }
        } else if (dir == 2) {
            Point rightWall = new Point(room.tRight.x + 1, room.tRight.y);
            for (int i = 0; i < room.height; i++) {
                points.add(rightWall.y - i);
            }
        } else if (dir == 3) {
            Point bottomWall = new Point(room.bLeft.x, room.bLeft.y - 1);
            for (int i = 0; i < room.width; i++) {
                points.add(bottomWall.x + i);
            }
        } else if (dir == 4) {
            Point leftWall = new Point(room.bLeft.x - 1, room.bLeft.y);
            for (int i = 0; i < room.height; i++) {
                points.add(leftWall.y + i);
            }
        }

        return points;
    }

    /** Generates a randomly sized room/path based on point.dir
     * and returns the newly created room */
    public Room randomRoom(Point point) {
        int hall = rand.nextInt(0, 2);
        int w = 0;
        int h = 0;
        if (hall == 0) {
            w = rand.nextInt(2, 8);
            h = rand.nextInt(2, 8);
        } else {
            if (point.dir == 1 || point.dir == 3) {
                w = 1;
                h = rand.nextInt(3, 5);
            } else if (point.dir == 2 || point.dir == 4) {
                w = rand.nextInt(3, 6);
                h = 1;
            }
        }

        Room newRoom = null;

        if (point.dir == 1) {
            newRoom = new Room(w, h, point);
            newRoom.dir = 1;
        } else if (point.dir == 2) {
            newRoom = new Room(w, h, 2);
            newRoom.bLeft = new Point(point.x + 2, point.y - h + 1);
            newRoom.tRight = new Point(point.x + w + 1, point.y);
        } else if (point.dir == 3) {
            newRoom = new Room(w, h, 3);
            newRoom.bLeft = new Point(point.x - w + 1, point.y - h - 1);
            newRoom.tRight = new Point(point.x, point.y - 2);
        } else if (point.dir == 4) {
            newRoom = new Room(w, h, 4);
            newRoom.bLeft = new Point(point.x - w - 1, point.y);
            newRoom.tRight = new Point(point.x - 2, point.y + h - 1);
        }

        return newRoom;
    }

    /** Generates a random Point on the wall of a Room based
     * on the dir. */
    public Point randomWallPoint(Room initialRoom, int dir) {
        int x = 0;
        int y = 0;
        Point p = null;
        if (dir == 1) {
            if (initialRoom.bLeft.x == initialRoom.tRight.x) {
                x = initialRoom.bLeft.x;
            } else {
                x = rand.nextInt(initialRoom.bLeft.x, initialRoom.tRight.x);
            }
            y = initialRoom.tRight.y;
        } else if (dir == 2) {
            if (initialRoom.bLeft.y == initialRoom.tRight.y) {
                y = initialRoom.tRight.y;
            } else {
                y = rand.nextInt(initialRoom.bLeft.y, initialRoom.tRight.y);
            }
            x = initialRoom.tRight.x;
        } else if (dir == 3) {
            if (initialRoom.bLeft.x == initialRoom.tRight.x) {
                x = initialRoom.bLeft.x;
            } else {
                x = rand.nextInt(initialRoom.bLeft.x, initialRoom.tRight.x);
            }
            y = initialRoom.bLeft.y;
        } else if (dir == 4) {
            if (initialRoom.bLeft.y == initialRoom.tRight.y) {
                y = initialRoom.bLeft.y;
            } else {
                y = rand.nextInt(initialRoom.bLeft.y, initialRoom.tRight.y);
            }
            x = initialRoom.bLeft.x;
        }

        p = new Point(x, y);
        p.dir = dir;
        return p;
    }

    /** Checks if the room will go out of the bounds of current world
     * based on the room's bLeft and tRight points. */
    public boolean wallCollide(Room room) {
        boolean collide = false;

        if (room.bLeft.x - 1 < 2 || room.bLeft.y - 1 < 2) {
            collide = true;
        } else if (room.tRight.x + 1 > width - 2 || room.tRight.y + 1 > height - 2) {
            collide = true;
        }

        return collide;
    }

    /** Room class creates rooms with a height and width and tracks
     * the rooms bottom left and top right corners. */
    public class Room {
        // a path is just a room with width 1
        private Point bLeft;
        private Point tRight;
        private int width;
        private int height;
        private int dir;

        /** Creates a Room object with random width and height.
         * Sets the corners to points based on origin point. */
        public Room(int w, int h, Point startPoint) {
            // builds a north facing room
            this.width = w;
            this.height = h;
            this.bLeft = new Point(startPoint.x, startPoint.y + 2);
            this.tRight = new Point(startPoint.x + w - 1, startPoint.y + h + 1);
        }

        public Room(int w, int h, int dir) {
            this.width = w;
            this.height = h;
            this.dir = dir;
        }

        /** Checks if building newRoom from startPoint will overlap with
         * any other rooms. Returns false if none overlap and true if
         * there is overlap.
         * need to change so that when rooms are generated they are generated
         * so that they don't overlap with the walls of the room they originated from*/
        public boolean overlap() {
            for (Room r : allRooms) {
                HashSet<Point> entireR = r.allFloorPoints();
                HashSet<Point> entireCurr = this.floorAndWallPoints();
                for (Point p : entireCurr) {
                    if (entireR.contains(p)) {
                        return true;
                    }
                }
            }
            return false;
        }

        /** Puts all coordinates of the walls and floor of a room into a set */
        private HashSet<Point> floorAndWallPoints() {
            HashSet<Point> points = new HashSet<>();
            for (int i = 0; i < this.height + 2; i++) {
                Point start = new Point(this.bLeft.x - 1, this.bLeft.y - 1 + i);
                for (int k = 0; k < this.width + 2; k++) {
                    points.add(new Point(start.x + k, start.y));
                }
            }

            return points;
        }

        /** Puts all coordinates of the floor of a room into a set */
        private HashSet<Point> allFloorPoints() {
            HashSet<Point> allPoints = new HashSet<>();
            for (int i = 0; i < this.height; i++) {
                Point start = new Point(this.bLeft.x, this.bLeft.y + i);
                for (int k = 0; k < this.width; k++) {
                    allPoints.add(new Point(start.x + k, start.y));
                }
            }

            return allPoints;
        }

    }

    /** Avatar class keeps track of where the avatar is in the world */
    public class Avatar {
        private Point currSpot;
        private boolean prevCrown = false;

        public Avatar(int x, int y) {
            this.currSpot = new Point(x, y);
            world[currSpot.x][currSpot.y] = Tileset.AVATAR;
        }

        public void move(int dir) {
            if (dir == 1) {
                if (canMove(currSpot.x, currSpot.y + 1)) {
                    moveUp();
                }
            } else if (dir == 2) {
                if (canMove(currSpot.x + 1, currSpot.y)) {
                    moveRight();
                }
            } else if (dir == 3) {
                if (canMove(currSpot.x, currSpot.y - 1)) {
                    moveDown();
                }
            } else if (dir == 4) {
                if (canMove(currSpot.x - 1, currSpot.y)) {
                    moveLeft();
                }
            }
        }

        public boolean canMove(int x, int y) {
            return world[x][y] != Tileset.WALL;
        }

        public boolean isCrown(int x, int y) {
            return world[x][y] == Tileset.CROWN;
        }

        public boolean wasCrown() {
            return prevCrown;
        }

        public void moveRight() {
            world[currSpot.x][currSpot.y] = poopATile();
            this.currSpot.x += 1;
            if (isCrown(currSpot.x, currSpot.y)) {
                prevCrown = true;
            }
            world[currSpot.x][currSpot.y] = Tileset.AVATAR;
        }

        public void moveLeft() {
            world[currSpot.x][currSpot.y] = poopATile();
            this.currSpot.x -= 1;
            if (isCrown(currSpot.x, currSpot.y)) {
                prevCrown = true;
            }
            world[currSpot.x][currSpot.y] = Tileset.AVATAR;
        }

        public void moveUp() {
            world[currSpot.x][currSpot.y] = poopATile();
            this.currSpot.y += 1;
            if (isCrown(currSpot.x, currSpot.y)) {
                prevCrown = true;
            }
            world[currSpot.x][currSpot.y] = Tileset.AVATAR;
        }

        public void moveDown() {
            world[currSpot.x][currSpot.y] = poopATile();
            this.currSpot.y -= 1;
            if (isCrown(currSpot.x, currSpot.y)) {
                prevCrown = true;
            }
            world[currSpot.x][currSpot.y] = Tileset.AVATAR;
        }


        public TETile poopATile() {
            int chance = rand.nextInt(0, 20);
            if (chance == 5) {
                return Tileset.FLOWER;
            }
            return currTile;
        }

        public int getX() {
            return currSpot.x;
        }

        public int getY() {
            return currSpot.y;
        }
    }

    /** Point class keeps track of x and y coordinates
     * dir key:
     * 1 = north
     * 2 = east
     * 3 = south
     * 4 = west */
    public static class Point {

        private int x;
        private int y;
        private int dir;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }

            if (o.getClass() != this.getClass()) {
                return false;
            }

            Point other = (Point) o;
            return other.x == this.x && other.y == this.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }
}
