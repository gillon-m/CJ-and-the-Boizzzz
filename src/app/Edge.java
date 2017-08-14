package app;
/**
 * Simple class to represent an edge in a class.
 * Edges have a source vertex, destination vertex, and a weight
 * @author Brad
 *
 */
public class Edge {
	
	private Vertex _source;
	private Vertex _destination;
	private int _weight;
	
	public Edge(Vertex source, Vertex destination, int weight) {
		_source = source;
		_destination = destination;
		_weight = weight;
	}
	
	public Vertex getSource() {
		return _source;
	}
	
	public Vertex getDestination() {
		return _destination;
	}
	
	public int getWeight() {
		return _weight;
	}
}
