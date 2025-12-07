import java.util.List;
import java.util.Scanner;

public class PathfindingApp {
    public static void main(String[] args) {
        int rows;
        int cols;

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number of rows and columns for the maze\n<prefer odd number>\nrows: ");
        rows = sc.nextInt();
        System.out.print("cols: ");
        cols = sc.nextInt();

        System.out.println("Generating a " + rows + "x" + cols + " maze...");
        int[][] grid = MazeGenerator.generateMaze(rows, cols);

        Node start = new Node(0, 0);
        Node goal = new Node(cols - 1, rows - 1);

        System.out.println("Finding path from (" + start.x + "," + start.y + ") to (" + goal.x + "," + goal.y + ")...");
        List<Node> path = AStar.findPath(grid, start, goal);

        if (path.isEmpty()) {
            System.out.println("\nNo path found!");
            PrintGridPath.printer(grid, null);
        } else {
            System.out.println("\nPath found! Length: " + path.size());
            PrintGridPath.printer(grid, path);
        }
    }
}
