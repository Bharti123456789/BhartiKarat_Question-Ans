// Online Java Compiler
// Use this editor to write, compile and run your Java code online
import java.util.*;

public class SnakeLanes {

    static class Result {
        List<Integer> rows;
        List<Integer> columns;

        Result(List<Integer> r, List<Integer> c) {
            this.rows = r;
            this.columns = c;
        }
    }

    public static Result findPassableLanes(char[][] board) {
        int rowLength = board.length;
        int colLength = board[0].length;

        List<Integer> passableRows = new ArrayList<>();
        List<Integer> passableCols = new ArrayList<>();

        for (int i = 0; i < rowLength; i++) {
            boolean passable = true;
            for (int j = 0; j < colLength; j++) {
                if (board[i][j] == '+') {
                    passable = false;
                    break;
                }
            }
            if (passable) {
                passableRows.add(i);
            }
        }

        for (int j = 0; j < colLength; j++) {
            boolean passable = true;
            for (int i = 0; i < rowLength; i++) {
                if (board[i][j] == '+') {
                    passable = false;
                    break;
                }
            }
            if (passable) {
                passableCols.add(j);
            }
        }

        return new Result(passableRows, passableCols);
    }
    
    public static void main(String[] args) {
        System.out.println("Solution below : ");
        char[][] board1 = {
    {'+', '+', '+', '0', '+', '0', '0'},
    {'0', '0', '+', '0', '0', '0', '0'},
    {'0', '0', '0', '0', '+', '0', '0'},
    {'+', '+', '+', '0', '0', '+', '0'},
    {'0', '0', '0', '0', '0', '0', '0'}
};
      char[][] board2 = {
    {'+', '+', '+', '0', '+', '0', '0'},
    {'0', '0', '0', '0', '0', '+', '0'},
    {'0', '0', '+', '0', '0', '0', '0'},
    {'0', '0', '0', '0', '+', '0', '0'},
    {'+', '+', '+', '0', '0', '0', '+'}
};

char[][] board4 = {
    {'+'}
};

char[][] board5 = {
    {'0'}
};

char[][] board6 = {
    {'0', '0'},
    {'0', '0'},
    {'0', '0'},
    {'0', '0'}
};

        Result res = findPassableLanes(board6);
        System.out.println("Rows: "+res.rows);
        System.out.println("Columns: "+res.columns);
    }
}

