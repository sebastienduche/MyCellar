package mycellar.requester;

import mycellar.Bouteille;
import mycellar.Music;
import mycellar.Program;
import mycellar.core.BottlesStatus;
import mycellar.core.IMyCellarObject;
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
import java.util.Arrays;
import java.util.Objects;

import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.ADDVIN_COLOR;
import static mycellar.general.ResourceKey.MAIN_CAPACITYORSUPPORT;
import static mycellar.general.ResourceKey.MYCELLARMANAGEBOTTLES_STATUS;
import static mycellar.general.ResourceKey.PREDICATES_AND;
import static mycellar.general.ResourceKey.PREDICATES_ARTIST;
import static mycellar.general.ResourceKey.PREDICATES_CONTAINS;
import static mycellar.general.ResourceKey.PREDICATES_ENDWITH;
import static mycellar.general.ResourceKey.PREDICATES_GREATER;
import static mycellar.general.ResourceKey.PREDICATES_NAME;
import static mycellar.general.ResourceKey.PREDICATES_OR;
import static mycellar.general.ResourceKey.PREDICATES_PLACE;
import static mycellar.general.ResourceKey.PREDICATES_PRICE;
import static mycellar.general.ResourceKey.PREDICATES_SELECTCOLOR;
import static mycellar.general.ResourceKey.PREDICATES_SELECTPLACE;
import static mycellar.general.ResourceKey.PREDICATES_SELECTSIZE;
import static mycellar.general.ResourceKey.PREDICATES_SELECTSTATUS;
import static mycellar.general.ResourceKey.PREDICATES_SMALLER;
import static mycellar.general.ResourceKey.PREDICATES_STARTWITH;
import static mycellar.general.ResourceKey.PREDICATES_VIGNOBLE;
import static mycellar.general.ResourceKey.PREDICATES_YEAR;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2014
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 2.5
 * @since 21/03/25
 */

public class Predicates {

  public static final IPredicate<Bouteille> COLOR = new IPredicate<>() {

    @Override
    public boolean apply(Bouteille bottle, Object compare, int type) {
      if (compare instanceof BottleColor color) {
        return bottle.getColor().equals(color.name());
      }
      throw new UnsupportedOperationException("Unknown type: " + compare);
    }

    @Override
    public String getName() {
      return getLabel(ADDVIN_COLOR);
    }

    @Override
    public boolean isEmptyValueForbidden() {
      return false;
    }

    @Override
    public ValueSearch askForValue() {
      JPanel panel = new JPanel();
      panel.setLayout(new MigLayout("", "grow", "[]"));
      JComboBox<BottleColor> liste = new JComboBox<>();
      Arrays.stream(BottleColor.values()).forEach(liste::addItem);
      panel.add(new JLabel(getLabel(PREDICATES_SELECTCOLOR)), "wrap");
      panel.add(liste);
      JOptionPane.showMessageDialog(MainFrame.getInstance(), panel,
          "",
          JOptionPane.PLAIN_MESSAGE);
      return new ValueSearch(liste.getSelectedItem());
    }
  };

  public static final IPredicate<IMyCellarObject> STATUS = new IPredicate<>() {

    @Override
    public boolean apply(IMyCellarObject bottle, Object compare, int type) {
      final String status = bottle.getStatus();
      final BottlesStatus bottlesStatus = (BottlesStatus) compare;
      return (status.isEmpty() && BottlesStatus.NONE.equals(bottlesStatus)) || status.equals(bottlesStatus.name());
    }

    @Override
    public String getName() {
      return getLabel(MYCELLARMANAGEBOTTLES_STATUS);
    }

    @Override
    public boolean isEmptyValueForbidden() {
      return false;
    }

    @Override
    public ValueSearch askForValue() {
      JPanel panel = new JPanel();
      panel.setLayout(new MigLayout("", "grow", "[]"));
      JComboBox<BottlesStatus> liste = new JComboBox<>();
      Arrays.stream(BottlesStatus.values()).forEach(liste::addItem);
      panel.add(new JLabel(getLabel(PREDICATES_SELECTSTATUS)), "wrap");
      panel.add(liste);
      JOptionPane.showMessageDialog(MainFrame.getInstance(), panel,
          "",
          JOptionPane.PLAIN_MESSAGE);
      return new ValueSearch(liste.getSelectedItem());
    }
  };

  public static final IPredicate<IMyCellarObject> NAME = new IPredicate<>() {

    private int type = -1;

    @Override
    public boolean apply(IMyCellarObject myCellarObject, Object compare, int type) {
      if (type == 0) {
        if (compare instanceof String s) {
          return myCellarObject.getNom() != null && myCellarObject.getNom().startsWith(s);
        }
      } else if (type == 1) {
        if (compare instanceof String s) {
          return myCellarObject.getNom() != null && myCellarObject.getNom().endsWith(s);
        }
      } else if (type == 2) {
        if (compare instanceof String s) {
          return myCellarObject.getNom() != null && myCellarObject.getNom().contains(s);
        }
      }
      return false;
    }

    @Override
    public int getType() {
      return type;
    }

    @Override
    public String getName() {
      String label = getLabel(PREDICATES_NAME);
      if (type == 0) {
        label += getLabel(PREDICATES_STARTWITH);
      } else if (type == 1) {
        label += getLabel(PREDICATES_ENDWITH);
      } else if (type == 2) {
        label += getLabel(PREDICATES_CONTAINS);
      }
      return label;
    }

    @Override
    public ValueSearch askForValue() {
      type = 0;
      JPanel panel = new JPanel();
      JComboBox<String> combo = new JComboBox<>();
      combo.addItem(getLabel(PREDICATES_STARTWITH));
      combo.addItem(getLabel(PREDICATES_ENDWITH));
      combo.addItem(getLabel(PREDICATES_CONTAINS));
      combo.addItemListener((e) -> type = combo.getSelectedIndex());
      panel.add(combo);
      return new ValueSearch(JOptionPane.showInputDialog(panel));
    }
  };

  public static final IPredicate<IMyCellarObject> ARTIST = new IPredicate<>() {

    private int type = -1;

    @Override
    public boolean apply(IMyCellarObject myCellarObject, Object compare, int type) {
      Program.throwNotImplementedIfNotFor(myCellarObject, Music.class);
      Music music = (Music) myCellarObject;
      if (music.getArtist() == null) {
        return false;
      }
      if (type == 0) {
        if (compare instanceof String s) {
          return music.getArtist().startsWith(s);
        }
      } else if (type == 1) {
        if (compare instanceof String s) {
          return music.getArtist().endsWith(s);
        }
      } else if (type == 2) {
        if (compare instanceof String s) {
          return music.getArtist().contains(s);
        }
      }
      return false;
    }

    @Override
    public int getType() {
      return type;
    }

    @Override
    public String getName() {
      String label = getLabel(PREDICATES_ARTIST);
      if (type == 0) {
        label += getLabel(PREDICATES_STARTWITH);
      } else if (type == 1) {
        label += getLabel(PREDICATES_ENDWITH);
      } else if (type == 2) {
        label += getLabel(PREDICATES_CONTAINS);
      }
      return label;
    }

    @Override
    public ValueSearch askForValue() {
      type = 0;
      JPanel panel = new JPanel();
      JComboBox<String> combo = new JComboBox<>();
      combo.addItem(getLabel(PREDICATES_STARTWITH));
      combo.addItem(getLabel(PREDICATES_ENDWITH));
      combo.addItem(getLabel(PREDICATES_CONTAINS));
      combo.addItemListener((e) -> type = combo.getSelectedIndex());
      panel.add(combo);
      return new ValueSearch(JOptionPane.showInputDialog(panel));
    }
  };


  public static final IPredicate<IMyCellarObject> YEAR = new IPredicate<>() {

    @Override
    public boolean apply(IMyCellarObject myCellarObject, Object compare, int type) {
      return (compare instanceof String) && myCellarObject.getAnnee() != null && myCellarObject.getAnnee().equals(compare);
    }

    @Override
    public String getName() {
      return getLabel(PREDICATES_YEAR);
    }

    @Override
    public ValueSearch askForValue() {
      return new ValueSearch(JOptionPane.showInputDialog(getName()));
    }
  };

  public static final IPredicate<IMyCellarObject> RANGEMENT = new IPredicate<>() {


    @Override
    public boolean apply(IMyCellarObject myCellarObject, Object compare, int type) {
      return (compare instanceof String) && myCellarObject.getEmplacement() != null && myCellarObject.getEmplacement().equals(compare);
    }

    @Override
    public String getName() {
      return getLabel(PREDICATES_PLACE);
    }

    @Override
    public ValueSearch askForValue() {
      JPanel panel = new JPanel();
      panel.setLayout(new MigLayout("", "grow", "[]"));
      JComboBox<AbstractPlace> liste = new JComboBox<>();
      Program.getAbstractPlaces().forEach(liste::addItem);
      panel.add(new JLabel(getLabel(PREDICATES_SELECTPLACE)), "wrap");
      panel.add(liste);
      JOptionPane.showMessageDialog(MainFrame.getInstance(), panel,
          "",
          JOptionPane.PLAIN_MESSAGE);
      return new ValueSearch(((AbstractPlace) Objects.requireNonNull(liste.getSelectedItem())).getName());
    }
  };


  public static final IPredicate<Bouteille> CAPACITY = new IPredicate<>() {

    @Override
    public boolean apply(Bouteille bottle, Object compare, int type) {
      return bottle.getKind().equals(compare);
    }

    @Override
    public String getName() {
      return getLabel(MAIN_CAPACITYORSUPPORT);
    }

    @Override
    public ValueSearch askForValue() {
      JPanel panel = new JPanel();
      panel.setLayout(new MigLayout("", "grow", "[]"));
      JComboBox<String> liste = new JComboBox<>();
      MyCellarBottleContenance.getList().forEach(liste::addItem);
      panel.add(new JLabel(getLabel(PREDICATES_SELECTSIZE)), "wrap");
      panel.add(liste);
      JOptionPane.showMessageDialog(MainFrame.getInstance(), panel,
          "",
          JOptionPane.PLAIN_MESSAGE);
      return new ValueSearch(liste.getSelectedItem());
    }
  };

  public static final IPredicate<IMyCellarObject> PRICE = new IPredicate<>() {

    private int type = -1;

    @Override
    public boolean apply(IMyCellarObject myCellarObject, Object compare, int type) {
      if (type == 0) {
        if (compare instanceof String s) {
          return myCellarObject.getPrice().compareTo(BigDecimal.ZERO) != 0 && myCellarObject.getPrice().compareTo(new BigDecimal(s)) < 0;
        }
      } else if (type == 1) {
        if (compare instanceof String s) {
          return myCellarObject.getPrice().compareTo(BigDecimal.ZERO) != 0 && myCellarObject.getPrice().compareTo(new BigDecimal(s)) > 0;
        }
      }
      return false;
    }

    @Override
    public String getName() {
      String label = getLabel(PREDICATES_PRICE);
      if (type == 0) {
        label += getLabel(PREDICATES_SMALLER);
      } else if (type == 1) {
        label += getLabel(PREDICATES_GREATER);
      }
      return label;
    }

    @Override
    public int getType() {
      return type;
    }

    @Override
    public ValueSearch askForValue() {
      type = 0;
      JPanel panel = new JPanel();
      JComboBox<String> combo = new JComboBox<>();
      combo.addItem(getLabel(PREDICATES_SMALLER));
      combo.addItem(getLabel(PREDICATES_GREATER));
      combo.addItemListener((e) -> type = combo.getSelectedIndex());
      panel.add(combo);
      return new ValueSearch(JOptionPane.showInputDialog(panel));
    }
  };

  public static final IPredicate<Bouteille> COUNTRY = new IPredicate<>() {

    @Override
    public boolean apply(Bouteille bouteille, Object compare, int type) {
      if (!(compare instanceof VignobleJaxb compareVignobleJaxb) || bouteille.getVignoble() == null) {
        return false;
      }
      final VignobleJaxb vignobleJaxb = bouteille.getVignoble();
      return compareVignobleJaxb.getCountry().equals(vignobleJaxb.getCountry()) &&
          (compareVignobleJaxb.getName().isEmpty() || compareVignobleJaxb.getName().equals(vignobleJaxb.getName())) &&
          (compareVignobleJaxb.getAOC().isEmpty() || compareVignobleJaxb.getAOC().equals(vignobleJaxb.getAOC())) &&
          (compareVignobleJaxb.getIGP().isEmpty() || compareVignobleJaxb.getIGP().equals(vignobleJaxb.getIGP()));
    }

    @Override
    public String getName() {
      return getLabel(PREDICATES_VIGNOBLE);
    }

    @Override
    public ValueSearch askForValue() {
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
      return getLabel(PREDICATES_AND);
    }

    @Override
    public ValueSearch askForValue() {
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
      return getLabel(PREDICATES_OR);
    }

    @Override
    public ValueSearch askForValue() {
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
    public ValueSearch askForValue() {
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
    public ValueSearch askForValue() {
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
