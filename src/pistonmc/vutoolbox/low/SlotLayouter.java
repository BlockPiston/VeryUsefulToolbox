package pistonmc.vutoolbox.low;

/**
 * Util to automatically layout slots in GUI
 */
public class SlotLayouter {
	/**
	 * Size of each slot (16 + border 1 * 2)
	 */
	private static final int SIZE = 18;
	/**
	 * Current index
	 */
	private int index;
	/**
	 * Current location in GUI
	 */
	private int x;
	private int y;
	
	/**
	 * Start of X to go back to when changing lines
	 */
	private int xStart;
	
	/**
	 * How many columns in the current layout
	 */
	private int columnCount;
	private int currentColumn;
	
	public void anchorTo(int x, int y, int columnCount) {
		this.x = x;
		this.y = y;
		this.xStart = x;
		this.columnCount = columnCount;
		this.currentColumn = 0;
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public void next() {
		if (currentColumn >= columnCount - 1) {
			x = xStart;
			y += SIZE;
			currentColumn = 0;
		} else {
			x += SIZE;
			currentColumn++;
		}
		index++;
	}
}
