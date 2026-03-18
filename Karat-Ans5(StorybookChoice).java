

import java.io.*;
import java.util.*;
class Main {
    public static int stories(int[] endings, int[][] choices, int option) {
    // Convert endings to a set for O(1) lookup
    Set<Integer> endingSet = new HashSet<>();
    for (int e : endings) {
        endingSet.add(e);
    }

    // Map current_page -> [option1, option2]
    Map<Integer, int[]> choiceMap = new HashMap<>();
    for (int[] c : choices) {
        choiceMap.put(c[0], new int[]{c[1], c[2]});
    }

    Set<Integer> visited = new HashSet<>();
    int page = 1;

    while (true) {
        // If we hit an ending, return it
        if (endingSet.contains(page)) {
            return page;
        }

        // Loop detection
        if (visited.contains(page)) {
            return -1;
        }
        visited.add(page);

        // If there's a choice, jump accordingly
        if (choiceMap.containsKey(page)) {
            int[] opts = choiceMap.get(page);
            page = (option == 1) ? opts[0] : opts[1];
        } else {
            // Otherwise, move to the next page
            page++;
        }
    }
}

    
    public static void main(String[] args) {
        System.out.println("Started solving.....");
        int[] endings1 = {6, 15, 21, 30};
    int[][] choices1_1 = {
      {3, 7, 8},
      {9, 4, 2},
    };
    int[][] choices1_2 = {
      {3, 14, 2},
    };
    int[][] choices1_3 = {
      {5, 11, 28},
      {9, 19, 29},
      {14, 16, 20},
      {18, 7, 22},
      {25, 6, 30},
    };
    int[][] choices1_4 = {
      {2, 10, 15},
      {3, 4, 10},
      {4, 3, 15},
      {10, 3, 15},
    };

    int[] endings2 = {11};
    int[][] choices2_1 = {
      {2, 3, 4},
      {5, 10, 2},
    };
    int[][] choices2_2 = {};

    int[] endings3 = {4, 11};
    int[][] choices3_1 = {
      {10, 6, 8},
    };

    int[] endings4 = {20};
    int[][] choices4_1 = {
      {2, 6, 3},
      {3, 1, 4},
      {4, 10, 5},
      {6, 3, 7}
    };
    
    
    System.out.println("Ending : "+stories(endings1, choices1_1, 1));
    System.out.println("Ending : "+stories(endings1, choices1_1, 2));
    System.out.println("Ending : "+stories(endings1, choices1_2, 1));
    System.out.println("Ending : "+stories(endings1, choices1_2, 2));
    System.out.println("Ending : "+stories(endings1, choices1_3, 1));
    System.out.println("Ending : "+stories(endings1, choices1_3, 2));
    System.out.println("Ending : "+stories(endings1, choices1_4, 1));
    System.out.println("Ending : "+stories(endings1, choices1_4, 2));
    System.out.println("Ending : "+stories(endings2, choices2_1, 1));
    System.out.println("Ending : "+stories(endings2, choices2_1, 2));
    System.out.println("Ending : "+stories(endings2, choices2_2, 1));
    System.out.println("Ending : "+stories(endings2, choices2_2, 2));
    System.out.println("Ending : "+stories(endings3, choices3_1, 1));
    System.out.println("Ending : "+stories(endings3, choices3_1, 2));
    System.out.println("Ending : "+stories(endings4, choices4_1, 1));
    
    
    }
}