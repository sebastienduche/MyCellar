package mycellar.core.common.music;

import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarSpinner;
import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import java.time.LocalDateTime;

/**
 * Titre : Cave &agrave; vin
 * Description : Votre description
 * Copyright : Copyright (c) 2021
 * Soci&eacute;t&eacute; : Seb Informatique
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.2
 * @since 24/05/21
 */
public final class PanelDuration extends JPanel {

  private static final long serialVersionUID = 2873901826552684927L;
  private final MyCellarSpinner hour = new MyCellarSpinner(0, 23);
  private final MyCellarSpinner minute = new MyCellarSpinner(0, 59);
  private final MyCellarSpinner second = new MyCellarSpinner(0, 59);

  public PanelDuration(LocalDateTime time) {
    setLayout(new MigLayout("", "[][][]", ""));
    add(new MyCellarLabel("PanelDuration.hour"));
    add(new MyCellarLabel("PanelDuration.minute"));
    add(new MyCellarLabel("PanelDuration.second"), "wrap");
    add(hour);
    add(minute);
    add(second);
    hour.setValue(time.getHour());
    minute.setValue(time.getMinute());
    second.setValue(time.getSecond());
  }

  public LocalDateTime getTime() {
    return LocalDateTime.of(2000, 1, 1, hour.getIntValue(), minute.getIntValue(), second.getIntValue());
  }
}
