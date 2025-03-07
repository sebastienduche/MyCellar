package mycellar.core.common;

import mycellar.Bouteille;
import mycellar.Music;
import mycellar.MyCellarUtils;
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
import static mycellar.core.text.MyCellarLabelManagement.getLabelWithProperty;
import static mycellar.general.ResourceKey.MYCELLARFIELDS_COLUMN;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2016
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.9
 * @since 31/12/23
 */

public enum MyCellarFields {
  NAME(0, "Main.Item", LabelProperty.SINGLE.withCapital()),
  YEAR(1, "Main.Year"),
  TYPE(2, "Main.CapacityOrSupport"),
  PLACE(3, "Main.Storage"),
  NUM_PLACE(4, "MyCellarFields.NumPlace"),
  LINE(5, "MyCellarFields.Line"),
  COLUMN(6, MYCELLARFIELDS_COLUMN.getKey()),
  PRICE(7, "Main.Price"),
  COMMENT(8, "Main.Comment"),
  MATURITY(9, "Main.Maturity"),
  PARKER(10, "Main.Rating"),
  COLOR(11, "AddVin.Color"),
  COUNTRY(12, "Main.Country"),
  VINEYARD(13, "Main.Vineyard"),
  AOC(14, "Main.AppellationAOC"),
  IGP(15, "Main.AppellationIGP"),
  STATUS(16, "Main.Status"),
  STYLE(17, "Main.Style"),
  COMPOSER(18, "Main.Composer"),
  ARTIST(19, "Main.Artist"),
  SUPPORT(20, "Main.Support"),
  DURATION(21, "Main.Duration"),
  DISK_NUMBER(22, "Main.DiskNumber"),
  DISK_COUNT(23, "Main.DiskCount"),
  RATING(24, "Main.Rating"),
  FILE(25, "Main.File"),
  EXTERNAL_ID(26, "Main.ExternalId"),
  ALBUM(27, "Main.Album"),

  // Pour l'import de donnees
  EMPTY(28, ""),
  USELESS(29, "Main.Useless");

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
  private final int index;
  private final String keyLabel;
  private final LabelProperty labelProperty;

  MyCellarFields(int index, String keyLabel) {
    this.index = index;
    this.keyLabel = keyLabel;
    labelProperty = null;
  }

  MyCellarFields(int index, String keyLabel, LabelProperty labelProperty) {
    this.index = index;
    this.keyLabel = keyLabel;
    this.labelProperty = labelProperty;
  }

  public int getIndex() {
    return index;
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
      if (myCellarObject instanceof Bouteille bouteille) {
        return bouteille.getMaturity();
      }
    }
    if (field == PARKER) {
      if (myCellarObject instanceof Bouteille bouteille) {
        return bouteille.getParker();
      }
    }
    if (field == COLOR) {
      if (myCellarObject instanceof Bouteille bouteille) {
        return BottleColor.getColor(bouteille.getColor()).toString();
      }
    }
    if (field == STATUS) {
      return BottlesStatus.getStatus(myCellarObject.getStatus()).toString();
    }
    if (field == STYLE) {
      if (myCellarObject instanceof Music music) {
        return music.getGenre();
      }
    }
    if (field == COMPOSER) {
      if (myCellarObject instanceof Music music) {
        return music.getComposer();
      }
    }
    if (field == ARTIST) {
      if (myCellarObject instanceof Music music) {
        return music.getArtist();
      }
    }
    if (field == SUPPORT) {
      if (myCellarObject instanceof Music music) {
        return music.getMusicSupport().name();
      }
    }
    if (field == DURATION) {
      if (myCellarObject instanceof Music music) {
        return music.getDuration();
      }
    }
    if (field == FILE) {
      if (myCellarObject instanceof Music music) {
        return music.getFile();
      }
    }
    if (field == DISK_COUNT) {
      if (myCellarObject instanceof Music music) {
        return Integer.toString(music.getDiskCount());
      }
    }
    if (field == DISK_NUMBER) {
      if (myCellarObject instanceof Music music) {
        return Integer.toString(music.getDiskNumber());
      }
    }
    if (field == RATING) {
      if (myCellarObject instanceof Music music) {
        return Integer.toString(music.getRating());
      }
    }
    if (field == EXTERNAL_ID) {
      if (myCellarObject instanceof Music music) {
        return Integer.toString(music.getExternalId());
      }
    }
    if (field == ALBUM) {
      if (myCellarObject instanceof Music music) {
        return music.getAlbum();
      }
    }
    if (field == COUNTRY) {
      if (myCellarObject instanceof Bouteille bouteille) {
        if (bouteille.getVignoble() != null) {
          CountryJaxb c = CountryListJaxb.findbyId(bouteille.getVignoble().getCountry()).orElse(null);
          if (c != null) {
            return c.toString();
          }
        }
      }
    }
    if (field == VINEYARD) {
      if (myCellarObject instanceof Bouteille bouteille) {
        if (bouteille.getVignoble() != null) {
          return bouteille.getVignoble().getName();
        }
      }
    }
    if (field == AOC) {
      if (myCellarObject instanceof Bouteille bouteille) {
        if (bouteille.getVignoble() != null && bouteille.getVignoble().getAOC() != null) {
          return bouteille.getVignoble().getAOC();
        }
      }
    }
    if (field == IGP) {
      if (myCellarObject instanceof Bouteille bouteille) {
        if (bouteille.getVignoble() != null && bouteille.getVignoble().getIGP() != null) {
          return bouteille.getVignoble().getIGP();
        }
      }
    }
    return "";
  }

  public static boolean hasSpecialHTMLCharacters(MyCellarFields field) {
    return field != null && MyCellarUtils.isAnyOf(field, List.of(NAME, TYPE, COMMENT, PRICE, PLACE));
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
    return getLabelWithProperty(keyLabel, labelProperty);
  }
}
