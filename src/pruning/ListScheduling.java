package pruning;

import java.util.ArrayList;
import java.util.List;

import components.CalculateBottomLevel;
import graph.Graph;
import graph.Vertex;
import heuristics.CostFunctionCalculator;
import scheduler.Schedule;
/**
 * This class does list scheduling to calculates the upper bound cost
 * 
 * @author Alex Yoo
 *
 */
public class ListScheduling {
	private int _numberOfProcessors;
	public ListScheduling(int numberOfProcessors) {
		_numberOfProcessors = numberOfProcessors;
	}
	
	/**
	 * Returns the cost function of the upper bound 
	 * @return
	 */
	public int getUpperBoundCostFunction() {
		List<Vertex> scheduledList = this.getListVertexInPriority();
		Schedule upperBoundSchedule = this.getSchedule(scheduledList);
		return this.costOfSchedule(upperBoundSchedule);
	}
	
	/**
	 * This method generates a list of the vertices in priority order
	 * and also respects its dependencies
	 * @return
	 */
	private List<Vertex> getListVertexInPriority(){
		List<Vertex> scheduledList = new ArrayList<Vertex>();
		List<Vertex> freeVertices = new ArrayList<Vertex>(Graph.getInstance().getRootVertices());

		while(!freeVertices.isEmpty()) {
			Vertex v = this.getBestPrioritisedVertex(freeVertices);
			scheduledList.add(v);
			freeVertices.remove(v);
			this.addVertexIfDoesNotExist(v.getChildren(), freeVertices);
		}
		
		return scheduledList;
	}
	/**
	 * Adds the vertex from childVertices into freeVertices if it does not exist
	 * @param childVertices
	 * @param freeVertices
	 */
	private void addVertexIfDoesNotExist(List<Vertex> childVertices, List<Vertex> freeVertices) {
		for(Vertex v: childVertices) {
			if(!freeVertices.contains(v)) {
				freeVertices.add(v);
			}
		}
	}
	/**
	 * Goes through the list of vertices to give back the vertex with the highest priority
	 * Priority is decided by the bottom level of a vertex
	 * 
	 * @param listOfVertices
	 * @return
	 */
	private Vertex getBestPrioritisedVertex(List<Vertex> listOfVertices) {
		Vertex bestPrioritisedVertex = null;
		int highestPriority = 0;
		for(Vertex v : listOfVertices) {
			int tempCost = CalculateBottomLevel.getBtmLvl(v);
			if(highestPriority < tempCost) {
				highestPriority = tempCost;
				bestPrioritisedVertex = v;
			}
		}
		return bestPrioritisedVertex;
	}

	/**
	 * Produces a schedule from the scheduled list of vertices
	 * 
	 * @param listOfVertices
	 * @return
	 */
	private Schedule getSchedule(List<Vertex> listOfVertices) {
		Schedule schedule = new Schedule(_numberOfProcessors);
		Schedule[] schedules = new Schedule[_numberOfProcessors];

		for(int i = 0; i < listOfVertices.size(); i++) {
			Vertex v = listOfVertices.get(i);
			schedules = schedule.generateAllPossibleScheduleForSpecifiedVertex(v);
			int smallestTimeForThisVertex = 0;
			boolean firstTime = true;
			for(int j = 0 ; j < schedules.length; j++) {
				if(firstTime) {
					smallestTimeForThisVertex = schedules[j].getVertexStartTime(v);
					firstTime = false;
					schedule = schedules[j];
				} else if(smallestTimeForThisVertex > schedules[j].getVertexStartTime(v)) {
					smallestTimeForThisVertex = schedules[j].getVertexStartTime(v);
					schedule = schedules[j];
				}
			}
		}
		
		return schedule;
	}
	/**
	 * gets back a cost function of the given schedule
	 * @param s
	 * @return
	 */
	private int costOfSchedule(Schedule s) {
		CostFunctionCalculator c = new CostFunctionCalculator();
		return c.getTotalCostFunction(s);
	}
}
