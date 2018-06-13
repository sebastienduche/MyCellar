package mycellar.core;

import mycellar.Program;

import java.util.ArrayList;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2016</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.5
 * @since 08/06/18
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
	
	private final String label;
	
	MyCellarFields(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
	private static final ArrayList<MyCellarFields> FIELDS = new ArrayList<>();
	
	public static ArrayList<MyCellarFields> getFieldsList() {
		if(FIELDS.isEmpty()) {
    		FIELDS.add(NAME);
    		FIELDS.add(YEAR);
    		FIELDS.add(TYPE);
    		FIELDS.add(PLACE);
    		FIELDS.add(NUM_PLACE);
    		FIELDS.add(LINE);
    		FIELDS.add(COLUMN);
    		FIELDS.add(PRICE);
    		FIELDS.add(COMMENT);
    		FIELDS.add(MATURITY);
    		FIELDS.add(PARKER);
    		FIELDS.add(COLOR);
    		FIELDS.add(COUNTRY);
    		FIELDS.add(VINEYARD);
    		FIELDS.add(AOC);
    		FIELDS.add(IGP);
		}
		return FIELDS;
	}
}
