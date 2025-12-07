import java.util.Comparator;

class NodeComparator implements Comparator<Node> {
    @Override
    public int compare(Node n1, Node n2) {
        return Double.compare(n1.f, n2.f);
    }
}