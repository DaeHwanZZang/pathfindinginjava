public class Node {
    public int x;
    public int y;
    public double g; // Cost from start to this node
    public double h; // Heuristic cost from this node to goal
    public double f; // Total cost (g + h)
    public Node parent;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.g = Double.MAX_VALUE;
        this.h = 0;
        this.f = Double.MAX_VALUE;
        this.parent = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}