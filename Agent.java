public abstract class Agent {

    
	public int agentType;
    public int position;
    public int peopleInCar;
    public int score;
    public int numberOfActionsDone;
    public double totalAgentRunningTime;
	int totalNumOfExpands; //for all game
   
    public Agent(int agentType, int positions, int peopleInCar) {
        super();
        this.agentType = agentType;
        this.position = positions;
        this.peopleInCar = peopleInCar;
    }

    public abstract double timeForNextAction();

	public abstract int doAction();//return the num of the vertex we moved to
    

}








