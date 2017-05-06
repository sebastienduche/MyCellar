package mycellar.requester.ui;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mycellar.Program;
import mycellar.core.MyCellarLabel;
import mycellar.requester.CollectionFilter;
import mycellar.requester.Predicates;
import net.miginfocom.swing.MigLayout;


/**
 * <p>Titre : Cave à vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2014</p>
 * <p>Société : Seb Informatique</p>
 * @author Sébastien Duché
 * @version 0.2
 * @since 03/09/14
 */
public class PanelRequest extends JPanel {

	private static final long serialVersionUID = -1239228393406479587L;

	private PanelDAndD panelRequest;
	private MyCellarLabel labelError = new MyCellarLabel();

	public PanelRequest() {

		MainChangeListener.setChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(!CollectionFilter.validatePredicates(getPredicates())) {
					labelError.setText(CollectionFilter.getError());
					labelError.setVisible(true);
				} else {
					labelError.setText("");
					labelError.setVisible(false);
				}
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
		panelKeyword.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Program.getLabel("PanelRequest.Parameters")));
		panelOperator.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Program.getLabel("PanelRequest.Operators")));
		add(panelKeyword, "grow");
		add(panelOperator, "grow, wrap");
		panelRequest = new PanelDAndD(true);
		panelRequest.setLayout(new MigLayout("","",""));
		JScrollPane scroll = new JScrollPane(panelRequest);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		panelRequest.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Program.getLabel("PanelRequest.Request")));
		add(scroll, "h 75::, grow, span 2");
		add(labelError, "newline, span 2, center, hidemode 3");
	}

	public Collection<Predicates> getPredicates() {
		if(panelRequest == null)
			return null;
		Collection<Predicates> predicates = new ArrayList<Predicates>();
		for(int i=0; i<panelRequest.getComponentCount(); i++) {
			Object obj = panelRequest.getComponent(i);
			if(obj instanceof LabelSearch) {
				LabelSearch label = (LabelSearch)obj;
				predicates.add(new Predicates(label.getPredicate(), label.getValue()));
			}
		}
		return predicates;
	}
}