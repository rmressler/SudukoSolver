import java.util.ArrayList;


public class Grid {
	public int size;
	public Cell[][] grid;
	public ArrayList<ArrayList<Cell>> rows, cols, groups;
	
	/**Construct a grid of size sizexsize
	 * 
	 * @param size: the size along any edge of the grid
	 */
	public Grid(int size) {
		this.size = size;
		rows = new ArrayList<ArrayList<Cell>>(size);
		cols = new ArrayList<ArrayList<Cell>>(size);
		groups = new ArrayList<ArrayList<Cell>>(size);
		for(int i = 0; i < size; i++) {
			ArrayList<Cell> rowtmp = new ArrayList<Cell>(size);
			ArrayList<Cell> coltmp = new ArrayList<Cell>(size);
			ArrayList<Cell> grptmp = new ArrayList<Cell>(size);
			rows.add(rowtmp);
			cols.add(coltmp);
			groups.add(grptmp);
		}
		grid = new Cell[size][size];
		for(int row = 0; row < size; row++) {
			for(int col = 0; col < size; col++) {
				//groupid is initialized to be squares
				int tmp = (int)Math.sqrt(size); 
				grid[row][col] = new Cell(row + 1, col + 1, (col/tmp) + tmp*(row/tmp) + 1, size);
				rows.get(row).add(grid[row][col]);
				cols.get(col).add(grid[row][col]);
				groups.get((col/tmp) + tmp*(row/tmp)).add(grid[row][col]);
			}
		}
	}
	
	/**Allows the creation of a grid using a preset matrix of values
	 * 
	 * @param size: the dimension of the matrix (sizexsize)
	 * @param preset: the matrix to be used
	 * 
	 * @note there is no error checking here, do it right
	 */
	public Grid(int size, int[][] preset) {
		this(size);
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				if(preset[i][j] != 0) grid[i][j].setValue(preset[i][j]);
			}
		}
	}
	
	/**Copy constructor
	 * 
	 * @param g: the grid to be copied
	 */
	public Grid(Grid g) {
		this.size = g.size;
		rows = new ArrayList<ArrayList<Cell>>(size);
		cols = new ArrayList<ArrayList<Cell>>(size);
		groups = new ArrayList<ArrayList<Cell>>(size);
		for(int i = 0; i < size; i++) {
			ArrayList<Cell> rowtmp = new ArrayList<Cell>(size);
			ArrayList<Cell> coltmp = new ArrayList<Cell>(size);
			ArrayList<Cell> grptmp = new ArrayList<Cell>(size);
			rows.add(rowtmp);
			cols.add(coltmp);
			groups.add(grptmp);
		}
		grid = new Cell[size][size];
		for(int row = 0; row < size; row++) {
			for(int col = 0; col < size; col++) {
				int tmp = (int)Math.sqrt(size); 
				grid[row][col] = new Cell(g.grid[row][col]);
				rows.get(row).add(grid[row][col]);
				cols.get(col).add(grid[row][col]);
				groups.get((col/tmp) + tmp*(row/tmp)).add(grid[row][col]);
			}
		}
	}
	
	/**Simple printing method
	 * 
	 */
	public String toString() {
		String str = new String();
		for(int row = 0; row < size; row++) {
			for(int col = 0; col < size; col++) {
				str += grid[row][col].getValue() + "\t"; 
			}
			str += "\n";
		}
		return str;
	}
	
	/**Super caller for clear(Cell c), calls on every cell in the grid
	 * 
	 */
	public void clear() {
		for(int row = 0; row < size; row++) {
			for(int col = 0; col < size; col++) {
				clear(grid[row][col]);
			}
		}
	}
	
	/**For a given Cell, clears its possibility matrix according to the values in its respective groups
	 * 
	 * @param c: the cell to be cleared
	 */
	public void clear(Cell c) {
		for(Cell cell : rows.get(c.row - 1)) {
			cell.clear(c.getValue());
		}
		for(Cell cell : cols.get(c.col - 1)) {
			cell.clear(c.getValue());
		}
		for(Cell cell : groups.get(c.groupid - 1)) {
			cell.clear(c.getValue());
		}
	}

	/**Super caller for groupHasOne()
	 * 
	 * @return True iff any call returned true
	 */
	public boolean groupsHaveOne() {
		boolean changed = false;
		for(ArrayList<Cell> list : rows) {
			if(groupHasOne(list)) changed = true;
		}
		for(ArrayList<Cell> list : cols) {
			if(groupHasOne(list)) changed = true;
		}
		for(ArrayList<Cell> list : groups) {
			if(groupHasOne(list)) changed = true;
		}
		return changed;
	}
	
	/**Within any group (group, col, row) checks if there is only one box with a particular possibility
	 * 
	 * @param list: the group to be checked
	 * @return True iff something has been changed
	 */
	public boolean groupHasOne(ArrayList<Cell> list) {
		boolean changed = false;
		for(int i = 1; i <= size; i++) {
			boolean flag = false;
			boolean change = true;
			Cell potential = null;
			for(Cell c : list) {
				if(c.getValue() == i) {
					change = false;
					break;
				}
				if(c.poss(i)) {
					if(flag) {
						change = false;
						break;
					}
					potential = c;
					flag = true;
				}
			}
			if(change && potential != null) {
				potential.setValue(i);
				changed = true;
			}
		}
		return changed;
	}
	
	
	/**Super caller for onlyOnePossible() in the Cell class
	 * 
	 * @return True iff one of the calls to onlyOnePossible() returned true (i.e. something has been changed)
	 */
	public boolean onlyOnePossible() {
		boolean flag = false;
		for(int row = 0; row < size; row++) {
			for(int col = 0; col < size; col++) {
				if(grid[row][col].onlyOnePossible()) flag = true;
			}
		}
		return flag;
	}
		
	/**Attempts the two logical rules until it cannot produce any more results with them
	 * 
	 * @return True iff there are no errors AND the grid is solved
	 */
	public boolean solve() {
		boolean changed = true;
		while(changed) {
			changed = false;
			clear();
			if(groupsHaveOne()) changed = true;
			clear();
			if(onlyOnePossible()) changed = true;
		}
		if(!errors()) {
			return solved();
		}
		return false;
	}
	
	/**Recursive method to solve a given grid
	 * 
	 * @param g The grid to be solved
	 * @return The totally solved grid, null if the grid has errors
	 */
	public Grid solver(Grid g) {
		if(g.solve()) return g;
		if(g.errors()) return null;
		Grid tmp = null;
		Cell ctmp;
		while(tmp == null) {
			//try something
			ctmp = nextAttempt(g);
			//tmp will only be something other than null if this attempt ultimately solved the grid
			tmp = solver(attempt(g, ctmp));
			//if the attempt failed in the long run clear the possibility of this attempt
			if(tmp == null) {
				g.grid[ctmp.row][ctmp.col].clear(ctmp.getValue());
				//if clearing this attempt leaves us with only one possibility in this cell, we just do solver on our new situation
				if(g.grid[ctmp.row][ctmp.col].onlyOnePossible()) return solver(g);
			}
		}
		return tmp;
	}
	
	/**Creates a new grid with the given attempt cell
	 * 
	 * @param g The base grid to return a modified copy of (remains the same)
	 * @param c The cell object with the relevant info in it (x, y, and value)
	 * @return The modified Grid copy
	 */
	public Grid attempt(Grid g, Cell c) {
		Grid tmp = new Grid(g);
		tmp.grid[c.row][c.col].setValue(c.getValue());
		return tmp;
	}
	
	/**
	 * 
	 * @param g The grid to be searched
	 * @return A dummy Cell object with the relevant coordinates and value to attempt
	 */
	public Cell nextAttempt(Grid g) {
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				for(int p = 1; p <= size; p++) {
					if(g.grid[i][j].poss(p)) return new Cell(i, j, p);
				}
			}
		}
		return null;
	}
	
	/** A non error checking solved function
	 * @return Returns true if every cell has been finalized
	 */
	public boolean solved() {
		for(int row = 0; row < size; row++) {
			for(int col = 0; col < size; col++) {
				if(!grid[row][col].finalized) return false;
			}
		}
		return true;
	}
	
	/**This is the super-caller for error()
	 * @return True if any call to error() on any group, column, or row returned true
	 */
	public boolean errors() {
		//in this function we break as soon as we hit any error
		//all you need is one!
		for(ArrayList<Cell> list : rows) {
			if(error(list)) return true;
		}
		for(ArrayList<Cell> list : cols) {
			if(error(list)) return true;
		}
		for(ArrayList<Cell> list : groups) {
			if(error(list)) return true;
		}
		return false;
	}
	
	/**Check for any errors in the list
	 * 
	 * Checks for two things: 
	 * whether there is a collision (i.e. two conflicting finalized cells) 
	 * whether there is a value which can no longer possibly be placed in this list 
	 * @param list The list to be checked
	 * @return Returns true IFF there is an error in the list
	 */
	public boolean error(ArrayList<Cell> list) {
		//collisions will keep track of all of the finalized values we see, and IF we see any twice we'll return true (error)
		boolean[] collisions = new boolean[size];
		//possibles will keep track of all the possible values in this list, and IF by the end there's some value that isn't possible, we'll return true (error)
		boolean[] possibles = new boolean[size];
		for(int i = 0; i < size; i++) {
			collisions[i] = false;
			possibles[i] = false;
		}
		for(Cell c : list) {
			if(c.finalized) {
				//if the value has been finalized at zero, something went wrong, return true (error)
				if(c.getValue() == 0) return true;
				//if we have already seen the value of this cell, return true (error)
				if(collisions[c.getValue() - 1]) return true;
				//otherwise put a true value in that spot in the collision matrix for future reference
				collisions[c.getValue() - 1] = true;
				//also register that this value is possible in this list (it's not just possible, it's actual)
				possibles[c.getValue() - 1] = true;
			}
			for(int i = 1; i <= size; i++) {
				//now run through the list of possibilities for this cell
				if(c.poss(i)) possibles[i-1] = true;
			}
		}
		//traverse through possibles, and if we find a false value return true (error)
		for(int i = 0; i < size; i++) {
			if(!possibles[i]) return true;
		}
		return false;
	}
	
	public static void main(String args[]) {
		int size = 9;
		int[][] preset =
			{
				{0,0,0,0,4,5,0,0,9},
				{0,7,8,0,0,0,0,0,6},
				{0,0,0,0,0,0,0,0,0},
				{0,5,0,0,0,7,8,0,0},
				{0,9,0,0,0,0,0,4,0},
				{0,0,1,4,0,0,0,2,0},
				{0,0,0,7,0,0,0,0,0},
				{6,0,0,0,0,0,2,5,0},
				{5,0,0,1,3,0,0,0,4}
			};
		Grid g = new Grid(size, preset);
		System.out.println(g.solver(g));
	}
}
