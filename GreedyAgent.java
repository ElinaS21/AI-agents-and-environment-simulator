import java.util.LinkedList;


public class GreedyAgent extends Agent {

	
	LinkedList<Integer> path = new LinkedList<Integer>();
	
	
	public GreedyAgent(int type, int position, int peopleInCar) {
		super(type, position, peopleInCar);
	}

	@Override
	public double timeForNextAction() {
		double timeForAction = Double.POSITIVE_INFINITY;
		
		if(!isStillValid()) {
			path.clear();
			DijkstraResult distances = DijkstraAlgorithm.dijkstra(position, peopleInCar, -1);
			calculateNewPath(distances);
		}
		if(path.isEmpty())
			return 1; //no-op
		
		if (position < path.getFirst())
			timeForAction=Main.vertexMatrix[position][path.getFirst()];
		else
			timeForAction=Main.vertexMatrix[path.getFirst()][position];
		
		timeForAction=timeForAction*(1 + Main.k_parameter*peopleInCar);
		System.out.println("time for action is: " + timeForAction);
		
		return timeForAction;
	}
	
	
	
	
	@Override
	public int doAction() {
		
		if(path.isEmpty()) 
			return position; //no-op
		
		int decision = path.removeFirst();
		peopleInCar += Main.peopleToSave[decision];
		Main.peopleToSave[decision]=0;
		
		// put people in shelter if needed
		if (Main.shelters[decision]){
			score += peopleInCar;
			peopleInCar = 0;
		}
		position = decision;
		return position;	
	}
	
	private void calculateNewPath(DijkstraResult distances) {
		int minIndex=distances.goal;
		
		if(minIndex==-1) //no goal
			return;
		
		while(distances.prev[minIndex]!=-1) {
			path.addFirst(minIndex);
			minIndex=distances.prev[minIndex];
		}
		
	}
	
	private boolean isStillValid() {
		if(path.isEmpty())
			return false;
		if(peopleInCar==0 && Main.peopleToSave[path.peekLast()]==0)
			return false;
		for (int i = 0; i < path.size()-1; i++) {
			if(path.get(i) < path.get(i+1) && Main.vertexMatrix[path.get(i)][path.get(i+1)]==0)
				return false;
			if(path.get(i) > path.get(i+1) && Main.vertexMatrix[path.get(i+1)][path.get(i)]==0)
				return false;
		}
		return true;
	}
	

}


