package maze.solver;

import java.util.*;

public class AStarAlgorithm {
    private Maze maze;
    Node neo;
    Node end;
    Scanner scanner;

    public AStarAlgorithm() {
        maze = new Maze(9, 9);
        scanner = new Scanner(System.in);
        scanner.nextLine();
        neo = maze.createNode(0, 0, NodeType.START);
        findEnd();
    }

    public void execute() {
        PointModified endPoint = new PointModified(end.getCoordinates());
        PointModified prevPosition = null;
        Queue<PointModified> queue = new PriorityQueue<>(
                Comparator.comparingInt((PointModified o) -> o.weight)
                        .thenComparingInt(o -> o.directDistance));
        PointModified bestEnd = new PointModified(0, 0)
                .setDistance(Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2);

        queue.add(new PointModified(neo.getX(), neo.getY()).setDistance(directPath(neo.getCoordinates(), end.getCoordinates()), 0));

        while (!queue.isEmpty()) {
            PointModified current = queue.poll();
            if (prevPosition != null && !current.prev.equals(prevPosition)) {
                goTo(current, prevPosition);
            }
            step(Pair.of(current.coordinates.x, current.coordinates.y));

            for (Pair direction: Pair.directions()) {
                Pair to = Pair.of(current.coordinates.x + direction.x, current.coordinates.y + direction.y);

                if (isValidDestination(to, maze.width, maze.height)
                        && !neo.isDanger(direction)
                        && (current.prev == null || !isVisited(current, to))) {
                    if (to.equals(endPoint.coordinates)) {
                        System.out.println("e " + (current.passedDistance + 1));
                        return;
                    }
                    queue.add(
                            current
                                    .createNext(to)
                                    .setDistance(
                                            directPath(to, endPoint.coordinates),
                                            current.passedDistance + 1
                                    ));
                }
            }
            prevPosition = current;
        }
        System.out.println("e -1");
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

    private void goTo(PointModified current, PointModified prev) {
        PointModified currentPosition = current;
        PointModified prevPosition = prev;
        List<PointModified> path = new ArrayList<>();
        while (prevPosition.prev != null) {
            path.add(prevPosition.prev);
            prevPosition = prevPosition.prev;
        }
        path.forEach(a -> step(a.coordinates));

        path = new ArrayList<>();
        while (currentPosition.prev != null) {
            path.add(currentPosition.prev);
            currentPosition = currentPosition.prev;
        }
        Collections.reverse(path);
        path.stream().skip(1).forEach(a -> step(a.coordinates));
    }

    private boolean isValidDestination(Pair to, int width, int height) {
        return to.x >= 0 && to.y >= 0 && to.x < width && to.y < height;
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



    private void findEnd() {
        String[] params = scanner.nextLine().split(" ");
        end = maze.createNode(Integer.parseInt(params[0]), Integer.parseInt(params[1]), NodeType.END);
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

            neo = maze.createNode(coordinates.x, coordinates.y, NodeType.AGENT);

    }

    static class PointModified {
        private final Pair coordinates;

        private PointModified prev;
        private int directDistance;
        private int passedDistance;
        private int weight;

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
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coordinates, weight);
        }

    }
}
