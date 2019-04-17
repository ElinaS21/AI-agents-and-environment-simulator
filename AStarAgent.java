public class AStarAgent extends AI_Agent{

	public AStarAgent(int agentTypes, int positions, int peopleInCar) {
		super(agentTypes, positions, peopleInCar);
	}

	@Override
	public double timeForNextAction() {
		
		nextStep = calculateNextStep(position, true, Integer.MAX_VALUE);
		
		if (nextStep==-1 || nextStep==position) {
			return 1;//no-op
		}
		else {
			double w = Double.POSITIVE_INFINITY;
			if(position<nextStep && Main.vertexMatrix[position][nextStep]>0) 
				w = DijkstraAlgorithm.cTime(Main.vertexMatrix[position][nextStep], peopleInCar, Main.k_parameter);
			else if(position>nextStep && Main.vertexMatrix[nextStep][position]>0) 
				w = DijkstraAlgorithm.cTime(Main.vertexMatrix[nextStep][position], peopleInCar, Main.k_parameter);			
			timeForAction= w;
		}
		return timeForAction;
	}

	@Override
	public int doAction() {
		if (nextStep==-1) { //no-op
			return position;
		}
		
		// take people to car if needed
		peopleInCar += Main.peopleToSave[nextStep];
		Main.peopleToSave[nextStep]=0;
				
		// put people in shelter if needed
		if (Main.shelters[nextStep]){
			score += peopleInCar;
			peopleInCar = 0;
		}
				
		//update position
		position = nextStep;
		return position;
		
	}
	

}
