/* *******************************************************
 * @ 1996-2009 HR Access Solutions. All rights reserved
 * ******************************************************/

package mycellar;

import org.apache.commons.lang.Validate;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2012</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.5
 * @since 02/03/18
 */

/**
 * A {@link JComboBox} with an auto-populating history feature. 
 * 
 * @author Francois RITALY / HR Access Solutions
 */
public class JCompletionComboBox extends JComboBox {

	private static final long serialVersionUID = -7209698149395632434L;
	

	/**
	 * Custom {@link Document} to implement the history feature.
	 * 
	 * @author Fran�ois RITALY / HR Access Solutions
	 */
	private static final class ComboDocument extends PlainDocument implements
			ActionListener, KeyListener, FocusListener {

		private static final long serialVersionUID = 2104011788354384089L;

		// flag to indicate if setSelectedItem has been called
		// subsequent calls to remove/insertString should be ignored
		private boolean selecting = false;

//		private boolean hitBackspace = false;

//		private boolean hitBackspaceOnSelection;

		private final boolean hidePopupOnFocusLoss;
		
		private boolean caseSensitive;

		private final JComboBox comboBox;
		
		private boolean modified;
		
		private final List<Object> objectList = new LinkedList<>();

		private ComboDocument(JComboBox comboBox) {
			Validate.isTrue(comboBox != null, "The given combo box is null");

			// Bug 5100422 on Java 1.5: Editable JComboBox won't hide popup when
			// tabbing out
			hidePopupOnFocusLoss = System
					.getProperty("java.version").startsWith("1.5"); //$NON-NLS-1$ //$NON-NLS-2$
			this.comboBox = comboBox;

			// Highlight whole text when gaining focus
			JTextComponent editor = (JTextComponent) comboBox.getEditor()
					.getEditorComponent();
			editor.setDocument(this);
			editor.addFocusListener(this);
			editor.addKeyListener(this);
		}

		private void setCaseSensitive(boolean caseSensitive) {
			this.caseSensitive = caseSensitive;
		}

		private boolean isCaseSensitive() {
			return caseSensitive;
		}
		
		private boolean isModified() {
			return modified;
		}
		
		private void setModified(boolean modified) {
			this.modified = modified;
		}
		
		private void doAfterModify(){
		}
		
		private boolean hasItem(Object o) {
			return objectList.contains(o);
		}
		
		private void addItem(Object o) {
			objectList.add(o);
		}
		
		private void removeItem(Object o) {
			objectList.remove(o);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!selecting) {
				highlightCompletedText(0);
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
			modified = true;
			doAfterModify();
			if (comboBox.isDisplayable()) {
				comboBox.setPopupVisible(true);
			}
//			hitBackspace = false;
			// determine if the pressed key is backspace (needed by the remove
			// method)
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				JTextComponent editor = (JTextComponent) comboBox.getEditor()
						.getEditorComponent();
//				hitBackspace = true;
//				hitBackspaceOnSelection = editor.getSelectionStart() != editor
//						.getSelectionEnd();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void focusGained(FocusEvent arg0) {
			highlightCompletedText(0);
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			// Workaround for Bug 5100422 - Hide Popup on focus loss
			if (hidePopupOnFocusLoss) {
				comboBox.setPopupVisible(false);
			}
		}

		private void setText(String text) {
			try {
				// remove all text and insert the completed string
				super.remove(0, getLength());
				super.insertString(0, text, null);
			} catch (BadLocationException e) {
				throw new RuntimeException(e.toString());
			}
		}

		@Override
		public void remove(int offs, int len) throws BadLocationException {
			// return immediately when selecting an item
			if (selecting) {
				return;
			}

			// always remove the deleted characters
			super.remove(offs, len);
		}

		@Override
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {

			// return immediately when selecting an item
			if (selecting) {
				return;
			}
			// insert the string into the document
			super.insertString(offs, str, a);
			
			// lookup and select a matching item
			Object item = lookupItem(getText(0, getLength()));

			if (item != null) {
				setSelectedItem(item);
				setText(item.toString());
				
				// select the completed part
				highlightCompletedText(offs + str.length());
			}
		}

		private void highlightCompletedText(int start) {
			JTextComponent editor = (JTextComponent) comboBox.getEditor()
					.getEditorComponent();

			try {
				editor.setCaretPosition(getLength());
				editor.moveCaretPosition(start);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		private void setSelectedItem(Object item) {
			selecting = true;
			comboBox.getModel().setSelectedItem(item);
			selecting = false;
		}

		private Object lookupItem(String pattern) {
			ComboBoxModel model = comboBox.getModel();
			Object selectedItem = model.getSelectedItem();
			// only search for a different item if the currently selected does
			// not match
			if(isCaseSensitive()) {
				if ((selectedItem != null)
						&& selectedItem.toString().startsWith(pattern)) {
					return selectedItem;
				} else {
					// iterate over all items
					for (int i = 0, n = model.getSize(); i < n; i++) {
						Object currentItem = model.getElementAt(i);
						// current item starts with the pattern?
						if (currentItem.toString().startsWith(pattern)) {
							return currentItem;
						}
					}
				}
			}	else {
				if ((selectedItem != null)
						&& selectedItem.toString().toLowerCase().startsWith(pattern.toLowerCase())) {
					return selectedItem;
				} else {
					// iterate over all items
					for (int i = 0, n = model.getSize(); i < n; i++) {
						Object currentItem = model.getElementAt(i);
						// current item starts with the pattern?
						if (currentItem.toString().toLowerCase().startsWith(pattern.toLowerCase())) {
							return currentItem;
						}
					}
				}
			}
			
			// no item starts with the pattern => return null
			return null;
		}
	}
	
	private Object lastSelectedItem;
	
	private boolean handleSelectionChange = false;
	
	private ComboDocument document;

	/**
	 * Creates a new instance of JCompletionComboBox.
	 */
	public JCompletionComboBox() {
		super();

		init();
	}

	/**
	 * Creates a new instance of JCompletionComboBox.
	 * 
	 * @param items
	 */
	JCompletionComboBox(Object[] items) {
		super(items);

		init();
	}

	private void init() {
		addItemListener((arg0) -> {
				if(arg0.getStateChange() == ItemEvent.SELECTED) {
					setModified(true);
					doAfterModify();
				}
		});
		document = new ComboDocument(this);

		addActionListener(document);

		// Handle initially selected object
		Object selected = getSelectedItem();
		if (selected != null) {
			document.setText(selected.toString());
		}
		document.highlightCompletedText(0);
		
		setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = -7728351788449961901L;

			@Override
			public Component getListCellRendererComponent(JList jlist,
					Object obj, int i, boolean isSelected, boolean flag1) {
				
				Component c = super.getListCellRendererComponent(jlist, obj, i, isSelected, flag1);
				
				if ((lastSelectedItem != null) && (lastSelectedItem.equals(obj))) {
					c.setForeground(Color.BLUE);
				} else {
					c.setForeground(Color.BLACK);
				}
				setForeground(isSelected ? Color.WHITE : Color.BLACK);
				
				return c;
			}
		});
	}
	
	public void setCaseSensitive(boolean caseSensitive) {
		document.setCaseSensitive(caseSensitive);
	}

	public boolean isCaseSensitive() {
		return document.isCaseSensitive();
	}
	
	public boolean isModified() {
		return document.isModified();
	}
	
	public void setModified(boolean modified) {
		document.setModified(modified);
	}

	protected void doAfterModify(){
		document.doAfterModify();
	}
	
	public boolean hasItem(Object o) {
		return document.hasItem(o);
	}
	
	@Override
	public void addItem(Object obj) {
		super.addItem(obj);
		document.addItem(obj);
		
		if (handleSelectionChange) {
			// Memoriser le dernier objet ajoute a la liste afin de le mettre en
			// evidence dans la combobox
			this.lastSelectedItem = obj;
		}
	}
	
	@Override
	public void setSelectedItem(Object obj) {
		super.setSelectedItem(obj);
		
		if (handleSelectionChange) {
			// Memoriser le dernier objet ajoute a la liste afin de le mettre en
			// evidence dans la combobox. Le cas ou obj == null est gere
			lastSelectedItem = obj;
		}
	}
	
	@Override
	public void setSelectedIndex(int i) {
		super.setSelectedIndex(i);
		
		if (handleSelectionChange) {
			// Memoriser le dernier objet ajoute a la liste afin de le mettre en
			// evidence dans la combobox. Retournera null si i est non valide
			// donc deselectionnera l'item
			lastSelectedItem = getItemAt(i);
		}
	}
	
	@Override
	public void removeItem(Object obj) {
		super.removeItem(obj);
		document.removeItem(obj);
		
		if (handleSelectionChange && (lastSelectedItem != null) && lastSelectedItem.equals(obj)) {
			// Supprimer la selection
			lastSelectedItem = null;
		}
	}
	
	@Override
	public void removeItemAt(int i) {
		Object removedItem = getItemAt(i);
		
		super.removeItemAt(i);
		document.removeItem(removedItem);
		
		if (handleSelectionChange && (removedItem != null) && removedItem.equals(lastSelectedItem)) {
			// Supprimer la selection
			lastSelectedItem = null;
		}
	}

	public boolean isHandleSelectionChange() {
		return handleSelectionChange;
	}

	public void setHandleSelectionChange(boolean handleSelectionChange) {
		this.handleSelectionChange = handleSelectionChange;
	}
}
