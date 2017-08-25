package fileManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import graph.Edge;
import graph.Graph;
import graph.Vertex;
import scheduler.Schedule;

/**
 * Class that is responsible for displaying the output schedule and writing it on a dot 
 * file. A new folder named "output" is created and the file is created with an appropriate 
 * file name. The file has the same format as the input file but additional information
 * about the schedule is included. Although the order of the lines is not retained, the 
 * necessary information is displayed in the file.
 * 
 *@author CJ Bang
 */
public class OutputWriter {
	String OUTPUT_DIRECTORY = "./output/";
	String _outputFileName;
	Graph _graph;
	Schedule _schedule;
	public OutputWriter(String outputFileName, Graph g, Schedule s) {
		_outputFileName = outputFileName;
		_graph = g;
		_schedule = s;
	}
	/**
	 * Outputs the constructed schedule to and "output" 
	 */
	public void writeToFile() {
		File folder = new File("output"); //create folder named output if not exist already
		if (!folder.exists()) {
			folder.mkdir();
		}
		File file = new File(OUTPUT_DIRECTORY + _outputFileName); //create file with the given file name
		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter(file, false)); //overwrite the content every time
			String name = _graph.getName(); 
			char c = name.charAt(0); //the first letter is capitalised
			if (Character.isLowerCase(c)) {
				name = Character.toUpperCase(c) + name.substring(1, name.length());
			}
			fw.write("digraph \"output" + name + "\" {\n"); //the name has prefix of "output" now.
			for (Vertex v: _graph.getVertices()) {
				if (v.getName() != "-") {
					fw.write("\t"+v.getName()+"\t [Weight=" + v.getWeight() + ",Start=" + _schedule.getVertexStartTime(v) //vertices have two additional arguments 
					+ ",Processor="+ (_schedule.getProcessorIndex(v)+1) + "];\n");									// start time and processor number.					
				}
			}
			for (Edge e: _graph.getEdges()) { //lines for edge look identical. 		
				if (e.getSource().getName()!= "-") {
					fw.write("\t"+e.getSource().getName() + " -> " + e.getDestination().getName() + "\t [Weight=" + e.getWeight() + "];\n");					
				}
			}
			fw.write("}");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
