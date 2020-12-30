package mycellar.placesmanagement;

public class Row {
	private final int num;
	private int col;
	public Row(int num)
	{
		this.num = num;
	}

	public int getNum(){
		return num;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	@Override
	public String toString() {
		return "Row " + num + " columns: " + col;
	}
}
