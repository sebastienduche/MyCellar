package mycellar.core;

import mycellar.Bouteille;
import mycellar.Program;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.4
 * @since 09/03/18
 */

public class MyCellarError {

	private final int error;
	private boolean status;
	private final Bouteille bottle;
	private final String place;
	private final int numLieu;
	
	public MyCellarError(int error, Bouteille bottle, String place, int numLieu) {
		this.error = error;
		this.bottle = bottle;
		this.place = place;
		this.numLieu = numLieu;
		setStatus(false);
	}

	public MyCellarError(int error, Bouteille bottle, String place) {
		this.error = error;
		this.bottle = bottle;
		this.place = place;
		numLieu = -1;
		setStatus(false);
	}

	public MyCellarError(int error, Bouteille bottle) {
		this.error = error;
		this.bottle = bottle;
		place = "";
		numLieu = -1;
		setStatus(false);
	}
	
	public String getErrorMessage() {
		switch (error) {
		case 1:
			return MessageFormat.format(Program.getError("MyCellarError.inexistingPlace"), place);
		case 2:
			return MessageFormat.format(Program.getError("MyCellarError.inexistingNumPlace"), numLieu);
		case 3:
			return MessageFormat.format(Program.getError("MyCellarError.fullCaisse"), numLieu);
		case 4:
			return MessageFormat.format(Program.getError("MyCellarError.inexistingCase"), place);
		case 5:
			return Program.getError("MyCellarError.occupiedCase");
		}
		return "";
	}

	public int getError() {
		return error;
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

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MyCellarError) {
			return bottle.equals(((MyCellarError)obj).bottle);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(error, status, bottle);
	}
}
