package heuristics;

import java.util.List;

import components.CalculateBottomLevel;
import graph.Vertex;
import scheduler.Schedule;
/**
 * 
 * Calculates Heuristics cost function
 * 
 * Cost Function = Cost of Schedule + Bottom Level of any node
 * 
 * @author Alex Yoo
 *
 */
public class MaxStartTimeAndBottomLevel implements ICostFunction{
	
	@Override
	public int getCostFunction(Schedule currentSchedule) {
		return this.maxStartTimeAndBtmLvlNode(currentSchedule);
	}
	
	private int maxStartTimeAndBtmLvlNode(Schedule currentSchedule) {
		int costFunction = 0;
		for(Vertex usedVertex: currentSchedule.getAllUsedVertices()){
			int tempCost = currentSchedule.getVertexStartTime(usedVertex) + CalculateBottomLevel.getBtmLvl(usedVertex);
			if(tempCost > costFunction){
				costFunction = tempCost;
			}
		}
		return costFunction;
	}
}
