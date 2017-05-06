package mycellar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import mycellar.core.MyCellarLabel;
import net.miginfocom.swing.MigLayout;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 1998</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.4
 * @since 25/05/16
 */
public class TextFieldPopup extends JPanel {

	private static final long serialVersionUID = -7190629333835800410L;
	private boolean can;
	private JScrollPane scroll;
	private JPanel menu = new JPanel();
	private JTextField textfield = new JTextField();
	private LinkedList<MyJMenuItem> items = new LinkedList<MyJMenuItem>();
	private List<String> list;
	private int listHeight = 100;
	private JPopupMenu popupMenu = new JPopupMenu();
	private int x, y, width;
	

	public TextFieldPopup(List<String> list) {
		this.list = list;
		init();
	}
	
	public TextFieldPopup(List<String> list, int listHeight) {
		this.list = list;
		this.listHeight = listHeight;
		init();
	}
	
	private void init() {
		can = true;
		scroll = new JScrollPane(menu);
		scroll.getVerticalScrollBar().setUnitIncrement(5);
		textfield.addKeyListener(new PopupKeyListener());
		textfield.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				removeMenu();
			}
			
			@Override
			public void focusGained(FocusEvent e) {
			}
		});
		menu.setLayout(new MigLayout("", "[grow]", ""));
		setLayout(new MigLayout("", "grow", "[]0px[]"));
		add(textfield, "growx, wrap");
		popupMenu.add(scroll);
		scroll.setBorder(BorderFactory.createEmptyBorder());
	}
	
	public int getListHeight() {
		return listHeight;
	}

	public void setListHeight(int listHeight) {
		this.listHeight = listHeight;
	}

	private void addMenu() {
		popupMenu.setVisible(true);
		popupMenu.updateUI();
		updateUI();	
	}
	
	public void updateMenu(){
		if(!popupMenu.isVisible())
			addMenu();
		popupMenu.updateUI();
	}
	
	public void removeMenu() {
		menu.removeAll();
		menu.updateUI();
		popupMenu.setVisible(false);
		updateUI();
	}

	private boolean isCan() {
		return can;
	}

	private void setCan(boolean can) {
		this.can = can;
	}
	
	private List<String> filter(String val) {
		return list.stream().filter(b -> b.toLowerCase().startsWith(val.toLowerCase())).collect(Collectors.toList());
	}

	class PopupKeyListener implements KeyListener {

		private MyJMenuItem selected;
		private int index;

		public PopupKeyListener() {
			index = -1;
		}

		@Override
		public void keyTyped(KeyEvent e) {
			if(x == 0 || y == 0){
				x = (int) textfield.getLocationOnScreen().getX();
				y = (int) textfield.getLocationOnScreen().getY();
				width = textfield.getWidth();
				popupMenu.setLocation(x, y+textfield.getHeight());
				popupMenu.setPopupSize(width, listHeight);
			}
			String val = textfield.getText();
			char c = e.getKeyChar();
			if (!isCan()) {
				setCan(true);
				return;
			}

			if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
				removeMenu();
				return;
			}

			if( Character.isLetter(e.getKeyChar()) || Character.isDigit(e.getKeyChar()) )
				val += c;

			if (val.isEmpty()) {
				removeMenu();
				return;
			}
			
			menu.removeAll();
			items.clear();
			index = -1;
			ArrayList<String> list = (ArrayList<String>) filter(val);
			for(String b : list) {
				MyJMenuItem item = new MyJMenuItem(b);
				items.add(item);
				menu.add(item, "growx, gapy 0px, wrap");
			}
			updateMenu();
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				if (index == items.size() - 1)
					index = -1;
				if(selected != null)
					selected.deactivate();
				if(items.size() > (index+1)) {
    				selected = items.get(++index);
    				selected.activate();
				}
				if(index == 0)
					scroll.getVerticalScrollBar().setValue(0);
				else if(selected != null)
					selected.scrollRectToVisible(new Rectangle(0, 25, 0, 0));
			} else if (e.getKeyCode() == KeyEvent.VK_UP) {
				if (index <= 0) {
					index = items.size();
					scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
				}
				else
					selected.scrollRectToVisible(new Rectangle(0, -25, 0, 0));
				if(selected != null)
					selected.deactivate();
				if(index > 0) {
					selected = items.get(--index);
					selected.activate();
				}
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if(selected != null)
					selected.doClick();
			}
			if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
				removeMenu();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {}
	}

	class MenuAction extends AbstractAction {

		private static final long serialVersionUID = 1023561300178864980L;
		private String text;

		public MenuAction(String text) {
			super(text);
			this.text = text;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setCan(false);
			textfield.setText(text);
			removeMenu();
		}
	}
	
	class MyJMenuItem extends MyCellarLabel {

		private static final long serialVersionUID = -463113999199742853L;
		private Color blue = new Color(51,153,255);
		private Color lightblue = new Color(153,204,255);
		private Color foreground;
		private Color background;
		private boolean mouse = false;
		private String text;
		
		public MyJMenuItem(String text) {
			super(text);
			this.text = text;
			foreground = getForeground();
			background = getBackground();
			setFont(getFont().deriveFont(Font.PLAIN));
			setBorder(BorderFactory.createEmptyBorder());
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					mouse = true;
					activate();
				}
				@Override
				public void mouseExited(MouseEvent e) {
					mouse = false;
					deactivate();
				}
				@Override
				public void mouseClicked(MouseEvent e) {
					doClick();
				}
			});
		}
		
		public void activate() {
			setBorder(BorderFactory.createEtchedBorder());
			setBackground(mouse? lightblue : blue);
			setForeground(Color.white);
			setFont(getFont().deriveFont(Font.BOLD));
		}
		
		public void deactivate() {
			setBorder(BorderFactory.createEmptyBorder());
			setBackground(background);
			setForeground(foreground);
			setFont(getFont().deriveFont(Font.PLAIN));
		}
		
		public void doClick() {
			setCan(false);
			textfield.setText(text);
			removeMenu();
		}
	}
	
	public void setEditable(boolean b) {
		textfield.setEditable(b);
	}

	public String getSelectedText() {
		return textfield.getSelectedText();
	}
	
	public String getText() {
		return textfield.getText();
	}
	
	public void setText(String s) {
		textfield.setText(s);
	}

	public int getSelectionStart() {
		return textfield.getSelectionStart();
	}
	
	public int getSelectionEnd() {
		return textfield.getSelectionEnd();
	}
}
