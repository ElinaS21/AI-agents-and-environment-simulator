
public class DijkstraResult {
	double distance[];
	int prev[];
	int goal = -1; // early stop
	
	public DijkstraResult(double[] distance, int[] prev, int goal) {
		super();
		this.distance = distance;
		this.prev = prev;
		this.goal = goal;
	}
	
}
