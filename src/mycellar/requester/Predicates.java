package mycellar.requester;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import mycellar.BottleColor;
import mycellar.Bouteille;
import mycellar.Program;
import mycellar.Rangement;
import mycellar.vignobles.Appelation;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.4
 * @since 03/08/17
 */

public class Predicates {

	public static IPredicate<Bouteille> color = new IPredicate<Bouteille>() {

		@Override
		public boolean apply(Bouteille bottle) {
			return apply(bottle, "");
		}

		@Override
		public boolean apply(Bouteille bottle, Object compare) {
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
		public Object askforValue() {
			JPanel panel = new JPanel();
			panel.setLayout(new MigLayout("", "grow", "[]"));
			JComboBox<BottleColor> liste = new JComboBox<BottleColor>();
			liste.addItem(BottleColor.NONE);
			liste.addItem(BottleColor.RED);
			liste.addItem(BottleColor.PINK);
			liste.addItem(BottleColor.WHITE);
			panel.add(new JLabel(Program.getLabel("Predicates.SelectColor")), "wrap");
			panel.add(liste);
			JOptionPane.showMessageDialog(null, panel,
			        "",
			        JOptionPane.PLAIN_MESSAGE);
			return liste.getSelectedItem();
		}
	};

	public static IPredicate<Bouteille> name = new IPredicate<Bouteille>() {
		
		int type = -1;

		@Override
		public boolean apply(Bouteille type) {
			return apply(type, "");
		}

		@Override
		public boolean apply(Bouteille bouteille, Object compare) {
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
					return bouteille.getNom() != null && ( bouteille.getNom().indexOf((String)compare) != -1);
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
			String label = Program.getLabel("Predicates.Name");
			if(type == 0)
				label += Program.getLabel("Predicates.StartWith");
			else if(type == 1)
				label += Program.getLabel("Predicates.EndWith");
			else if(type == 2)
				label += Program.getLabel("Predicates.Contains");
			return label;
		}
		
		@Override
		public Object askforValue() {
			type = 0;
			JPanel panel = new JPanel();
			JComboBox<String> combo = new JComboBox<String>();
			combo.addItem(Program.getLabel("Predicates.StartWith"));
			combo.addItem(Program.getLabel("Predicates.EndWith"));
			combo.addItem(Program.getLabel("Predicates.Contains"));
			combo.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					type = combo.getSelectedIndex();
				}
			});
			panel.add(combo);
			return JOptionPane.showInputDialog(panel);
		}
	};
	
	public static IPredicate<Bouteille> year = new IPredicate<Bouteille>() {

		@Override
		public boolean apply(Bouteille type) {
			return apply(type, "");
		}

		@Override
		public boolean apply(Bouteille bouteille, Object compare) {
			if(compare instanceof String) {
				return bouteille.getAnnee() != null && bouteille.getAnnee().equals((String)compare);
			}
			return false;
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
		public Object askforValue() {
			return JOptionPane.showInputDialog(getName());
		}
	};
	
	public static IPredicate<Bouteille> rangement = new IPredicate<Bouteille>() {

		@Override
		public boolean apply(Bouteille type) {
			return apply(type, "");
		}

		@Override
		public boolean apply(Bouteille bouteille, Object compare) {
			if(compare instanceof String) {
				return bouteille.getEmplacement() != null && bouteille.getEmplacement().equals((String)compare);
			}
			return false;
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
		public Object askforValue() {
			JPanel panel = new JPanel();
			panel.setLayout(new MigLayout("", "grow", "[]"));
			JComboBox<Rangement> liste = new JComboBox<Rangement>();
			for(Rangement r : Program.getCave()) {
				liste.addItem(r);
			}
			panel.add(new JLabel(Program.getLabel("Predicates.SelectPlace")), "wrap");
			panel.add(liste);
			JOptionPane.showMessageDialog(null, panel,
			        "",
			        JOptionPane.PLAIN_MESSAGE);
			return ((Rangement)liste.getSelectedItem()).getNom();
		}
	};
	
	
	public static IPredicate<Bouteille> capacity = new IPredicate<Bouteille>() {

		@Override
		public boolean apply(Bouteille bottle) {
			return apply(bottle, "");
		}

		@Override
		public boolean apply(Bouteille bottle, Object compare) {
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
		public Object askforValue() {
			JPanel panel = new JPanel();
			panel.setLayout(new MigLayout("", "grow", "[]"));
			JComboBox<String> liste = new JComboBox<String>();
			for(String val : Program.half)
				liste.addItem(val);
			panel.add(new JLabel(Program.getLabel("Predicates.SelectSize")), "wrap");
			panel.add(liste);
			JOptionPane.showMessageDialog(null, panel,
			        "",
			        JOptionPane.PLAIN_MESSAGE);
			return liste.getSelectedItem();
		}
	};
	
	public static IPredicate<Appelation> AND = new IPredicate<Appelation>() {

		@Override
		public boolean apply(Appelation type) {
			return true;
		}

		@Override
		public boolean apply(Appelation type, Object compare) {
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
		public Object askforValue() {
			return null;
		}
	};
	
	public static IPredicate<Appelation> OR = new IPredicate<Appelation>() {

		@Override
		public boolean apply(Appelation type) {
			return true;
		}

		@Override
		public boolean apply(Appelation type, Object compare) {
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
		public Object askforValue() {
			return null;
		}
	};
	
	public static IPredicate<Appelation> openParenthesis = new IPredicate<Appelation>() {

		@Override
		public boolean apply(Appelation type) {
			return true;
		}

		@Override
		public boolean apply(Appelation type, Object compare) {
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
		public Object askforValue() {
			return null;
		}
	};
	
	public static IPredicate<Appelation> closeParenthesis = new IPredicate<Appelation>() {

		@Override
		public boolean apply(Appelation type) {
			return true;
		}

		@Override
		public boolean apply(Appelation type, Object compare) {
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
		public Object askforValue() {
			return null;
		}
	};
	
	public static boolean isFieldPredicate(IPredicate<?> predicate) {
		if(predicate == null)
			return false;
		return !isKeywordPredicate(predicate) && !isParenthesisPredicate(predicate);
	}
	
	public static boolean isKeywordPredicate(IPredicate<?> predicate) {
		if(predicate == null)
			return false;
		return (predicate.equals(AND) || predicate.equals(OR));
	}
	
	public static boolean isParenthesisPredicate(IPredicate<?> predicate) {
		if(predicate == null)
			return false;
		return (predicate.equals(openParenthesis) || predicate.equals(closeParenthesis));
	}

	@SuppressWarnings("rawtypes")
	private IPredicate predicate;
	private Object value;

	public <T> Predicates(IPredicate<T> predicate, Object value) {
		this.predicate = predicate;
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	public <T> IPredicate<T> getPredicate() {
		return predicate;
	}

	public Object getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Predicate: ");
		if(predicate != null) {
			sb.append(predicate.getName());
			sb.append(" ");
		}
		if(value != null)
			sb.append(value);
		sb.append("]");
		return sb.toString();
	}
}
