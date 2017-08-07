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
	BufferedReader br;
	String fileName, trimmedFileName = "";
	//String fixedFileName = "C:\\Projects\\CJ-and-the-Boizzzz\\input\\ex1_in.dot";
	public InputReader() {
	}
	
	/**
	 * method that asks user to provide a url for the file and stores the 
	 * file url in the correct format. 
	 */
	private void loadFile() {
		br = new BufferedReader(new InputStreamReader(System.in)); //waits user to drop the file to terminal
		try {
			fileName = br.readLine();
			// When user drops a file, bash surrounds the file url around quotation marks.
			// eg. '/media/cj/SHARED/workspace/Java/306-project1/input/ex1_in.dot'
			trimmedFileName = fileName.replace("\'", "").trim(); // removes quotation marks and trim the whitespace
		} catch (IOException e) {
		}
		while (!trimmedFileName.endsWith(".dot")) { //if user input does not ends with .dot
			System.out.print("This file is not a dot file. Please drop a .dot file and press Enter.");
			loadFile(); //repeat until user gives the correct input
		}
	}
	/**
	 * method that reads off the file and stores information into ////data structures////
	 * 
	 */
	public Graph readFile() {
		
		// Get user to specify an absolute path for .dot file by typing or drag/drop
		System.out.println("Drop a dot file here and press Enter.");
		loadFile(); 
		File file = new File(trimmedFileName);
		
		// Alternately, can hardcode absolute filepath
		//File file = new File(fixedFileName);

		Graph graph = null;
		
		try { //if file exists
			BufferedReader br2 = new BufferedReader(new FileReader(file));
			String line = "";
			Map<String, Vertex> verticesRead = new HashMap<String, Vertex>();
			List<Edge> edges = new ArrayList<Edge>();
			while ((line = br2.readLine()) != null) { // read each line and write on console until the end
				if (line.endsWith(";")) {
					line = line.trim(); //Remove leading and trailing whitespace
					if (line.contains("->")) { //Line is an edge
						String[] values = line.split(" ");
						Vertex sourceVertex = verticesRead.get(values[0]);
						Vertex destinationVertex = verticesRead.get(values[2]);
						int weight = Integer.parseInt(values[3].replaceAll("[\\D]", "")); //Retrieve weight as integer
						edges.add(new Edge(sourceVertex, destinationVertex, weight));
					} else { //Line is a vertex
						String[] nameAndWeight = line.split(" ");
						String name = nameAndWeight[0];
						int weight = Integer.parseInt(nameAndWeight[1].replaceAll("[\\D]", "")); //Retrieve weight as integer
						Vertex newVertex = new Vertex(name, weight);
						verticesRead.put(name, newVertex);
					}
				}
			}
			List<Vertex> vertices = new ArrayList<>(verticesRead.values());
			graph = new Graph(vertices, edges); //Create graph
			br2.close();
		} catch (FileNotFoundException e) { //if file does not exist, get user input again
			System.out.print("Invalid File url. Try again. ");
			trimmedFileName = "";
			readFile();
		} catch (IOException e) {
		}
		
		return graph; //Return created graph object
	}
}
