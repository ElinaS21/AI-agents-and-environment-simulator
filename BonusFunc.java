
public class BonusFunc {

	public static DijkstraResult dijkstra(int v_position, int v_peopleInCar, int destination, double arr[][]) {
		Node d[] = new Node[arr.length];
		int prevVertex[] = new int[arr.length];
		MinHeap minheap = new MinHeap(arr.length);
		Node node;
		
		for (int i = 0; i < arr.length; i++){
			if(i==v_position)
				node=new Node(i, 0);
			else 
				node=new Node(i, Double.POSITIVE_INFINITY);
			minheap.insert(node);
			d[i] = node;
			prevVertex[i]= -1;
		}
		
		// early stop when goal!=-1
		int goal = -1;

		while(!minheap.isEmpty()){
			node = minheap.extractMin();
			
			// early stop
			if(destination==-1) {
				if((v_peopleInCar==0 && Main.peopleToSave[node.index]>0)||
						(v_peopleInCar!=0 && Main.shelters[node.index])) {
					goal = node.index;
					break;
				}
			}
			else {
				if(node.index==destination) {
					goal = node.index;
					break;
				}
			}
			
			//update neighbors
			for (int i = 0; i < d.length; i++) {
				if (i < node.index) {
					double vertexWeight = DijkstraAlgorithm.cTime(arr[i][node.index], v_peopleInCar, Main.k_parameter);
					if(vertexWeight > 0 && d[i].value > (node.value+vertexWeight)) { //RELAX
						minheap.decreaseKey(d[i].indexInHeap, node.value+vertexWeight);
						prevVertex[i]=node.index;
					}
				}
				else if(i > node.index) {
					double vertexWeight = DijkstraAlgorithm.cTime(arr[node.index][i], v_peopleInCar, Main.k_parameter);
					if(vertexWeight > 0 && d[i].value > (node.value+vertexWeight)) { //RELAX
						minheap.decreaseKey(d[i].indexInHeap, node.value+vertexWeight);
						prevVertex[i]=node.index;
					}
				}
			}
		}
		
		double[] res = new double[arr.length];
		for (int i = 0; i < d.length; i++) 
			res[i] = d[i].value;
		return new DijkstraResult(res, prevVertex, goal);	
	}
	
	
	public static double calculateF(int agentPosition,int[] people, int numOfPeopleInCar,
			double time, double personWeight, boolean type, double arr[][]) 
	{
		double result;
		result=calculateHeuristic(agentPosition,people, numOfPeopleInCar,time, personWeight, arr);
		if(!type)return result;
		return result+time;
	}
	
	private static double calculateHeuristic(int agentPosition,int[] people, 
			int numOfPeopleInCar,double time, double personWeight,double arr[][]) 
	{
		double result = 0;
		for (int i = 0; i < people.length; i++) {
			if(people[i]>0) {
				double timeToEvacuate=calculateTime(agentPosition ,i, people, numOfPeopleInCar, arr);
				if(timeToEvacuate+time>Main.deadline) 
					result+=people[i]*personWeight;
			}
		}
		if(numOfPeopleInCar>0) {
			DijkstraResult rs = dijkstra(agentPosition, numOfPeopleInCar, -1, arr);
			if(rs.distance[rs.goal]+time>Main.deadline)
				result+=numOfPeopleInCar*personWeight;
		}
		return result;
	}
	
	private static double calculateTime(int agentPosition ,int vertex, int[] people, int numOfPeopleInCar, double arr[][]){
		double result = 0;
		DijkstraResult rs = dijkstra(agentPosition, numOfPeopleInCar, vertex, arr);
		result += rs.distance[vertex];
		rs = dijkstra(vertex, (numOfPeopleInCar+people[vertex]), -1, arr);
		result += rs.distance[rs.goal];
		return result;
	}
	
}
