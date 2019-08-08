package com.paulocorreaslz.tegra.util;
/**
 * 
 * Adaptado por Paulo Correa <pauloyaco@gmail.com> 2019
 */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FlightSearch {

	private Graph graph;
	private LinkedList<String> visited = new LinkedList<String>();
	private List<String> route = new ArrayList<String>();
	public FlightSearch() {
	}

	public void addGraph(Graph g) {
		this.graph = g;
	}

	public void addVisit(String visit) {
		visited.add(visit);
	}
	
	public List<String> run(String origin, String destination) {
		visited.add(origin);
        doInception(graph, visited, destination);
        printPathandPopulateRoute(visited);
        return route;
	}
	
    public void doInception(Graph graph, LinkedList<String> visited, String destination) {
        LinkedList<String> nodes = graph.adjacentNodes(visited.getLast());
        for (String node : nodes) {
            if (visited.contains(node)) {
                continue;
            }
            if (node.equals(destination)) {
                visited.add(node);
                printPathandPopulateRoute(visited);
                visited.removeLast();
                break;
            }
        }
        for (String node : nodes) {
            if (visited.contains(node) || node.equals(destination)) {
                continue;
            }
            visited.addLast(node);
            doInception(graph, visited, destination);
            visited.removeLast();
        }
    }

    private void printPathandPopulateRoute(LinkedList<String> visited) {
        for (String node : visited) {
            System.out.print(node);
            route.add(node);
            System.out.print(" ");
        }
        System.out.println();
    }
}