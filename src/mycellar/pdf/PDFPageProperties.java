package mycellar.pdf;

import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2016</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 08/06/18
 */

public class PDFPageProperties {

	private float marginTop;
	private float marginBottom;
	private float marginLeft;
	private float marginRight;
	private PDFont font;
	private int fontSize;
	private float startTop;
	
	public PDFPageProperties(float marginTop, float marginBottom, float marginLeft, float marginRight, PDFont font, int fontSize) {
		this.marginTop = marginTop;
		this.marginBottom = marginBottom;
		this.marginLeft = marginLeft;
		this.marginRight = marginRight;
		this.font = font;
		this.fontSize = fontSize;
		startTop = marginTop;
	}

	float getMarginTop() {
		return marginTop;
	}

	public void setMarginTop(float marginTop) {
		this.marginTop = marginTop;
	}

	float getMarginBottom() {
		return marginBottom;
	}

	public void setMarginBottom(float marginBottom) {
		this.marginBottom = marginBottom;
	}

	float getMarginLeft() {
		return marginLeft;
	}

	public void setMarginLeft(float marginLeft) {
		this.marginLeft = marginLeft;
	}

	float getMarginRight() {
		return marginRight;
	}

	public void setMarginRight(float marginRight) {
		this.marginRight = marginRight;
	}

	public PDFont getFont() {
		return font;
	}

	public void setFont(PDFont font) {
		this.font = font;
	}

	int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	float getStartTop() {
		return startTop;
	}

	public void setStartTop(float startTop) {
		this.startTop = startTop;
	}
}
