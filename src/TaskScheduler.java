import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import fileManager.*;
import graph.Graph;
import gui.GraphVisualiser;
import scheduler.Schedule;
import scheduler.Scheduler;
/**
 * The main class of the program. Takes input arguments from user input, reads the input file
 * and creates a graph, then uses it to create a correct schedule which is written as an output file.
 *
 * @author CJ Bang
 *
 */
public class TaskScheduler {
	// strings for error messages
	final String FILENAME_NOT_GIVEN = "Please enter the filename and number of processors as per instruction.";
	final String INVALID_FILENAME = "File can't be found. The input dot file should be in the same directory as the jar file. Please try again.";
	final String PROCESSOR_NUMBER_NOT_GIVEN = "Number of Processors is not given. Please indicate the number of processors to schedule the input graph on.";
	final String INVALID_PROCESSOR = "Number of Processors needs to be given as a digit. ie. 5 instead of five";
	final String INVALID_OPTION_VALUE = "Selected option has missing value. Please specify your option correctly as per instruction.";
	final String INVALID_OPTION = "Invalid option: \"-p\", \"-v\", or \"-o\" was not located correctly.";
	final String CORE_NUMBER_NOT_GIVEN = "Number of Cores for execution needs to be given as a digit. ie. 5 instead of five";
	final String CONFIRMATION_MESSAGE = "If options are correctly set, Press \"y\" for yes. The program will start executing automatically.\nIf you want to make changes, press any other key to exit and re-execute the file.";

	int indexOfArguments = 2; //0 is always the file name, 1 is the number of processor
	String _inputFileName, _outputFileName;
	int _noOfProcessors;
	int _noOfCores = -1; // by default number of cores is not set = it is sequential.
	boolean _visualisation = false; // by default visualisation is off
	Path _filepath = null; 
	
	/**
	 * Main program. It takes arguments from the user input and pass them to other methods to process.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new TaskScheduler(args);
	}

	public TaskScheduler(String[] args) throws Exception{
		parseArguments(args);
		//startExecution();
		confirmOptionsAndExecute();

	}
	/**
	 * Runs scheduler once user confirms the options selected. 
	 * Firstly program reads the input file using input reader and creates the graph.
	 * Using the graph, the program creates a correct schedule with the shortest time.
	 * And it generates an output file for the created schedule. 
	 * @throws Exception 
	 */
	private void startExecution() throws Exception {
		//InputReader ir = new InputReader(_inputFileName); //input file must be in same directory as jar file

		Path currentRelativePath = Paths.get("");
		Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.		
		String filename = "input" + File.separatorChar + _inputFileName;
		_filepath = currentDir.resolve(filename);
		InputReader ir = new InputReader(_filepath.toString());

		Graph graph = ir.readFile();
		graph.setUpForMakingSchedules();
		
		Scheduler scheduler = new Scheduler(_noOfProcessors, _visualisation);	
		Schedule s = scheduler.getOptimalSchedule();
		OutputWriter ow = new OutputWriter(_outputFileName, graph, s);
		ow.writeToFile();
		
		//Temporary check for the output
		String output = "Last Vertex = " + s.getLastUsedVertex().getName() +"\t|Time Taken = "+s.getTimeOfSchedule() + "\t|Note = - means empty\t|Format= Vertex:time"
							+"\n"+ s.toString();
		System.out.println(output);
	}
	
	/**
	 * Parses given arguments and stores them as variables. Invalid argument 
	 * throws an appropriate exceptions. When an exception gets thrown,
	 * an appropriate error message is provided and the program is halted.
	 * @param args
	 */
	private void parseArguments(String[] args) {
		try { // first argument is the file name. The output file name is determined by the input file name.
			_inputFileName = args[0];
			_outputFileName = _inputFileName.substring(0, _inputFileName.length()-4) + "-output.dot";
			//FileReader fr = new FileReader(new File(_inputFileName)); // try to read input file to see if it exists
			
			Path currentRelativePath = Paths.get("");
			Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.		
			String filename = "input" + File.separatorChar + _inputFileName;
			_filepath = currentDir.resolve(filename);
			FileReader fr = new FileReader(new File(_filepath.toString()));
		} catch (ArrayIndexOutOfBoundsException e){ // when the argument is not given
			System.out.println(FILENAME_NOT_GIVEN);
			System.exit(0);
		} catch (FileNotFoundException e) { // when the file cannot be located 
			System.out.println(INVALID_FILENAME);
			System.exit(0);
		}
		try { // second argument is the number of processors that user wants to execute the program with.
			_noOfProcessors = Integer.parseInt(args[1]);
		} catch (ArrayIndexOutOfBoundsException e1) { // when the argument is not given
			System.out.println(PROCESSOR_NUMBER_NOT_GIVEN);
			System.exit(0);
		} catch (NumberFormatException e2) { // when the given argument is not a number
			System.out.println(INVALID_PROCESSOR);
			System.exit(0);
		}
		if (args.length > 2) { // When additional optional parameters are passed in
			checkAdditionalOptions(args);
		}
	}
	/**
	 * Checks the additional option arguments.
	 * Depending on the option type, user may or may not need to pass an additional
	 * argument to specify option value. Therefore this method selectively checks for
	 * each option type and the required arguments.If any part of the arguments is
	 * incorrect, an appropriate error message is provided and the program is halted.
	 * @param args
	 */
	private void checkAdditionalOptions(String[] args) {
		while (indexOfArguments < args.length) { //checks the additional options - length could vary therefore using while loop
			if (args[indexOfArguments].equals("-p") || args[indexOfArguments].equals("-o")) { //options are correctly given but its not visualisation
				try {
					checkAdditionalOptionValue(args[indexOfArguments], args[indexOfArguments+1]); // then there has to be one following argument
				} catch (ArrayIndexOutOfBoundsException e1) { // when the value was not given
					System.out.println(INVALID_OPTION_VALUE);
					System.exit(0);
				} catch (NumberFormatException e2) { // for -p option, if the number of cores is not a number
					System.out.println(CORE_NUMBER_NOT_GIVEN);
					System.exit(0);
				}
				indexOfArguments += 2; // move to the next set of arguments if there are more
			} else if (args[indexOfArguments].equals("-v")) { // option is visualisation
				_visualisation = true;
				indexOfArguments += 1; // doesnt require another value to follow
			} else { // the argument was not an option selector therefore invalid argument
				System.out.println(INVALID_OPTION);
				System.exit(0);
			}
		}
	}

	/**
	 * Checks if the correct value was given for the option chosen by user.
	 * -p should be followed by an integer value.
	 * -o should be followed by a filename.
	 * if values are not given correctly, an appropriate exception gets thrown.
	 * @param option
	 * @param value
	 * @throws ArrayIndexOutOfBoundsException
	 * @throws NumberFormatException
	 */
	private void checkAdditionalOptionValue(String option, String value) throws ArrayIndexOutOfBoundsException, NumberFormatException {
		if (option.equals("-p")) { //if -p is selected, then it is followed by a number
			_noOfCores = Integer.parseInt(value);
		}
		if (option.equals("-o")) { //if -o is selected, then it is followed by a string
			_outputFileName = value + ".dot";
		}
	}

	/**
	 * Prints off the selected options before the scheduler starts executing.
	 * User confirms by pressing y which then allows the scheduler to start execution.
	 * If user does not press y, the program is halt and has to be re-executed.
	 * @throws Exception
	 */
	private void confirmOptionsAndExecute() throws Exception {
		// let user to review what options have been chosen and confirm
		System.out.println("*********************************************************************");
		System.out.println("The Input Graph to be scheduled is: " + _inputFileName);
		System.out.println("The Number of Processors to be used is: " + _noOfProcessors);
		if (_noOfCores == -1) {
			System.out.println("No of Cores for execution in parallel is: sequential");
		} else {
			System.out.println("No of Cores for execution in parallel is: " + _noOfCores);
		}
		if (_visualisation) {
			System.out.println("Visualisation Effect is: On");
		} else {
			System.out.println("Visualisation Effect is: Off");
		}
		System.out.println("The Name of Output File is to be: " + _outputFileName);
		System.out.println("*********************************************************************");
		System.out.println(CONFIRMATION_MESSAGE);

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); // get user confirmation
		String line;
		try {
			if ((line = br.readLine()).equals("y")) { // user confirms the options by pressing "y"
				startExecution();
			} else { // if other key was entered, then the program stops
				System.out.println("exiting...");
				System.exit(0);
			}
		} catch (IOException e) {
		}
	}
}