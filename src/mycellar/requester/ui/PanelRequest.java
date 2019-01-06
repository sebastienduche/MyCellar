package mycellar.requester.ui;

import mycellar.Program;
import mycellar.core.MyCellarLabel;
import mycellar.requester.CollectionFilter;
import mycellar.requester.Predicates;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.5
 * @since 06/01/19
 */
public class PanelRequest extends JPanel {

	private static final long serialVersionUID = -1239228393406479587L;

	private final PanelDAndD panelRequest;
	private final MyCellarLabel labelError = new MyCellarLabel();

	public PanelRequest() {

		labelError.setFont(Program.FONT_LABEL_BOLD);
		labelError.setForeground(Color.red);

		MainChangeListener.setChangeListener((e) -> {
			if(!CollectionFilter.validatePredicates(getPredicates())) {
				labelError.setText(CollectionFilter.getError());
				labelError.setVisible(true);
			} else {
				labelError.setText("");
				labelError.setVisible(false);
			}
		});

		setLayout(new MigLayout("","[grow][grow]","[][]"));
		PanelDAndD panelKeyword = new PanelDAndD();
		panelKeyword.setLayout(new MigLayout());
		PanelDAndD panelOperator = new PanelDAndD();
		panelOperator.setLayout(new MigLayout());
		LabelSearch label = new LabelSearch(Predicates.name, panelKeyword);
		label.setCopy(true);
		panelKeyword.add(label);
		label = new LabelSearch(Predicates.AND, panelOperator);
		label.setCopy(true);
		panelOperator.add(label);
		label = new LabelSearch(Predicates.OR, panelOperator);
		label.setCopy(true);
		panelOperator.add(label);
		label = new LabelSearch(Predicates.openParenthesis, panelOperator);
		label.setCopy(true);
		panelOperator.add(label);
		label = new LabelSearch(Predicates.closeParenthesis, panelOperator);
		label.setCopy(true);
		panelOperator.add(label);
		label = new LabelSearch(Predicates.year, panelKeyword);
		label.setCopy(true);
		panelKeyword.add(label);
		label = new LabelSearch(Predicates.rangement, panelKeyword);
		label.setCopy(true);
		panelKeyword.add(label);
		label = new LabelSearch(Predicates.color, panelKeyword);
		label.setCopy(true);
		panelKeyword.add(label);
		label = new LabelSearch(Predicates.capacity, panelKeyword);
		label.setCopy(true);
		panelKeyword.add(label);
		label = new LabelSearch(Predicates.price, panelKeyword);
		label.setCopy(true);
		panelKeyword.add(label);
		panelKeyword.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Program.getLabel("PanelRequest.Parameters")));
		panelOperator.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Program.getLabel("PanelRequest.Operators")));
		add(panelKeyword, "grow");
		add(panelOperator, "grow, wrap");
		panelRequest = new PanelDAndD(true);
		panelRequest.setLayout(new MigLayout("","",""));
		JScrollPane scroll = new JScrollPane(panelRequest);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		panelRequest.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Program.getLabel("PanelRequest.Request")));
		add(scroll, "h 75::, grow, span 2");
		add(labelError, "newline, span 2, center, hidemode 3");
	}

	public Collection<Predicates> getPredicates() {
		if(panelRequest == null)
			return null;
		Collection<Predicates> predicates = new ArrayList<>();
		for(int i=0; i<panelRequest.getComponentCount(); i++) {
			Object obj = panelRequest.getComponent(i);
			if(obj instanceof LabelSearch) {
				LabelSearch label = (LabelSearch)obj;
				predicates.add(new Predicates(label.getPredicate(), label.getValue(), label.getType()));
			}
		}
		return predicates;
	}
}
