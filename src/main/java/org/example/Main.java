import java.util.*;

public class Main {
    public static void main(String[] args) {
        BacktrackingAlgorithm backtrackingAlgorithm = new BacktrackingAlgorithm(); // initialization
        backtrackingAlgorithm.execute(); // execution
    }
}

/**
 * This class contains all information for the algorithm and execution method
 */
class BacktrackingAlgorithm {
    private Maze maze;
    Node neo;
    Node end;
    Scanner scanner;
    int best = Integer.MAX_VALUE;
    List<Pair> visited = new ArrayList<>();
    List<Pair> winPath = new ArrayList<>();

    /**
     * Initialization
     */
    public BacktrackingAlgorithm() {
        maze = new Maze(9, 9);
        scanner = new Scanner(System.in);
        scanner.nextLine();
        neo = maze.createNode(0, 0, NodeType.NEO);
        findEnd();
    }

    public void execute() {
        searchTreeBacktracking(new PointModified(neo.getCoordinates()).setDistance(directPath(neo.getCoordinates(), end.getCoordinates()), 0));
        // check the best solution or -1
        if (best != Integer.MAX_VALUE) {
            System.out.println("e " + best);
        } else {
            System.out.println("e -1");
        }
    }

    private boolean searchTreeBacktracking(PointModified current) {
        // check if the end is reached
        if (current.coordinates.equals(end.getCoordinates())) {
            if (current.passedDistance < best) {
                best = current.weight; // best score updated
                winPath = new ArrayList<>(); // store the win path coordinates
                winPath.add(current.coordinates);
                PointModified next = current;
                while (next.prev != null) {
                    winPath.add(next.prev.coordinates);
                    next = next.prev;
                }
            }
            return true;
        }

        step(current.coordinates); // the function to output move and input maze information
        Node neoInner = neo;

        int counter = 0;
        boolean causeVisited = false;
        List<PointModified> nextList = new ArrayList<>();
        // check all directions and creates next possible points
        for (Pair direction: Pair.directions()) {
            Pair to = Pair.of(current.coordinates.x + direction.x, current.coordinates.y + direction.y);
            // check if the destination is in bounds
            if (!isValidDestination(to, maze.width, maze.height)) {
                causeVisited = true;
                continue;
            }
            // check if the destination is not already visited
            if (visited.contains(to) || isVisited(current, to)) {
                continue;
            }
            // check if the destination is not dangerous
            if (neoInner.isDanger(direction)) {
                causeVisited = true;
                continue;
            }
            counter++; // to determine the dead end
            nextList.add(current.createNext(to)
                    .setDistance(
                            directPath(to, end.getCoordinates()),
                            current.passedDistance + 1));
        }
        nextList.sort((a, b) -> {
            if (a.directDistance == b.directDistance) {
                return b.passedDistance - a.passedDistance;
            } else {
                return a.directDistance - b.directDistance;
            }
        }); // sort destinations by direct distance
        for (PointModified next: nextList) {
            if (best != Integer.MAX_VALUE) {
                // check if the weight is less than the best
                if (next.weight < best) {
                    searchTreeBacktracking(next);
                    step(current.coordinates);
                }
            } else {
                searchTreeBacktracking(next);
                step(current.coordinates);
            }
        }
        // if the dead end and not a win path
        if (counter == 0 && !causeVisited) {
            if (!winPath.contains(current.coordinates)) {
                visited.add(current.coordinates);
            }
        }
        return false;
    }

    private boolean isValidDestination(Pair to, int width, int height) {
        return to.x >= 0 && to.y >= 0 && to.x < width && to.y < height;
    }

    /**
     * check if the destination is not in the tail of the path
     */
    private boolean isVisited(PointModified current, Pair to) {
        PointModified destination = new PointModified(to);
        PointModified next = current;
        while (true) {
            if (next.prev == null) {
                return false;
            } else if (next.prev.equals(destination)) {
                return true;
            } else {
                next = next.prev;
            }
        }
    }

    private void findEnd() {
        String[] params = scanner.nextLine().split(" ");
        end = maze.createNode(Integer.parseInt(params[0]), Integer.parseInt(params[1]), NodeType.END);
    }

    /**
     * Calculate the direct distance to the goal
     */
    private int directPath(Pair start, Pair end) {
        int counter = 0;
        Pair current = start;

        while (current.x != end.x || current.y != end.y) {
            if (current.x < end.x) {
                current = Pair.of(current.x + 1, current.y);
                counter++;
            } else if (current.x > end.x) {
                current = Pair.of(current.x - 1, current.y);
                counter++;
            }
            if (current.y < end.y) {
                current = Pair.of(current.x, current.y + 1);
                counter++;
            } else if (current.y > end.y) {
                current = Pair.of(current.x, current.y - 1);
                counter++;
            }
        }

        return counter;
    }


    private void step(Pair coordinates) {
        System.out.println("m " + coordinates.x + " " + coordinates.y);

        int paramCounter = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < paramCounter; i++) {
            String[] params = scanner.nextLine().split(" ");
            int x = Integer.parseInt(params[0]);
            int y = Integer.parseInt(params[1]);
            NodeType nodeType = NodeType.of(params[2]);
            if (nodeType != NodeType.KEY) {
                maze.createNode(x, y, NodeType.of(params[2]));
            }
        }
        neo = maze.createNode(coordinates.x, coordinates.y, NodeType.NEO);
    }

    /**
     * The class contains coordinates, previous coordinates and other information
     */
    public static class PointModified {
        private final Pair coordinates;

        private PointModified prev;
        public int directDistance;
        public int passedDistance;
        public int weight;

        private PointModified(Pair coordinates) {
            this.coordinates = coordinates;
        }

        private PointModified(int x, int y) {
            this.coordinates = Pair.of(x, y);
        }

        public PointModified setDistance(int directDistance, int passedDistance) {
            this.directDistance = directDistance;
            this.passedDistance = passedDistance;
            this.weight = directDistance + passedDistance;
            return this;
        }

        public PointModified createNext(Pair point) {
            PointModified next = new PointModified(point);
            next.prev = this;
            return next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PointModified that = (PointModified) o;
            return Objects.equals(coordinates, that.coordinates);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(coordinates);
        }
    }
}


class Pair {

    public final int x;
    public final int y;

    public static Pair of(int x, int y) {
        return new Pair(x, y);
    }

    public static List<Pair> directionsVisibility() {
        return new ArrayList<>(List.of(
                Pair.of(0, 1),
                Pair.of(1, 0),
                Pair.of(1, 1),
                Pair.of(1, -1),
                Pair.of(0, -1),
                Pair.of(-1, 0),
                Pair.of(-1, -1),
                Pair.of(-1, 1)
        ));
    }

    public static List<Pair> directions() {
        return new ArrayList<>(List.of(
                Pair.of(0, 1),
                Pair.of(1, 0),
                Pair.of(0, -1),
                Pair.of(-1, 0)
        ));
    }

    private Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return x == pair.x && y == pair.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

enum NodeType {
    NEO,
    DANGER,
    AGENT,
    Sentinel,
    KEY,
    END;

    public static NodeType of(String value) {
        switch (value) {
            case "P":
                return NodeType.DANGER;
            case "A":
                return NodeType.AGENT;
            case "S":
                return NodeType.Sentinel;
            case "B":
                return NodeType.KEY;
            case "K":
                return NodeType.END;
            default:
                return null;
        }
    }
}

class Node {
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

    /**
     * The function returns the neighbors of the current node
     */
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

    // check if the node is danger
    public boolean isDanger(Pair coordinates) {
        return neighbors.get(coordinates) != null
                && neighbors.get(coordinates).nodeType != NodeType.END
                && neighbors.get(coordinates).nodeType != NodeType.KEY // ignore key because tests don't care about of it
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

class Maze {
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

    // create a new node in the maze
    public Node createNode(int x, int y, NodeType type) {
        Node node = new Node(x, y, type, maze);
        maze.get(y).set(x, node);
        return node;
    }

}

