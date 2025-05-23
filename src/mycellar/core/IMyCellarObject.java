package mycellar.core;

import mycellar.core.common.MyCellarFields;
import mycellar.core.exceptions.MyCellarException;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.placesmanagement.places.PlacePosition;
import org.w3c.dom.Element;

import java.math.BigDecimal;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2021
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.0
 * @since 21/03/25
 */
public interface IMyCellarObject {
  int getId();

  void setId(int id);

  void setModified();

  String getEmplacement();

  void setEmplacement(String nom);

  boolean updateID();

  String getNom();

  void setNom(String value);

  int getAnneeInt();

  double getPriceDouble();

  AbstractPlace getAbstractPlace();

  int getNumLieu();

  void setNumLieu(int i);

  PlacePosition getPlacePosition();

  int getLigne();

  void setLigne(int row);

  int getColonne();

  void setColonne(int column);

  boolean isInTemporaryStock();

  boolean isInExistingPlace();

  String getAnnee();

  void setAnnee(String value);

  String getKind();

  void setKind(String value);

  String getPrix();

  boolean hasPrice();

  BigDecimal getPrice();

  void updateStatus();

  String getStatus();

  void setStatus(String name);

  String getLastModified();

  boolean hasNoStatus();

  void setCreated();

  String getComment();

  void setValue(MyCellarFields field, String value);

  void validateValue(MyCellarFields field, String value) throws MyCellarException;

  boolean isNonVintage();

  IMyCellarObject fromXmlElement(Element element);

  void update(IMyCellarObject object);

  static void assertObjectType(IMyCellarObject myCellarObject, Class<?> aClass) {
    if (!aClass.isInstance(myCellarObject)) {
      throw new ClassCastException("Invalid class cast: " + aClass);
    }
  }

   default boolean equalsValue(String value, String other) {
    if (value == null) {
      return other != null;
    } else {
      return !value.equals(other);
    }
  }
}
