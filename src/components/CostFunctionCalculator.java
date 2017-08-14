package components;

import java.util.List;

import graph.Vertex;
import scheduler.Processor;
import scheduler.Schedule;
/**
 * This class calculates the cost function used in ScheduleComparator for A* Algorithm
 * 
 * Cost Function is equal to the highest cost out of these 3 methods
 * 		maxStartTimeAndBtmLvlNode()
 * 		idleAndComputationTime()
 * 		minimalDataReadyTime()
 * 	
 * 
 * @author Alex Yoo
 *
 */

public class CostFunctionCalculator {
	private Schedule _currentSchedule;
	private int _cost;
	private static String _nameOfEmptyVertex = "-";
	
	public CostFunctionCalculator(Schedule currentSchedule) {
		_currentSchedule = currentSchedule;
		_cost = _currentSchedule.getTimeOfSchedule();
	}
	/**
	 * returns the highest cost out of these 3 methods
	 * @return 
	 */
	public int getTotalCostFunction() {
		return Math.max(Math.max(this.maxStartTimeAndBtmLvlNode(), this.idleAndComputationTime()), this.minimalDataReadyTime());
	}
	/**
	 * Cost Function = Cost of Schedule + Bottom Level 
	 * @return
	 */
	private int maxStartTimeAndBtmLvlNode() {
		int costFunction = _cost + this.getBtmLvl(_currentSchedule.getLastUsedVertex());
		return costFunction;
	}
	/**
	 * Cost Function = (Total Idle time in schedule + Weight of all used vertices) divided by number of processor
	 * @return
	 */
	private int idleAndComputationTime() {
		int costFunction = 0;
		int totalIdleTime = 0;
		int numberOfProcessors = _currentSchedule.getNumberOfProcessors();
		
		for(Processor p : _currentSchedule.getAllProcessors()) {
			totalIdleTime += this.getIdleTimeOfProcessor(p);
		}
		costFunction += totalIdleTime;
		costFunction += this.getWeightOfAllUsedVertices();
		costFunction = costFunction/numberOfProcessors;
		
		return costFunction;
	}
	/**
	 * Cost Function = highest cost out of the list of child vertices schedules (least cost schedule + bottom level) 
	 * @return
	 */
	private int minimalDataReadyTime() {
		int costFunction = 0;
		costFunction = this.getEarliestTimeNodeCanStart(_currentSchedule.getLastUsedVertex().getChildren());
		return costFunction;
	}
	
	/**
	 * Calculates the bottom level from this vertex
	 * @param vertex
	 * @return
	 */
	private int getBtmLvl(Vertex vertex) {
		int largestBtmLvlWeight = 0;
		largestBtmLvlWeight = this.getLargestWeightPathToLeaf(vertex.getChildren(), 0);
		return largestBtmLvlWeight;
	}
	/**
	 * Adds up and returns the idle time of the specified processor
	 * @param p
	 * @return
	 */
	private int getIdleTimeOfProcessor(Processor p) {
		int idleTime = 0;
		for(Vertex v : p.getScheduleOfProcessor()) {
			if(v.getName().equals(_nameOfEmptyVertex)) {
				idleTime += v.getWeight();
			}
		}
		return idleTime;
	}
	/**
	 * Adds up and returns the weight of all vertices 
	 * @return
	 */
	private int getWeightOfAllUsedVertices() {
		int weight = 0;
		for(Vertex usedVertex : _currentSchedule.getAllUsedVertices()) {
			weight += usedVertex.getWeight();
		}
		return weight;
	}
	/**
	 * Helps with bottom level calculation
	 * 
	 * Recursively calls until it reaches a leaf node
	 * It returns the largest cost to a leaf node
	 * 
	 * @param childVertices
	 * @param weightSoFar
	 * @return
	 */
	private int getLargestWeightPathToLeaf(List<Vertex> childVertices, int weightSoFar) {
		int largestVertexWeight = 0;
		for(Vertex v : childVertices) {
			if(!v.getChildren().isEmpty()) {
				weightSoFar = this.getLargestWeightPathToLeaf(v.getChildren(), weightSoFar+v.getWeight());
			} else {
				if(weightSoFar+v.getWeight() > largestVertexWeight) {
					largestVertexWeight = weightSoFar+v.getWeight();
				}
			}
		}
		return largestVertexWeight;
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
	private int getEarliestTimeNodeCanStart(List<Vertex> childVertices) {
		int largestTime = 0;
		for(Vertex v : childVertices) {
			int smallestTimeForThisVertex = 0;
			boolean firstTime = true;
			Schedule s = new Schedule(_currentSchedule);
			Schedule[] childSchedules = s.generateAllPossibleScheduleForSpecifiedVertex(v);
			for(int i = 0 ; i < childSchedules.length; i++) {
				if(firstTime) {
					smallestTimeForThisVertex = childSchedules[i].getVertexFinishTime(v);
					firstTime = false;
				} else if(smallestTimeForThisVertex > childSchedules[i].getVertexFinishTime(v)) {
					smallestTimeForThisVertex = childSchedules[i].getVertexFinishTime(v);
				}
			}
			int thisVertexMinimalReadyTimeCost = smallestTimeForThisVertex + this.getBtmLvl(v);
			if(thisVertexMinimalReadyTimeCost > largestTime) {
				largestTime = thisVertexMinimalReadyTimeCost;
			}
		}
		return largestTime;
	}
}
