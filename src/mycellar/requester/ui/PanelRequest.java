package mycellar.requester.ui;

import mycellar.Program;
import mycellar.core.uicomponents.MyCellarSimpleLabel;
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

import static mycellar.ProgramConstants.FONT_LABEL_BOLD;
import static mycellar.core.text.MyCellarLabelManagement.getLabel;
import static mycellar.general.ResourceKey.PANELREQUEST_OPERATORS;
import static mycellar.general.ResourceKey.PANELREQUEST_PARAMETERS;
import static mycellar.general.ResourceKey.PANELREQUEST_REQUEST;


/**
 * <p>Titre : Cave &agrave; vin
 * <p>Description : Votre description
 * <p>Copyright : Copyright (c) 2014
 * <p>Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 1.5
 * @since 14/03/25
 */
public final class PanelRequest extends JPanel {

  private final PanelDAndD panelRequest;
  private final MyCellarSimpleLabel labelError = new MyCellarSimpleLabel();

  public PanelRequest() {
    labelError.setFont(FONT_LABEL_BOLD);
    labelError.setForeground(Color.red);

    MainChangeListener.setChangeListener((e) -> {
      if (!CollectionFilter.validatePredicates(getPredicates())) {
        labelError.setText(CollectionFilter.getError());
        labelError.setVisible(true);
      } else {
        labelError.setText("");
        labelError.setVisible(false);
      }
    });

    setLayout(new MigLayout("", "[grow][grow]", "[][]"));
    PanelDAndD panelKeyword = new PanelDAndD();
    panelKeyword.setLayout(new MigLayout());
    PanelDAndD panelOperator = new PanelDAndD();
    panelOperator.setLayout(new MigLayout());
    panelOperator.add(new LabelSearch(Predicates.AND, panelOperator, true));
    panelOperator.add(new LabelSearch(Predicates.OR, panelOperator, true));
    panelOperator.add(new LabelSearch(Predicates.OPEN_PARENTHESIS, panelOperator, true));
    panelOperator.add(new LabelSearch(Predicates.CLOSE_PARENTHESIS, panelOperator, true));
    panelKeyword.add(new LabelSearch(Predicates.NAME, panelKeyword, true));
    panelKeyword.add(new LabelSearch(Predicates.YEAR, panelKeyword, true));
    panelKeyword.add(new LabelSearch(Predicates.RANGEMENT, panelKeyword, true));
    if (Program.isWineType()) {
      panelKeyword.add(new LabelSearch(Predicates.COLOR, panelKeyword, true));
      panelKeyword.add(new LabelSearch(Predicates.CAPACITY, panelKeyword, true));
      panelKeyword.add(new LabelSearch(Predicates.COUNTRY, panelKeyword, true));
    } else if (Program.isMusicType()) {
      panelKeyword.add(new LabelSearch(Predicates.ARTIST, panelKeyword, true));
    }
    panelKeyword.add(new LabelSearch(Predicates.PRICE, panelKeyword, true));
    panelKeyword.add(new LabelSearch(Predicates.STATUS, panelKeyword, true));
    panelKeyword.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), getLabel(PANELREQUEST_PARAMETERS)));
    panelOperator.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), getLabel(PANELREQUEST_OPERATORS)));
    add(panelKeyword, "grow");
    add(panelOperator, "grow, wrap");
    panelRequest = new PanelDAndD(true);
    panelRequest.setLayout(new MigLayout("", "", ""));
    JScrollPane scroll = new JScrollPane(panelRequest);
    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    scroll.setBorder(BorderFactory.createEmptyBorder());
    panelRequest.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), getLabel(PANELREQUEST_REQUEST)));
    add(scroll, "h 75::, grow, span 2");
    add(labelError, "newline, span 2, center, hidemode 3");
  }

  public Collection<Predicates> getPredicates() {
    Collection<Predicates> predicates = new ArrayList<>();
    if (panelRequest != null) {
      for (int i = 0; i < panelRequest.getComponentCount(); i++) {
        Object obj = panelRequest.getComponent(i);
        if (obj instanceof LabelSearch label) {
          predicates.add(new Predicates(label.getPredicate(), label.getValue(), label.getType()));
        }
      }
    }
    return predicates;
  }
}
