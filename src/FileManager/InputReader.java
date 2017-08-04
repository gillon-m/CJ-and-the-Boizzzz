package FileManager;

import java.io.*;

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
	String fixedFile = "/media/cj/SHARED/workspace/Java/306-project1/input/ex1_in.dot";
	public InputReader() {
		readFile();
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
	private void readFile() {
		// Get user to type in an absolute path for .dot file
		
		
		System.out.println("Drop a dot file here and press Enter.");
		loadFile(); 
		File file = new File(trimmedFileName);


		
		// Or just used one file for now
		
//		File file = new File(fixedFile);
		// file selected!!! can implement JGrapht thing here
		

		try { //if file exists
			BufferedReader br2 = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br2.readLine()) != null) { // read each line and write on console until the end
				if (line.endsWith(";")) {
					System.out.println(line);
				}
			}
		} catch (FileNotFoundException e) { //if file does not exist, get user input again
			System.out.print("Invalid File url. Try again. ");
			trimmedFileName = "";
			readFile();
		} catch (IOException e) {
		}

		/////
		/////
		/////
	}
}
