package App;

import java.util.List;

public class Graph {
	
	private List<Vertex> _vertices;
	private List<Edge> _edges;
	private String _name;
	public Graph(String name, List<Vertex> vertices, List<Edge> edges) {
		_name = name;
		_vertices = vertices;
		_edges = edges;
	}
	
	public List<Vertex> getVertices() {
		return _vertices;
	}
	
	public List<Edge> getEdges() {
		return _edges;
	}
	
	public String getName() {
		return _name;
	}
}
