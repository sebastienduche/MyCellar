package Cave.pdf;

import java.util.LinkedList;

import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2016</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.1
 * @since 17/01/16
 */

public class PDFRow {

	private LinkedList<String> columns =  new LinkedList<String>();
	private PDFont font;
	private int fontSize;
	
	public PDFRow() {
		
	}
	
	public void addCell(String value) {
		columns.add(value);
	}
	
	public LinkedList<String> getCells() {
		return columns;
	}
	
	public int getCellCount() {
		return columns.size();
	}
	
	public void setFont(PDFont font, int fontSize) {
		this.font = font;
		this.fontSize = fontSize;
	}
	
	public PDFont getFont() {
		return font;
	}
	
	public int getFontSize() {
		return fontSize;
	}
}
