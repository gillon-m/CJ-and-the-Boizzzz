package app;

import java.util.ArrayList;
import java.util.List;

public class Graph {
	
	private List<Vertex> _vertices;
	private List<Edge> _edges;
	private List<Vertex> _rootVertices;
	private String _name;

	private static Graph instance = null;
	
	public static Graph getInstance() {
		return instance;
	}
	
	public Graph(String name, List<Vertex> vertices, List<Edge> edges) {
		_name = name;
		_vertices = vertices;
		_edges = edges;
		instance = this;
	}
	
	public List<Vertex> getVertices() {
		return _vertices;
	}
	
	public List<Edge> getEdges() {
		return _edges;
	}
	
	public List<Vertex> getRootVertices() {
		return _rootVertices;
	}
	public String getName() {
		return _name;
	}

	public void setUpForMakingSchedules() {
		setUpChildrenParents();
		setUpLevelsOfNodes();
		setUpRootNodes();
	}
	private void setUpChildrenParents() {
		for (Vertex v : this.getVertices()) {
			for (Edge e : this.getEdges()) {
				if (!e.getDestination().getParents().contains(e.getSource())) {
					e.getDestination().addParent(e.getSource());
				}
				if (v.equals(e.getSource())) {
					Vertex child = e.getDestination();
					v.addChild(child);
				} 
			}
		}
	}
	
	private void setUpLevelsOfNodes() {
		_rootVertices = new ArrayList<Vertex>();
		for (Vertex v : this.getVertices()) {
			if (v.getParents().size() == 0) {
				_rootVertices.add(v);
			}
			for (Vertex root: _rootVertices) {
				int lvl = level(root, v);
				v.setLevel(lvl);
			}
		}
	}
	private void setUpRootNodes() {
		if (_rootVertices.size() >1) {
			Vertex emptyVertex = new Vertex("-", 0);
			for(Vertex v: _rootVertices) {
				Edge emptyEdge = new Edge(emptyVertex, v, 0);	
				_edges.add(0, emptyEdge);
			}
			_rootVertices.clear();
			_rootVertices.add(emptyVertex);
			_vertices.add(0, emptyVertex);
			setUpChildrenParents();
			setUpLevelsOfNodes();
		}
	}
	/**
	 * This helper method topologically sorts a given di-graph.
	 * As of right now, I'm not sure as to whether this method will actually be helpful. But it has been implemented
	 * as by meeting discussion. Open to any other ideas for generating schedules. 
	 * @param root : the start node at which to begin tree traversal.
	 * @param target : the target node that we want to find the height of.
	 * @return
	 */
	private int level(Vertex root, Vertex target){
	    return level(root, target, 0);
	}

	private int level(Vertex tree, Vertex target, int currentLevel) {
	    int returnLevel = -1;        
	    if(tree.getName().equals(target.getName())) {
	        returnLevel = currentLevel;
	    } else {
	        for(Vertex child : tree.getChildren()) {
	            if((returnLevel = level(child, target, currentLevel + 1)) != -1){
	                break;
	            }                
	        }
	    }
	    return returnLevel;
	}
}
