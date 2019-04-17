

public class VandalAgent extends Agent {

    public VandalAgent(int agentType, int position, int peopleInCar) {
        super(agentType, position, peopleInCar);
    }
    
	@Override
	public double timeForNextAction() {
		// return the time needed to the action
		if (numberOfAdjacentV()<2) {
			return 1;//no-op
		}
		else {
			return ((2*Main.shelters.length)+1+secondMinimalLength()); //V+1
		}
	}

	@Override
	public int doAction() {
		if(timeForNextAction()==1)
			return position;
		// block the lowest-cost edge
		int indexToBlock = indexOfMinimalLength();
		if(indexToBlock>-1) {
			if (indexToBlock < position)
				Main.vertexMatrix[indexToBlock][position]=0;
			else
				Main.vertexMatrix[position][indexToBlock]=0;
			position = indexOfMinimalLength();
		}
		return position;
	}   
	
	public int numberOfAdjacentV() {
		int ans = 0;
		for(int i=0; i<Main.vertexMatrix.length; i++) {
			if (i<position) {
				if (Main.vertexMatrix[i][position]>0)
					ans++;
			}
			else if (position<i) {
				if (Main.vertexMatrix[position][i]>0)
					ans++;
			}
		}
		return ans;
	}
	
	public double minimalLength() {
		double ans = Double.POSITIVE_INFINITY;
		for(int i=0; i<Main.vertexMatrix.length; i++) {
			if (i<position) {
				if (Main.vertexMatrix[i][position]>0)
					ans = Math.min(Main.vertexMatrix[i][position],ans);
			}
			else if (position<i) {
				if (Main.vertexMatrix[position][i]>0)
					ans = Math.min(Main.vertexMatrix[position][i],ans);
			}
		}
		return ans;
	}
	
	public double secondMinimalLength() {
		int min = indexOfMinimalLength();
		double ans = Double.POSITIVE_INFINITY;
		for(int i=0; i<Main.vertexMatrix.length; i++) {
			if (i<position && i!=min) {
				if (Main.vertexMatrix[i][position]>0)
					ans = Math.min(Main.vertexMatrix[i][position],ans);
			}
			else if (position<i && i!=min) {
				if (Main.vertexMatrix[position][i]>0)
					ans = Math.min(Main.vertexMatrix[position][i],ans);
			}
		}
		return ans;
	}
	
	public int indexOfMinimalLength() {
		double len = Double.POSITIVE_INFINITY;
		int ans = -1;
		for(int i=0; i<Main.vertexMatrix.length; i++) {
			if (i<position) {
				if (Main.vertexMatrix[i][position]>0)
					if(Main.vertexMatrix[i][position]<len) {
						ans=i;
						len=Main.vertexMatrix[i][position];
					}
			}
			else if (position<i) {
				if (Main.vertexMatrix[position][i]>0)
					if(Main.vertexMatrix[position][i]<len) {
						ans=i;
						len=Main.vertexMatrix[position][i];
					}
			}
		}
		return ans;
	}
	
}