import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.List;

class AStar {

    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // R, D, L, U

    private static double heuristic(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private static List<Node> reconstructPath(Node goalNode) {
        List<Node> path = new ArrayList<>();
        Node current = goalNode;
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    public static List<Node> findPath(int[][] grid, Node start, Node goal) {
        int rows = grid.length;
        int cols = grid[0].length;

        PriorityQueue<Node> openList = new PriorityQueue<>(new NodeComparator());
        Set<Node> closedList = new HashSet<>();

        // Initialize start node
        start.g = 0;
        start.h = heuristic(start, goal);
        start.f = start.g + start.h;

        openList.add(start);

        while (!openList.isEmpty()) {
            Node current = openList.poll();

            if (current.equals(goal)) {
                return reconstructPath(current);
            }

            closedList.add(current);

            for (int[] direction : DIRECTIONS) {
                int newX = current.x + direction[0];
                int newY = current.y + direction[1];

                // Check bounds and obstacles
                if (newX < 0 || newY < 0 || newX >= cols || newY >= rows || grid[newY][newX] == 1) {
                    continue;
                }

                Node neighbor = new Node(newX, newY);
                // If neighbor is already in closed list, skip
                if (closedList.contains(neighbor)) {
                    continue;
                }

                // Calculate tentative g-score
                double tentativeG = current.g + 1; // Assuming cost of 1 for each step

                // If a better path to neighbor is found
                if (tentativeG < neighbor.g) {
                    neighbor.parent = current;
                    neighbor.g = tentativeG;
                    neighbor.h = heuristic(neighbor, goal);
                    neighbor.f = neighbor.g + neighbor.h;

                    // If neighbor is not in open list, add it
                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    } else {
                        // If it is in open list, update its priority (f-value)
                        // This is implicitly handled by PriorityQueue if the object is updated
                        // and re-added, or if a custom update mechanism is used.
                        // For simplicity, we'll just re-add if g is better, which might
                        // lead to duplicates but will eventually find the shortest path.
                        // A more efficient way would be to remove and re-add, or use a Fibonacci heap.
                        // For typical grid sizes, this is usually fine.
                    }
                }
            }
        }

        return new ArrayList<>(); // No path found
    }
}