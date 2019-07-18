package mycellar.requester;

import mycellar.BottleColor;
import mycellar.BottlesStatus;
import mycellar.Bouteille;
import mycellar.Program;
import mycellar.Rangement;
import mycellar.Vignoble;
import mycellar.core.PanelVignobles;
import mycellar.core.datas.MyCellarBottleContenance;
import mycellar.requester.ui.ValueSearch;
import mycellar.vignobles.Appelation;
import net.miginfocom.swing.MigLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.math.BigDecimal;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.9
 * @since 15/07/19
 */

public class Predicates {

	public static final IPredicate<Bouteille> color = new IPredicate<>() {

		@Override
		public boolean apply(Bouteille bottle) {
			return apply(bottle, "", -1);
		}

		@Override
		public boolean apply(Bouteille bottle, Object compare, int type) {
			return bottle.getColor().equals(((BottleColor)compare).name());
		}

		@Override
		public boolean isValueRequired() {
			return true;
		}

		@Override
		public String getName() {
			return Program.getLabel("AddVin.Color");
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
			panel.add(new JLabel(Program.getLabel("Predicates.SelectColor")), "wrap");
			panel.add(liste);
			JOptionPane.showMessageDialog(null, panel,
			        "",
			        JOptionPane.PLAIN_MESSAGE);
			return new ValueSearch(liste.getSelectedItem());
		}
	};

	public static final IPredicate<Bouteille> status = new IPredicate<>() {

		@Override
		public boolean apply(Bouteille bottle) {
			return apply(bottle, "", -1);
		}

		@Override
		public boolean apply(Bouteille bottle, Object compare, int type) {
			final String status = bottle.getStatus();
			final BottlesStatus bottlesStatus = (BottlesStatus) compare;
			if (status.isEmpty() && BottlesStatus.NONE.equals(bottlesStatus)) {
				return true;
			}
			return status.equals(bottlesStatus.name());
		}

		@Override
		public boolean isValueRequired() {
			return true;
		}

		@Override
		public String getName() {
			return Program.getLabel("MyCellarManageBottles.status");
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
			panel.add(new JLabel(Program.getLabel("Predicates.SelectStatus")), "wrap");
			panel.add(liste);
			JOptionPane.showMessageDialog(null, panel,
					"",
					JOptionPane.PLAIN_MESSAGE);
			return new ValueSearch(liste.getSelectedItem());
		}
	};

	public static final IPredicate<Bouteille> name = new IPredicate<>() {
		
		private int type = -1;

		@Override
		public boolean apply(Bouteille bouteille) {
			return apply(bouteille, "", -1);
		}

		@Override
		public boolean apply(Bouteille bouteille, Object compare, int type) {
			if(type == 0) {
    			if(compare instanceof String) {
    				return bouteille.getNom() != null && bouteille.getNom().startsWith((String)compare);
    			}
			}
			else if(type == 1) {
    			if(compare instanceof String) {
    				return bouteille.getNom() != null && bouteille.getNom().endsWith((String)compare);
    			}
			}
			else if(type == 2) {
				if(compare instanceof String) {
					return bouteille.getNom() != null && ( bouteille.getNom().contains((String)compare));
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
			String label = Program.getLabel("Predicates.Name");
			if(type == 0) {
				label += Program.getLabel("Predicates.StartWith");
			} else if(type == 1) {
				label += Program.getLabel("Predicates.EndWith");
			} else if(type == 2) {
				label += Program.getLabel("Predicates.Contains");
			}
			return label;
		}
		
		@Override
		public ValueSearch askforValue() {
			type = 0;
			JPanel panel = new JPanel();
			JComboBox<String> combo = new JComboBox<>();
			combo.addItem(Program.getLabel("Predicates.StartWith"));
			combo.addItem(Program.getLabel("Predicates.EndWith"));
			combo.addItem(Program.getLabel("Predicates.Contains"));
			combo.addItemListener((e) -> {
					type = combo.getSelectedIndex();
				});
			panel.add(combo);
			return new ValueSearch(JOptionPane.showInputDialog(panel));
		}
	};
	
	public static IPredicate<Bouteille> year = new IPredicate<>() {

		@Override
		public boolean apply(Bouteille bouteille) {
			return apply(bouteille, "", -1);
		}

		@Override
		public boolean apply(Bouteille bouteille, Object compare, int type) {
			return (compare instanceof String) && bouteille.getAnnee() != null && bouteille.getAnnee().equals(compare);
		}
		
		@Override
		public boolean isValueRequired() {
			return true;
		}
		
		@Override
		public String getName() {
			return Program.getLabel("Predicates.Year");
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
	
	public static IPredicate<Bouteille> rangement = new IPredicate<>() {

		@Override
		public boolean apply(Bouteille bouteille) {
			return apply(bouteille, "", -1);
		}

		@Override
		public boolean apply(Bouteille bouteille, Object compare, int type) {
			return (compare instanceof String) && bouteille.getEmplacement() != null && bouteille.getEmplacement().equals(compare);
		}
		
		@Override
		public boolean isValueRequired() {
			return true;
		}
		
		@Override
		public String getName() {
			return Program.getLabel("Predicates.Place");
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
			JComboBox<Rangement> liste = new JComboBox<>();
			for(Rangement r : Program.getCave()) {
				liste.addItem(r);
			}
			panel.add(new JLabel(Program.getLabel("Predicates.SelectPlace")), "wrap");
			panel.add(liste);
			JOptionPane.showMessageDialog(null, panel,
			        "",
			        JOptionPane.PLAIN_MESSAGE);
			return new ValueSearch(((Rangement)liste.getSelectedItem()).getNom());
		}
	};
	
	
	public static IPredicate<Bouteille> capacity = new IPredicate<>() {

		@Override
		public boolean apply(Bouteille bottle) {
			return apply(bottle, "", -1);
		}

		@Override
		public boolean apply(Bouteille bottle, Object compare, int type) {
			return bottle.getType().equals(compare);
		}

		@Override
		public boolean isValueRequired() {
			return true;
		}

		@Override
		public String getName() {
			return Program.getLabel("Infos134");
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
			for(String val : MyCellarBottleContenance.getList()) {
				liste.addItem(val);
			}
			panel.add(new JLabel(Program.getLabel("Predicates.SelectSize")), "wrap");
			panel.add(liste);
			JOptionPane.showMessageDialog(null, panel,
			        "",
			        JOptionPane.PLAIN_MESSAGE);
			return new ValueSearch(liste.getSelectedItem());
		}
	};

	public static IPredicate<Bouteille> price = new IPredicate<>() {

		private int type = -1;

		@Override
		public boolean apply(Bouteille bouteille) {
			return apply(bouteille, "", -1);
		}

		@Override
		public boolean apply(Bouteille bouteille, Object compare, int type) {
			if(type == 0) {
				if(compare instanceof String) {
					return bouteille.getPrice().compareTo(BigDecimal.ZERO) != 0 && bouteille.getPrice().compareTo(new BigDecimal((String)compare)) < 0;
				}
			}
			else if(type == 1) {
				if(compare instanceof String) {
					return bouteille.getPrice().compareTo(BigDecimal.ZERO) != 0 && bouteille.getPrice().compareTo(new BigDecimal((String)compare)) > 0;
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
			String label = Program.getLabel("Predicates.Price");
			if(type == 0) {
				label += Program.getLabel("Predicates.Smaller");
			} else if(type == 1) {
				label += Program.getLabel("Predicates.Greater");
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
			combo.addItem(Program.getLabel("Predicates.Smaller"));
			combo.addItem(Program.getLabel("Predicates.Greater"));
			combo.addItemListener((e) -> type = combo.getSelectedIndex());
			panel.add(combo);
			return new ValueSearch(JOptionPane.showInputDialog(panel));
		}
	};

	public static IPredicate<Bouteille> country = new IPredicate<>() {

		@Override
		public boolean apply(Bouteille bouteille) {
			return apply(bouteille, "", -1);
		}

		@Override
		public boolean apply(Bouteille bouteille, Object compare, int type) {
			if (!(compare instanceof Vignoble) || bouteille.getVignoble() == null) {
				return false;
			}
			Vignoble compareVignoble = (Vignoble) compare;
			final Vignoble vignoble = bouteille.getVignoble();
			if (!compareVignoble.getCountry().equals(vignoble.getCountry())) {
				return false;
			}
			if (!compareVignoble.getName().isEmpty() && !compareVignoble.getName().equals(vignoble.getName())) {
				return false;
			}
			if (!compareVignoble.getAOC().isEmpty() && !compareVignoble.getAOC().equals(vignoble.getAOC())) {
				return false;
			}
			if (!compareVignoble.getIGP().isEmpty() && !compareVignoble.getIGP().equals(vignoble.getIGP())) {
				return false;
			}
			return true;
		}

		@Override
		public boolean isValueRequired() {
			return true;
		}

		@Override
		public String getName() {
			return Program.getLabel("Predicates.Vignoble");
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
			PanelVignobles panelVignobles = new PanelVignobles(true, false);
			JOptionPane.showMessageDialog(null, panelVignobles,
					"",
					JOptionPane.PLAIN_MESSAGE);
			final Vignoble selectedVignoble = panelVignobles.getSelectedVignoble();
			return new ValueSearch(selectedVignoble, selectedVignoble.getSearchLabel());
		}
	};

	public static final IPredicate<Appelation> AND = new IPredicate<>() {

		@Override
		public boolean apply(Appelation appelation) {
			return true;
		}

		@Override
		public boolean apply(Appelation appelation, Object compare, int type) {
			return true;
		}

		@Override
		public boolean isValueRequired() {
			return false;
		}

		@Override
		public String getName() {
			return Program.getLabel("Predicates.And");
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

	public static final IPredicate<Appelation> OR = new IPredicate<>() {

		@Override
		public boolean apply(Appelation appelation) {
			return true;
		}

		@Override
		public boolean apply(Appelation appelation, Object compare, int type) {
			return true;
		}
		
		@Override
		public boolean isValueRequired() {
			return false;
		}
		
		@Override
		public String getName() {
			return Program.getLabel("Predicates.Or");
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
	
	public static IPredicate<Appelation> openParenthesis = new IPredicate<>() {

		@Override
		public boolean apply(Appelation appelation) {
			return true;
		}

		@Override
		public boolean apply(Appelation appelation, Object compare, int type) {
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
	
	public static IPredicate<Appelation> closeParenthesis = new IPredicate<>() {

		@Override
		public boolean apply(Appelation appelation) {
			return true;
		}

		@Override
		public boolean apply(Appelation appelation, Object compare, int type) {
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
	
	static boolean isFieldPredicate(IPredicate<?> predicate) {
		return (predicate != null) && !isKeywordPredicate(predicate) && !isParenthesisPredicate(predicate);
	}
	
	static boolean isKeywordPredicate(IPredicate<?> predicate) {
		return (predicate != null) && (predicate.equals(AND) || predicate.equals(OR));
	}
	
	static boolean isParenthesisPredicate(IPredicate<?> predicate) {
		return (predicate != null) && (predicate.equals(openParenthesis) || predicate.equals(closeParenthesis));
	}

	@SuppressWarnings("rawtypes")
	private final IPredicate predicate;
	private final Object value;
	private final int type;

	public <T> Predicates(IPredicate<T> predicate, Object value, int type) {
		this.predicate = predicate;
		this.value = value;
		this.type = type;
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
		if(predicate != null) {
			sb.append(predicate.getName());
			sb.append(" ");
		}
		if(value != null) {
			sb.append(value).append(" ");
		}
		sb.append(type);
		sb.append("]");
		return sb.toString();
	}
}
