package App;

import java.io.*;

import FileManager.*;
import SearchSpace.SearchSpace;
//C:\Users\Andon\Desktop\CJ-and-the-Boizzzz-master\input\ex1_in.dot
public class App {
	private static Vertex _root;
	public static void main(String[] args) throws IOException {
		InputReader ir = new InputReader();
		Graph graph = ir.readFile();
		SearchSpace searchSpace = new SearchSpace(graph);	
		searchSpace.makeSearchSpace();								// Creates all possible schedules
		//searchSpace.tempPrintOutSchedules();						// prints out all possible schedules
		searchSpace.tempPrintOutLastNodeScheduleInTimeOrder();		// prints out schedule for last vertex
		//setUpChildrenParents(graph);
		//setUpLevelsOfNodes(graph);	
	}
	

	private static void setUpChildrenParents(Graph graph) {
		for (Vertex v : graph.getVertices()) {
			for (Edge e : graph.getEdges()) {
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
	
	private static void setUpLevelsOfNodes(Graph graph) {
		for (Vertex v : graph.getVertices()) {
			System.out.println("The parents of " + v.getName() + " are: " + "");
			for (Vertex p : v.getParents()) {
				System.out.println(p.getName());
			}
			if (v.getParents().size() == 0) {
				_root = v;
				System.out.println("The root node is: " + v.getName());
			}
			int lvl = level(_root,v);
			v.setLevel(lvl);
			System.out.println("The node " + v.getName() + " resides on level: " + lvl);
		}
		
	}
	/**
	 * This helper method topologically sorts a given di-graph.
	 * As of right now, I(Andon)'m not sure as to whether this method will actually be helpful. But it has been implemented
	 * as by meeting discussion. Open to any other ideas for generating schedules. 
	 * @param root : the start node at which to begin tree traversal.
	 * @param target : the target node that we want to find the height of.
	 * @return
	 */
	public static Integer level(Vertex root, Vertex target){
	    return level(root, target, 0);
	}

	private static Integer level(Vertex tree, Vertex target, int currentLevel) {
	    Integer returnLevel = -1;        
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
