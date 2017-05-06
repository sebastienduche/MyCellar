package mycellar.core;

import java.util.LinkedList;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.1
 * @since 16/04/14
 */

public class MyCellarError {

	private LinkedList<String> messages = new LinkedList<String>();
	private int error;
	
	public MyCellarError(int error, String message) {
		this.error = error;
		messages.add(message);
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
}
