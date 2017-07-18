package mycellar.core;

import java.util.LinkedList;

import mycellar.Bouteille;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 18/07/17
 */

public class MyCellarError {

	private LinkedList<String> messages = new LinkedList<String>();
	private int error;
	
	private Bouteille bottle;
	
	public MyCellarError(int error, String message) {
		this.error = error;
		messages.add(message);
	}
	
	public MyCellarError(int error, Bouteille bottle) {
		this.error = error;
		this.bottle = bottle;
	}
	
	public MyCellarError(int error, String message, String message2) {
		this.error = error;
		messages.add(message);
		messages.add(message2);
	}

	public MyCellarError(int error, LinkedList<String> messages) {
		this.messages = messages;
		this.error = error;
	}

	public LinkedList<String> getMessages() {
		return messages;
	}
	
	public String getFirstMessage() {
		return messages.getFirst();
	}

	public int getError() {
		return error;
	}
	
	public Bouteille getBottle() {
		return bottle;
	}
}
