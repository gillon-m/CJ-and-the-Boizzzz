package FileManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import App.Edge;
import App.Graph;
import App.Vertex;
import SearchSpace.Schedule;

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
	public void writeToFile() {
		File folder = new File("output");
		if (!folder.exists()) {
			folder.mkdir();
		}
		File file = new File(OUTPUT_DIRECTORY + _outputFileName);
		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter(file, false)); //overwrite the content every time
			String name = _graph.getName();
			char c = name.charAt(0);
			if (Character.isLowerCase(c)) {
				name = Character.toUpperCase(c) + name.substring(1, name.length()-1);
			}
			fw.write("digraph \"output" + name + "\" {\n");
			for (Vertex v: _graph.getVertices()) {
				fw.write("\t"+v.getName()+"\t [Weight=" + v.getWeight() + ",Start=" + _schedule.getStartTime(v) + ",Processor="+ (_schedule.getProcessorIndex(v)+1) + "];\n");
			}
			for (Edge e: _graph.getEdges()) {				
				fw.write("\t"+e.getSource().getName() + " -> " + e.getDestination().getName() + "\t [Weight=" + e.getWeight() + "];\n");
			}
			fw.write("}");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
