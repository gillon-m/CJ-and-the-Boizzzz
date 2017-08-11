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
	
	/**
	 * Prints Graph
	 */
	
	public void printGraph(){
		System.out.println("Vertices");
		for(Vertex v: _vertices){
			System.out.println(v.getName());
			System.out.println("Children");
			for(Vertex child: v.getChildren()){
				System.out.print(child.getName()+" ");
			}
			System.out.println();
			System.out.println("Parents");
			for(Vertex parent: v.getParents()){
				System.out.print(parent.getName()+" ");
			}
			System.out.println();
			System.out.println();
		}
		System.out.println();
		System.out.println("Edges");
		for(Edge e: _edges){
			System.out.println(e.getSource().getName()+"->"+e.getDestination().getName());
		}
	}
}
