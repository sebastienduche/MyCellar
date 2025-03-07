package mycellar;

import mycellar.core.uicomponents.MyCellarSimpleLabel;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 1998
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.9
 * @since 02/03/25
 */
public abstract class TextFieldPopup extends JPanel {

  @Serial
  private static final long serialVersionUID = -7190629333835800410L;
  private final JScrollPane scroll;
  private final JPanel menu = new JPanel();
  private final JTextField textfield = new JTextField();
  private final List<MyJMenuItem> items = new LinkedList<>();
  private List<String> list;
  private final JPopupMenu popupMenu = new JPopupMenu();
  private boolean can;
  private int listHeight;
  private int x, y;

  protected TextFieldPopup(List<String> list, int listHeight) {
    this.list = list;
    this.listHeight = listHeight;

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

  public void setList(List<String> list) {
    this.list = list;
  }

  public abstract void doAfterValidate();

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

  private void updateMenu() {
    if (!popupMenu.isVisible()) {
      addMenu();
    }
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

  private class PopupKeyListener implements KeyListener {

    private MyJMenuItem selected;
    private int index;

    private PopupKeyListener() {
      index = -1;
    }

    @Override
    public void keyTyped(KeyEvent e) {
      if (x == 0 || y == 0) {
        x = (int) textfield.getLocationOnScreen().getX();
        y = (int) textfield.getLocationOnScreen().getY();
        int width = textfield.getWidth();
        popupMenu.setLocation(x, y + textfield.getHeight());
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

      if (Character.isLetter(e.getKeyChar()) || Character.isDigit(e.getKeyChar())) {
        val += c;
      }

      if (val.isEmpty()) {
        removeMenu();
        return;
      }

      menu.removeAll();
      items.clear();
      selected = null;
      index = -1;
      for (String b : filter(val)) {
        MyJMenuItem item = new MyJMenuItem(b);
        items.add(item);
        menu.add(item, "growx, gapy 0px, wrap");
      }
      if (items.size() == 1) {
        selected = items.get(0);
        selected.activate();
      }
      updateMenu();
    }

    @Override
    public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_DOWN) {
        if (index == items.size() - 1) {
          index = -1;
        }
        if (selected != null) {
          selected.deactivate();
        }
        if (items.size() > (index + 1)) {
          selected = items.get(++index);
          selected.activate();
        }
        if (index == 0) {
          scroll.getVerticalScrollBar().setValue(0);
        } else if (selected != null) {
          selected.scrollRectToVisible(new Rectangle(0, 25, 0, 0));
        }
      } else if (e.getKeyCode() == KeyEvent.VK_UP) {
        if (index <= 0) {
          index = items.size();
          scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
        } else {
          selected.scrollRectToVisible(new Rectangle(0, -25, 0, 0));
        }
        if (selected != null) {
          selected.deactivate();
        }
        if (index > 0) {
          selected = items.get(--index);
          selected.activate();
        }
      } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        if (selected != null) {
          selected.doClick();
        }
      }
      if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
        removeMenu();
      }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
  }

  final class MyJMenuItem extends MyCellarSimpleLabel {

    private static final long serialVersionUID = -463113999199742853L;
    private final Color blue = new Color(51, 153, 255);
    private final Color lightblue = new Color(153, 204, 255);
    private final Color background;
    private final String text;
    private boolean mouse = false;

    private MyJMenuItem(String text) {
      super(text);
      this.text = text;
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

    void activate() {
      setBorder(BorderFactory.createEtchedBorder());
      setBackground(mouse ? lightblue : blue);
      setFont(getFont().deriveFont(Font.BOLD));
    }

    void deactivate() {
      setBorder(BorderFactory.createEmptyBorder());
      setBackground(background);
      setFont(getFont().deriveFont(Font.PLAIN));
    }

    void doClick() {
      setCan(false);
      textfield.setText(text);
      removeMenu();
      doAfterValidate();
    }
  }
}
