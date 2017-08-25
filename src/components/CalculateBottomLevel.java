package components;

import java.util.List;

import graph.Vertex;
/**
 * Utility class that helps other classes calculate the bottom level cost of a vertex
 * 
 * @author Alex Yoo
 *
 */
public final class CalculateBottomLevel {
	private CalculateBottomLevel() {
	}
	/**
	 * Calculates the bottom level from this vertex
	 * @param vertex
	 * @return
	 */
	public static int getBtmLvl(Vertex vertex) {
		int largestBtmLvlWeight = 0;
		largestBtmLvlWeight = getLargestWeightPathToLeaf(vertex.getChildren(), vertex.getWeight());
		return largestBtmLvlWeight;
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
	private static int getLargestWeightPathToLeaf(List<Vertex> childVertices, int weightSoFar) {
		int largestVertexWeight = weightSoFar;
		for(Vertex v : childVertices) {
			int currentWeight = weightSoFar+v.getWeight();
			if(!v.getChildren().isEmpty()) {
				currentWeight = getLargestWeightPathToLeaf(v.getChildren(), currentWeight);
			}
			if(currentWeight > largestVertexWeight) {
				largestVertexWeight = weightSoFar+v.getWeight();
			}
		}
		return largestVertexWeight;
	}
}
