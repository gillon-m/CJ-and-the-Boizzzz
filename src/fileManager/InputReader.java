package fileManager;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graph.Edge;
import graph.Graph;
import graph.Vertex;

/**
 * Class that loads a dot file to be analysed and reads it to produce a graph object.
 * This reads the given input file line by line and process each line to store important
 * values to create a graph object. The graph object has a name, list of vertices, and 
 * list of edges.
 * 
 * @author CJ Bang, Brad Miller
 *
 */
public class InputReader {
	
	String _inputFileName;
	
	public InputReader(String inputFileName) {
		_inputFileName = inputFileName;
	}

	/**
	 * method that reads off the file and stores information into data structure
	 * @return Graph
	 */
	public Graph readFile() {
		File file = new File(_inputFileName);

		Graph graph = null;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String firstLine = br.readLine(); // reads the first line for the graph name
			String nameOfGraphPlusQMarks = firstLine.split(" ")[1]; // first word is always digraph, second is always the name of the graph
			String nameOfGraph = nameOfGraphPlusQMarks.substring(1, nameOfGraphPlusQMarks.length()-1); // remove quotation marks from the graph name

			String line = "";
			Map<String, Vertex> verticesRead = new HashMap<String, Vertex>();
			Map<Integer, Edge> edgesRead = new HashMap<Integer, Edge>();
			int edgeCount = 0;
			while ((line = br.readLine()) != null) {
				line = line.trim(); //Remove leading and trailing whitespace
				line = line.replaceAll("\t", ""); //Remove all tab characters from the line
				if (line.endsWith(";")) { //If line is part of the graph
					String[] values = line.split(" ");
					if (values.length == 2) { //Line is a vertex
  						String name = values[0];
						int weight = Integer.parseInt(values[1].replaceAll("[\\D]", ""));
						Vertex newVertex = new Vertex(name, weight);
						verticesRead.put(name, newVertex);
					} else if (values.length == 4) { //Line is an edge
						edgeCount++;
						Vertex sourceVertex = verticesRead.get(values[0]); // the first string is the source vertex 
						Vertex destinationVertex = verticesRead.get(values[2]); // the second string is the destination vertex (2nd being the arrow)
						int weight = Integer.parseInt(values[3].replaceAll("[\\D]", "")); 
						edgesRead.put(edgeCount,new Edge(sourceVertex, destinationVertex, weight));
					}
				}
			}
			List<Vertex> vertices = new ArrayList<>(verticesRead.values());
			List<Edge> edges = new ArrayList<Edge>(edgesRead.values());
			graph = new Graph(nameOfGraph, vertices, edges); //Create graph
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return graph; //Return created graph object
	}
}