package gui;

import org.graphstream.graph.implementations.*;
import graph.Vertex;
import org.graphstream.ui.layout.HierarchicalLayout;
import org.graphstream.ui.view.Viewer;

public class GraphVisualiser {
	private org.graphstream.graph.Graph _graph;

	private static final String STYLE_SHEET = ""
			+ "graph {"
			+ "fill-color: red;"
			+ "}"
			+ ""
			+ ""
			+ ""
			+ ""
			+ ""
			+ ""
			+ ""
			+ ""
			+ ""
			+ ""
			+ "";

	public static void main(String args[]) {
		//new GraphVisualiser();
		
		org.graphstream.graph.Graph graph = new SingleGraph("Tutorial 1");
		graph.addAttribute("ui.stylesheet", STYLE_SHEET);
		graph.addNode("A");
		graph.getNode("A").addAttribute("ui.style", "fill-color: rgb(0,100,255);");
		graph.getNode("A").addAttribute("ui.style", "size: 100,100,100;");
		graph.getNode("A").addAttribute("ui.label", "size: 100,100,100;");
		graph.addNode("B");
		graph.addNode("C");
		graph.addEdge("AB", "A", "B");
		graph.addEdge("BC", "B", "C");
		graph.addEdge("CA", "C", "A");
		graph.display();
	}

	public GraphVisualiser(){
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		_graph = new SingleGraph("Tutorial 1");
		Viewer viewer = _graph.display();
		viewer.enableAutoLayout(new HierarchicalLayout());
		createGraphVisualiser();
	}

	private void createGraphVisualiser(){
		graph.Graph inputGraph = graph.Graph.getInstance();
		if(inputGraph!=null){
			for(Vertex v: inputGraph.getVertices()){
				String vertex = v.getName();
				if(!vertex.equals("-")){
					_graph.addNode(vertex);
				}
			}
			for(graph.Edge e: inputGraph.getEdges()){
				String source = e.getSource().getName();
				String destination = e.getDestination().getName();
				if(!source.equals("-")&&!destination.equals("-")){
					_graph.addEdge(source+destination, source, destination, true);

				}
			}
		}
	}
}