package mycellar;

import java.util.LinkedList;

public class Part {
	private int num;
	private LinkedList<Row> rows;

	public Part(int num) {
		this.num = num;
		rows = new LinkedList<Row>();
	}

	public void setRows(int nb)
	{
		if(nb > rows.size())
		{
			while(rows.size() < nb)
				rows.add(new Row(rows.size()+1));
		}else
		{
			while(rows.size() > nb)
				rows.removeLast();
		}
	}

	public LinkedList<Row> getRows(){
		return rows;
	}

	public int getRowSize(){
		return rows.size();
	}

	public Row getRow(int n){
		if(rows == null || rows.size() <= n)
			return null;
		return rows.get(n);
	}

	public int getNum(){
		return num;
	}
}

class Row {
	private int num;
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
}
