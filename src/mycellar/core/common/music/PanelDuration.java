package mycellar.core.common.music;

import mycellar.core.text.LabelType;
import mycellar.core.uicomponents.MyCellarLabel;
import mycellar.core.uicomponents.MyCellarSpinner;
import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;
import java.time.LocalDateTime;

/**
 * <p>Titre : Cave &agrave; vin</p>
 * <p>Description : Votre description</p>
 * <p>Copyright : Copyright (c) 2021</p>
 * <p>Soci&eacute;t&eacute; : Seb Informatique</p>
 *
 * @author S&eacute;bastien Duch&eacute;
 * @version 0.1
 * @since 15/05/21
 */
public final class PanelDuration extends JPanel {

  private static final long serialVersionUID = 2873901826552684927L;
  private final MyCellarSpinner hour = new MyCellarSpinner(0, 23);
  private final MyCellarSpinner minute = new MyCellarSpinner(0, 59);
  private final MyCellarSpinner second = new MyCellarSpinner(0, 59);

  public PanelDuration(LocalDateTime time) {
    setLayout(new MigLayout("", "[][][]", ""));
    add(new MyCellarLabel(LabelType.INFO_OTHER, "PanelDuration.hour"));
    add(new MyCellarLabel(LabelType.INFO_OTHER, "PanelDuration.minute"));
    add(new MyCellarLabel(LabelType.INFO_OTHER, "PanelDuration.second"), "wrap");
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
