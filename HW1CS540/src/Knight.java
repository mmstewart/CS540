import java.util.Iterator;

public class Knight {


    public static int count = 1;
    public static int DEAD_END = 0;
    public Location start,
            finish;
    private int x;
    private int y;
    public int[][] board = new int[x][y];
    public Location parent;

    
    public Knight(int rows, int columns) {
    	board = new int[rows][columns];
    } 


    public int[][] getboard() {
        int[][] boardCopy = new int[board.length][board[0].length];

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                boardCopy[i][j] = board[i][j];
            }
        }
        return boardCopy;
    } 

    public boolean isOK(Location pos) {
		if (pos.getX() >= 0 && pos.getY() >= 0 && pos.getX() < board.length && pos.getY() < board[0].length && board[pos.getX()][pos.getY()] == 0) 
		{
			return true;
		}
		return false;
    }
    
    public int countExpandedNodes() {
    	return count++;
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


    public void markAsPossible(Location pos) {
        if (this.isOK(pos)) {
            board[pos.getX()][pos.getY()] = count;
            count++;
        }

    }

    public boolean isGoal(Location pos) {
        if (count == (board.length * board[0].length) + 1) {
            return true;
        } else {
            return false;
        }
    }

    public void markAsDeadEnd(Location pos) {

        board[pos.getX()][pos.getY()] = DEAD_END;
        count--;
    }

    public String toString() {
        String result = "\n";

        for (int m = 0; m < board.length; m++) {
            for (int n = 0; n < board[0].length; n++) {
                result += String.valueOf(board[m][n]) + ' ';
            }
            result += "\n";
        } 
        return result;
    }


    public Iterator<Location> iterator(Location pos) {
        return new KnightIterator(pos);
    }

    protected class KnightIterator implements Iterator<Location> {

        
        protected int m,
                n, count2;
        protected Location newParent;

        /**
         * Initializes this KnightIterator object to start at 
         * a given Location.
         *
         * @param pos the Location the Iterator objects starts at.
         */
        public KnightIterator(Location pos) {
            m = pos.getX();
            n = pos.getY();
            newParent = pos.getParent();
            count2 = 0;

        }
        
        /**
         * Determines if this KnightIterator object can advance to another
         * Location.
         *
         * @return true if this KnightIterator object can advance; false
         * otherwise.
         */
        @Override
        public boolean hasNext() {
            return count2 < 8;
        }

        /**
         * Advances this KnightIterator object to the next Location.
         *
         * @return the Location advanced to.
         */
        @Override
        public Location next() {
            Location nextLocation = new Location(m, n, newParent);
            switch (count2++) {

                case 0:
                    nextLocation = new Location(m + 2, n - 1, newParent); //right 2 down 1
                    break;
                case 1:
                    nextLocation = new Location(m + 1, n - 2, newParent); //right 1 down 2
                    break;
                case 2:
                    nextLocation = new Location(m - 1, n - 2, newParent); //left 1 down 2
                    break;
                case 3:
                    nextLocation = new Location(m - 2, n - 1, newParent); //left 2 down 1
                    break;
                case 4:
                    nextLocation = new Location(m - 2, n + 1, newParent); //left 2 up 1
                    break;
                case 5:
                    nextLocation = new Location(m - 1, n + 2, newParent); //left 1 up 2
                    break;
                case 6:
                    nextLocation = new Location(m + 1, n + 2, newParent); //right 1 up 2
                    break;
                case 7:
                    nextLocation = new Location(m + 2, n + 1, newParent); //right 2 up 1
                    break;
            } // switch;                
            return nextLocation;
        }

    }
}
