package mycellar.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2016</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.4
 * @since 08/06/18
 */
public class PDFTools {
	
	private final PDDocument document;
	private final PDPage page;
	private final PDPageContentStream content;
	
	private PDFTools(PDDocument document, PDPage page, PDPageContentStream content) {
		this.document = document;
		this.page = page;
		this.content = content;
	}

	public PDDocument getDocument() {
		return document;
	}

	public PDPage getPage() {
		return page;
	}

	public PDPageContentStream getContent() {
		return content;
	}
	
	public void drawTable(PDFPageProperties pageProperties, PDFProperties properties, List<PDFRow> rows, PDFRow header) throws IOException {
		drawTable(document, page, content, pageProperties, properties, rows, header);
	}

	/**
	 * @param page
	 * @param contentStream
	 * @throws IOException
	 */
	private static void drawTable(PDDocument doc, PDPage page, PDPageContentStream contentStream,
	                            PDFPageProperties pageProperties, PDFProperties properties,
	                            List<PDFRow> rows, PDFRow header) throws IOException {
		
	    int nbrows = rows.size();
	    if(nbrows == 0)
	    	return;
	    final int nbDatas = nbrows;
	    final int cols = rows.get(0).getCellCount();
	    final float rowHeight = 20f;
	    float tableWidth = page.getMediaBox().getWidth() - pageProperties.getMarginLeft() - pageProperties.getMarginRight();
	    final float unitColWidth = tableWidth / 19f;
	    final float colWidth = tableWidth/(float)cols;
	    final float cellMargin = 5f;

	    float y = page.getMediaBox().getHeight() - pageProperties.getStartTop();

	    //draw the rows
	    float nexty = y;
	    int maxDrawRow = nbrows;
	    if(header != null) {
	    	maxDrawRow++;
	    	nbrows++;
	    }
	    int i;
	    if(properties != null)
	    	tableWidth = unitColWidth * properties.getTotalColumnWidth();
	    for (i = 0; i <= nbrows; i++) {
	        contentStream.moveTo(pageProperties.getMarginLeft(), nexty);
	        contentStream.lineTo(pageProperties.getMarginLeft()+tableWidth, nexty);
	        contentStream.stroke();
	        nexty -= rowHeight;
	        if((nexty - pageProperties.getMarginBottom())< 0) {
	        	maxDrawRow = i;
	        	break;
	        }
	    }

	    //draw the columns
	    float nextx = pageProperties.getMarginLeft();
	    float yEnd = y-(maxDrawRow*rowHeight);
	    for (i = 0; i <= cols; i++) {
	        contentStream.moveTo(nextx, y);
	        contentStream.lineTo(nextx, yEnd);
	        contentStream.stroke();
	        if(i == cols)
	        	break;
	        if(properties != null)
	        	nextx += (unitColWidth)*properties.getColumnWidth(i);
	        else
	        	nextx += colWidth;
	    }

	    //now add the text        
	    contentStream.setFont(pageProperties.getFont(), properties.getFontSize());        

	    float textx = pageProperties.getMarginLeft()+cellMargin;
	    float texty = y-15;       
	    i = 0;
	    if(header != null) {
	    	drawRow(contentStream, pageProperties, properties, header, textx, texty, unitColWidth, colWidth);
	    	texty -= rowHeight;
	    	i++;
	    }
	    int countDatas = 0;
	    for(PDFRow r : rows){
	    	if(i>=maxDrawRow)
	    		break;
	    	i++;
	    	countDatas++;
	    	drawRow(contentStream, pageProperties, properties, r, textx, texty, unitColWidth, colWidth);
	        texty -= rowHeight;
	    }
	    
	    if(countDatas < nbDatas) {
	    	rows = rows.subList(countDatas, nbDatas);
	    	page = new PDPage();
			doc.addPage( page );
			contentStream = new PDPageContentStream(doc, page);
			pageProperties.setStartTop(pageProperties.getMarginTop());
	    	drawTable(doc, page, contentStream, pageProperties, properties, rows, header);
	    	contentStream.close();
	    }
	}
	
	private static void drawRow(PDPageContentStream contentStream, PDFPageProperties pageProperties, PDFProperties properties, PDFRow r, float textx, float texty, float unitColWidth, float colWidth) throws IOException {
		if(r.getFont() != null)
    		contentStream.setFont(r.getFont(), r.getFontSize());
    	else
    		contentStream.setFont(pageProperties.getFont(), pageProperties.getFontSize());
    	int j=0;
        for(String text : r.getCells()){
            contentStream.beginText();
            contentStream.newLineAtOffset(textx,texty);
            contentStream.showText(text);
            contentStream.endText();
            if(properties != null)
	        	textx += (unitColWidth)*properties.getColumnWidth(j);
	        else
	        	textx += colWidth;
            j++;
        }
	}

	public void addTitle(String title, float marginTop, PDFont font, int fontSize) throws IOException {
		addTitle(title, marginTop, font, fontSize, page, content);
	}
	
	private static void addTitle(String title, float marginTop, PDFont font, int fontSize, PDPage page, PDPageContentStream content) throws IOException {
    	float titleWidth = font.getStringWidth(title) / 1000 * fontSize;
    	float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

    	content.beginText();
    	content.setFont(font, fontSize);
    	content.newLineAtOffset((page.getMediaBox().getWidth() - titleWidth) / 2, page.getMediaBox().getHeight() - marginTop - titleHeight);
    	content.showText(title);
    	content.endText();
    }
	
	/*public static void main(String args[]) throws IOException, COSVisitorException {
		PDDocument doc = new PDDocument();
		PDPage page = new PDPage();
		doc.addPage( page );

		PDPageContentStream contentStream = new PDPageContentStream(doc, page);

		LinkedList<PDFRow> rows = new LinkedList<PDFRow>();
		PDFRow row = new PDFRow();
		for(int j=0; j<6; j++)
			row.addCell("Header "+j);
		rows.add(row);
		row.setFont(PDType1Font.HELVETICA_BOLD, 12);
		for(int i=0; i<50; i++) {
			row = new PDFRow();
			for(int j=0; j<6; j++)
				row.addCell("Test"+i+" "+j);
			rows.add(row);
		}

		PDFPageProperties pageProperties = new PDFPageProperties(30, 20, 20, 20, PDType1Font.HELVETICA, 12);
		pageProperties.setStartTop(100);
		PDFProperties properties = new PDFProperties("Titre", 10, 12, true, true);
		properties.addColumn(0, 2);
		properties.addColumn(1, 3);
		properties.addColumn(2, 2);
		properties.addColumn(3, 3);
		properties.addColumn(4, 2);
		properties.addColumn(5, 2);
		drawTable(doc, page, contentStream, pageProperties, properties, rows, true);
		contentStream.close();
		doc.save("test.pdf");
	}
*/
	public static PDFTools createPDFFile() throws IOException {
		PDDocument doc = new PDDocument();
		PDPage page = new PDPage();
		doc.addPage(page);
		
		return new PDFTools(doc, page, new PDPageContentStream(doc, page));
	}
	
	public void save(File file) throws IOException {
		content.close();
		document.save(file);
		document.close();
	}
}
