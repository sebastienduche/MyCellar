package mycellar.launcher;

import com.sebastienduche.Server;

/**
 *
 * Titre : Cave à vin
 * Description : Votre description
 * Copyright : Copyright (c) 2011
 * Société : Seb Informatique
 * @author Sébastien Duché
 * @version 3.2
 * @since 08/07/21
 */

public class MyCellarServer extends Server {

	private static final MyCellarServer INSTANCE = new MyCellarServer();

	private MyCellarServer() {
		super("https://github.com/sebastienduche/MyCellar/raw/master/Build/", "MyCellarVersion.txt", "MyCellar", "MyCellarDebug");
	}

	public static MyCellarServer getInstance() {
		return INSTANCE;
	}

	public static void Debug(String sText) {
		getInstance().debug(sText);
	}

}
