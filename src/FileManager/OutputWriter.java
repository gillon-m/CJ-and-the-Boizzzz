package FileManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OutputWriter {
	File _inputFile;
	String _outputFileName;
	public OutputWriter(String outputFileName, String inputFileName) {
		_inputFile = new File(inputFileName);
		_outputFileName = outputFileName;
	}
	public void writeToFile(String output) {
		File file = copyOfInputFile(_inputFile);
		try {
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, false)); //overwrite the content every time
			fileWriter.write(output);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private File copyOfInputFile(File inputFile) {
		File copy = new File(_outputFileName);
		try {
			copy.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//copy input file to new output file
		return copy;
	}
}
