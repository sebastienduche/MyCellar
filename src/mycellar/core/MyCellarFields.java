package mycellar.core;

import java.util.ArrayList;

import mycellar.Program;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2016</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.4
 * @since 26/07/17
 */

public enum MyCellarFields {
	NAME(Program.getLabel("Infos106")),
	YEAR(Program.getLabel("Infos189")),
	TYPE(Program.getLabel("Infos134")),
	PLACE(Program.getLabel("Infos217")),
	NUM_PLACE(Program.getLabel("Infos082")),
	LINE(Program.getLabel("Infos028")),
	COLUMN(Program.getLabel("Infos083")),
	PRICE(Program.getLabel("Infos135")),
	COMMENT(Program.getLabel("Infos137")),
	MATURITY(Program.getLabel("Infos391")),
	PARKER(Program.getLabel("Infos392")),
	COLOR(Program.getLabel("AddVin.Color")),
	COUNTRY(Program.getLabel("Main.Country")),
	VINEYARD(Program.getLabel("Main.Vignoble")),
	AOC(Program.getLabel("Main.AppelationAOC")),
	IGP(Program.getLabel("Main.AppelationIGP")),
	
	// Pour l'import de données
	EMPTY(""),
	USELESS(Program.getLabel("Infos271"));
	
	private String label;
	
	MyCellarFields(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
	private static final ArrayList<MyCellarFields> list = new ArrayList<MyCellarFields>();
	
	public static ArrayList<MyCellarFields> getFieldsList() {
		if(list.isEmpty()) {
    		list.add(NAME);
    		list.add(YEAR);
    		list.add(TYPE);
    		list.add(PLACE);
    		list.add(NUM_PLACE);
    		list.add(LINE);
    		list.add(COLUMN);
    		list.add(PRICE);
    		list.add(COMMENT);
    		list.add(MATURITY);
    		list.add(PARKER);
    		list.add(COLOR);
    		list.add(COUNTRY);
    		list.add(VINEYARD);
    		list.add(AOC);
    		list.add(IGP);
		}
		return list;
	}
}
