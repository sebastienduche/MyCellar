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
import mycellar.general.IResource;
import mycellar.general.ResourceKey;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static mycellar.MyCellarUtils.isNullOrEmpty;
import static mycellar.core.text.MyCellarLabelManagement.getLabelWithProperty;
import static mycellar.general.ResourceKey.ADDVIN_COLOR;
import static mycellar.general.ResourceKey.MAIN_ALBUM;
import static mycellar.general.ResourceKey.MAIN_APPELLATIONAOC;
import static mycellar.general.ResourceKey.MAIN_APPELLATIONIGP;
import static mycellar.general.ResourceKey.MAIN_ARTIST;
import static mycellar.general.ResourceKey.MAIN_CAPACITYORSUPPORT;
import static mycellar.general.ResourceKey.MAIN_COMMENT;
import static mycellar.general.ResourceKey.MAIN_COMPOSER;
import static mycellar.general.ResourceKey.MAIN_COUNTRY;
import static mycellar.general.ResourceKey.MAIN_DISKCOUNT;
import static mycellar.general.ResourceKey.MAIN_DISKNUMBER;
import static mycellar.general.ResourceKey.MAIN_DURATION;
import static mycellar.general.ResourceKey.MAIN_EXTERNALID;
import static mycellar.general.ResourceKey.MAIN_FILE;
import static mycellar.general.ResourceKey.MAIN_ITEM;
import static mycellar.general.ResourceKey.MAIN_MATURITY;
import static mycellar.general.ResourceKey.MAIN_PRICE;
import static mycellar.general.ResourceKey.MAIN_RATING;
import static mycellar.general.ResourceKey.MAIN_STATUS;
import static mycellar.general.ResourceKey.MAIN_STORAGE;
import static mycellar.general.ResourceKey.MAIN_SUPPORT;
import static mycellar.general.ResourceKey.MAIN_USELESS;
import static mycellar.general.ResourceKey.MAIN_VINEYARD;
import static mycellar.general.ResourceKey.MAIN_YEAR;
import static mycellar.general.ResourceKey.MYCELLARFIELDS_COLUMN;
import static mycellar.general.ResourceKey.MYCELLARFIELDS_LINE;
import static mycellar.general.ResourceKey.MYCELLARFIELDS_NUMPLACE;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2016
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 3.0
 * @since 14/03/25
 */

public enum MyCellarFields {
  NAME(0, MAIN_ITEM, LabelProperty.SINGLE.withCapital()),
  YEAR(1, MAIN_YEAR),
  TYPE(2, MAIN_CAPACITYORSUPPORT),
  PLACE(3, MAIN_STORAGE),
  NUM_PLACE(4, MYCELLARFIELDS_NUMPLACE),
  LINE(5, MYCELLARFIELDS_LINE),
  COLUMN(6, MYCELLARFIELDS_COLUMN),
  PRICE(7, MAIN_PRICE),
  COMMENT(8, MAIN_COMMENT),
  MATURITY(9, MAIN_MATURITY),
  PARKER(10, MAIN_RATING),
  COLOR(11, ADDVIN_COLOR),
  COUNTRY(12, MAIN_COUNTRY),
  VINEYARD(13, MAIN_VINEYARD),
  AOC(14, MAIN_APPELLATIONAOC),
  IGP(15, MAIN_APPELLATIONIGP),
  STATUS(16, MAIN_STATUS),
  STYLE(17, MAIN_STATUS),
  COMPOSER(18, MAIN_COMPOSER),
  ARTIST(19, MAIN_ARTIST),
  SUPPORT(20, MAIN_SUPPORT),
  DURATION(21, MAIN_DURATION),
  DISK_NUMBER(22, MAIN_DISKNUMBER),
  DISK_COUNT(23, MAIN_DISKCOUNT),
  RATING(24, MAIN_RATING),
  FILE(25, MAIN_FILE),
  EXTERNAL_ID(26, MAIN_EXTERNALID),
  ALBUM(27, MAIN_ALBUM),

  // Pour l'import de donnees
  EMPTY(28, ResourceKey.EMPTY),
  USELESS(29, MAIN_USELESS);

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
  private final IResource keyLabel;
  private final LabelProperty labelProperty;

  MyCellarFields(int index, IResource keyLabel) {
    this.index = index;
    this.keyLabel = keyLabel;
    labelProperty = null;
  }

  MyCellarFields(int index, IResource keyLabel, LabelProperty labelProperty) {
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
    if (isNullOrEmpty(keyLabel.getKey())) {
      return "";
    }
    return getLabelWithProperty(keyLabel, labelProperty);
  }
}
