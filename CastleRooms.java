package SinariobasedexapKarat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CastleRooms {
    public static List<String> filterRooms(List<List<String>> instructions, List<String> treasureRooms) {
        // Treasure lookup in O(1)
        Set<String> treasureSet = new HashSet<>(treasureRooms);
        // nextRoom[source] = destination
        Map<String, String> nextRoom = new HashMap<>();
        // inDegree[room] = how many sources point to it
        Map<String, Integer> inDegree = new HashMap<>();
        // Build graph info
        for (List<String> inst : instructions) {
            String src = inst.get(0);
            String dst = inst.get(1);

            nextRoom.put(src, dst);
            inDegree.put(dst, inDegree.getOrDefault(dst, 0) + 1);

            // Ensure src exists in inDegree map (optional, but helpful)
            inDegree.putIfAbsent(src, inDegree.getOrDefault(src, 0));
        }
        // Filter rooms
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : nextRoom.entrySet()) {
            String room = entry.getKey();
            String destination = entry.getValue();

            if (inDegree.getOrDefault(room, 0) >= 2 && treasureSet.contains(destination)) {
                result.add(room);
            }
        }
        // Optional: sort for deterministic output (helps match examples)
        Collections.sort(result);
        return result;
    }
    // Quick test runner
    public static void main(String[] args) {
        List<List<String>> instructions1 = Arrays.asList(
                Arrays.asList("jasmin", "tulip"),
                Arrays.asList("lily", "tulip"),
                Arrays.asList("tulip", "tulip"),
                Arrays.asList("rose", "rose"),
                Arrays.asList("violet", "rose"),
                Arrays.asList("sunflower", "violet"),
                Arrays.asList("daisy", "violet"),
                Arrays.asList("iris", "violet")
        );
        List<String> treasureRooms1 = Arrays.asList("lily", "tulip", "violet", "rose");
        List<String> treasureRooms2 = Arrays.asList("lily", "jasmin", "violet");

        System.out.println(filterRooms(instructions1, treasureRooms1)); // [tulip, violet]
        System.out.println(filterRooms(instructions1, treasureRooms2)); // []

        List<List<String>> instructions2 = Arrays.asList(
                Arrays.asList("jasmin", "tulip"),
                Arrays.asList("lily", "tulip"),
                Arrays.asList("tulip", "violet"),
                Arrays.asList("violet", "violet")
        );
        List<String> treasureRooms3 = Arrays.asList("violet");
        System.out.println(filterRooms(instructions2, treasureRooms3)); // [tulip]
    }
}
