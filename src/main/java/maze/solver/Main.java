package maze.solver;

import java.io.IOException;
import java.util.Timer;

public class Main {
    public static void main(String[] args) throws IOException {
        BacktrackingAlgorithm backtrackingAlgorithm = new BacktrackingAlgorithm();
        long startTime = System.nanoTime();
        backtrackingAlgorithm.execute();
        long endTime = System.nanoTime();
//        System.out.println(((endTime - startTime) / 1_000_000.0));
        AStarAlgorithm aStarAlgorithm = new AStarAlgorithm();
        startTime = System.nanoTime();
        aStarAlgorithm.execute();
        endTime = System.nanoTime();
//        System.out.println(((endTime - startTime) / 1_000_000.0));
    }
}
