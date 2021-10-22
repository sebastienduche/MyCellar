package mycellar.requester.ui;

import mycellar.core.MyCellarLabel;
import mycellar.requester.IPredicate;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Objects;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 *
 * @author Sébastien Duché
 * @version 0.4
 * @since 08/03/19
 */
final class LabelSearch extends JPanel {

  private static final long serialVersionUID = 3361283505652395494L;
  private final IPredicate<?> predicate;
  private final MyCellarLabel MyCellarLabel = new MyCellarLabel();
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
    MyCellarLabel.setText(label);
    setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.gray, Color.white));
    setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
    add(MyCellarLabel);
    add(panelClose = new PanelCloseButton() {
      private static final long serialVersionUID = 3495975676025406824L;

      @Override
      protected void actionPerformed() {
        Component parent = LabelSearch.this.getParent();
        if (parent instanceof JPanel) {
          ((JPanel) parent).remove(LabelSearch.this);
          source.add(LabelSearch.this);
          ((JPanel) parent).updateUI();
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
    return MyCellarLabel.getText();
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
    MyCellarLabel.setText(s.toString());
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

    value = predicate.askforValue();
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
      MyCellarLabel.setForeground(null);
      setValue(null);
    } else {
      setBorder(BorderFactory.createEtchedBorder());
      setBackground(new Color(102, 102, 255));
      MyCellarLabel.setForeground(new Color(255, 255, 255));
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
