
public class DijkstraAlgorithm {
	
	//v_peopleInCar==0 && destination==-1  ->  look for closer node with people
	//v_peopleInCar==0 && destination>=0  ->    look for closer path to destination node
	public static DijkstraResult dijkstra(int v_position, int v_peopleInCar, int destination) {
		Node d[] = new Node[Main.vertexMatrix.length];
		int prevVertex[] = new int[Main.vertexMatrix.length];
		MinHeap minheap = new MinHeap(Main.vertexMatrix.length);
		Node node;
		
		for (int i = 0; i < Main.vertexMatrix.length; i++){
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
					double vertexWeight = cTime(Main.vertexMatrix[i][node.index], v_peopleInCar, Main.k_parameter);
					if(vertexWeight > 0 && d[i].value > (node.value+vertexWeight)) { //RELAX
						minheap.decreaseKey(d[i].indexInHeap, node.value+vertexWeight);
						prevVertex[i]=node.index;
					}
				}
				else if(i > node.index) {
					double vertexWeight = cTime(Main.vertexMatrix[node.index][i], v_peopleInCar, Main.k_parameter);
					if(vertexWeight > 0 && d[i].value > (node.value+vertexWeight)) { //RELAX
						minheap.decreaseKey(d[i].indexInHeap, node.value+vertexWeight);
						prevVertex[i]=node.index;
					}
				}
			}
		}
		
		double[] res = new double[Main.vertexMatrix.length];
		for (int i = 0; i < d.length; i++) 
			res[i] = d[i].value;
		return new DijkstraResult(res, prevVertex, goal);	
	}
	
	
	public static double cTime(double w, int p, double k) {
		return w*(p*k+1);
	}
	

}
