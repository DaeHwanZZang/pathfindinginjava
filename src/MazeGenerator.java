/*
Here’s the core idea:
        1. Start with a grid completely filled with walls.
        2. Pick a starting cell, mark it as a path, and "visit" it.
        3. From the current cell, look for unvisited neighboring cells (up, down, left, or right).
        4. If you find any, choose one randomly, knock down the wall between it and the current cell, and move to it.
        5. Repeat the process. If you get stuck (no unvisited neighbors), backtrack to the previous cell and try a different direction.
        6. This continues until all possible cells have been visited, creating a "perfect" maze (one with no loops and a single path between any two points).

        For this to work well, it's best to use a grid with odd dimensions (e.g., 21x21).
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// MazeGenerator class
class MazeGenerator {

    private static void carveMaze(int[][] grid, int cx, int cy, Random rand) {
        int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}}; // N, E, S, W

        List<int[]> dirList = new ArrayList<>();
        for (int[] dir : directions) {
            dirList.add(dir);
        }
        Collections.shuffle(dirList, rand);

        for (int[] dir : dirList) {
            int nx = cx + dir[0] * 2;
            int ny = cy + dir[1] * 2;

            if (nx > 0 && nx < grid[0].length - 1 && ny > 0 && ny < grid.length - 1 && grid[ny][nx] == 1) {
                grid[ny][nx] = 0; // Carve path to neighbor
                grid[cy + dir[1]][cx + dir[0]] = 0; // Carve wall in between
                carveMaze(grid, nx, ny, rand);
            }
        }
    }

    public static int[][] generateMaze(int rows, int cols) {
        if (rows % 2 == 0) rows++;
        if (cols % 2 == 0) cols++;

        int[][] grid = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = 1; // Initialize with walls
            }
        }

        Random rand = new Random();
        int startX = 1;
        int startY = 1;
        grid[startY][startX] = 0; // Start carving from (1,1)

        carveMaze(grid, startX, startY, rand);

        // Ensure the conventional start and goal are open
        grid[0][0] = 0;
        grid[rows - 1][cols - 1] = 0;

        // Ensure there's an entry/exit path from the maze borders
        if (grid[1][0] == 1 && grid[0][1] == 1) {
            grid[1][0] = 0;
        }
        if (grid[rows - 2][cols - 1] == 1 && grid[rows - 1][cols - 2] == 1) {
            grid[rows - 2][cols - 1] = 0;
        }

        return grid;
    }
}