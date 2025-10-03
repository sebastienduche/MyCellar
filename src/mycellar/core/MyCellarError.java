package mycellar.core;

import mycellar.Bouteille;
import mycellar.core.text.MyCellarLabelManagement;
import mycellar.general.ResourceErrorKey;

import java.util.Objects;

import static mycellar.general.ResourceErrorKey.ERROR_FULLCAISSE;
import static mycellar.general.ResourceErrorKey.ERROR_INEXISTINGCASE;
import static mycellar.general.ResourceErrorKey.ERROR_INEXISTINGNUMPLACE;
import static mycellar.general.ResourceErrorKey.ERROR_INEXISTINGPLACE;
import static mycellar.general.ResourceErrorKey.ERROR_OCCUPIEDCASE;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.3
 * @since 03/10/25
 */

public class MyCellarError {

  private final ResourceErrorKey error;
  private final Bouteille bottle;
  private final String place;
  private final int numLieu;
  private boolean status;
  private boolean solved;

  public MyCellarError(ResourceErrorKey error, Bouteille bottle, String place, int numLieu) {
    this.error = error;
    this.bottle = bottle;
    this.place = place;
    this.numLieu = numLieu;
    status = false;
    solved = false;
  }

  public MyCellarError(ResourceErrorKey error, Bouteille bottle, String place) {
    this.error = error;
    this.bottle = bottle;
    this.place = place;
    numLieu = -1;
    status = false;
    solved = false;
  }

  public String getErrorMessage() {
    return switch (error) {
      case ERROR_INEXISTINGPLACE -> MyCellarLabelManagement.getError(ERROR_INEXISTINGPLACE, place);
      case ERROR_INEXISTINGNUMPLACE -> MyCellarLabelManagement.getError(ERROR_INEXISTINGNUMPLACE, numLieu);
      case ERROR_FULLCAISSE -> MyCellarLabelManagement.getError(ERROR_FULLCAISSE, numLieu);
      case ERROR_INEXISTINGCASE -> MyCellarLabelManagement.getError(ERROR_INEXISTINGCASE, place);
      case ERROR_OCCUPIEDCASE -> MyCellarLabelManagement.getError(ERROR_OCCUPIEDCASE);
      default -> throw new IllegalStateException("Unexpected value: " + error);
    };
  }

  public Bouteille getBottle() {
    return bottle;
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
    return obj instanceof MyCellarError err && bottle.equals(err.bottle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(error, status, bottle);
  }

}
