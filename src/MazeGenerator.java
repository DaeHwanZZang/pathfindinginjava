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

    // 미로의 벽을 무작위로 허물어 루프(순환)를 만드는 메서드
    public static void addLoops(int[][] grid, int removeCount) {
        Random rand = new Random();
        int rows = grid.length;
        int cols = grid[0].length;
        int count = 0;

        while (count < removeCount) {
            // 랜덤한 내부 좌표 선택 (테두리 제외)
            int r = rand.nextInt(rows - 2) + 1;
            int c = rand.nextInt(cols - 2) + 1;

            // 해당 좌표가 벽(1)이라면
            if (grid[r][c] == 1) {
                // 상하좌우 중 빈 공간(0)이 2개 이상 접해있으면 벽을 뚫어 루프 생성 가능성이 높음
                int openNeighbors = 0;
                if (grid[r-1][c] == 0) openNeighbors++;
                if (grid[r+1][c] == 0) openNeighbors++;
                if (grid[r][c-1] == 0) openNeighbors++;
                if (grid[r][c+1] == 0) openNeighbors++;

                // 두 개의 통로를 가로막고 있는 벽이라면 뚫어서 연결
                if (openNeighbors >= 2) {
                    grid[r][c] = 0;
                    count++;
                }
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

        int totalCells = rows * cols;
        addLoops(grid, totalCells / 60);

        return grid;
    }
}