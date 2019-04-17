
public class HeuristicCalculator {

	public static double calculateF(int agentPosition,int[] people, int numOfPeopleInCar,double time, double personWeight, boolean type) {
		double result;
		result=calculateHeuristic(agentPosition,people, numOfPeopleInCar,time, personWeight);
		if(!type)return result;
		return result+time;
	}
	
	private static double calculateHeuristic(int agentPosition,int[] people, int numOfPeopleInCar,double time, double personWeight) {
		double result = 0;
		for (int i = 0; i < people.length; i++) {
			if(people[i]>0) {
				double timeToEvacuate=calculateTime(agentPosition ,i, people, numOfPeopleInCar);
				if(timeToEvacuate+time>Main.deadline) 
					result+=people[i]*personWeight;
			}
		}
		if(numOfPeopleInCar>0) {
			DijkstraResult rs = DijkstraAlgorithm.dijkstra(agentPosition, numOfPeopleInCar, -1);
			if(rs.distance[rs.goal]+time>Main.deadline)
				result+=numOfPeopleInCar*personWeight;
		}
		return result;
	}
	
	private static double calculateTime(int agentPosition ,int vertex, int[] people, int numOfPeopleInCar){
		double result = 0;
		DijkstraResult rs = DijkstraAlgorithm.dijkstra(agentPosition, numOfPeopleInCar, vertex);
		result += rs.distance[vertex];
		rs = DijkstraAlgorithm.dijkstra(vertex, (numOfPeopleInCar+people[vertex]), -1);
		result += rs.distance[rs.goal];
		return result;
	}
	

}