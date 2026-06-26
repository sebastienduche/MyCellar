package mycellar.core.common;

import mycellar.Bouteille;
import mycellar.MyCellarUtils;
import mycellar.core.BottlesStatus;
import mycellar.core.common.bottle.BottleColor;
import mycellar.core.datas.jaxb.CountryJaxb;
import mycellar.core.datas.jaxb.CountryListJaxb;
import mycellar.general.IResource;
import mycellar.general.ResourceKey;

import java.util.Arrays;
import java.util.List;

import static mycellar.MyCellarUtils.isNullOrEmpty;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.ADDVIN_COLOR;
import static mycellar.general.ResourceKey.MAIN_APPELLATIONAOC;
import static mycellar.general.ResourceKey.MAIN_APPELLATIONIGP;
import static mycellar.general.ResourceKey.MAIN_CAPACITYORSUPPORT;
import static mycellar.general.ResourceKey.MAIN_COMMENT;
import static mycellar.general.ResourceKey.MAIN_COUNTRY;
import static mycellar.general.ResourceKey.MAIN_ITEM;
import static mycellar.general.ResourceKey.MAIN_MATURITY;
import static mycellar.general.ResourceKey.MAIN_PRICE;
import static mycellar.general.ResourceKey.MAIN_RATING;
import static mycellar.general.ResourceKey.MAIN_STATUS;
import static mycellar.general.ResourceKey.MAIN_STORAGE;
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
 * @version 3.4
 * @since 18/05/26
 */

public enum MyCellarFields {
  NAME(0, MAIN_ITEM),
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

  // Pour l'import de donnees
  EMPTY(28, ResourceKey.EMPTY),
  USELESS(29, MAIN_USELESS);

  private static final List<MyCellarFields> FIELDSFORIMPORT_WINE = Arrays.asList(
      NAME, YEAR, TYPE, PLACE, NUM_PLACE, LINE, COLUMN, PRICE, COMMENT, MATURITY, PARKER, COLOR,
      COUNTRY, VINEYARD, AOC, IGP
  );

  private static final List<MyCellarFields> FIELDS_WINE = Arrays.asList(
      NAME, YEAR, TYPE, PLACE, NUM_PLACE, LINE, COLUMN, PRICE, COMMENT, MATURITY, PARKER, COLOR,
      COUNTRY, VINEYARD, AOC, IGP, STATUS
  );
  private final int index;
  private final IResource keyLabel;

  MyCellarFields(int index, IResource keyLabel) {
    this.index = index;
    this.keyLabel = keyLabel;
  }

  public int getIndex() {
    return index;
  }

  public static String getValue(String field, Bouteille bouteille) {
    return getValue(valueOf(field), bouteille);
  }

  public static String getValue(MyCellarFields field, Bouteille bouteille) {
    if (bouteille == null) {
      return "";
    }
    if (field == NAME) {
      return bouteille.getNom();
    }
    if (field == YEAR) {
      return bouteille.getAnnee();
    }
    if (field == TYPE) {
      return bouteille.getKind();
    }
    if (field == PLACE) {
      return bouteille.getEmplacement();
    }
    if (field == NUM_PLACE) {
      return Integer.toString(bouteille.getNumLieu());
    }
    if (field == LINE) {
      return Integer.toString(bouteille.getLigne());
    }
    if (field == COLUMN) {
      return Integer.toString(bouteille.getColonne());
    }
    if (field == PRICE) {
      return bouteille.getPrix();
    }
    if (field == COMMENT) {
      return bouteille.getComment();
    }
    if (field == MATURITY) {
      return bouteille.getMaturity();
    }
    if (field == PARKER) {
      return bouteille.getParker();
    }
    if (field == COLOR) {
      return BottleColor.getColor(bouteille.getColor()).toString();
    }
    if (field == STATUS) {
      return BottlesStatus.getStatus(bouteille.getStatus()).toString();
    }
    if (field == COUNTRY) {
      if (bouteille.getVignoble() != null) {
        CountryJaxb c = CountryListJaxb.findByVignoble(bouteille.getVignoble()).orElse(null);
        if (c != null) {
          return c.toString();
        }
      }
    }
    if (field == VINEYARD) {
      if (bouteille.getVignoble() != null) {
        return bouteille.getVignoble().getName();
      }
    }
    if (field == AOC) {
      if (bouteille.getVignoble() != null && bouteille.getVignoble().getAOC() != null) {
        return bouteille.getVignoble().getAOC();
      }
    }
    if (field == IGP) {
      if (bouteille.getVignoble() != null && bouteille.getVignoble().getIGP() != null) {
        return bouteille.getVignoble().getIGP();
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
    return FIELDS_WINE;
  }

  public static List<MyCellarFields> getFieldsListForImportAndWorksheet() {
    return FIELDSFORIMPORT_WINE;
  }

  @Override
  public String toString() {
    if (isNullOrEmpty(keyLabel.getKey())) {
      return "";
    }
    return getLabel(keyLabel);
  }
}
