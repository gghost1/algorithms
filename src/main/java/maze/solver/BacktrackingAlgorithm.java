package maze.solver;

import java.util.*;

public class BacktrackingAlgorithm {
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
                winPath = new ArrayList<>();
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
        // if the dead end
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
