package test;

import App.Graph;
import FileManager.InputReader;
import SearchSpace.SearchSpace;


public class TestSchedule {
	private static final String FILENAME = "E:\\stuff\\uni\\SOFTENG306\\CJ-and-the-Boizzzz\\input\\ex1_in.dot";
	private static final int NUMBER_OF_PROCESSORS = 4;
	public static void main(String[] args){
		TestSchedule ts = new TestSchedule();
		
	}
	private TestSchedule(){
		InputReader ir = new InputReader(FILENAME);
		Graph graph = ir.readFile();
		
		SearchSpace ss = new SearchSpace(graph, NUMBER_OF_PROCESSORS);
		ss.makeSearchSpace();
		
	}
}
