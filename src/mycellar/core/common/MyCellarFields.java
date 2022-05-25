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

import static mycellar.MyCellarUtils.isNullOrEmpty;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2016
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.6
 * @since 25/05/22
 */

public enum MyCellarFields {
  NAME("Main.Item", LabelProperty.SINGLE.withCapital()),
  YEAR("Main.Year"),
  TYPE("Main.CapacityOrSupport"),
  PLACE("Main.Storage"),
  NUM_PLACE("MyCellarFields.NumPlace"),
  LINE("MyCellarFields.Line"),
  COLUMN("MyCellarFields.Column"),
  PRICE("Main.Price"),
  COMMENT("Main.Comment"),
  MATURITY("Main.Maturity"),
  PARKER("Main.Rating"),
  COLOR("AddVin.Color"),
  COUNTRY("Main.Country"),
  VINEYARD("Main.Vineyard"),
  AOC("Main.AppelationAOC"),
  IGP("Main.AppelationIGP"),
  STATUS("Main.Status"),
  STYLE("Main.Style"),
  COMPOSER("Main.Composer"),
  ARTIST("Main.Artist"),
  SUPPORT("Main.Support"),
  DURATION("Main.Duration"),
  DISK_NUMBER("Main.DiskNumber"),
  DISK_COUNT("Main.DiskCount"),
  RATING("Main.Rating"),
  FILE("Main.File"),
  EXTERNAL_ID("Main.ExternalId"),
  ALBUM("Main.Album"),

  // Pour l'import de donnees
  EMPTY(""),
  USELESS(getLabel("Main.Useless"));

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
  private final String keyLabel;
  private final LabelProperty labelProperty;

  MyCellarFields(String keyLabel) {
    this.keyLabel = keyLabel;
    labelProperty = null;
  }

  MyCellarFields(String keyLabel, LabelProperty labelProperty) {
    this.keyLabel = keyLabel;
    this.labelProperty = labelProperty;
  }

  public static String getValue(String field, IMyCellarObject myCellarObject) {
    return getValue(valueOf(field), myCellarObject);
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
    if (isNullOrEmpty(keyLabel)) {
      return "";
    }
    return getLabel(keyLabel, labelProperty);
  }
}
