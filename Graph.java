package graphs;

import java.util.*;

/**
 * Implements a graph. We use two maps: one map for adjacency properties 
 * (adjancencyMap) and one map (dataMap) to keep track of the data associated 
 * with a vertex. 
 * 
 * @author cmsc132
 * 
 * @param <E>
 */
public class Graph<E> {
	/* You must use the following maps in your implementation */
	private HashMap<String, HashMap<String, Integer>> adjacencyMap;
	private HashMap<String, E> dataMap;

	public Graph() {
		
		adjacencyMap = new HashMap<String, HashMap<String, Integer>>();
		dataMap = new HashMap<String, E>();
		
	}
	
	public void addDirectedEdge(String vertexName, String endVertexName, int cost) {
		
		if(!(dataMap.containsKey(vertexName)) && dataMap.containsKey(endVertexName))
			
			throw new IllegalArgumentException("One of the vertices do not exist!");
		
		if(!(adjacencyMap.containsKey(vertexName))) 
			
			adjacencyMap.put(vertexName, new HashMap<String, Integer>());
		
		adjacencyMap.get(vertexName).put(endVertexName, cost);
		
	}
	
	public void addVertex(String vertexName, E data) {
		
		if(dataMap.get(vertexName) != null) throw new IllegalArgumentException("Vertex already exists!");
		
		dataMap.put(vertexName, data);
		
	}
	
	public void doBreadthFirstSearch(String startVertexName, CallBack<E> callback) {
		
		if(!adjacencyMap.containsKey(startVertexName)) throw new IllegalArgumentException("Vertex does not exist!");
		
		Queue<String> visitThese = new PriorityQueue<String>();
		Set<String> visitedNodes = new HashSet<String>();
		
		visitThese.add(startVertexName);
		
		while(!(visitThese.isEmpty())) {
			
			String currentVertex = visitThese.poll();
			
			if(!(visitedNodes.contains(currentVertex))) {
				
				visitedNodes.add(currentVertex);
				callback.processVertex(currentVertex, dataMap.get(currentVertex));
				
				for(String i : adjacencyMap.get(currentVertex).keySet())
					
					if(!(visitedNodes.contains(i)))
					
						visitThese.add(i);
				
			}
			
		}
		
	}
	
	public void doDepthFirstSearch(String startVertexName, CallBack<E> callback) {
		
		if(adjacencyMap.get(startVertexName) == null) throw new IllegalArgumentException("Vertex does not exist!");
		
		Stack<String> visitThese = new Stack<String>();
		Set<String> visitedNodes = new HashSet<String>();

		visitThese.add(startVertexName);
		
		while(!(visitThese.isEmpty())) {
			
			String currentVertex = visitThese.pop();
			
			if(!(visitedNodes.contains(currentVertex))) {
				
				visitedNodes.add(currentVertex);
				callback.processVertex(currentVertex, dataMap.get(currentVertex));
				
				for(String i : adjacencyMap.get(currentVertex).keySet())
					
					if(!(visitedNodes.contains(i)))
						
						visitThese.add(i);
				
			}
			
		}
		
	}
	
	public int doDijkstras(String startVertexName, String endVertexName, ArrayList<String> shortestPath) {
		
		if(startVertexName.equals(endVertexName)) {
			
			shortestPath.add(startVertexName);
			return 0;
			
		}
		
		Queue<String> toCheck = new PriorityQueue<String>();
		Set<String> allConnectedNodes = new HashSet<String>();
		toCheck.add(startVertexName);
		allConnectedNodes.add(startVertexName);
		
		while(!(toCheck.isEmpty())) {
			
			String currentVertex = toCheck.poll();

			if(adjacencyMap.containsKey(currentVertex) && adjacencyMap.get(currentVertex).keySet().size() > 0) {
				
				allConnectedNodes.add(currentVertex);
				
				for(String i : adjacencyMap.get(currentVertex).keySet()) {
					
					if(!(allConnectedNodes.contains(i))) {
						
						allConnectedNodes.add(i);
						toCheck.add(i);
						
					}
					
				}
			
			}
			
		}
		
		if(!(allConnectedNodes.contains(startVertexName)) || !(allConnectedNodes.contains(endVertexName))) {
			
			shortestPath.add("None");
			return -1;
			
		}
		
		Set<String> visited = new HashSet<String>();
		Set<String> nextNodes = new HashSet<String>();
		HashMap<String, String> predecessors = new HashMap<String, String>();
		HashMap<String, Integer> distances = new HashMap<String, Integer>();

		for(String i : dataMap.keySet()) {
			
			distances.put(i, 10000);
			
		}
		
		nextNodes.add(startVertexName);
		distances.put(startVertexName, 0);
		
		while(!(visited.containsAll(adjacencyMap.keySet()))) {
			
			String currentVertex = retrieveVertexWithLowestDistance(distances, nextNodes);
			
			visited.add(currentVertex);
			
			if(currentVertex == null) break;
			
			if(adjacencyMap.containsKey(currentVertex)) {
				
				for(String a : adjacencyMap.get(currentVertex).keySet()) {
				
					if(distances.get(a) != null && getCost(currentVertex, a) + distances.get(currentVertex) < distances.get(a)) {
						
						distances.put(a, getCost(currentVertex, a) + distances.get(currentVertex));
						predecessors.put(a, currentVertex);
					
					}
					
					if(!(visited.contains(a))) nextNodes.add(a);
					
				}
				
			}
			
			visited.add(currentVertex);
			
		}
		
		String current = predecessors.get(endVertexName);
		shortestPath.add(endVertexName);
		
		while(!(current.equals(startVertexName))) {
			
			shortestPath.add(current);
			current = predecessors.get(current);
			
		}
		
		shortestPath.add(startVertexName);
		Collections.reverse(shortestPath);
		
		return distances.get(endVertexName);
		
	}
	
	private static String retrieveVertexWithLowestDistance(HashMap<String, Integer> distances, Set<String> set) {
		
		String cheapestVertex = null;
		int lowestCost = Integer.MAX_VALUE;
		
		for(String i : set) {
			
			if(distances.get(i) < lowestCost) {
				
				cheapestVertex = i;
				lowestCost = distances.get(i);
				
			}
			
		}
		
		set.remove(cheapestVertex);
		return cheapestVertex;
		
	}
	
	public Map<String, Integer> getAdjacentVertices(String vertexName){
		
		return adjacencyMap.get(vertexName);
		
	}
	
	public int getCost(String startVertexName, String endVertexName) {
		
		return adjacencyMap.get(startVertexName).get(endVertexName);
		
	}
	
	public E getData(String vertex) {
		
		if(!(dataMap.containsKey(vertex))) throw new IllegalArgumentException("The vertex does not exist!");
		
		return dataMap.get(vertex);
		
	}
	
	public Set<String> getVertices() {
		
		Set<String> vertices = new HashSet<String>();
		
		for(String i : dataMap.keySet()) {
			
			vertices.add(i);
			
		}
		
		return vertices;
		
		
	}
	
	public String toString() {
		
		TreeMap<String, E> sortedVertices = new TreeMap<String, E>(dataMap);
		TreeMap<String, HashMap<String, Integer>> sortedAdjVertices = new TreeMap<String, HashMap<String, Integer>>(adjacencyMap);
		StringBuffer graphInfo = new StringBuffer("Vertices: [");
		
		for(String i : sortedVertices.keySet()) {
			
			graphInfo.append(i + ", ");
			
		}
		
		graphInfo = new StringBuffer(graphInfo.substring(0, graphInfo.toString().length() - 2));
		graphInfo.append("]\n" + "Edges:\n");
		
		for(String a : sortedVertices.keySet()) {
			
			graphInfo.append("Vertex(" + a + ")--->{");
			
			if(sortedAdjVertices.containsKey(a)) {
			
				for(String b : sortedAdjVertices.get(a).keySet()) {
				
					graphInfo.append(b + "=" + sortedAdjVertices.get(a).get(b) + ", ");	
				
				}
			
				if(sortedAdjVertices.get(a).keySet().size() != 0) {
				
					graphInfo = new StringBuffer(graphInfo.substring(0, graphInfo.toString().length() - 2));
					graphInfo.append("}\n");
				
				}
				
			} else {
				
				graphInfo.append("}\n");
				
			}
			
		}
		
		return graphInfo.toString();
		
	}

}