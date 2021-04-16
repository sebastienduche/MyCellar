package mycellar.core;

import mycellar.placesmanagement.Place;
import mycellar.placesmanagement.Rangement;
import org.w3c.dom.Element;

import java.math.BigDecimal;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.2
 * @since 16/04/21
 */
public interface IMyCellarObject<T> {
  void setId(int id);

  int getId();

  void setModified();

  String getEmplacement();

  boolean updateID();

  String getNom();

  int getAnneeInt();

  double getPriceDouble();

  Rangement getRangement();

  void setEmplacement(String nom);

  int getNumLieu();

  void setNumLieu(int i);

  Place getPlace();

  int getLigne();

  int getColonne();

  boolean isInTemporaryStock();

  boolean isInExistingPlace();

  String getAnnee();

  String getType();

  String getPrix();

  boolean hasPrice();

  BigDecimal getPrice();

  void setLigne(int row);

  void setColonne(int column);

  void updateStatus();

  String getStatus();

  String getLastModified();

  boolean hasNoStatus();

  void setCreated();

  String getComment();

  void setValue(MyCellarFields field, String value);

  void setStatus(String name);

  void setNom(String value);

  void setType(String value);

  void setAnnee(String value);

  T fromXmlElemnt(Element element);
}
