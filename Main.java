
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner; 


public class Main {
		
	//globals
	static Scanner scan = new Scanner(System.in);
	static int numOfAgents;
	static Agent[] agents;
	static double k_parameter;
	static double[][] vertexMatrix;
	static double deadline;
	static boolean[] shelters;
	static int[] peopleToSave;
	static double time = 0;
	static double mostHeavyEdge;
	static int f_parameter;
	
	//Bonus
	static boolean isBonus = false;
	
	public static void main(String[] args) {
		FileParser parser = new FileParser();
		parser.parse("file.txt");
		
		vertexMatrix = parser.vertexMatrix;
		deadline = parser.deadline;
		shelters = parser.isShelter;
		peopleToSave = parser.people;
		
		getInputsFromUser();
		checkForPeopleInShelters();
		checkForAgentsWithPeopleInCar();
		getHeaviestEdge();

		File outfile = new File("results.txt");
		
		//Bonus 
		if(numOfAgents == 2)
			if((agents[0].agentType == 3 && agents[1].agentType>3) || (agents[1].agentType == 3 && agents[0].agentType>3))
				isBonus=true;
		
		try {
			outfile.createNewFile();
			start(outfile);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	

	private static void getInputsFromUser(){
		//Scanner scan = new Scanner(System.in);
		System.out.println("Please enter number of agents");
		int ans = scan.nextInt();
		
		while (ans<=0) { // validate numOfAgents
			System.out.println("number of agents should be positive integer. Try again");
			ans = scan.nextInt();
		}
		numOfAgents = ans;
		agents = new Agent[numOfAgents];
		for (int i=0;i<numOfAgents;i++){
			System.out.println("Please enter the type of agent "+(i+1));
			System.out.println("For HUMAN agent press 1");
			System.out.println("For GREEDY agent press 2");
			System.out.println("For VANDAL agent press 3");
			System.out.println("For GREEDY-SEARCH agent press 4");
			System.out.println("For A-STAR agent press 5");
			System.out.println("For REAL-TIME-A-STAR agent press 6");
			
			ans = scan.nextInt();
			while(ans<1 | ans>6) {//validate type
				System.out.println("type of agent should be 1-6. Try again");
				ans = scan.nextInt();
			}
			int type=ans;
			System.out.println("Please enter the initial position of agent "+(i+1));
			ans = scan.nextInt()-1;
			while(ans>=vertexMatrix.length | ans<0) {//validate position
				System.out.println("position of agent should be positive integer smaller than number of vertices. Try again");
				ans = scan.nextInt()-1;
			}
			int pos = ans;
			
			switch(type) {
				case 1: 
					agents[i] = new HumanAgent(type,pos,0);
					break;
				case 2:
					agents[i] = new GreedyAgent(type,pos,0);
					break;
				case 3:
					agents[i] = new VandalAgent(type,pos,0);
					break;
				case 4:
					agents[i] = new GreedySearchAgent(type,pos,0);
					break;
				case 5:
					agents[i] = new AStarAgent(type,pos,0);
					break;
				case 6:
					agents[i] = new RealtimeAStarAgent(type,pos,0);
					break;
			}	
		}
		
		System.out.println("Please enter the parameter k");		
		double ans2 = scan.nextDouble();
		while(ans2<0|ans2>1) {//validate k
			System.out.println("k should be double between 0 and 1. Try again");
			ans2 = scan.nextDouble();
		}
		k_parameter = ans2;
		System.out.println("Please enter the parameter f");		
		int ans3 = scan.nextInt();
		f_parameter = ans3;
		return;
	}
	
	
	private static void start(File outfile) throws IOException{
		FileWriter writer = new FileWriter(outfile);
		boolean valid = true;
		while (valid){	
			for(int i=0; i<numOfAgents; i++){
				writer.write("time is " + time + "/" + deadline +"\n");
				System.out.println("time: " + time + "/" + deadline);
				
				if(time<deadline) {
					System.out.println("The state of the world is:");
					Main.printWorldState(Main.deadline-Main.time);
					double timeForAction = agents[i].timeForNextAction();
					
					if (time+timeForAction<=deadline) {
						int vertexToMoveTo = agents[i].doAction();
						agents[i].totalAgentRunningTime+=timeForAction;
						agents[i].numberOfActionsDone++;
						System.out.println("The new state of the world is:");
						printWorldState(deadline-time-timeForAction);
						System.out.println("");
						
						if (agents[i].agentType!=3) {
							writer.write("agent " + (i+1) + " moved to vertex " + (vertexToMoveTo+1) + "\n");
							writer.write("agent " + (i+1) + " has " + agents[i].peopleInCar + " people in car\n");
							writer.write("agent " + (i+1) + " do " + agents[i].numberOfActionsDone);
							writer.write(" actions in total time of "+agents[i].totalAgentRunningTime + "\n");
							writer.write("agents " + (i+1) + " score is " + agents[i].score + "\n\n");
						}
						else {
							writer.write("vandal " + (i+1) + " moved to vertex " + (vertexToMoveTo+1) + "\n\n");
						}
					}
					time += timeForAction;  //update time
					if (time>deadline){
						valid = false;
						int vertexToMoveTo = agents[i].doAction();
						writer.write("the last action doesn't have enough time :( \n");
						writer.write("agent " + (i+1) + " tried to go to vertex " + (vertexToMoveTo+1));
						writer.write(" but this action takes "+ timeForAction +" time units\n");
						System.out.println("__DEADLINE__");
						break;
					}
				}
				else {
					valid = false;
					System.out.println("__DEADLINE__");
				}	
			}
		}
		for(int i=0; i<numOfAgents; i++){
			if(agents[i].agentType>3)
				writer.write("agents " + (i+1) + " performance is " + ((agents[i].score)*f_parameter+agents[i].totalNumOfExpands) +"\n");
		}
		writer.close();
	}
	
	public static void printWorldState(double timeleft){
        // vertexMatrix
        for(int i=0;i<vertexMatrix.length;i++){
            for(int j=0;j<vertexMatrix.length;j++)
                System.out.print(vertexMatrix[i][j]+ " ");
            System.out.println();
        } 
        // agent types + agent positions
        for(int i=0;i<numOfAgents;i++){
            System.out.print("agent " +(i+1)+" is from type " + agents[i].agentType + " and ");
            System.out.println("in position "+ (agents[i].position+1));
            System.out.print("agent " +(i+1)+ " do " + agents[i].numberOfActionsDone);
            System.out.println(" actions in total time of "+agents[i].totalAgentRunningTime);
        }
        // deadline
        System.out.println("deadline will be in " + timeleft + " time units");
        // shelters
        System.out.print("shelters: ");
        for(int i=0;i<shelters.length;i++)
            System.out.print(shelters[i]+ " ");
        System.out.println();
        //people to save
        System.out.print("people to evacuate: ");
        for(int i=0;i<peopleToSave.length;i++)
            System.out.print(peopleToSave[i]+ " ");
        System.out.println();
        //people in car
        System.out.print("people in car: ");
        for(int i=0;i<numOfAgents;i++)
            System.out.print(agents[i].peopleInCar + " ");
        System.out.println();
    }

	public static void checkForPeopleInShelters() {
		//if there are people in vertices with shelters - delete them
		for(int i=0; i<vertexMatrix.length; i++){
			if (shelters[i])
				peopleToSave[i]=0;		
		}
	}
	
	public static void checkForAgentsWithPeopleInCar() {
		//if there are agents in vertices with people - add to people in car
		for(int i=0; i<numOfAgents; i++) {
			agents[i].peopleInCar += peopleToSave[agents[i].position];
			peopleToSave[agents[i].position]=0;
		}
	}
	
	public static void getHeaviestEdge() {
		//mostHeavyEdge=100;
		//return;
		
		double heavy=0;
		for(int i=0;i<vertexMatrix.length;i++)
			for(int j=i+1;j<vertexMatrix.length;j++)
				heavy=Math.max(heavy, vertexMatrix[i][j]);
		mostHeavyEdge=heavy*deadline;
		
	}
	

}