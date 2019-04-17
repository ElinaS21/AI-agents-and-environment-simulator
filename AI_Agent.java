import java.util.ArrayList;

public abstract class AI_Agent extends Agent{
	
	double timeForAction;
	int nextStep;
	int numOfExpands;//for this round
	
	
	ArrayList<State> st = new ArrayList<State>();
	MinHeap mh = new MinHeap(Main.vertexMatrix.length);
	

	public AI_Agent(int agentType, int position, int peopleInCar) {
		super(agentType, position, peopleInCar);
	}
	
	public int calculateNextStep(int position, boolean type, int maxExpands){
		
		st.clear();
		mh.clear();
		numOfExpands=0;
		
		int goal_state;
		State temp_s;		
		Node temp_n;
		
		//add initial state
		double time = Main.time;
		int[] p_arr = Main.peopleToSave.clone();
		double f_func= HeuristicCalculator.calculateF(position,p_arr, peopleInCar,time, Main.mostHeavyEdge, type);
		
		////Bonus 
		if(!Main.isBonus)
			temp_s = new State(position, p_arr, peopleInCar, time,f_func, -1);
		else if (Main.agents[0].agentType==3) 
			temp_s = new BonusState(position, p_arr, peopleInCar, time,f_func, -1, copy(Main.vertexMatrix), Main.agents[0].position);	
		else
			temp_s = new BonusState(position, p_arr, peopleInCar, time,f_func, -1, copy(Main.vertexMatrix), Main.agents[1].position);	
		
		st.add(temp_s);
		temp_n = new Node(st.size()-1, f_func);
		mh.insert(temp_n);
		
		while(true) {
			
			//check
			if(mh.isEmpty())
				return -1;
			
			temp_n = mh.extractMin();
			time = st.get(temp_n.index).time;
			
			//check for finish
			if(time>=Main.deadline || numOfExpands>=maxExpands) {
				goal_state = temp_n.index; //index of state in "st" arr
				break;
			}
			
			if(Main.isBonus) {
				//calculate road matrix after vandal action 
				if (Main.agents[0].agentType==3) {
					VandalState vs = vandalStep(((BonusState)st.get(temp_n.index)).copyArr , Main.agents[0].position,time);
					((BonusState)st.get(temp_n.index)).copyArr = vs.arr;
					((BonusState)st.get(temp_n.index)).vandalVertex = vs.v2;
					((BonusState)st.get(temp_n.index)).time = vs.timeForAction;
				}
				else {
					VandalState vs = vandalStep(((BonusState)st.get(temp_n.index)).copyArr , Main.agents[1].position,time);
					((BonusState)st.get(temp_n.index)).copyArr = vs.arr;
					((BonusState)st.get(temp_n.index)).vandalVertex = vs.v2;
					((BonusState)st.get(temp_n.index)).time = vs.timeForAction;
				}
			}
			expand(temp_n, st.get(temp_n.index), type);
		}
		
		int result =  st.get(goal_state).vertex;
		int prev=st.get(goal_state).prev;
		
		if(prev==-1) {
			System.out.println("no-op");
			return -1;
		}
		
		//calculate first step in path
		while(st.get(prev).prev!=-1) {
			result= st.get(prev).vertex;
			prev = st.get(prev).prev;
			
		}
		return result;
	}
	
	public double[][] copy(double original[][]){
		
		double res [][] = new double[original.length][original.length];
	    for(int i = 0; i< original.length; i++){
	        for (int j = 0; j < original[i].length; j++){
	        	res[i][j] = original[i][j];
	        }
	    }
	    return res;
	}
	
	public void expand(Node n, State s, boolean type) {
		
		//expand for all neighbors
		int numOfNeighbours=0;
		State temp_s;		
		Node temp_n;
		double f_func;
		
		numOfExpands++;
		totalNumOfExpands++;
		int vertex = s.vertex;
		
		if(Main.shelters[vertex])
			s.peopleInCar=0;
		
		//check if road is close	
		for (int i = 0; i < Main.vertexMatrix.length; i++) {
			double edgeWeight = 0;
			
			if(!Main.isBonus) {
				if(vertex<i && Main.vertexMatrix[vertex][i]>0) {
					edgeWeight = DijkstraAlgorithm.cTime(Main.vertexMatrix[vertex][i], s.peopleInCar, Main.k_parameter);
					numOfNeighbours++;
				}
				else if(vertex>i && Main.vertexMatrix[i][vertex]>0) {
					edgeWeight = DijkstraAlgorithm.cTime(Main.vertexMatrix[i][vertex], s.peopleInCar, Main.k_parameter);
					numOfNeighbours++;
				}
				else 
					continue;
			}
			else {
				if(vertex<i && ((BonusState)s).copyArr[vertex][i]>0) {
					edgeWeight = DijkstraAlgorithm.cTime(((BonusState)s).copyArr[vertex][i], s.peopleInCar, Main.k_parameter);
					numOfNeighbours++;
				}
				else if(vertex>i && ((BonusState)s).copyArr[i][vertex]>0) {
					edgeWeight = DijkstraAlgorithm.cTime(((BonusState)s).copyArr[i][vertex], s.peopleInCar, Main.k_parameter);
					numOfNeighbours++;
				}
				else 
					continue;
			}
			
			int[] peopleArr = s.peopleArr.clone();
			int sumOfPeople = s.peopleInCar;
			
			if(peopleArr[i]>0) {
				sumOfPeople+=peopleArr[i];
				peopleArr[i]=0;
			}
			
			//bonus
			if(!Main.isBonus) {
				f_func = HeuristicCalculator.calculateF(i, peopleArr, sumOfPeople, s.time+edgeWeight, Main.mostHeavyEdge, type);
				temp_s = new State(i, peopleArr, sumOfPeople, s.time+edgeWeight, f_func, n.index);
			}
			else {
				f_func = BonusFunc.calculateF(i, peopleArr, sumOfPeople, s.time+edgeWeight, Main.mostHeavyEdge, type, ((BonusState)s).copyArr);
				temp_s = new BonusState(i, peopleArr, sumOfPeople, s.time+edgeWeight, f_func, n.index, ((BonusState)s).copyArr.clone(), ((BonusState)s).vandalVertex);
			}
			st.add(temp_s);
			temp_n = new Node(st.size()-1, f_func);
			mh.insert(temp_n);
			
		}
		
		if(numOfNeighbours==0) {
			if(!Main.isBonus) {
				f_func = HeuristicCalculator.calculateF(vertex, s.peopleArr, s.peopleInCar, s.time+1, Main.mostHeavyEdge, type);
				temp_s = new State(vertex, s.peopleArr, s.peopleInCar, s.time+1, f_func, vertex);
				
			}
			else {
				f_func = BonusFunc.calculateF(vertex, s.peopleArr, s.peopleInCar, s.time+1, Main.mostHeavyEdge, type, ((BonusState)s).copyArr);
				temp_s = new BonusState(vertex, s.peopleArr, s.peopleInCar, s.time+1, f_func, vertex, ((BonusState)s).copyArr.clone(), ((BonusState)s).vandalVertex);
			}
			
			st.add(temp_s);
			temp_n = new Node(st.size()-1, f_func);
			mh.insert(temp_n);
			
			
		}
	}
	
	
	private VandalState vandalStep(double[][] copyArr, int position, double timeForAction) {
		VandalState st = new VandalState(copyArr, position, timeForAction);
		
		int numOfAdjacent = 0;
		for(int j=0; j<copyArr.length; j++) {
			if (j<position) {
				if (copyArr[j][position]>0)
					numOfAdjacent++;
			}
			else if (position<j) {
				if (copyArr[position][j]>0)
					numOfAdjacent++;
			}
		}
		if (numOfAdjacent<2) //no-op
			return st;
		
		double minLen = Double.POSITIVE_INFINITY;
		int minInd = -1;
		for(int i=0; i<copyArr.length; i++) {
			if (i<position) {
				if (copyArr[i][position]>0)
					if(copyArr[i][position]<minLen) {
						minInd=i;
						minLen=copyArr[i][position];
					}	
			}
			else if (position<i) {
				if (copyArr[position][i]>0)
					if(copyArr[position][i]<minLen) {
						minInd=i;
						minLen=copyArr[position][i];
					}
			}
		}
		
		//update array
		if(minInd<position)
			st.arr[minInd][position]=0;
		else if(minInd>position)
			st.arr[position][minInd]=0;
		
		//update position
		minLen = Double.POSITIVE_INFINITY;
		minInd = -1;
		for(int i=0; i<copyArr.length; i++) {
			if (i<position) {
				if (copyArr[i][position]>0)
					if(copyArr[i][position]<minLen) {
						minInd=i;
						minLen=copyArr[i][position];
					}	
			}
			else if (position<i) {
				if (copyArr[position][i]>0)
					if(copyArr[position][i]<minLen) {
						minInd=i;
						minLen=copyArr[position][i];
					}
			}
		}
		
		int prevV = st.v2; 
		st.v2=minInd;
		st.timeForAction+=2*copyArr.length+1+copyArr[prevV][st.v2]+copyArr[st.v2][prevV]; 
		
		return st;
	}
	
}

class State {
	int vertex;
	int[] peopleArr;
	int peopleInCar;
	double time;
	double heuristic;
	int prev;
	
	public State(int vertex, int[] peopleArr, int peopleInCar, double time, double heuristic, int prev) {
		super();
		this.vertex = vertex;
		this.peopleArr = peopleArr;
		this.peopleInCar = peopleInCar;
		this.time = time;
		this.heuristic = heuristic;
		this.prev = prev;	
	}
}

class BonusState extends State {
	
	//Bonus
	double copyArr[][];
	int vandalVertex;
	
	public BonusState(int vertex, int[] peopleArr, int peopleInCar, double time, double heuristic, int prev, double arr [][], int vandalVertex) {
		super(vertex, peopleArr, peopleInCar, time, heuristic, prev);
		this.copyArr=arr;
		this.vandalVertex = vandalVertex;
	}	
}


//Bonus
class VandalState {
	double[][] arr; //matrix of roads
	int v2;      //new position of vandal
	double timeForAction;
	public VandalState(double[][] arr, int v2, double timeForAction) {
		super();
		this.arr = arr;
		this.v2 = v2;
		this.timeForAction = timeForAction;
	}		
}
