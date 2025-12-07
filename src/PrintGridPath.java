import java.util.List;

public class PrintGridPath {
    public static void printer(int[][] grid, List<Node> path) {
        int rows = grid.length;
        int cols = grid[0].length;

        char[][] displayGrid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                displayGrid[i][j] = (grid[i][j] == 1) ? '■' : ' ';
            }
        }

        if (path != null) {
            for (Node node : path) {
                displayGrid[node.y][node.x] = '.';
            }
        }

        // Mark start and goal
        displayGrid[0][0] = 'S';
        displayGrid[rows - 1][cols - 1] = 'G';

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(displayGrid[i][j] + "  ");
            }
            System.out.println();
        }
    }
}
