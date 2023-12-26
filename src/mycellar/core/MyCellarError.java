package mycellar.core;

import mycellar.core.text.MyCellarLabelManagement;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.8
 * @since 26/12/23
 */

public class MyCellarError {

  private final ID error;
  private final MyCellarObject myCellarObject;
  private final String place;
  private final int numLieu;
  private boolean status;
  private boolean solved;

  public MyCellarError(ID error, MyCellarObject myCellarObject, String place, int numLieu) {
    this.error = error;
    this.myCellarObject = myCellarObject;
    this.place = place;
    this.numLieu = numLieu;
    status = false;
    solved = false;
  }

  public MyCellarError(ID error, MyCellarObject myCellarObject, String place) {
    this.error = error;
    this.myCellarObject = myCellarObject;
    this.place = place;
    numLieu = -1;
    status = false;
    solved = false;
  }

  public MyCellarError(ID error, MyCellarObject myCellarObject) {
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
          MessageFormat.format(MyCellarLabelManagement.getError("MyCellarError.inexistingPlace"), place);
      case INEXISTING_NUM_PLACE ->
          MessageFormat.format(MyCellarLabelManagement.getError("MyCellarError.inexistingNumPlace"), numLieu);
      case FULL_BOX -> MessageFormat.format(MyCellarLabelManagement.getError("MyCellarError.fullCaisse"), numLieu);
      case INEXISTING_CELL ->
          MessageFormat.format(MyCellarLabelManagement.getError("MyCellarError.inexistingCase"), place);
      case CELL_FULL -> MyCellarLabelManagement.getError("MyCellarError.occupiedCase");
    };
  }

  public ID getError() {
    return error;
  }

  public MyCellarObject getMyCellarObject() {
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
