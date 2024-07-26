# Build Your Own World (BYOW)
A software engineering design project for **CS61BL: Data Structures and Programming Methodology**. We created a world exploration engine to generate explorable worlds by developing a pseudorandom algorithm that supports user input. The user input is known as a *seed*, which determines how the world is generated. We also implemented game-like mechanics such as an interactable HUD, world element modification post-generation, and the ability to win. The link to the original project specifications can be found [here](https://cs61bl.org/su22/projects/byow/).

Date Completed: Summer 2022

## Classes

### Engine
*Deals with how the world is displayed to the user following user input, such as character movement, saving current progress, and altering the world*

### Input
*Interface created for the handling of user input and text-file input*

### KeyInput 
*Deals with real-time user input, and passes such inputs to the Engine class to update the world*

### StringInput
*Deals with text-file input for loading a user's save point after they have exited the game*

### Main
*Parses command line inputs*

### RandomUtils
*Generates random numbers based on user input for the generation of random worlds*

### Utils
*Used to read and parse a user's save file to regenerate the same engine*

### World
*Stores the pseudorandom world generation algorithm, the functions that allow a character to move throughout the world, and keeps track of the current state of the world*

---
**Collaborators: Layla Schweng (Duke University, '25)**
