package mycellar.core.common;

import mycellar.Bouteille;
import mycellar.Music;
import mycellar.Program;
import mycellar.core.BottlesStatus;
import mycellar.core.IMyCellarObject;
import mycellar.core.LabelProperty;
import mycellar.core.common.bottle.BottleColor;
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
 * @version 1.9
 * @since 23/04/21
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
	DURATION(Program.getLabel("Main.Duration")),

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
		if (field == NAME) {
			return myCellarObject.getNom();
		}
		if (field == YEAR) {
			return myCellarObject.getAnnee();
		}
		if (field == TYPE) {
			return myCellarObject.getKind();
		}
		if (field == PLACE) {
			return myCellarObject.getEmplacement();
		}
		if (field == NUM_PLACE) {
			return Integer.toString(myCellarObject.getNumLieu());
		}
		if (field == LINE) {
			return Integer.toString(myCellarObject.getLigne());
		}
		if (field == COLUMN) {
			return Integer.toString(myCellarObject.getColonne());
		}
		if (field == PRICE) {
			return myCellarObject.getPrix();
		}
		if (field == COMMENT) {
			return myCellarObject.getComment();
		}
		if (field == MATURITY) {
			if (myCellarObject instanceof Bouteille) {
				return ((Bouteille) myCellarObject).getMaturity();
			}
		}
		if (field == PARKER) {
			if (myCellarObject instanceof Bouteille) {
				return ((Bouteille) myCellarObject).getParker();
			}
		}
		if (field == COLOR) {
			if (myCellarObject instanceof Bouteille) {
				return BottleColor.getColor(((Bouteille) myCellarObject).getColor()).toString();
			}
		}
		if (field == STATUS) {
			return BottlesStatus.getStatus(myCellarObject.getStatus()).toString();
		}
		if (field == STYLE) {
			if (myCellarObject instanceof Music) {
				return ((Music) myCellarObject).getGenre();
			}
		}
		if (field == COMPOSER) {
			if (myCellarObject instanceof Music) {
				return ((Music) myCellarObject).getComposer();
			}
		}
		if (field == ARTIST) {
			if (myCellarObject instanceof Music) {
				return ((Music) myCellarObject).getArtist();
			}
		}
		if (field == SUPPORT) {
			if (myCellarObject instanceof Music) {
				return ((Music) myCellarObject).getMusicSupport().name();
			}
		}
		if (field == DURATION) {
			if (myCellarObject instanceof Music) {
				return ((Music) myCellarObject).getDuration();
			}
		}
		if (field == COUNTRY) {
			if (myCellarObject instanceof Bouteille) {
				Bouteille bouteille = (Bouteille) myCellarObject;
				if (bouteille.getVignoble() != null) {
					CountryJaxb c = CountryListJaxb.findbyId(bouteille.getVignoble().getCountry()).orElse(null);
					if (c != null) {
						return c.toString();
					}
				}
			}
		}
		if (field == VINEYARD) {
			if (myCellarObject instanceof Bouteille) {
				Bouteille bouteille = (Bouteille) myCellarObject;
				if (bouteille.getVignoble() != null) {
					return bouteille.getVignoble().getName();
				}
			}
		}
		if (field == AOC) {
			if (myCellarObject instanceof Bouteille) {
				Bouteille bouteille = (Bouteille) myCellarObject;
				if (bouteille.getVignoble() != null && bouteille.getVignoble().getAOC() != null) {
					return bouteille.getVignoble().getAOC();
				}
			}
		}
		if (field == IGP) {
			if (myCellarObject instanceof Bouteille) {
				Bouteille bouteille = (Bouteille) myCellarObject;
				if (bouteille.getVignoble() != null && bouteille.getVignoble().getIGP() != null) {
					return bouteille.getVignoble().getIGP();
				}
			}
		}
		return "";
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
			NAME, YEAR, PLACE, NUM_PLACE, LINE, COLUMN, PRICE, COMMENT, ARTIST, COMPOSER, STYLE, SUPPORT, DURATION
	);

	private static final List<MyCellarFields> FIELDS_WINE = Arrays.asList(
			NAME, YEAR, TYPE, PLACE, NUM_PLACE, LINE, COLUMN, PRICE, COMMENT, MATURITY, PARKER, COLOR,
			COUNTRY, VINEYARD, AOC, IGP, STATUS
	);

	private static final List<MyCellarFields> FIELDS_MUSIC = Arrays.asList(
			NAME, YEAR, PLACE, NUM_PLACE, LINE, COLUMN, PRICE, COMMENT, STATUS, ARTIST, COMPOSER, STYLE, SUPPORT, DURATION
	);

	public static List<MyCellarFields> getFieldsList() {
		if (Program.isWineType()) {
			return FIELDS_WINE;
		}
		if (Program.isMusicType()) {
			return FIELDS_MUSIC;
		}
		Program.throwNotImplementedIfNotFor(new Music(), Bouteille.class);
		return null;
	}

	public static List<MyCellarFields> getFieldsListForImportAndWorksheet() {
		if (Program.isWineType()) {
			return FIELDSFORIMPORT_WINE;
		}
		if (Program.isMusicType()) {
			return FIELDSFORIMPORT_MUSIC;
		}
		Program.throwNotImplementedIfNotFor(new Music(), Bouteille.class);
		return null;
	}
}
