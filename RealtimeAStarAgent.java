import java.util.Scanner; 


public class RealtimeAStarAgent extends AI_Agent{

	int maxNumOfExpands;
	boolean askUser = true;
	
	
	public RealtimeAStarAgent(int agentType, int position, int peopleInCar) {
		super(agentType, position, peopleInCar);
	}

	@Override
	public double timeForNextAction() {
		if(askUser) {
			Scanner sc = Main.scan;
			System.out.println("Please enter maximum number of expands");
			int ans = sc.nextInt();
			while(ans<0) {
				System.out.println("Maximum number of expands should be positive integer");
				ans = sc.nextInt();
			}
			maxNumOfExpands=ans;
			askUser= false;
		}

		nextStep = calculateNextStep(position, true, maxNumOfExpands);
		
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
