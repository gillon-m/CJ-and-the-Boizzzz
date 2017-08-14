import java.io.*;

import fileManager.*;
import graph.Graph;
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
	//final String DIRECTORY = "./input/";
	final String FILENAME_NOT_GIVEN = "Please enter the filename and number of processors as per instruction.";
	final String INVALID_FILENAME = "File can't be found. The input dot file should be in the same directory as the jar file. Please try again.";
	final String PROCESSOR_NUMBER_NOT_GIVEN = "Number of Processors is not given. Please indicate the number of processors to schedule the input graph on.";
	final String INVALID_PROCESSOR = "Number of Processors needs to be given as a digit. ie. 5 instead of five";
	final String INVALID_OPTION_VALUE = "Selected option has missing value. Please specify your option correctly as per instruction.";
	final String INVALID_OPTION = "Invalid option: \"-p\", \"-v\", or \"-o\" was not located correctly.";
	final String CORE_NUMBER_NOT_GIVEN = "Number of Cores for execution needs to be given as a digit. ie. 5 instead of five";
	final String CONFIRMATION_MESSAGE = "If options are correctly set, Press \"y\" for yes. The program will start executing automatically.\nIf you want to make changes, press any other key to exit and re-execute the file.";
	int indexOfArguments = 2;
	String _inputFileName, _outputFileName; 
	int _noOfProcessors;
	int _noOfCores = -1;
	boolean _visualisationOn = false; // by default visualisation is off 

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
		//InputReader ir = new InputReader(DIRECTORY + _inputFileName);
		InputReader ir = new InputReader(_inputFileName); //input file must be in same directory as jar file
		Graph graph = ir.readFile();
		graph.setUpForMakingSchedules();
		Scheduler scheduler = new Scheduler(_noOfProcessors);	
		Schedule s =  scheduler.getOptimalSchedule();
		OutputWriter ow = new OutputWriter(_outputFileName, graph, s);
		ow.writeToFile();
		
		//Temporary check for the output
		String output = "Last Vertex = " + s.getLastUsedVertex().getName() +"\t|Time Taken = "+s.getTimeOfSchedule() + "\t|Note = - means empty\t|Format= Vertex:time"
							+"\n"+ s.toString();
		System.out.println(output);
	}
	
	/**
	 * Parses the given arguments and stores them as variables. Invalid argument 
	 * throws an appropriate exceptions. When an exception gets thrown,
	 * an appropriate error message is provided and the program is halted.
	 * @param args
	 */
	private void parseArguments(String[] args) {
		try {
			_inputFileName = args[0];
			_outputFileName = _inputFileName.substring(0, _inputFileName.length()-4) + "-output.dot";
			//FileReader fr = new FileReader(new File(DIRECTORY + _inputFileName));
			FileReader fr = new FileReader(new File(_inputFileName)); //input file must be in same directory as jar file
		} catch (ArrayIndexOutOfBoundsException e){
			System.out.println(FILENAME_NOT_GIVEN);
			System.exit(0);
		} catch (FileNotFoundException e) {
			System.out.println(INVALID_FILENAME);
			System.exit(0);
		}
		try {
			_noOfProcessors = Integer.parseInt(args[1]);
		} catch (ArrayIndexOutOfBoundsException e1) {
			System.out.println(PROCESSOR_NUMBER_NOT_GIVEN);
			System.exit(0);
		} catch (NumberFormatException e2) {
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
		while (indexOfArguments < args.length) {
			if (args[indexOfArguments].equals("-p") || args[indexOfArguments].equals("-o")) { //options are correctly given but its not visualisation
				try {
					checkAdditionalOptionValue(args[indexOfArguments], args[indexOfArguments+1]);
				} catch (ArrayIndexOutOfBoundsException e1) {
					System.out.println(INVALID_OPTION_VALUE);
					System.exit(0);
				} catch (NumberFormatException e2) {
					System.out.println(CORE_NUMBER_NOT_GIVEN);
					System.exit(0);
				}
				indexOfArguments += 2;
			} else if (args[indexOfArguments].equals("-v")) { // option is visualisation
				indexOfArguments += 1;
			} else {
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
		if (option.equals("-p")) {
			_noOfCores = Integer.parseInt(value);
		}
		if (option.equals("-o")) {
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
		System.out.println("*********************************************************************");
		System.out.println("The Input Graph to be scheduled is: " + _inputFileName);
		System.out.println("The Number of Processors to be used is: " + _noOfProcessors);
		if (_noOfCores == -1) {
			System.out.println("No of Cores for execution in parallel is: sequential");
		} else {
			System.out.println("No of Cores for execution in parallel is: " + _noOfCores);
		}
		if (_visualisationOn) {
			System.out.println("Visualisation Effect is: On");
		} else {
			System.out.println("Visualisation Effect is: Off");
		}
		System.out.println("The Name of Output File is to be: " + _outputFileName);
		System.out.println("*********************************************************************");
		System.out.println(CONFIRMATION_MESSAGE);		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line;
		try {
			if ((line = br.readLine()).equals("y")) {
				startExecution();
			} else {
				System.out.println("exiting...");
				System.exit(0);
			}
		} catch (IOException e) {
		}		
	}
}