package mycellar.core;

import mycellar.Program;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.7
 * @since 09/04/21
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
    switch (error) {
      case INEXISTING_PLACE:
        return MessageFormat.format(Program.getError("MyCellarError.inexistingPlace"), place);
      case INEXISTING_NUM_PLACE:
        return MessageFormat.format(Program.getError("MyCellarError.inexistingNumPlace"), numLieu);
      case FULL_BOX:
        return MessageFormat.format(Program.getError("MyCellarError.fullCaisse"), numLieu);
      case INEXISTING_CELL:
        return MessageFormat.format(Program.getError("MyCellarError.inexistingCase"), place);
      case CELL_FULL:
        return Program.getError("MyCellarError.occupiedCase");
      default:
        return "";
    }
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
    return obj instanceof MyCellarError && myCellarObject.equals(((MyCellarError) obj).myCellarObject);
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
