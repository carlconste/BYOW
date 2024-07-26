# Build Your Own World Design Document

**Partner 1: Carl Conste**

**Partner 2: Layla Schweng**

## Classes and Data Structures
### World Class
#### Point Class
#### Room Class

## Algorithms
### fillWithNothing()
Generates a world that contains only Nothing tiles to 
initialize the world.
### randomWorldGenerator()
Using the starting point _Point_ object we will generate 
a randomly sized room. 
### randomPoint()
Using the random object created from new Random(SEED), 
this generates a pseudorandom number of points where 
a new Room/Hall will be created based on the Room that 
is passed through. These Points can only be from the walls
of the Room.
### randomRoomGenerator()
Using the random object created from new Random(SEED), 
this generates a new room object of pseudorandom size.
### overlap()
Ensures that the newly generated Room/Hall has space to be 
made in frame and will not overlap with any existing Room/Hall 
by using the leftCorner and rightCorner variables of Room objects.

## Persistence
****
