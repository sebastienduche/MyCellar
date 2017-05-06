/* *******************************************************
 * � 1996-2009 HR Access Solutions. All rights reserved
 * ******************************************************/

package mycellar;

import java.util.EventObject;

/**
 * An event triggered by a tab when it's about to close.
 * 
 * @author Fran�ois RITALY / HR Access Solutions
 */
public class TabEvent extends EventObject {

	private static final long serialVersionUID = -7541555469417472595L;

	/**
	 * Creates a new instance of TabEvent.
	 * 
	 * @param source
	 */
	public TabEvent(Object source) {
		super(source);
	}
}