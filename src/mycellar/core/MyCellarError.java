package mycellar.core;

import java.text.MessageFormat;

import mycellar.Bouteille;
import mycellar.Program;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.3
 * @since 20/07/17
 */

public class MyCellarError {

	private int error;
	private boolean status;
	private Bouteille bottle;
	
	public MyCellarError(int error, Bouteille bottle) {
		this.error = error;
		this.bottle = bottle;
		this.setStatus(false);
	}
	
	public String getErrorMessage() {
		switch (error) {
		case 1:
			return MessageFormat.format(Program.getError("MyCellarError.inexistingPlace"), bottle.getEmplacement());
		case 2:
			return MessageFormat.format(Program.getError("MyCellarError.inexistingNumPlace"), bottle.getNumLieu());
		case 3:
			return MessageFormat.format(Program.getError("MyCellarError.fullCaisse"), bottle.getNumLieu());
		case 4:
			return MessageFormat.format(Program.getError("MyCellarError.inexistingCase"), bottle.getEmplacement());
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
			return bottle == ((MyCellarError)obj).bottle;
		}
		return false;
	}
}
