package mycellar.core;

import mycellar.core.text.MyCellarLabelManagement;

import java.util.Objects;

import static mycellar.general.ResourceErrorKey.MYCELLARERROR_FULLCAISSE;
import static mycellar.general.ResourceErrorKey.MYCELLARERROR_INEXISTINGCASE;
import static mycellar.general.ResourceErrorKey.MYCELLARERROR_INEXISTINGNUMPLACE;
import static mycellar.general.ResourceErrorKey.MYCELLARERROR_INEXISTINGPLACE;
import static mycellar.general.ResourceErrorKey.MYCELLARERROR_OCCUPIEDCASE;


/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.0
 * @since 21/03/25
 */

public class MyCellarError {

  private final ID error;
  private final IMyCellarObject myCellarObject;
  private final String place;
  private final int numLieu;
  private boolean status;
  private boolean solved;

  public MyCellarError(ID error, IMyCellarObject myCellarObject, String place, int numLieu) {
    this.error = error;
    this.myCellarObject = myCellarObject;
    this.place = place;
    this.numLieu = numLieu;
    status = false;
    solved = false;
  }

  public MyCellarError(ID error, IMyCellarObject myCellarObject, String place) {
    this.error = error;
    this.myCellarObject = myCellarObject;
    this.place = place;
    numLieu = -1;
    status = false;
    solved = false;
  }

  public MyCellarError(ID error, IMyCellarObject myCellarObject) {
    this.error = error;
    this.myCellarObject = myCellarObject;
    place = "";
    numLieu = -1;
    status = false;
    solved = false;
  }

  public String getErrorMessage() {
    return switch (error) {
      case INEXISTING_PLACE ->
          MyCellarLabelManagement.getError(MYCELLARERROR_INEXISTINGPLACE, place);
      case INEXISTING_NUM_PLACE ->
          MyCellarLabelManagement.getError(MYCELLARERROR_INEXISTINGNUMPLACE, numLieu);
      case FULL_BOX -> MyCellarLabelManagement.getError(MYCELLARERROR_FULLCAISSE, numLieu);
      case INEXISTING_CELL ->
          MyCellarLabelManagement.getError(MYCELLARERROR_INEXISTINGCASE, place);
      case CELL_FULL -> MyCellarLabelManagement.getError(MYCELLARERROR_OCCUPIEDCASE);
    };
  }

  public ID getError() {
    return error;
  }

  public IMyCellarObject getMyCellarObject() {
    return myCellarObject;
  }

  public boolean isStatus() {
    return status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public boolean isSolved() {
    return solved;
  }

  public void setSolved(boolean solved) {
    this.solved = solved;
  }

  public boolean isNotSolved() {
    return !solved;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof MyCellarError err && myCellarObject.equals(err.myCellarObject);
  }

  @Override
  public int hashCode() {
    return Objects.hash(error, status, myCellarObject);
  }

  public enum ID {
    INEXISTING_PLACE,
    INEXISTING_NUM_PLACE,
    FULL_BOX,
    INEXISTING_CELL,
    CELL_FULL
  }
}
