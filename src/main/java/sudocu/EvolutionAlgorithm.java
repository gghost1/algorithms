package sudocu;

import maze.solver.Pair;

import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class EvolutionAlgorithm {

    public static List<List<Integer>> init = new ArrayList<>(List.of(
            new ArrayList<>(List.of(-1, 8, -1, -1, -1, -1, -1, 9, -1)),
            new ArrayList<>(List.of(-1, -1, 7, 5, -1, 2, 8, -1, -1)),
            new ArrayList<>(List.of(6, -1, -1, 8, -1, 7, -1, -1, 5)),
            new ArrayList<>(List.of(3, 7, -1, -1, 8, -1, -1, 5, 1)),
            new ArrayList<>(List.of(2, -1, -1, -1, -1, -1, -1, -1, 8)),
            new ArrayList<>(List.of(9, 5, -1, -1, 4, -1, -1, 3, 2)),
            new ArrayList<>(List.of(8, -1, -1, 1, -1, 4, -1, -1, 9)),
            new ArrayList<>(List.of(-1, -1, 1, 9, -1, 3, 6, -1, -1)),
            new ArrayList<>(List.of(-1, 4, -1, -1, -1, -1, -1, 2, -1))
    ));
    public static HashSet<Pair> initCoordinates;

    public static void main(String[] args) {
        initCoordinates = Grid.checkInit(init);
/*
init 100 random
then in each iteration:
  + 5 random
  + for the best one <errors number> random rows columns squares to errors and reload grids(fill all by -1, choose for error and fill)
  - other random cross with 5 best
    change -1 between 2 grids
*/

        for (int i = 0; i < 30; i++) {
            System.out.println("----------");
            LocalTime start = LocalTime.now();
            solve();
            LocalTime end = LocalTime.now();
            System.out.println("----------");
            System.out.println(end.getSecond() - start.getSecond());
            System.out.println(end.getNano() - start.getNano());
            System.out.println("----------");
        }

    }

    private static void solve() {
        List<Grid> grids = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            Grid grid = new Grid(init, initCoordinates);
            if (grid.fill()) {
                grid.print();
                System.out.println("Solved in first generation");
                return;
            } else {
                grids.add(grid);
            }
        }
        int best = Integer.MAX_VALUE;
        int count = 0;
        while (true) {
            grids.sort(Comparator.comparingInt(Grid::getErrors));
            grids = grids.stream().limit(60).collect(Collectors.toList());
                try {
                    List<Grid> newGrids = Grid.fillCross(grids.stream().limit(40).toList());
                    grids = grids.stream().limit(20).collect(Collectors.toList());
                    newGrids.sort(Comparator.comparingInt(Grid::getErrors));
                    grids.addAll(newGrids.stream().limit(60).toList());
                } catch (SolvedException e) {
                    e.grid.print();
                    System.out.println("Solved in cross generation");
                    return;
                }

            for (int i = 0; i < grids.size(); i++) {
                try {
                    grids.addAll(grids.get(i).fillErrors());
                } catch (SolvedException e) {
                    e.grid.print();
                    System.out.println("Solved in error fixing generation");
                    return;
                }
            }
            for (int i = 0; i < 20; i++) {
                Grid grid = new Grid(init, initCoordinates);
                if (grid.fill()) {
                    grid.print();
                    System.out.println("Solved in children generation");
                    return;
                } else {
                    grids.add(grid);
                }
            }
        }
    }


}
