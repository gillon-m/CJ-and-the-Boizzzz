package FileManager;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import App.Edge;
import App.Graph;
import App.Vertex;

/**
 * Class that loads a dot file to be analysed and reads it to produce a 
 * graph object.
 *  
 * 
 *
 */
public class InputReader {
	String _inputFileName;
	public InputReader(String inputFileName) {
		_inputFileName = inputFileName;
	}
	/**
	 * method that reads off the file and stores information into ////data structures////
	 * 
	 */
	public Graph readFile() {
		File file = new File(_inputFileName);

		Graph graph = null;
		
		try { // read file
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			Map<String, Vertex> verticesRead = new HashMap<String, Vertex>();
			List<Edge> edges = new ArrayList<Edge>();
			while ((line = br.readLine()) != null) {
				line = line.trim(); //Remove leading and trailing whitespace
				if (line.endsWith(";")) {
//					line = line.trim(); //Remove leading and trailing whitespace
					if (line.contains("->")) { //Line is an edge
						String[] values = line.split(" ");
						for (String s : verticesRead.keySet()) {
							System.out.println("key " + s);
							System.out.println("value " + verticesRead.get(s).getName());
						}
						for (String s : values) {
							s.replaceAll("\\t+", "");
							if (verticesRead.containsKey(s)){
								System.out.println("A NEW TEST " + s);
							}
						}
						Vertex sourceVertex = verticesRead.get(values[0].replaceAll("\\t+",""));
						Vertex destinationVertex = verticesRead.get(values[2].replaceAll("\\t+",""));
						System.out.println(sourceVertex);
						int weight = Integer.parseInt(values[3].replaceAll("[\\D]", "")); //Retrieve weight as integer
						edges.add(new Edge(sourceVertex, destinationVertex, weight));
					} else { //Line is a vertex
						String[] nameAndWeight = line.split(" ");
  						String name = nameAndWeight[0].replaceAll("\\t+", "");
						int weight = Integer.parseInt(nameAndWeight[1].replaceAll("[\\D]", "")); //Retrieve weight as integer
						Vertex newVertex = new Vertex(name, weight);
						verticesRead.put(name, newVertex);
					}
				}
			}
			List<Vertex> vertices = new ArrayList<>(verticesRead.values());
			graph = new Graph(vertices, edges); //Create graph
			br.close();
		} catch (IOException e) {
		}
		
		return graph; //Return created graph object
	}
}