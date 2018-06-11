package mycellar;
/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.7
 * @since 20/07/17
 */
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public final class MyCellarImage {
	
	private static final MyCellarImage instance = new MyCellarImage();
	
	private static MyCellarImage getInstance() {
		return instance;
	}

	public static final ImageIcon NEW = getInstance().getImage("new.gif");
	public static final ImageIcon SEARCH = getInstance().getImage("find.gif");
	public static final ImageIcon OPEN = getInstance().getImage("folder.gif");
	public static final ImageIcon SAVE = getInstance().getImage("save.png");
	public static final ImageIcon PLACE = getInstance().getImage("place.gif");
	public static final ImageIcon MODIFYPLACE = getInstance().getImage("modplace.gif");
	public static final ImageIcon DELPLACE = getInstance().getImage("delplace.gif");
	public static final ImageIcon TABLE = getInstance().getImage("table.gif");
	public static final ImageIcon IMPORT = getInstance().getImage("import.gif");
	public static final ImageIcon EXPORT = getInstance().getImage("export.gif");
	public static final ImageIcon PARAMETER = getInstance().getImage("parameters.gif");
	public static final ImageIcon SAVEAS = getInstance().getImage("saveas.png");
	public static final ImageIcon WINE = getInstance().getImage("wine.png");
	public static final ImageIcon ADD = getInstance().getImage("add.png");
	public static final ImageIcon DELETE = getInstance().getImage("delete.gif");
	public static final ImageIcon SHOW = getInstance().getImage("glasses.png");
	public static final ImageIcon STATS = getInstance().getImage("stats.png");
	public static final ImageIcon CUT = getInstance().getImage("Cut16.gif");
	public static final ImageIcon COPY = getInstance().getImage("Copy16.gif");
	public static final ImageIcon PASTE = getInstance().getImage("Paste16.gif");
	public static final ImageIcon WHITEWINE = getInstance().getImage("winegreen.png");
	public static final ImageIcon BLACKWINE = getInstance().getImage("wineblack.png");
	public static final ImageIcon PINKWINE = getInstance().getImage("winewhite.png");
	public static final ImageIcon TRASH = getInstance().getImage("trash.png");
	public static final ImageIcon RESTORE = getInstance().getImage("restore.png");
	public static final ImageIcon PDF = getInstance().getImage("pdf.png");
	public static final ImageIcon ICON = getInstance().getImage("MyCellar.gif");
	public static final ImageIcon ERROR = getInstance().getImage("errors.png");

	private ImageIcon getImage(final String filename) {
		URL stream = getClass().getClassLoader().getResource("resources/"+filename);
		if(stream == null) {
			Program.Debug("MyCellarImage: Missing resource "+filename);
			return null;
		}
		BufferedImage image = null;
		try {
			image = ImageIO.read(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ImageIcon(image);
	}
}
