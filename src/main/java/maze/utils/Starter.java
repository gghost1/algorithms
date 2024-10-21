package maze.utils;

import maze.solver.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Starter {

    public static void main(String[] args) throws IOException {

        BufferedReader mapReader = new BufferedReader(new FileReader("Q:\\algorithms\\src\\main\\java\\maze\\utils\\maze.txt"));

        List<List<String>> maze = new ArrayList<>();
        String line;
        while ((line = mapReader.readLine()) != null) {
            List<String> row = new ArrayList<>(List.of(line.split("")));
            maze.add(row);
        }

        ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", "Q:/algorithms/target/classes", "maze.solver.Main");
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        PrintWriter writer = new PrintWriter(process.getOutputStream(), true); // Автоматическая очистка буфера

        writer.println("1");
        writer.println("4 1");
        writer.flush();
        while (true) {
            String[] line1 = reader.readLine().split(" ");
            if (line1[0].equals("e")) {
                System.out.println(line1[1]);
                break;
            } else if (!line1[0].equals("m")) {
                System.out.println("Error");
                break;
            }

            List<String> neighbors = new ArrayList<>();
            int x = Integer.parseInt(line1[1]);
            int y = Integer.parseInt(line1[2]);


            for (maze.solver.Pair neighborDirection : Pair.directionsVisibility()) {
                int neighborX = x + neighborDirection.x;
                int neighborY = y + neighborDirection.y;

                if (!(neighborX < 0 || neighborX >= maze.getFirst().size() || neighborY < 0 || neighborY >= maze.size())) {
                    String type = maze.get(neighborY).get(neighborX);
                    if (!type.equals(".") && !type.equals("N")) {
                        neighbors.add(neighborX + " " + neighborY + " " + type);
                        System.out.println(neighborX + " " + neighborY + " " + type);
                    }

                }
            }
            writer.println(neighbors.size());
            if (!neighbors.isEmpty()) {
                writer.println(String.join("\n", neighbors));
            }
        }
    }

}
