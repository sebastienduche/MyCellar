package mycellar.core.common;

import mycellar.Bouteille;
import mycellar.Music;
import mycellar.Program;
import mycellar.core.BottlesStatus;
import mycellar.core.IMyCellarObject;
import mycellar.core.common.bottle.BottleColor;
import mycellar.core.datas.jaxb.CountryJaxb;
import mycellar.core.datas.jaxb.CountryListJaxb;
import mycellar.core.text.LabelProperty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * <p>Titre : Cave &agrave; vin
 * <p>Description : Votre description
 * <p>Copyright : Copyright (c) 2016
 * <p>Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.1
 * @since 14/09/21
 */

public enum MyCellarFields {
  NAME(getLabel("Main.Item", LabelProperty.SINGLE.withCapital())),
  YEAR(getLabel("Infos189")),
  TYPE(getLabel("Infos134")),
  PLACE(getLabel("Infos217")),
  NUM_PLACE(getLabel("Infos082")),
  LINE(getLabel("Infos028")),
  COLUMN(getLabel("Infos083")),
  PRICE(getLabel("Infos135")),
  COMMENT(getLabel("Infos137")),
  MATURITY(getLabel("Infos391")),
  PARKER(getLabel("Infos392")),
  COLOR(getLabel("AddVin.Color")),
  COUNTRY(getLabel("Main.Country")),
  VINEYARD(getLabel("Main.Vignoble")),
  AOC(getLabel("Main.AppelationAOC")),
  IGP(getLabel("Main.AppelationIGP")),
  STATUS(getLabel("Main.Status")),
  STYLE(getLabel("Main.Style")),
  COMPOSER(getLabel("Main.Composer")),
  ARTIST(getLabel("Main.Artist")),
  SUPPORT(getLabel("Main.Support")),
  DURATION(getLabel("Main.Duration")),
  DISK_NUMBER(getLabel("Main.DiskNumber")),
  DISK_COUNT(getLabel("Main.DiskCount")),
  RATING(getLabel("Main.Rating")),
  FILE(getLabel("Main.File")),
  EXTERNAL_ID(getLabel("Main.ExternalId")),
  ALBUM(getLabel("Main.Album")),

  // Pour l'import de donnees
  EMPTY(""),
  USELESS(getLabel("Infos271"));

  private static final List<MyCellarFields> FIELDSFORIMPORT_WINE = Arrays.asList(
      NAME, YEAR, TYPE, PLACE, NUM_PLACE, LINE, COLUMN, PRICE, COMMENT, MATURITY, PARKER, COLOR,
      COUNTRY, VINEYARD, AOC, IGP
  );
  private static final List<MyCellarFields> FIELDSFORIMPORT_MUSIC = Arrays.asList(
      NAME, YEAR, PLACE, NUM_PLACE, LINE, COLUMN, PRICE, COMMENT, ARTIST, COMPOSER, STYLE, SUPPORT, DURATION, EXTERNAL_ID, ALBUM
  );
  private static final List<MyCellarFields> FIELDS_WINE = Arrays.asList(
      NAME, YEAR, TYPE, PLACE, NUM_PLACE, LINE, COLUMN, PRICE, COMMENT, MATURITY, PARKER, COLOR,
      COUNTRY, VINEYARD, AOC, IGP, STATUS
  );
  private static final List<MyCellarFields> FIELDS_MUSIC = Arrays.asList(
      NAME, YEAR, PLACE, NUM_PLACE, LINE, COLUMN, PRICE, COMMENT, STATUS, ARTIST, COMPOSER, STYLE, SUPPORT, DURATION, EXTERNAL_ID, ALBUM
  );
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
    if (field == FILE) {
      if (myCellarObject instanceof Music) {
        return ((Music) myCellarObject).getFile();
      }
    }
    if (field == DISK_COUNT) {
      if (myCellarObject instanceof Music) {
        return Integer.toString(((Music) myCellarObject).getDiskCount());
      }
    }
    if (field == DISK_NUMBER) {
      if (myCellarObject instanceof Music) {
        return Integer.toString(((Music) myCellarObject).getDiskNumber());
      }
    }
    if (field == RATING) {
      if (myCellarObject instanceof Music) {
        return Integer.toString(((Music) myCellarObject).getRating());
      }
    }
    if (field == EXTERNAL_ID) {
      if (myCellarObject instanceof Music) {
        return Integer.toString(((Music) myCellarObject).getExternalId());
      }
    }
    if (field == ALBUM) {
      if (myCellarObject instanceof Music) {
        return ((Music) myCellarObject).getAlbum();
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

  public static List<MyCellarFields> getFieldsList() {
    if (Program.isWineType()) {
      return FIELDS_WINE;
    }
    if (Program.isMusicType()) {
      return FIELDS_MUSIC;
    }
    Program.throwNotImplementedForNewType();
    return Collections.emptyList();
  }

  public static List<MyCellarFields> getFieldsListForImportAndWorksheet() {
    if (Program.isWineType()) {
      return FIELDSFORIMPORT_WINE;
    }
    if (Program.isMusicType()) {
      return FIELDSFORIMPORT_MUSIC;
    }
    Program.throwNotImplementedForNewType();
    return Collections.emptyList();
  }

  @Override
  public String toString() {
    return label;
  }
}
