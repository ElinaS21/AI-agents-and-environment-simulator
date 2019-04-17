import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileParser {
	
	
	public int numOfVertices;
	public double vertexMatrix[][];
	public double deadline;
	public int people[];
	public boolean isShelter[];
	
	
	public void parse (String fileName){
		File file = new File("file.txt");
		try(BufferedReader br = new BufferedReader(new FileReader(file)); )
		{
			String line;
			while ((line = br.readLine()) != null){
				if(line.charAt(0)=='#')
				{
					switch(line.charAt(1)){
						case 'T':
							parseNumOfVertex(line.substring(3));
							break;
						case 'E': 
							parseEdge(line.substring(3)); 
							break;
						case 'V': 
							parseVertex(line.substring(3));
							break;
						case 'D': 
							parseDeadline(line.substring(3)); 
							break;
					}
				}
			}
		}
		catch(IOException e){
			System.out.println("Unable to open file");
			e.printStackTrace();
		}
		finally{
			System.out.println("***reading of file is completed***");
		}
	}
	
	
	private void parseNumOfVertex(String input){  //#T 4
		String[] array = input.split(" "); 
		String s = array[0];
		numOfVertices = Integer.parseInt(s);
		vertexMatrix = new double[numOfVertices][numOfVertices];
		people = new int[numOfVertices];
		isShelter = new boolean[numOfVertices];
	}
	
	
	private void parseEdge(String input){ //#E 1 2 W1
		String[] array = input.split(" "); 
		int firstVertexIndex = Integer.parseInt(array[0]);
		int secondVertexIndex = Integer.parseInt(array[1]);
		float vertexWeight = 0;
		
		if(array[2].charAt(0) != 'W' || array[2].length() < 2)
			System.out.println("bad file syntax: " + array[2]);
		else 
			vertexWeight = Float.parseFloat(array[2].substring(1));
		
		if(firstVertexIndex< secondVertexIndex)
			vertexMatrix[firstVertexIndex-1][secondVertexIndex-1] = vertexWeight;
		else
			vertexMatrix[secondVertexIndex-1][firstVertexIndex-1] = vertexWeight;
	}	
	
	private void parseVertex(String input){ // #V 2 P 1   #V 1 S 
		String[] array = input.split(" "); 
		int vertexIndex = Integer.parseInt(array[0]);
		if(array[1].charAt(0)=='P')
			people[vertexIndex-1]= Integer.parseInt(array[2]) ;
		
		else if(array[1].charAt(0)=='S')
			isShelter[vertexIndex-1]=true;
		
		else System.out.println("bad file syntax: " + array[1]);
	}
	
	
	private void parseDeadline(String input){ //#D 10 
		String[] array = input.split(" "); 
		String s = array[0];
		deadline = Integer.parseInt(s);
	}
}
