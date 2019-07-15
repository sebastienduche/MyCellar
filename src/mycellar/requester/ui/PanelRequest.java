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
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.7
 * @since 15/07/19
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
		panelOperator.add(new LabelSearch(Predicates.AND, panelOperator, true));
		panelOperator.add(new LabelSearch(Predicates.OR, panelOperator, true));
		panelOperator.add(new LabelSearch(Predicates.openParenthesis, panelOperator, true));
		panelOperator.add(new LabelSearch(Predicates.closeParenthesis, panelOperator, true));
		panelKeyword.add(new LabelSearch(Predicates.name, panelKeyword, true));
		panelKeyword.add(new LabelSearch(Predicates.year, panelKeyword, true));
		panelKeyword.add(new LabelSearch(Predicates.rangement, panelKeyword, true));
		panelKeyword.add(new LabelSearch(Predicates.color, panelKeyword, true));
		panelKeyword.add(new LabelSearch(Predicates.capacity, panelKeyword, true));
		panelKeyword.add(new LabelSearch(Predicates.price, panelKeyword, true));
		panelKeyword.add(new LabelSearch(Predicates.country, panelKeyword, true));
		panelKeyword.add(new LabelSearch(Predicates.status, panelKeyword, true));
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
		if(panelRequest == null) {
			return null;
		}
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
