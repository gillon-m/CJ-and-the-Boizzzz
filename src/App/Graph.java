package App;

import java.util.List;

public class Graph {
	
	private List<Vertex> _vertices;
	private List<Edge> _edges;
	
	public Graph(List<Vertex> vertices, List<Edge> edges) {
		_vertices = vertices;
		_edges = edges;
	}
	
	public List<Vertex> getVertices() {
		return _vertices;
	}
	
	public List<Edge> getEdges() {
		return _edges;
	}
	
	/**
	 * Adds vertex to graph
	 */
	public void addVertex(Vertex v){
		_vertices.add(v);
	}
	
	/**
	 * Adds edge to graph
	 */
	public void addEdge(Edge e){
		_edges.add(e);
	}
}
