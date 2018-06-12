package mycellar.core;

import mycellar.vignobles.Appelation;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2017</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.1
 * @since 21/01/17
 */
class IGPItem {
	private final Appelation appellation;

	public IGPItem(Appelation appellation) {
		this.appellation = appellation;
	}

	@Override
	public String toString() {
		return appellation.getIGP();
	}
}
