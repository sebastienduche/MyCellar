package mycellar.core;

import mycellar.core.common.MyCellarFields;
import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.places.IBasicPlace;

import java.math.BigDecimal;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2021
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.5
 * @since 27/05/22
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

  IBasicPlace getRangement();

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
