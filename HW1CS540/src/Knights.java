
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
class Loc {
	public int x = 0;
	public int y = 0;
	private Loc parent;
	public static int count = 1;
	private int a;
	private int b;
	public int[][] board = new int[a][b];
	public static int DEAD_END = 0;
	public int count2;
	public Loc k;

	public Loc(int x, int y, Loc parent) {
		this.x = x;
		this.y = y;
		this.parent = parent;
	}

	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	public Loc getParent() {
		return parent;
	}
	
	public void setParent(Loc parent) {
		this.parent = parent;
	}

	public boolean isOK(Loc pos) 
	{
		if (pos.getX() >= 0 && pos.getY() >= 0 && pos.getX() < board.length && pos.getY() < board[0].length) 
		{
			return true;
		}
		return false;
	}

	public void markAsPossible(Loc pos)
	{
		if (this.isOK(pos)) 
		{
			board[pos.getX()][pos.getY()] = count;
			count++;
		}
	}
	
	public int countExpandedNodes() 
	{
		return count++;
	}

	public boolean isGoal(Loc pos) {
		if (count == (board.length * board[0].length) + 1) {
			return true;
		} else {
			return false;
		}
	}

	public void markAsDeadEnd(Loc pos) {

		board[pos.getX()][pos.getY()] = DEAD_END;
		count--;
	}

	public boolean hasNext() {
		return count2 < 8;
	}

	public Loc next() {
		Loc nextLoc = new Loc(x, y, parent);
		switch (count2++) {

		case 0:
			nextLoc = new Loc(x + 2, y - 1, parent); // right 2 down 1
			break;
		case 1:
			nextLoc = new Loc(x + 1, y - 2, parent); // right 1 down 2
			break;
		case 2:
			nextLoc = new Loc(x - 1, y - 2, parent); // left 1 down 2
			break;
		case 3:
			nextLoc = new Loc(x - 2, y - 1, parent); // left 2 down 1
			break;
		case 4:
			nextLoc = new Loc(x - 2, y + 1, parent); // left 2 up 1
			break;
		case 5:
			nextLoc = new Loc(x - 1, y + 2, parent); // left 1 up 2
			break;
		case 6:
			nextLoc = new Loc(x + 1, y + 2, parent); // right 1 up 2
			break;
		case 7:
			nextLoc = new Loc(x + 2, y + 1, parent); // right 2 up 1
			break;
		}
		return nextLoc;
	}

	@Override
	public String toString() {
		return x + " " + y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Loc) {
			Loc loc = (Loc) obj;
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

	public boolean reachGoal(Loc pos) {
		k = new Loc(pos.getX(),pos.getY(),pos.getParent());
		while (k.hasNext()) {
			pos = next();
			if (k.isOK(pos)) {
				k.markAsPossible(pos);
				System.out.println(k.toString());
				if (k.isGoal(pos) || reachGoal(pos)) 
				{
					return true;
				}
				k.markAsDeadEnd(pos);
			}
		}
		System.out.println("NOT REACHABLE");
		return false;
	}
}

public class Knights {
	// represents the map/board
	private static boolean[][] board;
	// represents the goal node
	private static Loc king;
	// represents the start node
	private static Loc knight;
	// y dimension of board
	private static int n;
	// x dimension of the board
	private static int m;

	// enum defining different algo types
	enum SearchAlgo {
		BFS, DFS, ASTAR;
	}

	public static void main(String[] args) {
		// if (args != null && args.length > 0) {
		// loads the input file and populates the data variables
		System.out.println("Enter file name: ");
		Scanner s = new Scanner(System.in);
		String input = s.next();
		s.close();
		// SearchAlgo algo = loadFile(args[0]);
		SearchAlgo algo = loadFile(input);
		if (algo != null) {
			switch (algo) {
			case DFS:
				executeDFS();
				break;
			case BFS:
				executeBFS();
				break;
			case ASTAR:
				executeAStar();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Implementation of Astar algorithm for the problem
	 */
	private static void executeAStar() {
		// TODO: Implement A* algorithm in this method
	}

	/**
	 * Implementation of BFS algorithm
	 */
	private static void executeBFS() {

	}

	/**
	 * Implementation of DFS algorithm
	 */
	private static void executeDFS() {
		knight = new Loc(knight.getX(), knight.getY(), knight.getParent());
		knight.reachGoal(king);
		System.out.println(knight.toString());
		System.out.println("Expanded nodes: " + knight.countExpandedNodes());
	}

	/**
	 * 
	 * @param filename
	 * @return Algo type This method reads the input file and populates all the data
	 *         variables for further processing
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
						knight = new Loc(j, i, null);
					} else if (line.charAt(j) == 'G') {
						king = new Loc(j, i, null);
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