package mycellar.requester.ui;

import mycellar.core.uicomponents.MyCellarSimpleLabel;
import mycellar.requester.IPredicate;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Objects;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2014
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.6
 * @since 26/12/23
 */
final public class LabelSearch extends JPanel {

  private final IPredicate<?> predicate;
  private final MyCellarSimpleLabel myCellarLabel = new MyCellarSimpleLabel();
  private final PanelCloseButton panelClose;
  private String label;
  private ValueSearch value;
  private int type;
  private boolean copy = false;
  private PanelDAndD source;

  private LabelSearch(IPredicate<?> predicate) {
    super();
    label = predicate.getName();
    this.predicate = predicate;
    type = predicate.getType();
    myCellarLabel.setText(label);
    setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.gray, Color.white));
    setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
    add(myCellarLabel);
    add(panelClose = new PanelCloseButton() {
      @Override
      protected void actionPerformed() {
        Component parent = LabelSearch.this.getParent();
        if (parent instanceof JPanel panel) {
          panel.remove(LabelSearch.this);
          source.add(LabelSearch.this);
          panel.updateUI();
        }
      }
    });
    panelClose.setVisible(false);
  }

  LabelSearch(IPredicate<?> predicate, PanelDAndD source) {
    this(predicate);
    this.source = source;
  }

  LabelSearch(IPredicate<?> predicate, PanelDAndD source, boolean copy) {
    this(predicate);
    this.source = source;
    this.copy = copy;
  }


  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
    setLabel();
  }

  public String getText() {
    return myCellarLabel.getText();
  }

  private void setLabel() {
    StringBuilder s = new StringBuilder();
    if (label != null && !label.isEmpty()) {
      s.append(label);
    }
    if (label != null && !label.isEmpty() && value != null && !value.getLabel().isEmpty()) {
      s.append(": ");
    }
    if (value != null) {
      s.append(value.getLabel());
    }
    myCellarLabel.setText(s.toString());
  }

  ValueSearch getValueSearch() {
    return value;
  }

  public int getType() {
    return type;
  }

  Object getValue() {
    if (value == null) {
      return null;
    }
    return value.getValue();
  }

  public void setValue(ValueSearch value) {
    this.value = value;
    setLabel();
  }

  void askForValue() {
    if (!predicate.isValueRequired() || (value != null && !value.getLabel().isEmpty())) {
      return;
    }

    value = predicate.askForValue();
    type = predicate.getType();
    setLabel(predicate.getName());
  }

  public boolean isCopy() {
    return copy;
  }

  public void setCopy(boolean copy) {
    this.copy = copy;
  }

  public PanelDAndD getSource() {
    return source;
  }


  public void setSource(PanelDAndD source) {
    this.source = source;
  }


  void setAsKeyword(boolean b) {
    if (b) {
      setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.gray, Color.white));
      setBackground(null);
      myCellarLabel.setForeground(null);
      setValue(null);
    } else {
      setBorder(BorderFactory.createEtchedBorder());
      setBackground(new Color(102, 102, 255));
      myCellarLabel.setForeground(new Color(255, 255, 255));
    }
    panelClose.setVisible(!b);
  }

  IPredicate<?> getPredicate() {
    return predicate;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((label == null) ? 0 : label.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!Objects.equals(getClass(), obj.getClass())) {
      return false;
    }
    assert obj instanceof LabelSearch;
    LabelSearch other = (LabelSearch) obj;
    if (label == null) {
      if (other.label != null) {
        return false;
      }
    } else if (!label.equals(other.label)) {
      return false;
    }
    return other.copy;
  }
}
