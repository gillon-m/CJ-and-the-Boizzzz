package App;

import java.io.*;
import java.util.Map;

import FileManager.*;
import SearchSpace.Schedule;
import SearchSpace.SearchSpace;
public class App {
	private static Vertex _root;
	
	// When running through the eclipse, change this fixedFileName so that you dont have to give arguments each time you run.
	static String fixedFileName = "E:\\stuff\\uni\\SOFTENG306\\CJ-and-the-Boizzzz\\input\\ex1_in.dot";
	//String fixedFileName = "C:\\Projects\\CJ-and-the-Boizzzz\\input\\ex1_in.dot";
	//C:\Users\Andon\Desktop\CJ-and-the-Boizzzz-master\input\ex1_in.dot

	static String _inputFileName = ""; 
	static int _noOfProcessors = 2; // for now its set at 2
	static int _noOfCores = -1;
	static boolean _visualisationOn = false; // by default visualisation is off 
	static String _outputFileName = "Output.dot";
	public static void main(String[] args) {
		if (args.length > 0) { // When a jar file is being executed
			try {
				_inputFileName = args[0];
				_outputFileName = _inputFileName.substring(0, _inputFileName.length()-4) + "-output.dot";
				FileReader fr = new FileReader(new File(_inputFileName));
			} catch (FileNotFoundException e) {
				System.out.println("File can't be found. The input dot file should be in the same directory as the jar file. Please try again.");
				System.exit(0);
			}
			try {
				_noOfProcessors = Integer.parseInt(args[1]);
			} catch (ArrayIndexOutOfBoundsException e1) {
				System.out.println("Number of Processors is not given. Please indicate the number of processors to schedule the input graph on.");
				System.exit(0);
			} catch (NumberFormatException e2) {
				System.out.println("Number of Processors needs to be given as a digit. ie. 5 instead of five");
				System.exit(0);
			}
			if (args.length > 2) { // When additional optional parameters are passed in
				for (int i = 2; i < args.length; i++) {
					try {
						if (args[i].equals("-p")) {
							_noOfCores = Integer.parseInt(args[i+1]);
						}
						if (args[i].equals("-v")) {
							_visualisationOn = true;
						}
						if (args[i].equals("-o")) {
							_outputFileName = args[i+1] + ".dot";
						}
					} catch (ArrayIndexOutOfBoundsException e1) {
						System.out.println("Invalid additional arguments. Please double check the arguments.");
						System.exit(0);
					} catch (NumberFormatException e2) {
						System.out.println("Number of Cores for execution needs to be given as a digit. ie. 5 instead of five");
					}
				}
			}
			confirmOptionsAndExecute();
		}
		// When arguments are not passed in ie. through eclipse - use fixedFileName instead of taking argument
		startExecution(fixedFileName);
	}
	
	/**
	 * This method prints off the selected options before the scheduler starts executing.
	 * User confirms by pressing y which then allows the scheduler to start execution.
	 * If user does not press y, the program is halt and has to be re-executed.
	 */
	private static void confirmOptionsAndExecute() {
		System.out.println("*********************************************************************");
		System.out.println("The Input Graph to be scheduled is: " + _inputFileName);
		System.out.println("The Number of Processors to be used is: " + _noOfProcessors);
		if (_noOfCores != -1) {
			System.out.println("No of Cores for execution in parallel is: " + _noOfCores);
		}
		if (_visualisationOn) {
			System.out.println("Visualisation Effect is: On");
		} else {
			System.out.println("Visualisation Effect is: Off");
		}
		System.out.println("The Name of Output File is to be: " + _outputFileName);
		System.out.println("*********************************************************************");
		System.out.println("If options are correctly set, Press \"y\" for yes. The program will start executing automatically. If you want to make changes, press any other key to exit and re-execute the file.");		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line;
		try {
			if ((line = br.readLine()).equals("y")) {
				startExecution(_inputFileName);
			} else {
				System.out.println("exiting...");
				System.exit(0);
			}
		} catch (IOException e) {
		}		
	}
	/**
	 * Runs scheduler once user confirms the options selected.
	 * @param fileName
	 */
	private static void startExecution(String inputFileName) {
		InputReader ir = new InputReader(inputFileName);
		Graph graph = ir.readFile();
		
		SearchSpace searchSpace = new SearchSpace(graph, _noOfProcessors);	
		searchSpace.makeSearchSpace();								// Creates all possible schedules
		//searchSpace.tempPrintOutSchedules();						// prints out all possible schedules
		//searchSpace.tempPrintOutLastNodeScheduleInTimeOrder();		// prints out schedule for last vertex
		
		String output = searchSpace.outputToPrint();
		//OutputWriter ow = new OutputWriter(_outputFileName, inputFileName);
		//ow.writeToFile(output);
		
		//setUpChildrenParents(graph);
		//setUpLevelsOfNodes(graph);	
		graph.printGraph();

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
