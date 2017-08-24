package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Very simple class to represent a vertex in a graph.
 * Vertices have a name (like 'a') and a weight (e.g. 2)
 * @author Brad
 *
 */
public class Vertex {
	private String _name;
	private int _weight;
	private int _level;
	private List<Vertex> _children = new ArrayList<Vertex>();
	private List<Vertex> _parents = new ArrayList<Vertex>();
	
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
	
	public void setLevel(int level) {
		_level = level;
	}
	
	public int getLevel() {
		return _level;
	}
	
	public void addChild(Vertex child) {
		_children.add(child);
	}
	
	public List<Vertex> getChildren() {
		return _children;
	}
	
	public void addParent(Vertex parent) {
		_parents.add(parent);
	}
	public List<Vertex> getParents() {
		return _parents;
	}
}
