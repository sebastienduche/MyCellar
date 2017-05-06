package Cave;

import java.io.File;
import java.util.LinkedList;

import javax.swing.filechooser.FileFilter;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 23/04/16
 */

public class Filtre extends FileFilter {

	public static final Filtre FILTRE_SINFO = new Filtre("sinfo", Program.getLabel("Infos313"));
	public static final Filtre FILTRE_XLS = new Filtre("xls", Program.getLabel("Infos235"));
	public static final Filtre FILTRE_ODS = new Filtre("ods", Program.getLabel("Infos336"));
	public static final Filtre FILTRE_HTM = new Filtre("htm", Program.getLabel("Infos204"));
	public static final Filtre FILTRE_HTML = new Filtre("html", Program.getLabel("Infos205"));
	public static final Filtre FILTRE_XML = new Filtre("xml", Program.getLabel("Infos203"));
	public static final Filtre FILTRE_PDF = new Filtre("pdf", Program.getLabel("Infos249"));
	public static final Filtre FILTRE_CSV = new Filtre("csv", Program.getLabel("Infos202"));
	public static final Filtre FILTRE_TXT = new Filtre("txt", Program.getLabel("Infos201"));
	
	private LinkedList<String> suffixes;
	private String laDescription;

	public Filtre(LinkedList<String> suffixes, String description) {
		this.suffixes = new LinkedList<String>();
		for(String s : suffixes)
			this.suffixes.add(s.toLowerCase());
		this.laDescription = description;
	}

	private Filtre(String suffixe, String description) {
		this.suffixes = new LinkedList<String>();
		this.suffixes.add(suffixe.toLowerCase());
		this.laDescription = description;
	}

	public boolean accept(File f) {
		if (f.isDirectory())
			return true;

		String suffixe = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1) {
			suffixe = s.substring(i + 1).toLowerCase().trim();
		}
		return suffixe != null && suffixes.contains(suffixe);
	}

	public String getDescription() {
		return laDescription;
	}

	public String toString(){
		return suffixes.get(0);
	}

}
