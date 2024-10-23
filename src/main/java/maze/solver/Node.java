package maze.solver;

import java.util.HashMap;
import java.util.List;

public class Node {
    private final int x;
    private final int y;

    private final NodeType nodeType;
    private final HashMap<Pair, Node> neighbors;

    public Node(int x, int y, NodeType nodeType, List<List<Node>> maze) {
        this.x = x;
        this.y = y;
        this.nodeType = nodeType;
        this.neighbors = getNeighbors(maze);
    }

    public Pair getCoordinates() {
        return Pair.of(x, y);
    }

    private HashMap<Pair, Node> getNeighbors(List<List<Node>> maze) {
        List<Pair> neighborDirections = Pair.directionsVisibility();
        HashMap<Pair, Node> neighborsCreation = new HashMap<>();

        for (Pair neighborDirection : neighborDirections) {
            int neighborX = x + neighborDirection.x;
            int neighborY = y + neighborDirection.y;

            if (!(neighborX < 0 || neighborX >= maze.getFirst().size() || neighborY < 0 || neighborY >= maze.size())) {
                neighborsCreation.put(neighborDirection, maze.get(neighborY).get(neighborX));
            }
        }
        return neighborsCreation;
    }

    public boolean isDanger(Pair coordinates) {
        return neighbors.get(coordinates) != null
                && neighbors.get(coordinates).nodeType != NodeType.END
                && neighbors.get(coordinates).nodeType != NodeType.KEY
                && neighbors.get(coordinates).nodeType != NodeType.NEO;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

}
