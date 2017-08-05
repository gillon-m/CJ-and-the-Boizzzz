package App;
/**
 * Very simple class to represent a vertex in a graph.
 * Vertices have a name (like 'a') and a weight (e.g. 2)
 * @author Brad
 *
 */
public class Vertex {
	private String _name;
	private int _weight;
	
	public Vertex(String name, int weight) {
		_name = name;
		_weight = weight;
	}
	
	public String getName() {
		return _name;
	}
	
	public int getWeight() {
		return _weight;
	}
}
