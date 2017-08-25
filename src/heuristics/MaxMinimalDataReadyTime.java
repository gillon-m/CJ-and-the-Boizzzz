package heuristics;

import java.util.List;

import components.CalculateBottomLevel;
import graph.Vertex;
import scheduler.Schedule;

/**
 * 
 * Calculates Heuristics cost function max minimal data ready time
 * 
 * Cost Function = highest cost out of the list of child vertices schedules (which is the least cost schedule + bottom level possible for that child vertex) 
 * 
 * @author Alex Yoo
 *
 */
public class MaxMinimalDataReadyTime implements ICostFunction {

	@Override
	public int getCostFunction(Schedule currentSchedule) {
		return this.minimalDataReadyTime(currentSchedule);
	}
	
	private int minimalDataReadyTime(Schedule currentSchedule) {
		int costFunction = 0;
		
		costFunction = this.getEarliestTimeNodeCanStart(currentSchedule);

		return costFunction;
	}
	/**
	 * Helps with the method minimalDataReadyTime method
	 * It goes through the child vertices to find the least cost to make a schedule for that specific vertex.
	 * 
	 * And then it compares the least cost schedule between child vertices with bottom level added onto it 
	 * It returns the highest value from this comparison
	 * 
	 * @param childVertices
	 * @return
	 */
	private int getEarliestTimeNodeCanStart(Schedule currentSchedule) {
		List<Vertex> childVertices = currentSchedule.getChildVertices();
		int largestTime = 0;
		for(Vertex v : childVertices) {
			int smallestTimeForThisVertex = 0;
			boolean firstTime = true;
			Schedule s = new Schedule(currentSchedule);
			Schedule[] childSchedules = s.generateAllPossibleScheduleForSpecifiedVertex(v);
			for(int i = 0 ; i < childSchedules.length; i++) {
				if(firstTime) {
					smallestTimeForThisVertex = childSchedules[i].getVertexStartTime(v);
					firstTime = false;
				} else if(smallestTimeForThisVertex > childSchedules[i].getVertexStartTime(v)) {
					smallestTimeForThisVertex = childSchedules[i].getVertexStartTime(v);
				}
			}
			int thisVertexMinimalReadyTimeCost = smallestTimeForThisVertex + CalculateBottomLevel.getBtmLvl(v);
			if(thisVertexMinimalReadyTimeCost > largestTime) {
				largestTime = thisVertexMinimalReadyTimeCost;
			}
		}
		return largestTime;
	}

}
