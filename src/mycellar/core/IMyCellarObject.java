package mycellar.core;

import mycellar.core.common.MyCellarFields;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;

import java.math.BigDecimal;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.4
 * @since 23/04/21
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

  Rangement getRangement();

  int getNumLieu();

  void setNumLieu(int i);

  Place getPlace();

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

  boolean isNonVintage();
}
