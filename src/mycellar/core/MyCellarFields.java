package mycellar.core;

import mycellar.Bouteille;
import mycellar.Program;
import mycellar.countries.Countries;
import mycellar.countries.Country;

import java.util.ArrayList;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2016</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.7
 * @since 09/01/19
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

  public static String getValue(MyCellarFields field, Bouteille b) {
		if (b == null) {
			return "";
		}
		String value = "";
		if(field == NAME) {
			value = b.getNom();
		} else if(field == YEAR) {
			value = b.getAnnee();
		} else if(field == TYPE) {
			value = b.getType();
		} else if(field == PLACE) {
			value = b.getEmplacement();
		} else if(field == NUM_PLACE) {
			value = Integer.toString(b.getNumLieu());
		} else if(field == LINE) {
			value = Integer.toString(b.getLigne());
		} else if(field == COLUMN) {
			value = Integer.toString(b.getColonne());
		} else if(field == PRICE) {
			value = b.getPrix();
		} else if(field == COMMENT) {
			value = b.getComment();
		} else if(field == MATURITY) {
			value = b.getMaturity();
		} else if(field == PARKER) {
			value = b.getParker();
		} else if(field == COLOR) {
			value = b.getColor();
		} else if(field == COUNTRY) {
			if(b.getVignoble() != null) {
				Country c = Countries.find(b.getVignoble().getCountry());
				if(c != null) {
					value = c.toString();
				}
			}
		} else if(field == VINEYARD) {
			if(b.getVignoble() != null) {
				value = b.getVignoble().getName();
			}
		}else if(field == AOC) {
			if(b.getVignoble() != null && b.getVignoble().getAOC() != null) {
				value = b.getVignoble().getAOC();
			}
		}	else if(field == IGP) {
			if(b.getVignoble() != null && b.getVignoble().getIGP() != null) {
				value = b.getVignoble().getIGP();
			}
		}
		return value;
  }

	public static boolean hasSpecialHTMLCharacters(MyCellarFields field) {
		return field.equals(NAME) || field.equals(TYPE) || field.equals(COMMENT) || field.equals(PRICE) || field.equals(PLACE);
	}

	public static boolean isRealField(MyCellarFields field) {
		return !(field.equals(EMPTY) || field.equals(USELESS));
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
