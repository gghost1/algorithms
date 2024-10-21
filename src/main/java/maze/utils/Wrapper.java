package maze.utils;

import maze.solver.Maze;
import maze.solver.Node;
import maze.solver.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Wrapper {

    private PrintWriter writer;

    private BufferedReader mapReader;
    private List<List<String>> maze;
    private Pair prevCoordinates;

    public Wrapper() throws IOException {
        mapReader = new BufferedReader(new FileReader("Q:\\algorithms\\src\\main\\java\\maze\\utils\\maze.txt"));
        writer = new PrintWriter(System.out);

        maze = new ArrayList<>();
        String line;
        while ((line = mapReader.readLine()) != null) {
            List<String> row = new ArrayList<>(List.of(line.split("")));
            maze.add(row);
        }
    }

    public void listener(String line) throws InterruptedException, IOException {
            if (!line.isEmpty()) {

                String[] params = line.split(" ");
                if (params[0].equals("m")) {
                    int x = Integer.parseInt(params[1]);
                    int y = Integer.parseInt(params[2]);

                    List<maze.solver.Pair> neighborDirections = Pair.directionsVisibility();
                    List<String> neighbors = new ArrayList<>();

                    for (maze.solver.Pair neighborDirection : neighborDirections) {
                        int neighborX = x + neighborDirection.x;
                        int neighborY = y + neighborDirection.y;

                        if (!(neighborX < 0 || neighborX >= maze.getFirst().size() || neighborY < 0 || neighborY >= maze.size())) {
                            neighbors.add(neighborX + " " + neighborY + maze.get(neighborY).get(neighborX));
                        }
                    }

                    maze.get(y).set(x, "M");
                    if (prevCoordinates != null) {
                        maze.get(prevCoordinates.y).set(prevCoordinates.x, ".");
                    }
                    prevCoordinates = Pair.of(x, y);
                    writer.println();
                    for (List<String> row : maze) {
                        writer.println(String.join("", row));
                    }
                    writer.println();
                    writer.flush();
                }
            }
        Thread.sleep(1500);
    }

}
