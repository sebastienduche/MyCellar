package mycellar.core;

public class IdGenerator {

	private static long localID = 0; // Used for all temp ids (jaxb)
	
	public static long generateID() {
		return localID++;
	}
}
