
public class Cell {
	private int value;
	private int max;
	public int row, col, groupid;
	public boolean finalized;
	private boolean[] poss;
	
	public Cell(int x, int y, int groupid, int max) {
		this.row = x;
		this.col = y;
		this.max = max;
		this.groupid = groupid;
		value = 0;
		finalized = false;
		poss = new boolean[max];
		for(int i = 0; i < max; i++) {
			poss[i] = true;
		}
	}
	
	public Cell(int x, int y, int value) {
		this(x, y, 0, 1);
		this.setValue(value);
	}
	
	public Cell(Cell cell) {
		this.row = cell.row;
		this.col = cell.col;
		this.max = cell.max;
		this.groupid = cell.groupid;
		this.value = cell.value;
		this.finalized = cell.finalized;
		this.poss = new boolean[this.max];
		for(int i = 0; i < this.max; i++) {
			this.poss[i] = cell.poss(i + 1);
		}
	}

	/** 
	 * @return Returns true IFF a value has been assigned
	 */
	boolean onlyOnePossible() {
		if(finalized) return false;
		boolean found = false;
		int index = 0;
		for(int i = 0; i < max; i++) {
			if(poss[i]) {
				if(found) return false;
				found = true;
				index = i + 1;
			}
		}
		setValue(index);
		return true;
	}
	
	void clear(int i) {
		if(i != 0) poss[i - 1] = false;
	}
	
	boolean poss(int i) {
		return poss[i-1];
	}
	
	public String toString() {
		String str = new String();
		str = "Value: " + value + "\nX: " + row + "\nY: " + col + "\nGroupId: " + groupid;
		return str;		
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		finalized = true;
		this.value = value;
		for(int i = 0; i < max; i++) {
			poss[i] = false;
		}
	}
}
