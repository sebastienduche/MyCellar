package Cave.requester.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import Cave.requester.IPredicate;
import Cave.requester.ui.PanelDAndD;
import Cave.core.MyCellarLabel;

/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.1
 * @since 11/06/14
 */
class LabelSearch extends JPanel {

	private static final long serialVersionUID = 3361283505652395494L;
	private String label;
	private String value;
	private IPredicate<?> predicate;
	private boolean copy = false;
	private MyCellarLabel MyCellarLabel = new MyCellarLabel();
	private PanelCloseButton panelClose;
	private PanelDAndD source;

	public LabelSearch(IPredicate<?> predicate) {
		super();
		this.label = predicate.getName();
		this.predicate = predicate;
		MyCellarLabel.setText(label);
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.gray, Color.white));
		setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
		add(MyCellarLabel);
		add(panelClose = new PanelCloseButton(){
			private static final long serialVersionUID = 3495975676025406824L;

			protected void actionPerformed() {
				Component parent = LabelSearch.this.getParent();
				if(parent instanceof JPanel) {
					((JPanel)parent).remove(LabelSearch.this);
					source.add(LabelSearch.this);
					((JPanel)parent).updateUI();
				}
			}
		});
		panelClose.setVisible(false);
	}
	
	public LabelSearch(IPredicate<?> predicate, PanelDAndD source) {
		this(predicate);
		this.source = source;
	}


	public String getLabel() {
		return label;
	}
	
	public String getText() {
		return MyCellarLabel.getText();
	}

	public void setLabel(String label) {
		this.label = label;
		setLabel();
	}

	private void setLabel() {
		String s = "";
		if(label != null && !label.isEmpty())
			s += label;
		if(label != null && !label.isEmpty() && value != null && !value.isEmpty())
			s += ": ";
		if(value != null)
			s += value;
		MyCellarLabel.setText(s);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
		setLabel();
	}

	public void askForValue() {
		if(!predicate.isValueRequired() || (value != null && !value.isEmpty()))
			return;

		value = (String) predicate.askforValue();
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


	public void setAsKeyword(boolean b) {
		if(b){
			setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.gray, Color.white));
			setBackground(null);
			MyCellarLabel.setForeground(null);
			setValue("");
		}
		else {
			setBorder(BorderFactory.createEtchedBorder());
			setBackground(new Color(102,102,255));
			MyCellarLabel.setForeground(new Color(255,255,255));
		}
		panelClose.setVisible(!b);
	}

	public IPredicate<?> getPredicate() {
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LabelSearch other = (LabelSearch) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if(!other.copy)
			return false;
		return true;
	}
}
