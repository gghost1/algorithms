package maze.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pair {

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
