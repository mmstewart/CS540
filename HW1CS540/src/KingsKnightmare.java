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
	
	public Location(int x, int y, Location parent) {
		this.x = x;
		this.y = y;
		this.parent = parent;
	}
	
	public Location() {

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


public class KingsKnightmare {
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
		if (args != null && args.length > 0) {
			//loads the input file and populates the data variables
			SearchAlgo algo = loadFile(args[0]);
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
		}
	}
	private static int h(Location next){
		int heuristic = 0; //Manhattan distance
		int value = 0;
		int g = 0;
		heuristic = Math.abs(next.getX() - king.getX()) + Math.abs(next.getY() - king.getY()); //Manhattan distance
		//|x – x_goal| + |y – y_goal|
		while(next.getParent() != null){
			g++; 
			next = next.getParent();
		}
		value = heuristic + (g*3); 
		return value;
	}

	
	/**
	 * Implementation of Astar algorithm for the problem
	 */
	private static void executeAStar() {
		PriorityQ<Location> frontier = new PriorityQ<Location>(); //a priority Q ordered by path-cost
		explored = new boolean[n+1][m+1]; //2D array of explored spaces
		frontier.add(knight, 3);
		int count = 0;//number of moves tried
		boolean goal = false;
		int exploredNodes = 0;//number of nodes explored, 1 because of knight
		
		while (goal == false){
			if (frontier.isEmpty() == true){
				System.out.println("NOT REACHABLE");
				System.out.println("Number of Expanded Nodes: " + exploredNodes);
				break;
			}
			
			SimpleEntry<Location, Integer> next = frontier.poll();
			knight = next.getKey();
			if (isGoal(knight) == true){ //if Goal
			    Stack<Location> reverse =new Stack<Location>(); 
			    while(knight.getParent() != null){ //reverse the stack with correct path
			        reverse.push(knight);
			        knight = knight.getParent();
			    }
			    reverse.push(knight); 
			    while(!reverse.isEmpty()){
			    	System.out.println(reverse.pop().toString());
			    }
				System.out.println("Number of Expanded Nodes: " + exploredNodes);
				goal = true;
			}
			explored[knight.getY()][knight.getX()] = true; //mark next as explored
			exploredNodes++;
			while (count < 8){ //push valid moves to queue
				Location possibleMove = nextLocation(knight, count); //get next based on count
				if (validMove(possibleMove) == true //if move is valid
						&& explored[possibleMove.getY()][possibleMove.getX()] == false //is not in explored
						&& frontier.exists(possibleMove) == false){  //and is not in frontier
					frontier.add(possibleMove, h(possibleMove)); //add move to frontier
					explored[next.getKey().getY()][next.getKey().getX()] = true; //mark next as explored
				}
				else if (validMove(possibleMove) == true //if move is valid
						&& frontier.exists(possibleMove) == true
						&& (frontier.getPriorityScore(possibleMove) > h(possibleMove))){  //and is in frontier){
					frontier.remove(possibleMove);
					frontier.add(possibleMove, h(possibleMove));
				}
				else{
					count++;
					explored[next.getKey().getY()][next.getKey().getX()] = true;
				}			
			}
			count = 0; //reset count;
		}
	}


	/**
	 * Implementation of BFS algorithm
	 */
	private static void executeBFS() {
		explored = new boolean[n+1][m+1]; //2D array of explored spaces
        LinkedList<Location> pathQueue = new LinkedList<Location>(); //initialize new queue to hold path 
        int count = 0;//number of moves tried
		int invalids = 0; //number of invalid moves
		int exploredNodes = 1;//number of nodes explored, 1 because of knight
		boolean goal = false;
        
		explored[knight.getY()][knight.getX()] = true; //mark knight as explored
		pathQueue.add(knight);
        while (goal == false){
        	knight = pathQueue.removeFirst();
        	while (count < 8){ //push valid moves to queue
				Location next = nextLocation(knight, count); //get next based on count
				if (validMove(next) == true && explored[next.getY()][next.getX()] == false){ //if move is valid
					if (isGoal(next) == true){ //if Goal
						knight = next;
					    Stack<Location> reverse =new Stack<Location>(); 
					    while(knight.getParent() != null){ //reverse the stack with correct path
					        reverse.push(knight);
					        knight = knight.getParent();
					    }
					    reverse.push(knight); 
					    while(!reverse.isEmpty()){
					    	System.out.println(reverse.pop().toString());
					  
					    }
						System.out.println("Expanded Nodes: " + exploredNodes);
						goal = true;
					}
					else{
						pathQueue.add(next); //add move to queue
						explored[next.getY()][next.getX()] = true; //mark next as explored
					}
					
				}
				else{ //move is invalid
					invalids++; //add one to invalids
				}
				count++;  //to do next move
			}
        	if (invalids > 7 && pathQueue.getFirst() == null){ //all moves are invalid and no valid next move
				System.out.println("NOT REACHABLE");
				
				System.out.println("Expanded Nodes: " + exploredNodes);
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
		Stack<Location> path = new Stack<Location>(); //initialize new stack to hold path 
		int count = 0;//number of moves tried
		int invalids = 0; //number of invalid moves
		int exploredNodes = 0;//number of nodes explored
		
		path.push(knight); //push starting knight position to stack
		explored[knight.getY()][knight.getX()] = true; //mark knight as explored
		
		while (!path.isEmpty()){
			//exploredNodes++; //mark an explored node
			if (isGoal(knight) == true){ //if Goal
			    Stack<Location> reverse =new Stack<Location>(); 
			    while(knight.getParent() != null){ //reserse the stack with correct path
			        reverse.push(knight);
			        knight = knight.getParent();
			    }
			    reverse.push(knight); 
			    while(!reverse.isEmpty()){
			    	System.out.println(reverse.pop().toString());
			    	exploredNodes++;
			    }
				System.out.println("Expanded Nodes: " + exploredNodes);
				break;
			}
			while (count < 8){ //push valid moves to stack
				Location next = nextLocation(knight, count); //get next based on count
				if (validMove(next) == true && explored[next.getY()][next.getX()] == false){ //if move is valid
					path.push(next); //push move to stack
					explored[next.getY()][next.getX()] = true; //mark next as explored
				}
				else{ //move is invalid
					invalids++; //add one to invalids
				}
				count++;  //to do next move
			}
			
			if (invalids > 7 && knight.getParent() == null){ //all moves are invalid and no valid parent
				System.out.println("NOT REACHABLE");
				exploredNodes++;
				System.out.println("Expanded Nodes: " + exploredNodes);
				break;
			}
			invalids = 0; //reset invalids
			count = 0; //reset count
			knight = path.pop();
		}
	}
	
    public static Location nextLocation(Location parent, int count) {
    	Location nextPosition = new Location();
    	int x = parent.getX();
    	int y = parent.getY();
    	
        switch (count++) {

            case 0:
                nextPosition = new Location(x + 2, y + 1, parent);
                break;
            case 1:
                nextPosition = new Location(x + 1, y + 2, parent);
                break;
            case 2:
                nextPosition = new Location(x - 1, y + 2, parent);
                break;
            case 3:
                nextPosition = new Location(x - 2, y + 1, parent);
                break;
            case 4:
                nextPosition = new Location(x - 2, y - 1, parent);
                break;
            case 5:
                nextPosition = new Location(x - 1, y - 2, parent);
                break;
            case 6:
                nextPosition = new Location(x + 1, y - 2, parent);
                break;
            case 7:
                nextPosition = new Location(x + 2, y - 1, parent);
                break;
        }              
        return nextPosition;
    }
    
	public static boolean validMove(Location pos){
		if ((pos.getX() < 0) || (pos.getX() >= m) //x coordinate is not valid
				|| (pos.getY() < 0) || (pos.getY() >= n)){ //y coordinate is not valid
			return false;
		}
		else if (board[pos.getY()][pos.getX()] == true){ //obstacle is at position
			return false;
		}
		else{ 
			return true;
		}		
	}
	
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
