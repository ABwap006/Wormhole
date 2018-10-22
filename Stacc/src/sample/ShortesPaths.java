package sample;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;

public class ShortesPaths {

    public static ArrayList<Integer> bfsShortest(int source, int goal, ArrayList<ArrayList<Integer>> arr) throws IOException, ParseException {
        Queue<Integer> queue = new ArrayDeque<>();
        ArrayList<Integer> ret = new ArrayList<>();
        int[] parent = new int[arr.size()];
        boolean[] visited = new boolean[arr.size()];
        queue.add(source);
        parent[source] = -1;

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            visited[curr] = true;
            if (curr == goal - 1)
                break;
            for (Integer neigh : arr.get(curr)) {
                if (!visited[neigh]) {
                    queue.add(neigh);
                    parent[neigh] = curr;
                }
            }

        }

        int prev = goal-1;
        while (prev != -1) {
            ret.add(prev);
            prev = parent[prev];
        }

        Collections.reverse(ret);
        return ret;
    }
}
