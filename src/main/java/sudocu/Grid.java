package sudocu;

import maze.solver.Pair;

import java.security.SecureRandom;
import java.util.*;

public class Grid {
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

    public List<Grid> fillErrors() throws SolvedException {
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
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.getFirst().size(); j++) {
                System.out.print(grid.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }


}