package com.paulocorreaslz.tegra.util;
/**
 * Criado por Paulo Correa <pauloyaco@gmail.com> 2019
 */
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;


public class Graph {
    private Map<String, LinkedHashSet<String>> map = new HashMap<String, LinkedHashSet<String>>();

    public void addEdge(String node1, String node2) {
        LinkedHashSet<String> adjacent = map.get(node1);
        if(adjacent==null) {
            adjacent = new LinkedHashSet<String>();
            map.put(node1, adjacent);
        }
        adjacent.add(node2);
    }

    public LinkedList<String> adjacentNodes(String last) {
        LinkedHashSet<String> adjacent = map.get(last);
        if(adjacent==null) {
            return new LinkedList<String>();
        }
        return new LinkedList<String>(adjacent);
    }
}