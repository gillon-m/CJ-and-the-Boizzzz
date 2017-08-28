
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import fileManager.InputReader;
import graph.Graph;
import scheduler.ParallelisedScheduler;
import scheduler.Schedule;


public class TestScheduler {
	
    @Test
    public void testEx1Processor2() throws Exception {
		Path currentRelativePath = Paths.get("");
		Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.		
		String filename = "input" + File.separatorChar + "ex1_in.dot";
		Path filepath = currentDir.resolve(filename);
		InputReader ir = new InputReader(filepath.toString());
		Graph graph = ir.readFile();
		
		assertEquals(4, graph.getVertices().size());
		assertEquals(4, graph.getEdges().size());
		assertEquals("example", graph.getName());

		graph.setUpForMakingSchedules();
		ParallelisedScheduler scheduler = new ParallelisedScheduler(2, 4, false);	
		//ParallelisedScheduler scheduler = new ParallelisedScheduler(2, -1, false);	
		Schedule s =  scheduler.getOptimalSchedule();

		assertEquals(4, s.getAllUsedVerticesWithoutEmpty().size());
		assertEquals(8, s.getTimeOfSchedule());
    }
    
    @Test
    public void testEx2Processor2() throws Exception {
		Path currentRelativePath = Paths.get("");
		Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.		
		String filename = "input" + File.separatorChar + "ex2_in.dot";
		Path filepath = currentDir.resolve(filename);
		InputReader ir = new InputReader(filepath.toString());
		Graph graph = ir.readFile();

		assertEquals(7, graph.getVertices().size());
		assertEquals(9, graph.getEdges().size());
		assertEquals("example2", graph.getName());

		graph.setUpForMakingSchedules();
		ParallelisedScheduler scheduler = new ParallelisedScheduler(2, 4, false);	
		//ParallelisedScheduler scheduler = new ParallelisedScheduler(2, -1, false);	
		Schedule s =  scheduler.getOptimalSchedule();

		assertEquals(7, s.getAllUsedVerticesWithoutEmpty().size());
		assertEquals(12, s.getTimeOfSchedule());
    }

    @Test
    public void testNodes7Processor2() throws Exception {
		Path currentRelativePath = Paths.get("");
		Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.		
		String filename = "input" + File.separatorChar + "Nodes_7_OutTree.dot";
		Path filepath = currentDir.resolve(filename);
		InputReader ir = new InputReader(filepath.toString());
		Graph graph = ir.readFile();

		assertEquals(7, graph.getVertices().size());
		assertEquals(6, graph.getEdges().size());
		assertEquals("OutTree-Balanced-MaxBf-3_Nodes_7_CCR_2.0_WeightType_Random", graph.getName());

		graph.setUpForMakingSchedules();
		ParallelisedScheduler scheduler = new ParallelisedScheduler(2, 4, false);	
		//ParallelisedScheduler scheduler = new ParallelisedScheduler(2, -1, false);	
		Schedule s =  scheduler.getOptimalSchedule();
		
		assertEquals(7, s.getAllUsedVerticesWithoutEmpty().size());
		assertEquals(28, s.getTimeOfSchedule());
    }
    @Test
    public void testNodes7Processor4() throws Exception {
		Path currentRelativePath = Paths.get("");
		Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.		
		String filename = "input" + File.separatorChar + "Nodes_7_OutTree.dot";
		Path filepath = currentDir.resolve(filename);
		InputReader ir = new InputReader(filepath.toString());
		Graph graph = ir.readFile();

		assertEquals(7, graph.getVertices().size());
		assertEquals(6, graph.getEdges().size());
		assertEquals("OutTree-Balanced-MaxBf-3_Nodes_7_CCR_2.0_WeightType_Random", graph.getName());

		graph.setUpForMakingSchedules();
		ParallelisedScheduler scheduler = new ParallelisedScheduler(4, 4, false);	
		//ParallelisedScheduler scheduler = new ParallelisedScheduler(4, -1, false);	
		Schedule s =  scheduler.getOptimalSchedule();
		
		assertEquals(7, s.getAllUsedVerticesWithoutEmpty().size());
		assertEquals(22, s.getTimeOfSchedule());
    }
    @Test
    public void testNodes8Processor2() throws Exception {
		Path currentRelativePath = Paths.get("");
		Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.		
		String filename = "input" + File.separatorChar + "Nodes_8_Random.dot";
		Path filepath = currentDir.resolve(filename);
		InputReader ir = new InputReader(filepath.toString());
		Graph graph = ir.readFile();

		assertEquals(8, graph.getVertices().size());
		assertEquals(16, graph.getEdges().size());
		assertEquals("Random_Nodes_8_Density_2.0_CCR_0.1_WeightType_Random", graph.getName());

		graph.setUpForMakingSchedules();
		ParallelisedScheduler scheduler = new ParallelisedScheduler(2, 4, false);	
		//ParallelisedScheduler scheduler = new ParallelisedScheduler(2, -1, false);	
		Schedule s =  scheduler.getOptimalSchedule();
		
		assertEquals(8, s.getAllUsedVerticesWithoutEmpty().size());
		assertEquals(581, s.getTimeOfSchedule());
    }
    @Test
    public void testNodes8Processor4() throws Exception {
		Path currentRelativePath = Paths.get("");
		Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.		
		String filename = "input" + File.separatorChar + "Nodes_8_Random.dot";
		Path filepath = currentDir.resolve(filename);
		InputReader ir = new InputReader(filepath.toString());
		Graph graph = ir.readFile();

		assertEquals(8, graph.getVertices().size());
		assertEquals(16, graph.getEdges().size());
		assertEquals("Random_Nodes_8_Density_2.0_CCR_0.1_WeightType_Random", graph.getName());

		graph.setUpForMakingSchedules();
		ParallelisedScheduler scheduler = new ParallelisedScheduler(4, 4, false);	
		//ParallelisedScheduler scheduler = new ParallelisedScheduler(4, -1, false);	
		Schedule s =  scheduler.getOptimalSchedule();
		
		assertEquals(8, s.getAllUsedVerticesWithoutEmpty().size());
		assertEquals(581, s.getTimeOfSchedule());
    }
    @Test
    public void testNodes9Processor2() throws Exception {
		Path currentRelativePath = Paths.get("");
		Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.		
		String filename = "input" + File.separatorChar + "Nodes_9_SeriesParallel.dot";
		Path filepath = currentDir.resolve(filename);
		InputReader ir = new InputReader(filepath.toString());
		Graph graph = ir.readFile();

		assertEquals(9, graph.getVertices().size());
		assertEquals(12, graph.getEdges().size());
		assertEquals("SeriesParallel-MaxBf-3_Nodes_9_CCR_10.0_WeightType_Random", graph.getName());

		graph.setUpForMakingSchedules();
		ParallelisedScheduler scheduler = new ParallelisedScheduler(2, 4, false);	
		//ParallelisedScheduler scheduler = new ParallelisedScheduler(2, -1, false);	
		Schedule s =  scheduler.getOptimalSchedule();
		
		assertEquals(9, s.getAllUsedVerticesWithoutEmpty().size());
		assertEquals(55, s.getTimeOfSchedule());
    }
    @Test
    public void testNodes9Processor4() throws Exception {
		Path currentRelativePath = Paths.get("");
		Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.		
		String filename = "input" + File.separatorChar + "Nodes_9_SeriesParallel.dot";
		Path filepath = currentDir.resolve(filename);
		InputReader ir = new InputReader(filepath.toString());
		Graph graph = ir.readFile();

		assertEquals(9, graph.getVertices().size());
		assertEquals(12, graph.getEdges().size());
		assertEquals("SeriesParallel-MaxBf-3_Nodes_9_CCR_10.0_WeightType_Random", graph.getName());

		graph.setUpForMakingSchedules();
		ParallelisedScheduler scheduler = new ParallelisedScheduler(4, 4, false);	
		//ParallelisedScheduler scheduler = new ParallelisedScheduler(4, -1, false);	
		Schedule s =  scheduler.getOptimalSchedule();
		
		assertEquals(9, s.getAllUsedVerticesWithoutEmpty().size());
		assertEquals(55, s.getTimeOfSchedule());
    }
    @Test
    public void testNodes10Processor2() throws Exception {
		Path currentRelativePath = Paths.get("");
		Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.		
		String filename = "input" + File.separatorChar + "Nodes_10_Random.dot";
		Path filepath = currentDir.resolve(filename);
		InputReader ir = new InputReader(filepath.toString());
		Graph graph = ir.readFile();

		assertEquals(10, graph.getVertices().size());
		assertEquals(19, graph.getEdges().size());
		assertEquals("Random_Nodes_10_Density_1.90_CCR_10.00_WeightType_Random", graph.getName());

		graph.setUpForMakingSchedules();
		ParallelisedScheduler scheduler = new ParallelisedScheduler(2, 4, false);	
		//ParallelisedScheduler scheduler = new ParallelisedScheduler(2, -1, false);	
		Schedule s =  scheduler.getOptimalSchedule();
		
		assertEquals(10, s.getAllUsedVerticesWithoutEmpty().size());
		assertEquals(50, s.getTimeOfSchedule());
    }
    @Test
    public void testNodes10Processor4() throws Exception {
		Path currentRelativePath = Paths.get("");
		Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.		
		String filename = "input" + File.separatorChar + "Nodes_10_Random.dot";
		Path filepath = currentDir.resolve(filename);
		InputReader ir = new InputReader(filepath.toString());
		Graph graph = ir.readFile();

		assertEquals(10, graph.getVertices().size());
		assertEquals(19, graph.getEdges().size());
		assertEquals("Random_Nodes_10_Density_1.90_CCR_10.00_WeightType_Random", graph.getName());

		graph.setUpForMakingSchedules();
		ParallelisedScheduler scheduler = new ParallelisedScheduler(4, 4, false);	
		//ParallelisedScheduler scheduler = new ParallelisedScheduler(4, -1, false);	
		Schedule s =  scheduler.getOptimalSchedule();
		
		assertEquals(10, s.getAllUsedVerticesWithoutEmpty().size());
		assertEquals(50, s.getTimeOfSchedule());
    }
    @Test
    public void testNodes11Processor2() throws Exception {
		Path currentRelativePath = Paths.get("");
		Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.		
		String filename = "input" + File.separatorChar + "Nodes_11_OutTree.dot";
		Path filepath = currentDir.resolve(filename);
		InputReader ir = new InputReader(filepath.toString());
		Graph graph = ir.readFile();

		assertEquals(11, graph.getVertices().size());
		assertEquals(10, graph.getEdges().size());
		assertEquals("OutTree-Balanced-MaxBf-3_Nodes_11_CCR_0.1_WeightType_Random", graph.getName());

		graph.setUpForMakingSchedules();
		ParallelisedScheduler scheduler = new ParallelisedScheduler(2, 4, false);	
		//ParallelisedScheduler scheduler = new ParallelisedScheduler(2, -1, false);	
		Schedule s =  scheduler.getOptimalSchedule();

		assertEquals(11, s.getAllUsedVerticesWithoutEmpty().size());
		assertEquals(350, s.getTimeOfSchedule());
    }
    @Test
    public void testNodes11Processor4() throws Exception {
		Path currentRelativePath = Paths.get("");
		Path currentDir = currentRelativePath.toAbsolutePath(); // <-- Get the Path and use resolve on it.		
		String filename = "input" + File.separatorChar + "Nodes_11_OutTree.dot";
		Path filepath = currentDir.resolve(filename);
		InputReader ir = new InputReader(filepath.toString());
		Graph graph = ir.readFile();

		assertEquals(11, graph.getVertices().size());
		assertEquals(10, graph.getEdges().size());
		assertEquals("OutTree-Balanced-MaxBf-3_Nodes_11_CCR_0.1_WeightType_Random", graph.getName());

		graph.setUpForMakingSchedules();
		ParallelisedScheduler scheduler = new ParallelisedScheduler(4, 4, false);	
		//ParallelisedScheduler scheduler = new ParallelisedScheduler(4, -1, false);	
		Schedule s =  scheduler.getOptimalSchedule();
		
		assertEquals(11, s.getAllUsedVerticesWithoutEmpty().size());
		assertEquals(227, s.getTimeOfSchedule());
    }
}
