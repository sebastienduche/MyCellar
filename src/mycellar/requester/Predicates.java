package mycellar.requester;

import mycellar.Bouteille;
import mycellar.Music;
import mycellar.Program;
import mycellar.core.BottlesStatus;
import mycellar.core.IMyCellarObject;
import mycellar.core.MyCellarObject;
import mycellar.core.PanelVignobles;
import mycellar.core.common.bottle.BottleColor;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.core.datas.jaxb.AppelationJaxb;
import mycellar.core.datas.jaxb.VignobleJaxb;
import mycellar.frame.MainFrame;
import mycellar.placesmanagement.places.AbstractPlace;
import mycellar.requester.ui.ValueSearch;
import net.miginfocom.swing.MigLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.math.BigDecimal;
import java.util.Objects;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2014
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.2
 * @since 01/06/22
 */

public class Predicates {

  public static final IPredicate<Bouteille> COLOR = new IPredicate<>() {

    @Override
    public boolean apply(Bouteille bottle) {
      return apply(bottle, "", -1);
    }

    @Override
    public boolean apply(Bouteille bottle, Object compare, int type) {
      return bottle.getColor().equals(((BottleColor) compare).name());
    }

    @Override
    public boolean isValueRequired() {
      return true;
    }

    @Override
    public String getName() {
      return getLabel("AddVin.Color");
    }

    @Override
    public int getType() {
      return 0;
    }

    @Override
    public boolean isEmptyValueForbidden() {
      return false;
    }

    @Override
    public ValueSearch askforValue() {
      JPanel panel = new JPanel();
      panel.setLayout(new MigLayout("", "grow", "[]"));
      JComboBox<BottleColor> liste = new JComboBox<>();
      for (BottleColor color : BottleColor.values()) {
        liste.addItem(color);
      }
      panel.add(new JLabel(getLabel("Predicates.SelectColor")), "wrap");
      panel.add(liste);
      JOptionPane.showMessageDialog(null, panel,
          "",
          JOptionPane.PLAIN_MESSAGE);
      return new ValueSearch(liste.getSelectedItem());
    }
  };

  public static final IPredicate<IMyCellarObject> STATUS = new IPredicate<>() {

    @Override
    public boolean apply(IMyCellarObject bottle) {
      return apply(bottle, "", -1);
    }

    @Override
    public boolean apply(IMyCellarObject bottle, Object compare, int type) {
      final String status = bottle.getStatus();
      final BottlesStatus bottlesStatus = (BottlesStatus) compare;
      return (status.isEmpty() && BottlesStatus.NONE.equals(bottlesStatus)) || status.equals(bottlesStatus.name());
    }

    @Override
    public boolean isValueRequired() {
      return true;
    }

    @Override
    public String getName() {
      return getLabel("MyCellarManageBottles.Status");
    }

    @Override
    public int getType() {
      return 0;
    }

    @Override
    public boolean isEmptyValueForbidden() {
      return false;
    }

    @Override
    public ValueSearch askforValue() {
      JPanel panel = new JPanel();
      panel.setLayout(new MigLayout("", "grow", "[]"));
      JComboBox<BottlesStatus> liste = new JComboBox<>();
      for (BottlesStatus status : BottlesStatus.values()) {
        liste.addItem(status);
      }
      panel.add(new JLabel(getLabel("Predicates.SelectStatus")), "wrap");
      panel.add(liste);
      JOptionPane.showMessageDialog(null, panel,
          "",
          JOptionPane.PLAIN_MESSAGE);
      return new ValueSearch(liste.getSelectedItem());
    }
  };

  public static final IPredicate<IMyCellarObject> NAME = new IPredicate<>() {

    private int type = -1;

    @Override
    public boolean apply(IMyCellarObject bouteille) {
      return apply(bouteille, "", -1);
    }

    @Override
    public boolean apply(IMyCellarObject myCellarObject, Object compare, int type) {
      if (type == 0) {
        if (compare instanceof String) {
          return myCellarObject.getNom() != null && myCellarObject.getNom().startsWith((String) compare);
        }
      } else if (type == 1) {
        if (compare instanceof String) {
          return myCellarObject.getNom() != null && myCellarObject.getNom().endsWith((String) compare);
        }
      } else if (type == 2) {
        if (compare instanceof String) {
          return myCellarObject.getNom() != null && myCellarObject.getNom().contains((String) compare);
        }
      }
      return false;
    }

    @Override
    public boolean isValueRequired() {
      return true;
    }

    @Override
    public int getType() {
      return type;
    }

    @Override
    public boolean isEmptyValueForbidden() {
      return true;
    }

    @Override
    public String getName() {
      String label = getLabel("Predicates.Name");
      if (type == 0) {
        label += getLabel("Predicates.StartWith");
      } else if (type == 1) {
        label += getLabel("Predicates.EndWith");
      } else if (type == 2) {
        label += getLabel("Predicates.Contains");
      }
      return label;
    }

    @Override
    public ValueSearch askforValue() {
      type = 0;
      JPanel panel = new JPanel();
      JComboBox<String> combo = new JComboBox<>();
      combo.addItem(getLabel("Predicates.StartWith"));
      combo.addItem(getLabel("Predicates.EndWith"));
      combo.addItem(getLabel("Predicates.Contains"));
      combo.addItemListener((e) -> type = combo.getSelectedIndex());
      panel.add(combo);
      return new ValueSearch(JOptionPane.showInputDialog(panel));
    }
  };

  public static final IPredicate<MyCellarObject> ARTIST = new IPredicate<>() {

    private int type = -1;

    @Override
    public boolean apply(MyCellarObject music) {
      return apply(music, "", -1);
    }

    @Override
    public boolean apply(MyCellarObject myCellarObject, Object compare, int type) {
      Program.throwNotImplementedIfNotFor(myCellarObject, Music.class);
      Music music = (Music) myCellarObject;
      if (music.getArtist() == null) {
        return false;
      }
      if (type == 0) {
        if (compare instanceof String) {
          return music.getArtist().startsWith((String) compare);
        }
      } else if (type == 1) {
        if (compare instanceof String) {
          return music.getArtist().endsWith((String) compare);
        }
      } else if (type == 2) {
        if (compare instanceof String) {
          return music.getArtist().contains((String) compare);
        }
      }
      return false;
    }

    @Override
    public boolean isValueRequired() {
      return true;
    }

    @Override
    public int getType() {
      return type;
    }

    @Override
    public boolean isEmptyValueForbidden() {
      return true;
    }

    @Override
    public String getName() {
      String label = getLabel("Predicates.Artist");
      if (type == 0) {
        label += getLabel("Predicates.StartWith");
      } else if (type == 1) {
        label += getLabel("Predicates.EndWith");
      } else if (type == 2) {
        label += getLabel("Predicates.Contains");
      }
      return label;
    }

    @Override
    public ValueSearch askforValue() {
      type = 0;
      JPanel panel = new JPanel();
      JComboBox<String> combo = new JComboBox<>();
      combo.addItem(getLabel("Predicates.StartWith"));
      combo.addItem(getLabel("Predicates.EndWith"));
      combo.addItem(getLabel("Predicates.Contains"));
      combo.addItemListener((e) -> type = combo.getSelectedIndex());
      panel.add(combo);
      return new ValueSearch(JOptionPane.showInputDialog(panel));
    }
  };


  public static final IPredicate<IMyCellarObject> YEAR = new IPredicate<>() {

    @Override
    public boolean apply(IMyCellarObject myCellarObject) {
      return apply(myCellarObject, "", -1);
    }

    @Override
    public boolean apply(IMyCellarObject myCellarObject, Object compare, int type) {
      return (compare instanceof String) && myCellarObject.getAnnee() != null && myCellarObject.getAnnee().equals(compare);
    }

    @Override
    public boolean isValueRequired() {
      return true;
    }

    @Override
    public String getName() {
      return getLabel("Predicates.Year");
    }

    @Override
    public int getType() {
      return 0;
    }

    @Override
    public boolean isEmptyValueForbidden() {
      return true;
    }

    @Override
    public ValueSearch askforValue() {
      return new ValueSearch(JOptionPane.showInputDialog(getName()));
    }
  };

  public static final IPredicate<IMyCellarObject> RANGEMENT = new IPredicate<>() {

    @Override
    public boolean apply(IMyCellarObject myCellarObject) {
      return apply(myCellarObject, "", -1);
    }

    @Override
    public boolean apply(IMyCellarObject myCellarObject, Object compare, int type) {
      return (compare instanceof String) && myCellarObject.getEmplacement() != null && myCellarObject.getEmplacement().equals(compare);
    }

    @Override
    public boolean isValueRequired() {
      return true;
    }

    @Override
    public String getName() {
      return getLabel("Predicates.Place");
    }

    @Override
    public int getType() {
      return 0;
    }

    @Override
    public boolean isEmptyValueForbidden() {
      return true;
    }

    @Override
    public ValueSearch askforValue() {
      JPanel panel = new JPanel();
      panel.setLayout(new MigLayout("", "grow", "[]"));
      JComboBox<AbstractPlace> liste = new JComboBox<>();
      Program.getAbstractPlaces().forEach(liste::addItem);
      panel.add(new JLabel(getLabel("Predicates.SelectPlace")), "wrap");
      panel.add(liste);
      JOptionPane.showMessageDialog(null, panel,
          "",
          JOptionPane.PLAIN_MESSAGE);
      return new ValueSearch(((AbstractPlace) Objects.requireNonNull(liste.getSelectedItem())).getName());
    }
  };


  public static final IPredicate<Bouteille> CAPACITY = new IPredicate<>() {

    @Override
    public boolean apply(Bouteille bottle) {
      return apply(bottle, "", -1);
    }

    @Override
    public boolean apply(Bouteille bottle, Object compare, int type) {
      return bottle.getKind().equals(compare);
    }

    @Override
    public boolean isValueRequired() {
      return true;
    }

    @Override
    public String getName() {
      return getLabel("Main.CapacityOrSupport");
    }

    @Override
    public int getType() {
      return 0;
    }

    @Override
    public boolean isEmptyValueForbidden() {
      return true;
    }

    @Override
    public ValueSearch askforValue() {
      JPanel panel = new JPanel();
      panel.setLayout(new MigLayout("", "grow", "[]"));
      JComboBox<String> liste = new JComboBox<>();
      MyCellarBottleContenance.getList().forEach(liste::addItem);
      panel.add(new JLabel(getLabel("Predicates.SelectSize")), "wrap");
      panel.add(liste);
      JOptionPane.showMessageDialog(null, panel,
          "",
          JOptionPane.PLAIN_MESSAGE);
      return new ValueSearch(liste.getSelectedItem());
    }
  };

  public static final IPredicate<IMyCellarObject> PRICE = new IPredicate<>() {

    private int type = -1;

    @Override
    public boolean apply(IMyCellarObject myCellarObject) {
      return apply(myCellarObject, "", -1);
    }

    @Override
    public boolean apply(IMyCellarObject myCellarObject, Object compare, int type) {
      if (type == 0) {
        if (compare instanceof String) {
          return myCellarObject.getPrice().compareTo(BigDecimal.ZERO) != 0 && myCellarObject.getPrice().compareTo(new BigDecimal((String) compare)) < 0;
        }
      } else if (type == 1) {
        if (compare instanceof String) {
          return myCellarObject.getPrice().compareTo(BigDecimal.ZERO) != 0 && myCellarObject.getPrice().compareTo(new BigDecimal((String) compare)) > 0;
        }
      }
      return false;
    }

    @Override
    public boolean isValueRequired() {
      return true;
    }

    @Override
    public String getName() {
      String label = getLabel("Predicates.Price");
      if (type == 0) {
        label += getLabel("Predicates.Smaller");
      } else if (type == 1) {
        label += getLabel("Predicates.Greater");
      }
      return label;
    }

    @Override
    public int getType() {
      return type;
    }

    @Override
    public boolean isEmptyValueForbidden() {
      return true;
    }

    @Override
    public ValueSearch askforValue() {
      type = 0;
      JPanel panel = new JPanel();
      JComboBox<String> combo = new JComboBox<>();
      combo.addItem(getLabel("Predicates.Smaller"));
      combo.addItem(getLabel("Predicates.Greater"));
      combo.addItemListener((e) -> type = combo.getSelectedIndex());
      panel.add(combo);
      return new ValueSearch(JOptionPane.showInputDialog(panel));
    }
  };

  public static final IPredicate<Bouteille> COUNTRY = new IPredicate<>() {

    @Override
    public boolean apply(Bouteille bouteille) {
      return apply(bouteille, "", -1);
    }

    @Override
    public boolean apply(Bouteille bouteille, Object compare, int type) {
      if (!(compare instanceof VignobleJaxb) || bouteille.getVignoble() == null) {
        return false;
      }
      VignobleJaxb compareVignobleJaxb = (VignobleJaxb) compare;
      final VignobleJaxb vignobleJaxb = bouteille.getVignoble();
      return compareVignobleJaxb.getCountry().equals(vignobleJaxb.getCountry()) &&
          (compareVignobleJaxb.getName().isEmpty() || compareVignobleJaxb.getName().equals(vignobleJaxb.getName())) &&
          (compareVignobleJaxb.getAOC().isEmpty() || compareVignobleJaxb.getAOC().equals(vignobleJaxb.getAOC())) &&
          (compareVignobleJaxb.getIGP().isEmpty() || compareVignobleJaxb.getIGP().equals(vignobleJaxb.getIGP()));
    }

    @Override
    public boolean isValueRequired() {
      return true;
    }

    @Override
    public String getName() {
      return getLabel("Predicates.Vignoble");
    }

    @Override
    public int getType() {
      return 0;
    }

    @Override
    public boolean isEmptyValueForbidden() {
      return true;
    }

    @Override
    public ValueSearch askforValue() {
      PanelVignobles panelVignobles = new PanelVignobles(true, false, false);
      JOptionPane.showMessageDialog(MainFrame.getInstance(), panelVignobles,
          "",
          JOptionPane.PLAIN_MESSAGE);
      final VignobleJaxb selectedVignobleJaxb = panelVignobles.getSelectedVignoble();
      return new ValueSearch(selectedVignobleJaxb, selectedVignobleJaxb.getSearchLabel());
    }
  };

  public static final IPredicate<AppelationJaxb> AND = new IPredicate<>() {

    @Override
    public boolean apply(AppelationJaxb appelationJaxb) {
      return true;
    }

    @Override
    public boolean apply(AppelationJaxb appelationJaxb, Object compare, int type) {
      return true;
    }

    @Override
    public boolean isValueRequired() {
      return false;
    }

    @Override
    public String getName() {
      return getLabel("Predicates.And");
    }

    @Override
    public int getType() {
      return 0;
    }

    @Override
    public boolean isEmptyValueForbidden() {
      return true;
    }

    @Override
    public ValueSearch askforValue() {
      return null;
    }
  };

  public static final IPredicate<AppelationJaxb> OR = new IPredicate<>() {

    @Override
    public boolean apply(AppelationJaxb appelationJaxb) {
      return true;
    }

    @Override
    public boolean apply(AppelationJaxb appelationJaxb, Object compare, int type) {
      return true;
    }

    @Override
    public boolean isValueRequired() {
      return false;
    }

    @Override
    public String getName() {
      return getLabel("Predicates.Or");
    }

    @Override
    public int getType() {
      return 0;
    }

    @Override
    public boolean isEmptyValueForbidden() {
      return true;
    }

    @Override
    public ValueSearch askforValue() {
      return null;
    }
  };

  public static final IPredicate<AppelationJaxb> OPEN_PARENTHESIS = new IPredicate<>() {

    @Override
    public boolean apply(AppelationJaxb appelationJaxb) {
      return true;
    }

    @Override
    public boolean apply(AppelationJaxb appelationJaxb, Object compare, int type) {
      return true;
    }

    @Override
    public boolean isValueRequired() {
      return false;
    }

    @Override
    public String getName() {
      return "(";
    }

    @Override
    public int getType() {
      return 0;
    }

    @Override
    public boolean isEmptyValueForbidden() {
      return true;
    }

    @Override
    public ValueSearch askforValue() {
      return null;
    }
  };

  public static final IPredicate<AppelationJaxb> CLOSE_PARENTHESIS = new IPredicate<>() {

    @Override
    public boolean apply(AppelationJaxb appelationJaxb) {
      return true;
    }

    @Override
    public boolean apply(AppelationJaxb appelationJaxb, Object compare, int type) {
      return true;
    }

    @Override
    public boolean isValueRequired() {
      return false;
    }

    @Override
    public String getName() {
      return ")";
    }

    @Override
    public int getType() {
      return 0;
    }

    @Override
    public boolean isEmptyValueForbidden() {
      return true;
    }

    @Override
    public ValueSearch askforValue() {
      return null;
    }
  };
  @SuppressWarnings("rawtypes")
  private final IPredicate predicate;
  private final Object value;
  private final int type;

  public <T> Predicates(IPredicate<T> predicate, Object value, int type) {
    this.predicate = predicate;
    this.value = value;
    this.type = type;
  }

  static boolean isFieldPredicate(IPredicate<?> predicate) {
    return (predicate != null) && !isKeywordPredicate(predicate) && !isParenthesisPredicate(predicate);
  }

  static boolean isKeywordPredicate(IPredicate<?> predicate) {
    return (predicate != null) && (predicate.equals(AND) || predicate.equals(OR));
  }

  static boolean isParenthesisPredicate(IPredicate<?> predicate) {
    return (predicate != null) && (predicate.equals(OPEN_PARENTHESIS) || predicate.equals(CLOSE_PARENTHESIS));
  }

  @SuppressWarnings("unchecked")
  <T> IPredicate<T> getPredicate() {
    return predicate;
  }

  public Object getValue() {
    return value;
  }

  public int getType() {
    return type;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[Predicate: ");
    if (predicate != null) {
      sb.append(predicate.getName());
      sb.append(" ");
    }
    if (value != null) {
      sb.append(value).append(" ");
    }
    sb.append(type);
    sb.append("]");
    return sb.toString();
  }
}
