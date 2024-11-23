package sudocu;

import java.io.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class EvolutionAlgorithm {

    public static List<List<Integer>> init;
    public static HashSet<Pair> initCoordinates;

    public static void main(String[] args) {
/*
init 100 random
then in each iteration:
  + 5 random
  + for the best one <errors number> random rows columns squares to errors and reload grids(fill all by -1, choose for error and fill)
  - other random cross with 5 best
    change -1 between 2 grids
*/
        long avar = 0;
        for (int i = 0; i < 40; i++) {
            input();
            init = Grid.initFill(init).grid;
            initCoordinates = Grid.checkInit(init);
            long startTime = System.nanoTime();
            solve();
            long endTime = System.nanoTime();
            avar += ((endTime - startTime) / 1_000_000.0);
            init = new ArrayList<>();
            initCoordinates = new HashSet<>();
            System.out.println(avar/(i+1));
        }
        System.out.println(avar / 40);


    }

    private static void input() {
        init = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
            for (int i = 0; i < 9; i++) {
                String line = reader.readLine().replaceAll("-", "-1");
                init.add(new ArrayList<>(Arrays.stream(line.split(" ")).map(Integer::parseInt).collect(Collectors.toList())));
            }
        } catch (Exception e) {
        }
    }

    private static void solve() {
        Grid bestGrid = new Grid(init, initCoordinates);
        while (true) {
            List<Grid> grids = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                Grid grid = new Grid(init, initCoordinates);
                if (grid.fill()) {
                    grid.print();
                    return;
                } else {
                    grids.add(grid);
                }
            }
            try {
                grids.add(bestGrid.fillErrors());
            } catch (SolvedException e) {
                e.grid.print();
                return;
            }

            int best = Integer.MAX_VALUE;
            int count = 0;
            while (true) {
                grids.sort(Comparator.comparingInt(Grid::getErrors));
                count++;
                if (best > grids.getFirst().getErrors()) {
                    best = grids.getFirst().getErrors();
                    count = 0;
                }
                if (count > 5) {
                    bestGrid = grids.getFirst();
                    break;
                }
                try {
                    List<Grid> gridsToCross = grids.stream().limit(5).collect(Collectors.toList());
                    gridsToCross.addAll(grids.stream().skip(grids.size()-1).limit(1).toList());
                    List<Grid> newGrids = Grid.fillCross(gridsToCross);
                    grids = grids.stream().skip(5).limit(20).collect(Collectors.toList());
                    newGrids.sort(Comparator.comparingInt(Grid::getErrors));
                    grids.addAll(newGrids.stream().limit(30).toList());
                } catch (SolvedException e) {
                    e.grid.print();
                    return;
                }

                for (int i = 0; i < grids.size(); i++) {
                    if (i > 15) break;
                    try {
                        grids.addAll(grids.get(i).fillError());
                    } catch (SolvedException e) {
                        e.grid.print();
                        return;
                    }
                }
                for (int i = 0; i < 15; i++) {
                    Grid grid = new Grid(init, initCoordinates);
                    if (grid.fill()) {
                        grid.print();
                        return;
                    } else {
                        grids.add(grid);
                    }
                }
            }
        }

    }
}

class Grid {
    public static List<Integer> possibleNumbers = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
    private final SecureRandom secureRandom;

    public List<List<Integer>> grid;
    private HashSet<Pair> init;
    private Queue<Pair> errors;

    public Grid(List<List<Integer>> initGrid, HashSet<Pair> init) {
        this.secureRandom = new SecureRandom();
        this.grid = createInit(initGrid);
        this.init = init;
        errors = new LinkedList<>();
    }

    public Grid(Grid grid) {
        this.secureRandom = grid.secureRandom;
        this.init = grid.init;
        this.grid = createInit(grid.grid);
        this.errors = new LinkedList<>();
    }

    private List<List<Integer>> createInit(List<List<Integer>> initGrid) {
        List<List<Integer>> grid = new ArrayList<>();
        for (int i = 0; i < initGrid.size(); i++) {
            grid.add(new ArrayList<>());
            for (int j = 0; j < initGrid.getFirst().size(); j++) {
                grid.get(i).add(initGrid.get(i).get(j));
            }
        }
        return grid;
    }

    public int getErrors() {
        return errors.size();
    }

    private boolean add(int x, int y, int number) {
        grid.get(y).set(x, number);
        return true;
    }

    public static List<Grid> fillCross(List<Grid> gridsInit) throws SolvedException {
        List<Grid> grids = new ArrayList<>();
        for (int i = 0; i < gridsInit.size(); i++) {
            Grid initGrid = gridsInit.get(i);
            for (int j = 0; j < gridsInit.size(); j++) {
                if (j != i) {
                    grids.add(initGrid.fillErrors(gridsInit.get(j)));
                }
            }
        }
        return grids;
    }

    private Grid fillErrors(Grid additionalGrid) throws SolvedException {
        Grid newGrid = new Grid(this);

        Queue<Pair> innerErrors = new LinkedList<>(errors);
        Pair pair = innerErrors.poll();
        while (pair != null) {
            newGrid.clear(pair.x, pair.y);
            pair = innerErrors.poll();
        }
        innerErrors = new LinkedList<>(errors);
        pair = innerErrors.poll();
        while (pair != null) {
            newGrid.add(pair.x, pair.y, additionalGrid.grid.get(pair.y).get(pair.x));
            pair = innerErrors.poll();
        }
        if (newGrid.fill(-1)) {
            throw new SolvedException(newGrid);
        }

        return newGrid;
    }

    public Grid fillErrors() throws SolvedException {
        Queue<Pair> innerErrors = new LinkedList<>(errors);
        Pair pair = innerErrors.poll();
        Grid newGrid = new Grid(this);
        while (pair != null) {
            newGrid.clear(pair.x, pair.y);
            newGrid.fillCell(pair.x, pair.y);
            pair = innerErrors.poll();
        }
        if (newGrid.fill(-1)) {
            throw new SolvedException(newGrid);
        }
        return newGrid;
    }

    public List<Grid> fillError() throws SolvedException {
        Queue<Pair> innerErrors = new LinkedList<>(errors);
        Pair pair = innerErrors.poll();
        List<Grid> newGrids = new ArrayList<>();
        while (pair != null) {

            Grid newGrid = new Grid(this);
            if (newGrid.fill(pair.x, pair.y)) {
                throw new SolvedException(newGrid);
            }

            newGrids.add(newGrid);

            pair = innerErrors.poll();
        }
        return newGrids;
    }

    public boolean fill() {
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.getFirst().size(); j++) {
                if (!init.contains(Pair.of(j, i))) {
                    fillCell(j, i);
                }
            }
        }
        return errors.isEmpty();
    }

    private boolean fill(int parameter) {
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.getFirst().size(); j++) {
                if (grid.get(i).get(j) == parameter) {
                    if (!init.contains(Pair.of(j, i))) {
                        fillCell(j, i);
                    }
                }
            }
        }
        return errors.isEmpty();
    }

    private boolean fill(int x, int y) {
        clear(x, y);
        fillCell(x, y);
        return fill(-1);
    }

    private void clear(int x, int y) {
        for (int i = 0; i < grid.size(); i++) {
            if (!init.contains(Pair.of(i, y))) {
                grid.get(y).set(i, -1);
            }
            if (!init.contains(Pair.of(x, i))) {
                grid.get(i).set(x, -1);
            }
        }
        fillSquare(x, y, -1);
    }

    private boolean fillCell(int x, int y) {
        List<Integer> possible = getPossibleNumbers(x, y);
        if (!possible.isEmpty()) {
            int number = possible.get(secureRandom.nextInt(possible.size()));
            add(x, y, number);
            return true;
        } else {
            errors.add(Pair.of(x, y));
            add(x, y, -1);
        }
        return false;
    }

    private List<Integer> getPossibleNumbers(int x, int y) {
        List<Integer> numbers = new ArrayList<>(possibleNumbers);
        List<Integer> row = getRow(y);
        List<Integer> column = getColumn(x);
        List<Integer> square = getSquare(x, y);
        numbers = numbers.stream()
                .filter(number ->
                        !row.contains(number) && !column.contains(number) && !square.contains(number)
                )
                .toList();
        return numbers;
    }

    private List<Integer> getSquare(int x, int y) {
        List<Integer> square = new ArrayList<>();
        int yUp = y - y % 3;
        int yDown = yUp + 2;
        int xLeft = x - x % 3;
        int xRight = xLeft + 2;

        for (int i = yUp; i <= yDown; i++) {
            for (int j = xLeft; j < xRight; j++) {
                square.add(grid.get(i).get(j));
            }
        }
        return square;
    }

    private void fillSquare(int x, int y, int number) {
        int yUp = y - y % 3;
        int yDown = yUp + 2;
        int xLeft = x - x % 3;
        int xRight = xLeft + 2;
        for (int i = yUp; i <= yDown; i++) {
            for (int j = xLeft; j < xRight; j++) {
                if (!init.contains(Pair.of(j, i))) {
                    grid.get(i).set(j, number);
                }
            }
        }
    }

    private List<Integer> getRow(int y) {
        return grid.get(y);
    }

    private List<Integer> getColumn(int x) {
        List<Integer> column = new ArrayList<>();
        grid.forEach(row -> column.add(row.get(x)));
        return column;
    }

    public static Grid initFill(List<List<Integer>> initGrid) {
        Grid grid = new Grid(initGrid, new HashSet<>());
        for (int i = 0; i < grid.grid.size(); i++) {
            for (int j = 0; j < grid.grid.getFirst().size(); j++) {
                if (grid.grid.get(i).get(j) == -1) {
                    List<Integer> possible = grid.getPossibleNumbers(j, i);
                    if (possible.size() == 1) {
                        grid.add(j, i, possible.getFirst());
                    }
                }
            }
        }
        return grid;
    }

    public static HashSet<Pair> checkInit(List<List<Integer>> initGrid) {
        HashSet<Pair> initCoordinates = new HashSet<>();
        for (int i = 0; i < initGrid.size(); i++) {
            for (int j = 0; j < initGrid.getFirst().size(); j++) {
                if (initGrid.get(i).get(j) != -1) {
                    initCoordinates.add(Pair.of(j, i));
                }
            }
        }
        return initCoordinates;
    }

    public void print() {
        try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(System.out))) {
            for (int i = 0; i < grid.size(); i++) {
                for (int j = 0; j < grid.getFirst().size(); j++) {
                    printWriter.print(grid.get(i).get(j));
                    if (j != grid.getFirst().size() - 1) {
                        printWriter.print(" ");
                    } else {
                        printWriter.print("\n");
                    }
                }
            }
            printWriter.flush();
        }
    }
}

class SolvedException extends Exception {
    public Grid grid;
    public SolvedException(Grid grid) {
        super("message");
        this.grid = grid;
    }
}

class Pair {

    public final int x;
    public final int y;

    public static Pair of(int x, int y) {
        return new Pair(x, y);
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
