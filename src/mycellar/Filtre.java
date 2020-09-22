package mycellar;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2011</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.6
 * @since 02/09/20
 */

public class Filtre extends FileFilter {

	static final Filtre FILTRE_SINFO = new Filtre("sinfo", Program.getLabel("Infos313"));
	static final Filtre FILTRE_XLS = new Filtre("xlsx", Program.getLabel("Infos235"));
	static final Filtre FILTRE_ODS = new Filtre("ods", Program.getLabel("Infos336"));
	static final Filtre FILTRE_HTML = new Filtre("html", Program.getLabel("Infos205"));
	public static final Filtre FILTRE_XML = new Filtre("xml", Program.getLabel("Infos203"));
	public static final Filtre FILTRE_PDF = new Filtre("pdf", Program.getLabel("Infos249"));
	static final Filtre FILTRE_CSV = new Filtre("csv", Program.getLabel("Infos202"));
	static final Filtre FILTRE_TXT = new Filtre("txt", Program.getLabel("Infos201"));
	
	private final List<String> suffixes;
	private final String laDescription;

	public Filtre(List<String> suffixes, String description) {
		this.suffixes = new LinkedList<>();
		for(String s : suffixes)
			this.suffixes.add(s.toLowerCase());
		laDescription = description;
	}

	private Filtre(String suffixe, String description) {
		suffixes = new LinkedList<>();
		suffixes.add(suffixe.toLowerCase());
		laDescription = description;
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory())
			return true;

		String suffixe = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1) {
			suffixe = s.substring(i + 1).toLowerCase().strip();
		}
		return suffixe != null && suffixes.contains(suffixe);
	}

	@Override
	public String getDescription() {
		return laDescription;
	}

	public String toString(){
		return "." + suffixes.get(0);
	}

}
