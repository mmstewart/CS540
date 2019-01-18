import java.io.File;
import java.io.FileNotFoundException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

/**
 * @author abhanshu 
 * This class is a template for implementation of 
 * HW1 for CS540 section 2
 */
/**
 * Data structure to store each node.
 */
class Location {
	private int x;
	private int y;
	private Location parent;

	public Location() {

	}
	
	public Location(int x, int y, Location parent) {
		this.x = x;
		this.y = y;
		this.parent = parent;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Location getParent() {
		return parent;
	}
	

	@Override
	public String toString() {
		return x + " " + y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Location) {
			Location loc = (Location) obj;
			return loc.x == x && loc.y == y;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * (hash + x);
		hash = 31 * (hash + y);
		return hash;
	}
}


public class Main {
	//represents the map/board
	private static boolean[][] board;
	//represents the goal node
	private static Location king;
	//represents the start node
	private static Location knight;
	//y dimension of board
	private static int n;
	//x dimension of the board
	private static int m;
	//enum defining different algo types
	private static boolean[][] explored;

	enum SearchAlgo{
		BFS, DFS, ASTAR;
	}

	public static void main(String[] args) {
		//if (args != null && args.length > 0) {
			//loads the input file and populates the data variables
		System.out.println("Enter file name: ");	
		Scanner scnr = new Scanner(System.in);
			String input = scnr.next();
			scnr.close();
			//SearchAlgo algo = loadFile(args[0]);
			SearchAlgo algo = loadFile(input);
			if (algo != null) {
				switch (algo) {
					case DFS :
						executeDFS();
						break;
					case BFS :
						executeBFS();
						break;
					case ASTAR :
						executeAStar();
						break;
					default :
						break;
				}
			}
		//}
	}

	/**
	 * Implementation of Astar algorithm for the problem
	 */
	private static void executeAStar() {
		PriorityQ<Location> frontier = new PriorityQ<Location>(); //a priority Q ordered by path-cost
		explored = new boolean[n+1][m+1]; //2D array of explored spaces
		frontier.add(knight, 3);
		int count = 0;//number of moves tried
		boolean goalFound = false;
		int exploredNodes = 0;//number of nodes explored, 1 because of knight
		
		while (goalFound == false){
			if (frontier.isEmpty() == true){
				System.out.println("NOT REACHABLE");
				System.out.println("Number of Expanded Nodes: " + exploredNodes);
				break;
			}
			
			SimpleEntry<Location, Integer> nextMove = frontier.poll();
			knight = nextMove.getKey();
			if (isGoal(knight) == true){ //if Goal
			    Stack<Location> stackReverse =new Stack<Location>(); 
			    while(knight.getParent() != null){ //reverse the stack with correct path
			        stackReverse.push(knight);
			        knight = knight.getParent();
			    }
			    stackReverse.push(knight); 
			    while(!stackReverse.isEmpty()){
			    	System.out.println(stackReverse.pop().toString());
			    }
				System.out.println("Number of Expanded Nodes: " + exploredNodes);
				goalFound = true;
			}
			explored[knight.getY()][knight.getX()] = true; //mark nextMove as explored
			exploredNodes++;
			while (count < 8){ //push valid moves to queue
				Location possibleMove = nextLocation(knight, count); //get nextMove based on count
				if (isValid(possibleMove) == true //if move is valid
						&& explored[possibleMove.getY()][possibleMove.getX()] == false //is not in explored
						&& frontier.exists(possibleMove) == false){  //and is not in frontier
					frontier.add(possibleMove, h(possibleMove)); //add move to frontier
					explored[nextMove.getKey().getY()][nextMove.getKey().getX()] = true; //mark nextMove as explored
				}
				else if (isValid(possibleMove) == true //if move is valid
						&& frontier.exists(possibleMove) == true
						&& (frontier.getPriorityScore(possibleMove) > h(possibleMove))){  //and is in frontier){
					frontier.remove(possibleMove);
					frontier.add(possibleMove, h(possibleMove));
				}
				else{
					count++;
					explored[nextMove.getKey().getY()][nextMove.getKey().getX()] = true;
				}			
			}
			count = 0; //reset count;
		}
	}

	private static int h(Location nextMove){
		int heuristic = 0; //Manhattan distance
		int value = 0;
		int g = 0;
		heuristic = Math.abs(nextMove.getX() - king.getX()) + Math.abs(nextMove.getY() - king.getY()); //Manhattan distance
		//|x – x_goal| + |y – y_goal|
		while(nextMove.getParent() != null){
			g++; //distance from source to currentNode, to be multiplied by cost of move
			nextMove = nextMove.getParent();
		}
		value = heuristic + (g*3); 
		return value;
	}

	/**
	 * Implementation of BFS algorithm
	 */
	private static void executeBFS() {
		explored = new boolean[n+1][m+1]; //2D array of explored spaces
        LinkedList<Location> pathQueue = new LinkedList<Location>(); //initialize new queue to hold path 
        int count = 0;//number of moves tried
		int invalids = 0; //number of invalid moves
		int exploredNodes = 0;//number of nodes explored, 1 because of knight
		boolean goalFound = false;
        
		explored[knight.getY()][knight.getX()] = true; //mark knight as explored
		pathQueue.add(knight);
        while (goalFound == false){
        	knight = pathQueue.removeFirst();
        	while (count < 8){ //push valid moves to queue
				Location nextMove = nextLocation(knight, count); //get nextMove based on count
				if (isValid(nextMove) == true && explored[nextMove.getY()][nextMove.getX()] == false){ //if move is valid
					if (isGoal(nextMove) == true){ //if Goal
						knight = nextMove;
					    Stack<Location> stackReverse =new Stack<Location>(); 
					    while(knight.getParent() != null){ //reverse the stack with correct path
					        stackReverse.push(knight);
					        knight = knight.getParent();
					    }
					    stackReverse.push(knight); 
					    while(!stackReverse.isEmpty()){
					    	System.out.println(stackReverse.pop().toString());
					    }
						System.out.println("Number of Expanded Nodes: " + exploredNodes);
						goalFound = true;
					}
					else{
						pathQueue.add(nextMove); //add move to queue
						explored[nextMove.getY()][nextMove.getX()] = true; //mark nextMove as explored
					}
					
				}
				else{ //move is invalid
					invalids++; //add one to invalids
				}
				count++;  //to do next move
			}
        	if (invalids > 7 && pathQueue.getFirst() == null){ //all moves are invalid and no valid next move
				System.out.println("NOT REACHABLE");
				System.out.println("Number of Expanded Nodes: " + exploredNodes);
				break;
			}
			invalids = 0; //reset invalids
			count = 0; //reset count
			exploredNodes++;
        }
	}
	
	/**
	 * Implementation of DFS algorithm
	 */
	private static void executeDFS() {
		explored = new boolean[n+1][m+1]; //2D array of explored spaces
		Stack<Location> pathStack = new Stack<Location>(); //initialize new stack to hold path 
		int count = 0;//number of moves tried
		int invalids = 0; //number of invalid moves
		int exploredNodes = 0;//number of nodes explored
		
		pathStack.push(knight); //push starting knight position to stack
		explored[knight.getY()][knight.getX()] = true; //mark knight as explored
		
		while (!pathStack.isEmpty()){
			exploredNodes++; //mark an explored node
			if (isGoal(knight) == true){ //if Goal
			    Stack<Location> stackReverse =new Stack<Location>(); 
			    while(knight.getParent() != null){ //reserse the stack with correct path
			        stackReverse.push(knight);
			        knight = knight.getParent();
			    }
			    stackReverse.push(knight); 
			    while(!stackReverse.isEmpty()){
			    	System.out.println(stackReverse.pop().toString());
			    }
				System.out.println("Number of Expanded Nodes: " + exploredNodes);
				break;
			}
			while (count < 8){ //push valid moves to stack
				Location nextMove = nextLocation(knight, count); //get nextMove based on count
				if (isValid(nextMove) == true && explored[nextMove.getY()][nextMove.getX()] == false){ //if move is valid
					pathStack.push(nextMove); //push move to stack
					explored[nextMove.getY()][nextMove.getX()] = true; //mark nextMove as explored
				}
				else{ //move is invalid
					invalids++; //add one to invalids
				}
				count++;  //to do next move
			}
			
			if (invalids > 7 && knight.getParent() == null){ //all moves are invalid and no valid parent
				System.out.println("NOT REACHABLE");
				System.out.println("Number of Expanded Nodes: " + exploredNodes);
				break;
			}
			invalids = 0; //reset invalids
			count = 0; //reset count
			knight = pathStack.pop();
		}
	}
	
	/**
	 * Returns the nextLocation based on whichever move is next to be performed
	 * @param currentLocal
	 * @param count
	 * @return
	 */
    public static Location nextLocation(Location currentLocal, int count) {
    	Location nextPosition = new Location();
    	int x = currentLocal.getX();
    	int y = currentLocal.getY();
    	
        switch (count++) {

            case 0:
                nextPosition = new Location(x + 2, y + 1, currentLocal);
                break;
            case 1:
                nextPosition = new Location(x + 1, y + 2, currentLocal);
                break;
            case 2:
                nextPosition = new Location(x - 1, y + 2, currentLocal);
                break;
            case 3:
                nextPosition = new Location(x - 2, y + 1, currentLocal);
                break;
            case 4:
                nextPosition = new Location(x - 2, y - 1, currentLocal);
                break;
            case 5:
                nextPosition = new Location(x - 1, y - 2, currentLocal);
                break;
            case 6:
                nextPosition = new Location(x + 1, y - 2, currentLocal);
                break;
            case 7:
                nextPosition = new Location(x + 2, y - 1, currentLocal);
                break;
        } // switch;                
        return nextPosition;
    } // method next
    
    
    /**
     * isValid: checks if the next possible location is a valid location. Checks if it is within the
     * bounds of the board and if the location is free of obstacles.
     * @param possibleLocation
     * @return true if valid, false if not
     */
	public static boolean isValid(Location possibleLocation){
		if ((possibleLocation.getX() < 0) || (possibleLocation.getX() >= m) //x coordinate is not valid
				|| (possibleLocation.getY() < 0) || (possibleLocation.getY() >= n)){ //y coordinate is not valid
			return false;
		}
		else if (board[possibleLocation.getY()][possibleLocation.getX()] == true){ //obstacle is at possibleLocation
			return false;
		}
		else{ //move is valid
			return true;
		}		
	}
	
	/**
	 * isGoal: checks if the last completed move is the goal. 
	 * @param currentMove
	 * @return true if goal, false if not;
	 */
	public static boolean isGoal(Location currentMove){
		if ((currentMove.equals(king))){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * 
	 * @param filename
	 * @return Algo type
	 * This method reads the input file and populates all the 
	 * data variables for further processing
	 */
	private static SearchAlgo loadFile(String filename) {
		File file = new File(filename);
		try {
			Scanner sc = new Scanner(file);
			SearchAlgo algo = SearchAlgo.valueOf(sc.nextLine().trim().toUpperCase());
			n = sc.nextInt();
			m = sc.nextInt();
			sc.nextLine();
			board = new boolean[n][m];
			for (int i = 0; i < n; i++) {
				String line = sc.nextLine();
				for (int j = 0; j < m; j++) {
					if (line.charAt(j) == '1') {
						board[i][j] = true;
					} else if (line.charAt(j) == 'S') {
						knight = new Location(j, i, null);
					} else if (line.charAt(j) == 'G') {
						king = new Location(j, i, null);
					}
				}
			}
			sc.close();
			return algo;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
