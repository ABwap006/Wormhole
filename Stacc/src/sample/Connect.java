package sample;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.json.simple.parser.ParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class Connect {
    private static final String link = "https://visningsrom.stacc.com/dd_server_worms/rest/boards/2";
    private static URL url;
    private static URLConnection urlc;
    private static JSONObject jo;
    private static BufferedReader br;
    private static Object json;

    public static String getInformationFromStacc(String send, String square) throws IOException, ParseException {
        url = new URL(link + square);
        urlc = url.openConnection();
        br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
        json = new JSONParser().parse(br.readLine());
        jo = (JSONObject) json;
        br.close();

        if (send.equals("links")) {
            JSONObject jo2 = (JSONObject) json;
            JSONArray linksList = (JSONArray) jo2.get(send);
            for (int i = 0; i < linksList.size(); i++) {
                jo2 = (JSONObject) linksList.get(i);

                if (jo2.get("direction").equals("next"))
                    return String.valueOf(jo2.get("square"));
            }
            return "null";
        }
        String s = String.valueOf(jo.get(send));
        return s;
    }

    public static ArrayList<ArrayList<Integer>> buildGraph() throws IOException, ParseException {
        ArrayList<ArrayList<Integer>> adjList = new ArrayList<>();
        int size = Integer.parseInt(Connect.getInformationFromStacc("dimX", "")) *
                Integer.parseInt(Connect.getInformationFromStacc("dimY", ""));

        String wormhole;
        String next;
        for (int i = 1; i <= size; i++) {
            adjList.add(new ArrayList<>());
            wormhole = Connect.getInformationFromStacc("wormhole", "/" + String.valueOf(i));
            next = Connect.getInformationFromStacc("links", "/" + String.valueOf(i));
            if (!wormhole.equals("null"))
                adjList.get(i-1).add(Integer.parseInt(wormhole) - 1);
            if (!next.equals("null"))
                adjList.get(i-1).add(Integer.parseInt(next) - 1);
        }

        return adjList;
    }

}
