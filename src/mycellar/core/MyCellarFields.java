package mycellar.core;

import mycellar.Bouteille;
import mycellar.Music;
import mycellar.Program;
import mycellar.core.datas.jaxb.CountryJaxb;
import mycellar.core.datas.jaxb.CountryListJaxb;

import java.util.Arrays;
import java.util.List;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2016</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.6
 * @since 16/04/21
 */

public enum MyCellarFields {
	NAME(Program.getLabel("Main.Item", LabelProperty.SINGLE.withCapital())),
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
	STATUS(Program.getLabel("Main.Status")),
	STYLE(Program.getLabel("Main.Style")),
	COMPOSER(Program.getLabel("Main.Composer")),
	ARTIST(Program.getLabel("Main.Artist")),
	SUPPORT(Program.getLabel("Main.Support")),

	// Pour l'import de donnees
	EMPTY(""),
	USELESS(Program.getLabel("Infos271"));
	
	private final String label;
	
	MyCellarFields(String label) {
		this.label = label;
	}

  public static String getValue(MyCellarFields field, IMyCellarObject myCellarObject) {
		if (myCellarObject == null) {
			return "";
		}
		Program.throwNotImplementedForMusic(myCellarObject);
		String value = "";
		if (field == NAME) {
			value = myCellarObject.getNom();
		} else if (field == YEAR) {
			value = myCellarObject.getAnnee();
		} else if (field == TYPE) {
			value = myCellarObject.getType();
		} else if (field == PLACE) {
			value = myCellarObject.getEmplacement();
		} else if (field == NUM_PLACE) {
			value = Integer.toString(myCellarObject.getNumLieu());
		} else if (field == LINE) {
			value = Integer.toString(myCellarObject.getLigne());
		} else if (field == COLUMN) {
			value = Integer.toString(myCellarObject.getColonne());
		} else if (field == PRICE) {
			value = myCellarObject.getPrix();
		} else if (field == COMMENT) {
			value = myCellarObject.getComment();
		} else if (field == MATURITY) {
			if (myCellarObject instanceof Bouteille) {
				value = ((Bouteille) myCellarObject).getMaturity();
			}
		} else if (field == PARKER) {
			if (myCellarObject instanceof Bouteille) {
				value = ((Bouteille) myCellarObject).getParker();
			}
		} else if (field == COLOR) {
			if (myCellarObject instanceof Bouteille) {
				value = ((Bouteille) myCellarObject).getColor();
			}
		} else if (field == STATUS) {
			value = myCellarObject.getStatus();
		} else if (field == STYLE) {
			if (myCellarObject instanceof Music) {
				value = ((Music) myCellarObject).getGenre();
			}
		} else if (field == COMPOSER) {
			if (myCellarObject instanceof Music) {
				value = ((Music) myCellarObject).getComposer();
			}
		} else if (field == ARTIST) {
			if (myCellarObject instanceof Music) {
				value = ((Music) myCellarObject).getArtist();
			}
		} else if (field == SUPPORT) {
			if (myCellarObject instanceof Music) {
				value = ((Music) myCellarObject).getMusicSupport().name();
			}
		} else if (field == COUNTRY) {
			if (myCellarObject instanceof Bouteille) {
				Bouteille bouteille = (Bouteille) myCellarObject;
				if (bouteille.getVignoble() != null) {
					CountryJaxb c = CountryListJaxb.findbyId(bouteille.getVignoble().getCountry()).orElse(null);
					if (c != null) {
						value = c.toString();
					}
				}
			}
		} else if (field == VINEYARD) {
			if (myCellarObject instanceof Bouteille) {
				Bouteille bouteille = (Bouteille) myCellarObject;
				if (bouteille.getVignoble() != null) {
					value = bouteille.getVignoble().getName();
				}
			}
		}else if (field == AOC) {
			if (myCellarObject instanceof Bouteille) {
				Bouteille bouteille = (Bouteille) myCellarObject;
					if (bouteille.getVignoble() != null && bouteille.getVignoble().getAOC() != null) {
						value = bouteille.getVignoble().getAOC();
					}
				}
		}	else if (field == IGP) {
			if (myCellarObject instanceof Bouteille) {
				Bouteille bouteille = (Bouteille) myCellarObject;
				if (bouteille.getVignoble() != null && bouteille.getVignoble().getIGP() != null) {
					value = bouteille.getVignoble().getIGP();
				}
			}
		}
		return value;
  }

	public static boolean hasSpecialHTMLCharacters(MyCellarFields field) {
		return field != null && (field.equals(NAME) || field.equals(TYPE) || field.equals(COMMENT) || field.equals(PRICE) || field.equals(PLACE));
	}

	public static boolean isRealField(MyCellarFields field) {
		return field != null && !(field.equals(EMPTY) || field.equals(USELESS));
	}

  @Override
	public String toString() {
		return label;
	}
	
	private static final List<MyCellarFields> FIELDSFORIMPORT_WINE = Arrays.asList(
			NAME, YEAR, TYPE, PLACE, NUM_PLACE, LINE, COLUMN, PRICE, COMMENT, MATURITY, PARKER, COLOR,
			COUNTRY, VINEYARD, AOC, IGP
	);

	private static final List<MyCellarFields> FIELDSFORIMPORT_MUSIC = Arrays.asList(
			NAME, YEAR, TYPE, PLACE, NUM_PLACE, LINE, COLUMN, PRICE, COMMENT, ARTIST, COMPOSER, STYLE, SUPPORT
	);

	private static final List<MyCellarFields> FIELDS_WINE = Arrays.asList(
			NAME, YEAR, TYPE, PLACE, NUM_PLACE, LINE, COLUMN, PRICE, COMMENT, MATURITY, PARKER, COLOR,
			COUNTRY, VINEYARD, AOC, IGP, STATUS
	);

	private static final List<MyCellarFields> FIELDS_MUSIC = Arrays.asList(
			NAME, YEAR, TYPE, PLACE, NUM_PLACE, LINE, COLUMN, PRICE, COMMENT, STATUS, ARTIST, COMPOSER, STYLE, SUPPORT
	);

	public static List<MyCellarFields> getFieldsList() {
		if (Program.isWineType()) {
			return FIELDS_WINE;
		}
		if (Program.isMusicType()) {
			return FIELDS_MUSIC;
		}
		Program.throwNotImplementedForMusic(new Music());
		return null;
	}

	public static List<MyCellarFields> getFieldsListForImportAndWorksheet() {
		if (Program.isWineType()) {
			return FIELDSFORIMPORT_WINE;
		}
		if (Program.isMusicType()) {
			return FIELDSFORIMPORT_MUSIC;
		}
		Program.throwNotImplementedForMusic(new Music());
		return null;
	}
}
