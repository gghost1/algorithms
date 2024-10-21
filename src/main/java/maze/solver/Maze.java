package maze.solver;

import java.util.ArrayList;
import java.util.List;

public class Maze {
    private final List<List<Node>> maze;
    public final int width;
    public final int height;

    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        List<List<Node>> maze = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
            List<Node> row = new ArrayList<>(width);
            for (int j = 0; j < width; j++) {
                row.add(null);
            }
            maze.add(row);
        }
        this.maze = maze;
    }

    public Node getNode(int x, int y) {
        return maze.get(y).get(x);
    }

    public Node createNode(int x, int y, NodeType type) {
        Node node = new Node(x, y, type, maze);
        maze.get(y).set(x, node);
        return node;
    }

}
